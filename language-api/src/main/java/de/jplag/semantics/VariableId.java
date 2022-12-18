package de.jplag.semantics;

public record VariableId(String id) {
    private static long counter;

    public VariableId() {
        this(Long.toString(counter++));
    }

    @Override
    public String toString() {
        return id;
    }
}
