package jplag;

import jplag.clustering.Cluster;

public class JPlagResult {

  // TODO: Only for debugging purposes.
  private String test = "Works";

  // --------------------------------------------------------------------------

  private Cluster cluster;

  private SortedVector<AllMatches> avgMatches;

  private SortedVector<AllMatches> maxMatches;

  private SortedVector<AllMatches> minMatches;


  /**
   * 10-element array representing the similarity distribution of the detected matches.
   * <p>
   * Each entry represents the absolute frequency of matches whose similarity lies within the
   * respective interval.
   * <p>
   * Intervals:
   * <p>
   * 0: [0% - 10%), 1: [10% - 20%), 2: [20% - 30%), ..., 9: [90% - 100%]
   */
  private int[] similarityDistribution = null;

  public JPlagResult() {

  }

  public JPlagResult(
      Cluster cluster,
      SortedVector<AllMatches> avgMatches,
      SortedVector<AllMatches> maxMatches,
      SortedVector<AllMatches> minMatches,
      int[] similarityDistribution
  ) {
    this.cluster = cluster;
    this.avgMatches = avgMatches;
    this.maxMatches = maxMatches;
    this.minMatches = minMatches;
    this.similarityDistribution = similarityDistribution;
  }

  public int[] getSimilarityDistribution() {
    return similarityDistribution;
  }

  public String getTest() {
    return test;
  }
}
