package de.jplag.strategy;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jplag.GreedyStringTiling;
import de.jplag.JPlag;
import de.jplag.JPlagComparison;
import de.jplag.Submission;
import de.jplag.SubmissionSet;
import de.jplag.options.JPlagOptions;

public abstract class AbstractComparisonStrategy implements ComparisonStrategy {

    private static final Logger logger = LoggerFactory.getLogger(JPlag.class);

    private GreedyStringTiling greedyStringTiling;

    protected JPlagOptions options;

    public AbstractComparisonStrategy(JPlagOptions options, GreedyStringTiling greedyStringTiling) {
        this.greedyStringTiling = greedyStringTiling;
        this.options = options;
    }

    /**
     * Compare all submissions to the basecode.
     * <p>
     * Caller must ensure that the provided set does have a basecode submission before calling.
     * </p>
     * @param submissionSet Submissions and basecode to compare.
     */
    protected void compareSubmissionsToBaseCode(SubmissionSet submissionSet) {
        Submission baseCodeSubmission = submissionSet.getBaseCode();
        for (Submission currentSubmission : submissionSet.getSubmissions()) {
            JPlagComparison baseCodeComparison = greedyStringTiling.compareWithBaseCode(currentSubmission, baseCodeSubmission);
            currentSubmission.setBaseCodeComparison(baseCodeComparison);
            baseCodeSubmission.resetBaseCode();
        }
    }

    /**
     * Compares two submissions and optionally returns the results if similarity is high enough.
     */
    protected Optional<JPlagComparison> compareSubmissions(Submission first, Submission second, boolean withBaseCode) {
        JPlagComparison comparison = greedyStringTiling.compare(first, second);
        logger.info("Comparing " + first.getName() + "-" + second.getName() + ": " + comparison.similarity());

        if (options.getSimilarityMetric().isAboveThreshold(comparison, options.getSimilarityThreshold())) {
            return Optional.of(comparison);
        }
        return Optional.empty();
    }
}
