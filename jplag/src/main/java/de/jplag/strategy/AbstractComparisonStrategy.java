package de.jplag.strategy;

import java.util.Optional;

import de.jplag.GreedyStringTiling;
import de.jplag.JPlagComparison;
import de.jplag.Submission;
import de.jplag.SubmissionSet;
import de.jplag.options.SimilarityMetric;

public abstract class AbstractComparisonStrategy implements ComparisonStrategy {

    private final GreedyStringTiling greedyStringTiling;

    private final SimilarityMetric similarityMetric;
    private final float similarityThreshold;

    public AbstractComparisonStrategy(GreedyStringTiling greedyStringTiling, SimilarityMetric similarityMetric, float similarityThreshold) {
        this.greedyStringTiling = greedyStringTiling;
        this.similarityMetric = similarityMetric;
        this.similarityThreshold = similarityThreshold;
    }

    /**
     * Compare all submissions to the basecode.
     * <p>Caller must ensure that the provided set does have a basecode submission before calling.</p>
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
        System.out.println("Comparing " + first.getName() + "-" + second.getName() + ": " + comparison.similarity());

        if (similarityMetric.isAboveThreshold(comparison, similarityThreshold)) {
            return Optional.of(comparison);
        }
        return Optional.empty();
    }
}
