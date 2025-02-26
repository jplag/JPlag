package de.jplag.comparison;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jplag.JPlagComparison;
import de.jplag.JPlagResult;
import de.jplag.Submission;
import de.jplag.SubmissionSet;
import de.jplag.logging.ProgressBar;
import de.jplag.logging.ProgressBarLogger;
import de.jplag.logging.ProgressBarType;
import de.jplag.options.JPlagOptions;

/**
 * Implements a parallelized token-based longest common subsequence search for all pairs of programs in a given set of
 * programs.
 */
public class LongestCommonSubsquenceSearch {

    private final Logger logger = LoggerFactory.getLogger(LongestCommonSubsquenceSearch.class);

    private final JPlagOptions options;

    /**
     * Creates an instance of the subsequence search algorithm.
     * @param options specifies relevant parameters for the comparison.
     */
    public LongestCommonSubsquenceSearch(JPlagOptions options) {
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

        for (int i = 0; i < (submissions.size() - 1); i++) {
            Submission first = submissions.get(i);
            for (int j = (i + 1); j < submissions.size(); j++) {
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
     * @return the comparison results.
     */
    public JPlagResult compareSubmissions(SubmissionSet submissionSet) {
        long timeBeforeStartInMillis = System.currentTimeMillis();

        TokenValueMapper tokenValueMapper = new TokenValueMapper(submissionSet);
        GreedyStringTiling coreAlgorithm = new GreedyStringTiling(options, tokenValueMapper);

        boolean withBaseCode = submissionSet.hasBaseCode();
        if (withBaseCode) {
            compareSubmissionsToBaseCode(coreAlgorithm, submissionSet);
        }

        List<SubmissionTuple> tuples = buildComparisonTuples(submissionSet.getSubmissions());
        ProgressBar progressBar = ProgressBarLogger.createProgressBar(ProgressBarType.COMPARING, tuples.size());
        List<JPlagComparison> comparisons = tuples.stream().parallel().flatMap(tuple -> {
            Optional<JPlagComparison> result = compareSubmissions(coreAlgorithm, tuple.left(), tuple.right());
            progressBar.step();
            return result.stream();
        }).toList();
        progressBar.dispose();

        long durationInMillis = System.currentTimeMillis() - timeBeforeStartInMillis;

        return new JPlagResult(comparisons, submissionSet, durationInMillis, options);
    }
}
