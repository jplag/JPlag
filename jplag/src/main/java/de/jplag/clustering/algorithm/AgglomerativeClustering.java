package de.jplag.clustering.algorithm;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.stream.Collectors;

import org.apache.commons.math3.linear.RealMatrix;

/**
 * Begin by assigning a cluster to each entity and then successively merge similar clusters.
 */
public class AgglomerativeClustering implements GenericClusteringAlgorithm {

    private ClusteringOptions options;

    public AgglomerativeClustering(ClusteringOptions options) {
        this.options = options;
    }

    @Override
    public Collection<Collection<Integer>> cluster(RealMatrix similarityMatrix) {
        int N = similarityMatrix.getRowDimension();
        // all clusters that do not have a parent yet
        HashSet<Cluster> clusters = new HashSet<>(N);
        // calculated similarities. Might contain connections to already visited clusters, those need to be ignored.
        PriorityQueue<ClusterConnection> similarities;

        {
            // initialization

            ArrayList<Cluster> initialClusters = new ArrayList<>(N);
            for (int i = 0; i < N; i++) {
                Cluster cluster = new Cluster(Collections.singletonList(i));
                initialClusters.add(cluster);
                clusters.add(cluster);
            }

            ArrayList<ClusterConnection> initialSimilarities = new ArrayList<>(N * (N - 1) / 2);

            for (int leftIndex = 0; leftIndex < initialClusters.size(); leftIndex++) {
                Cluster leftCluster = initialClusters.get(leftIndex);
                for (int rightIndex = leftIndex + 1; rightIndex < initialClusters.size(); rightIndex++) {
                    Cluster rightCluster = initialClusters.get(rightIndex);
                    initialSimilarities
                            .add(new ClusterConnection(leftCluster, rightCluster, (float) similarityMatrix.getEntry(leftIndex, rightIndex)));
                }
            }

            similarities = new PriorityQueue<>(initialSimilarities);
        }

        while (clusters.size() > 1) {
            ClusterConnection nearest = similarities.poll();
            if (!(clusters.contains(nearest.left) && clusters.contains(nearest.right))) {
                // One cluster already part of another cluster
                continue;
            }
            if (nearest.similarity < options.minimalSimilarity) {
                break;
            }
            clusters.remove(nearest.left);
            clusters.remove(nearest.right);
            Cluster combined = new Cluster(new MergedList(nearest.left.getSubmissions(), nearest.right.getSubmissions()));
            for (Cluster otherCluster : clusters) {
                similarities.add(new ClusterConnection(combined, otherCluster, similarityMatrix));
            }
            clusters.add(combined);
        }

        return clusters.stream().map(Cluster::getSubmissions).collect(Collectors.toList());
    }

    private float clusterSimilarity(List<Integer> leftCluster, List<Integer> rightCluster, RealMatrix similarityMatrix) {
        float similarity = 0;
        switch (options.similarity) {
            case MIN:
                similarity = Float.MAX_VALUE;
                break;
            case MAX:
                similarity = -Float.MAX_VALUE;
                break;
            case AVERAGE:
                similarity = 0;
                break;
        }

        for (int leftIndex = 0; leftIndex < leftCluster.size(); leftIndex++) {
            int leftSubmission = leftCluster.get(leftIndex);
            for (int rightIndex = 0; rightIndex < rightCluster.size(); rightIndex++) {
                float submissionSimilarity = (float) similarityMatrix.getEntry(leftSubmission, rightCluster.get(rightIndex));
                switch (options.similarity) {
                    case MIN:
                        similarity = Math.min(similarity, submissionSimilarity);
                        break;
                    case MAX:
                        similarity = Math.max(similarity, submissionSimilarity);
                        break;
                    case AVERAGE:
                        similarity += submissionSimilarity;
                }
            }
        }

        if (options.similarity == InterClusterSimilarity.AVERAGE) {
            similarity /= leftCluster.size() * rightCluster.size();
        }
        return similarity;
    }

    public static class ClusteringOptions {
        public float minimalSimilarity = 0.1f;
        public InterClusterSimilarity similarity = InterClusterSimilarity.AVERAGE;
    }

    public static enum InterClusterSimilarity {
        MIN,
        MAX,
        AVERAGE;
    }

    private class ClusterConnection implements Comparable<ClusterConnection> {

        private Cluster left;
        private Cluster right;
        private float similarity;

        public ClusterConnection(Cluster left, Cluster right, RealMatrix similarity) {
            this(left, right, clusterSimilarity(left.submissions, right.submissions, similarity));
        }

        public ClusterConnection(Cluster left, Cluster right, float similarity) {
            this.left = left;
            this.right = right;
            this.similarity = similarity;
        }

        @Override
        public int compareTo(ClusterConnection other) {
            return (int) Math.signum(other.similarity - similarity);
        }

    }

    private class Cluster {
        private List<Integer> submissions;

        public Cluster(List<Integer> submissions) {
            this.submissions = submissions;
        }

        public List<Integer> getSubmissions() {
            return submissions;
        }

    }

    private class MergedList extends AbstractList<Integer> {

        private List<Integer> left, right;
        private int size;

        public MergedList(List<Integer> left, List<Integer> right) {
            this.left = left;
            this.right = right;
            size = left.size() + right.size();
        }

        @Override
        public Integer get(int index) {
            if (index < left.size()) {
                return left.get(index);
            } else {
                return right.get(index - left.size());
            }
        }

        @Override
        public int size() {
            return size;
        }

    }

}
