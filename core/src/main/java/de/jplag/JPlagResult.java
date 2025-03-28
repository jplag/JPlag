package de.jplag;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.ToDoubleFunction;

import de.jplag.clustering.ClusteringResult;
import de.jplag.options.JPlagOptions;
import de.jplag.options.SimilarityMetric;

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
    private final int SIMILARITY_DISTRIBUTION_SIZE = 100;

    public JPlagResult(List<JPlagComparison> comparisons, SubmissionSet submissions, long durationInMillis, JPlagOptions options) {
        // sort by similarity (descending)
        this.comparisons = comparisons.stream().sorted(Comparator.comparing(JPlagComparison::similarity).reversed()).toList();
        this.submissions = submissions;
        this.durationInMillis = durationInMillis;
        this.options = options;
        similarityDistribution = calculateSimilarityDistribution(comparisons);
    }

    /**
     * Drops elements from the comparison list to free memory. Note, that this affects the similarity distribution and is
     * only meant to be used if you don't need the information about comparisons with lower match similarity anymore.
     * @param limit the number of comparisons to keep in the list
     */
    public void dropComparisons(int limit) {
        this.comparisons = this.getComparisons(limit);
    }

    public void setClusteringResult(List<ClusteringResult<Submission>> clustering) {
        this.clusteringResult = clustering;
    }

    /**
     * Returns all comparisons.
     * @return a list of all comparisons sorted by similarity (descending).
     */
    public List<JPlagComparison> getAllComparisons() {
        return comparisons;
    }

    /**
     * Returns the first n comparisons (sorted by similarity, descending), limited by the specified parameter.
     * @param numberOfComparisons specifies the number of requested comparisons. If set to -1, all comparisons will be
     * returned.
     * @return a list of comparisons sorted descending by similarity.
     */
    public List<JPlagComparison> getComparisons(int numberOfComparisons) {
        if (numberOfComparisons == JPlagOptions.SHOW_ALL_COMPARISONS) {
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
     * Provides all submissions.
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
     * Provides access the the options.
     * @return the JPlag options with which the JPlag run was configured.
     */
    public JPlagOptions getOptions() {
        return options;
    }

    /**
     * For the {@link SimilarityMetric} JPlag was run with, this returns the similarity distribution of detected matches in
     * a 100-element array. Each entry represents the absolute frequency of matches whose similarity lies within the
     * respective interval. Intervals: 0: [0% - 1%), 1: [1% - 2%), 2: [2% - 3%), ..., 99: [99% - 100%]
     * @return the similarity distribution array.
     */
    public int[] getSimilarityDistribution() {
        return similarityDistribution;
    }

    /**
     * For the {@link SimilarityMetric#MAX} that is built in to every {@link JPlagComparison}, this returns the similarity
     * distribution of detected matches in a 100-element array. Each entry represents the absolute frequency of matches
     * whose similarity lies within the respective interval. Intervals: 0: [0% - 1%), 1: [1% - 20%), 2: [2% - 3%), ..., 99:
     * [99% - 100%].
     * @return the similarity distribution array. When JPlag was run with the {@link SimilarityMetric#MAX}, this will return
     * the same distribution as {@link JPlagResult#getSimilarityDistribution()}.
     */
    public int[] getMaxSimilarityDistribution() {
        return calculateDistributionFor(comparisons, JPlagComparison::maximalSimilarity);
    }

    public List<ClusteringResult<Submission>> getClusteringResult() {
        return this.clusteringResult;
    }

    /**
     * Calculates the distribution of all comparisons. The distribution is boxed to a 100-Element Array, index with ranges:
     * 0: [0%, 1%), 1: [1%, 2%), ..., 99: [99%, 100%].
     * @param similarityMetric Metric to use.
     * @return the similarity distribution.
     */
    public List<Integer> calculateDistributionFor(ToDoubleFunction<JPlagComparison> similarityMetric) {
        return Arrays.stream(calculateDistributionFor(this.comparisons, similarityMetric)).boxed().toList();
    }

    @Override
    public String toString() {
        return String.format("JPlagResult { comparisons: %d, duration: %d ms, language: %s, submissions: %d }", getAllComparisons().size(),
                getDuration(), getOptions().language().getName(), submissions.numberOfSubmissions());
    }

    /**
     * Note: Before, comparisons with a similarity below the given threshold were also included in the similarity matrix.
     */
    private int[] calculateSimilarityDistribution(List<JPlagComparison> comparisons) {
        return calculateDistributionFor(comparisons, JPlagComparison::similarity);
    }

    private int[] calculateDistributionFor(List<JPlagComparison> comparisons, ToDoubleFunction<JPlagComparison> similarityExtractor) {
        int[] similarityDistribution = new int[SIMILARITY_DISTRIBUTION_SIZE];
        for (JPlagComparison comparison : comparisons) {
            double similarity = similarityExtractor.applyAsDouble(comparison); // extract similarity: 0.0 <= similarity <= 1.0
            int index = (int) (similarity * SIMILARITY_DISTRIBUTION_SIZE); // divide similarity by bucket size to find index of correct bucket.
            index = Math.min(index, SIMILARITY_DISTRIBUTION_SIZE - 1); // index is out of bounds when similarity is 1.0. decrease by one to count
                                                                       // towards the highest value bucket
            similarityDistribution[index]++; // count comparison towards its determined bucket.
        }
        return similarityDistribution;
    }
}
