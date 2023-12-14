package de.jplag.reporting.reportobject.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ClusterOptionsReport(@JsonProperty("enabled") boolean enabled, @JsonProperty("metric") String clusterSimilarityMetric,
        @JsonProperty("spectral_bandwidth") double spectralBandwidth,
        @JsonProperty("spectral_gaussian_variance") double spectralGaussianProcessVariance, @JsonProperty("spectral_min_runs") int spectralMinRuns,
        @JsonProperty("spectral_max_runs") int spectralMaxRuns, @JsonProperty("spectral_max_kmeans_iterations") int spectralMaxKMeansIterations,
        @JsonProperty("agglomerative_threshold") double agglomerativeThreshold, @JsonProperty("preprocessor") String preprocessor,
        @JsonProperty("algorithm") String algorithm, @JsonProperty("inter_similarity") String interClusterSimilarity,
        @JsonProperty("preprocessor_threshold") double preprocessorThreshold,
        @JsonProperty("preprocessor_precentile") double preprocessorPercentile) {
}
