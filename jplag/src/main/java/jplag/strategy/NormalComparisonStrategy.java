package jplag.strategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import jplag.JPlagComparison;
import jplag.GSTiling;
import jplag.JPlagOptions;
import jplag.JPlagResult;
import jplag.Submission;

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

    long timeBeforeStartInMillis = System.currentTimeMillis();
    int i, j, numberOfSubmissions = submissions.size();
    Submission s1, s2;
    JPlagComparison comparison;

    List<JPlagComparison> comparisons = new ArrayList<>();

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

        System.out.println("Comparing " + s1.name + "-" + s2.name + ": " + comparison.percent());

        if (baseCodeSubmission != null) {
          comparison.bcMatchesA = baseCodeMatches.get(comparison.subA.name);
          comparison.bcMatchesB = baseCodeMatches.get(comparison.subB.name);
        }

        if (isAboveSimilarityThreshold(comparison)) {
          comparisons.add(comparison);
        }
      }
    }

    long durationInMillis = System.currentTimeMillis() - timeBeforeStartInMillis;

    // TODO:
    // Cluster cluster = null;
    //
    // if (options.getClusterType() != ClusterType.NONE) {
    //     cluster = this.clusters.calculateClustering(submissions);
    // }

    return new JPlagResult(comparisons, durationInMillis);
  }

}
