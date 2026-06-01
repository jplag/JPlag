package de.jplag.java_cpg.token.characteristic;

/**
 * This class represents a characteristic vector that counts occurrences of different elements in source code.
 */
public class CharacteristicVector {
    private final int[] values;

    /**
     * Creates a new characteristic vector with all dimensions initialized to zero.
     */
    public CharacteristicVector() {
        this.values = new int[CharacteristicVectorDimension.DIMENSION_COUNT];
    }

    /**
     * Increments the count for the given dimension by one.
     * @param dimension The dimension to increment
     */
    public void incrementDimension(CharacteristicVectorDimension dimension) {
        this.values[dimension.ordinal()]++;
    }

    /**
     * Adds another characteristic vector to this one by summing up the counts for each dimension.
     * @param otherVector The other characteristic vector to add
     * @throws IllegalArgumentException if the other vector is null
     */
    public void addVector(CharacteristicVector otherVector) {
        if (otherVector == null) {
            throw new IllegalArgumentException("Other vector must not be null");
        }
        for (int i = 0; i < CharacteristicVectorDimension.DIMENSION_COUNT; i++) {
            this.values[i] += otherVector.values[i];
        }
    }

    /**
     * Getter for the values of the characteristic vector.
     * @return An array of integers representing the counts for each dimension
     */
    public int[] getValue() {
        return values;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("CharacteristicVector{");
        for (int i = 0; i < CharacteristicVectorDimension.DIMENSION_COUNT; i++) {
            sb.append(CharacteristicVectorDimension.values()[i].name()).append("=").append(values[i]);
            if (i < CharacteristicVectorDimension.DIMENSION_COUNT - 1) {
                sb.append(", ");
            }
        }
        sb.append("}");
        return sb.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof CharacteristicVector otherVector)) {
            return false;
        }
        for (int i = 0; i < CharacteristicVectorDimension.DIMENSION_COUNT; i++) {
            if (this.values[i] != otherVector.values[i]) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        return java.util.Arrays.hashCode(values);
    }
}
