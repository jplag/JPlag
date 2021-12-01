package de.jplag;

import static de.jplag.options.Verbosity.LONG;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import de.jplag.options.JPlagOptions;

/**
 * Builder class for the creation of a {@link SubmissionSet}.
 * @author Timur Saglam
 */
public class SubmissionSetBuilder {

    private final Language language;
    private final JPlagOptions options;
    private final ErrorCollector errorCollector;
    private final HashSet<String> excludedFileNames; // Set of file names to be excluded in comparison.

    /**
     * Creates a builder for submission sets.
     * @param language is the language of the submissions.
     * @param options are the configured options.
     * @param errorCollector is the interface for error reporting.
     */
    public SubmissionSetBuilder(Language language, JPlagOptions options, ErrorCollector errorCollector) {
        this.language = language;
        this.options = options;
        this.errorCollector = errorCollector;
        excludedFileNames = readExclusionFile();
    }

    /**
     * Builds a submission set for all submissions of a specific directory.
     * @param rootDirectory is the specific directory.
     * @param setName Name of the submission set.
     * @return the newly built submission set.
     * @throws ExitException if the directory cannot be read.
     */
    public SubmissionSet buildSubmissionSet(File rootDirectory, String setName) throws ExitException {
        String[] fileNames;

        try {
            fileNames = rootDirectory.list();
        } catch (SecurityException exception) {
            throw new ExitException("Cannot list files of the root directory! " + exception.getMessage());
        }

        if (fileNames == null) {
            throw new ExitException("Cannot list files of the root directory! " + "Make sure the specified root directory is in fact a directory.");
        }

        Arrays.sort(fileNames);

        return mapFileNamesToSubmissions(setName, fileNames, rootDirectory);
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
     * Construct a submission set from the file names.
     * @param setName If not {@ocde null}, the name of the set.
     * @param fileNames Submission roots relative to 'rootDirectory'.
     * @param rootDirectory Root path of the submission set.
     * @return The constructed submission set.
     * @throws ExitException when an error was detected in the submissions.
     */
    private SubmissionSet mapFileNamesToSubmissions(String setName, String[] fileNames, File rootDirectory) throws ExitException {
        List<Submission> submissions = new ArrayList<>();
        Optional<Submission> baseCodeSubmission = Optional.empty();

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
                    throw new ExitException(
                            String.format("Submission %s does not contain the given subdirectory '%s'", fileName, options.getSubdirectoryName()));
                }

                if (!submissionFile.isDirectory()) {
                    throw new ExitException(String.format("The given subdirectory '%s' is not a directory!", options.getSubdirectoryName()));
                }
            }

            String submissionName = (setName == null) ? fileName : setName + "::" + fileName;
            Submission submission = new Submission(submissionName, submissionFile, parseFilesRecursively(submissionFile), language, errorCollector);

            if (options.hasBaseCode() && options.getBaseCodeSubmissionName().equals(fileName)) {
                baseCodeSubmission = Optional.of(submission);
            } else {
                submissions.add(submission);
            }
        }

        return new SubmissionSet(setName, submissions, baseCodeSubmission, errorCollector, options);
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
     * If an exclusion file is given, it is read in and all strings are saved in the set "excluded".
     */
    private HashSet<String> readExclusionFile() {
        HashSet<String> excludedFileNames = new HashSet<>();
        if (options.getExclusionFileName() != null) {
            try {
                BufferedReader reader = new BufferedReader(new FileReader(options.getExclusionFileName(), JPlagOptions.CHARSET));
                String line;
                while ((line = reader.readLine()) != null) {
                    excludedFileNames.add(line.trim());
                }
                reader.close();
            } catch (IOException exception) {
                System.out.println("Could not read exclusion file: " + exception.getMessage());
            }

            if (options.getVerbosity() == LONG) {
                errorCollector.print(null, "Excluded files:");

                for (String excludedFileName : excludedFileNames) {
                    errorCollector.print(null, "  " + excludedFileName);
                }
            }
        }
        return excludedFileNames;
    }
}
