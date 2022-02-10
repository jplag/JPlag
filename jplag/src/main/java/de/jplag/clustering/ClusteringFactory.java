package de.jplag.clustering;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import de.jplag.JPlagComparison;
import de.jplag.Submission;
import de.jplag.clustering.algorithm.ClusteringAlgorithm;
import de.jplag.clustering.algorithm.SpectralClustering;
import de.jplag.clustering.algorithm.TopDownHierarchicalClustering;
import de.jplag.clustering.preprocessors.CdfPreprocessor;
import de.jplag.clustering.preprocessors.PercentileThresholdProcessor;
import de.jplag.clustering.preprocessors.ThresholdPreprocessor;
import de.jplag.options.JPlagOptions;

/**
 * Produces the clustering according to the options.
 */
public class ClusteringFactory {
    public static List<ClusteringResult<Submission>> getClusterings(Collection<JPlagComparison> comparisons, JPlagOptions options) {
        if (!options.isClustering()) return Collections.emptyList();

        // init algorithm
        ClusteringAlgorithm ca = null;
        if (options.getClusteringAlgorithm() == Algorithms.AGGLOMERATIVE) {
            TopDownHierarchicalClustering.ClusteringOptions clusteringOptions = new TopDownHierarchicalClustering.ClusteringOptions();
            clusteringOptions.minimalSimilarity = options.getClusteringAgglomerativeThreshold();
            clusteringOptions.similarity = options.getClusteringAgglomerativeInterClusterSimilarity();
            ca = new TopDownHierarchicalClustering(clusteringOptions);
        } else if (options.getClusteringAlgorithm() == Algorithms.SPECTRAL) {
            SpectralClustering.ClusteringOptions clusteringOptions = new SpectralClustering.ClusteringOptions();
            clusteringOptions.GPVariance = options.getClusteringSpectralGPVariance();
            clusteringOptions.kernelBandwidth = options.getClusteringSpectralKernelBandwidth();
            clusteringOptions.maxKMeansIterations = options.getClusteringSpectralMaxKMeansIterationPerRun();
            clusteringOptions.maxRuns = options.getClusteringSpectralMaxRuns();
            clusteringOptions.minRuns = options.getClusteringSpectralMinRuns();
            ca = new SpectralClustering(clusteringOptions);
        }

        // init preprocessor
        Preprocessor preprocessor;
        switch (options.getClusteringPreprocessor()) {
            case CDF:
                preprocessor = new CdfPreprocessor();
                break;
            case THRESHOLD:
                preprocessor = new ThresholdPreprocessor(options.getClusteringPreprocessorThreshold());
                break;
            case PERCENTILE:
                preprocessor = new PercentileThresholdProcessor(options.getClusteringPreprocessorPercentile());
                break;
            case NONE:
            default:
                preprocessor = null;
                break;
        }
        if (preprocessor != null) {
            // Package preprocessor into a clustering algorithm
            ca = new Preprocessor.PreprocessedClusteringAlgorithm(ca, preprocessor);
        }

        // init adapter
        ClusteringAdapter adapter = new ClusteringAdapter(comparisons, options.getClusteringSimilarityMetric());

        // run clustering
        ClusteringResult<Submission> result = adapter.doClustering(ca);

        // remove bad clusters
        result = removeBadClusters(result);
        
        return List.of(result);
    }

    private static ClusteringResult<Submission> removeBadClusters(final ClusteringResult<Submission> clustering) {
        return new ClusteringResult<>() {

            @Override
            public Collection<Cluster<Submission>> getClusters() {
                return clustering.getClusters().stream()
                    .filter(cluster -> !cluster.isBadCluster())
                    .collect(Collectors.toList());
            }

            @Override
            public float getCommunityStrength() {
                // Do not lie by letting the clustering improve by wiping away the bad part...
                return clustering.getCommunityStrength();
            }

            @Override
            public int size() {
                return getClusters().size();
            }
        };
    }
}
