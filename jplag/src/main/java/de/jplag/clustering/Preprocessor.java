package de.jplag.clustering;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;

import de.jplag.clustering.algorithm.ClusteringAlgorithm;

public interface Preprocessor {
    double[][] preprocessSimilarities(double[][] similarityMatrix);

    Collection<Collection<Integer>> postProcessResult(Collection<Collection<Integer>> result);

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

    public static class PreprocessedClusteringAlgorithm implements ClusteringAlgorithm {

        private final ClusteringAlgorithm base;
        private final Preprocessor preprocessor;

        public PreprocessedClusteringAlgorithm(ClusteringAlgorithm base, Preprocessor preprocessor) {
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