package de.jplag;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;

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
        Set<Path> submissionDirectories = verifyRootDirectories(options.submissionDirectories(), true);
        Set<Path> oldSubmissionDirectories = verifyRootDirectories(options.oldSubmissionDirectories(), false);
        checkForNonOverlappingRootDirectories(submissionDirectories, oldSubmissionDirectories);

        // For backward compatibility, don't prefix submission names with their root directory
        // if there is only one root directory.
        int numberOfRootDirectories = submissionDirectories.size() + oldSubmissionDirectories.size();
        boolean multipleRoots = numberOfRootDirectories > 1;

        List<SubmissionFileData> submissionFiles = new ArrayList<>();
        for (Path submissionDirectory : submissionDirectories) {
            submissionFiles.addAll(listSubmissionFiles(submissionDirectory, true));
        }
        for (Path submissionDirectory : oldSubmissionDirectories) {
            submissionFiles.addAll(listSubmissionFiles(submissionDirectory, false));
        }

        ProgressBar progressBar = ProgressBarLogger.createProgressBar(ProgressBarType.LOADING, submissionFiles.size());
        Map<Path, Submission> foundSubmissions = new HashMap<>();
        try {
            for (SubmissionFileData submissionFile : submissionFiles) {
                processSubmissionFile(submissionFile, multipleRoots, foundSubmissions);
                progressBar.step();
            }
        } finally {
            progressBar.dispose();
        }

        Optional<Submission> baseCodeSubmission = loadBaseCode();
        baseCodeSubmission.ifPresent(baseSubmission -> foundSubmissions.remove(baseSubmission.getRoot()));

        // Merge everything in a submission set.
        List<Submission> submissions = new ArrayList<>(foundSubmissions.values());

        return new SubmissionSet(submissions, baseCodeSubmission.orElse(null), options);
    }

    /**
     * Verify that the given root directories exist and have no duplicate entries.
     */
    private Set<Path> verifyRootDirectories(Set<Path> rootDirectoryNames, boolean areNewDirectories) throws ExitException {
        if (areNewDirectories && rootDirectoryNames.isEmpty()) {
            throw new RootDirectoryException("No root directories specified with submissions to check for plagiarism!");
        }

        Set<Path> canonicalRootDirectories = HashSet.newHashSet(rootDirectoryNames.size());
        for (final Path rootDirectory : rootDirectoryNames) {
            if (!Files.exists(rootDirectory)) {
                throw new RootDirectoryException(String.format("Root directory \"%s\" does not exist!", rootDirectory));
            }
            if (!Files.exists(rootDirectory)) {
                throw new RootDirectoryException(String.format("Root directory \"%s\" is not a directory!", rootDirectory));
            }

            Path canonicalRootDirectory = makeCanonical(rootDirectory,
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
    private void checkForNonOverlappingRootDirectories(Set<Path> submissionDirectories, Set<Path> oldSubmissionDirectories) {

        Set<Path> commonRootdirectories = new HashSet<>(submissionDirectories);
        commonRootdirectories.retainAll(oldSubmissionDirectories);
        if (commonRootdirectories.isEmpty()) {
            return;
        }

        // As old submission directories are only read while new submission directories are both read and checked, the
        // former use can be removed without affecting the result of the checks.
        oldSubmissionDirectories.removeAll(commonRootdirectories);
        for (Path rootDirectory : commonRootdirectories) {
            logger.warn(
                    "Root directory \"{}\" is specified both for plagiarism checking and for prior submissions, will perform plagiarism checking only.",
                    rootDirectory);
        }
    }

    private Optional<Submission> loadBaseCode() throws ExitException {
        if (!options.hasBaseCode()) {
            return Optional.empty();
        }

        Path baseCodeSubmissionDirectory = options.baseCodeSubmissionDirectory();
        if (!Files.exists(baseCodeSubmissionDirectory)) {
            throw new BasecodeException("Basecode directory \"%s\" does not exist".formatted(baseCodeSubmissionDirectory));
        }

        if (isFileExcluded(baseCodeSubmissionDirectory)) { // Stating an excluded path as basecode isn't very useful.
            throw new BasecodeException("Exclude submission: " + baseCodeSubmissionDirectory.getFileName());
        }
        if (Files.isRegularFile(baseCodeSubmissionDirectory) && !hasValidSuffix(baseCodeSubmissionDirectory)) {
            throw new BasecodeException("Ignore submission with invalid extension or suffix: " + baseCodeSubmissionDirectory.getFileName());
        }

        Submission baseCodeSubmission = processSubmission(baseCodeSubmissionDirectory.getFileName().toString(), baseCodeSubmissionDirectory, false);
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
    private List<SubmissionFileData> listSubmissionFiles(Path rootDirectory, boolean isNew) throws RootDirectoryException {
        if (!Files.isDirectory(rootDirectory)) {
            throw new AssertionError("Given root is not a directory.");
        }

        try {
            Stream<Path> files = Files.list(rootDirectory);
            if (files == null) {
                throw new RootDirectoryException("Cannot list files of the root directory!");
            }

            return files.map(it -> new SubmissionFileData(it, rootDirectory, isNew)).toList();
        } catch (SecurityException | IOException exception) {
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
    private Submission processSubmission(String submissionName, Path submissionFile, boolean isNew) throws ExitException {
        Path file = submissionFile;
        if (Files.isDirectory(file) && options.subdirectoryName() != null) {
            // Use subdirectory instead
            file = file.resolve(options.subdirectoryName());

            if (!Files.exists(file)) {
                throw new SubmissionException(
                        String.format("Submission %s does not contain the given subdirectory '%s'", submissionName, options.subdirectoryName()));
            }

            if (!Files.isDirectory(file)) {
                throw new SubmissionException(String.format("The given subdirectory '%s' is not a directory!", options.subdirectoryName()));
            }
        }

        file = makeCanonical(file, it -> new SubmissionException("Cannot create submission: " + submissionName, it));
        return new Submission(submissionName, file, isNew, listFilesRecursively(file), options.language());
    }

    private void processSubmissionFile(SubmissionFileData file, boolean multipleRoots, Map<Path, Submission> foundSubmissions) throws ExitException {
        if (isFileExcluded(file.submissionFile())) {
            logger.error("Exclude submission: {}", file.submissionFile().getFileName());
        } else if (Files.isRegularFile(file.submissionFile()) && !hasValidSuffix(file.submissionFile())) {
            logger.error("Ignore submission with invalid extension or suffix: {}", file.submissionFile().getFileName());
        } else {
            String rootDirectoryPrefix = multipleRoots ? file.rootDirectory().getFileName() + File.separator : "";
            String submissionName = rootDirectoryPrefix + file.submissionFile().getFileName();
            Submission submission = processSubmission(submissionName, file.submissionFile(), file.isNew());
            foundSubmissions.put(submission.getRoot(), submission);
        }
    }

    /**
     * Checks if a file has a valid file extension for the current language or ends in a specified suffix.
     * @param file is the file to check.
     * @return true if the file matches the file extension or suffix.
     */
    private boolean hasValidSuffix(Path file) {
        List<String> validSuffixes = options.fileSuffixes();

        // This is the case if either the language modules or the CLI did not set the valid suffixes array in options
        if (validSuffixes == null || validSuffixes.isEmpty()) {
            return true;
        }
        return validSuffixes.stream().anyMatch(suffix -> file.getFileName().toString().toLowerCase().endsWith(suffix.toLowerCase()));
    }

    /**
     * Checks if a file is excluded or not.
     */
    private boolean isFileExcluded(Path file) {
        return options.excludedFiles().stream().anyMatch(excludedName -> file.getFileName().toString().endsWith(excludedName));
    }

    /**
     * Recursively scan the given directory for nested files. Excluded files and files with an invalid extension or suffix
     * are ignored.
     * <p>
     * If the given file is not a directory, the input will be returned as a singleton list.
     * @param file - File to start the scan from.
     * @return a list of nested files.
     */
    private Collection<Path> listFilesRecursively(Path file) {
        if (isFileExcluded(file)) {
            return Collections.emptyList();
        }

        if (Files.isRegularFile(file) && hasValidSuffix(file)) {
            return Collections.singletonList(file);
        }
        try {
            Stream<Path> nestedFileNames = Files.list(file);

            if (nestedFileNames == null) {
                return Collections.emptyList();
            }

            Collection<Path> files = new ArrayList<>();

            for (Path fileName : nestedFileNames.toList()) {
                files.addAll(listFilesRecursively(file.resolve(fileName)));
            }

            return files;
        } catch (IOException _) {
            return Collections.emptyList();
        }
    }

    /**
     * Computes the canonical file of a file, if an exception is thrown it is wrapped accordingly and re-thrown.
     */
    private Path makeCanonical(Path file, Function<Exception, ExitException> exceptionWrapper) throws ExitException {
        try {
            return file.toRealPath();
        } catch (IOException exception) {
            throw exceptionWrapper.apply(exception);
        }
    }

}
