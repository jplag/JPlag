package de.jplag.clustering;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;

/**
 * Implements removal of zero rows / columns and calculation of original indices for classes implementing
 * {@link ClusteringPreprocessor}.
 */
public class PreprocessorHelper {
    private IntegerMapping<Integer> mapping;

    /**
     * Removes disconnected edges from the input matrix.
     * @param connections similarity matrix
     * @return similarity matrix without zero rows / cols
     */
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

    /**
     * Implements the logic for {@link ClusteringPreprocessor#postProcessResult(Collection)}
     * @param result clustering result gained from preprocessed similarities
     * @return post processed clustering result
     */
    public Collection<Collection<Integer>> postProcessResult(Collection<Collection<Integer>> result) {
        return result.stream().map(cluster -> cluster.stream().map(mapping::unmap).collect(Collectors.toList())).collect(Collectors.toList());
    }
}