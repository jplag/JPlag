package de.jplag.clustering;

import java.util.Collection;

import org.apache.commons.math3.linear.RealMatrix;

import de.jplag.JPlagComparison;
import de.jplag.Submission;

/**
 * Interface for classes that can create a similarity matrix from a collection of comparisons, that can for example be
 * used in the clustering process.
 */
public interface SimilarityMatrixCreator {
    /**
     * Creates a similarity matrix from the given comparisons.
     * @param comparisons the comparisons the resulting matrix should be based on
     * @param submissionsMap the map that has to be used for translating a certain submission to a certain integer, that
     * describes the row/column of that submission in the resulting matrix
     * @return the similarity matrix
     */
    RealMatrix createSimilarityMatrix(Collection<JPlagComparison> comparisons, IntegerMapping<Submission> submissionsMap);
}
