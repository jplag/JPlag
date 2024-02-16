package de.jplag.emf.normalization;

import java.util.List;

/**
 * A vector for the occurrence frequency of different token types. The vector is padded with zeroes beyond its original
 * size. The vector content cannot be changed after its creation.
 */
public class TokenOccurenceVector {
    private final List<Double> originalVector;

    /**
     * Creates a zero-padded token occurrence vector.
     * @param originalVector specifies the occurrence frequency values for the vector.
     */
    public TokenOccurenceVector(List<Double> originalVector) {
        this.originalVector = originalVector;
    }

    /**
     * Return a occurrence frequency value of the vector at the specified.
     * @param index is the specified index.
     * @return the occurrence frequency value or zero if the index is beyond the size of the vector.
     */
    public double get(int index) {
        if (index >= originalVector.size()) {
            return 0.0;
        }
        return originalVector.get(index);
    }

    /**
     * The original size of the vector, without padding.
     * @return the size.
     */
    public int size() {
        return originalVector.size();
    }
}