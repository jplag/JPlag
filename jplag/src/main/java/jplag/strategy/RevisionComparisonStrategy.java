package jplag.strategy;

import java.util.Vector;
import jplag.AllMatches;
import jplag.GSTiling;
import jplag.JPlagOptions;
import jplag.JPlagResult;
import jplag.SortedVector;
import jplag.Submission;
import jplag.clustering.Cluster;

public class RevisionComparisonStrategy extends AbstractComparisonStrategy {

  public RevisionComparisonStrategy(JPlagOptions options, GSTiling gSTiling) {
    super(options, gSTiling);
  }

  @Override
  public JPlagResult compareSubmissions(
      Vector<Submission> submissions,
      Submission baseCodeSubmission
  ) {
    int size = submissions.size();

    // Result vectors
    SortedVector<AllMatches> avgMatches = new SortedVector<>(
        new AllMatches.AvgReversedComparator());
    SortedVector<AllMatches> maxMatches = new SortedVector<>(
        new AllMatches.MaxReversedComparator());
    SortedVector<AllMatches> minMatches = new SortedVector<>(
        new AllMatches.MinReversedComparator());

    int[] dist = new int[10];

    long msec;
    Submission s1, s2;

    if (options.hasBaseCode()) {
      compareSubmissionsToBaseCode(submissions, baseCodeSubmission);
    }

    int totalcomps = size - 1;
    int anz = 0, count = 0;
    AllMatches match;

    msec = System.currentTimeMillis();

    s1loop:
    for (int i = 0; i < size - 1; ) {
      s1 = submissions.elementAt(i);
      if (s1.struct == null) {
        count++;
        continue;
      }

      // Find next valid submission
      int j = i;
      do {
        j++;
        if (j >= size) {
          break s1loop; // no more comparison pairs available
        }
        s2 = submissions.elementAt(j);
      } while (s2.struct == null);

      match = this.gSTiling.compare(s1, s2);

      anz++;

      /*
       * System.out.println("Comparing "+s1.name+"-"+s2.name+": "+
       * match.percent());
       */
      // histogram:
      if (options.hasBaseCode()) {
        match.bcmatchesA = baseCodeMatches.get(match.subA.name);
        match.bcmatchesB = baseCodeMatches.get(match.subB.name);
      }

      registerMatch(match, dist, avgMatches, maxMatches, minMatches, i, j);
      count++;

      i = j;
    }

    long time = System.currentTimeMillis() - msec;

    // TODO
//    print("\n",
//        "Total time for comparing submissions: " + ((time / 3600000 > 0) ? (time / 3600000) + " h "
//            : "")
//            + ((time / 60000 > 0) ? ((time / 60000) % 60000) + " min " : "") + (time / 1000 % 60)
//            + " sec\n" + "Time per comparison: "
//            + (time / anz) + " msec\n");

    // ------------------------------------------------------------------------

    Cluster cluster = null;

    // TODO
//    if (options.getClusterType() != ClusterType.NONE) {
//      cluster = this.clusters.calculateClustering(submissions);
//    }

    return new JPlagResult(cluster, avgMatches, maxMatches, minMatches, dist);
  }
}
