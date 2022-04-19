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
        long timeBeforeStartInMillis = System.currentTimeMillis();
        List<JPlagComparison> comparisons = new ArrayList<>();

        for (SubmissionTuple tuple : buildComparisonTuples(submissions)) {
            compareSubmissions(tuple.getLeft(), tuple.getRight(), withBaseCode).ifPresent(comparisons::add);
        }

        long durationInMillis = System.currentTimeMillis() - timeBeforeStartInMillis;
        return new JPlagResult(comparisons, submissionSet, durationInMillis, options);
    }

}
