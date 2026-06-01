package de.jplag.java_cpg.transformation.matching.edges;

import static de.jplag.java_cpg.transformation.matching.edges.IEdge.EdgeCategory.AST;

import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;

import de.fraunhofer.aisec.cpg.graph.Node;

/**
 * This is a wrapper for a graph edge (with a 1:1 relation).
 * @param <T> The type of the source node
 * @param <R> The type of the related node
 */
public class CpgEdge<T extends Node, R extends Node> extends AEdge<T, R> {
    private final Function<T, R> getter;
    private final BiConsumer<T, R> setter;

    /**
     * Creates a new {@link CpgEdge} with a getter and setter for the related node.
     * @param getter the getter
     * @param setter the setter
     * @param category the edge category
     */
    public CpgEdge(Function<T, R> getter, BiConsumer<T, R> setter, EdgeCategory category) {
        super(category);
        this.getter = getter;
        this.setter = setter;
    }

    /**
     * Creates a new {@link CpgEdge} with a getter and setter for the related node.
     * @param getter the getter
     * @param setter the setter
     */
    public CpgEdge(Function<T, R> getter, BiConsumer<T, R> setter) {
        this(getter, setter, AST);
    }

    /**
     * Creates a new list-valued {@link CpgEdge} with a getter and setter for the related node list.
     * @param getter the getter
     * @param setter the setter
     * @param <T> The type of the source node
     * @param <R> The type of the related node
     * @return the new {@link CpgEdge}
     */
    public static <T extends Node, R extends Node> CpgEdge<T, R> listValued(Function<T, List<R>> getter, BiConsumer<T, List<R>> setter) {
        // used only for assignment left-hand sides and right-hand sides, where in Java only one value is allowed
        return new CpgEdge<>(node -> getter.apply(node).getFirst(), (node, value) -> setter.accept(node, List.of(value)));
    }

    /**
     * Gets the related node of this edge starting from the given source node.
     * @param from the source node
     * @return the related node
     */
    public R getRelated(T from) {
        return getter.apply(from);
    }

    /**
     * Gets the getter function of this {@link CpgEdge}, used to get the relateds from a given source.
     * @return the getter
     */
    public Function<T, R> getter() {
        return getter;
    }

    /**
     * Gets the setter function of this {@link CpgEdge}, used to set the relateds for a given source.
     * @return the setter
     */
    public BiConsumer<T, R> setter() {
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
