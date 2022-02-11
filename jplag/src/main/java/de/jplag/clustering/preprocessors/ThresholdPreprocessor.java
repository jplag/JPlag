package de.jplag.clustering.preprocessors;

import java.util.Collection;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.DefaultRealMatrixChangingVisitor;
import org.apache.commons.math3.linear.RealMatrix;

import de.jplag.clustering.Preprocessor;

/**
 * Suppresses all similarities below a given threshold.
 */
public class ThresholdPreprocessor implements Preprocessor {

    private double threshold;
    private PreprocessorHelper helper = new PreprocessorHelper();

    public ThresholdPreprocessor(double threshold) {
        this.threshold = threshold;
    }

    @Override
    public double[][] preprocessSimilarities(double[][] similarityMatrix) {
        RealMatrix similarity = new Array2DRowRealMatrix(similarityMatrix, true);
        similarity.walkInOptimizedOrder(new DefaultRealMatrixChangingVisitor() {
            @Override
            public double visit(int row, int column, double value) {
                return value >= threshold ? value : 0;
            }
        });
        return helper.removeDisconnectedEntries(similarity.getData());
    }

    @Override
    public Collection<Collection<Integer>> postProcessResult(Collection<Collection<Integer>> result) {
        return helper.postProcessResult(result);
    }

}
