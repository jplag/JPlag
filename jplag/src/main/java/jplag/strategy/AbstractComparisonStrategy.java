package jplag.strategy;

import java.util.Hashtable;
import java.util.Vector;

import jplag.GreedyStringTiling;
import jplag.JPlagBaseCodeComparison;
import jplag.JPlagComparison;
import jplag.JPlagOptions;
import jplag.Submission;
import jplag.options.SimilarityMetric;

public abstract class AbstractComparisonStrategy implements ComparisonStrategy {

    // TODO PB: I think it's better to make each submission store its own matches with the base code.
    // Hashtable that maps the name of a submissions to its matches with the provided base code.
    protected Hashtable<String, JPlagBaseCodeComparison> baseCodeMatches = new Hashtable<>(30);

    protected GreedyStringTiling gSTiling;

    protected JPlagOptions options;

    public AbstractComparisonStrategy(JPlagOptions options, GreedyStringTiling gSTiling) {
        this.gSTiling = gSTiling;
        this.options = options;
    }

    protected void compareSubmissionsToBaseCode(Vector<Submission> submissions, Submission baseCodeSubmission) {
        int numberOfSubmissions = submissions.size();

        JPlagBaseCodeComparison bcMatch;
        Submission currentSubmission;

        for (int i = 0; i < (numberOfSubmissions); i++) {
            currentSubmission = submissions.elementAt(i);

            bcMatch = this.gSTiling.compareWithBaseCode(currentSubmission, baseCodeSubmission);
            baseCodeMatches.put(currentSubmission.name, bcMatch);

            this.gSTiling.resetBaseSubmission(baseCodeSubmission);
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
