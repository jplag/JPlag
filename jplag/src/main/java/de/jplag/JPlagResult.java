package de.jplag;

import java.util.List;

import de.jplag.clustering.ClusteringResult;
import de.jplag.options.JPlagOptions;

/**
 * Encapsulates the results of a comparison of a set of source code submissions.
 */
public class JPlagResult {

    private List<JPlagComparison> comparisons; // comparisons whose similarity was about the specified threshold

    private final SubmissionSet submissions;

    private final JPlagOptions options;

    private final long durationInMillis;

    private final int[] similarityDistribution; // 10-element array representing the similarity distribution of the detected matches.

    private List<ClusteringResult<Submission>> clusteringResult;

    public JPlagResult(List<JPlagComparison> comparisons, SubmissionSet submissions, long durationInMillis, JPlagOptions options) {
        this.comparisons = comparisons;
        this.submissions = submissions;
        this.durationInMillis = durationInMillis;
        this.options = options;
        similarityDistribution = calculateSimilarityDistribution(comparisons);
        comparisons.sort((first, second) -> Float.compare(second.similarity(), first.similarity())); // Sort by percentage (descending).
    }

    /**
     * Drops elements from the comparison list to free memory. Note, that this affects the similarity distribution and is
     * only meant to be used if you don't need the information about comparisons with lower match percentage anymore.
     * @param limit the number of comparisons to keep in the list
     */
    public void dropComparisons(int limit) {
        this.comparisons = this.getComparisons(limit);
    }

    public void setClusteringResult(List<ClusteringResult<Submission>> clustering) {
        this.clusteringResult = clustering;
    }

    /**
     * @return a list of all comparisons sorted by percentage (descending)
     */
    public List<JPlagComparison> getComparisons() {
        return comparisons;
    }

    /**
     * Returns the first n comparisons (sorted by percentage, descending), limited by the specified parameter.
     * @param numberOfComparisons specifies the number of requested comparisons. If set to -1, all comparisons will be
     * returned.
     * @return a list of comparisons sorted descending by percentage.
     */
    public List<JPlagComparison> getComparisons(int numberOfComparisons) {
        if (numberOfComparisons == -1) {
            return comparisons;
        }
        return comparisons.subList(0, Math.min(numberOfComparisons, comparisons.size()));
    }

    /**
     * @return the duration of the comparison in milliseconds.
     */
    public long getDuration() {
        return durationInMillis;
    }

    /**
     * @return the submission set that contains both the valid submissions and the invalid ones.
     */
    public SubmissionSet getSubmissions() {
        return submissions;
    }

    /**
     * @return the total number of submissions that have been compared.
     */
    public int getNumberOfSubmissions() {
        return submissions.numberOfSubmissions(); // Convenience method to preserve API
    }

    /**
     * @return the JPlag options with which the JPlag run was configured.
     */
    public JPlagOptions getOptions() {
        return options;
    }

    /**
     * Returns the similarity distribution of detected matches in a 10-element array. Each entry represents the absolute
     * frequency of matches whose similarity lies within the respective interval. Intervals: 0: [0% - 10%), 1: [10% - 20%),
     * 2: [20% - 30%), ..., 9: [90% - 100%]
     * @return the similarity distribution array.
     */
    public int[] getSimilarityDistribution() {
        return similarityDistribution;
    }

    public List<ClusteringResult<Submission>> getClusteringResult() {
        return this.clusteringResult;
    }

    @Override
    public String toString() {
        return String.format("JPlagResult { comparisons: %d, duration: %d ms, language: %s, submissions: %d }", getComparisons().size(),
                getDuration(), getOptions().getLanguageOption(), submissions.numberOfSubmissions());
    }

    /**
     * Note: Before, comparisons with a similarity below the given threshold were also included in the similarity matrix.
     */
    private int[] calculateSimilarityDistribution(List<JPlagComparison> comparisons) {
        int[] similarityDistribution = new int[10];

        comparisons.stream().map(JPlagComparison::similarity).map(percent -> percent / 10).map(Float::intValue).map(index -> index == 10 ? 9 : index)
                .forEach(index -> similarityDistribution[index]++);

        return similarityDistribution;
    }
}
