package de.jplag.java_cpg.transformation.matching.pattern.relation;

import java.util.List;
import java.util.Objects;

import de.fraunhofer.aisec.cpg.graph.Node;
import de.jplag.java_cpg.transformation.matching.edges.IEdge;
import de.jplag.java_cpg.transformation.matching.pattern.Match;
import de.jplag.java_cpg.transformation.matching.pattern.NodePattern;

/**
 * This class models the relation between a node and a related node.
 * @param <T> the type of the source node
 * @param <R> the type of the related node
 * @param <V> the target value
 */
public abstract sealed class Relation<T extends Node, R extends Node, V> permits OneToNRelation, RelatedNode {

    /**
     * The pattern of the related node.
     */
    public final NodePattern<? extends R> pattern;

    /**
     * The edge connecting the source node to the related node.
     */
    public final IEdge<T, R> edge;

    /**
     * Creates a new Relation with the given pattern and edge.
     * @param pattern the pattern of the related node
     * @param edge the edge connecting the source node to the related node
     */
    protected Relation(NodePattern<? extends R> pattern, IEdge<T, R> edge) {
        this.pattern = pattern;
        this.edge = edge;
    }

    /**
     * Gets the edge connecting the source node to the related node.
     * @return the edge
     */
    public IEdge<T, R> getEdge() {
        return edge;
    }

    /**
     * Gets the pattern of the related node.
     * @param from the source node
     * @return the pattern of the related node
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
     * Checks whether this relation is equivalent to the given relation.
     * @param targetRelated the relation to compare to
     * @param multipleCandidates true iff there are multiple candidates for the related node of this relation, false
     * otherwise.
     * @return true iff this relation is equivalent to the given relation, false otherwise
     */
    public abstract boolean isEquivalentTo(Relation<?, ?, ?> targetRelated, boolean multipleCandidates);

    /**
     * Recursively matches this relation, starting from the given parent node and the open matches of the current match.
     * @param pattern the pattern of the related node to match
     * @param parent the parent node to start the match from
     * @param openMatches the open matches of the current match
     * @param <C> the type of the related node to match
     */
    public abstract <C extends T> void recursiveMatch(NodePattern<C> pattern, T parent, List<Match> openMatches);

}
