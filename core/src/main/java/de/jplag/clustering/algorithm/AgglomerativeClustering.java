package de.jplag.clustering.algorithm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.math3.linear.RealMatrix;

import de.jplag.clustering.ClusteringOptions;

/**
 * Begin by assigning a cluster to each entity and then successively merge similar clusters.
 */
public class AgglomerativeClustering implements GenericClusteringAlgorithm {

    private final ClusteringOptions options;

    /**
     * Creates a new agglomerative clustering algorithm with the given options.
     * @param options clustering configuration
     */
    public AgglomerativeClustering(ClusteringOptions options) {
        this.options = options;
    }

    @Override
    public Collection<Collection<Integer>> cluster(RealMatrix similarityMatrix) {
        int size = similarityMatrix.getRowDimension();
        // all clusters that do not have a parent yet
        Set<Cluster> clusters = HashSet.newHashSet(size);
        // calculated similarities. Might contain connections to already visited
        // clusters, those need to be ignored.
        PriorityQueue<ClusterConnection> similarities;

        // initialization

        List<Cluster> initialClusters = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            List<Integer> members = new ArrayList<>();
            members.add(i);
            Cluster cluster = new Cluster(members);
            initialClusters.add(cluster);
            clusters.add(cluster);
        }

        List<ClusterConnection> initialSimilarities = new ArrayList<>(size * (size - 1) / 2);

        for (int leftIndex = 0; leftIndex < initialClusters.size(); leftIndex++) {
            Cluster leftCluster = initialClusters.get(leftIndex);
            for (int rightIndex = leftIndex + 1; rightIndex < initialClusters.size(); rightIndex++) {
                Cluster rightCluster = initialClusters.get(rightIndex);
                initialSimilarities.add(new ClusterConnection(leftCluster, rightCluster, similarityMatrix.getEntry(leftIndex, rightIndex)));
            }
        }

        similarities = new PriorityQueue<>(initialSimilarities);

        if (similarities.isEmpty()) {
            return Collections.emptyList();
        }

        while (clusters.size() > 1) {
            ClusterConnection nearest = similarities.poll();
            // nearest will never be null. If the queue is empty initially we exit before the loop.
            // If there are at least two clusters remaining we added a new similarity in the for loop at the end of the while.
            // Otherwise, the loop exits

            if (clusters.contains(nearest.left) && clusters.contains(nearest.right)) { // Checks if one cluster is already part of another cluster
                if (nearest.similarity < options.agglomerativeThreshold()) {
                    break;
                }
                clusters.remove(nearest.left);
                clusters.remove(nearest.right);
                nearest.left.submissions().addAll(nearest.right.submissions());
                Cluster combined = new Cluster(nearest.left.submissions());
                for (Cluster otherCluster : clusters) {
                    double similarity = options.agglomerativeInterClusterSimilarity().clusterSimilarity(combined.submissions,
                            otherCluster.submissions, similarityMatrix);
                    similarities.add(new ClusterConnection(combined, otherCluster, similarity));
                }
                clusters.add(combined);
            }
        }

        return clusters.stream().map(Cluster::submissions).collect(Collectors.toList());
    }

    private record ClusterConnection(Cluster left, Cluster right, double similarity) implements Comparable<ClusterConnection> {
        @Override
        public int compareTo(ClusterConnection other) {
            return Double.compare(other.similarity, similarity);
        }

    }

    /**
     * Encapsulate a list in a class because we do not want hashing based on the members but on identity only. Also, a
     * cluster need a different identity than the list because the lists are reused.
     * @param submissions is the list of members.
     */
    private record Cluster(List<Integer> submissions) {
    }

}
