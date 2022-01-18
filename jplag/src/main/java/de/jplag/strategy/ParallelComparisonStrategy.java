package de.jplag.strategy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import de.jplag.GreedyStringTiling;
import de.jplag.JPlagComparison;
import de.jplag.JPlagResult;
import de.jplag.Submission;
import de.jplag.SubmissionSet;
import de.jplag.options.JPlagOptions;

/**
 * Strategy for the parallel comparison of submissions. Uses all available cores and compares in a non-blocking manner.
 * @author Timur Saglam
 */
public class ParallelComparisonStrategy extends AbstractComparisonStrategy {
    private static final int TIMEOUT_IN_SECONDS = 5;
    private final ConcurrentMap<String, Lock> submissionLocks;
    private ExecutorService threadPool;
    private final List<JPlagComparison> comparisons;
    private int successfulComparisons;

    public ParallelComparisonStrategy(JPlagOptions options, GreedyStringTiling greedyStringTiling) {
        super(options, greedyStringTiling);
        submissionLocks = new ConcurrentHashMap<>();
        comparisons = Collections.synchronizedList(new ArrayList<>());
    }

    @Override
    public JPlagResult compareSubmissions(SubmissionSet submissionSet) {
        // Initialize:
        long timeBeforeStartInMillis = System.currentTimeMillis();
        boolean withBaseCode = submissionSet.hasBaseCode();
        if (withBaseCode) {
            compareSubmissionsToBaseCode(submissionSet);
        }
        threadPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        comparisons.clear();
        submissionLocks.clear();
        successfulComparisons = 0;

        // Parallel compare:
        List<Submission> submissions = submissionSet.getSubmissions();
        List<SubmissionTuple> tuples = buildComparisonTuples(submissions);
        Collections.shuffle(tuples); // Reduces how often submission pairs must be re-submitted
        for (SubmissionTuple tuple : tuples) {
            threadPool.execute(compareTuple(tuple, withBaseCode));
        }

        // Ensure termination:
        while (successfulComparisons < tuples.size()) {
            try {
                Thread.sleep(5);
            } catch (InterruptedException exception) {
                exception.printStackTrace();
            }
        }

        // Clean up and return result:
        shutdownThreadPool();
        long durationInMillis = System.currentTimeMillis() - timeBeforeStartInMillis;
        return new JPlagResult(comparisons, submissionSet, durationInMillis, options);
    }

    /**
     * @return a list of all submission tuples to be processed.
     */
    private List<SubmissionTuple> buildComparisonTuples(List<Submission> submissions) {
        List<SubmissionTuple> tuples = new ArrayList<>();
        for (int i = 0; i < (submissions.size() - 1); i++) {
            Submission first = submissions.get(i);
            if (first.getTokenList() != null) {
                for (int j = (i + 1); j < submissions.size(); j++) {
                    Submission second = submissions.get(j);
                    if (second.getTokenList() != null) {
                        tuples.add(new SubmissionTuple(first, second));
                    }
                }
            }
        }
        return tuples;
    }

    /**
     * Creates a runnable which compares a submission tuple. If the submissions are locked, the runnable is re-submitted.
     * @param tuple contains the submissions to compare.
     * @param withBaseCode specifies if base code is used.
     * @return the runnable for parallel use.
     */
    private Runnable compareTuple(SubmissionTuple tuple, boolean withBaseCode) {
        return new Runnable() {
            @Override
            public void run() {
                Lock leftLock = getOrCreateLock(tuple.getLeft().getName());
                Lock rightLock = getOrCreateLock(tuple.getRight().getName());
                boolean hasLeft = leftLock.tryLock();
                boolean hasRight = hasLeft && rightLock.tryLock();
                try {
                    if (hasLeft && hasRight) { // both locks acquired!
                        compareSubmissions(tuple.getLeft(), tuple.getRight(), withBaseCode).ifPresent(it -> comparisons.add(it));
                        synchronized (this) {
                            successfulComparisons++;
                        }
                    } else {
                        threadPool.execute(this); // re-submit runnable, as at least one submission is locked.
                    }
                } finally {
                    if (hasRight) {
                        rightLock.unlock();
                    }
                    if (hasLeft) {
                        leftLock.unlock();
                    }
                }
            }
        };
    }

    /**
     * @return a lock for a given key. If it does not exist, it is created in a thread-safe manner.
     */
    private Lock getOrCreateLock(String key) {
        submissionLocks.putIfAbsent(key, new ReentrantLock()); // atomic operation
        return submissionLocks.get(key);
    }

    /**
     * Shuts down the thread pool and awaits termination
     */
    private void shutdownThreadPool() {
        threadPool.shutdown();
        try {
            if (!threadPool.awaitTermination(TIMEOUT_IN_SECONDS, TimeUnit.SECONDS)) {
                throw new IllegalStateException("Parallel comparison calculation timed out!");
            }
        } catch (InterruptedException exception) {
            throw new IllegalStateException("Thread pool interrupted during comparison: " + exception.getMessage());
        }
    }

}
