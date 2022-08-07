package de.jplag;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
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
import de.jplag.options.JPlagOptions;

/**
 * Builder class for the creation of a {@link SubmissionSet}.
 * @author Timur Saglam
 */
public class SubmissionSetBuilder {

    private static final Logger logger = LoggerFactory.getLogger(SubmissionSetBuilder.class);

    private final Language language;
    private final JPlagOptions options;
    private final Set<String> excludedFileNames; // Set of file names to be excluded in comparison.

    /**
     * Creates a builder for submission sets.
     * @param language is the language of the submissions.
     * @param options are the configured options.
     * @param excludedFileNames a list of file names to be excluded
     */
    public SubmissionSetBuilder(Language language, JPlagOptions options, Set<String> excludedFileNames) {
        this.language = language;
        this.options = options;
        this.excludedFileNames = excludedFileNames;
    }

    /**
     * Builds a submission set for all submissions of a specific directory.
     * @return the newly built submission set.
     * @throws ExitException if the directory cannot be read.
     */
    public SubmissionSet buildSubmissionSet() throws ExitException {
        Set<File> submissionDirectories = verifyRootDirectories(options.getSubmissionDirectories(), true);
        Set<File> oldSubmissionDirectories = verifyRootDirectories(options.getOldSubmissionDirectories(), false);
        checkForNonOverlappingRootDirectories(submissionDirectories, oldSubmissionDirectories);

        // For backward compatibility, don't prefix submission names with their root directory
        // if there is only one root directory.
        int numberOfRootDirectories = submissionDirectories.size() + oldSubmissionDirectories.size();
        boolean multipleRoots = (numberOfRootDirectories > 1);

        // Collect valid looking entries from the root directories.
        Map<File, Submission> foundSubmissions = new HashMap<>();
        for (File directory : submissionDirectories) {
            processRootDirectoryEntries(directory, multipleRoots, foundSubmissions, true);
        }
        for (File oldDirectory : oldSubmissionDirectories) {
            processRootDirectoryEntries(oldDirectory, multipleRoots, foundSubmissions, false);
        }

        Optional<Submission> baseCodeSubmission = loadBaseCode(submissionDirectories, oldSubmissionDirectories, foundSubmissions);

        // Merge everything in a submission set.
        List<Submission> submissions = new ArrayList<>(foundSubmissions.values());
        return new SubmissionSet(submissions, baseCodeSubmission.orElse(null), options);
    }

    /**
     * Verify that the given root directories exist and have no duplicate entries.
     */
    private Set<File> verifyRootDirectories(List<String> rootDirectoryNames, boolean areNewDirectories) throws ExitException {
        if (areNewDirectories && rootDirectoryNames.isEmpty()) {
            throw new RootDirectoryException("No root directories specified with submissions to check for plagiarism!");
        }

        Set<File> canonicalRootDirectories = new HashSet<>(rootDirectoryNames.size());
        for (String rootDirectoryName : rootDirectoryNames) {
            File rootDirectory = new File(rootDirectoryName);

            if (!rootDirectory.exists()) {
                throw new RootDirectoryException(String.format("Root directory \"%s\" does not exist!", rootDirectoryName));
            }
            if (!rootDirectory.isDirectory()) {
                throw new RootDirectoryException(String.format("Root directory \"%s\" is not a directory!", rootDirectoryName));
            }

            rootDirectory = makeCanonical(rootDirectory, it -> new RootDirectoryException("Cannot read root directory: " + rootDirectoryName, it));
            if (!canonicalRootDirectories.add(rootDirectory)) {
                // Root directory was already added, report a warning.
                logger.warn("Root directory \"{}\" was specified more than once, duplicates will be ignored.", rootDirectoryName);
            }
        }
        return canonicalRootDirectories;
    }

    /**
     * Verify that the new and old directory sets are disjunct and modify the old submissions set if necessary.
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

    private Optional<Submission> loadBaseCode(Set<File> submissionDirectories, Set<File> oldSubmissionDirectories,
            Map<File, Submission> foundSubmissions) throws ExitException {
        if (!options.hasBaseCode()) {
            return Optional.empty();
        }

        String baseCodeName = options.getBaseCodeSubmissionName().orElseThrow();
        Submission baseCode = loadBaseCodeAsPath(baseCodeName);
        if (baseCode == null) {
            int numberOfRootDirectories = submissionDirectories.size() + oldSubmissionDirectories.size();
            if (numberOfRootDirectories > 1) {
                throw new BasecodeException("The base code submission needs to be specified by path instead of by name!");
            }

            // There is one root directory, and the submissionDirectories variable has been checked to be non-empty.
            // That set thus contains the one and only root directory.
            File rootDirectory = submissionDirectories.iterator().next();

            // Single root-directory, try the legacy way of specifying basecode.
            baseCode = loadBaseCodeViaName(baseCodeName, rootDirectory, foundSubmissions);
        }

        if (baseCode != null) {
            logger.info("Basecode directory \"{}\" will be used.", baseCode.getName());

            // Basecode may also be registered as a user submission. If so, remove the latter.
            Submission removed = foundSubmissions.remove(baseCode.getRoot());
            if (removed != null) {
                logger.info("Submission \"{}\" is the specified basecode, it will be skipped during comparison.", removed.getName());
            }
        }
        return Optional.ofNullable(baseCode);
    }

    /**
     * Try to load the basecode under the assumption of being a path.
     * @return Base code submission if the option value can be interpreted as global path, else {@code null}.
     * @throws ExitException when the option value is a path with errors.
     */
    private Submission loadBaseCodeAsPath(String baseCodeName) throws ExitException {
        File basecodeSubmission = new File(baseCodeName);
        if (!basecodeSubmission.exists()) {
            return null;
        }

        String errorMessage = isExcludedEntry(basecodeSubmission);
        if (errorMessage != null) {
            throw new BasecodeException(errorMessage); // Stating an excluded path as basecode isn't very useful.
        }

        try {
            // Use an unlikely short name for the base code. If all is well, this name should not appear
            // in the output since basecode matches are removed from it
            return processSubmission(basecodeSubmission.getName(), basecodeSubmission, false);
        } catch (SubmissionException exception) {
            throw new BasecodeException(exception.getMessage(), exception); // Change thrown exception to basecode exception.
        }
    }

    /**
     * Try to load the basecode by looking up the basecode name in the root directory.
     * @return the base code submission if a fitting subdirectory was found, else {@code null}.
     * @throws ExitException when the option value is a sub-directory with errors.
     */
    private Submission loadBaseCodeViaName(String baseCodeName, File rootDirectory, Map<File, Submission> foundSubmissions) throws ExitException {
        // Is the option value a single name after trimming spurious separators?
        String name = baseCodeName;
        while (name.startsWith(File.separator)) {
            name = name.substring(1);
        }
        while (name.endsWith(File.separator)) {
            name = name.substring(0, name.length() - 1);
        }

        // If it is not a name of a single sub-directory, bail out.
        if (name.isEmpty() || name.contains(File.separator)) {
            return null;
        }

        if (name.contains(".")) {
            throw new BasecodeException("The basecode directory name \"" + name + "\" cannot contain dots!");
        }

        // Grab the basecode submission from the regular submissions.
        File basecodePath = new File(rootDirectory, baseCodeName);

        Submission baseCode = foundSubmissions
                .get(makeCanonical(basecodePath, it -> new BasecodeException("Cannot compute canonical file path: " + basecodePath, it)));
        if (baseCode == null) {
            // No base code found at all, report an error.
            throw new BasecodeException(String.format("Basecode path \"%s\" relative to the working directory could not be found.", baseCodeName));
        } else {
            // Found a base code as a submission, warn about legacy usage.
            logger.warn("Legacy use of the base code option found, please specify the basecode by path instead of by name!");
        }
        return baseCode;
    }

    /**
     * Read entries in the given root directory.
     */
    private String[] listSubmissionFiles(File rootDirectory) throws ExitException {
        if (!rootDirectory.isDirectory()) {
            throw new AssertionError("Given root is not a directory.");
        }

        String[] fileNames;

        try {
            fileNames = rootDirectory.list();
        } catch (SecurityException exception) {
            throw new RootDirectoryException("Cannot list files of the root directory! " + exception.getMessage(), exception);
        }

        if (fileNames == null) {
            throw new RootDirectoryException("Cannot list files of the root directory!");
        }

        Arrays.sort(fileNames);
        return fileNames;
    }

    /**
     * Check that the given submission entry is not invalid due to exclusion names or bad suffix.
     * @param submissionEntry Entry to check.
     * @return Error message if the entry should be ignored.
     */
    private String isExcludedEntry(File submissionEntry) {
        if (isFileExcluded(submissionEntry)) {
            return "Exclude submission: " + submissionEntry.getName();
        }

        if (submissionEntry.isFile() && !hasValidSuffix(submissionEntry)) {
            return "Ignore submission with invalid suffix: " + submissionEntry.getName();
        }
        return null;
    }

    /**
     * Process the given directory entry as a submission, the path MUST not be excluded.
     * @param submissionFile the file for the submission.
     * @param isNew states whether submissions found in the root directory must be checked for plagiarism.
     * @return The entry converted to a submission.
     * @throws ExitException when an error has been found with the entry.
     */
    private Submission processSubmission(String submissionName, File submissionFile, boolean isNew) throws ExitException {

        if (submissionFile.isDirectory() && options.getSubdirectoryName() != null) {
            // Use subdirectory instead
            submissionFile = new File(submissionFile, options.getSubdirectoryName());

            if (!submissionFile.exists()) {
                throw new SubmissionException(
                        String.format("Submission %s does not contain the given subdirectory '%s'", submissionName, options.getSubdirectoryName()));
            }

            if (!submissionFile.isDirectory()) {
                throw new SubmissionException(String.format("The given subdirectory '%s' is not a directory!", options.getSubdirectoryName()));
            }
        }

        submissionFile = makeCanonical(submissionFile, it -> new SubmissionException("Cannot create submission: " + submissionName, it));
        return new Submission(submissionName, submissionFile, isNew, parseFilesRecursively(submissionFile), language);
    }

    /**
     * Process entries in the root directory to check whether they qualify as submissions.
     * @param rootDirectory is the root directory being examined.
     * @param foundSubmissions Submissions found so far, is updated in-place.
     * @param isNew states whether submissions found in the root directory must be checked for plagiarism.
     */
    private void processRootDirectoryEntries(File rootDirectory, boolean multipleRoots, Map<File, Submission> foundSubmissions, boolean isNew)
            throws ExitException {
        for (String fileName : listSubmissionFiles(rootDirectory)) {
            File submissionFile = new File(rootDirectory, fileName);

            String errorMessage = isExcludedEntry(submissionFile);
            if (errorMessage == null) {
                String rootDirectoryPrefix = multipleRoots ? (rootDirectory.getName() + File.separator) : "";
                String submissionName = rootDirectoryPrefix + fileName;
                Submission submission = processSubmission(submissionName, submissionFile, isNew);
                foundSubmissions.put(submission.getRoot(), submission);
            } else {
                logger.error(errorMessage);
            }
        }
    }

    /**
     * Checks if a file has a valid suffix for the current language.
     * @param file is the file to check.
     * @return true if the file suffix matches the language.
     */
    private boolean hasValidSuffix(File file) {
        String[] validSuffixes = options.getFileSuffixes();

        // This is the case if either the language frontends or the CLI did not set the valid suffixes array in options
        if (validSuffixes == null || validSuffixes.length == 0) {
            return true;
        }
        return Arrays.stream(validSuffixes).anyMatch(suffix -> file.getName().endsWith(suffix));
    }

    /**
     * Checks if a file is excluded or not.
     */
    private boolean isFileExcluded(File file) {
        return excludedFileNames.stream().anyMatch(excludedName -> file.getName().endsWith(excludedName));
    }

    /**
     * Recursively scan the given directory for nested files. Excluded files and files with an invalid suffix are ignored.
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
