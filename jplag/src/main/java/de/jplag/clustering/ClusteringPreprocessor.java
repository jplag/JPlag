package de.jplag.clustering;

import java.util.Collection;
import java.util.Collections;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;

import de.jplag.clustering.algorithm.GenericClusteringAlgorithm;

/**
 * Interface for classes that process similarity matrices before any clustering. Classes implementing this interface
 * must ensure that they do not produce zero rows/columns inside the similarity matrix. They must also be able to
 * calculate the original indices of rows/columns after use through their
 * {@link ClusteringPreprocessor#postProcessResult} method.
 */
public interface ClusteringPreprocessor {
    double[][] preprocessSimilarities(double[][] similarityMatrix);

    Collection<Collection<Integer>> postProcessResult(Collection<Collection<Integer>> result);

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
                Collection<Collection<Integer>> preliminaryResult = base.cluster(new Array2DRowRealMatrix(data, false));
                return preprocessor.postProcessResult(preliminaryResult);
            }
            return Collections.emptyList();
        }

    }
}