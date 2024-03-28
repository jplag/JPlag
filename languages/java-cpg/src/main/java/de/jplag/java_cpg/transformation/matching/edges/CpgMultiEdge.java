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
 * @param <T> The type of the target node
 * @param <R> The type of the related node
 */
public final class CpgMultiEdge<T extends Node, R extends Node> extends AEdge<T, R> {
    private final Function<T, List<R>> getter;
    private final Function<T, List<PropertyEdge<R>>> getEdges;
    private final TriConsumer<T, Integer, R> setter;
    private final Map<NodePattern<?>, AnyOfNEdge<T, R>> any1ofNEdges;
    private final ValueType valueType;
    private final HashMap<NodePattern<R>, Integer> sequenceNodes;

    /**
     * Creates a new CpgMultiEdge.
     * @param getter a function to get all the target nodes
     * @param setter a function to set the nth target node
     * @param valueType describes the type of representation of this edge in the CPG.
     * @param getEdges if edgeValued, then this should be a function to get all the target edges, null otherwise.
     * @param category category of the edge
     */
    public CpgMultiEdge(Function<T, List<R>> getter, TriConsumer<T, Integer, R> setter, ValueType valueType,
            Function<T, List<PropertyEdge<R>>> getEdges, EdgeCategory category) {
        super(category);
        this.getter = getter;
        this.setter = setter;
        this.valueType = valueType;
        this.getEdges = getEdges;

        this.any1ofNEdges = new HashMap<>();
        this.sequenceNodes = new HashMap<>();
    }

    /**
     * A shorthand to create an edge-valued {@link CpgMultiEdge}.
     * @param getter a function to get all the edges
     * @param <T> The type of the target node
     * @param <R> The type of the related node
     * @return the new {@link CpgMultiEdge}
     */
    public static <T extends Node, R extends Node> CpgMultiEdge<T, R> edgeValued(Function<T, List<PropertyEdge<R>>> getter) {
        return edgeValued(getter, AST);
    }

    /**
     * A shorthand to create an edge-valued {@link CpgMultiEdge}.
     * @param getter a function to get all the edges
     * @param category the category of the edge
     * @param <T> The type of the target node
     * @param <R> The type of the related node
     * @return the new {@link CpgMultiEdge}
     */
    public static <T extends Node, R extends Node> CpgMultiEdge<T, R> edgeValued(Function<T, List<PropertyEdge<R>>> getter, EdgeCategory category) {
        Function<T, List<R>> getNodes = (T node) -> getter.apply(node).stream().map(PropertyEdge::getEnd).toList();
        TriConsumer<T, Integer, R> setOne = (T node, Integer n, R value) -> getter.apply(node).get(n).setEnd(value);
        return new CpgMultiEdge<>(getNodes, setOne, ValueType.EDGE_VALUED, getter, category);
    }

    /**
     * A shorthand to create a node-valued {@link CpgMultiEdge}.
     * @param getter a function to get all the nodes
     * @param category the edge category
     * @param <T> The type of the target node
     * @param <R> The type of the related node
     * @return the new {@link CpgMultiEdge}
     */
    public static <T extends Node, R extends Node> CpgMultiEdge<T, R> nodeValued(Function<T, List<R>> getter, EdgeCategory category) {
        TriConsumer<T, Integer, R> setOne = (T node, Integer n, R value) -> getter.apply(node).set(n, value);
        return new CpgMultiEdge<>(getter, setOne, NODE_VALUED, null, category);
    }

    /**
     * A shorthand to create a node-valued AST {@link CpgMultiEdge}.
     * @param getter a function to get all the nodes
     * @param <T> The type of the target node
     * @param <R> The type of the related node
     * @return the new {@link CpgMultiEdge}
     */
    public static <T extends Node, R extends Node> CpgMultiEdge<T, R> nodeValued(Function<T, List<R>> getter) {
        return nodeValued(getter, AST);
    }

    /**
     * Gets all the targets of this edge, starting from the given concrete source node.
     * @param t the source node
     * @return the target nodes
     */
    public List<R> getAllTargets(T t) {
        return getter.apply(t);
    }

    /**
     * Get the getter function of this multi edge.
     * @return the getter
     */
    public Function<T, List<R>> getter() {
        return getter;
    }

    public void saveSequenceIndex(NodePattern<? extends R> pattern, int idx) {
        this.sequenceNodes.put((NodePattern<R>) pattern, idx);
    }

    /**
     * Gets the setter function of this multi edge.
     * @return the setter
     */
    public TriConsumer<T, Integer, R> setter() {
        return setter;
    }

    /**
     * Gets a {@link AnyOfNEdge} from this {@link CpgMultiEdge} directed at the given {@link NodePattern}.
     * @param pattern the pattern
     * @return the 'any of n' edge
     */
    public AnyOfNEdge<T, R> getAnyOfNEdgeTo(NodePattern<? extends R> pattern) {
        int index = this.sequenceNodes.getOrDefault(pattern, 0);
        return this.any1ofNEdges.computeIfAbsent(pattern, p -> new AnyOfNEdge<>(this, index));
    }

    /**
     * If true, the getter and setter method handle a list of edges. Otherwise, they handle a list of nodes.
     * @return true if this edge is edge-valued, false if this edge is node-valued.
     */
    public boolean isEdgeValued() {
        return this.valueType == ValueType.EDGE_VALUED;
    }

    @Override
    public boolean isEquivalentTo(IEdge<?, ?> other) {
        // true if it is the same edge
        return Objects.equals(this, other);
    }

    /**
     * Gets all the edges represented by this edge, starting from the given concrete source node.
     * @param t the source node
     * @return the target edges
     */
    public List<PropertyEdge<R>> getAllEdges(T t) {
        return getEdges.apply(t);
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
