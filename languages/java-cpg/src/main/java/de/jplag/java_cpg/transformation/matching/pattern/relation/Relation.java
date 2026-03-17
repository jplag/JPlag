package de.jplag.java_cpg.transformation.matching.pattern.relation;

import java.util.List;
import java.util.Objects;

import de.fraunhofer.aisec.cpg.graph.Node;
import de.jplag.java_cpg.transformation.matching.edges.IEdge;
import de.jplag.java_cpg.transformation.matching.pattern.GraphPattern;
import de.jplag.java_cpg.transformation.matching.pattern.Match;
import de.jplag.java_cpg.transformation.matching.pattern.NodePattern;

/**
 * This class represents a directed relation of one graph node patterns to either one other node or to a collection of
 * nodes, which is expressed by edges in the CPG.
 * @param <T> the node type of the origin graph pattern node
 * @param <R> the node type of the related graph pattern node(s)
 * @param <V> the type representing the target(s).
 */
public abstract sealed class Relation<T extends Node, R extends Node, V> permits OneToNRelation, RelatedNode {
    /**
     * The origin node pattern.
     */
    protected final NodePattern<? extends R> pattern;

    /**
     * The edge to one related node or multiple related node.
     */
    protected final IEdge<T, R> edge;

    protected Relation(NodePattern<? extends R> pattern, IEdge<T, R> edge) {
        this.pattern = pattern;
        this.edge = edge;
    }

    /**
     * Gets the pattern.
     * @return the pattern
     */
    public NodePattern<? extends R> getPattern() {
        return pattern;
    }

    /**
     * Gets the edge.
     * @return the edge
     */
    public IEdge<T, R> getEdge() {
        return edge;
    }

    /**
     * Gets the concrete related node of this relation given the concrete origin node.
     * @param from the concrete origin node
     * @return the related node
     */
    public abstract V getTarget(T from);

    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;
        if (obj == null || obj.getClass() != this.getClass())
            return false;
        var that = this.getClass().cast(obj);
        return Objects.equals(this.pattern, that.pattern) && Objects.equals(this.edge, that.edge);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pattern, edge);
    }

    /**
     * Determines whether the given {@link Relation} in the target {@link GraphPattern} is equivalent to this
     * {@link Relation}.
     * @param targetRelated relation in the target graph pattern
     * @param multipleCandidates if true, multiple similar relations may occur for the same origin node pattern; thus, also
     * equivalence of the related node pattern is checked.
     * @return true if equivalent
     */
    public abstract boolean isEquivalentTo(Relation<?, ?, ?> targetRelated, boolean multipleCandidates);

    /**
     * Matches the given {@link NodePattern} against the node related to the given concrete parent node via this relation.
     * @param pattern the node pattern for the related node
     * @param parent the concrete origin node of the relation
     * @param openMatches the in-out argument of potential matches
     * @param <C> the concrete type of origin
     */
    public abstract <C extends T> void recursiveMatch(NodePattern<C> pattern, T parent, List<Match> openMatches);

}
