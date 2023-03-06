package de.jplag.semantics;

/**
 * Each variable has its unique identity, important for tracing in graph (NormalizationGraph::spreadKeep).
 */
public class Variable {
    private final String name;
    private final Scope scope;
    private final boolean isMutable;

    Variable(String name, Scope scope, boolean isMutable) {
        this.name = name;
        this.scope = scope;
        this.isMutable = isMutable;
    }

    boolean isMutable() {
        return isMutable;
    }

    @Override
    public String toString() {
        return name + (isMutable ? "*" : "") + " [scope: " + scope.name().toLowerCase() + "]";
    }
}
