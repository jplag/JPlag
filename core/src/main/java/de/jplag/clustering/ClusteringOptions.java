
package de.jplag.clustering;

import de.jplag.clustering.algorithm.InterClusterSimilarity;
import de.jplag.options.SimilarityMetric;

/**
 * Collection of all possible parameters that describe how a clustering should be performed.
 */
public class ClusteringOptions {

    public static final ClusteringOptions DEFAULTS = new Builder().build();

    private final SimilarityMetric similarityMetric;
    private final float spectralKernelBandwidth;
    private final float spectralGaussianProcessVariance;
    private final int spectralMinRuns;
    private final int spectralMaxRuns;
    private final int spectralMaxKMeansIterationPerRun;
    private final float agglomerativeThreshold;
    private final Preprocessing preprocessor;
    private final boolean enabled;
    private final ClusteringAlgorithm algorithm;
    private final InterClusterSimilarity agglomerativeInterClusterSimilarity;
    private final float preprocessorThreshold;
    private final float preprocessorPercentile;

    /**
     * @return The similarity metric is used for clustering
     */
    public SimilarityMetric getSimilarityMetric() {
        return similarityMetric;
    }

    /**
     * The kernel bandwidth for the matern kernel used in the gaussian process for the automatic search for the number of
     * clusters in spectral clustering. Affects the runtime and results of the spectral clustering.
     * @return kernel bandwidth for spectral clustering
     */
    public float getSpectralKernelBandwidth() {
        return spectralKernelBandwidth;
    }

    /**
     * This is the assumed level of noise in the evaluation results of a spectral clustering. Acts as normalization
     * parameter for the Gaussian Process. The default setting works well with similarity scores in the range between zero
     * and one. Affects the results of the spectral clustering.
     * @return assumed variance of noise in Gaussian Process during spectral clustering.
     */
    public float getSpectralGaussianProcessVariance() {
        return spectralGaussianProcessVariance;
    }

    /**
     * The minimal number of times the kMeans algorithm is run for the spectral clustering. These runs will use predefined
     * numbers of clusters and will not use the bayesian optimization to determine the number of clusters.
     * @return minimal kMeans runs during spectral clustering
     */
    public int getSpectralMinRuns() {
        return spectralMinRuns;
    }

    /**
     * The maximal number of times the kMeans algorithm is run during the bayesian optimization for spectral clustering. The
     * bayesian optimization may be stopped before, when no more maxima of the acquisition-function are found.
     * @return maximal kMeans runs during spectral clustering
     */
    public int getSpectralMaxRuns() {
        return spectralMaxRuns;
    }

    /**
     * Maximum number of iterations of the kMeans clustering per run during spectral clustering.
     * @return maximal kMeans iterations
     */
    public int getSpectralMaxKMeansIterationPerRun() {
        return spectralMaxKMeansIterationPerRun;
    }

    /**
     * Agglomerative clustering will merge clusters that have a similarity higher than this threshold.
     * @return merging threshold for agglomerative clustering
     */
    public float getAgglomerativeThreshold() {
        return agglomerativeThreshold;
    }

    /**
     * Preprocessing for the similarity values before clustering. Preprocessing is mandatory for spectral clustering and
     * optional for agglomerative clustering.
     * @return preprocessor
     */
    public Preprocessing getPreprocessor() {
        return preprocessor;
    }

    /**
     * @return whether clustering should be performed
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * @return the clustering algorithm to use
     */
    public ClusteringAlgorithm getAlgorithm() {
        return algorithm;
    }

    /**
     * Similarity measure between clusters in agglomerative clustering.
     * @return similarity measure
     */
    public InterClusterSimilarity getAgglomerativeInterClusterSimilarity() {
        return agglomerativeInterClusterSimilarity;
    }

    /**
     * @return up to which similarity the threshold-preprocessor zeroes out the similarities
     */
    public float getPreprocessorThreshold() {
        return preprocessorThreshold;
    }

    /**
     * @return up to which percentile of similarities the percentile-preprocessor zeroes out the similarities
     */
    public float getPreprocessorPercentile() {
        return preprocessorPercentile;
    }

    public static class Builder {

        private SimilarityMetric similarityMetric;
        private float spectralKernelBandwidth;
        private float spectralGaussianProcessVariance;
        private int spectralMinRuns;
        private int spectralMaxRuns;
        private int spectralMaxKMeansIterationPerRun;
        private float agglomerativeThreshold;
        private Preprocessing preprocessor;
        private boolean enabled;
        private ClusteringAlgorithm algorithm;
        private InterClusterSimilarity agglomerativeInterClusterSimilarity;
        private float preprocessorThreshold;
        private float preprocessorPercentile;

        public Builder() {
            // Setting the defaults here
            similarityMetric(SimilarityMetric.MAX);
            spectralKernelBandwidth(20.f);
            spectralGaussianProcessVariance(0.05f * 0.05f);
            spectralMinRuns(5);
            spectralMaxRuns(50);
            spectralMaxKMeansIterationPerRun(200);
            agglomerativeThreshold(0.2f);
            preprocessor(Preprocessing.CUMULATIVE_DISTRIBUTION_FUNCTION);
            enabled(true);
            algorithm(ClusteringAlgorithm.SPECTRAL);
            agglomerativeInterClusterSimilarity(InterClusterSimilarity.AVERAGE);
            preprocessorThreshold(0.2f);
            preprocessorPercentile(0.5f);
        }

        public Builder similarityMetric(SimilarityMetric similarityMetric) {
            this.similarityMetric = similarityMetric;
            return Builder.this;
        }

        public Builder spectralKernelBandwidth(float spectralKernelBandwidth) {
            this.spectralKernelBandwidth = spectralKernelBandwidth;
            return Builder.this;
        }

        public Builder spectralGaussianProcessVariance(float spectralGPVariance) {
            this.spectralGaussianProcessVariance = spectralGPVariance;
            return Builder.this;
        }

        public Builder spectralMinRuns(int spectralMinRuns) {
            this.spectralMinRuns = spectralMinRuns;
            return Builder.this;
        }

        public Builder spectralMaxRuns(int spectralMaxRuns) {
            this.spectralMaxRuns = spectralMaxRuns;
            return Builder.this;
        }

        public Builder spectralMaxKMeansIterationPerRun(int spectralMaxKMeansIterationPerRun) {
            this.spectralMaxKMeansIterationPerRun = spectralMaxKMeansIterationPerRun;
            return Builder.this;
        }

        public Builder agglomerativeThreshold(float agglomerativeThreshold) {
            this.agglomerativeThreshold = agglomerativeThreshold;
            return Builder.this;
        }

        public Builder preprocessor(Preprocessing preprocessor) {
            this.preprocessor = preprocessor;
            return Builder.this;
        }

        public Builder enabled(boolean enabled) {
            this.enabled = enabled;
            return Builder.this;
        }

        public Builder algorithm(ClusteringAlgorithm algorithm) {
            this.algorithm = algorithm;
            return Builder.this;
        }

        public Builder agglomerativeInterClusterSimilarity(InterClusterSimilarity agglomerativeInterClusterSimilarity) {
            this.agglomerativeInterClusterSimilarity = agglomerativeInterClusterSimilarity;
            return Builder.this;
        }

        public Builder preprocessorThreshold(float preprocessorThreshold) {
            this.preprocessorThreshold = preprocessorThreshold;
            return Builder.this;
        }

        public Builder preprocessorPercentile(float preprocessorPercentile) {
            this.preprocessorPercentile = preprocessorPercentile;
            return Builder.this;
        }

        public ClusteringOptions build() {

            return new ClusteringOptions(this);
        }
    }

    private ClusteringOptions(Builder builder) {
        this.similarityMetric = builder.similarityMetric;
        this.spectralKernelBandwidth = builder.spectralKernelBandwidth;
        this.spectralGaussianProcessVariance = builder.spectralGaussianProcessVariance;
        this.spectralMinRuns = builder.spectralMinRuns;
        this.spectralMaxRuns = builder.spectralMaxRuns;
        this.spectralMaxKMeansIterationPerRun = builder.spectralMaxKMeansIterationPerRun;
        this.agglomerativeThreshold = builder.agglomerativeThreshold;
        this.preprocessor = builder.preprocessor;
        this.enabled = builder.enabled;
        this.algorithm = builder.algorithm;
        this.agglomerativeInterClusterSimilarity = builder.agglomerativeInterClusterSimilarity;
        this.preprocessorThreshold = builder.preprocessorThreshold;
        this.preprocessorPercentile = builder.preprocessorPercentile;
    }

}
