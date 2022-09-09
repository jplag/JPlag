package de.jplag.clustering.preprocessors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.IntUnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealMatrix;

import de.jplag.clustering.ClusteringPreprocessor;

public class PreprocessingTestBase {

    private static final double EPSILON = 0.000001;

    public double[][] createTestData() {
        RealMatrix similarity = new Array2DRowRealMatrix(5, 5);
        for (int i = 0; i < 4; i++) {
            similarity.setEntry(i, i, 1);
        }
        // These are similar
        setEntries(similarity, 0, 1, 0.5);
        setEntries(similarity, 2, 3, 0.5);

        // Others are dissimilar
        setEntries(similarity, 0, 2, 0.1);
        setEntries(similarity, 0, 3, 0.1);
        setEntries(similarity, 1, 2, 0.1);
        setEntries(similarity, 1, 3, 0.1);

        // last row is empty

        return similarity.getData();
    }

    public void validPreprocessing(double[][] originalArray, double[][] resultArray, IntUnaryOperator originalIndex) {
        RealMatrix result = new Array2DRowRealMatrix(resultArray, false);
        RealMatrix original = new Array2DRowRealMatrix(originalArray, false);
        assertEquals(result.getColumnDimension(), result.getRowDimension(), "not a square matrix");

        List<Integer> usedOriginalIndices = IntStream.range(0, result.getColumnDimension()).map(originalIndex).boxed().collect(Collectors.toList());

        assertEquals(usedOriginalIndices.size(), new HashSet<>(usedOriginalIndices).size(), "original indices not unique");

        assertTrue(usedOriginalIndices.stream().allMatch(index -> index >= 0 && index < original.getColumnDimension()), "original indices valid");

        double[] columnNorms = IntStream.range(0, result.getColumnDimension()).mapToObj(result::getColumn)
                .mapToDouble(array -> new ArrayRealVector(array, false).getNorm()).toArray();

        for (int i = 0; i < columnNorms.length; i++) {
            assertTrue(columnNorms[i] > EPSILON, "produced zero column in column " + i);
        }
    }

    public double[][] withAllValues(ClusteringPreprocessor preprocessor, double[][] original, double[][] result,
            BiConsumer<Double, Optional<Double>> originalAndPreprocessedConsumer) {

        // Construct mapping from original indices to preprocessed indices as implied by
        // originalIndexOf
        Map<Integer, Integer> mappedIndices = IntStream.range(0, result.length).collect(HashMap::new,
                (map, index) -> map.put(preprocessor.originalIndexOf(index), index), HashMap::putAll);
        for (int j = 0; j < original.length; j++) {
            double[] row = original[j];
            Optional<double[]> mappedRow = Optional.ofNullable(mappedIndices.get(j)).map(index -> result[index]);
            for (int i = 0; i < row.length; i++) {
                double originalValue = row[i];
                Optional<Double> value = Optional.ofNullable(mappedIndices.get(i)).flatMap(index -> mappedRow.map(x -> x[index]));
                originalAndPreprocessedConsumer.accept(originalValue, value);
            }
        }

        return result;
    }

    private static void setEntries(RealMatrix matrix, int i, int j, double value) {
        matrix.setEntry(i, j, value);
        matrix.setEntry(j, i, value);
    }
}
