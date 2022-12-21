package de.jplag.normalization;

import java.util.Objects;

import org.jgrapht.graph.DefaultEdge;

import de.jplag.semantics.Variable;

public class Dependency extends DefaultEdge { // for optimization
    private final DependencyType type;
    private final Variable cause;

    public Dependency(DependencyType type, Variable cause) {
        this.type = type;
        this.cause = cause;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Dependency that = (Dependency) o;
        return type == that.type && Objects.equals(cause, that.cause);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, cause, super.getSource(), super.getTarget());
    }
}
