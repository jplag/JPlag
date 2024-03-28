package de.jplag.java_cpg.transformation.matching.pattern.relation;

import java.util.List;

import de.fraunhofer.aisec.cpg.graph.Node;
import de.jplag.java_cpg.transformation.matching.edges.CpgMultiEdge;
import de.jplag.java_cpg.transformation.matching.pattern.Match;
import de.jplag.java_cpg.transformation.matching.pattern.NodePattern;

/**
 * A {@link ForAllRelatedNode} describes a one-to-n relation where in a match, <b>all</b> candidate nodes must match the
 * given pattern.
 * @param <T> the parent node type
 * @param <R> the related node type
 */
public final class ForAllRelatedNode<T extends Node, R extends Node> extends OneToNRelation<T, R> {

    public ForAllRelatedNode(NodePattern<? extends R> pattern, CpgMultiEdge<T, R> edge) {
        super(pattern, edge);
    }

    @Override
    public List<R> getTarget(T from) {
        return getEdge().getAllTargets(from);
    }

    @Override
    public <C extends T> void recursiveMatch(NodePattern<C> pattern, T parent, List<Match> openMatches) {
        List<? extends Node> candidates = getTarget(parent);

        for (Node candidate : candidates) {
            this.pattern.recursiveMatch(candidate, openMatches);
            openMatches.forEach(match -> match.remove(this.pattern));
        }
    }

    @Override
    public String toString() {
        return "ForAllRelatedNode[" + "pattern=" + pattern + ", " + "edge=" + edge + ']';
    }

}
