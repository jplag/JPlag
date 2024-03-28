package de.jplag.java_cpg.transformation.matching.pattern.relation;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import de.fraunhofer.aisec.cpg.graph.Node;
import de.jplag.java_cpg.transformation.matching.edges.CpgMultiEdge;
import de.jplag.java_cpg.transformation.matching.pattern.Match;
import de.jplag.java_cpg.transformation.matching.pattern.NodePattern;

/**
 * This is a superclass for the multiple kinds of one-to-n relations.
 * @param <T> the target node type
 * @param <R> the related node type
 */
public sealed class OneToNRelation<T extends Node, R extends Node> extends Relation<T, R, List<R>> permits ForAllRelatedNode, RelatedOneToNNode {

    private final CpgMultiEdge<T, R> multiEdge;

    protected OneToNRelation(NodePattern<? extends R> pattern, CpgMultiEdge<T, R> edge) {
        super(pattern, edge);
        this.multiEdge = edge;
    }

    @Override
    public CpgMultiEdge<T, R> getEdge() {
        return this.multiEdge;
    }

    public List<R> getTarget(T from) {
        return multiEdge.getAllTargets(from);
    }

    @Override
    public boolean isEquivalentTo(Relation<?, ?, ?> targetRelated, boolean multipleCandidates) {
        return this.edge.isEquivalentTo(targetRelated.edge)
                && (!multipleCandidates || this.pattern.getRole().equals(targetRelated.pattern.getRole()));
    }

    @Override
    public <C extends T> void recursiveMatch(NodePattern<C> pattern, T parent, List<Match> openMatches) {
        List<R> candidates = getTarget(parent);
        List<Match> resultMatches = IntStream.range(0, candidates.size()).mapToObj(i -> {
            R candidate = candidates.get(i);
            ArrayList<Match> openMatchesCopy = openMatches.stream().map(Match::copy).map(match -> match.resolveAnyOfNEdge(pattern, this, i))
                    .collect(Collectors.toCollection(ArrayList::new));
            this.pattern.recursiveMatch(candidate, openMatchesCopy);
            return openMatchesCopy;
        }).flatMap(List::stream).toList();
        openMatches.clear();
        openMatches.addAll(resultMatches);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        if (!super.equals(o))
            return false;

        OneToNRelation<?, ?> that = (OneToNRelation<?, ?>) o;

        return multiEdge.equals(that.multiEdge);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + multiEdge.hashCode();
        return result;
    }
}
