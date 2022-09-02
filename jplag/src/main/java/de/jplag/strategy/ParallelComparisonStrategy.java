package de.jplag.strategy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jplag.*;
import de.jplag.options.JPlagOptions;

/**
 * Strategy for the parallel comparison of submissions. Uses all available cores.
 * @author Timur Saglam
 */
public class ParallelComparisonStrategy extends AbstractComparisonStrategy {
    private static final Logger logger = LoggerFactory.getLogger("JPlag");

    private static final int TIMEOUT_IN_SECONDS = 5;

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
        ExecutorService threadPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        // Parallel compare:
        List<Submission> submissions = submissionSet.getSubmissions();
        List<SubmissionTuple> tuples = buildComparisonTuples(submissions);
        Collections.shuffle(tuples); // Reduce how often cache collisions occur
        List<JPlagComparison> comparisons = Collections.synchronizedList(new ArrayList<>());
        for (SubmissionTuple tuple : tuples) {
            threadPool.execute(compareTuple(tuple, comparisons));
        }

        // Clean up and return result:
        shutdownThreadPool(threadPool);
        long durationInMillis = System.currentTimeMillis() - timeBeforeStartInMillis;
        return new JPlagResult(comparisons, submissionSet, durationInMillis, options);
    }

    /**
     * Creates a runnable which compares a submission tuple.
     * @param tuple contains the submissions to compare.
     * @param comparisons is the list to append the result to.
     * @return the runnable for parallel use.
     */
    private Runnable compareTuple(SubmissionTuple tuple, List<JPlagComparison> comparisons) {
        return new Runnable() {
            @Override
            public void run() {
                compareSubmissions(tuple.left(), tuple.right()).ifPresent(comparisons::add);
            }
        };
    }

    /**
     * Shuts down the thread pool and awaits termination
     */
    private void shutdownThreadPool(ExecutorService threadPool) {
        threadPool.shutdown();
        try {
            if (!threadPool.awaitTermination(TIMEOUT_IN_SECONDS, TimeUnit.SECONDS)) {
                throw new IllegalStateException("Parallel comparison calculation timed out!");
            }
        } catch (InterruptedException e) {
            logger.error(e.getMessage(), e);
            Thread.currentThread().interrupt();
        }
    }

}
