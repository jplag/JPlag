package jplag.strategy;

import java.util.Vector;
import jplag.JPlagComparison;
import jplag.GSTiling;
import jplag.JPlagOptions;
import jplag.JPlagResult;
import jplag.SortedVector;
import jplag.Submission;
import jplag.clustering.Cluster;

public class NormalComparisonStrategy extends AbstractComparisonStrategy {

  public NormalComparisonStrategy(JPlagOptions options, GSTiling gSTiling) {
    super(options, gSTiling);
  }

  @Override
  public JPlagResult compareSubmissions(
      Vector<Submission> submissions,
      Submission baseCodeSubmission
  ) {
    if (baseCodeSubmission != null) {
      compareSubmissionsToBaseCode(submissions, baseCodeSubmission);
    }

    // Result vectors
    SortedVector<JPlagComparison> avgMatches = new SortedVector<>(new JPlagComparison.AvgComparator());
    SortedVector<JPlagComparison> maxMatches = new SortedVector<>(new JPlagComparison.MaxComparator());
    // TODO: Why is minMatches missing?

    // Similarity distribution
    int[] dist = new int[10];

    int numberOfSubmissions = submissions.size();
    int i, j, numberOfComparisons = 0;

    Submission s1, s2;
    JPlagComparison comparison;

    long timeMillis = System.currentTimeMillis();

    for (i = 0; i < (numberOfSubmissions - 1); i++) {
      s1 = submissions.elementAt(i);

      if (s1.tokenList == null) {
        continue;
      }

      for (j = (i + 1); j < numberOfSubmissions; j++) {
        s2 = submissions.elementAt(j);

        if (s2.tokenList == null) {
          continue;
        }

        comparison = this.gSTiling.compare(s1, s2);
        numberOfComparisons++;

        System.out.println("Comparing " + s1.name + "-" + s2.name + ": " + comparison.percent());

        if (baseCodeSubmission != null) {
          comparison.bcMatchesA = baseCodeMatches.get(comparison.subA.name);
          comparison.bcMatchesB = baseCodeMatches.get(comparison.subB.name);
        }

        registerMatch(comparison, dist, avgMatches, maxMatches, null, i, j);
      }
    }


    long time = System.currentTimeMillis() - timeMillis;

    // TODO
//    print("\n",
//        "Total time for comparing submissions: " + ((time / 3600000 > 0) ? (time / 3600000) + " h "
//            : "")
//            + ((time / 60000 > 0) ? ((time / 60000) % 60000) + " min " : "") + (time / 1000 % 60)
//            + " sec\n" + "Time per comparison: "
//            + (time / numberOfComparisons) + " msec\n");

    Cluster cluster = null;

    // TODO
//    if (options.getClusterType() != ClusterType.NONE) {
//      cluster = this.clusters.calculateClustering(submissions);
//    }

    return new JPlagResult(cluster, avgMatches, maxMatches, null, dist);
  }

}
