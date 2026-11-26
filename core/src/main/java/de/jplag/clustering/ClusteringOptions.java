
package de.jplag.clustering;

import java.util.Objects;

import de.jplag.clustering.algorithm.InterClusterSimilarity;
import de.jplag.options.SimilarityMetric;

/**
 * Collection of all possible parameters that describe how a clustering should be performed.
 * @param similarityMetric The similarity metric is used for clustering
 * @param spectralKernelBandwidth The kernel bandwidth for the matern kernel used in the Gaussian process for the
 * automatic search for the number of clusters in spectral clustering. Affects the runtime and results of the spectral
 * clustering.
 * @param spectralGaussianProcessVariance This is the assumed level of noise in the evaluation results of a spectral
 * clustering. Acts as normalization parameter for the Gaussian Process. The default setting works well with similarity
 * scores in the range between zero and one. Affects the results of the spectral clustering.
 * @param spectralMinRuns The minimal number of times the kMeans algorithm is run for the spectral clustering. These
 * runs will use predefined numbers of clusters and will not use the Bayesian optimization to determine the number of
 * clusters.
 * @param spectralMaxRuns The maximal number of times the kMeans algorithm is run during the Bayesian optimization for
 * spectral clustering. The Bayesian optimization may be stopped before, when no more maxima of the acquisition-function
 * are found.
 * @param spectralMaxKMeansIterationPerRun Maximum number of iterations of the kMeans clustering per run during spectral
 * clustering.
 * @param agglomerativeThreshold Agglomerative clustering will merge clusters that have a similarity higher than this
 * threshold.
 * @param preprocessor Preprocessing for the similarity values before clustering. Preprocessing is mandatory for
 * spectral clustering and optional for agglomerative clustering.
 * @param enabled whether clustering should be performed
 * @param algorithm the clustering algorithm to use
 * @param agglomerativeInterClusterSimilarity Similarity measure between clusters in agglomerative clustering
 * @param preprocessorThreshold up to which similarity the threshold-preprocessor zeroes out the similarities
 * @param preprocessorPercentile up to which percentile of similarities the percentile-preprocessor zeroes out the
 * similarities
 */
public record ClusteringOptions(SimilarityMetric similarityMetric, double spectralKernelBandwidth, double spectralGaussianProcessVariance,
        int spectralMinRuns, int spectralMaxRuns, int spectralMaxKMeansIterationPerRun, double agglomerativeThreshold, Preprocessing preprocessor,
        boolean enabled, ClusteringAlgorithm algorithm, InterClusterSimilarity agglomerativeInterClusterSimilarity, double preprocessorThreshold,
        double preprocessorPercentile) {

    /**
     * Default value for the similarity metric
     */
    public static final SimilarityMetric DEFAULT_SIMILARITY_METRIC = SimilarityMetric.AVG;
    /**
     * Default value for the spectral clustering kernel bandwidth
     */
    public static final double DEFAULT_SPECTRAL_KERNEL_BANDWIDTH = 20.f;
    /**
     * Default value for the spectral clustering Gaussian process variance
     */
    public static final double DEFAULT_SPECTRAL_GAUSSIAN_PROCESS_VARIANCE = 0.05 * 0.05;
    /**
     * Default value for the minimal number of kMeans runs in spectral clustering
     */
    public static final int DEFAULT_SPECTRAL_MIN_RUNS = 5;
    /**
     * Default value for the maximal number of kMeans runs in spectral clustering
     */
    public static final int DEFAULT_SPECTRAL_MAX_RUNS = 50;
    /**
     * Default value for the maximal number of kMeans iterations per run in spectral clustering
     */
    public static final int DEFAULT_SPECTRAL_MAX_KMEANS_ITERATION_PER_RUN = 200;
    /**
     * Default value for the agglomerative clustering threshold
     */
    public static final double DEFAULT_AGGLOMERATIVE_THRESHOLD = 0.2;
    /**
     * Default value for the preprocessor
     */
    public static final Preprocessing DEFAULT_PREPROCESSOR = Preprocessing.CUMULATIVE_DISTRIBUTION_FUNCTION;
    /**
     * Default value for whether clustering is enabled
     */
    public static final boolean DEFAULT_ENABLED = true;
    /**
     * Default value for the clustering algorithm
     */
    public static final ClusteringAlgorithm DEFAULT_ALGORITHM = ClusteringAlgorithm.SPECTRAL;
    /**
     * Default value for the inter-cluster similarity in agglomerative clustering
     */
    public static final InterClusterSimilarity DEFAULT_AGGLOMERATIVE_INTER_CLUSTER_SIMILARITY = InterClusterSimilarity.AVERAGE;
    /**
     * Default value for the preprocessor threshold
     */
    public static final double DEFAULT_PREPROCESSOR_THRESHOLD = 0.2;
    /**
     * Default value for the preprocessor percentile
     */
    public static final double DEFAULT_PREPROCESSOR_PERCENTILE = 0.5;

    /**
     * Constructs clustering options with all configuration parameters.
     * @param similarityMetric metric used to measure similarity between submissions
     * @param spectralKernelBandwidth kernel bandwidth for spectral clustering
     * @param spectralGaussianProcessVariance variance for spectral clustering's GP
     * @param spectralMinRuns minimum runs for spectral clustering
     * @param spectralMaxRuns maximum runs for spectral clustering
     * @param spectralMaxKMeansIterationPerRun max iterations per KMeans run in spectral clustering
     * @param agglomerativeThreshold threshold for agglomerative clustering
     * @param preprocessor preprocessing method applied before clustering
     * @param enabled whether clustering is enabled
     * @param algorithm clustering algorithm to use
     * @param agglomerativeInterClusterSimilarity similarity metric between clusters for agglomerative clustering
     * @param preprocessorThreshold threshold used by the preprocessor
     * @param preprocessorPercentile percentile used by the preprocessor
     */
    public ClusteringOptions(SimilarityMetric similarityMetric, double spectralKernelBandwidth, double spectralGaussianProcessVariance,
            int spectralMinRuns, int spectralMaxRuns, int spectralMaxKMeansIterationPerRun, double agglomerativeThreshold, Preprocessing preprocessor,
            boolean enabled, ClusteringAlgorithm algorithm, InterClusterSimilarity agglomerativeInterClusterSimilarity, double preprocessorThreshold,
            double preprocessorPercentile) {
        this.similarityMetric = Objects.requireNonNull(similarityMetric);
        this.spectralKernelBandwidth = spectralKernelBandwidth;
        this.spectralGaussianProcessVariance = spectralGaussianProcessVariance;
        this.spectralMinRuns = spectralMinRuns;
        this.spectralMaxRuns = spectralMaxRuns;
        this.spectralMaxKMeansIterationPerRun = spectralMaxKMeansIterationPerRun;
        this.agglomerativeThreshold = agglomerativeThreshold;
        this.preprocessor = Objects.requireNonNull(preprocessor);
        this.enabled = enabled;
        this.algorithm = Objects.requireNonNull(algorithm);
        this.agglomerativeInterClusterSimilarity = Objects.requireNonNull(agglomerativeInterClusterSimilarity);
        this.preprocessorThreshold = preprocessorThreshold;
        this.preprocessorPercentile = preprocessorPercentile;
    }

    /**
     * Constructs clustering options with default values.
     */
    public ClusteringOptions() {
        this(DEFAULT_ENABLED);
    }

    /**
     * Constructs clustering options with default values.
     * @param enabled whether clustering is enabled
     */
    public ClusteringOptions(boolean enabled) {
        this(DEFAULT_SIMILARITY_METRIC, DEFAULT_SPECTRAL_KERNEL_BANDWIDTH, DEFAULT_SPECTRAL_GAUSSIAN_PROCESS_VARIANCE, DEFAULT_SPECTRAL_MIN_RUNS,
                DEFAULT_SPECTRAL_MAX_RUNS, DEFAULT_SPECTRAL_MAX_KMEANS_ITERATION_PER_RUN, DEFAULT_AGGLOMERATIVE_THRESHOLD, DEFAULT_PREPROCESSOR,
                enabled, DEFAULT_ALGORITHM, DEFAULT_AGGLOMERATIVE_INTER_CLUSTER_SIMILARITY, DEFAULT_PREPROCESSOR_THRESHOLD,
                DEFAULT_PREPROCESSOR_PERCENTILE);
    }
}
