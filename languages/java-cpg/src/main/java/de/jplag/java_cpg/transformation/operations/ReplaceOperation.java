package de.jplag.java_cpg.transformation.operations;

import de.fraunhofer.aisec.cpg.graph.Node;
import de.fraunhofer.aisec.cpg.graph.edge.PropertyEdge;
import de.fraunhofer.aisec.cpg.helpers.SubgraphWalker;
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
        LOGGER.info("Replace %s by %s".formatted(oldTarget.toString(), newTarget.toString()));

        // Replace EOG edges
        SubgraphWalker.Border eogOldBorders = TransformationHelper.getEogBorders(oldTarget);
        SubgraphWalker.Border eogNewBorders = TransformationHelper.getEogBorders(newTarget);

        var oldEntry = eogOldBorders.getEntries().get(0);
        var newEntry = eogNewBorders.getEntries().get(0);
        var predecessor = oldEntry.getPrevEOG().get(0);
        PropertyEdge<Node> predEogFwEdge = predecessor.getNextEOGEdges().stream()
            .filter(e -> e.getEnd().equals(oldEntry)).findFirst().get();

        if (predEogFwEdge.getEnd().equals(oldEntry)) {
            predEogFwEdge.setEnd(newEntry);
            newEntry.getPrevEOGEdges().removeIf(e -> !TransformationHelper.isAstChild(newTarget, e.getEnd()));
            newEntry.addPrevEOG(predEogFwEdge);
            oldEntry.removePrevEOGEntry(predecessor);
        }
        // TODO: Revise for nodes with multiple exits (e.g. if-else, switch)
        Node oldExit = eogOldBorders.getExits().get(0);
        Node newExit = eogNewBorders.getExits().get(0);
        Node successor = oldExit.getNextEOG().get(0);
        PropertyEdge<Node> succEogBwEdge = successor.getPrevEOGEdges().get(0);
        if (succEogBwEdge.getStart().equals(oldExit)) {
            succEogBwEdge.setStart(newExit);
            newExit.getNextEOGEdges().removeIf(e -> !TransformationHelper.isAstChild(newTarget, e.getStart()));
            newExit.addNextEOG(succEogBwEdge);
            oldExit.getNextEOGEdges().removeIf(e -> e.getEnd().equals(successor));
        }

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
