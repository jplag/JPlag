package jplag.strategy;

import java.util.Hashtable;
import java.util.Vector;
import jplag.JPlagBaseCodeComparison;
import jplag.JPlagComparison;
import jplag.GSTiling;
import jplag.JPlagOptions;
import jplag.SortedVector;
import jplag.Submission;

public abstract class AbstractComparisonStrategy implements ComparisonStrategy {

  /**
   * Hashtable that maps the name of a submissions to its matches with the provided base code.
   */
  protected Hashtable<String, JPlagBaseCodeComparison> baseCodeMatches = new Hashtable<>(30);

  protected GSTiling gSTiling;

  protected JPlagOptions options;

  public AbstractComparisonStrategy(JPlagOptions options, GSTiling gSTiling) {
    this.gSTiling = gSTiling;
    this.options = options;
  }

  protected void compareSubmissionsToBaseCode(
      Vector<Submission> submissions,
      Submission baseCodeSubmission
  ) {
    int numberOfSubmissions = submissions.size();

    JPlagBaseCodeComparison bcMatch;
    Submission currentSubmission;

//    long msec = System.currentTimeMillis();

    for (int i = 0; i < (numberOfSubmissions); i++) {
      currentSubmission = submissions.elementAt(i);

      bcMatch = this.gSTiling.compareWithBaseCode(currentSubmission, baseCodeSubmission);
      baseCodeMatches.put(currentSubmission.name, bcMatch);

      this.gSTiling.resetBaseSubmission(baseCodeSubmission);
    }

//    long timebc = System.currentTimeMillis() - msec;
//
//    print("\n\n",
//        "\nTime for comparing with Basecode: " + ((timebc / 3600000 > 0) ? (timebc / 3600000)
//            + " h " : "")
//            + ((timebc / 60000 > 0) ? ((timebc / 60000) % 60000) + " min " : "") + (timebc / 1000
//            % 60) + " sec\n"
//            + "Time per basecode comparison: " + (timebc / numberOfSubmissions) + " msec\n\n");
  }

  protected void registerMatch(
      JPlagComparison match,
      SortedVector<JPlagComparison> avgMatches,
      SortedVector<JPlagComparison> maxMatches,
      SortedVector<JPlagComparison> minMatches,
      int a,
      int b
  ) {
    float avgPercent = match.percent();
    float maxPercent = match.percentMaxAB();
    float minPercent = match.percentMinAB();

    if (!options.isStorePercent()) {
      if ((avgMatches.size() < options.getStoreMatches() || avgPercent > avgMatches.lastElement()
          .percent()) && avgPercent > 0) {
        avgMatches.insert(match);

        if (avgMatches.size() > options.getStoreMatches()) {
          avgMatches.removeElementAt(options.getStoreMatches());
        }
      }

      if (maxMatches != null && (maxMatches.size() < options.getStoreMatches()
          || maxPercent > maxMatches.lastElement().percent()) && maxPercent > 0) {
        maxMatches.insert(match);

        if (maxMatches.size() > options.getStoreMatches()) {
          maxMatches.removeElementAt(options.getStoreMatches());
        }
      }

      if (minMatches != null && (minMatches.size() < options.getStoreMatches()
          || minPercent > minMatches.lastElement().percent()) && minPercent > 0) {
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

    // TODO
//    if (options.getClusterType() != ClusterType.NONE) {
//      similarity.setSimilarity(a, b, avgPercent);
//    }
  }

}
