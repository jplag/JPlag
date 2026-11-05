
package de.jplag.clustering;

import java.util.Objects;

import de.jplag.clustering.algorithm.ChineseWhispersClusteringMode;
import de.jplag.clustering.algorithm.InterClusterSimilarity;
import de.jplag.options.SimilarityMetric;

/**
 * Collection of all possible parameters that describe how a clustering should be performed.
 * @param similarityMetric The similarity metric is used for clustering
 * @param spectralKernelBandwidth The kernel bandwidth for the matern kernel used in the gaussian process for the
 * automatic search for the number of clusters in spectral clustering. Affects the runtime and results of the spectral
 * clustering.
 * @param spectralGaussianProcessVariance This is the assumed level of noise in the evaluation results of a spectral
 * clustering. Acts as normalization parameter for the Gaussian Process. The default setting works well with similarity
 * scores in the range between zero and one. Affects the results of the spectral clustering.
 * @param spectralMinRuns The minimal number of times the kMeans algorithm is run for the spectral clustering. These
 * runs will use predefined numbers of clusters and will not use the bayesian optimization to determine the number of
 * clusters.
 * @param spectralMaxRuns The maximal number of times the kMeans algorithm is run during the bayesian optimization for
 * spectral clustering. The bayesian optimization may be stopped before, when no more maxima of the acquisition-function
 * are found.
 * @param spectralMaxKMeansIterationPerRun Maximum number of iterations of the kMeans clustering per run during spectral
 * clustering.
 * @param agglomerativeThreshold Agglomerative clustering will merge clusters that have a similarity higher than this
 * threshold.
 * @param chineseWhispersMaxIterations The maximum number of iterations during Chinese Whispers clustering.
 * @param chineseWhispersClusteringMode Specifies the exact mode of operation of the Chinese Whispers algorithm.
 * @param useAdvancedSimilarityPreprocessing Specifies, whether {@link ClusterFocusedSimilarityMatrixCreator advanced
 * similarity preprocessing} will be used.
 * @param matchGroupWeightingMode The weighting mode that should be used in the advanced similarity preprocessing.
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
        int spectralMinRuns, int spectralMaxRuns, int spectralMaxKMeansIterationPerRun, double agglomerativeThreshold,
        int chineseWhispersMaxIterations, ChineseWhispersClusteringMode chineseWhispersClusteringMode, boolean useAdvancedSimilarityPreprocessing,
        MatchGroupWeightingMode matchGroupWeightingMode, Preprocessing preprocessor, boolean enabled, ClusteringAlgorithm algorithm,
        InterClusterSimilarity agglomerativeInterClusterSimilarity, double preprocessorThreshold, double preprocessorPercentile) {

    /**
     * Constructs clustering options with all configuration parameters.
     * @param similarityMetric metric used to measure similarity between submissions
     * @param spectralKernelBandwidth kernel bandwidth for spectral clustering
     * @param spectralGaussianProcessVariance variance for spectral clustering's GP
     * @param spectralMinRuns minimum runs for spectral clustering
     * @param spectralMaxRuns maximum runs for spectral clustering
     * @param spectralMaxKMeansIterationPerRun max iterations per KMeans run in spectral clustering
     * @param agglomerativeThreshold threshold for agglomerative clustering
     * @param chineseWhispersMaxIterations max iterations in Chinese Whispers clustering
     * @param chineseWhispersClusteringMode the exact mode of operation for the Chinese Whispers algorithm
     * @param useAdvancedSimilarityPreprocessing whether to use advanced similarity preprocessing or not
     * @param matchGroupWeightingMode the weighting mode that should be used in the advanced similarity preprocessing
     * @param preprocessor preprocessing method applied before clustering
     * @param enabled whether clustering is enabled
     * @param algorithm clustering algorithm to use
     * @param agglomerativeInterClusterSimilarity similarity metric between clusters for agglomerative clustering
     * @param preprocessorThreshold threshold used by the preprocessor
     * @param preprocessorPercentile percentile used by the preprocessor
     */
    public ClusteringOptions(SimilarityMetric similarityMetric, double spectralKernelBandwidth, double spectralGaussianProcessVariance,
            int spectralMinRuns, int spectralMaxRuns, int spectralMaxKMeansIterationPerRun, double agglomerativeThreshold,
            int chineseWhispersMaxIterations, ChineseWhispersClusteringMode chineseWhispersClusteringMode, boolean useAdvancedSimilarityPreprocessing,
            MatchGroupWeightingMode matchGroupWeightingMode, Preprocessing preprocessor, boolean enabled, ClusteringAlgorithm algorithm,
            InterClusterSimilarity agglomerativeInterClusterSimilarity, double preprocessorThreshold, double preprocessorPercentile) {
        this.similarityMetric = Objects.requireNonNull(similarityMetric);
        this.spectralKernelBandwidth = spectralKernelBandwidth;
        this.spectralGaussianProcessVariance = spectralGaussianProcessVariance;
        this.spectralMinRuns = spectralMinRuns;
        this.spectralMaxRuns = spectralMaxRuns;
        this.spectralMaxKMeansIterationPerRun = spectralMaxKMeansIterationPerRun;
        this.agglomerativeThreshold = agglomerativeThreshold;
        this.chineseWhispersMaxIterations = chineseWhispersMaxIterations;
        this.chineseWhispersClusteringMode = chineseWhispersClusteringMode;
        this.useAdvancedSimilarityPreprocessing = useAdvancedSimilarityPreprocessing;
        this.matchGroupWeightingMode = matchGroupWeightingMode;
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
        this(SimilarityMetric.AVG, 20.f, 0.05 * 0.05, 5, 50, 200, 0.2, 30, ChineseWhispersClusteringMode.UPDATE_IMMEDIATELY_RANDOMIZED, false,
                MatchGroupWeightingMode.NO_PAIRS_ANTI_PROP, Preprocessing.CUMULATIVE_DISTRIBUTION_FUNCTION, true, ClusteringAlgorithm.SPECTRAL,
                InterClusterSimilarity.AVERAGE, 0.2, 0.5);
    }

    /**
     * Returns a copy of this ClusteringOptions with a different similarity metric.
     * @param similarityMetric the new similarity metric
     * @return a new ClusteringOptions instance with the updated metric
     */
    public ClusteringOptions withSimilarityMetric(SimilarityMetric similarityMetric) {
        return new ClusteringOptions(similarityMetric, spectralKernelBandwidth, spectralGaussianProcessVariance, spectralMinRuns, spectralMaxRuns,
                spectralMaxKMeansIterationPerRun, agglomerativeThreshold, chineseWhispersMaxIterations, chineseWhispersClusteringMode,
                useAdvancedSimilarityPreprocessing, matchGroupWeightingMode, preprocessor, enabled, algorithm, agglomerativeInterClusterSimilarity,
                preprocessorThreshold, preprocessorPercentile);
    }

    /**
     * Returns a copy of this ClusteringOptions with a different spectral kernel bandwidth.
     * @param spectralKernelBandwidth the new kernel bandwidth value
     * @return a new ClusteringOptions instance with the updated bandwidth
     */
    public ClusteringOptions withSpectralKernelBandwidth(double spectralKernelBandwidth) {
        return new ClusteringOptions(similarityMetric, spectralKernelBandwidth, spectralGaussianProcessVariance, spectralMinRuns, spectralMaxRuns,
                spectralMaxKMeansIterationPerRun, agglomerativeThreshold, chineseWhispersMaxIterations, chineseWhispersClusteringMode,
                useAdvancedSimilarityPreprocessing, matchGroupWeightingMode, preprocessor, enabled, algorithm, agglomerativeInterClusterSimilarity,
                preprocessorThreshold, preprocessorPercentile);
    }

    /**
     * Returns a copy with an updated spectral Gaussian process variance.
     * @param spectralGaussianProcessVariance new variance value
     * @return new ClusteringOptions with the updated value
     */
    public ClusteringOptions withSpectralGaussianProcessVariance(double spectralGaussianProcessVariance) {
        return new ClusteringOptions(similarityMetric, spectralKernelBandwidth, spectralGaussianProcessVariance, spectralMinRuns, spectralMaxRuns,
                spectralMaxKMeansIterationPerRun, agglomerativeThreshold, chineseWhispersMaxIterations, chineseWhispersClusteringMode,
                useAdvancedSimilarityPreprocessing, matchGroupWeightingMode, preprocessor, enabled, algorithm, agglomerativeInterClusterSimilarity,
                preprocessorThreshold, preprocessorPercentile);
    }

    /**
     * Returns a copy with an updated minimum number of spectral clustering runs.
     * @param spectralMinRuns new minimum run count
     * @return new ClusteringOptions with the updated value
     */
    public ClusteringOptions withSpectralMinRuns(int spectralMinRuns) {
        return new ClusteringOptions(similarityMetric, spectralKernelBandwidth, spectralGaussianProcessVariance, spectralMinRuns, spectralMaxRuns,
                spectralMaxKMeansIterationPerRun, agglomerativeThreshold, chineseWhispersMaxIterations, chineseWhispersClusteringMode,
                useAdvancedSimilarityPreprocessing, matchGroupWeightingMode, preprocessor, enabled, algorithm, agglomerativeInterClusterSimilarity,
                preprocessorThreshold, preprocessorPercentile);
    }

    /**
     * Returns a copy with an updated maximum number of spectral clustering runs.
     * @param spectralMaxRuns new maximum run count
     * @return new ClusteringOptions with the updated value
     */
    public ClusteringOptions withSpectralMaxRuns(int spectralMaxRuns) {
        return new ClusteringOptions(similarityMetric, spectralKernelBandwidth, spectralGaussianProcessVariance, spectralMinRuns, spectralMaxRuns,
                spectralMaxKMeansIterationPerRun, agglomerativeThreshold, chineseWhispersMaxIterations, chineseWhispersClusteringMode,
                useAdvancedSimilarityPreprocessing, matchGroupWeightingMode, preprocessor, enabled, algorithm, agglomerativeInterClusterSimilarity,
                preprocessorThreshold, preprocessorPercentile);
    }

    /**
     * Returns a copy with an updated max iteration count for KMeans per spectral run.
     * @param spectralMaxKMeansIterationPerRun new iteration count
     * @return new ClusteringOptions with the updated value
     */
    public ClusteringOptions withSpectralMaxKMeansIterationPerRun(int spectralMaxKMeansIterationPerRun) {
        return new ClusteringOptions(similarityMetric, spectralKernelBandwidth, spectralGaussianProcessVariance, spectralMinRuns, spectralMaxRuns,
                spectralMaxKMeansIterationPerRun, agglomerativeThreshold, chineseWhispersMaxIterations, chineseWhispersClusteringMode,
                useAdvancedSimilarityPreprocessing, matchGroupWeightingMode, preprocessor, enabled, algorithm, agglomerativeInterClusterSimilarity,
                preprocessorThreshold, preprocessorPercentile);
    }

    /**
     * Returns a copy with an updated threshold for agglomerative clustering.
     * @param agglomerativeThreshold new threshold value
     * @return new ClusteringOptions with the updated value
     */
    public ClusteringOptions withAgglomerativeThreshold(double agglomerativeThreshold) {
        return new ClusteringOptions(similarityMetric, spectralKernelBandwidth, spectralGaussianProcessVariance, spectralMinRuns, spectralMaxRuns,
                spectralMaxKMeansIterationPerRun, agglomerativeThreshold, chineseWhispersMaxIterations, chineseWhispersClusteringMode,
                useAdvancedSimilarityPreprocessing, matchGroupWeightingMode, preprocessor, enabled, algorithm, agglomerativeInterClusterSimilarity,
                preprocessorThreshold, preprocessorPercentile);
    }

    /**
     * Returns a copy with an updated number of iterations for Chinese Whispers clustering.
     * @param chineseWhispersMaxIterations new maximum number of iterations
     * @return new ClusteringOptions with the updated value
     */
    public ClusteringOptions withChineseWhispersMaxIterations(int chineseWhispersMaxIterations) {
        return new ClusteringOptions(similarityMetric, spectralKernelBandwidth, spectralGaussianProcessVariance, spectralMinRuns, spectralMaxRuns,
                spectralMaxKMeansIterationPerRun, agglomerativeThreshold, chineseWhispersMaxIterations, chineseWhispersClusteringMode,
                useAdvancedSimilarityPreprocessing, matchGroupWeightingMode, preprocessor, enabled, algorithm, agglomerativeInterClusterSimilarity,
                preprocessorThreshold, preprocessorPercentile);
    }

    /**
     * Returns a copy with an updated clustering mode for Chinese Whispers clustering.
     * @param chineseWhispersClusteringMode new mode
     * @return new ClusteringOptions with the updated mode
     */
    public ClusteringOptions withChineseWhispersClusteringMode(ChineseWhispersClusteringMode chineseWhispersClusteringMode) {
        return new ClusteringOptions(similarityMetric, spectralKernelBandwidth, spectralGaussianProcessVariance, spectralMinRuns, spectralMaxRuns,
                spectralMaxKMeansIterationPerRun, agglomerativeThreshold, chineseWhispersMaxIterations, chineseWhispersClusteringMode,
                useAdvancedSimilarityPreprocessing, matchGroupWeightingMode, preprocessor, enabled, algorithm, agglomerativeInterClusterSimilarity,
                preprocessorThreshold, preprocessorPercentile);
    }

    /**
     * Returns a copy with an updated similarity preprocessing strategy.
     * @param useAdvancedSimilarityPreprocessing whether advanced similarity preprocessing should be used
     * @return new ClusteringOptions with the updated strategy
     */
    public ClusteringOptions withUseAdvancedSimilarityPreprocessing(boolean useAdvancedSimilarityPreprocessing) {
        return new ClusteringOptions(similarityMetric, spectralKernelBandwidth, spectralGaussianProcessVariance, spectralMinRuns, spectralMaxRuns,
                spectralMaxKMeansIterationPerRun, agglomerativeThreshold, chineseWhispersMaxIterations, chineseWhispersClusteringMode,
                useAdvancedSimilarityPreprocessing, matchGroupWeightingMode, preprocessor, enabled, algorithm, agglomerativeInterClusterSimilarity,
                preprocessorThreshold, preprocessorPercentile);
    }

    /**
     * Returns a copy with an updated weighting mode.
     * @param matchGroupWeightingMode new mode
     * @return new ClusteringOptions with the updated mode
     */
    public ClusteringOptions withMatchGroupWeightingMode(MatchGroupWeightingMode matchGroupWeightingMode) {
        return new ClusteringOptions(similarityMetric, spectralKernelBandwidth, spectralGaussianProcessVariance, spectralMinRuns, spectralMaxRuns,
                spectralMaxKMeansIterationPerRun, agglomerativeThreshold, chineseWhispersMaxIterations, chineseWhispersClusteringMode,
                useAdvancedSimilarityPreprocessing, matchGroupWeightingMode, preprocessor, enabled, algorithm, agglomerativeInterClusterSimilarity,
                preprocessorThreshold, preprocessorPercentile);
    }

    /**
     * Returns a copy with a different preprocessing strategy.
     * @param preprocessor new preprocessor to apply
     * @return new ClusteringOptions with the updated preprocessor
     */
    public ClusteringOptions withPreprocessor(Preprocessing preprocessor) {
        return new ClusteringOptions(similarityMetric, spectralKernelBandwidth, spectralGaussianProcessVariance, spectralMinRuns, spectralMaxRuns,
                spectralMaxKMeansIterationPerRun, agglomerativeThreshold, chineseWhispersMaxIterations, chineseWhispersClusteringMode,
                useAdvancedSimilarityPreprocessing, matchGroupWeightingMode, preprocessor, enabled, algorithm, agglomerativeInterClusterSimilarity,
                preprocessorThreshold, preprocessorPercentile);
    }

    /**
     * Returns a copy with updated clustering enabled state.
     * @param enabled whether clustering should be enabled
     * @return new ClusteringOptions with the updated state
     */
    public ClusteringOptions withEnabled(boolean enabled) {
        return new ClusteringOptions(similarityMetric, spectralKernelBandwidth, spectralGaussianProcessVariance, spectralMinRuns, spectralMaxRuns,
                spectralMaxKMeansIterationPerRun, agglomerativeThreshold, chineseWhispersMaxIterations, chineseWhispersClusteringMode,
                useAdvancedSimilarityPreprocessing, matchGroupWeightingMode, preprocessor, enabled, algorithm, agglomerativeInterClusterSimilarity,
                preprocessorThreshold, preprocessorPercentile);
    }

    /**
     * Returns a copy with a different clustering algorithm.
     * @param algorithm new algorithm to use
     * @return new ClusteringOptions with the updated algorithm
     */
    public ClusteringOptions withAlgorithm(ClusteringAlgorithm algorithm) {
        return new ClusteringOptions(similarityMetric, spectralKernelBandwidth, spectralGaussianProcessVariance, spectralMinRuns, spectralMaxRuns,
                spectralMaxKMeansIterationPerRun, agglomerativeThreshold, chineseWhispersMaxIterations, chineseWhispersClusteringMode,
                useAdvancedSimilarityPreprocessing, matchGroupWeightingMode, preprocessor, enabled, algorithm, agglomerativeInterClusterSimilarity,
                preprocessorThreshold, preprocessorPercentile);
    }

    /**
     * Returns a copy with a different inter-cluster similarity strategy for agglomerative clustering.
     * @param agglomerativeInterClusterSimilarity new similarity strategy
     * @return new ClusteringOptions with the updated strategy
     */
    public ClusteringOptions withAgglomerativeInterClusterSimilarity(InterClusterSimilarity agglomerativeInterClusterSimilarity) {
        return new ClusteringOptions(similarityMetric, spectralKernelBandwidth, spectralGaussianProcessVariance, spectralMinRuns, spectralMaxRuns,
                spectralMaxKMeansIterationPerRun, agglomerativeThreshold, chineseWhispersMaxIterations, chineseWhispersClusteringMode,
                useAdvancedSimilarityPreprocessing, matchGroupWeightingMode, preprocessor, enabled, algorithm, agglomerativeInterClusterSimilarity,
                preprocessorThreshold, preprocessorPercentile);
    }

    /**
     * Returns a copy with a different threshold used by the preprocessor.
     * @param preprocessorThreshold new threshold value
     * @return new ClusteringOptions with the updated threshold
     */
    public ClusteringOptions withPreprocessorThreshold(double preprocessorThreshold) {
        return new ClusteringOptions(similarityMetric, spectralKernelBandwidth, spectralGaussianProcessVariance, spectralMinRuns, spectralMaxRuns,
                spectralMaxKMeansIterationPerRun, agglomerativeThreshold, chineseWhispersMaxIterations, chineseWhispersClusteringMode,
                useAdvancedSimilarityPreprocessing, matchGroupWeightingMode, preprocessor, enabled, algorithm, agglomerativeInterClusterSimilarity,
                preprocessorThreshold, preprocessorPercentile);
    }

    /**
     * Returns a copy with a different percentile used by the preprocessor.
     * @param preprocessorPercentile new percentile value
     * @return new ClusteringOptions with the updated percentile
     */
    public ClusteringOptions withPreprocessorPercentile(double preprocessorPercentile) {
        return new ClusteringOptions(similarityMetric, spectralKernelBandwidth, spectralGaussianProcessVariance, spectralMinRuns, spectralMaxRuns,
                spectralMaxKMeansIterationPerRun, agglomerativeThreshold, chineseWhispersMaxIterations, chineseWhispersClusteringMode,
                useAdvancedSimilarityPreprocessing, matchGroupWeightingMode, preprocessor, enabled, algorithm, agglomerativeInterClusterSimilarity,
                preprocessorThreshold, preprocessorPercentile);
    }
}
