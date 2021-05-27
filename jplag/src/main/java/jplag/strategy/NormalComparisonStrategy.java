package jplag.strategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import jplag.GreedyStringTiling;
import jplag.JPlagComparison;
import jplag.JPlagOptions;
import jplag.JPlagResult;
import jplag.Submission;

public class NormalComparisonStrategy extends AbstractComparisonStrategy {

    public NormalComparisonStrategy(JPlagOptions options, GreedyStringTiling gSTiling) {
        super(options, gSTiling);
    }

    @Override
    public JPlagResult compareSubmissions(Vector<Submission> submissions, Submission baseCodeSubmission) {
        if (baseCodeSubmission != null) {
            compareSubmissionsToBaseCode(submissions, baseCodeSubmission);
        }

        long timeBeforeStartInMillis = System.currentTimeMillis();
        int i, j, numberOfSubmissions = submissions.size();
        Submission first, second;
        JPlagComparison comparison;

        List<JPlagComparison> comparisons = new ArrayList<>();

        for (i = 0; i < (numberOfSubmissions - 1); i++) {
            first = submissions.elementAt(i);

            if (first.tokenList == null) {
                continue;
            }

            for (j = (i + 1); j < numberOfSubmissions; j++) {
                second = submissions.elementAt(j);

                if (second.tokenList == null) {
                    continue;
                }

                comparison = this.gSTiling.compare(first, second);

                System.out.println("Comparing " + first.name + "-" + second.name + ": " + comparison.percent());

                if (baseCodeSubmission != null) {
                    comparison.bcMatchesA = baseCodeMatches.get(comparison.firstSubmission.name);
                    comparison.bcMatchesB = baseCodeMatches.get(comparison.secondSubmission.name);
                }

                if (isAboveSimilarityThreshold(comparison)) {
                    comparisons.add(comparison);
                }
            }
        }

        long durationInMillis = System.currentTimeMillis() - timeBeforeStartInMillis;

        // TODO TS: Cluster currently not supported
        // Cluster cluster = null;
        //
        // if (options.getClusterType() != ClusterType.NONE) {
        // cluster = this.clusters.calculateClustering(submissions);
        // }

        return new JPlagResult(comparisons, durationInMillis, numberOfSubmissions, options);
    }

}
