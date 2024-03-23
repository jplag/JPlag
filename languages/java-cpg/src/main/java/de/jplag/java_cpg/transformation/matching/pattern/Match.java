package de.jplag.java_cpg.transformation.matching.pattern;

import static de.jplag.java_cpg.transformation.matching.pattern.PatternUtil.desc;

import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import de.fraunhofer.aisec.cpg.graph.Node;
import de.jplag.java_cpg.transformation.matching.edges.AnyOfNEdge;
import de.jplag.java_cpg.transformation.matching.edges.CpgEdge;
import de.jplag.java_cpg.transformation.matching.edges.CpgNthEdge;
import de.jplag.java_cpg.transformation.matching.pattern.WildcardGraphPattern.ParentNodePattern;
import de.jplag.java_cpg.transformation.matching.pattern.relation.OneToNRelation;
import de.jplag.java_cpg.transformation.matching.pattern.relation.RelatedNode;
import de.jplag.java_cpg.transformation.operations.GraphOperation;
import de.jplag.java_cpg.transformation.operations.GraphOperationImpl;

/**
 * A {@link Match} stores the mapping between a {@link GraphPattern} and {@link Node}s matching the pattern. Especially,
 * a {@link ParentNodePattern}'s match in the sourceGraph can be saved.
 */
public class Match implements Comparable<Match> {

    private final Map<NodePattern<? extends Node>, Node> patternToNode;
    private final GraphPattern pattern;
    private final Match parent;
    private final Map<ParentNodePattern<?>, WildcardMatch<?, ?>> wildcardMatches;
    private final Map<EdgeMapKey<?, ?>, CpgEdge<?, ?>> edgeMap;

    private final int childId;
    private int childCount;

    /**
     * Creates a new {@link Match}.
     * @param pattern the {@link SimpleGraphPattern} of which this is a {@link Match}.
     */
    public Match(GraphPattern pattern) {
        this.pattern = pattern;
        patternToNode = new HashMap<>();
        edgeMap = new HashMap<>();
        this.childId = 0;
        this.childCount = 0;
        parent = this;
        this.wildcardMatches = new HashMap<>();
    }

    /**
     * Creates a new {@link Match}.
     * @param pattern the {@link GraphPattern} that this is a match of
     * @param parent the parent {@link Match}
     */
    public Match(GraphPattern pattern, Match parent) {
        this.pattern = pattern;
        patternToNode = new HashMap<>(parent.patternToNode);
        edgeMap = new HashMap<>(parent.edgeMap);
        this.childCount = 0;
        this.childId = parent.childCount++;
        this.parent = parent;
        wildcardMatches = new HashMap<>(parent.wildcardMatches);
    }

    @Override
    public int compareTo(Match o) {
        Iterator<Integer> thisId = this.getFullID().iterator();
        Iterator<Integer> otherId = o.getFullID().iterator();

        while (true) {
            if (!thisId.hasNext() || !otherId.hasNext()) {
                return (thisId.hasNext() ? 1 : 0) + (otherId.hasNext() ? -1 : 0);
            }

            Integer thisNext = thisId.next();
            Integer otherNext = otherId.next();
            if (!thisNext.equals(otherNext)) {
                return thisNext - otherNext;
            }
        }
    }

    /**
     * Checks if the given {@link NodePattern} is contained in this {@link Match}.
     * @param pattern the patterns
     * @return true if the pattern is contained in this {@link Match}
     */
    public boolean contains(NodePattern<?> pattern) {
        return patternToNode.containsKey(pattern);
    }

    /**
     * Creates a copy of this {@link Match} in its current state.
     * @return the copy
     */
    public Match copy() {
        return new Match(pattern, this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        Match match = (Match) o;

        if (this.pattern != match.pattern) {
            return false;
        }
        if (this.pattern.getAllRoles().stream().anyMatch(role -> {
            Node patternNode1 = this.get(this.pattern.getPattern(role));
            Node patternNode2 = match.get(match.pattern.getPattern(role));
            return !Objects.equals(patternNode1, patternNode2);
        })) {
            return false;
        }

        boolean wildcardMismatch = !this.wildcardMatches.keySet().stream()
                .allMatch(key -> Objects.equals(this.wildcardMatches.get(key), match.wildcardMatches.get(key)));
        if (wildcardMismatch) {
            return false;
        }

        return this.edgeMap.keySet().stream().allMatch(key -> Objects.equals(this.edgeMap.get(key), match.edgeMap.get(key)));
    }

    @Override
    public int hashCode() {
        int result = pattern.hashCode();
        result = 31 * result + parent.hashCode();
        return result;
    }

    /**
     * Gets the concrete {@link Node} corresponding to the given {@link NodePattern}.
     * @param pattern the pattern
     * @param <T> the node type
     * @return the concrete {@link Node}
     */
    public <T extends Node> T get(NodePattern<T> pattern) {
        return (T) patternToNode.get(pattern);
    }

    /**
     * Gets the concrete {@link CpgNthEdge} for a {@link AnyOfNEdge} in this {@link Match}.
     * @param sourcePattern the source pattern
     * @param edge the any-of-n edge
     * @param <S> the source node type
     * @param <T> the target node type
     * @return the nth edge
     */
    public <S extends Node, T extends Node> CpgNthEdge<S, T> getEdge(NodePattern<? extends S> sourcePattern, AnyOfNEdge<S, T> edge) {
        return (CpgNthEdge<S, T>) this.edgeMap.get(new EdgeMapKey<>(sourcePattern, edge));
    }

    private LinkedList<Integer> getFullID() {
        LinkedList<Integer> id = Objects.equals(this, this.parent) ? new LinkedList<>() : parent.getFullID();
        id.addLast(this.childId);
        return id;
    }

    /**
     * Gets the representing {@link Node} of this {@link Match}.
     * @return the representing node.
     */
    public Node getRepresentingNode() {
        NodePattern<?> representingNodePattern = this.pattern.getRepresentingNode();
        if (Objects.isNull(representingNodePattern)) {
            return null;
        }
        return this.get(representingNodePattern);
    }

    /**
     * Gets the count of {@link Node}s that are part of this {@link Match}.
     * @return the number of nodes
     */
    public int getSize() {
        return patternToNode.size();
    }

    public <T extends Node> GraphOperation instantiateGraphOperation(ParentNodePattern<T> wcParent, GraphOperationImpl<?, T> wildcardedOperation) {
        WildcardMatch<?, T> wildcardMatch = (WildcardMatch<?, T>) this.wildcardMatches.get(wcParent);
        return wildcardMatch.instantiateGraphOperation(wildcardedOperation::fromWildcardMatch);
    }

    /**
     * Adds a matching concrete {@link Node} to this {@link Match}.
     * @param pattern the {@link NodePattern} matching the node
     * @param node the node
     * @param <N> the concrete {@link Node} type
     */
    public <N extends Node> void register(NodePattern<? super N> pattern, N node) {
        patternToNode.put(pattern, node);
    }

    /**
     * Removes a node from this {@link Match}.
     * @param pattern the pattern
     */
    public void remove(NodePattern<?> pattern) {
        this.patternToNode.remove(pattern);
    }

    /**
     * Resolves an {@link AnyOfNEdge} to a concrete {@link CpgNthEdge}.
     * @param parent the parent node pattern
     * @param relation the relation object
     * @param index the child index
     * @param <T> the parent node type
     * @param <R> the child node type
     * @return the nth edge
     */
    public <T extends Node, R extends Node, C extends T> Match resolveAnyOfNEdge(NodePattern<C> parent, OneToNRelation<T, R> relation, int index) {
        AnyOfNEdge<T, R> anyOfNEdge = relation.getEdge().getAnyOfNEdgeTo(relation.pattern);
        CpgNthEdge<T, R> concreteEdgePattern = new CpgNthEdge<>(anyOfNEdge.getMultiEdge(), index);
        EdgeMapKey<T, R> key = new EdgeMapKey<>(parent, anyOfNEdge);
        this.edgeMap.put(key, concreteEdgePattern);
        return this;
    }

    /**
     * Saves the concrete parent {@link Node} and edge corresponding to a {@link WildcardGraphPattern}.
     * @param parentPattern the parent node pattern
     * @param parent the concrete parent node
     * @param edge the edge
     * @param <S> the node type of the parent as specified by the edge
     * @param <T> the node type of the child as specified by the edge
     */
    public <S extends Node, T extends Node> void resolveWildcard(ParentNodePattern<T> parentPattern, S parent, CpgEdge<S, ? super T> edge) {
        NodePattern<S> concreteRoot = NodePattern.forNodeType(edge.getSourceClass());
        concreteRoot.addRelation(new RelatedNode<>(parentPattern.getChildPattern(), edge));

        this.wildcardMatches.put(parentPattern, new WildcardMatch<>(concreteRoot, edge));
        this.patternToNode.put(concreteRoot, parent);
    }

    @Override
    public String toString() {
        return getFullID().stream().map(Object::toString).collect(Collectors.joining("."))
                + (getRepresentingNode() == null ? "" : "[%s]".formatted(desc(getRepresentingNode())));
    }

    /**
     * Saves the data related to a concrete occurrence of a {@link WildcardGraphPattern}.
     * @param <T> the concrete type of the child, specified by the edge
     */
    public static final class WildcardMatch<S extends Node, T extends Node> {
        private final NodePattern<? extends S> parentPattern;
        private final CpgEdge<S, T> edge;

        /**
         * @param parentPattern A concrete {@link NodePattern} for the parent
         * @param edge the edge
         */
        public WildcardMatch(NodePattern<? extends S> parentPattern, CpgEdge<S, T> edge) {
            this.parentPattern = parentPattern;
            this.edge = edge;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this)
                return true;
            if (obj == null || obj.getClass() != this.getClass())
                return false;
            var that = (WildcardMatch) obj;
            return Objects.equals(this.parentPattern, that.parentPattern) && Objects.equals(this.edge, that.edge);
        }

        @Override
        public int hashCode() {
            return Objects.hash(parentPattern, edge);
        }

        public GraphOperation instantiateGraphOperation(BiFunction<NodePattern<? extends S>, CpgEdge<S, T>, GraphOperation> factoryMethod) {
            return factoryMethod.apply(this.parentPattern, this.edge);
        }

        @Override
        public String toString() {
            return "WildcardMatch[" + "parentPattern=" + parentPattern + ", " + "edge=" + edge + ']';
        }

    }

    private static final class EdgeMapKey<T extends Node, R extends Node> {
        private final NodePattern<? extends T> parent;
        private final AnyOfNEdge<T, R> edge;

        private <C extends T> EdgeMapKey(NodePattern<C> parent, AnyOfNEdge<T, R> edge) {
            this.parent = parent;
            this.edge = edge;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this)
                return true;
            if (obj == null || obj.getClass() != this.getClass())
                return false;
            var that = (EdgeMapKey) obj;
            return Objects.equals(this.parent, that.parent) && Objects.equals(this.edge, that.edge);
        }

        @Override
        public int hashCode() {
            return Objects.hash(parent, edge);
        }

        @Override
        public String toString() {
            return "EdgeMapKey[" + "parent=" + parent + ", " + "edge=" + edge + ']';
        }

    }
}
