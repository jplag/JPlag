package jplag;

import java.util.List;
import jplag.clustering.Cluster;

public class JPlagResult {

  private Cluster cluster;

  private SortedVector<JPlagComparison> avgMatches;

  private SortedVector<JPlagComparison> maxMatches;

  private SortedVector<JPlagComparison> minMatches;

  private List<JPlagComparison> comparisons;

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

  /**
   * Duration of the JPlag run in milliseconds.
   */
  private long durationInMillis;

  public JPlagResult() {
  }

  public JPlagResult(List<JPlagComparison> comparisons, long durationInMillis) {
    this.comparisons = comparisons;
    this.durationInMillis = durationInMillis;
    this.similarityDistribution = calculateSimilarityDistribution(comparisons);
  }

  @Deprecated
  public JPlagResult(
      Cluster cluster,
      SortedVector<JPlagComparison> avgMatches,
      SortedVector<JPlagComparison> maxMatches,
      SortedVector<JPlagComparison> minMatches
  ) {
    this.cluster = cluster;
    this.avgMatches = avgMatches;
    this.maxMatches = maxMatches;
    this.minMatches = minMatches;

    this.similarityDistribution = calculateSimilarityDistribution(avgMatches);
  }

  /**
   * Note: Before, comparisons with a similarity below the given threshold were also included in the
   * similarity matrix.
   */
  private int[] calculateSimilarityDistribution(List<JPlagComparison> comparisons) {
    int[] similarityDistribution = new int[10];

    comparisons.stream()
        .map(JPlagComparison::percent)
        .map(percent -> percent / 10)
        .map(Float::intValue)
        .map(index -> index == 10 ? 9 : index)
        .forEach(index -> similarityDistribution[index]++);

    return similarityDistribution;
  }

  public List<JPlagComparison> getComparisons() {
    return comparisons;
  }

  public long getDuration() {
    return durationInMillis;
  }

  public int getNumberOfComparisons() {
    return comparisons.size();
  }

  public int[] getSimilarityDistribution() {
    return similarityDistribution;
  }

  @Override
  public String toString() {
    return String.format(
        "JPlagResult { duration: %d ms, comparisons: %d }",
        getDuration(),
        getNumberOfComparisons()
    );
  }
}
