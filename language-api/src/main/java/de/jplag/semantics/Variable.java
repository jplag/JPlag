package de.jplag.semantics;

public record Variable(String name, VariableId id) {

    public Variable(String name) {
        this(name, new VariableId());
    }

    @Override
    public String toString() {
        return name + "[" + id + "]";
    }
}
