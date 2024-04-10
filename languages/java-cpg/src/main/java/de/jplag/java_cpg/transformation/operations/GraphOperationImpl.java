package de.jplag.java_cpg.transformation.operations;

import de.fraunhofer.aisec.cpg.graph.Node;
import de.jplag.java_cpg.transformation.matching.edges.AnyOfNEdge;
import de.jplag.java_cpg.transformation.matching.edges.CpgEdge;
import de.jplag.java_cpg.transformation.matching.pattern.NodePattern;
import de.jplag.java_cpg.transformation.matching.pattern.WildcardGraphPattern;

/**
 * This class stores method implementations common to all types of {@link GraphOperation}s.
 * @param <T> The type of the parent node where this GraphOperation happens
 * @param <R> The type of node related to the parent node
 */
public abstract class GraphOperationImpl<T extends Node, R extends Node> implements GraphOperation {

    protected final NodePattern<? extends T> parentPattern;
    protected final CpgEdge<T, R> edge;

    /**
     * Creates a new GraphOperationImpl.
     * @param parentPattern the {@link NodePattern} where the {@link GraphOperation} sets in
     * @param edge the {@link CpgEdge} that this {@link GraphOperation} manipulates
     */
    protected GraphOperationImpl(NodePattern<? extends T> parentPattern, CpgEdge<T, R> edge) {
        this.parentPattern = parentPattern;
        this.edge = edge;
    }

    @Override
    public boolean isMultiEdged() {
        return this.edge instanceof AnyOfNEdge;
    }

    @Override
    public boolean isWildcarded() {
        return this.parentPattern instanceof WildcardGraphPattern.ParentNodePattern && this.edge instanceof WildcardGraphPattern.Edge;
    }

    public abstract <T2 extends Node> GraphOperationImpl<T2, R> fromWildcardMatch(NodePattern<? extends T2> pattern, CpgEdge<T2, R> edge);
}
