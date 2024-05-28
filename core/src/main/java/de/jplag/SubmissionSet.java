package de.jplag;

import static de.jplag.SubmissionState.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jplag.exceptions.BasecodeException;
import de.jplag.exceptions.ExitException;
import de.jplag.exceptions.SubmissionException;
import de.jplag.logging.ProgressBar;
import de.jplag.logging.ProgressBarLogger;
import de.jplag.logging.ProgressBarType;
import de.jplag.options.JPlagOptions;

/**
 * Collection of all submissions and their basecode if it exists. Parses all submissions upon creation.
 */
public class SubmissionSet {
    private static final Logger logger = LoggerFactory.getLogger(SubmissionSet.class);

    /**
     * Submissions to check for plagiarism.
     */
    private final List<Submission> allSubmissions;
    private final List<Submission> invalidSubmissions;
    private final List<Submission> submissions;

    /**
     * Base code submission if it exists.
     */
    private final Submission baseCodeSubmission;

    private final JPlagOptions options;
    private int errors = 0;
    private String currentSubmissionName;

    /**
     * @param submissions Submissions to check for plagiarism.
     * @param baseCode Base code submission if it exists or {@code null}.
     * @param options The JPlag options
     */
    public SubmissionSet(List<Submission> submissions, Submission baseCode, JPlagOptions options) throws ExitException {
        this.allSubmissions = submissions;
        this.baseCodeSubmission = baseCode;
        this.options = options;
        parseAllSubmissions();
        this.submissions = filterValidSubmissions();
        invalidSubmissions = filterInvalidSubmissions();
    }

    /**
     * @return Whether a basecode is available for this collection.
     */
    public boolean hasBaseCode() {
        return baseCodeSubmission != null;
    }

    /**
     * Retrieve the base code of this collection.<br>
     * <b>Asking for a non-existing basecode crashes the errorConsumer.</b>
     * @return The base code submission.
     * @see #hasBaseCode
     */
    public Submission getBaseCode() {
        if (baseCodeSubmission == null) {
            throw new AssertionError("Querying a non-existing basecode submission.");
        }
        return baseCodeSubmission;
    }

    /**
     * @return The number of valid submissions.
     */
    public int numberOfSubmissions() {
        return submissions.size();
    }

    /**
     * Obtain the valid submissions.<br>
     * <b>Changes in the list are reflected in this instance.</b>
     */
    public List<Submission> getSubmissions() {
        return submissions;
    }

    /**
     * Obtain the invalid submissions.<br>
     * <b>Changes in the list are reflected in this instance.</b>
     */
    public List<Submission> getInvalidSubmissions() {
        return invalidSubmissions;
    }

    public void normalizeSubmissions() {
        ProgressBarLogger.iterate(ProgressBarType.TOKEN_STRING_NORMALIZATION, submissions, Submission::normalize);
    }

    private List<Submission> filterValidSubmissions() {
        return allSubmissions.stream().filter(it -> it.getState() == VALID).collect(Collectors.toCollection(ArrayList::new));
    }

    private List<Submission> filterInvalidSubmissions() {
        return allSubmissions.stream().filter(it -> it.getState() != VALID).toList();
    }

    private void parseAllSubmissions() throws ExitException {
        try {
            parseSubmissions(allSubmissions);
            if (baseCodeSubmission != null) {
                parseBaseCodeSubmission(baseCodeSubmission);
            }
        } catch (OutOfMemoryError exception) {
            throw new SubmissionException("Out of memory during parsing of submission \"" + currentSubmissionName + "\"", exception);
        }
    }

    /**
     * Parse the given base code submission.
     */
    private void parseBaseCodeSubmission(Submission baseCode) throws BasecodeException {
        logger.trace("----- Parsing basecode submission: " + baseCode.getName());
        if (!baseCode.parse(options.debugParser(), options.normalize(), options.minimumTokenMatch())) {
            if (baseCode.getState() == SubmissionState.TOO_SMALL) {
                throw new BasecodeException("Basecode contains %d token(s), which is below the minimum match length (%d)!"
                        .formatted(baseCode.getNumberOfTokens(), options.minimumTokenMatch()));
            } else {
                throw new BasecodeException("Error while parsing the basecode submission!");
            }

        }
    }

    /**
     * Parse all given submissions.
     * @param submissions The list of submissions
     */
    private void parseSubmissions(List<Submission> submissions) {
        if (submissions.isEmpty()) {
            logger.error("No submissions to parse!");
            return;
        }

        ProgressBar progressBar = ProgressBarLogger.createProgressBar(ProgressBarType.PARSING, submissions.size());
        for (Submission submission : submissions) {

            logger.trace("------ Parsing submission: " + submission.getName());
            currentSubmissionName = submission.getName();

            boolean successful = submission.parse(options.debugParser(), options.normalize(), options.minimumTokenMatch());
            if (!successful) {
                errors++;
                logger.error("ERROR -> Submission {} removed with reason {}", currentSubmissionName, submission.getState());
            }
            progressBar.step();
        }
        progressBar.dispose();

        int validSubmissions = submissions.size() - errors;
        logger.debug(validSubmissions + " submissions parsed successfully!");
        logger.debug(errors + " parser error" + (errors != 1 ? "s!" : "!"));
    }

}
