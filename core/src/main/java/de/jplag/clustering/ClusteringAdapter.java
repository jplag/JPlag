package de.jplag.clustering;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.math3.linear.RealMatrix;

import de.jplag.JPlagComparison;
import de.jplag.Submission;
import de.jplag.clustering.algorithm.GenericClusteringAlgorithm;

/**
 * This class acts as an adapter between:
 * <ul>
 * <li>the clustering algorithms (that operate on collections of integers).</li>
 * <li>and the rest of the code base (that operates on {@link ClusteringResult}s of {@link Submission}s).</li>
 * </ul>
 */
public class ClusteringAdapter {

    private final RealMatrix similarityMatrix;
    private final IntegerMapping<Submission> mapping;

    /**
     * Creates the clustering adapter. Only submissions that appear in those similarities might also appear in
     * {@link ClusteringResult}s obtained from this adapter.
     * @param comparisons that should be included in the process of clustering
     * @param matrixCreator the matrix creator that should be used for creating the similarity matrix
     */
    public ClusteringAdapter(Collection<JPlagComparison> comparisons, SimilarityMatrixCreator matrixCreator) {
        mapping = new IntegerMapping<>(comparisons.size());
        for (JPlagComparison comparison : comparisons) {
            mapping.map(comparison.firstSubmission());
            mapping.map(comparison.secondSubmission());
        }
        similarityMatrix = matrixCreator.createSimilarityMatrix(comparisons, mapping);
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
