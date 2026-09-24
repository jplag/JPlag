package de.jplag.reporting.reportobject.mapper;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;

import de.jplag.JPlagResult;
import de.jplag.Submission;
import de.jplag.clustering.ClusteringResult;
import de.jplag.reporting.reportobject.model.Cluster;

/**
 * Extracts and maps the clusters from the JPlagResult to the corresponding JSON DTO.
 */
public class ClusteringResultMapper {
    private final Function<Submission, String> submissionToIdFunction;

    /**
     * Constructs a ClusteringResultMapper with a function to map submissions to their IDs.
     * @param submissionToIdFunction a function that converts a Submission to its ID string
     */
    public ClusteringResultMapper(Function<Submission, String> submissionToIdFunction) {
        this.submissionToIdFunction = submissionToIdFunction;
    }

    /**
     * Maps the clustering results from a JPlagResult to a list of JSON DTO Clusters.
     * @param result the JPlagResult containing clustering data
     * @return a list of mapped Cluster objects
     */
    public List<Cluster> map(JPlagResult result) {
        var clusteringResult = result.getClusteringResult();
        return clusteringResult.stream().map(ClusteringResult::getClusters).flatMap(Collection::stream).map(this::convertCluster).toList();
    }

    private Cluster convertCluster(de.jplag.clustering.Cluster<Submission> from) {
        var strength = from.getCommunityStrength();
        var avgSimilarity = from.getAverageSimilarity();
        var member = from.getMembers().stream().map(submissionToIdFunction).toList();
        return new Cluster(avgSimilarity, strength, member);
    }
}
