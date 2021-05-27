package jplag;

import java.util.List;

/**
 * Encapsulates the results of a comparison of a set of source code submissions.
 */
public class JPlagResult {

    /**
     * List of detected comparisons whose similarity was about the specified threshold.
     */
    private List<JPlagComparison> comparisons;

    /**
     * Duration of the JPlag run in milliseconds.
     */
    private long durationInMillis;

    /**
     * Total number of submissions that have been compared.
     */
    private int numberOfSubmissions;

    /**
     * Options for the plagiarism detection run.
     */
    private JPlagOptions options;

    /**
     * 10-element array representing the similarity distribution of the detected matches.
     * <p>
     * Each entry represents the absolute frequency of matches whose similarity lies within the respective interval.
     * <p>
     * Intervals:
     * <p>
     * 0: [0% - 10%), 1: [10% - 20%), 2: [20% - 30%), ..., 9: [90% - 100%]
     */
    private int[] similarityDistribution = null;

    /**
     * Creates empty results.
     */
    public JPlagResult() {
        // No results available.
    }

    public JPlagResult(List<JPlagComparison> comparisons, long durationInMillis, int numberOfSubmissions, JPlagOptions options) {
        this.comparisons = comparisons;
        this.durationInMillis = durationInMillis;
        this.numberOfSubmissions = numberOfSubmissions;
        this.options = options;

        this.similarityDistribution = calculateSimilarityDistribution(comparisons);
    }

    /**
     * Note: Before, comparisons with a similarity below the given threshold were also included in the similarity matrix.
     */
    private int[] calculateSimilarityDistribution(List<JPlagComparison> comparisons) {
        int[] similarityDistribution = new int[10];

        comparisons.stream().map(JPlagComparison::percent).map(percent -> percent / 10).map(Float::intValue).map(index -> index == 10 ? 9 : index)
                .forEach(index -> similarityDistribution[index]++);

        return similarityDistribution;
    }

    public List<JPlagComparison> getComparisons() {
        return comparisons;
    }

    public long getDuration() {
        return durationInMillis;
    }

    public JPlagOptions getOptions() {
        return options;
    }

    public int getNumberOfSubmissions() {
        return numberOfSubmissions;
    }

    public int[] getSimilarityDistribution() {
        return similarityDistribution;
    }

    @Override
    public String toString() {
        return String.format("JPlagResult { comparisons: %d, duration: %d ms, language: %s, submissions: %d }", getComparisons().size(),
                getDuration(), getOptions().getLanguageOption(), getNumberOfSubmissions());
    }
}
