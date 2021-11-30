package de.jplag.strategy;

import java.util.ArrayList;
import java.util.List;

import de.jplag.GreedyStringTiling;
import de.jplag.JPlagComparison;
import de.jplag.JPlagResult;
import de.jplag.SubmissionSet;
import de.jplag.options.JPlagOptions;

public class NormalComparisonStrategy extends AbstractComparisonStrategy {

    public NormalComparisonStrategy(JPlagOptions options, GreedyStringTiling greedyStringTiling) {
        super(options, greedyStringTiling);
    }

    @Override
    public JPlagResult compareSubmissions(List<SubmissionSet> submissionSets) {
        // Check submissions against their base code.
        for (SubmissionSet submissionSet: submissionSets) {
            if (submissionSet.hasBaseCode()) {
                compareSubmissionsToBaseCode(submissionSet);
            }
        }

        // Compare the submissions.
        List<SubmissionTuple> tuples = TupleBuilder.buildComparisonTuples(submissionSets);
        List<JPlagComparison> comparisons = new ArrayList<>();

        long timeBeforeStartInMillis = System.currentTimeMillis();

        for (SubmissionTuple tuple : tuples) {
            compareSubmissions(tuple.getLeft(), tuple.getRight()).ifPresent(it -> comparisons.add(it));
        }

        long durationInMillis = System.currentTimeMillis() - timeBeforeStartInMillis;
        return new JPlagResult(comparisons, durationInMillis, TupleBuilder.getNumberOfSubmissions(submissionSets), options);
    }

}
