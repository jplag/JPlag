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
        if (baseCodeSubmission != null) {
            compareSubmissionsToBaseCode(submissions, baseCodeSubmission);
        }
        
        long timeBeforeStartInMillis = System.currentTimeMillis();
        int i, j, numberOfSubmissions = submissions.size();
        Submission first, second;
        List<JPlagComparison> comparisons = new ArrayList<>();
        JPlagComparison comparison;
        
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
                comparison = greedyStringTiling.compare(first, second);

                // TODO SH: Why does this differ from the results shown in the result web page?
                System.out.println("Comparing " + first.name + "-" + second.name + ": " + comparison.percent());
                if (baseCodeSubmission != null) {
                    comparison.baseCodeMatchesA = baseCodeMatches.get(comparison.firstSubmission.name);
                    comparison.baseCodeMatchesB = baseCodeMatches.get(comparison.secondSubmission.name);
                }
                if (isAboveSimilarityThreshold(comparison)) {
                    comparisons.add(comparison);
                }
            }
        }
        long durationInMillis = System.currentTimeMillis() - timeBeforeStartInMillis;
        return new JPlagResult(comparisons, durationInMillis, numberOfSubmissions, options);
    }

}
