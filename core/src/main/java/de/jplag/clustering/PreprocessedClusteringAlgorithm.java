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

    public PreprocessedClusteringAlgorithm(GenericClusteringAlgorithm base, ClusteringPreprocessor preprocessor) {
        this.base = base;
        this.preprocessor = preprocessor;
    }

    @Override
    public Collection<Collection<Integer>> cluster(RealMatrix similarityMatrix) {
        double[][] data = preprocessor.preprocessSimilarities(similarityMatrix.getData());
        if (data.length > 2) {
            Collection<Collection<Integer>> preliminaryResult = base.cluster(new Array2DRowRealMatrix(data, false));
            return preliminaryResult.stream().map(cluster -> cluster.stream().map(preprocessor::originalIndexOf).collect(Collectors.toList()))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

}