package de.jplag.reporting.reportobject.mapper;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;

import de.jplag.JPlagResult;
import de.jplag.Submission;
import de.jplag.clustering.ClusteringResult;
import de.jplag.reporting.reportobject.model.Cluster;

/**
 * Extracts and maps the clusters from the JPlagResult to the corresponding JSON DTO
 */
public class ClusteringResultMapper {
    private final Function<Submission, String> submissionToIdFunction;

    public ClusteringResultMapper(Function<Submission, String> submissionToIdFunction) {
        this.submissionToIdFunction = submissionToIdFunction;
    }

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
