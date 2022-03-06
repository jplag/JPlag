package de.jplag.clustering;

/**
 * Interface for classes that process similarity matrices before any clustering. Classes implementing this interface
 * must ensure that they do not produce zero rows/columns inside the similarity matrix. They must also be able to
 * calculate the original indices of rows/columns after use through their {@link ClusteringPreprocessor#originalIndexOf}
 * method.
 */
public interface ClusteringPreprocessor {
    /**
     * Applies some preprocessing defined by the implementing class.
     * @param similarityMatrix original similarities
     * @return preprocessed similarities
     */
    double[][] preprocessSimilarities(double[][] similarityMatrix);

    /**
     * Maps the indices of the preprocessed similarity matrix back to indices in the original matrix.
     * @param index after preprocessing
     * @return corresponding index before preprocessing
     */
    int originalIndexOf(int index);
}
