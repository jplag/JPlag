package jplag.strategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import jplag.GreedyStringTiling;
import jplag.JPlagComparison;
import jplag.JPlagResult;
import jplag.Submission;
import jplag.options.JPlagOptions;

public class NormalComparisonStrategy extends AbstractComparisonStrategy {

    public NormalComparisonStrategy(JPlagOptions options, GreedyStringTiling greedyStringTiling) {
        super(options, greedyStringTiling);
    }

    @Override
    public JPlagResult compareSubmissions(Vector<Submission> submissions, Submission baseCodeSubmission) {
        boolean withBaseCode = baseCodeSubmission != null;
        if (withBaseCode) {
            compareSubmissionsToBaseCode(submissions, baseCodeSubmission);
        }

        long timeBeforeStartInMillis = System.currentTimeMillis();
        int i, j, numberOfSubmissions = submissions.size();
        Submission first, second;
        List<JPlagComparison> comparisons = new ArrayList<>();

        for (i = 0; i < (numberOfSubmissions - 1); i++) {
            first = submissions.elementAt(i);
            if (first.getTokenList() == null) {
                continue;
            }
            for (j = (i + 1); j < numberOfSubmissions; j++) {
                second = submissions.elementAt(j);
                if (second.getTokenList() == null) {
                    continue;
                }
                compareSubmissions(first, second, withBaseCode).ifPresent(it -> comparisons.add(it));
            }
        }

        long durationInMillis = System.currentTimeMillis() - timeBeforeStartInMillis;
        return new JPlagResult(comparisons, durationInMillis, numberOfSubmissions, options);
    }

}
