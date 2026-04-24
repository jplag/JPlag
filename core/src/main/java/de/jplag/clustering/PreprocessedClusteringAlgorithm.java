package de.jplag.clustering;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;

import de.jplag.clustering.algorithm.GenericClusteringAlgorithm;

/**
 * Adapter class to put a preprocessor before any clustering algorithm.
 */
public class PreprocessedClusteringAlgorithm implements GenericClusteringAlgorithm {

    private final GenericClusteringAlgorithm base;
    private final ClusteringPreprocessor preprocessor;

    /**
     * Constructs a new clustering algorithm that applies a preprocessor to the similarity matrix before delegating
     * clustering to the base algorithm.
     * @param base The base clustering algorithm to delegate to
     * @param preprocessor The preprocessor to apply to the similarity matrix before clustering
     */
    public PreprocessedClusteringAlgorithm(GenericClusteringAlgorithm base, ClusteringPreprocessor preprocessor) {
        this.base = base;
        this.preprocessor = preprocessor;
    }

    @Override
    public Collection<Collection<Integer>> cluster(RealMatrix similarityMatrix) {
        double[][] data = preprocessor.preprocessSimilarities(similarityMatrix.getData());
        if (data.length <= 2) {
            return Collections.emptyList();
        }
        Collection<Collection<Integer>> preliminaryResult = base.cluster(new Array2DRowRealMatrix(data, false));
        return preliminaryResult.stream().map(cluster -> cluster.stream().map(preprocessor::originalIndexOf).toList()).collect(Collectors.toList());
    }

}