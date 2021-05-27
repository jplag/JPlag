package jplag.strategy;

import java.io.File;
import java.util.Vector;

import jplag.GreedyStringTiling;
import jplag.JPlagComparison;
import jplag.JPlagOptions;
import jplag.JPlagResult;
import jplag.SortedVector;
import jplag.Structure;
import jplag.Submission;
import jplag.clustering.Cluster;

@SuppressWarnings("unused") // TODO TS: External comparison strategy currently not supported
public class ExternalComparisonStrategy extends AbstractComparisonStrategy {

    private final Runtime runtime = Runtime.getRuntime();

    public ExternalComparisonStrategy(JPlagOptions options, GreedyStringTiling gSTiling) {
        super(options, gSTiling);
    }

    /**
     * This is the special external comparison routine
     */
    @Override
    public JPlagResult compareSubmissions(Vector<Submission> submissions, Submission baseCodeSubmission) {
        try {
            int size = submissions.size();

            // Result vector
            SortedVector<JPlagComparison> avgmatches = new SortedVector<>(new JPlagComparison.AvgComparator());
            SortedVector<JPlagComparison> maxmatches = new SortedVector<>(new JPlagComparison.MaxComparator());
            int[] dist = new int[10];

            // print("Comparing: " + size + " submissions\n", null);

            long totalComparisons = (size * (size - 1)) / 2, count = 0, comparisons = 0;
            int index;
            JPlagComparison match;
            Submission first, second;
            long remain;
            String totalTimeStr, remainTime;

            // print("Checking memory size...\n", null);

            // First try to load as many submissions as possible
            index = fillMemory(submissions, 0, size);

            long startTime;
            long totalTime = 0;
            int startA = 0;
            int endA = index / 2;
            int startB = endA + 1;
            int endB = index;
            int i, j;
            // long progStart;

            do {
                // compare A to A
                startTime = System.currentTimeMillis();
                // print("Comparing block A (" + startA + "-" + endA + ") to block A\n", null);

                for (i = startA; i <= endA; i++) {
                    first = submissions.elementAt(i);

                    if (first.tokenList == null) {
                        count += (endA - i);
                        continue;
                    }

                    for (j = (i + 1); j <= endA; j++) {
                        second = submissions.elementAt(j);

                        if (second.tokenList == null) {
                            count++;
                            continue;
                        }

                        match = this.gSTiling.compare(first, second);
                        registerMatch(match, avgmatches, maxmatches, null, i, j);
                        comparisons++;
                        count++;
                    }
                }

                // print("\n", null);
                totalTime += System.currentTimeMillis() - startTime;

                // Are we finished?
                if (startA == startB) {
                    break;
                }

                do {
                    totalTimeStr = "" + ((totalTime / 3600000 > 0) ? (totalTime / 3600000) + " h " : "")
                            + ((totalTime / 60000 > 0) ? ((totalTime / 60000) % 60) + " min " : "") + (totalTime / 1000 % 60) + " sec";

                    if (comparisons != 0) {
                        remain = totalTime * (totalComparisons - count) / comparisons;
                    } else {
                        remain = 0;
                    }

                    remainTime = "" + ((remain / 3600000 > 0) ? (remain / 3600000) + " h " : "")
                            + ((remain / 60000 > 0) ? ((remain / 60000) % 60) + " min " : "") + (remain / 1000 % 60) + " sec";

                    // print("Progress: " + (100 * count) / totalComparisons + "%\nTime used for comparisons: "
                    // + totalTimeStr
                    // + "\nRemaining time (estimate): " + remainTime + "\n", null);

                    // compare A to B
                    startTime = System.currentTimeMillis();
                    // print("Comparing block A (" + startA + "-" + endA + ") to block B (" + startB + "-" + endB
                    // + ")\n", null);

                    for (i = startB; i <= endB; i++) {
                        first = submissions.elementAt(i);

                        if (first.tokenList == null) {
                            count += (endA - startA + 1);
                            continue;
                        }

                        for (j = startA; j <= endA; j++) {
                            second = submissions.elementAt(j);

                            if (second.tokenList == null) {
                                count++;
                                continue;
                            }

                            match = this.gSTiling.compare(first, second);
                            registerMatch(match, avgmatches, maxmatches, null, i, j);
                            comparisons++;
                            count++;
                        }

                        first.tokenList = null; // remove B
                    }

                    // print("\n", null);
                    totalTime += System.currentTimeMillis() - startTime;

                    if (endB == size - 1) {
                        totalTimeStr = "" + ((totalTime / 3600000 > 0) ? (totalTime / 3600000) + " h " : "")
                                + ((totalTime / 60000 > 0) ? ((totalTime / 60000) % 60) + " min " : "") + (totalTime / 1000 % 60) + " sec";
                        remain = totalTime * (totalComparisons - count) / comparisons;
                        remainTime = "" + ((remain / 3600000 > 0) ? (remain / 3600000) + " h " : "")
                                + ((remain / 60000 > 0) ? ((remain / 60000) % 60) + " min " : "") + (remain / 1000 % 60) + " sec";

                        // print("Progress: " + (100 * count) / totalComparisons + "%\nTime used for comparisons: "
                        // + totalTimeStr
                        // + "\nRemaining time (estimate): " + remainTime + "\n", null);
                        break;
                    }

                    runtime.runFinalization();
                    runtime.gc();
                    Thread.yield();

                    // Try to find the next B
                    // print("Finding next B\n", null);

                    index = fillMemory(submissions, endB + 1, size);

                    startB = endB + 1;
                    endB = index;

                } while (true);

                // Remove A
                for (i = startA; i <= endA; i++) {
                    submissions.elementAt(i).tokenList = null;
                }

                runtime.runFinalization();
                runtime.gc();
                Thread.yield();

                // print("Find next A.\n", null);

                // First try to load as many submissions as possible

                index = fillMemory(submissions, endA + 1, size);

                if (index != size - 1) {
                    startA = endA + 1;
                    endA = startA + (index - startA + 1) / 2;
                    startB = endA + 1;
                    endB = index;
                } else {
                    startA = startB; // last block
                    endA = endB = index;
                }
            } while (true);

            totalTime += System.currentTimeMillis() - startTime;
            totalTimeStr = "" + ((totalTime / 3600000 > 0) ? (totalTime / 3600000) + " h " : "")
                    + ((totalTime / 60000 > 0) ? ((totalTime / 60000) % 60000) + " min " : "") + (totalTime / 1000 % 60) + " sec";

            // print("Total comparison time: " + totalTimeStr + "\nComparisons: " + count + "/" + comparisons
            // + "/" + totalComparisons + "\n",
            // null);

            // free remaining memory
            for (i = startA; i <= endA; i++) {
                submissions.elementAt(i).tokenList = null;
            }

            runtime.runFinalization();
            runtime.gc();
            Thread.yield();

            Cluster cluster = null;

            // TODO TS: Cluster currently not supported
            // if (options.getClusterType() == ClusterType.NONE) {
            // cluster = this.clusters.calculateClustering(submissions);
            // }

            // TODO TS: External comparison strategy currently not supported
            // Deprecated:
            // writeResults(dist, avgmatches, maxmatches, null, cluster);

            // TODO TS: External comparison strategy currently not supported
            return new JPlagResult();
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            return new JPlagResult();
        }
    }

    private int fillMemory(Vector<Submission> submissions, int from, int size) {
        Submission sub = null;
        int index = from;

        runtime.runFinalization();
        runtime.gc();
        Thread.yield();
        long freeBefore = runtime.freeMemory();
        try {
            for (; index < size; index++) {
                sub = submissions.elementAt(index);
                sub.tokenList = new Structure();
                if (!sub.tokenList.load(new File("temp", sub.submissionFile.getName() + sub.name))) {
                    sub.tokenList = null;
                }
            }
        } catch (java.lang.OutOfMemoryError e) {
            sub.tokenList = null;
            // print("Memory overflow after loading " + (index - from + 1) + " submissions.\n", null);
        }
        if (index >= size) {
            index = size - 1;
        }

        if (freeBefore / runtime.freeMemory() <= 2) {
            return index;
        }
        for (int i = (index - from) / 2; i > 0; i--) {
            submissions.elementAt(index--).tokenList = null;
        }
        runtime.runFinalization();
        runtime.gc();
        Thread.yield();

        // make sure we freed half of the "available" memory.
        long free;
        while (freeBefore / (free = runtime.freeMemory()) > 2) {
            submissions.elementAt(index--).tokenList = null;
            runtime.runFinalization();
            runtime.gc();
            Thread.yield();
        }

        // print(free / 1024 / 1024 + "MByte freed. Current index: " + index + "\n", null);

        return index;
    }

    private void registerMatch(JPlagComparison match, SortedVector<JPlagComparison> avgMatches, SortedVector<JPlagComparison> maxMatches,
            SortedVector<JPlagComparison> minMatches, int a, int b) {
        float avgPercent = match.percent();
        float maxPercent = match.percentMaxAB();
        float minPercent = match.percentMinAB();

        if (!options.isStorePercent()) {
            if ((avgMatches.size() < options.getStoreMatches() || avgPercent > avgMatches.lastElement().percent()) && avgPercent > 0) {
                avgMatches.insert(match);

                if (avgMatches.size() > options.getStoreMatches()) {
                    avgMatches.removeElementAt(options.getStoreMatches());
                }
            }

            if (maxMatches != null && (maxMatches.size() < options.getStoreMatches() || maxPercent > maxMatches.lastElement().percent())
                    && maxPercent > 0) {
                maxMatches.insert(match);

                if (maxMatches.size() > options.getStoreMatches()) {
                    maxMatches.removeElementAt(options.getStoreMatches());
                }
            }

            if (minMatches != null && (minMatches.size() < options.getStoreMatches() || minPercent > minMatches.lastElement().percent())
                    && minPercent > 0) {
                minMatches.insert(match);

                if (minMatches.size() > options.getStoreMatches()) {
                    minMatches.removeElementAt(options.getStoreMatches());
                }
            }
        } else { // store_percent
            if (avgPercent > options.getStoreMatches()) {
                avgMatches.insert(match);

                if (avgMatches.size() > JPlagOptions.MAX_RESULT_PAIRS) {
                    avgMatches.removeElementAt(JPlagOptions.MAX_RESULT_PAIRS);
                }
            }

            if (maxMatches != null && maxPercent > options.getStoreMatches()) {
                maxMatches.insert(match);

                if (maxMatches.size() > JPlagOptions.MAX_RESULT_PAIRS) {
                    maxMatches.removeElementAt(JPlagOptions.MAX_RESULT_PAIRS);
                }
            }

            if (minMatches != null && minPercent > options.getStoreMatches()) {
                minMatches.insert(match);

                if (minMatches.size() > JPlagOptions.MAX_RESULT_PAIRS) {
                    minMatches.removeElementAt(JPlagOptions.MAX_RESULT_PAIRS);
                }
            }
        }

        // TODO TS: Cluster currently not supported
        // if (options.getClusterType() != ClusterType.NONE) {
        // similarity.setSimilarity(a, b, avgPercent);
        // }
    }
}
