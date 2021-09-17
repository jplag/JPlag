package jplag.strategy;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Optional;

import jplag.GreedyStringTiling;
import jplag.JPlagComparison;
import jplag.Submission;
import jplag.options.JPlagOptions;

public abstract class AbstractComparisonStrategy implements ComparisonStrategy {

    // TODO PB: I think it's better to make each submission store its own matches with the base code.
    // Hashtable that maps the name of a submissions to its matches with the provided base code.
    private Hashtable<String, JPlagComparison> baseCodeMatches = new Hashtable<>(30);

    private GreedyStringTiling greedyStringTiling;

    protected JPlagOptions options;

    public AbstractComparisonStrategy(JPlagOptions options, GreedyStringTiling greedyStringTiling) {
        this.greedyStringTiling = greedyStringTiling;
        this.options = options;
    }

    protected void compareSubmissionsToBaseCode(ArrayList<Submission> submissions, Submission baseCodeSubmission) {
        for (Submission currentSubmission : submissions) {
            JPlagComparison baseCodeMatch = greedyStringTiling.compareWithBaseCode(currentSubmission, baseCodeSubmission);
            baseCodeMatches.put(currentSubmission.getName(), baseCodeMatch);
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
            comparison.setFirstBaseCodeMatches(baseCodeMatches.get(comparison.getFirstSubmission().getName()));
            comparison.setSecondBaseCodeMatches(baseCodeMatches.get(comparison.getSecondSubmission().getName()));
        }
        if (options.getSimilarityMetric().isAboveThreshold(comparison, options.getSimilarityThreshold())) {
            return Optional.of(comparison);
        }
        return Optional.empty();
    }

}
