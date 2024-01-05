package de.jplag.java_cpg.transformation.operations;

import de.fraunhofer.aisec.cpg.graph.Node;
import de.fraunhofer.aisec.cpg.graph.edge.Properties;
import de.fraunhofer.aisec.cpg.graph.edge.PropertyEdge;
import de.jplag.java_cpg.transformation.matching.edges.CpgNthEdge;
import de.jplag.java_cpg.transformation.matching.pattern.GraphPattern;
import de.jplag.java_cpg.transformation.matching.pattern.NodePattern;
import de.jplag.java_cpg.transformation.matching.pattern.WildcardGraphPattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.IntStream;

/**
 * Replaces the target {@link Node} of an edge by another {@link Node}.
 *
 * @param parentPattern   source node of the edge
 * @param edge            edge of which the target shall be replaced
 * @param newChildPattern replacement node
 * @param <S>             type of the parentPattern node, defined by the edge
 * @param <T>             type of the destination node, defined by the edge
 */
public record InsertOperation<S extends Node, T extends Node>(NodePattern<? extends S> parentPattern,
                                                              CpgNthEdge<S, T> edge,
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
        LOGGER.info("Insert %s into %s at position #%d".formatted(desc(newTarget), desc(parent), edge.getIndex()));

        PropertyEdge<T> newEdge = new PropertyEdge<>(parent, newTarget);
        newEdge.addProperty(Properties.INDEX, edge.getIndex());
        newEdge.addProperty(Properties.UNREACHABLE, false);

        // Set AST edge
        List<PropertyEdge<T>> edges = edge.getMultiEdge().getAllEdges(parent);
        edges.add(edge.getIndex(), newEdge);
        IntStream.range(edge.getIndex(), edges.size()).forEach(i -> edges.get(i).addProperty(Properties.INDEX, i + 1));
    }

    @Override
    public NodePattern<?> getTarget() {
        return parentPattern;
    }

    @Override
    public <S extends Node, T extends Node> GraphOperation instantiate(GraphPattern.Match.WildcardMatch<S, T> match) {
        if (!(this.parentPattern instanceof WildcardGraphPattern<?>.ParentNodePattern)) {
            return this;
        }

        throw new RuntimeException("Cannot apply InsertOperation with WildcardGraphPattern.ParentPattern as parentPattern. Use a surrounding Block instead.");
    }


}
