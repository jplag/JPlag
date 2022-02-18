package de.jplag.clustering;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.stream.DoubleStream;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;

/**
 * Set of clusters dividing a set of entities.
 * @param <T> type of the clustered entities (e.g. Submission)
 */
public class ClusteringResult<T> {

    private final List<Cluster<T>> clusters;
    private float communityStrength = 0;

    public ClusteringResult(Collection<Cluster<T>> clusters, float communityStrength) {
        this.clusters = Collections.unmodifiableList(new ArrayList<>(clusters));
        this.communityStrength = communityStrength;
        for (Cluster<T> cluster : clusters) {
            cluster.setClusteringResult(this);
        }
    }

    public Collection<Cluster<T>> getClusters() {
        return Collections.unmodifiableList(clusters);
    }

    /**
     * Community strength of the clustering. The expectation of community strength in a random graph is zero and it can not
     * exceed one. It's the sum of it's clusters {@link Cluster#getCommunityStrength}. If the underlying network is not
     * changed, a higher community strength denotes a better clustering. See: Finding and evaluating community structure in
     * networks, M. E. J. Newman and M. Girvan, Phys. Rev. E 69, 026113 – Published 26 February 2004, Doi:
     * 10.1103/PhysRevE.69.026113 It's called modularity in that paper.
     * @return community strength
     */
    public float getCommunityStrength() {
        return communityStrength;
    }

    /**
     * How much this clustering result is worth during optimization.
     * @param similarity
     * @return worth
     */
    public float getWorth(BiFunction<T, T, Float> similarity) {
        return (float) getClusters().stream().mapToDouble(c -> c.getWorth(similarity)).map(worth -> Double.isFinite(worth) ? worth : 0).average()
                .getAsDouble();
    }

    /**
     * Responsible for calculating the {@link ClusteringResult#getCommunityStrength} of a new clustering on integers and
     * it's clusters.
     */
    public static ClusteringResult<Integer> fromIntegerCollections(List<Collection<Integer>> clustering, RealMatrix similarity) {
        int numberOfSubmissions = similarity.getRowDimension();
        Map<Integer, Integer> clusterIndicesOfSubmissionIndices = new HashMap<>();
        int clusterIdx = 0;
        for (Collection<Integer> cluster : clustering) {
            for (Integer submissionIdx : cluster) {
                clusterIndicesOfSubmissionIndices.put(submissionIdx, clusterIdx);
            }
            clusterIdx++;
        }
        List<Cluster<Integer>> clusters = new ArrayList<>(clustering.size());
        float communityStrength = 0;
        if (clustering.size() > 0) {
            RealMatrix percentagesOfSimilaritySums = new Array2DRowRealMatrix(clustering.size(), clustering.size());
            percentagesOfSimilaritySums = percentagesOfSimilaritySums.scalarMultiply(0);
            for (int i = 0; i < numberOfSubmissions; i++) {
                if (!clusterIndicesOfSubmissionIndices.containsKey(i))
                    continue;
                int clusterA = clusterIndicesOfSubmissionIndices.get(i);
                for (int j = i + 1; j < numberOfSubmissions; j++) {
                    if (!clusterIndicesOfSubmissionIndices.containsKey(j))
                        continue;
                    int clusterB = clusterIndicesOfSubmissionIndices.get(j);
                    percentagesOfSimilaritySums.addToEntry(clusterA, clusterB, similarity.getEntry(i, j));
                    percentagesOfSimilaritySums.addToEntry(clusterB, clusterA, similarity.getEntry(i, j));
                }
            }
            percentagesOfSimilaritySums = percentagesOfSimilaritySums
                    .scalarMultiply(1 / Arrays.stream(similarity.getData()).flatMapToDouble(DoubleStream::of).sum());
            for (int i = 0; i < clustering.size(); i++) {
                double outWeightSum = percentagesOfSimilaritySums.getRowVector(i).getL1Norm();
                double clusterCommunityStrength = percentagesOfSimilaritySums.getEntry(i, i) - outWeightSum * outWeightSum;
                clusters.add(new Cluster<Integer>(clustering.get(i), (float) clusterCommunityStrength));
                communityStrength += clusterCommunityStrength;
            }
        }
        return new ClusteringResult<>(clusters, communityStrength);
    }

}
