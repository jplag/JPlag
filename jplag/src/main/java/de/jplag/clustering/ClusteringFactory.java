package de.jplag.clustering;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import de.jplag.JPlagComparison;
import de.jplag.Submission;
import de.jplag.clustering.algorithm.GenericClusteringAlgorithm;

/**
 * Runs the clustering according to an options object.
 */
public class ClusteringFactory {
    public static List<ClusteringResult<Submission>> getClusterings(Collection<JPlagComparison> comparisons, ClusteringOptions options) {
        if (!options.isEnabled())
            return Collections.emptyList();

        // init algorithm
        GenericClusteringAlgorithm clusteringAlgorithm = options.getAlgorithm().create(options);

        // init preprocessor
        Optional<ClusteringPreprocessor> preprocessor = options.getPreprocessor().constructPreprocessor(options);

        if (preprocessor.isPresent()) {
            // Package preprocessor into a clustering algorithm
            clusteringAlgorithm = new PreprocessedClusteringAlgorithm(clusteringAlgorithm, preprocessor.orElseThrow());
        }

        // init adapter
        ClusteringAdapter adapter = new ClusteringAdapter(comparisons, options.getSimilarityMetric());

        // run clustering
        ClusteringResult<Submission> result = adapter.doClustering(clusteringAlgorithm);

        // remove bad clusters
        result = removeBadClusters(result);

        return List.of(result);
    }

    private static ClusteringResult<Submission> removeBadClusters(final ClusteringResult<Submission> clustering) {
        List<Cluster<Submission>> filtered = clustering.getClusters().stream().filter(cluster -> !cluster.isBadCluster())
                .collect(Collectors.toList());
        return new ClusteringResult<>(filtered, clustering.getCommunityStrength());
    }
}
