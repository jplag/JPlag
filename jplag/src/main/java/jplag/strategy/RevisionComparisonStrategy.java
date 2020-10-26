package jplag.strategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import jplag.JPlagComparison;
import jplag.GSTiling;
import jplag.JPlagOptions;
import jplag.JPlagResult;
import jplag.Submission;

public class RevisionComparisonStrategy extends AbstractComparisonStrategy {

  public RevisionComparisonStrategy(JPlagOptions options, GSTiling gSTiling) {
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
    int numberOfComparisons = 0;
    int numberOfSubmissions = submissions.size();
    Submission s1, s2;
    JPlagComparison comparison;

    List<JPlagComparison> comparisons = new ArrayList<>();

    s1loop:
    for (int i = 0; i < numberOfSubmissions - 1; ) {
      s1 = submissions.elementAt(i);

      if (s1.tokenList == null) {
        continue;
      }

      // Find next valid submission
      int j = i;

      do {
        j++;

        if (j >= numberOfSubmissions) {
          break s1loop; // no more comparison pairs available
        }

        s2 = submissions.elementAt(j);
      } while (s2.tokenList == null);

      comparison = this.gSTiling.compare(s1, s2);
      numberOfComparisons++;

      System.out.println("Comparing " + s1.name + "-" + s2.name + ": " + comparison.percent());

      if (baseCodeSubmission != null) {
        comparison.bcMatchesA = baseCodeMatches.get(comparison.subA.name);
        comparison.bcMatchesB = baseCodeMatches.get(comparison.subB.name);
      }

      if (isAboveSimilarityThreshold(comparison)) {
        comparisons.add(comparison);
      }

      i = j;
    }

    long durationInMillis = System.currentTimeMillis() - timeBeforeStartInMillis;

    // TODO:
    // Cluster cluster = null;
    //
    // if (options.getClusterType() != ClusterType.NONE) {
    //     cluster = this.clusters.calculateClustering(submissions);
    // }

    return new JPlagResult(comparisons, numberOfComparisons, durationInMillis);
  }
}
