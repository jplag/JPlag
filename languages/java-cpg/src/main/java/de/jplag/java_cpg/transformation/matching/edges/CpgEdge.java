package de.jplag.java_cpg.transformation.matching.edges;

import de.fraunhofer.aisec.cpg.graph.Node;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * This is a wrapper for a graph edge (with a 1:1 relation).
 * @param <S> The type of the source node
 * @param <T> The type of the target node
 */
public class CpgEdge<S extends Node, T extends Node> implements IEdge<S, T> {
    private final Function<S, T> getter;
    private final BiConsumer<S, T> setter;
    private Class<S> fromClass;
    private Class<T> toClass;

    /**
     * Creates a new {@link CpgEdge} with a getter and setter for the target node.
     * @param getter the getter
     * @param setter the setter
     */
    public CpgEdge(Function<S, T> getter,
                   BiConsumer<S, T> setter) {
        this.getter = getter;
        this.setter = setter;
    }

    /**
     * Gets the target node of this edge starting from the given source node.
     * @param from the source node
     * @return the target node
     */
    public T getRelated(S from) {
        return getter.apply(from);
    }

    @Override
    public void setFromClass(Class<S> sClass) {
        this.fromClass = sClass;
    }

    @Override
    public void setToClass(Class<T> tClass) {
        this.toClass = tClass;
    }

    public Class<S> getFromClass() {
        return fromClass;
    }

    public Class<T> getToClass() {
        return toClass;
    }


    public Function<S, T> getter() {
        return getter;
    }


    public BiConsumer<S, T> setter() {
        return setter;
    }


    @Override
    public String toString() {
        return "CpgEdge[" +
            "getter=" + getter + ", " +
            "setter=" + setter + ']';
    }

    @Override
    public boolean isEquivalentTo(IEdge<S, ?> other) {
        return Objects.equals(this, other);
    }
}
