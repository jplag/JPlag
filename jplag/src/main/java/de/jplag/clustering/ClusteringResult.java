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
    private final double communityStrength;

    public ClusteringResult(Collection<Cluster<T>> clusters, double communityStrength) {
        this.clusters = List.copyOf(clusters);
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
    public double getCommunityStrength() {
        return communityStrength;
    }

    /**
     * How much this clustering result is worth during optimization.
     * @param similarity TODO DF: JAVADOC
     * @return worth
     */
    public double getWorth(BiFunction<T, T, Double> similarity) {
        return getClusters().stream().mapToDouble(c -> c.getWorth(similarity)).map(worth -> Double.isFinite(worth) ? worth : 0).average()
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
        double communityStrength = 0;
        if (!clustering.isEmpty()) {
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
                double averageSimilarity = calculateAverageSimilarityFor(clustering.get(i), similarity);
                clusters.add(new Cluster<>(clustering.get(i), clusterCommunityStrength, averageSimilarity));
                communityStrength += clusterCommunityStrength;
            }
        }
        return new ClusteringResult<>(clusters, communityStrength);
    }

    private static double calculateAverageSimilarityFor(Collection<Integer> cluster, RealMatrix similarityMatrix) {
        double sumOfSimilarities = 0;
        List<Integer> indices = List.copyOf(cluster);
        for (int i = 1; i < cluster.size(); i++) {
            int indexOfSubmission1 = indices.get(i);
            for (int j = 0; j < i; j++) { // as the similarity matrix is symmetrical we need only iterate over one half of it
                int indexOfSubmission2 = indices.get(j);
                sumOfSimilarities += similarityMatrix.getEntry(indexOfSubmission1, indexOfSubmission2);
            }
        }
        int nMinusOne = cluster.size() - 1;
        double numberOfComparisons = (nMinusOne * (nMinusOne + 1)) / 2.0;
        /*
         * Use Gauss sum to calculate number of comparisons in cluster: Given cluster of size n we need Gauss sum of n-1
         * comparisons: compare first element of cluster to all other except itself: n-1 comparisons. compare second element to
         * all other except itself and first element (as these two were already compared when we processed the first element),
         * n-2 comparisons. compare third element to all other but itself and all previously compared: n-3 comparisons and so
         * on. when we reach the second to last element we have n-(n-1)=1 comparisons left. when we reach the last element it
         * has already been compared to all other. adding up all comparisons we get: (n-1) + (n-2) + (n-3) + ... + (n-(n-1)) =
         * Gauss sum of (n-1)
         */
        return sumOfSimilarities / numberOfComparisons;
    }

}
