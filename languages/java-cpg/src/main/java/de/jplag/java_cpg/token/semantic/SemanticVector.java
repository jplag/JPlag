package de.jplag.java_cpg.token.semantic;

public class SemanticVector {
    private final int[] values;

    public SemanticVector() {
        this.values = new int[SemanticDimension.DIMENSION_COUNT];
    }
    public void incrementDimension(SemanticDimension dimension) {
        this.values[dimension.ordinal()]++;
    }

    public void addVector(SemanticVector otherVector) {
        if (otherVector == null) {
            throw new IllegalArgumentException("Other vector must not be null");
        }
        for (int i = 0; i < SemanticDimension.DIMENSION_COUNT; i++) {
            this.values[i] += otherVector.values[i];
        }
    }

    public int[] getValue() {
        return values;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("SemanticVector{");
        for (int i = 0; i < SemanticDimension.DIMENSION_COUNT; i++) {
            sb.append(SemanticDimension.values()[i].name()).append("=").append(values[i]);
            if (i < SemanticDimension.DIMENSION_COUNT - 1) {
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
        if (!(obj instanceof SemanticVector otherVector)) {
            return false;
        }
        for (int i = 0; i < SemanticDimension.DIMENSION_COUNT; i++) {
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
