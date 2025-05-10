
package de.jplag.clustering;

import java.util.Objects;

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

    public ClusteringOptions() {
        this(SimilarityMetric.AVG, 20.f, 0.05 * 0.05, 5, 50, 200, 0.2, Preprocessing.CUMULATIVE_DISTRIBUTION_FUNCTION, true,
                ClusteringAlgorithm.SPECTRAL, InterClusterSimilarity.AVERAGE, 0.2, 0.5);
    }

    public ClusteringOptions withSimilarityMetric(SimilarityMetric similarityMetric) {
        return new ClusteringOptions(similarityMetric, spectralKernelBandwidth, spectralGaussianProcessVariance, spectralMinRuns, spectralMaxRuns,
                spectralMaxKMeansIterationPerRun, agglomerativeThreshold, preprocessor, enabled, algorithm, agglomerativeInterClusterSimilarity,
                preprocessorThreshold, preprocessorPercentile);
    }

    public ClusteringOptions withSpectralKernelBandwidth(double spectralKernelBandwidth) {
        return new ClusteringOptions(similarityMetric, spectralKernelBandwidth, spectralGaussianProcessVariance, spectralMinRuns, spectralMaxRuns,
                spectralMaxKMeansIterationPerRun, agglomerativeThreshold, preprocessor, enabled, algorithm, agglomerativeInterClusterSimilarity,
                preprocessorThreshold, preprocessorPercentile);
    }

    public ClusteringOptions withSpectralGaussianProcessVariance(double spectralGaussianProcessVariance) {
        return new ClusteringOptions(similarityMetric, spectralKernelBandwidth, spectralGaussianProcessVariance, spectralMinRuns, spectralMaxRuns,
                spectralMaxKMeansIterationPerRun, agglomerativeThreshold, preprocessor, enabled, algorithm, agglomerativeInterClusterSimilarity,
                preprocessorThreshold, preprocessorPercentile);
    }

    public ClusteringOptions withSpectralMinRuns(int spectralMinRuns) {
        return new ClusteringOptions(similarityMetric, spectralKernelBandwidth, spectralGaussianProcessVariance, spectralMinRuns, spectralMaxRuns,
                spectralMaxKMeansIterationPerRun, agglomerativeThreshold, preprocessor, enabled, algorithm, agglomerativeInterClusterSimilarity,
                preprocessorThreshold, preprocessorPercentile);
    }

    public ClusteringOptions withSpectralMaxRuns(int spectralMaxRuns) {
        return new ClusteringOptions(similarityMetric, spectralKernelBandwidth, spectralGaussianProcessVariance, spectralMinRuns, spectralMaxRuns,
                spectralMaxKMeansIterationPerRun, agglomerativeThreshold, preprocessor, enabled, algorithm, agglomerativeInterClusterSimilarity,
                preprocessorThreshold, preprocessorPercentile);
    }

    public ClusteringOptions withSpectralMaxKMeansIterationPerRun(int spectralMaxKMeansIterationPerRun) {
        return new ClusteringOptions(similarityMetric, spectralKernelBandwidth, spectralGaussianProcessVariance, spectralMinRuns, spectralMaxRuns,
                spectralMaxKMeansIterationPerRun, agglomerativeThreshold, preprocessor, enabled, algorithm, agglomerativeInterClusterSimilarity,
                preprocessorThreshold, preprocessorPercentile);
    }

    public ClusteringOptions withAgglomerativeThreshold(double agglomerativeThreshold) {
        return new ClusteringOptions(similarityMetric, spectralKernelBandwidth, spectralGaussianProcessVariance, spectralMinRuns, spectralMaxRuns,
                spectralMaxKMeansIterationPerRun, agglomerativeThreshold, preprocessor, enabled, algorithm, agglomerativeInterClusterSimilarity,
                preprocessorThreshold, preprocessorPercentile);
    }

    public ClusteringOptions withPreprocessor(Preprocessing preprocessor) {
        return new ClusteringOptions(similarityMetric, spectralKernelBandwidth, spectralGaussianProcessVariance, spectralMinRuns, spectralMaxRuns,
                spectralMaxKMeansIterationPerRun, agglomerativeThreshold, preprocessor, enabled, algorithm, agglomerativeInterClusterSimilarity,
                preprocessorThreshold, preprocessorPercentile);
    }

    public ClusteringOptions withEnabled(boolean enabled) {
        return new ClusteringOptions(similarityMetric, spectralKernelBandwidth, spectralGaussianProcessVariance, spectralMinRuns, spectralMaxRuns,
                spectralMaxKMeansIterationPerRun, agglomerativeThreshold, preprocessor, enabled, algorithm, agglomerativeInterClusterSimilarity,
                preprocessorThreshold, preprocessorPercentile);
    }

    public ClusteringOptions withAlgorithm(ClusteringAlgorithm algorithm) {
        return new ClusteringOptions(similarityMetric, spectralKernelBandwidth, spectralGaussianProcessVariance, spectralMinRuns, spectralMaxRuns,
                spectralMaxKMeansIterationPerRun, agglomerativeThreshold, preprocessor, enabled, algorithm, agglomerativeInterClusterSimilarity,
                preprocessorThreshold, preprocessorPercentile);
    }

    public ClusteringOptions withAgglomerativeInterClusterSimilarity(InterClusterSimilarity agglomerativeInterClusterSimilarity) {
        return new ClusteringOptions(similarityMetric, spectralKernelBandwidth, spectralGaussianProcessVariance, spectralMinRuns, spectralMaxRuns,
                spectralMaxKMeansIterationPerRun, agglomerativeThreshold, preprocessor, enabled, algorithm, agglomerativeInterClusterSimilarity,
                preprocessorThreshold, preprocessorPercentile);
    }

    public ClusteringOptions withPreprocessorThreshold(double preprocessorThreshold) {
        return new ClusteringOptions(similarityMetric, spectralKernelBandwidth, spectralGaussianProcessVariance, spectralMinRuns, spectralMaxRuns,
                spectralMaxKMeansIterationPerRun, agglomerativeThreshold, preprocessor, enabled, algorithm, agglomerativeInterClusterSimilarity,
                preprocessorThreshold, preprocessorPercentile);
    }

    public ClusteringOptions withPreprocessorPercentile(double preprocessorPercentile) {
        return new ClusteringOptions(similarityMetric, spectralKernelBandwidth, spectralGaussianProcessVariance, spectralMinRuns, spectralMaxRuns,
                spectralMaxKMeansIterationPerRun, agglomerativeThreshold, preprocessor, enabled, algorithm, agglomerativeInterClusterSimilarity,
                preprocessorThreshold, preprocessorPercentile);
    }
}
