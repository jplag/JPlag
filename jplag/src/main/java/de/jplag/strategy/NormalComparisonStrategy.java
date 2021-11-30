package de.jplag.strategy;

import java.util.ArrayList;
import java.util.List;

import de.jplag.GreedyStringTiling;
import de.jplag.JPlagComparison;
import de.jplag.JPlagResult;
import de.jplag.Submission;
import de.jplag.SubmissionSet;
import de.jplag.options.JPlagOptions;

public class NormalComparisonStrategy extends AbstractComparisonStrategy {

    public NormalComparisonStrategy(JPlagOptions options, GreedyStringTiling greedyStringTiling) {
        super(options, greedyStringTiling);
    }

    @Override
    public JPlagResult compareSubmissions(SubmissionSet submissionSet) {
        boolean withBaseCode = submissionSet.hasBaseCode();
        if (withBaseCode) {
            compareSubmissionsToBaseCode(submissionSet);
        }

        List<Submission> submissions = submissionSet.getSubmissions();
        List<SubmissionTuple> tuples = TupleBuilder.buildComparisonTuples(submissions);
        List<JPlagComparison> comparisons = new ArrayList<>();

        long timeBeforeStartInMillis = System.currentTimeMillis();

        for (SubmissionTuple tuple : tuples) {
            compareSubmissions(tuple.getLeft(), tuple.getRight()).ifPresent(it -> comparisons.add(it));
        }

        long durationInMillis = System.currentTimeMillis() - timeBeforeStartInMillis;
        return new JPlagResult(comparisons, durationInMillis, numberOfSubmissions, options);
    }

}
