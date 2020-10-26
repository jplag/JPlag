package jplag.strategy;

import java.io.File;
import java.util.Vector;
import jplag.AllMatches;
import jplag.GSTiling;
import jplag.JPlagOptions;
import jplag.JPlagResult;
import jplag.SortedVector;
import jplag.Submission;

public class SpecialComparisonStrategy extends AbstractComparisonStrategy {

  public SpecialComparisonStrategy(JPlagOptions options, GSTiling gSTiling) {
    super(options, gSTiling);
  }

  /*
   * Now the special comparison
   * TODO: Previously, this comparison created a `Report.java` (removed)
   * TODO: Check whether this comparison is now any different than the others after the report has been removed
   */
  @Override
  public JPlagResult compareSubmissions(
      Vector<Submission> submissions,
      Submission baseCodeSubmission
  ) {
    File root = new File(options.getRootDirName());

    int size = submissions.size();
    int matchIndex = 0;

    // TODO
//    print("Comparing: ", countValidSubmissions() + " submissions");
//    print("\n(Writing results at the same time.)\n", null);

    int totalcomps = size * size;
    int i, j, anz = 0, count = 0;
    AllMatches match;
    Submission s1, s2;
    long msec = System.currentTimeMillis();

    for (i = 0; i < (size - 1); i++) {
      // Result vector
      SortedVector<AllMatches> matches = new SortedVector<>(
          new AllMatches.AvgComparator());

      s1 = submissions.elementAt(i);

      if (s1.tokenList == null) {
        count += (size - 1);
        continue;
      }

      for (j = 0; j < size; j++) {
        s2 = submissions.elementAt(j);

        if ((i == j) || (s2.tokenList == null)) {
          count++;
          continue;
        }

        match = this.gSTiling.compare(s1, s2);
        anz++;

        float percent = match.percent();

        if ((matches.size() < options.getNumberOfSubmissionsToCompareTo() || matches.size() == 0 || match
            .moreThan(matches.lastElement().percent()))
            && match.moreThan(0)) {
          matches.insert(match);
          if (matches.size() > options.getNumberOfSubmissionsToCompareTo()) {
            matches.removeElementAt(options.getNumberOfSubmissionsToCompareTo());
          }
        }

        // TODO
//        if (options.getClusterType() != ClusterType.NONE) {
//          similarity.setSimilarity(i, j, percent);
//        }

        count++;
      }
    }

    long time = System.currentTimeMillis() - msec;

    // TODO
//    print("\n", "Total time: " + ((time / 3600000 > 0) ? (time / 3600000) + " h " : "")
//        + ((time / 60000 > 0) ? ((time / 60000) % 60000) + " min " : "") + (time / 1000 % 60)
//        + " sec\n" + "Time per comparison: "
//        + (time / anz) + " msec\n");

    // TODO
    return new JPlagResult();
  }
}
