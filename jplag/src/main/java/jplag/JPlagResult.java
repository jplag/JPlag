package jplag;

import java.util.List;
import jplag.clustering.Cluster;

public class JPlagResult {

  private Cluster cluster;

  private List<JPlagComparison> comparisons;

  private SortedVector<JPlagComparison> avgMatches;

  private SortedVector<JPlagComparison> maxMatches;

  private SortedVector<JPlagComparison> minMatches;


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
      SortedVector<JPlagComparison> avgMatches,
      SortedVector<JPlagComparison> maxMatches,
      SortedVector<JPlagComparison> minMatches,
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
