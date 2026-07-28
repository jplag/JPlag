package de.jplag;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import de.jplag.inputs.SubmissionDirectory;
import de.jplag.inputs.SubmissionFile;
import de.jplag.inputs.SubmissionFolder;
import de.jplag.inputs.SubmissionInputData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jplag.exceptions.BasecodeException;
import de.jplag.exceptions.ExitException;
import de.jplag.exceptions.RootDirectoryException;
import de.jplag.logging.ProgressBarLogger;
import de.jplag.logging.ProgressBarType;
import de.jplag.options.JPlagOptions;

/**
 * This class is responsible for the creation of a {@link SubmissionSet}. It processes multiple root directories of
 * submission, verifies the validity of submission, and processes the necessary source code files.
 */
public class SubmissionSetBuilder {

    private static final Logger logger = LoggerFactory.getLogger(SubmissionSetBuilder.class);

    private final JPlagOptions options;

    /**
     * Creates a builder for submission sets.
     *
     * @param language is the language of the submissions.
     * @param options  are the configured options.
     * @deprecated in favor of {@link #SubmissionSetBuilder(JPlagOptions)}.
     */
    @Deprecated(since = "4.3.0")
    public SubmissionSetBuilder(Language language, JPlagOptions options) {
        this(options.withLanguageOption(language));
    }

    /**
     * Creates a builder for submission sets.
     *
     * @param options are the configured options.
     */
    public SubmissionSetBuilder(JPlagOptions options) {
        this.options = options;
    }

    public SubmissionSet buildSubmissionSet() throws ExitException {
        Set<SubmissionDirectory> regularSubmssionDirectories = options.submissionDirectories();
        Set<SubmissionDirectory> oldSubmssionDirectories = options.oldSubmissionDirectories();

        List<SubmissionInputData> submissionCollector = new ArrayList<>();
        boolean isMultiRoot = regularSubmssionDirectories.size() + oldSubmssionDirectories.size() > 1;

        for (SubmissionDirectory regularSubmissionDirectory : regularSubmssionDirectories) {
            for (SubmissionFolder submission : regularSubmissionDirectory.resolveSubmissions()) {
                submissionCollector.add(new SubmissionInputData(regularSubmissionDirectory, submission, true, isMultiRoot));
            }
        }
        for (SubmissionDirectory oldSubmissionDirectory : oldSubmssionDirectories) {
            for (SubmissionFolder submission : oldSubmissionDirectory.resolveSubmissions()) {
                submissionCollector.add(new SubmissionInputData(oldSubmissionDirectory, submission, false, isMultiRoot));
            }
        }

        List<Submission> submissions = new ArrayList<>();
        ProgressBarLogger.iterate(ProgressBarType.LOADING, submissionCollector, (submission) -> {
            submission.applyFilter(this::isFileValid); //TODO Create file list here, but preserve tree
            if (options.subdirectoryName() != null) {
                submission.useSubdirectory(options.subdirectoryName());
            }

            //TODO remove if empty
            submissions.add(new Submission(submission, options.language()));
        });

        Optional<Submission> baseCode = findBaseCodeSubmission(submissions, options);
        if(baseCode.isPresent()) {
            submissions.remove(baseCode.get());
        }

        return new SubmissionSet(submissions, baseCode.orElse(null), options);
    }

    public Optional<Submission> findBaseCodeSubmission(List<Submission> submissions, JPlagOptions options) throws BasecodeException {
        if (!options.hasBaseCode()) {
            return Optional.empty();
        }

        List<Submission> matchingSubmissions = submissions.stream().filter(submission -> submission.matchesIdentifier(options.baseCodeSubmission())).toList();

        if (matchingSubmissions.size() != 1) {
            throw new BasecodeException("Cannot find basecode submission (" + options.baseCodeSubmission() + ")");
        }

        if(matchingSubmissions.size() == 1) {
            return Optional.ofNullable(matchingSubmissions.getFirst());
        } else {
            return Optional.empty();
        }
    }

    private boolean isFileValid(SubmissionFile file) {
        return hasValidSuffix(file) && !isFileExcluded(file);
    }

    /**
     * Checks if a file has a valid file extension for the current language or ends in a specified suffix.
     *
     * @param file is the file to check.
     * @return true if the file matches the file extension or suffix.
     */
    private boolean hasValidSuffix(SubmissionFile file) {
        List<String> validSuffixes = options.fileSuffixes();

        // This is the case if either the language modules or the CLI did not set the valid suffixes array in options
        if (validSuffixes == null || validSuffixes.isEmpty()) {
            return true;
        }
        return validSuffixes.stream().anyMatch(suffix -> file.name().toLowerCase().endsWith(suffix.toLowerCase()));
    }

    /**
     * Checks if a file is excluded or not.
     */
    private boolean isFileExcluded(SubmissionFile file) {
        return options.excludedFiles().stream().anyMatch(excludedName -> file.name().endsWith(excludedName));
    }
}
