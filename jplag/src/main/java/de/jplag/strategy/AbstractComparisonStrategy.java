package de.jplag.strategy;

import java.util.Hashtable;
import java.util.Optional;

import de.jplag.GreedyStringTiling;
import de.jplag.JPlagComparison;
import de.jplag.Submission;
import de.jplag.SubmissionSet;
import de.jplag.options.JPlagOptions;

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

    /**
     * Compare all submissions to the basecode.
     * <p>Caller must ensure that the provided set does have a basecode submission before calling.</p>
     * @param submissionSet Submissions and basecode to compare.
     */
    protected void compareSubmissionsToBaseCode(SubmissionSet submissionSet) {
        Submission baseCodeSubmission = submissionSet.getBaseCode();
        for (Submission currentSubmission : submissionSet.getSubmissions()) {
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
