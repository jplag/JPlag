package de.jplag.java_cpg.transformation.matching.edges;

import de.fraunhofer.aisec.cpg.graph.Node;
import de.fraunhofer.aisec.cpg.graph.edge.PropertyEdge;
import de.fraunhofer.aisec.cpg.helpers.TriConsumer;
import de.jplag.java_cpg.transformation.matching.pattern.NodePattern;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

import static de.jplag.java_cpg.transformation.matching.edges.IEdge.EdgeCategory.ANALYTIC;
import static de.jplag.java_cpg.transformation.matching.edges.IEdge.EdgeCategory.AST;

/**
 * This is a wrapper for a graph edge (1:n relation).
 *
 * @param <S> The type of the source node
 * @param <T> The type of the target node
 */
public final class CpgMultiEdge<S extends Node, T extends Node> implements IEdge<S, T> {
    private final Function<S, List<T>> getter;
    private final Function<S, List<PropertyEdge<T>>> getEdges;
    private final EdgeCategory category;
    private final TriConsumer<S, Integer, T> setter;
    private final boolean edgeValued;
    private Class<S> fromClass;
    private Class<T> toClass;
    private final Map<NodePattern<?>, Any1ofNEdge> any1ofNEdges;

    /**
     * Creates a new CpgMultiEdge.
     *
     * @param getter     a function to get all the target nodes
     * @param setter     a function to set the nth target node
     * @param edgeValued if true, then this relation is represented by a {@link List} of {@link PropertyEdge}s. Otherwise, it returns a list of {@link Node}s.
     * @param getEdges   if edgeValued, then this should be a function to get all the target edges, null otherwise.
     * @param category   category of the edge
     */
    public CpgMultiEdge(Function<S, List<T>> getter,
                        TriConsumer<S, Integer, T> setter,
                        boolean edgeValued,
                        Function<S, List<PropertyEdge<T>>> getEdges,
                        EdgeCategory category) {
        this.getter = getter;
        this.setter = setter;
        // TODO: Model edgeValued as enum {NODE, EDGE} instead?
        this.edgeValued = edgeValued;
        this.getEdges = getEdges;
        this.category = category;

        this.any1ofNEdges = new HashMap<>();
    }

    /**
     * A shorthand to create an edge-valued {@link CpgMultiEdge}.
     *
     * @param getter a function to get all the edges
     * @param <S>    The type of the source node
     * @param <T>    The type of the target node
     * @return the new {@link CpgMultiEdge}
     */
    public static <S extends Node, T extends Node> CpgMultiEdge<S, T> edgeValued(Function<S, List<PropertyEdge<T>>> getter) {
        return edgeValued(getter, AST);
    }
    public static <S extends Node, T extends Node> CpgMultiEdge<S, T> edgeValued(Function<S, List<PropertyEdge<T>>> getter, EdgeCategory category) {
        Function<S, List<T>> getNodes = (S node) -> getter.apply(node).stream().map(PropertyEdge::getEnd).toList();
        TriConsumer<S, Integer, T> setOne = (S node, Integer n, T value) -> getter.apply(node).get(n).setEnd(value);
        return new CpgMultiEdge<>(getNodes, setOne, true, getter, category);
    }


    /**
     * A shorthand to create a node-valued {@link CpgMultiEdge}.
     *
     * @param getter a function to get all the nodes
     * @param <S>    The type of the source node
     * @param <T>    The type of the target node
     * @return the new {@link CpgMultiEdge}
     */
    public static <S extends Node, T extends Node> CpgMultiEdge<S, T> nodeValued(Function<S, List<T>> getter, EdgeCategory category) {
        TriConsumer<S, Integer, T> setOne = (S node, Integer n, T value) -> getter.apply(node).set(n, value);
        return new CpgMultiEdge<>(getter, setOne, false, null, category);
    }

    public static <S extends Node, T extends Node> CpgMultiEdge<S, T> nodeValued(Function<S, List<T>> getter) {
        return nodeValued(getter, AST);
    }


    /**
     * Gets all the targets of this edge, starting from the given concrete source node.
     *
     * @param s the source node
     * @return the target nodes
     */
    public List<T> getAllTargets(S s) {
        return getter.apply(s);
    }

    public Class<S> getFromClass() {
        return this.fromClass;
    }

    public void setFromClass(Class<S> sClass) {
        this.fromClass = sClass;
    }

    public Class<T> getToClass() {
        return toClass;
    }

    public void setToClass(Class<T> tClass) {
        this.toClass = tClass;
    }

    public Function<S, List<T>> getter() {
        return getter;
    }

    public TriConsumer<S, Integer, T> setter() {
        return setter;
    }

    public <R extends Node, S extends Node> CpgMultiEdge<S,R>.Any1ofNEdge getAny1ofNEdgeTo(NodePattern<? extends R> pattern) {
        return (CpgMultiEdge<S, R>.Any1ofNEdge) this.any1ofNEdges.computeIfAbsent(pattern, p -> new Any1ofNEdge());
    }

    /**
     * If true, the getter and setter method handle a list of edges. Otherwise, they handle a list of nodes.
     *
     * @return true if this edge is edge-valued, false if this edge is node-valued.
     */
    public boolean isEdgeValued() {
        return edgeValued;
    }

    @Override
    public boolean isEquivalentTo(IEdge<?, ?> other) {
        return Objects.equals(this, other);
    }

    /**
     * Gets all the edges represented by this edge, starting from the given concrete source node.
     *
     * @param s the source node
     * @return the target edges
     */
    public List<PropertyEdge<T>> getAllEdges(S s) {
        return getEdges.apply(s);
    }

    public boolean isAst() {
        return category == AST;
    }

    @Override
    public boolean isAnalytic() {
        return category == ANALYTIC;
    }

    public EdgeCategory getCategory() {
        return category;
    }

    /**
     * A {@link Any1ofNEdge} serves as a placeholder for a {@link CpgNthEdge} during transformation calculation as long as the index is not known.
     */
    public class Any1ofNEdge extends CpgNthEdge<S, T> {

        public Any1ofNEdge() {
            super(CpgMultiEdge.this, -1);
        }

        public CpgMultiEdge<S, T> getMultiEdge() {
            return CpgMultiEdge.this;
        }
    }
}
