package de.jplag.clustering;

import java.util.Collection;

/**
 * Interface for classes that process similarity matrices before any clustering. Classes implementing this interface
 * must ensure that they do not produce zero rows/columns inside the similarity matrix. They must also be able to
 * calculate the original indices of rows/columns after use through their
 * {@link ClusteringPreprocessor#postProcessResult} method.
 */
public interface ClusteringPreprocessor {
    /**
     * Applies some preprocessing defined by the implementing class.
     * @param similarityMatrix original similarities
     * @return preprocessed similarities
     */
    double[][] preprocessSimilarities(double[][] similarityMatrix);

    /**
     * Change the indices contained in the clustering result to the indices used before preprocessing.
     * @param result from clustering with preprocessed similarities
     * @return the same result but with the indices from the original similarity matrix
     */
    Collection<Collection<Integer>> postProcessResult(Collection<Collection<Integer>> result);
}
