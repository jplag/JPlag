package de.jplag.clustering;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.BiFunction;

/**
 * Cluster part of a {@link ClusteringResult}.
 * @param <T> type of the clusters members
 */
public class Cluster<T> {

    private final double communityStrength;
    private final Collection<T> members;
    private ClusteringResult<T> clusteringResult = null;
    private final double averageSimilarity;

    /**
     * @param members Members of the cluster.
     * @param communityStrength A metric of how strongly the members of this cluster are connected.
     * @param averageSimilarity The average similarity between all tuple comparisons of the members in this cluster.
     */
    public Cluster(Collection<T> members, double communityStrength, double averageSimilarity) {
        this.members = new ArrayList<>(members);
        this.communityStrength = communityStrength;
        this.averageSimilarity = averageSimilarity;
    }

    /**
     * @return a view on the members of this cluster.
     */
    public Collection<T> getMembers() {
        return new ArrayList<>(members);
    }

    /**
     * @return average similarity between all tuple comparisons of the members in this cluster.
     */
    public double getAverageSimilarity() {
        return averageSimilarity;
    }

    /**
     * See {@link ClusteringResult#getCommunityStrength}
     * @return community strength of the cluster
     */
    public double getCommunityStrength() {
        return communityStrength;
    }

    /**
     * Sets this clusters clustering result. Should only be called by classes extending {@link ClusteringResult} on their
     * own clusters.
     * @param clusteringResult the clustering result
     */
    public void setClusteringResult(ClusteringResult<T> clusteringResult) {
        this.clusteringResult = clusteringResult;
    }

    /**
     * @return How much each member of this cluster contributes to the {@link ClusteringResult#getCommunityStrength}.
     */
    public double getCommunityStrengthPerConnection() {
        int size = members.size();
        if (size < 2)
            return 0;
        return getCommunityStrength() / connections();
    }

    /**
     * Computes a normalized community strength per connection. Can be used as measure for strength of evidence in
     * comparison to other clusters in the same clustering. Guaranteed to be smaller than 1. Negative values indicate
     * non-clusters. This method may only be called on clusters that are part of a ClusteringResult.
     * @return normalized community strength per connection
     */
    public double getNormalizedCommunityStrengthPerConnection() {
        List<Cluster<T>> goodClusters = clusteringResult.getClusters().stream().filter(cluster -> cluster.getCommunityStrength() > 0).toList();
        double posCommunityStrengthSum = goodClusters.stream().mapToDouble(Cluster::getCommunityStrengthPerConnection).sum();

        int size = clusteringResult.getClusters().size();
        if (size < 2)
            return getCommunityStrengthPerConnection();
        return getCommunityStrengthPerConnection() / posCommunityStrengthSum;
    }

    /**
     * How much this cluster is worth during optimization.
     */
    public double getWorth(BiFunction<T, T, Double> similarity) {
        double communityStrength = getCommunityStrength();
        if (members.size() > 1) {
            communityStrength /= connections();
        }
        double averageSimilarity = averageSimilarity(similarity);
        return communityStrength * averageSimilarity;
    }

    /**
     * Computes the average similarity inside the cluster.
     * @param similarity function that supplies the similarity of two cluster members.
     * @return average similarity
     */
    private double averageSimilarity(BiFunction<T, T, Double> similarity) {
        List<T> members = new ArrayList<>(this.members);
        if (members.size() < 2) {
            return 1;
        }
        double similaritySum = 0;
        for (int i = 0; i < members.size(); i++) {
            for (int j = i + 1; j < members.size(); j++) {
                similaritySum += similarity.apply(members.get(i), members.get(j));
            }
        }
        return similaritySum / connections();
    }

    private int connections() {
        int size = members.size();
        return ((size - 1) * size) / 2;
    }

    /**
     * Whether this cluster is very uninformative or wrong and should be pruned as last step of the clustering process.
     * @return is bad
     */
    public boolean isBadCluster() {
        return members.size() < 2 || getCommunityStrength() < 0;
    }

}