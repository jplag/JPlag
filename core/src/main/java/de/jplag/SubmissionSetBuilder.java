package de.jplag;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jplag.exceptions.BasecodeException;
import de.jplag.exceptions.ExitException;
import de.jplag.exceptions.RootDirectoryException;
import de.jplag.exceptions.SubmissionException;
import de.jplag.logging.ProgressBar;
import de.jplag.logging.ProgressBarLogger;
import de.jplag.logging.ProgressBarType;
import de.jplag.options.JPlagOptions;

/**
 * This class is responsible for the creation of a {@link SubmissionSet}. It processes multiple root directories of
 * submission, verifies the validity of submission, and processes the necessary source code files.
 * @author Timur Saglam
 */
public class SubmissionSetBuilder {

    private static final Logger logger = LoggerFactory.getLogger(SubmissionSetBuilder.class);

    private final JPlagOptions options;

    /**
     * Creates a builder for submission sets.
     * @param language is the language of the submissions.
     * @param options are the configured options.
     * @deprecated in favor of {@link #SubmissionSetBuilder(JPlagOptions)}.
     */
    @Deprecated(since = "4.3.0")
    public SubmissionSetBuilder(Language language, JPlagOptions options) {
        this(options.withLanguageOption(language));
    }

    /**
     * Creates a builder for submission sets.
     * @param options are the configured options.
     */
    public SubmissionSetBuilder(JPlagOptions options) {
        this.options = options;
    }

    /**
     * Builds a submission set for all submissions of a specific directory.
     * @return the newly built submission set.
     * @throws ExitException if the directory cannot be read.
     */
    public SubmissionSet buildSubmissionSet() throws ExitException {
        Set<File> submissionDirectories = verifyRootDirectories(options.submissionDirectories(), true);
        Set<File> oldSubmissionDirectories = verifyRootDirectories(options.oldSubmissionDirectories(), false);
        checkForNonOverlappingRootDirectories(submissionDirectories, oldSubmissionDirectories);

        // For backward compatibility, don't prefix submission names with their root directory
        // if there is only one root directory.
        int numberOfRootDirectories = submissionDirectories.size() + oldSubmissionDirectories.size();
        boolean multipleRoots = numberOfRootDirectories > 1;

        List<SubmissionFileData> submissionFiles = new ArrayList<>();
        for (File submissionDirectory : submissionDirectories) {
            submissionFiles.addAll(listSubmissionFiles(submissionDirectory, true));
        }
        for (File submissionDirectory : oldSubmissionDirectories) {
            submissionFiles.addAll(listSubmissionFiles(submissionDirectory, false));
        }

        ProgressBar progressBar = ProgressBarLogger.createProgressBar(ProgressBarType.LOADING, submissionFiles.size());
        Map<File, Submission> foundSubmissions = new HashMap<>();
        for (SubmissionFileData submissionFile : submissionFiles) {
            processSubmissionFile(submissionFile, multipleRoots, foundSubmissions);
            progressBar.step();
        }
        progressBar.dispose();

        Optional<Submission> baseCodeSubmission = loadBaseCode();
        baseCodeSubmission.ifPresent(baseSubmission -> foundSubmissions.remove(baseSubmission.getRoot()));

        // Merge everything in a submission set.
        List<Submission> submissions = new ArrayList<>(foundSubmissions.values());

        // Some languages expect a certain order, which is ensured here:
        if (options.language().expectsSubmissionOrder()) {
            List<File> rootFiles = foundSubmissions.values().stream().map(Submission::getRoot).toList();
            rootFiles = options.language().customizeSubmissionOrder(rootFiles);
            submissions = new ArrayList<>(rootFiles.stream().map(foundSubmissions::get).toList());
        }
        return new SubmissionSet(submissions, baseCodeSubmission.orElse(null), options);
    }

    /**
     * Verify that the given root directories exist and have no duplicate entries.
     */
    private Set<File> verifyRootDirectories(Set<File> rootDirectoryNames, boolean areNewDirectories) throws ExitException {
        if (areNewDirectories && rootDirectoryNames.isEmpty()) {
            throw new RootDirectoryException("No root directories specified with submissions to check for plagiarism!");
        }

        Set<File> canonicalRootDirectories = HashSet.newHashSet(rootDirectoryNames.size());
        for (final File rootDirectory : rootDirectoryNames) {
            if (!rootDirectory.exists()) {
                throw new RootDirectoryException(String.format("Root directory \"%s\" does not exist!", rootDirectory));
            }
            if (!rootDirectory.isDirectory()) {
                throw new RootDirectoryException(String.format("Root directory \"%s\" is not a directory!", rootDirectory));
            }

            File canonicalRootDirectory = makeCanonical(rootDirectory,
                    it -> new RootDirectoryException("Cannot read root directory: " + rootDirectory, it));
            if (!canonicalRootDirectories.add(canonicalRootDirectory)) {
                // Root directory was already added, report a warning.
                logger.warn("Root directory \"{}\" was specified more than once, duplicates will be ignored.", canonicalRootDirectory);
            }
        }
        return canonicalRootDirectories;
    }

    /**
     * Verify that the new and old directory sets are disjunct and modify the old submissions set if necessary.
     * @param submissionDirectories directories of submissions which should be checked for plagiarism
     * @param oldSubmissionDirectories directories of submissions which are considered possible sources of plagiarism
     */
    private void checkForNonOverlappingRootDirectories(Set<File> submissionDirectories, Set<File> oldSubmissionDirectories) {

        Set<File> commonRootdirectories = new HashSet<>(submissionDirectories);
        commonRootdirectories.retainAll(oldSubmissionDirectories);
        if (commonRootdirectories.isEmpty()) {
            return;
        }

        // As old submission directories are only read while new submission directories are both read and checked, the
        // former use can be removed without affecting the result of the checks.
        oldSubmissionDirectories.removeAll(commonRootdirectories);
        for (File rootDirectory : commonRootdirectories) {
            logger.warn(
                    "Root directory \"{}\" is specified both for plagiarism checking and for prior submissions, will perform plagiarism checking only.",
                    rootDirectory);
        }
    }

    private Optional<Submission> loadBaseCode() throws ExitException {
        if (!options.hasBaseCode()) {
            return Optional.empty();
        }

        File baseCodeSubmissionDirectory = options.baseCodeSubmissionDirectory();
        if (!baseCodeSubmissionDirectory.exists()) {
            throw new BasecodeException("Basecode directory \"%s\" does not exist".formatted(baseCodeSubmissionDirectory));
        }

        if (isFileExcluded(baseCodeSubmissionDirectory)) { // Stating an excluded path as basecode isn't very useful.
            throw new BasecodeException("Exclude submission: " + baseCodeSubmissionDirectory.getName());
        }
        if (baseCodeSubmissionDirectory.isFile() && !hasValidSuffix(baseCodeSubmissionDirectory)) {
            throw new BasecodeException("Ignore submission with invalid extension or suffix: " + baseCodeSubmissionDirectory.getName());
        }

        Submission baseCodeSubmission = processSubmission(baseCodeSubmissionDirectory.getName(), baseCodeSubmissionDirectory, false);
        logger.info("Basecode directory \"{}\" will be used.", baseCodeSubmission.getName());
        return Optional.of(baseCodeSubmission);
    }

    /**
     * Creates a {@link SubmissionFileData} object for each submission in the given root directory.
     * @param rootDirectory the root directory which may contain single-file submissions and submission directories.
     * @param isNew if true, the resulting submission files will be compared to all other submissions, including "old"
     * submissions.
     * @return the submission file data for each single-file submission
     * @throws RootDirectoryException if #rootDirectory is not a valid path or an I/O error occurs.
     */
    private List<SubmissionFileData> listSubmissionFiles(File rootDirectory, boolean isNew) throws RootDirectoryException {
        if (!rootDirectory.isDirectory()) {
            throw new AssertionError("Given root is not a directory.");
        }

        try {
            File[] files = rootDirectory.listFiles();
            if (files == null) {
                throw new RootDirectoryException("Cannot list files of the root directory!");
            }

            return Arrays.stream(files).sorted(Comparator.comparing(File::getName)).map(it -> new SubmissionFileData(it, rootDirectory, isNew))
                    .toList();
        } catch (SecurityException exception) {
            throw new RootDirectoryException("Cannot list files of the root directory! " + exception.getMessage(), exception);
        }
    }

    /**
     * Process the given directory entry as a submission. The complete path of the submission MUST be preserved!
     * @param submissionName The name of the submission
     * @param submissionFile the file for the submission.
     * @param isNew If true, the resulting submission should be checked for plagiarism.
     * @return The entry converted to a submission.
     * @throws ExitException when an error has been found while processing the entry.
     */
    private Submission processSubmission(String submissionName, File submissionFile, boolean isNew) throws ExitException {
        File file = submissionFile;
        if (file.isDirectory() && options.subdirectoryName() != null) {
            // Use subdirectory instead
            file = new File(file, options.subdirectoryName());

            if (!file.exists()) {
                throw new SubmissionException(
                        String.format("Submission %s does not contain the given subdirectory '%s'", submissionName, options.subdirectoryName()));
            }

            if (!file.isDirectory()) {
                throw new SubmissionException(String.format("The given subdirectory '%s' is not a directory!", options.subdirectoryName()));
            }
        }

        file = makeCanonical(file, it -> new SubmissionException("Cannot create submission: " + submissionName, it));
        return new Submission(submissionName, file, isNew, parseFilesRecursively(file), options.language());
    }

    private void processSubmissionFile(SubmissionFileData file, boolean multipleRoots, Map<File, Submission> foundSubmissions) throws ExitException {
        if (isFileExcluded(file.submissionFile())) {
            logger.error("Exclude submission: {}", file.submissionFile().getName());
        } else if (file.submissionFile().isFile() && !hasValidSuffix(file.submissionFile())) {
            logger.error("Ignore submission with invalid extension or suffix: {}", file.submissionFile().getName());
        } else {
            String rootDirectoryPrefix = multipleRoots ? file.rootDirectory().getName() + File.separator : "";
            String submissionName = rootDirectoryPrefix + file.submissionFile().getName();
            Submission submission = processSubmission(submissionName, file.submissionFile(), file.isNew());
            foundSubmissions.put(submission.getRoot(), submission);
        }
    }

    /**
     * Checks if a file has a valid file extension for the current language or ends in a specified suffix.
     * @param file is the file to check.
     * @return true if the file matches the file extension or suffix.
     */
    private boolean hasValidSuffix(File file) {
        List<String> validSuffixes = options.fileSuffixes();

        // This is the case if either the language modules or the CLI did not set the valid suffixes array in options
        if (validSuffixes == null || validSuffixes.isEmpty()) {
            return true;
        }
        return validSuffixes.stream().anyMatch(suffix -> file.getName().toLowerCase().endsWith(suffix.toLowerCase()));
    }

    /**
     * Checks if a file is excluded or not.
     */
    private boolean isFileExcluded(File file) {
        return options.excludedFiles().stream().anyMatch(excludedName -> file.getName().endsWith(excludedName));
    }

    /**
     * Recursively scan the given directory for nested files. Excluded files and files with an invalid extension or suffix
     * are ignored.
     * <p>
     * If the given file is not a directory, the input will be returned as a singleton list.
     * @param file - File to start the scan from.
     * @return a list of nested files.
     */
    private Collection<File> parseFilesRecursively(File file) {
        if (isFileExcluded(file)) {
            return Collections.emptyList();
        }

        if (file.isFile() && hasValidSuffix(file)) {
            return Collections.singletonList(file);
        }

        String[] nestedFileNames = file.list();

        if (nestedFileNames == null) {
            return Collections.emptyList();
        }

        Collection<File> files = new ArrayList<>();

        for (String fileName : nestedFileNames) {
            files.addAll(parseFilesRecursively(new File(file, fileName)));
        }

        return files;
    }

    /**
     * Computes the canonical file of a file, if an exception is thrown it is wrapped accordingly and re-thrown.
     */
    private File makeCanonical(File file, Function<Exception, ExitException> exceptionWrapper) throws ExitException {
        try {
            return file.getCanonicalFile();
        } catch (IOException exception) {
            throw exceptionWrapper.apply(exception);
        }
    }

}
