package de.jplag.java_cpg.transformation.matching.edges;

import static de.jplag.java_cpg.transformation.matching.edges.IEdge.EdgeCategory.*;

import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;

import de.fraunhofer.aisec.cpg.graph.Node;

/**
 * This is a wrapper for a graph edge (with a 1:1 relation).
 * @param <S> The type of the source node
 * @param <T> The type of the target node
 */
public class CpgEdge<S extends Node, T extends Node> extends AEdge<S, T> {
    private final Function<S, T> getter;
    private final BiConsumer<S, T> setter;

    /**
     * Creates a new {@link CpgEdge} with a getter and setter for the target node.
     * @param getter the getter
     * @param setter the setter
     * @param category the edge category
     */
    public CpgEdge(Function<S, T> getter, BiConsumer<S, T> setter, EdgeCategory category) {
        super(category);
        this.getter = getter;
        this.setter = setter;
    }

    /**
     * Creates a new {@link CpgEdge} with a getter and setter for the target node.
     * @param getter the getter
     * @param setter the setter
     */
    public CpgEdge(Function<S, T> getter, BiConsumer<S, T> setter) {
        this(getter, setter, AST);
    }

    /**
     * Creates a new list-valued {@link CpgEdge} with a getter and setter for the target node list.
     * @param getter the getter
     * @param setter the setter
     * @param <S> The type of the source node
     * @param <T> The type of the target node
     * @return a {@link CpgEdge} object
     */
    public static <S extends Node, T extends Node> CpgEdge<S, T> listValued(Function<S, List<T>> getter, BiConsumer<S, List<T>> setter) {
        return new CpgEdge<>(node -> getter.apply(node).get(0), (node, value) -> setter.accept(node, List.of(value)));
    }

    /**
     * Gets the target node of this edge starting from the given source node.
     * @param from the source node
     * @return the target node
     */
    public T getRelated(S from) {
        return getter.apply(from);
    }

    /**
     * Gets the getter function of this {@link CpgEdge}, used to get the targets from a given source.
     * @return the getter
     */
    public Function<S, T> getter() {
        return getter;
    }

    /**
     * Gets the setter function of this {@link CpgEdge}, used to set the targets for a given source.
     * @return the setter
     */
    public BiConsumer<S, T> setter() {
        return setter;
    }

    @Override
    public String toString() {
        return "CpgEdge[" + "getter=" + getter + ", " + "setter=" + setter + ']';
    }

    @Override
    public boolean isEquivalentTo(IEdge<?, ?> other) {
        return Objects.equals(this, other);
    }

}
