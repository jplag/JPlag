package de.jplag;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import de.jplag.exceptions.BasecodeException;
import de.jplag.exceptions.ExitException;
import de.jplag.exceptions.SubmissionException;
import de.jplag.options.JPlagOptions;

/**
 * Collection of all submissions and their basecode if it exists. Parses all submissions upon creation.
 */
public class SubmissionSet {
    /**
     * Submissions to check for plagiarism.
     */
    private final List<Submission> allSubmissions;
    private final List<Submission> invalidSubmissions;
    private final List<Submission> submissions;

    /**
     * Base code submission if it exists.
     */
    private final Optional<Submission> baseCodeSubmission;

    private final ErrorCollector errorCollector;
    private final JPlagOptions options;
    private int errors = 0;
    private String currentSubmissionName;

    /**
     * @param submissions Submissions to check for plagiarism.
     * @param baseCode Base code submission if it exists.
     */
    public SubmissionSet(List<Submission> submissions, Optional<Submission> baseCode, ErrorCollector errorCollector, JPlagOptions options)
            throws ExitException {
        this.allSubmissions = submissions;
        this.baseCodeSubmission = baseCode;
        this.errorCollector = errorCollector;
        this.options = options;
        parseAllSubmissions();
        this.submissions = filterValidSubmissions();
        invalidSubmissions = filterInvalidSubmissions();
    }

    /**
     * @return Whether a basecode is available for this collection.
     */
    public boolean hasBaseCode() {
        return !baseCodeSubmission.isEmpty();
    }

    /**
     * Retrieve the base code of this collection.<br>
     * <b>Asking for a non-existing basecode crashes the errorConsumer.</b>
     * @return The base code submission.
     * @see #hasBaseCode
     */
    public Submission getBaseCode() {
        if (baseCodeSubmission.isEmpty()) {
            throw new AssertionError("Querying a non-existing basecode submission.");
        }
        return baseCodeSubmission.get();
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
        return allSubmissions.stream().filter(submission -> submission.hasErrors()).collect(toList());
    }

    private void parseAllSubmissions() throws ExitException {
        try {
            parseSubmissions(allSubmissions);
            if (baseCodeSubmission.isPresent()) {
                parseBaseCodeSubmission(baseCodeSubmission.get()); // cannot use ifPresent because of throws declaration
            }
        } catch (OutOfMemoryError exception) {
            throw new SubmissionException("Out of memory during parsing of submission \"" + currentSubmissionName + "\"", exception);
        }
        if (errorCollector.hasErrors()) {
            errorCollector.printCollectedErrors();
        }
    }

    /**
     * Parse the given base code submission.
     */
    private void parseBaseCodeSubmission(Submission baseCode) throws BasecodeException {
        long startTime = System.currentTimeMillis();
        errorCollector.print("----- Parsing basecode submission: " + baseCode.getName(), null);
        if (!baseCode.parse(options.isDebugParser())) {
            errorCollector.printCollectedErrors();
            throw new BasecodeException("Could not successfully parse basecode submission!");
        } else if (baseCode.getNumberOfTokens() < options.getMinimumTokenMatch()) {
            throw new BasecodeException("Basecode submission contains fewer tokens than minimum match length allows!");
        }
        errorCollector.print("Basecode submission parsed!", null);
        long duration = System.currentTimeMillis() - startTime;
        errorCollector.print(null, "Time for parsing Basecode: " + TimeUtil.formatDuration(duration));

    }

    /**
     * Parse all given submissions.
     */
    private void parseSubmissions(List<Submission> submissions) {
        if (submissions.isEmpty()) {
            errorCollector.print("No submissions to parse!", null);
            return;
        }

        long startTime = System.currentTimeMillis();

        int tooShort = 0;
        for (Submission submission : submissions) {
            boolean ok;

            errorCollector.print(null, "------ Parsing submission: " + submission.getName());
            currentSubmissionName = submission.getName();
            errorCollector.setCurrentSubmissionName(currentSubmissionName);

            if (!(ok = submission.parse(options.isDebugParser()))) {
                errors++;
            }

            if (submission.getTokenList() != null && submission.getNumberOfTokens() < options.getMinimumTokenMatch()) {
                errorCollector.addError("Submission contains fewer tokens than minimum match length allows!");
                submission.setTokenList(null);
                tooShort++;
                ok = false;
                submission.markAsErroneous();
            }

            if (ok) {
                errorCollector.print(null, "OK");
            } else {
                errorCollector.print(null, "ERROR -> Submission removed");
            }
        }

        int validSubmissions = submissions.size() - errors - tooShort;
        errorCollector.print(validSubmissions + " submissions parsed successfully!", null);
        errorCollector.print(errors + " parser error" + (errors != 1 ? "s!" : "!") + "", null);
        errorCollector.print(tooShort + " too short submission" + (tooShort != 1 ? "s!" : "!") + "", null);
        printDetails(submissions, startTime, tooShort);
        errorCollector.print("", null); // new line
    }

    private void printDetails(List<Submission> submissions, long startTime, int tooShort) {
        if (tooShort == 1) {
            errorCollector.print(null, tooShort + " submission is not valid because it contains fewer tokens than minimum match length allows.");
        } else if (tooShort > 1) {
            errorCollector.print(null, tooShort + " submissions are not valid because they contain fewer tokens than minimum match length allows.");
        }

        long duration = System.currentTimeMillis() - startTime;
        String timePerSubmission = submissions.size() > 0 ? Long.toString(duration / submissions.size()) : "n/a";
        errorCollector.print(null, "Total time for parsing: " + TimeUtil.formatDuration(duration));
        errorCollector.print(null, "Time per parsed submission: " + timePerSubmission + " msec");
    }

}
