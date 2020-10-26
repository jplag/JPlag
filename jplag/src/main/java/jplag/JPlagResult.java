package jplag;

import java.util.List;
import jplag.clustering.Cluster;

public class JPlagResult {


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
   * Total number of comparisons. This number also takes into account the comparisons that were
   * ignored due to their too low similarity.
   */
  private int totalNumberOfComparisons;

  /**
   * Duration of the JPlag run in milliseconds.
   */
  private long durationInMillis;

  /**
   * List of detected comparisons whose similarity was about the specified threshold.
   */
  private List<JPlagComparison> comparisons;

  public JPlagResult() {
  }

  public JPlagResult(
      List<JPlagComparison> comparisons,
      int totalNumberOfComparisons,
      long durationInMillis
  ) {
    this.comparisons = comparisons;
    this.durationInMillis = durationInMillis;
    this.totalNumberOfComparisons = totalNumberOfComparisons;

    this.similarityDistribution = calculateSimilarityDistribution(comparisons);
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

  public int getTotalNumberOfComparisons() {
    return totalNumberOfComparisons;
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
        "JPlagResult { duration: %d ms, totalComparisons: %d, detectedComparisons: %d }",
        getDuration(),
        getTotalNumberOfComparisons(),
        getNumberOfComparisons()
    );
  }
}
