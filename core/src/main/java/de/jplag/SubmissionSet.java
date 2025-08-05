package de.jplag;

import static de.jplag.SubmissionState.VALID;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jplag.exceptions.BasecodeException;
import de.jplag.exceptions.ExitException;
import de.jplag.exceptions.LanguageException;
import de.jplag.exceptions.SubmissionException;
import de.jplag.logging.ProgressBar;
import de.jplag.logging.ProgressBarLogger;
import de.jplag.logging.ProgressBarType;
import de.jplag.options.JPlagOptions;

/**
 * This class represents a collection of submissions to be checked for plagiarism. It manages both valid and invalid
 * submissions, as well as an optional base code submission. Instances of this class are responsible for parsing
 * submissions, filtering them based on their validity, and providing access to the valid and invalid submissions.
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
    private final AtomicInteger errors = new AtomicInteger(0);

    /**
     * Creates a submissions set and parses all submissions.
     * @param submissions list of submissions to check for plagiarism.
     * @param baseCode Base code submission if it exists or {@code null}.
     * @param options The JPlag options
     * @throws ExitException if the submissions cannot be parsed.
     */
    public SubmissionSet(List<Submission> submissions, Submission baseCode, JPlagOptions options) throws ExitException {
        this.allSubmissions = submissions;
        this.baseCodeSubmission = baseCode;
        this.options = options;
        parseSubmissions(allSubmissions);
        if (baseCodeSubmission != null) {
            parseBaseCodeSubmission(baseCodeSubmission);
        }
        this.submissions = filterValidSubmissions();
        invalidSubmissions = filterInvalidSubmissions();
    }

    /**
     * @return true if the submission set has a basecode submission.
     */
    public boolean hasBaseCode() {
        return baseCodeSubmission != null;
    }

    /**
     * Retrieve the base code of this collection.
     * @return The base code submission.
     * @throws IllegalStateException if no base code is present.
     * @see #hasBaseCode
     */
    public Submission getBaseCode() {
        if (baseCodeSubmission == null) {
            throw new IllegalStateException("Querying a non-existing basecode submission.");
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
     * Obtain the valid submissions. Changes in the list are reflected in this instance.
     * @return the valid submissions.
     */
    public List<Submission> getSubmissions() {
        return submissions;
    }

    /**
     * Obtain the invalid submissions. Changes in the list are reflected in this instance.
     * @return the invalid submissions.
     */
    public List<Submission> getInvalidSubmissions() {
        return invalidSubmissions;
    }

    /**
     * Normalizes the token sequences of all submissions (including basecode). This makes the token sequence invariant to
     * dead code insertion and independent statement reordering by removing dead tokens and optionally reordering tokens to
     * a deterministic order.
     */
    public void normalizeSubmissions() {
        if (baseCodeSubmission != null) {
            baseCodeSubmission.normalize();
        }
        ProgressBar progressBar = ProgressBarLogger.createProgressBar(ProgressBarType.TOKEN_SEQUENCE_NORMALIZATION, submissions.size());
        submissions.parallelStream().forEach(submission -> {
            submission.normalize();
            progressBar.step();
        });
        progressBar.dispose();
    }

    private List<Submission> filterValidSubmissions() {
        return allSubmissions.stream().filter(it -> it.getState() == VALID).collect(Collectors.toCollection(ArrayList::new));
    }

    private List<Submission> filterInvalidSubmissions() {
        return allSubmissions.stream().filter(it -> it.getState() != VALID).toList();
    }

    /**
     * Parse the given base code submission.
     */
    private void parseBaseCodeSubmission(Submission baseCode) throws BasecodeException, LanguageException {
        logger.trace("----- Parsing basecode submission: {}", baseCode.getName());
        if (!baseCode.parse(options.debugParser(), options.normalize(), options.minimumTokenMatch(), options.analyzeComments())) {
            if (baseCode.getState() == SubmissionState.TOO_SMALL) {
                throw new BasecodeException("Basecode contains %d token(s), which is below the minimum match length (%d)!"
                        .formatted(baseCode.getNumberOfTokens(), options.minimumTokenMatch()));
            }
            throw new BasecodeException("Error while parsing the basecode submission!");

        }
    }

    /**
     * Parse all given submissions.
     * @param submissions The list of submissions
     */
    private void parseSubmissions(List<Submission> submissions) throws ExitException {
        if (submissions.isEmpty()) {
            logger.error("No submissions to parse!");
            return;
        }

        ProgressBar progressBar = ProgressBarLogger.createProgressBar(ProgressBarType.PARSING, submissions.size());

        if (options.language().expectsSubmissionOrder()) {
            for (Submission submission : submissions) {
                parseSingleSubmission(progressBar, submission);
            }
        } else {
            parseSubmissionsInParallel(submissions, progressBar);
        }

        progressBar.dispose();

        int validSubmissions = submissions.size() - errors.get();
        logger.debug("{} submissions parsed successfully!", validSubmissions);
        logger.debug("{} parser error{}!", errors, errors.get() != 1 ? "s" : "");
    }

    private void parseSubmissionsInParallel(List<Submission> submissions, ProgressBar progressBar) throws SubmissionException {
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            for (Submission submission : submissions) {
                executor.submit(() -> {
                    parseSingleSubmission(progressBar, submission);
                    return null; // Ensure the lambda is a Callable for exception handling
                });
            }
            executor.shutdown();
            executor.awaitTermination(24, TimeUnit.HOURS); // Maximum time all processing can take.
        } catch (InterruptedException exception) {
            throw new SubmissionException("Error while parsing the submissions.", exception);
        }
    }

    /**
     * Parses a single submission (thread safe).
     */
    private void parseSingleSubmission(ProgressBar progressBar, Submission submission) throws LanguageException {
        boolean successful = submission.parse(options.debugParser(), options.normalize(), options.minimumTokenMatch(), options.analyzeComments());
        if (!successful) {
            errors.incrementAndGet();
            logger.debug("ERROR -> Submission {} removed with reason {}", submission.getName(), submission.getState());
        }
        progressBar.step();
    }

}
