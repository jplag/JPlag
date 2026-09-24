package de.jplag.clustering;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jplag.JPlagComparison;
import de.jplag.Submission;
import de.jplag.clustering.algorithm.GenericClusteringAlgorithm;
import de.jplag.logging.ProgressBar;
import de.jplag.logging.ProgressBarLogger;
import de.jplag.logging.ProgressBarType;

/**
 * Runs the clustering according to an options object.
 */
public class ClusteringFactory {
    private static final int MAX_LOGGED_MEMBERS = 5;
    private static final String STRENGTH_FORMAT = "%,.5f";
    private static final String SIMILARITY_FORMAT = "%,.1f%%";
    private static final String CLOSING_BRACKET = "]";
    private static final String DOTS = "...";

    private static final String CLUSTER_PATTERN = "avg similarity: {}, strength: {}, {} members: {}";
    private static final String CLUSTERING_RESULT = "{} clusters were found:";
    private static final String CLUSTERING_PARAMETERS = "Calculating clusters via {} clustering with {} pre-processing...";
    private static final String CLUSTERING_DISABLED = "Cluster calculation disabled (as requested)!";

    private static final Logger logger = LoggerFactory.getLogger(ClusteringFactory.class);

    private ClusteringFactory() {
        // private constructor for non-instantiability.
    }

    /**
     * Performs clustering on the given comparisons using the specified options.
     * @param comparisons the collection of comparisons to cluster.
     * @param options the clustering configuration.
     * @return a list containing the clustering result, or an empty list if clustering is disabled or input is empty.
     */
    public static List<ClusteringResult<Submission>> getClusterings(Collection<JPlagComparison> comparisons, ClusteringOptions options) {
        if (comparisons.isEmpty()) {
            return Collections.emptyList();
        }

        if (!options.enabled()) {
            logger.warn(CLUSTERING_DISABLED);
            return Collections.emptyList();
        }
        logger.info(CLUSTERING_PARAMETERS, options.algorithm(), options.preprocessor());

        ProgressBar progressBar = ProgressBarLogger.createProgressBar(ProgressBarType.CLUSTERING, 0);

        // init algorithm
        GenericClusteringAlgorithm clusteringAlgorithm = options.algorithm().create(options);

        // init preprocessor
        Optional<ClusteringPreprocessor> preprocessor = options.preprocessor().constructPreprocessor(options);

        if (preprocessor.isPresent()) {
            // Package preprocessor into a clustering algorithm
            clusteringAlgorithm = new PreprocessedClusteringAlgorithm(clusteringAlgorithm, preprocessor.orElseThrow());
        }

        // init adapter
        ClusteringAdapter adapter = new ClusteringAdapter(comparisons, options.similarityMetric());

        // run clustering
        ClusteringResult<Submission> result = adapter.doClustering(clusteringAlgorithm);

        // remove bad clusters
        result = removeBadClusters(result);

        progressBar.dispose();
        logClusters(result);

        return List.of(result);
    }

    private static ClusteringResult<Submission> removeBadClusters(final ClusteringResult<Submission> clustering) {
        List<Cluster<Submission>> filtered = clustering.getClusters().stream().filter(cluster -> !cluster.isBadCluster()).toList();
        return new ClusteringResult<>(filtered, clustering.getCommunityStrength());
    }

    private static void logClusters(ClusteringResult<Submission> result) {
        var clusters = new ArrayList<>(result.getClusters());
        clusters.sort(Comparator.comparing(Cluster<Submission>::getAverageSimilarity).reversed());
        logger.trace(CLUSTERING_RESULT, clusters.size());
        clusters.forEach(ClusteringFactory::logCluster);
    }

    private static void logCluster(Cluster<Submission> cluster) {
        String members = membersToString(cluster.getMembers());
        String similarity = String.format(SIMILARITY_FORMAT, cluster.getAverageSimilarity() * 100);
        String strength = String.format(STRENGTH_FORMAT, cluster.getCommunityStrength());
        logger.trace(CLUSTER_PATTERN, similarity, strength, cluster.getMembers().size(), members);
    }

    private static String membersToString(Collection<Submission> members) {
        if (members.size() > MAX_LOGGED_MEMBERS) {
            return List.copyOf(members).subList(0, MAX_LOGGED_MEMBERS).toString().replace(CLOSING_BRACKET, DOTS);
        }
        return members.toString();
    }
}
