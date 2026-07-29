package de.jplag.comparison;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jplag.JPlagComparison;
import de.jplag.JPlagResult;
import de.jplag.Submission;
import de.jplag.SubmissionSet;
import de.jplag.exceptions.ComparisonException;
import de.jplag.logging.ProgressBar;
import de.jplag.logging.ProgressBarLogger;
import de.jplag.logging.ProgressBarType;
import de.jplag.options.JPlagOptions;

/**
 * Implements a parallelized token-based longest common subsequence search for all pairs of programs in a given set of
 * programs.
 */
public class LongestCommonSubsequenceSearch {

    private final Logger logger = LoggerFactory.getLogger(LongestCommonSubsequenceSearch.class);

    private final JPlagOptions options;

    /**
     * Creates an instance of the subsequence search algorithm.
     * @param options specifies relevant parameters for the comparison.
     */
    public LongestCommonSubsequenceSearch(JPlagOptions options) {
        this.options = options;
    }

    /**
     * Compare all submissions to the basecode.
     * <p>
     * Caller must ensure that the provided set does have a basecode submission before calling.
     * </p>
     * @param comparisonAlgorithm is the algorithm implementation for the token-based longest common subsequence search.
     * @param submissionSet Submissions and basecode to compare.
     */
    private void compareSubmissionsToBaseCode(GreedyStringTiling comparisonAlgorithm, SubmissionSet submissionSet) {
        Submission baseCodeSubmission = submissionSet.getBaseCode();
        for (Submission currentSubmission : submissionSet.getSubmissions()) {
            JPlagComparison baseCodeComparison = comparisonAlgorithm.generateBaseCodeMarking(currentSubmission, baseCodeSubmission);
            currentSubmission.setBaseCodeComparison(baseCodeComparison);
        }
    }

    /**
     * Compares two submissions and optionally returns the results if similarity is high enough.
     * @param comparisonAlgorithm is the algorithm implementation for the token-based longest common subsequence search.
     * @param first is the first submission.
     * @param second is the second submission.
     * @return the comparison results, if the similarity is above the threshold specified via the options.
     */
    private Optional<JPlagComparison> compareSubmissions(GreedyStringTiling comparisonAlgorithm, Submission first, Submission second) {
        JPlagComparison comparison = comparisonAlgorithm.compare(first, second);
        logger.trace("Comparing {}-{}: {}", first.getName(), second.getName(), comparison.similarity());

        if (options.similarityMetric().isAboveThreshold(comparison, options.similarityThreshold())) {
            return Optional.of(comparison);
        }
        return Optional.empty();
    }

    /**
     * @return a list of all submission tuples to be processed.
     */
    private List<SubmissionTuple> buildComparisonTuples(List<Submission> submissions) {
        List<SubmissionTuple> tuples = new ArrayList<>();

        for (int i = 0; i < submissions.size() - 1; i++) {
            Submission first = submissions.get(i);
            for (int j = i + 1; j < submissions.size(); j++) {
                Submission second = submissions.get(j);
                if (first.isNew() || second.isNew()) {
                    tuples.add(new SubmissionTuple(first, second));
                }
            }
        }
        return tuples;
    }

    /**
     * Compares submissions from a set of submissions while considering a given base code.
     * @param submissionSet Collection of submissions with optional basecode to compare.
     * @return The comparison results.
     * @throws ComparisonException If a problem arises during comparison.
     */
    public JPlagResult compareSubmissions(SubmissionSet submissionSet) throws ComparisonException {
        long startTimeMillis = System.currentTimeMillis();

        // Set up data structures:
        TokenSequenceMapper tokenSequenceMapper = new TokenSequenceMapper(submissionSet);
        GreedyStringTiling coreAlgorithm = new GreedyStringTiling(options, tokenSequenceMapper);

        // Prepare base code comparisons:
        if (submissionSet.hasBaseCode()) {
            compareSubmissionsToBaseCode(coreAlgorithm, submissionSet);
        }

        // Compare all submission pairs in parallel:
        List<SubmissionTuple> tuples = buildComparisonTuples(submissionSet.getSubmissions());
        List<JPlagComparison> comparisons = new ArrayList<>();
        ProgressBar progressBar = ProgressBarLogger.createProgressBar(ProgressBarType.COMPARING, tuples.size());
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            List<Future<Optional<JPlagComparison>>> futures = tuples.stream().map(tuple -> executor.submit(() -> {
                Optional<JPlagComparison> result = compareSubmissions(coreAlgorithm, tuple.left(), tuple.right());
                progressBar.step();
                return result;
            })).toList();

            executor.shutdown();
            if (!executor.awaitTermination(24, TimeUnit.HOURS)) {
                throw new ComparisonException("Comparison timed out.");
            }

            for (Future<Optional<JPlagComparison>> future : futures) {
                future.get().ifPresent(comparisons::add);
            }
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            throw new ComparisonException("Error during comparison algorithm.", e);
        } finally {
            progressBar.dispose();
        }

        long durationInMilliseconds = System.currentTimeMillis() - startTimeMillis;
        return new JPlagResult(comparisons, submissionSet, durationInMilliseconds, options);
    }

}
