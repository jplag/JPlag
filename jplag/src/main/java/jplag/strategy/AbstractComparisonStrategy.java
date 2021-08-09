package jplag.strategy;

import java.util.Hashtable;
import java.util.Vector;

import jplag.GreedyStringTiling;
import jplag.JPlagComparison;
import jplag.Submission;
import jplag.options.JPlagOptions;
import jplag.options.SimilarityMetric;

public abstract class AbstractComparisonStrategy implements ComparisonStrategy {

    // TODO PB: I think it's better to make each submission store its own matches with the base code.
    // Hashtable that maps the name of a submissions to its matches with the provided base code.
    protected Hashtable<String, JPlagComparison> baseCodeMatches = new Hashtable<>(30);

    protected GreedyStringTiling greedyStringTiling;

    protected JPlagOptions options;

    public AbstractComparisonStrategy(JPlagOptions options, GreedyStringTiling greedyStringTiling) {
        this.greedyStringTiling = greedyStringTiling;
        this.options = options;
    }

    protected void compareSubmissionsToBaseCode(Vector<Submission> submissions, Submission baseCodeSubmission) {
        int numberOfSubmissions = submissions.size();

        JPlagComparison baseCodeMatch;
        Submission currentSubmission;

        for (int i = 0; i < (numberOfSubmissions); i++) {
            currentSubmission = submissions.elementAt(i);

            baseCodeMatch = greedyStringTiling.compareWithBaseCode(currentSubmission, baseCodeSubmission);
            baseCodeMatches.put(currentSubmission.name, baseCodeMatch);

            greedyStringTiling.resetBaseSubmission(baseCodeSubmission);
        }
    }

    protected boolean isAboveSimilarityThreshold(JPlagComparison comparison) {
        float similarityThreshold = this.options.getSimilarityThreshold();
        SimilarityMetric similarityMetric = this.options.getSimilarityMetric();

        switch (similarityMetric) {
        case AVG:
            return comparison.percent() >= similarityThreshold;
        case MAX:
            return comparison.percentMaxAB() >= similarityThreshold;
        case MIN:
            return comparison.percentMinAB() >= similarityThreshold;
        default:
            return true;
        }
    }

}
