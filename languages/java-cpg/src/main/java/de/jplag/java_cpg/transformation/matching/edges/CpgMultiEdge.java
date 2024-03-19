package de.jplag.java_cpg.transformation.matching.edges;

import static de.jplag.java_cpg.transformation.matching.edges.CpgMultiEdge.ValueType.NODE_VALUED;
import static de.jplag.java_cpg.transformation.matching.edges.IEdge.EdgeCategory.AST;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

import de.fraunhofer.aisec.cpg.graph.Node;
import de.fraunhofer.aisec.cpg.graph.edge.PropertyEdge;
import de.fraunhofer.aisec.cpg.helpers.TriConsumer;
import de.jplag.java_cpg.transformation.matching.pattern.NodePattern;

/**
 * This is a wrapper for a graph edge (1:n relation).
 * @param <S> The type of the source node
 * @param <T> The type of the target node
 */
public final class CpgMultiEdge<S extends Node, T extends Node> extends AEdge<S, T> {
    private final Function<S, List<T>> getter;
    private final Function<S, List<PropertyEdge<T>>> getEdges;
    private final TriConsumer<S, Integer, T> setter;
    private final Map<NodePattern<?>, AnyOfNEdge> any1ofNEdges;
    private final ValueType valueType;

    /**
     * Creates a new CpgMultiEdge.
     * @param getter a function to get all the target nodes
     * @param setter a function to set the nth target node
     * @param valueType describes the type of representation of this edge in the CPG.
     * @param getEdges if edgeValued, then this should be a function to get all the target edges, null otherwise.
     * @param category category of the edge
     */
    public CpgMultiEdge(Function<S, List<T>> getter, TriConsumer<S, Integer, T> setter, ValueType valueType,
            Function<S, List<PropertyEdge<T>>> getEdges, EdgeCategory category) {
        super(category);
        this.getter = getter;
        this.setter = setter;
        this.valueType = valueType;
        this.getEdges = getEdges;

        this.any1ofNEdges = new HashMap<>();
    }

    /**
     * A shorthand to create an edge-valued {@link CpgMultiEdge}.
     * @param getter a function to get all the edges
     * @param <S> The type of the source node
     * @param <T> The type of the target node
     * @return the new {@link CpgMultiEdge}
     */
    public static <S extends Node, T extends Node> CpgMultiEdge<S, T> edgeValued(Function<S, List<PropertyEdge<T>>> getter) {
        return edgeValued(getter, AST);
    }

    /**
     * A shorthand to create an edge-valued {@link CpgMultiEdge}.
     * @param getter a function to get all the edges
     * @param category the category of the edge
     * @param <S> The type of the source node
     * @param <T> The type of the target node
     * @return the new {@link CpgMultiEdge}
     */
    public static <S extends Node, T extends Node> CpgMultiEdge<S, T> edgeValued(Function<S, List<PropertyEdge<T>>> getter, EdgeCategory category) {
        Function<S, List<T>> getNodes = (S node) -> getter.apply(node).stream().map(PropertyEdge::getEnd).toList();
        TriConsumer<S, Integer, T> setOne = (S node, Integer n, T value) -> getter.apply(node).get(n).setEnd(value);
        return new CpgMultiEdge<>(getNodes, setOne, ValueType.EDGE_VALUED, getter, category);
    }

    /**
     * A shorthand to create a node-valued {@link CpgMultiEdge}.
     * @param getter a function to get all the nodes
     * @param category the edge category
     * @param <S> The type of the source node
     * @param <T> The type of the target node
     * @return the new {@link CpgMultiEdge}
     */
    public static <S extends Node, T extends Node> CpgMultiEdge<S, T> nodeValued(Function<S, List<T>> getter, EdgeCategory category) {
        TriConsumer<S, Integer, T> setOne = (S node, Integer n, T value) -> getter.apply(node).set(n, value);
        return new CpgMultiEdge<>(getter, setOne, NODE_VALUED, null, category);
    }

    /**
     * A shorthand to create a node-valued AST {@link CpgMultiEdge}.
     * @param getter a function to get all the nodes
     * @param <S> The type of the source node
     * @param <T> The type of the target node
     * @return the new {@link CpgMultiEdge}
     */
    public static <S extends Node, T extends Node> CpgMultiEdge<S, T> nodeValued(Function<S, List<T>> getter) {
        return nodeValued(getter, AST);
    }

    /**
     * Gets all the targets of this edge, starting from the given concrete source node.
     * @param s the source node
     * @return the target nodes
     */
    public List<T> getAllTargets(S s) {
        return getter.apply(s);
    }

    /**
     * Get the getter function of this multi edge.
     * @return the getter
     */
    public Function<S, List<T>> getter() {
        return getter;
    }

    /**
     * Gets the setter function of this multi edge.
     * @return a {@link TriConsumer} object
     */
    public TriConsumer<S, Integer, T> setter() {
        return setter;
    }

    /**
     * Gets a {@link AnyOfNEdge} from this {@link CpgMultiEdge} directed at the given {@link NodePattern}.
     * @param pattern the pattern
     * @return the 'any of n' edge
     */
    public CpgMultiEdge<S, T>.AnyOfNEdge getAnyOfNEdgeTo(NodePattern<? extends T> pattern) {
        return this.any1ofNEdges.computeIfAbsent(pattern, p -> new AnyOfNEdge());
    }

    /**
     * If true, the getter and setter method handle a list of edges. Otherwise, they handle a list of nodes.
     * @return true if this edge is edge-valued, false if this edge is node-valued.
     */
    public boolean isEdgeValued() {
        return this.valueType == ValueType.EDGE_VALUED;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isEquivalentTo(IEdge<?, ?> other) {
        return Objects.equals(this, other);
    }

    /**
     * Gets all the edges represented by this edge, starting from the given concrete source node.
     * @param s the source node
     * @return the target edges
     */
    public List<PropertyEdge<T>> getAllEdges(S s) {
        return getEdges.apply(s);
    }

    /**
     * A {@link AnyOfNEdge} serves as a placeholder for a {@link CpgNthEdge} during transformation calculation as long as
     * the index is not known.
     */
    public class AnyOfNEdge extends CpgNthEdge<S, T> {

        /**
         * Creates a new {@link AnyOfNEdge} for the corresponding {@link CpgMultiEdge}.
         */
        public AnyOfNEdge() {
            super(CpgMultiEdge.this, -1);
        }

        /**
         * Gets the corresponding {@link CpgMultiEdge}.
         * @return the multi edge
         */
        public CpgMultiEdge<S, T> getMultiEdge() {
            return CpgMultiEdge.this;
        }
    }

    /**
     * Describes the type of connection between nodes via an edge.
     */
    public enum ValueType {
        /**
         * An edge where the targets can be accessed directly as nodes.
         */
        NODE_VALUED,
        /**
         * An edge where the target can be accessed via {@link PropertyEdge}s.
         */
        EDGE_VALUED,
        /**
         * An edge where the target is a List of nodes.
         */
        LIST_VALUED
    }
}
