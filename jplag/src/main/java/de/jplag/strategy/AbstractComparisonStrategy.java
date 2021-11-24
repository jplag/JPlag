package de.jplag.strategy;

import java.util.List;
import java.util.Optional;

import de.jplag.GreedyStringTiling;
import de.jplag.JPlagComparison;
import de.jplag.Submission;
import de.jplag.options.JPlagOptions;

public abstract class AbstractComparisonStrategy implements ComparisonStrategy {

    private GreedyStringTiling greedyStringTiling;

    protected JPlagOptions options;

    public AbstractComparisonStrategy(JPlagOptions options, GreedyStringTiling greedyStringTiling) {
        this.greedyStringTiling = greedyStringTiling;
        this.options = options;
    }

    protected void compareSubmissionsToBaseCode(List<Submission> submissions, Submission baseCodeSubmission) {
        for (Submission currentSubmission : submissions) {
            JPlagComparison baseCodeMatch = greedyStringTiling.compareWithBaseCode(currentSubmission, baseCodeSubmission);
            currentSubmission.setBaseCodeMatch(baseCodeMatch);
            baseCodeSubmission.resetBaseCode();
        }
    }

    /**
     * Compares two submissions and optionally returns the results if similarity is high enough.
     */
    protected Optional<JPlagComparison> compareSubmissions(Submission first, Submission second, boolean withBaseCode) {
        JPlagComparison comparison = greedyStringTiling.compare(first, second);
        System.out.println("Comparing " + first.getName() + "-" + second.getName() + ": " + comparison.similarity());
        if (withBaseCode) {
            comparison.setFirstBaseCodeMatches(comparison.getFirstSubmission().getBaseCodeMatch());
            comparison.setSecondBaseCodeMatches(comparison.getSecondSubmission().getBaseCodeMatch());
        }
        if (options.getSimilarityMetric().isAboveThreshold(comparison, options.getSimilarityThreshold())) {
            return Optional.of(comparison);
        }
        return Optional.empty();
    }

}
