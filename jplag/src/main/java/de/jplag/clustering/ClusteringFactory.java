package de.jplag.clustering;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jplag.JPlagComparison;
import de.jplag.Submission;
import de.jplag.clustering.algorithm.GenericClusteringAlgorithm;

/**
 * Runs the clustering according to an options object.
 */
public class ClusteringFactory {
    private static final String CLUSTER_INFORMATION = "Calculating clusters via {} clustering with {} pre-processing...";
    private static final String NO_CLUSTERS = "Cluster calculation disabled (as requested)!";
    private static final Logger logger = LoggerFactory.getLogger(ClusteringFactory.class);

    public static List<ClusteringResult<Submission>> getClusterings(Collection<JPlagComparison> comparisons, ClusteringOptions options) {
        if (!options.isEnabled()) {
            logger.warn(NO_CLUSTERS);
            return Collections.emptyList();
        } else {
            logger.info(CLUSTER_INFORMATION, options.getAlgorithm(), options.getPreprocessor());
        }

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
        List<Cluster<Submission>> filtered = clustering.getClusters().stream().filter(cluster -> !cluster.isBadCluster()).toList();
        return new ClusteringResult<>(filtered, clustering.getCommunityStrength());
    }
}
