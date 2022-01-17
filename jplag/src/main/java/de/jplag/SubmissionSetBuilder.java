package de.jplag;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

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

    private final Language language;
    private final JPlagOptions options;
    private final ErrorCollector errorCollector;
    private final Set<String> excludedFileNames; // Set of file names to be excluded in comparison.

    /**
     * Creates a builder for submission sets.
     * @param language is the language of the submissions.
     * @param options are the configured options.
     * @param errorCollector is the interface for error reporting.
     * @param excludedFileNames
     */
    public SubmissionSetBuilder(Language language,
                                JPlagOptions options,
                                ErrorCollector errorCollector, final Set<String> excludedFileNames) {
        this.language = language;
        this.options = options;
        this.errorCollector = errorCollector;
        this.excludedFileNames = excludedFileNames;
    }

    /**
     * Builds a submission set for all submissions of a specific directory.
     * @return the newly built submission set.
     * @throws ExitException if the directory cannot be read.
     */
    public SubmissionSet buildSubmissionSet() throws ExitException {
        // Read the root directory and collect valid looking submission entries from it.
        File rootDirectory = new File(options.getRootDirectoryName());
        verifyRootdirExistence(rootDirectory);
        String[] fileNames = readSubmissionRootNames(rootDirectory);
        Map<String, Submission> foundSubmissions = processRootDirEntries(rootDirectory, fileNames);

        // Extract the basecode submission if necessary.
        Optional<Submission> baseCodeSubmission = Optional.empty();
        if (options.hasBaseCode()) {
            verifyBaseCodeName();

            String baseCodeName = options.getBaseCodeSubmissionName();
            String baseCodePath = rootDirectory.toString() + File.separator + baseCodeName;

            // Grab the basecode submission and erase it from the regular submissions.
            Submission foundBaseCodeSubmission = foundSubmissions.get(baseCodeName);
            if (foundBaseCodeSubmission == null) {
                throw new BasecodeException("Basecode submission \"" + baseCodePath + "\" doesn't exist!");
            }

            foundSubmissions.remove(baseCodeName);
            baseCodeSubmission = Optional.of(foundBaseCodeSubmission);
            System.out.println("Basecode directory \"" + baseCodePath + "\" will be used");
        }

        // Merge everything in a submission set.
        List<Submission> submissions = new ArrayList<>(foundSubmissions.values());
        return new SubmissionSet(submissions, baseCodeSubmission, errorCollector, options);
    }

    /**
     * Verify that the given root directory exists.
     */
    private void verifyRootdirExistence(File rootDir) throws ExitException {
        String rootDirectoryName = rootDir.getName();
        if (!rootDir.exists()) {
            throw new RootDirectoryException(String.format("Root directory \"%s\" does not exist!", rootDirectoryName));
        }
        if (!rootDir.isDirectory()) {
            throw new RootDirectoryException(String.format("Root directory \"%s\" is not a directory!", rootDirectoryName));
        }
    }

    /**
     * Verify the basecode name.
     */
    private void verifyBaseCodeName() throws ExitException {
        String name = options.getBaseCodeSubmissionName();
        if (name.contains(".")) {
            throw new BasecodeException("The basecode directory name \"" + name + "\" cannot contain dots!");
        }
    }

    /**
     * Read entries in the given root directory.
     */
    private String[] readSubmissionRootNames(File rootDirectory) throws ExitException {
        if (!rootDirectory.isDirectory()) {
            throw new AssertionError("Given root is not a directory.");
        }

        String[] fileNames;

        try {
            fileNames = rootDirectory.list();
        } catch (SecurityException exception) {
            throw new RootDirectoryException("Cannot list files of the root directory! " + exception.getMessage());
        }

        if (fileNames == null) {
            throw new RootDirectoryException("Cannot list files of the root directory!");
        }

        Arrays.sort(fileNames);
        return fileNames;
    }

    /**
     * Process entries in the root directory to check whether they qualify as submissions.
     * @param rootDirectory Root directory being examined.
     * @param fileNames Entries found in the root directory.
     * @return Candidate submissions ordered by their name.
     */
    private Map<String, Submission> processRootDirEntries(File rootDirectory, String[] fileNames) throws ExitException {
        Map<String, Submission> foundSubmissions = new LinkedHashMap<>(fileNames.length); // Capacity is an over-estimate.

        for (String fileName : fileNames) {
            File submissionFile = new File(rootDirectory, fileName);

            if (isFileExcluded(submissionFile)) {
                System.out.println("Exclude submission: " + submissionFile.getName());
                continue;
            }

            if (submissionFile.isFile() && !hasValidSuffix(submissionFile)) {
                System.out.println("Ignore submission with invalid suffix: " + submissionFile.getName());
                continue;
            }

            if (submissionFile.isDirectory() && options.getSubdirectoryName() != null) {
                // Use subdirectory instead
                submissionFile = new File(submissionFile, options.getSubdirectoryName());

                if (!submissionFile.exists()) {
                    throw new SubmissionException(
                            String.format("Submission %s does not contain the given subdirectory '%s'", fileName, options.getSubdirectoryName()));
                }

                if (!submissionFile.isDirectory()) {
                    throw new SubmissionException(String.format("The given subdirectory '%s' is not a directory!", options.getSubdirectoryName()));
                }
            }

            Submission submission = new Submission(fileName, submissionFile, parseFilesRecursively(submissionFile), language, errorCollector);
            foundSubmissions.put(fileName, submission);
        }
        return foundSubmissions;
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

}
