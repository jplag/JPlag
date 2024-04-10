package de.jplag.java_cpg.transformation.matching.pattern.relation;

import java.util.List;
import java.util.Objects;

import de.fraunhofer.aisec.cpg.graph.Node;
import de.jplag.java_cpg.transformation.matching.edges.CpgEdge;
import de.jplag.java_cpg.transformation.matching.pattern.Match;
import de.jplag.java_cpg.transformation.matching.pattern.NodePattern;

/**
 * Pair of a node patterns of a related node and a function to get from a reference node to a candidate related node.
 * @param <T> type of the target node
 * @param <R> type of the related node
 */
public final class RelatedNode<T extends Node, R extends Node> extends Relation<T, R, R> {

    private final CpgEdge<T, R> cpgEdge;

    /**
     * @param pattern the patterns describing the related node
     * @param edge edge to get a related node given a reference node
     */
    public RelatedNode(NodePattern<? extends R> pattern, CpgEdge<T, R> edge) {
        super(pattern, edge);
        this.cpgEdge = edge;
    }

    @Override
    public CpgEdge<T, R> getEdge() {
        return cpgEdge;
    }

    public R getTarget(T from) {
        return cpgEdge.getRelated(from);
    }

    public <C extends T> void recursiveMatch(NodePattern<C> pattern, T parent, List<Match> openMatches) {
        R candidateNode = getTarget(parent);

        if (Objects.isNull(candidateNode)) {
            openMatches.clear();
        } else {
            this.pattern.recursiveMatch(candidateNode, openMatches);
        }
    }

    @Override
    public boolean isEquivalentTo(Relation<?, ?, ?> targetRelated, boolean multipleCandidates) {
        return this.cpgEdge.isEquivalentTo(targetRelated.edge);
    }

    @Override
    public String toString() {
        return "RelatedNode{%s}".formatted(pattern.toString());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        if (!super.equals(o))
            return false;

        RelatedNode<?, ?> that = (RelatedNode<?, ?>) o;

        return cpgEdge.equals(that.cpgEdge);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + cpgEdge.hashCode();
        return result;
    }
}
