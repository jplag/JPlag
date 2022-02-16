package de.jplag.clustering;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;

import de.jplag.clustering.algorithm.GenericClusteringAlgorithm;

/**
 * Interface for classes that process similarity matrices before any clustering. Classes implementing this interface
 * must ensure that they do not produce zero rows/columns inside the similarity matrix. They must also be able to
 * calculate the original indices of rows/columns after use through their {@link ClusteringPreprocessor#postProcessResult} method.
 */
public interface ClusteringPreprocessor {
    double[][] preprocessSimilarities(double[][] similarityMatrix);

    Collection<Collection<Integer>> postProcessResult(Collection<Collection<Integer>> result);

    /**
     * Implements removal of zero rows / columns. And calculation of original indices.
     */
    public static class PreprocessorHelper {
        private IntegerMapping<Integer> mapping;

        public double[][] removeDisconnectedEntries(double[][] connections) {
            List<Integer> rowList = new ArrayList<>();
            mapping = new IntegerMapping<>(connections.length);
            RealMatrix similarity = new Array2DRowRealMatrix(connections, true);
            for (int i = 0; i < similarity.getRowDimension(); i++) {
                if (DoubleStream.of(similarity.getRow(i)).filter(x -> x > 0).findAny().isPresent()) {
                    rowList.add(i);
                    mapping.map(i);
                }
            }
            int[] preservedRows = rowList.stream().mapToInt(Integer::intValue).toArray();
            if (preservedRows.length == 0) {
                return new double[0][];
            }
            similarity = similarity.getSubMatrix(preservedRows, preservedRows);
            return similarity.getData();
        }

        public Collection<Collection<Integer>> postProcessResult(Collection<Collection<Integer>> result) {
            return result.stream().map(cluster -> cluster.stream().map(mapping::unmap).collect(Collectors.toList())).collect(Collectors.toList());
        }
    }

    /**
     * Adapter class to put a preprocessor before any clustering algorithm.
     */
    public static class PreprocessedClusteringAlgorithm implements GenericClusteringAlgorithm {

        private final GenericClusteringAlgorithm base;
        private final ClusteringPreprocessor preprocessor;

        public PreprocessedClusteringAlgorithm(GenericClusteringAlgorithm base, ClusteringPreprocessor preprocessor) {
            this.base = base;
            this.preprocessor = preprocessor;
        }

        @Override
        public Collection<Collection<Integer>> cluster(RealMatrix similarityMatrix) {
            double[][] data = preprocessor.preprocessSimilarities(similarityMatrix.getData());
            if (data.length > 2) {
                Collection<Collection<Integer>> prelimResult = base.cluster(new Array2DRowRealMatrix(data, false));
                return preprocessor.postProcessResult(prelimResult);
            }
            return Collections.emptyList();
        }

    }
}