package de.jplag.java_cpg.transformation.matching.pattern;

import de.fraunhofer.aisec.cpg.graph.Node;
import de.jplag.java_cpg.transformation.matching.edges.CpgEdge;
import de.jplag.java_cpg.transformation.matching.edges.CpgMultiEdge;
import de.jplag.java_cpg.transformation.matching.edges.CpgNthEdge;

import java.util.*;
import java.util.stream.Collectors;

/**
 * A {@link Match} stores the mapping between a {@link SimpleGraphPattern} and {@link Node}s matching the pattern.
 * Especially, a {@link WildcardGraphPattern.ParentNodePattern}'s match in the sourceGraph can be saved.
 */
public class Match implements Comparable<Match> {

    private final Map<NodePattern<? extends Node>, Node> patternToNode;
    private final GraphPattern pattern;
    private final Match parent;
    private final Map<WildcardGraphPattern.ParentNodePattern<?>, WildcardMatch<?, ?>> wildcardMatches;
    private final Map<CpgMultiEdge<?, ?>.Any1ofNEdge, CpgEdge<?, ?>> edgeMap;

    private final int childId;
    private int childCount;

    /**
     * Creates a new {@link Match}.
     *
     * @param pattern the {@link SimpleGraphPattern} of which this is a {@link Match}.
     */
    public Match(GraphPattern pattern) {
        this.pattern = pattern;
        patternToNode = new HashMap<>();
        edgeMap = new HashMap<>();
        this.childId = 0;
        this.childCount = 0;
        parent = null;
        this.wildcardMatches = new HashMap<>();
    }

    public Match(GraphPattern pattern, Match parent) {
        this.pattern = pattern;
        patternToNode = new HashMap<>(parent.patternToNode);
        edgeMap = new HashMap<>(parent.edgeMap);
        this.childCount = 0;
        this.childId = parent.childCount++;
        this.parent = parent;
        wildcardMatches = new HashMap<>(parent.wildcardMatches);
    }

    /**
     * Adds a matching concrete {@link Node} to this {@link Match}.
     *
     * @param pattern the {@link NodePattern} matching the node
     * @param node    the node
     * @param <N>     the concrete {@link Node} type
     */
    public <N extends Node> void register(NodePattern<? super N> pattern, N node) {
        patternToNode.put(pattern, node);
    }

    /**
     * Saves the concrete parent {@link Node} and edge corresponding to a {@link WildcardGraphPattern}.
     *
     * @param <S>    the concrete node type of the parent
     * @param parent the parent
     * @param edge   the edge
     */
    public <S extends Node, T extends Node> void resolveWildcard(WildcardGraphPattern.ParentNodePattern<T> parentPattern, S parent, CpgEdge<S, ? super T> edge) {
        NodePattern<S> concreteRoot = NodePattern.forNodeType(edge.getFromClass());
        concreteRoot.addRelatedNodePattern(parentPattern.getChildPattern(), edge);

        this.wildcardMatches.put(parentPattern, new WildcardMatch<>(concreteRoot, edge));
        this.patternToNode.put(concreteRoot, parent);
    }

    /**
     * Gets the count of {@link Node}s that are part of this {@link Match}.
     *
     * @return the number of nodes
     */
    public int getSize() {
        return patternToNode.size();
    }

    /**
     * Checks if the given {@link NodePattern} is contained in this {@link Match}.
     *
     * @param pattern the pattern
     * @return true if the pattern is contained in this {@link Match}
     */
    public boolean contains(NodePattern<?> pattern) {
        return patternToNode.containsKey(pattern);
    }

    /**
     * Gets the concrete {@link Node} corresponding to the given {@link NodePattern}.
     *
     * @param pattern the pattern
     * @param <T>     the node type
     * @return the concrete {@link Node}
     */
    public <T extends Node> T get(NodePattern<T> pattern) {
        return (T) patternToNode.get(pattern);
    }

    /**
     * Gets the current wildcard match.
     *
     * @return the wildcard match
     */
    public <T extends Node> WildcardMatch<?, T> getWildcardMatch(WildcardGraphPattern.ParentNodePattern<T> node) {
        WildcardMatch<?, T> wildcardMatch = (WildcardMatch<?, T>) wildcardMatches.get(node);
        return wildcardMatch;
    }

    public <S extends Node, T extends Node> CpgNthEdge<S, T> resolveAny1ofNEdge(CpgMultiEdge<S, T>.Any1ofNEdge any1ofNEdge, int index) {
        CpgNthEdge<S, T> concreteEdgePattern = new CpgNthEdge<>(any1ofNEdge.getMultiEdge(), index);
        this.edgeMap.put(any1ofNEdge, concreteEdgePattern);
        return concreteEdgePattern;
    }

    /**
     * Creates a copy of this {@link Match} in its current state.
     *
     * @return the copy
     */
    public Match copy() {
        Match copy = new Match(pattern, this);
        return copy;
    }

    public <S extends Node, T extends Node> CpgNthEdge<S, T> getEdge(CpgMultiEdge<S, T>.Any1ofNEdge any1OfNEdge) {
        // key-value pairs of this map are type-compatible
        return (CpgNthEdge<S, T>) this.edgeMap.get(any1OfNEdge);
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

    private LinkedList<Integer> getFullID() {
        LinkedList<Integer> id = Objects.isNull(this.parent) ? new LinkedList<>() : parent.getFullID();
        id.addLast(this.childId);
        return id;
    }

    @Override
    public String toString() {
        return getFullID().stream().map(Object::toString).collect(Collectors.joining("."));
    }

    public void remove(NodePattern<?> pattern) {
        this.patternToNode.remove(pattern);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Match match = (Match) o;

        if (this.pattern != match.pattern) {
            return false;
        }
        if (this.pattern.getAllIds().stream().anyMatch(id -> {
            Node patternNode1 = this.get(this.pattern.getPattern(id));
            Node patternNode2 = match.get(match.pattern.getPattern(id));
            return !Objects.equals(patternNode1,patternNode2);
        })) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = patternToNode.hashCode();
        result = 31 * result + (pattern != null ? pattern.hashCode() : 0);
        result = 31 * result + (parent != null ? parent.hashCode() : 0);
        result = 31 * result + wildcardMatches.hashCode();
        result = 31 * result + edgeMap.hashCode();
        result = 31 * result + childId;
        result = 31 * result + childCount;
        return result;
    }

    /**
     * Saves the data related to a concrete occurrence of a {@link WildcardGraphPattern}.
     *
     * @param <S>           the concrete type of the parent, specified by the edge
     * @param <T>           the concrete type of the child, specified by the edge
     * @param parentPattern A concrete {@link NodePattern} for the parent
     * @param edge          the edge
     */
    /*package-private */ public record WildcardMatch<S extends Node, T extends Node>(
        NodePattern<? extends S> parentPattern,
        CpgEdge<S, T> edge) {
    }
}
