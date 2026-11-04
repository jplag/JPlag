package de.jplag;

import static de.jplag.SubmissionState.VALID;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import de.jplag.options.ParsingOptions;
import de.jplag.options.SubmissionFileOptions;
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
    private List<Submission> invalidSubmissions;
    private List<Submission> submissions;

    /**
     * Base code submission if it exists.
     */
    private final Submission baseCodeSubmission;

    private final SubmissionFileOptions submissionFileOptions;
    private final AtomicInteger errors = new AtomicInteger(0);
    private final AtomicLong tokenizationDuration = new AtomicLong(0);

    /**
     * Creates a submissions set and parses all submissions.
     * @param submissions list of submissions to check for plagiarism.
     * @param baseCode Base code submission if it exists or {@code null}.
     * @param options The JPlag options
     * @throws ExitException if the submissions cannot be parsed.
     */
    public SubmissionSet(List<Submission> submissions, Submission baseCode, SubmissionFileOptions submissionFileOptions) throws ExitException {
        this.allSubmissions = submissions;
        this.baseCodeSubmission = baseCode;
        this.submissionFileOptions = submissionFileOptions;
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

    public void parse(ParsingOptions parsingOptions) throws ExitException {
        parseSubmissions(allSubmissions, parsingOptions);
        if (baseCodeSubmission != null) {
            parseBaseCodeSubmission(baseCodeSubmission, parsingOptions);
        }
        this.submissions = filterValidSubmissions();
        invalidSubmissions = filterInvalidSubmissions();
    }

    /**
     * Normalizes the token sequences of all submissions (including basecode). This makes the token sequence invariant to
     * dead code insertion and independent statement reordering by removing dead tokens and optionally reordering tokens to
     * a deterministic order.
     */
    public void normalizeSubmissions() {
        long startTimeMillis = System.currentTimeMillis();
        if (baseCodeSubmission != null) {
            baseCodeSubmission.normalize();
        }
        ProgressBar progressBar = ProgressBarLogger.createProgressBar(ProgressBarType.TOKEN_SEQUENCE_NORMALIZATION, submissions.size());
        submissions.parallelStream().forEach(submission -> {
            submission.normalize();
            progressBar.step();
        });
        progressBar.dispose();
        long durationInMilliseconds = System.currentTimeMillis() - startTimeMillis;
        tokenizationDuration.addAndGet(durationInMilliseconds);
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
    private void parseBaseCodeSubmission(Submission baseCode, ParsingOptions parsingOptions) throws BasecodeException, LanguageException {
        logger.trace("----- Parsing basecode submission: {}", baseCode.getName());
        long startTimeMillis = System.currentTimeMillis();
        boolean successful = baseCode.parse(parsingOptions);
        long durationInMilliseconds = System.currentTimeMillis() - startTimeMillis;
        tokenizationDuration.addAndGet(durationInMilliseconds);
        if (!successful) {
            if (baseCode.getState() == SubmissionState.TOO_SMALL) {
                throw new BasecodeException("Basecode contains %d token(s), which is below the minimum match length (%d)!"
                        .formatted(baseCode.getNumberOfTokens(), parsingOptions.minimumTokenMatch()));
            }
            throw new BasecodeException("Error while parsing the basecode submission!");
        }
    }

    /**
     * Parse all given submissions.
     * @param submissions The list of submissions
     */
    private void parseSubmissions(List<Submission> submissions, ParsingOptions parsingOptions) throws ExitException {
        if (submissions.isEmpty()) {
            logger.error("No submissions to parse!");
            return;
        }

        ProgressBar progressBar = ProgressBarLogger.createProgressBar(ProgressBarType.PARSING, submissions.size());

        parseSubmissionsInParallel(submissions, progressBar, parsingOptions);

        progressBar.dispose();

        int validSubmissions = submissions.size() - errors.get();
        logger.debug("{} submissions parsed successfully!", validSubmissions);
        logger.debug("{} parser error{}!", errors, errors.get() != 1 ? "s" : "");
    }

    private void parseSubmissionsInParallel(List<Submission> submissions, ProgressBar progressBar, ParsingOptions parsingOptions) throws SubmissionException {
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            long startTimeMillis = System.currentTimeMillis();
            for (Submission submission : submissions) {
                executor.submit(() -> {
                    parseSingleSubmission(progressBar, submission, parsingOptions);
                    return null; // Ensure the lambda is a Callable for exception handling
                });
            }
            executor.shutdown();
            executor.awaitTermination(24, TimeUnit.HOURS); // Maximum time all processing can take.
            long durationInMilliseconds = System.currentTimeMillis() - startTimeMillis;
            tokenizationDuration.addAndGet(durationInMilliseconds);
        } catch (InterruptedException exception) {
            throw new SubmissionException("Error while parsing the submissions.", exception);
        }
    }

    /**
     * Parses a single submission (thread safe).
     */
    private void parseSingleSubmission(ProgressBar progressBar, Submission submission, ParsingOptions parsingOptions) throws LanguageException {
        boolean successful = submission.parse(parsingOptions);
        if (!successful) {
            errors.incrementAndGet();
            logger.debug("ERROR -> Submission {} removed with reason {}", submission.getName(), submission.getState());
        }
        progressBar.step();
    }

    /**
     * Returns the total time taken during the parsing/tokenization stage.
     * @return the tokenization time in milliseconds
     */
    public long getTokenizationDuration() {
        return tokenizationDuration.get();
    }
}
