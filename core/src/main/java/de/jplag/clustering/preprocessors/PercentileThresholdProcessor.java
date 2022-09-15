package de.jplag.clustering.preprocessors;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.DefaultRealMatrixPreservingVisitor;
import org.apache.commons.math3.stat.descriptive.rank.Percentile;
import org.apache.commons.math3.stat.descriptive.rank.Percentile.EstimationType;

import de.jplag.clustering.ClusteringPreprocessor;

/**
 * Suppresses all similarities below a given percentile.
 */
public class PercentileThresholdProcessor implements ClusteringPreprocessor {

    private final double percentile;
    private ThresholdPreprocessor thresholdPreprocessor;

    public PercentileThresholdProcessor(double percentile) {
        this.percentile = percentile;
    }

    @Override
    public double[][] preprocessSimilarities(double[][] similarityMatrix) {
        Array2DRowRealMatrix similarity = new Array2DRowRealMatrix(similarityMatrix, false);
        Percentile percentileEstimator = new Percentile().withEstimationType(EstimationType.R_2);
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
        double threshold = percentileEstimator.evaluate(allWeights, percentile);
        thresholdPreprocessor = new ThresholdPreprocessor(threshold);

        return thresholdPreprocessor.preprocessSimilarities(similarityMatrix);
    }

    @Override
    public int originalIndexOf(int result) {
        return thresholdPreprocessor.originalIndexOf(result);
    }

}
