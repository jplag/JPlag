package jplag.strategy;

import java.io.File;
import java.util.Vector;

import jplag.GreedyStringTiling;
import jplag.JPlagComparison;
import jplag.JPlagOptions;
import jplag.JPlagResult;
import jplag.SortedVector;
import jplag.Submission;

@SuppressWarnings("unused") // TODO TS: Special comparison strategy currently not supported
public class SpecialComparisonStrategy extends AbstractComparisonStrategy {

    public SpecialComparisonStrategy(JPlagOptions options, GreedyStringTiling gSTiling) {
        super(options, gSTiling);
    }

    /*
     * Now the special comparison TODO PB: Previously, this comparison created a `Report.java` (removed) TODO PB: Check
     * whether this comparison is now any different than the others after the report has been removed
     */
    @Override
    public JPlagResult compareSubmissions(Vector<Submission> submissions, Submission baseCodeSubmission) {
        File root = new File(options.getRootDirName());

        int size = submissions.size();
        int matchIndex = 0;

        // TODO TS: Special comparison strategy currently not supported
        // print("Comparing: ", countValidSubmissions() + " submissions");
        // print("\n(Writing results at the same time.)\n", null);

        int totalcomps = size * size;
        int i, j, anz = 0, count = 0;
        JPlagComparison match;
        Submission first, second;
        long msec = System.currentTimeMillis();

        for (i = 0; i < (size - 1); i++) {
            // Result vector
            SortedVector<JPlagComparison> matches = new SortedVector<>(new JPlagComparison.AvgComparator());

            first = submissions.elementAt(i);

            if (first.tokenList == null) {
                count += (size - 1);
                continue;
            }

            for (j = 0; j < size; j++) {
                second = submissions.elementAt(j);

                if ((i == j) || (second.tokenList == null)) {
                    count++;
                    continue;
                }

                match = this.gSTiling.compare(first, second);
                anz++;

                float percent = match.percent();

                if ((matches.size() < options.getNumberOfSubmissionsToCompareTo() || matches.size() == 0
                        || match.moreThan(matches.lastElement().percent())) && match.moreThan(0)) {
                    matches.insert(match);
                    if (matches.size() > options.getNumberOfSubmissionsToCompareTo()) {
                        matches.removeElementAt(options.getNumberOfSubmissionsToCompareTo());
                    }
                }

                // TODO TS: Special comparison strategy currently not supported
                // if (options.getClusterType() != ClusterType.NONE) {
                // similarity.setSimilarity(i, j, percent);
                // }

                count++;
            }
        }

        long time = System.currentTimeMillis() - msec;

        // TODO TS: Special comparison strategy currently not supported
        // print("\n", "Total time: " + ((time / 3600000 > 0) ? (time / 3600000) + " h " : "")
        // + ((time / 60000 > 0) ? ((time / 60000) % 60000) + " min " : "") + (time / 1000 % 60)
        // + " sec\n" + "Time per comparison: "
        // + (time / anz) + " msec\n");

        // TODO TS: Special comparison strategy currently not supported
        return new JPlagResult();
    }
}
