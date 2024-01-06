package de.jplag.java_cpg.transformation.operations;

import de.fraunhofer.aisec.cpg.graph.Node;
import de.jplag.java_cpg.transformation.matching.edges.CpgEdge;
import de.jplag.java_cpg.transformation.matching.edges.CpgMultiEdge;
import de.jplag.java_cpg.transformation.matching.pattern.GraphPattern;
import de.jplag.java_cpg.transformation.matching.pattern.NodePattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/**
 * Replaces the target {@link Node} of an edge by another {@link Node}.
 *
 * @param <S> type of the parentPattern node, defined by the edge
 * @param <T> type of the destination node, defined by the edge
 */
public final class ReplaceOperation<S extends Node, T extends Node> extends GraphOperationImpl<S, T> {

    private static final Logger LOGGER;

    static {
        LOGGER = LoggerFactory.getLogger(RemoveOperation.class);
    }

    private final NodePattern<? extends T> newChildPattern;

    /**
     * @param parentPattern   source node of the edge
     * @param edge            edge of which the target shall be replaced
     * @param newChildPattern replacement node
     */
    public ReplaceOperation(NodePattern<? extends S> parentPattern,
                            CpgEdge<S, T> edge,
                            NodePattern<? extends T> newChildPattern) {
        super(parentPattern, edge);
        this.newChildPattern = newChildPattern;
    }

    @Override
    public void apply(GraphPattern.Match<?> match) {
        S parent = match.get(parentPattern);
        // match should contain newChildPattern node because of Builder.createNewNodes()
        T newTarget = match.get(newChildPattern);

        // Replace AST edge
        T oldTarget = edge.getter().apply(parent);
        LOGGER.info("Replace %s by %s".formatted(desc(oldTarget), desc(newTarget)));
        if (Objects.isNull(newTarget.getLocation())) {
            newTarget.setLocation(oldTarget.getLocation());
        }
        edge.setter().accept(parent, newTarget);

    }

    @Override
    public NodePattern<?> getTarget() {
        return parentPattern;
    }

    @Override
    public <S extends Node, T extends Node> GraphOperation instantiateWildcard(GraphPattern.Match.WildcardMatch<S, T> match) {
        NodePattern<S> sNodePattern = match.parentPattern();
        CpgEdge<S, ? super T> edge1 = match.edge();
        try {
            NodePattern<? extends T> toCopy = (NodePattern<? extends T>) newChildPattern;
            return new ReplaceOperation<>(sNodePattern, edge1, toCopy);
        } catch (ClassCastException e) {
            throw new RuntimeException("The wildcard match is incompatible with the child node class %s.".formatted(newChildPattern.getClass().getSimpleName()));
        }
    }

    @Override
    public GraphOperation instantiateAny1ofNEdge(GraphPattern.Match<?> match) {
        CpgMultiEdge<S,T>.Any1ofNEdge any1ofNEdge = (CpgMultiEdge<S,T>.Any1ofNEdge) edge;
        return new ReplaceOperation<>(parentPattern, match.getEdge(any1ofNEdge), newChildPattern);
    }



}
