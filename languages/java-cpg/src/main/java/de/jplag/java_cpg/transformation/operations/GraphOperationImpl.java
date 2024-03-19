package de.jplag.java_cpg.transformation.operations;

import de.fraunhofer.aisec.cpg.graph.Node;
import de.jplag.java_cpg.transformation.matching.edges.CpgEdge;
import de.jplag.java_cpg.transformation.matching.edges.CpgMultiEdge;
import de.jplag.java_cpg.transformation.matching.pattern.NodePattern;
import de.jplag.java_cpg.transformation.matching.pattern.WildcardGraphPattern;

/**
 * This class stores method implementations common to all types of
 * {@link de.jplag.java_cpg.transformation.operations.GraphOperation}s.
 * @param <S> The type of the parent node where this GraphOperation happens
 * @param <T> The type of node related to the parent node
 */
public abstract class GraphOperationImpl<S extends Node, T extends Node> implements GraphOperation {

    protected final NodePattern<? extends S> parentPattern;
    protected final CpgEdge<S, T> edge;

    /**
     * Creates a new GraphOperationImpl.
     * @param parentPattern the {@link NodePattern} where the {@link GraphOperation} sets in
     * @param edge the {@link CpgEdge} that this {@link GraphOperation} manipulates
     */
    protected GraphOperationImpl(NodePattern<? extends S> parentPattern, CpgEdge<S, T> edge) {
        this.parentPattern = parentPattern;
        this.edge = edge;
    }

    @Override
    public boolean isMultiEdged() {
        return this.edge instanceof CpgMultiEdge<S, ? super T>.AnyOfNEdge;
    }

    @Override
    public boolean isWildcarded() {
        return this.parentPattern instanceof WildcardGraphPattern.ParentNodePattern && this.edge instanceof WildcardGraphPattern.Edge;
    }
}
