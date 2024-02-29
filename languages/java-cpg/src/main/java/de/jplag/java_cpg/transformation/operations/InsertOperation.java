package de.jplag.java_cpg.transformation.operations;

import de.fraunhofer.aisec.cpg.TranslationContext;
import de.fraunhofer.aisec.cpg.graph.Node;
import de.fraunhofer.aisec.cpg.graph.edge.Properties;
import de.fraunhofer.aisec.cpg.graph.edge.PropertyEdge;
import de.fraunhofer.aisec.cpg.graph.scopes.Scope;
import de.jplag.java_cpg.transformation.matching.edges.CpgMultiEdge;
import de.jplag.java_cpg.transformation.matching.edges.CpgNthEdge;
import de.jplag.java_cpg.transformation.matching.pattern.Match;
import de.jplag.java_cpg.transformation.matching.pattern.NodePattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

/**
 * Replaces the target {@link Node} of an edge by another {@link Node}.
 *
 * @param <S> type of the parentPattern node, defined by the edge
 * @param <T> type of the destination node, defined by the edge
 */
public final class InsertOperation<S extends Node, T extends Node> extends GraphOperationImpl<S, T> {

    private static final Logger LOGGER;

    static {
        LOGGER = LoggerFactory.getLogger(RemoveOperation.class);
    }

    private final CpgNthEdge<S, T> edge;
    private final NodePattern<? extends T> newChildPattern;
    private final boolean connectEog;


    /**
     * @param parentPattern   source node of the edge
     * @param edge            edge of which the target shall be replaced
     * @param newChildPattern replacement node
     * @param connectEog      if true, the new element will be connected to its neighbor elements in the EOG graph.
     */
    public InsertOperation(NodePattern<? extends S> parentPattern,
                           CpgNthEdge<S, T> edge,
                           NodePattern<? extends T> newChildPattern, boolean connectEog) {
        super(parentPattern, edge);
        this.edge = edge;
        this.newChildPattern = newChildPattern;
        this.connectEog = connectEog;
    }

    @Override
    public void resolve(Match match, TranslationContext ctx) {
        S parent = match.get(parentPattern);
        // match should contain newChildPattern node because of Builder.createNewNodes()
        T newTarget = match.get(newChildPattern);
        int index = edge.getIndex();
        LOGGER.debug("Insert %s into %s at position #%d".formatted(desc(newTarget), desc(parent), index));

        apply(ctx, parent, newTarget, index);


    }

    public  void apply(TranslationContext ctx, S parent, T newTarget, int index) {
        PropertyEdge<T> newEdge = new PropertyEdge<>(parent, newTarget);
        newEdge.addProperty(Properties.INDEX, index);

        // Set AST edge
        List<PropertyEdge<T>> edges = edge.getMultiEdge().getAllEdges(parent);
        edges.add(index, newEdge);
        IntStream.range(index, edges.size()).forEach(i -> edges.get(i).addProperty(Properties.INDEX, i + 1));

        Scope parentScope = Objects.requireNonNullElse(ctx.getScopeManager().lookupScope(parent), parent.getScope());
        newTarget.setScope(parentScope);

        if (!connectEog) {
            return;
        }

        if (0 == index && edges.size() > 1)  {
            // successor exists
            T previouslyFirst = edges.get(index + 1).getEnd();
            TransformationHelper.transferEogPredecessor(previouslyFirst, newTarget);
            TransformationHelper.insertBefore(newTarget, previouslyFirst);
        } else if (0 < index && index < edges.size() - 1) {
            T successor = edges.get(index + 1).getEnd();
            TransformationHelper.transferEogPredecessor(successor, newTarget);
            T predecessor = edges.get(index - 1).getEnd();
            TransformationHelper.transferEogSuccessor(predecessor, newTarget);
            TransformationHelper.insertBefore(newTarget, successor);
        } else if (index == edges.size() - 1 && edges.size() > 1) {
            // predecessor exists
            T previouslyLast = edges.get(index - 1).getEnd();
            TransformationHelper.transferEogSuccessor(previouslyLast, newTarget);
            TransformationHelper.insertAfter(newTarget, previouslyLast);

        }
    }

    @Override
    public NodePattern<?> getTarget() {
        return parentPattern;
    }

    @Override
    public GraphOperation instantiateWildcard(Match match) {
        throw new RuntimeException("Cannot apply InsertOperation with WildcardGraphPattern.ParentPattern as parentPattern. Use a surrounding Block instead.");
    }

    @Override
    public GraphOperation instantiateAny1ofNEdge(Match match) {
        CpgMultiEdge<S, T>.Any1ofNEdge any1OfNEdge = (CpgMultiEdge<S, T>.Any1ofNEdge) edge;
        CpgNthEdge<S, T> edge1 = match.getEdge(any1OfNEdge);
        if (Objects.isNull(edge1)) edge1 = new CpgNthEdge<>(any1OfNEdge.getMultiEdge(), 0);
        return new InsertOperation<>(parentPattern, edge1, newChildPattern, this.connectEog);
    }

}
