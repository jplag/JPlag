
package de.jplag.clustering;

import de.jplag.clustering.algorithm.AgglomerativeClustering;
import de.jplag.options.SimilarityMetric;

public class ClusteringOptions {

    public static ClusteringOptions DEFAULTS = new Builder().build();

    private SimilarityMetric similarityMetric;
    private float spectralKernelBandwidth;
    private float spectralGPVariance;
    private int spectralMinRuns;
    private int spectralMaxRuns;
    private int spectralMaxKMeansIterationPerRun;
    private float agglomerativeThreshold;
    private Preprocessors preprocessor;
    private boolean enabled;
    private Algorithms algorithm;
    private AgglomerativeClustering.InterClusterSimilarity agglomerativeInterClusterSimilarity;
    private float preprocessorThreshold;
    private float preprocessorPercentile;

    public SimilarityMetric getSimilarityMetric() {
        return similarityMetric;
    }

    public float getSpectralKernelBandwidth() {
        return spectralKernelBandwidth;
    }

    public float getSpectralGPVariance() {
        return spectralGPVariance;
    }

    public int getSpectralMinRuns() {
        return spectralMinRuns;
    }

    public int getSpectralMaxRuns() {
        return spectralMaxRuns;
    }

    public int getSpectralMaxKMeansIterationPerRun() {
        return spectralMaxKMeansIterationPerRun;
    }

    public float getAgglomerativeThreshold() {
        return agglomerativeThreshold;
    }

    public Preprocessors getPreprocessor() {
        return preprocessor;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public Algorithms getAlgorithm() {
        return algorithm;
    }

    public AgglomerativeClustering.InterClusterSimilarity getAgglomerativeInterClusterSimilarity() {
        return agglomerativeInterClusterSimilarity;
    }

    public float getPreprocessorThreshold() {
        return preprocessorThreshold;
    }

    public float getPreprocessorPercentile() {
        return preprocessorPercentile;
    }

    public static class Builder {

        private SimilarityMetric similarityMetric;
        private float spectralKernelBandwidth;
        private float spectralGPVariance;
        private int spectralMinRuns;
        private int spectralMaxRuns;
        private int spectralMaxKMeansIterationPerRun;
        private float agglomerativeThreshold;
        private Preprocessors preprocessor;
        private boolean enabled;
        private Algorithms algorithm;
        private AgglomerativeClustering.InterClusterSimilarity agglomerativeInterClusterSimilarity;
        private float preprocessorThreshold;
        private float preprocessorPercentile;

        public Builder() {
            // Setting the defaults here
            similarityMetric(SimilarityMetric.MAX);
            spectralKernelBandwidth(20.f);
            spectralGPVariance(0.05f * 0.05f);
            spectralMinRuns(5);
            spectralMaxRuns(50);
            spectralMaxKMeansIterationPerRun(200);
            agglomerativeThreshold(0.2f);
            preprocessor(Preprocessors.CDF);
            enabled(true);
            algorithm(Algorithms.SPECTRAL);
            agglomerativeInterClusterSimilarity(AgglomerativeClustering.InterClusterSimilarity.AVERAGE);
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

        public Builder spectralGPVariance(float spectralGPVariance) {
            this.spectralGPVariance = spectralGPVariance;
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

        public Builder preprocessor(Preprocessors preprocessor) {
            this.preprocessor = preprocessor;
            return Builder.this;
        }

        public Builder enabled(boolean enabled) {
            this.enabled = enabled;
            return Builder.this;
        }

        public Builder algorithm(Algorithms algorithm) {
            this.algorithm = algorithm;
            return Builder.this;
        }

        public Builder agglomerativeInterClusterSimilarity(
                AgglomerativeClustering.InterClusterSimilarity agglomerativeInterClusterSimilarity) {
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
        this.spectralGPVariance = builder.spectralGPVariance;
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

    public void doSomething() {
        // do something
    }
}
