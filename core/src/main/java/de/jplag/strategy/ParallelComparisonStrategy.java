package de.jplag.strategy;

import java.util.List;
import java.util.Optional;

import de.jplag.GreedyStringTiling;
import de.jplag.JPlagComparison;
import de.jplag.JPlagResult;
import de.jplag.SubmissionSet;
import de.jplag.logging.ProgressBar;
import de.jplag.logging.ProgressBarLogger;
import de.jplag.logging.ProgressBarType;
import de.jplag.options.JPlagOptions;

/**
 * Strategy for the parallel comparison of submissions. Uses all available cores.
 * @author Timur Saglam
 */
public class ParallelComparisonStrategy extends AbstractComparisonStrategy {
    public ParallelComparisonStrategy(JPlagOptions options, GreedyStringTiling greedyStringTiling) {
        super(options, greedyStringTiling);
    }

    @Override
    public JPlagResult compareSubmissions(SubmissionSet submissionSet) {
        // Initialize:
        long timeBeforeStartInMillis = System.currentTimeMillis();
        boolean withBaseCode = submissionSet.hasBaseCode();
        if (withBaseCode) {
            compareSubmissionsToBaseCode(submissionSet);
        }

        List<SubmissionTuple> tuples = buildComparisonTuples(submissionSet.getSubmissions());
        ProgressBar progressBar = ProgressBarLogger.createProgressBar(ProgressBarType.COMPARING, tuples.size());
        List<JPlagComparison> comparisons = tuples.stream().parallel().map(tuple -> {
            Optional<JPlagComparison> result = compareSubmissions(tuple.left(), tuple.right());
            progressBar.step();
            return result;
        }).flatMap(Optional::stream).toList();
        progressBar.dispose();

        long durationInMillis = System.currentTimeMillis() - timeBeforeStartInMillis;
        return new JPlagResult(comparisons, submissionSet, durationInMillis, options);
    }
}
