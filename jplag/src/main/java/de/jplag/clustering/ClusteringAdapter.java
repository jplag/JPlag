package de.jplag.clustering;

import java.util.Collection;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;

import de.jplag.JPlagComparison;
import de.jplag.Submission;
import de.jplag.clustering.algorithm.ClusteringAlgorithm;

/**
 * This class acts as an adapter between
 * <ul>
 * <li>the clustering algorithms (that operate on collections of integers)</li>
 * <li>and the rest of the code base (that operates on {@link ClusteringResult}s of {@link Submission}s)</li>
 * </ul>
 */
public class ClusteringAdapter {

    private RealMatrix similarityMatrix;
    private IntegerMapping<Submission> mapping;

    public ClusteringAdapter(Collection<JPlagComparison> comparisons, Function<JPlagComparison, Float> metric) {
        mapping = new IntegerMapping<>(comparisons.size());
        for (JPlagComparison comparison : comparisons) {
            mapping.map(comparison.getFirstSubmission());
            mapping.map(comparison.getSecondSubmission());
        }
        int size = mapping.size();

        similarityMatrix = new Array2DRowRealMatrix(size, size);
        for (JPlagComparison comparison : comparisons) {
            int firstIndex = mapping.map(comparison.getFirstSubmission());
            int secondIndex = mapping.map(comparison.getSecondSubmission());
            float similarity = metric.apply(comparison);
            similarityMatrix.setEntry(firstIndex, secondIndex, similarity);
            similarityMatrix.setEntry(secondIndex, firstIndex, similarity);
        }
    }

    public ClusteringResult<Submission> doClustering(ClusteringAlgorithm algorithm) {
        Collection<Collection<Integer>> intResult = algorithm.cluster(similarityMatrix);
        ClusteringResult<Integer> modularityClusterResult = new IntegerClusteringResult(intResult, similarityMatrix);
        return new MappedClusteringResult<>(modularityClusterResult, mapping::unmap);
    }

    private static class MappedClusteringResult<M, T> implements ClusteringResult<T> {

        private Collection<Cluster<T>> clusters;
        private float communityStrength;
        private int size;

        public MappedClusteringResult(ClusteringResult<M> unmapped, Function<M, T> mapping) {
            communityStrength = unmapped.getCommunityStrength();
            clusters = unmapped.getClusters().stream().map(unmappedCluster -> {
                return new ClusteringResult.DefaultCluster<T>(unmappedCluster.getMembers().stream().map(mapping).collect(Collectors.toList()),
                        unmappedCluster.getCommunityStrength(), this);
            }).collect(Collectors.toList());
            size = unmapped.size();
        }

        @Override
        public Collection<Cluster<T>> getClusters() {
            return clusters;
        }

        @Override
        public float getCommunityStrength() {
            return communityStrength;
        }

        @Override
        public int size() {
            return size;
        }

    }
}
