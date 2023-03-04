package de.jplag.semantics;

/**
 * Each variable has its unique identity, important for tracing in graph (NormalizationGraph::spreadKeep).
 */
public class Variable {
    private final String name;
    private final boolean isOwnMember;
    private final boolean isMutable;

    Variable(String name, boolean isOwnMember, boolean isMutable) {
        this.name = name;
        this.isOwnMember = isOwnMember;
        this.isMutable = isMutable;
    }

    boolean isMutable() {
        return isMutable;
    }

    @Override
    public String toString() {
        return (isOwnMember ? "this." : "") + name + (isMutable ? "*" : "");
    }
}
