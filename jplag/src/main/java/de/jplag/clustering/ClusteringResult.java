package de.jplag.clustering;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public interface ClusteringResult<T> {

    Collection<Cluster<T>> getClusters();

    /**
     * Community strength of the clustering.
     * 
     * The expectation of community strength in a random graph is zero and it can not exceed one.
     * It's the sum of it's clusters {@link Cluster#getCommunityStrength}.  
     * 
     * If the underlying network is not changed, a higher community strength denotes a better clustering.
     * 
     * <p>
     * See:
     * Finding and evaluating community structure in networks,
     * M. E. J. Newman and M. Girvan,
     * Phys. Rev. E 69, 026113 – Published 26 February 2004,
     * Doi: 10.1103/PhysRevE.69.026113
     * </p>
     * 
     * It's called modularity in that paper.
     * @return community strength
     */
    float getCommunityStrength();

    int size();

    default float getWorth(BiFunction<T, T, Float> similarity) {
        return (float) getClusters().stream()
            .mapToDouble(c -> c.getWorth(similarity))
            .map(worth -> Double.isFinite(worth) ? worth : 0)
            .average()
            .getAsDouble();
    }
    
    public interface Cluster<T> {
        Collection<T> getMembers();

        /**
         * See {@link ClusteringResult#getCommunityStrength}
         * @return community strength of the cluster
         */
        float getCommunityStrength();

        ClusteringResult<T> getClusteringResult();

        /**
         * @return How much each member o f this cluster contributes to the {@link ClusteringResult#getCommunityStrength} 
         */
        default float getCommunityStrengthPerConnection() {
            int size = getMembers().size();
            if (size < 2) return 0;
            return getCommunityStrength() / connections();
        }

        /**
         * Computes a normalized community strength per connection.
         * Can be used as measure for strength of evidence in comparison to other clusters in the same clustering.
         * Guaranteed to be smaller than 1.
         * Negative values indicate non-clusters.
         * 
         * @return normalized community strength per connection
         */
        default float getNormalizedCommunityStrengthPerConnection() {
            List<Cluster<T>> goodClusters = getClusteringResult().getClusters().stream().filter(cluster -> cluster.getCommunityStrength() > 0).collect(Collectors.toList());
            float posCommunityStrengthSum = (float) goodClusters.stream().mapToDouble(Cluster::getCommunityStrengthPerConnection).sum();

            int size = getClusteringResult().size();
            if (size < 2) return getCommunityStrengthPerConnection();
            return getCommunityStrengthPerConnection() / posCommunityStrengthSum;
        }

        /**
         * How much this cluster is worth during optimization.
         */
        default double getWorth(BiFunction<T, T, Float> similarity) {
            double ncs = getCommunityStrength();
            if (getMembers().size() > 1) {
                ncs /= connections();
            }
            double avgSim = avgSimilarity(similarity);
            return ncs * avgSim;
        }

        /**
         * Computes the average similarity inside the cluster.
         * 
         * @param similarity function that supplies the similarity of two cluster members.
         * @return average similarity
         */
        default float avgSimilarity(BiFunction<T, T, Float> similarity) {
            List<T> members = new ArrayList<>(getMembers());
            if (members.size() < 2) {
                return 1;
            }
            float similaritySum = 0;
            for (int i = 0; i < members.size(); i++) {
                for (int j = i + 1; j < members.size(); j++) {
                    similaritySum += similarity.apply(members.get(i), members.get(j));
                }
            }
            return similaritySum / connections();
        }

        default int connections() {
            int size = getMembers().size();
            return ((size - 1) * size) / 2;
        }

        default boolean isBadCluster() {
            return getMembers().size() < 2 || getCommunityStrength() < 0;
        }
    }


    public static class DefaultCluster<T> implements Cluster<T> {

        private float communityStrength;
        private Collection<T> members;
        private ClusteringResult<T> clusteringResult;

        public DefaultCluster(Collection<T> members, float communityStrength, ClusteringResult<T> clustering) {
            this.members = new ArrayList<>(members);
            this.communityStrength = communityStrength;
            this.clusteringResult = clustering;
        }

        @Override
        public Collection<T> getMembers() {
            return members;
        }

        @Override
        public float getCommunityStrength() {
            return communityStrength;
        }

        @Override
        public ClusteringResult<T> getClusteringResult() {
            return clusteringResult;
        }



    } 
}


