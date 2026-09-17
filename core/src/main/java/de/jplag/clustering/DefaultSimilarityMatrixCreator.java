package de.jplag.clustering;

import java.util.Collection;
import java.util.function.ToDoubleFunction;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;

import de.jplag.JPlagComparison;
import de.jplag.Submission;

/**
 * This class is the trivial implementation of a SimilarityMatrixCreator. For each comparison, it uses a certain metric
 * to get the similarity value of the two submissions involved in that comparison, and writes that value in the correct
 * spot in the matrix.
 */
public class DefaultSimilarityMatrixCreator implements SimilarityMatrixCreator {
    private final ToDoubleFunction<JPlagComparison> metric;

    /**
     * Creates a default similarity matrix creator that uses the specified metric for assigning the similarity values.
     * @param metric a function returning the similarity value of a comparison
     */
    public DefaultSimilarityMatrixCreator(ToDoubleFunction<JPlagComparison> metric) {
        this.metric = metric;
    }

    @Override
    public RealMatrix createSimilarityMatrix(Collection<JPlagComparison> comparisons, IntegerMapping<Submission> submissionsMap) {
        RealMatrix matrix = new Array2DRowRealMatrix(submissionsMap.size(), submissionsMap.size());
        for (JPlagComparison comparison : comparisons) {
            int firstIndex = submissionsMap.map(comparison.firstSubmission());
            int secondIndex = submissionsMap.map(comparison.secondSubmission());
            double similarity = metric.applyAsDouble(comparison);
            matrix.setEntry(firstIndex, secondIndex, similarity);
            matrix.setEntry(secondIndex, firstIndex, similarity);
        }
        return matrix;
    }
}
