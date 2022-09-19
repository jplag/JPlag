package de.jplag;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jplag.exceptions.BasecodeException;
import de.jplag.exceptions.ExitException;
import de.jplag.exceptions.SubmissionException;
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

    private List<Submission> filterValidSubmissions() {
        return allSubmissions.stream().filter(submission -> !submission.hasErrors()).collect(Collectors.toCollection(ArrayList::new));
    }

    private List<Submission> filterInvalidSubmissions() {
        return allSubmissions.stream().filter(Submission::hasErrors).toList();
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
        long startTime = System.currentTimeMillis();
        logger.trace("----- Parsing basecode submission: " + baseCode.getName());
        if (!baseCode.parse(options.debugParser())) {
            throw new BasecodeException("Could not successfully parse basecode submission!");
        } else if (baseCode.getNumberOfTokens() < options.minimumTokenMatch()) {
            throw new BasecodeException("Basecode submission contains fewer tokens than minimum match length allows!");
        }
        logger.trace("Basecode submission parsed!");
        long duration = System.currentTimeMillis() - startTime;
        logger.trace("Time for parsing Basecode: " + TimeUtil.formatDuration(duration));

    }

    /**
     * Parse all given submissions.
     */
    private void parseSubmissions(List<Submission> submissions) {
        if (submissions.isEmpty()) {
            logger.warn("No submissions to parse!");
            return;
        }

        long startTime = System.currentTimeMillis();

        int tooShort = 0;
        for (Submission submission : submissions) {
            boolean ok;

            logger.trace("------ Parsing submission: " + submission.getName());
            currentSubmissionName = submission.getName();

            if (!(ok = submission.parse(options.debugParser()))) {
                errors++;
            }

            if (submission.getTokenList() != null && submission.getNumberOfTokens() < options.minimumTokenMatch()) {
                logger.error("Submission {} contains fewer tokens than minimum match length allows!", currentSubmissionName);
                submission.setTokenList(null);
                tooShort++;
                ok = false;
                submission.markAsErroneous();
            }

            if (ok) {
                logger.trace("OK");
            } else {
                logger.error("ERROR -> Submission {} removed", currentSubmissionName);
            }
        }

        int validSubmissions = submissions.size() - errors - tooShort;
        logger.trace(validSubmissions + " submissions parsed successfully!");
        logger.trace(errors + " parser error" + (errors != 1 ? "s!" : "!"));
        logger.trace(tooShort + " too short submission" + (tooShort != 1 ? "s!" : "!"));
        printDetails(submissions, startTime, tooShort);
    }

    private void printDetails(List<Submission> submissions, long startTime, int tooShort) {
        if (tooShort == 1) {
            logger.trace(tooShort + " submission is not valid because it contains fewer tokens than minimum match length allows.");
        } else if (tooShort > 1) {
            logger.trace(tooShort + " submissions are not valid because they contain fewer tokens than minimum match length allows.");
        }

        long duration = System.currentTimeMillis() - startTime;
        String timePerSubmission = submissions.isEmpty() ? "n/a" : Long.toString(duration / submissions.size());
        logger.trace("Total time for parsing: " + TimeUtil.formatDuration(duration));
        logger.trace("Time per parsed submission: " + timePerSubmission + " msec");
    }

}
