package de.jplag.java_cpg.transformation.operations;

import de.fraunhofer.aisec.cpg.graph.Node;
import de.jplag.java_cpg.transformation.matching.edges.CpgEdge;
import de.jplag.java_cpg.transformation.matching.pattern.GraphPattern;
import de.jplag.java_cpg.transformation.matching.pattern.NodePattern;
import de.jplag.java_cpg.transformation.matching.pattern.WildcardGraphPattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/**
 * Replaces the target {@link Node} of an edge by another {@link Node}.
 *
 * @param parentPattern   source node of the edge
 * @param edge            edge of which the target shall be replaced
 * @param newChildPattern replacement node
 * @param <S>             type of the parentPattern node, defined by the edge
 * @param <T>             type of the destination node, defined by the edge
 */
public record ReplaceOperation<S extends Node, T extends Node>(NodePattern<? extends S> parentPattern,
                                                               CpgEdge<S, T> edge,
                                                               NodePattern<? extends T> newChildPattern) implements GraphOperation {

    private static final Logger LOGGER;

    static {
        LOGGER = LoggerFactory.getLogger(RemoveOperation.class);
    }

    @Override
    public void apply(GraphPattern.Match<?> match) {
        S parent = match.get(parentPattern);
        // match should contain newChildPattern node because of Builder.createNewNodes()
        T newTarget = match.get(newChildPattern);

        // Replace AST edge
        T oldTarget = edge.getter().apply(parent);
        if (Objects.isNull(newTarget.getLocation())) {
            newTarget.setLocation(oldTarget.getLocation());
        }
        edge.setter().accept(parent, newTarget);
        LOGGER.info("Replace %s by %s".formatted(desc(oldTarget), desc(newTarget)));

        TransformationHelper.transferEogPredecessor(oldTarget, newTarget);
        TransformationHelper.transferEogSuccessor(oldTarget, newTarget);
    }

    @Override
    public NodePattern<?> getTarget() {
        return parentPattern;
    }

    @Override
    public <S extends Node, T extends Node> GraphOperation instantiate(GraphPattern.Match.WildcardMatch<S, T> match) {
        if (!(this.parentPattern instanceof WildcardGraphPattern<?>.ParentNodePattern && this.edge instanceof WildcardGraphPattern<?>.Edge)) {
            return this;
        }

        NodePattern<S> sNodePattern = match.parentPattern();
        CpgEdge<S, ? super T> edge1 = match.edge();
        try {
            NodePattern<? extends T> toCopy = (NodePattern<? extends T>) newChildPattern;
            return new ReplaceOperation<>(sNodePattern, edge1, toCopy);
        } catch (ClassCastException e) {
            throw new RuntimeException("The wildcard match is incompatible with the child node class %s.".formatted(newChildPattern.getClass().getSimpleName()));
        }
    }


}
