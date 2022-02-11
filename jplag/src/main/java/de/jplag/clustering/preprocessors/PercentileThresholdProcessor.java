package de.jplag.clustering.preprocessors;

import java.util.Collection;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.DefaultRealMatrixPreservingVisitor;
import org.apache.commons.math3.stat.descriptive.rank.Percentile;
import org.apache.commons.math3.stat.descriptive.rank.Percentile.EstimationType;

import de.jplag.clustering.Preprocessor;

public class PercentileThresholdProcessor implements Preprocessor {

    private float percentile;
    private ThresholdPreprocessor tp;

    public PercentileThresholdProcessor(float percentile) {
        this.percentile = percentile;
    }

    @Override
    public double[][] preprocessSimilarities(double[][] similarityMatrix) {
        Array2DRowRealMatrix similarity = new Array2DRowRealMatrix(similarityMatrix, false);
        Percentile p = new Percentile().withEstimationType(EstimationType.R_2);
        int connections = (similarity.getColumnDimension() * (similarity.getColumnDimension() - 1)) / 2;
        double[] allWeights = new double[connections];
        similarity.walkInOptimizedOrder(new DefaultRealMatrixPreservingVisitor() {

            int index = 0;

            @Override
            public void visit(int row, int column, double value) {
                // collect upper triangle
                if (row > column) {
                    allWeights[this.index++] = value;
                }
            }
        });
        double threshold = p.evaluate(allWeights, percentile);
        tp = new ThresholdPreprocessor(threshold);

        return tp.preprocessSimilarities(similarityMatrix);
    }

    @Override
    public Collection<Collection<Integer>> postProcessResult(Collection<Collection<Integer>> result) {
        return tp.postProcessResult(result);
    }

}
