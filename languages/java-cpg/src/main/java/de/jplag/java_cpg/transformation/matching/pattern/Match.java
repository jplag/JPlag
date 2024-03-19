package de.jplag.java_cpg.transformation.matching.pattern;

import static de.jplag.java_cpg.transformation.matching.pattern.PatternUtil.desc;

import java.util.*;
import java.util.stream.Collectors;

import de.fraunhofer.aisec.cpg.graph.Node;
import de.jplag.java_cpg.transformation.matching.edges.CpgEdge;
import de.jplag.java_cpg.transformation.matching.edges.CpgMultiEdge;
import de.jplag.java_cpg.transformation.matching.edges.CpgMultiEdge.AnyOfNEdge;
import de.jplag.java_cpg.transformation.matching.edges.CpgNthEdge;
import de.jplag.java_cpg.transformation.matching.pattern.WildcardGraphPattern.ParentNodePattern;

/**
 * A {@link Match} stores the mapping between a {@link GraphPattern} and {@link Node}s matching the pattern. Especially,
 * a {@link ParentNodePattern}'s match in the sourceGraph can be saved.
 */
public class Match implements Comparable<Match> {

    private final Map<NodePattern<? extends Node>, Node> patternToNode;
    private final GraphPattern pattern;
    private final Match parent;
    private final Map<ParentNodePattern<?>, WildcardMatch<?, ?>> wildcardMatches;
    private final Map<EdgeMapKey, CpgEdge<?, ?>> edgeMap;

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
        parent = null;
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
     * Saves the concrete parent {@link Node} and edge corresponding to a {@link WildcardGraphPattern}.
     * @param parentPattern the parent node pattern
     * @param parent the concrete parent node
     * @param edge the edge
     * @param <S> the node type of the parent as specified by the edge
     * @param <T> the node type of the child as specified by the edge
     */
    public <S extends Node, T extends Node> void resolveWildcard(ParentNodePattern<T> parentPattern, S parent, CpgEdge<S, ? super T> edge) {
        NodePattern<S> concreteRoot = NodePattern.forNodeType(edge.getSourceClass());
        concreteRoot.addRelatedNodePattern(parentPattern.getChildPattern(), edge);

        this.wildcardMatches.put(parentPattern, new WildcardMatch<>(concreteRoot, edge));
        this.patternToNode.put(concreteRoot, parent);
    }

    /**
     * Gets the count of {@link Node}s that are part of this {@link Match}.
     * @return the number of nodes
     */
    public int getSize() {
        return patternToNode.size();
    }

    /**
     * Checks if the given {@link NodePattern} is contained in this {@link Match}.
     * @param pattern the pattern
     * @return true if the pattern is contained in this {@link Match}
     */
    public boolean contains(NodePattern<?> pattern) {
        return patternToNode.containsKey(pattern);
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
     * Gets the current wildcard match for the given {@link ParentNodePattern}.
     * @param node the wildcard parent node pattern
     * @return the wildcard match for the pattern
     * @param <T> the node type of the child
     */
    public <T extends Node> WildcardMatch<?, T> getWildcardMatch(ParentNodePattern<T> node) {
        WildcardMatch<?, T> wildcardMatch = (WildcardMatch<?, T>) wildcardMatches.get(node);
        return wildcardMatch;
    }

    /**
     * Resolves an {@link AnyOfNEdge} to a concrete {@link CpgNthEdge}.
     * @param parent the parent node pattern
     * @param relation the relation object
     * @param index the child index
     * @param <S> the parent node type
     * @param <T> the child node type
     * @return the nth edge
     */
    public <S extends Node, T extends Node> CpgNthEdge<S, T> resolveAnyOfNEdge(NodePattern<?> parent, NodePattern.RelatedOneToNNode<S, T> relation,
            int index) {
        CpgMultiEdge<S, T>.AnyOfNEdge any1ofNEdge = relation.edge().getAnyOfNEdgeTo(relation.pattern());
        CpgNthEdge<S, T> concreteEdgePattern = new CpgNthEdge<>(any1ofNEdge.getMultiEdge(), index);
        EdgeMapKey key = new EdgeMapKey(parent, any1ofNEdge);
        this.edgeMap.put(key, concreteEdgePattern);
        return concreteEdgePattern;
    }

    /**
     * Creates a copy of this {@link Match} in its current state.
     * @return the copy
     */
    public Match copy() {
        Match copy = new Match(pattern, this);
        return copy;
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
        return getFullID().stream().map(Object::toString).collect(Collectors.joining("."))
                + (getRepresentingNode() == null ? "" : "[%s]".formatted(desc(getRepresentingNode())));
    }

    /**
     * Removes a node from this {@link Match}.
     * @param pattern the pattern
     */
    public void remove(NodePattern<?> pattern) {
        this.patternToNode.remove(pattern);
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
        if (this.pattern.getAllIds().stream().anyMatch(id -> {
            Node patternNode1 = this.get(this.pattern.getPattern(id));
            Node patternNode2 = match.get(match.pattern.getPattern(id));
            return !Objects.equals(patternNode1, patternNode2);
        })) {
            return false;
        }

        if (this.wildcardMatches.keySet().stream().anyMatch(key -> {
            WildcardMatch<?, ?> wildcardMatch1 = this.wildcardMatches.get(key);
            WildcardMatch<?, ?> wildcardMatch2 = match.wildcardMatches.get(key);
            return !Objects.equals(wildcardMatch1, wildcardMatch2);
        }))
            return false;

        if (this.edgeMap.keySet().stream().anyMatch(key -> {
            CpgEdge<?, ?> nthEdge1 = this.edgeMap.get(key);
            CpgEdge<?, ?> nthEdge2 = match.edgeMap.get(key);
            return !Objects.equals(nthEdge1, nthEdge2);
        }))
            return false;

        return true;
    }

    /**
     * Gets the concrete {@link CpgNthEdge} for a {@link AnyOfNEdge} in this {@link Match}.
     * @param sourcePattern the source pattern
     * @param edge the any-of-n edge
     * @param <S> the source node type
     * @param <T> the target node type
     * @return the nth edge
     */
    public <S extends Node, T extends Node> CpgNthEdge<S, T> getEdge(NodePattern<? extends S> sourcePattern, AnyOfNEdge edge) {
        return (CpgNthEdge<S, T>) this.edgeMap.get(new EdgeMapKey(sourcePattern, edge));
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
     * Saves the data related to a concrete occurrence of a {@link WildcardGraphPattern}.
     * @param <S> the concrete type of the parent, specified by the edge
     * @param <T> the concrete type of the child, specified by the edge
     * @param parentPattern A concrete {@link NodePattern} for the parent
     * @param edge the edge
     */
    /* package-private */ public record WildcardMatch<S extends Node, T extends Node>(NodePattern<? extends S> parentPattern, CpgEdge<S, T> edge) {
    }

    private record EdgeMapKey(NodePattern<?> parent, AnyOfNEdge edge) {

    }
}
