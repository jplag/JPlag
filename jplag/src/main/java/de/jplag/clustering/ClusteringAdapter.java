package de.jplag.clustering;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.ToDoubleFunction;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;

import de.jplag.JPlagComparison;
import de.jplag.Submission;
import de.jplag.clustering.algorithm.GenericClusteringAlgorithm;

/**
 * This class acts as an adapter between
 * <ul>
 * <li>the clustering algorithms (that operate on collections of integers)</li>
 * <li>and the rest of the code base (that operates on {@link ClusteringResult}s of {@link Submission}s)</li>
 * </ul>
 */
public class ClusteringAdapter {

    private final RealMatrix similarityMatrix;
    private final IntegerMapping<Submission> mapping;

    /**
     * Creates the clustering adapter. Only submissions that appear in those similarities might also appear in
     * {@link ClusteringResult}s obtained from this adapter.
     * @param comparisons that should be included in the process of clustering
     * @param metric function that assigns a similarity to each comparison
     */
    public ClusteringAdapter(Collection<JPlagComparison> comparisons, ToDoubleFunction<JPlagComparison> metric) {
        mapping = new IntegerMapping<>(comparisons.size());
        for (JPlagComparison comparison : comparisons) {
            mapping.map(comparison.firstSubmission());
            mapping.map(comparison.secondSubmission());
        }
        int size = mapping.size();

        similarityMatrix = new Array2DRowRealMatrix(size, size);
        for (JPlagComparison comparison : comparisons) {
            int firstIndex = mapping.map(comparison.firstSubmission());
            int secondIndex = mapping.map(comparison.secondSubmission());
            double similarity = metric.applyAsDouble(comparison);
            similarityMatrix.setEntry(firstIndex, secondIndex, similarity);
            similarityMatrix.setEntry(secondIndex, firstIndex, similarity);
        }
    }

    /**
     * Use a generic clustering algorithm to cluster the submissions, that were included in this {@link ClusteringAdapter}'s
     * comparison.
     * @param algorithm that is used for clustering
     * @return the clustered submissions
     */
    public ClusteringResult<Submission> doClustering(GenericClusteringAlgorithm algorithm) {
        Collection<Collection<Integer>> intResult = algorithm.cluster(similarityMatrix);
        ClusteringResult<Integer> modularityClusterResult = ClusteringResult.fromIntegerCollections(new ArrayList<>(intResult), similarityMatrix);
        List<Cluster<Submission>> mappedClusters = modularityClusterResult.getClusters().stream()
                .map(unmappedCluster -> new Cluster<>(unmappedCluster.getMembers().stream().map(mapping::unmap).toList(),
                        unmappedCluster.getCommunityStrength(), unmappedCluster.getAverageSimilarity()))
                .toList();
        return new ClusteringResult<>(mappedClusters, modularityClusterResult.getCommunityStrength());
    }

}
