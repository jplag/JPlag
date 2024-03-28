package de.jplag.java_cpg.transformation.matching.pattern.relation;

import java.util.List;
import java.util.Objects;

import de.fraunhofer.aisec.cpg.graph.Node;
import de.jplag.java_cpg.transformation.matching.edges.IEdge;
import de.jplag.java_cpg.transformation.matching.pattern.Match;
import de.jplag.java_cpg.transformation.matching.pattern.NodePattern;

public abstract sealed class Relation<T extends Node, R extends Node, V> permits OneToNRelation, RelatedNode {
    public final NodePattern<? extends R> pattern;
    public final IEdge<T, R> edge;

    protected Relation(NodePattern<? extends R> pattern, IEdge<T, R> edge) {
        this.pattern = pattern;
        this.edge = edge;
    }

    public IEdge<T, R> getEdge() {
        return edge;
    }

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

    public abstract boolean isEquivalentTo(Relation<?, ?, ?> targetRelated, boolean multipleCandidates);

    public abstract <C extends T> void recursiveMatch(NodePattern<C> pattern, T parent, List<Match> openMatches);

}
