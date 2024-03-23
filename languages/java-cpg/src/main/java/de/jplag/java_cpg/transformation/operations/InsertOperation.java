package de.jplag.java_cpg.transformation.operations;

import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fraunhofer.aisec.cpg.TranslationContext;
import de.fraunhofer.aisec.cpg.graph.Node;
import de.fraunhofer.aisec.cpg.graph.edge.Properties;
import de.fraunhofer.aisec.cpg.graph.edge.PropertyEdge;
import de.fraunhofer.aisec.cpg.graph.scopes.Scope;
import de.jplag.java_cpg.transformation.TransformationException;
import de.jplag.java_cpg.transformation.matching.edges.AnyOfNEdge;
import de.jplag.java_cpg.transformation.matching.edges.CpgEdge;
import de.jplag.java_cpg.transformation.matching.edges.CpgNthEdge;
import de.jplag.java_cpg.transformation.matching.pattern.Match;
import de.jplag.java_cpg.transformation.matching.pattern.NodePattern;

/**
 * Inserts the target {@link Node} into a collection of other child nodes of the parent {@link Node}.
 * @param <T> type of the parentPattern node, defined by the edge
 * @param <R> type of the target node, defined by the edge
 */
public final class InsertOperation<T extends Node, R extends Node> extends GraphOperationImpl<T, R> {

    private static final Logger logger;
    private static final String WILDCARD_ERROR_MESSAGE = "Cannot apply InsertOperation with WildcardGraphPattern.ParentPattern as parentPattern. Use a surrounding Block instead.";

    static {
        logger = LoggerFactory.getLogger(InsertOperation.class);
    }

    private final CpgNthEdge<T, R> nthEdge;
    private final NodePattern<? extends R> newChildPattern;
    private final boolean connectEog;

    /**
     * Creates a new {@link InsertOperation}.
     * @param parentPattern source node of the edge
     * @param nthEdge edge where an element shall be inserted
     * @param newChildPattern node to be inserted
     * @param connectEog if true, the new element will be connected to its neighbor elements in the EOG graph
     */
    public InsertOperation(NodePattern<? extends T> parentPattern, CpgNthEdge<T, R> nthEdge, NodePattern<? extends R> newChildPattern,
            boolean connectEog) {
        super(parentPattern, nthEdge);
        this.nthEdge = nthEdge;
        this.newChildPattern = newChildPattern;
        this.connectEog = connectEog;
    }

    @Override
    public <S2 extends Node> GraphOperationImpl<S2, R> fromWildcardMatch(NodePattern<? extends S2> pattern, CpgEdge<S2, R> edge) {
        throw new TransformationException(WILDCARD_ERROR_MESSAGE);
    }

    @Override
    public void resolveAndApply(Match match, TranslationContext ctx) {
        T parent = match.get(parentPattern);
        // match should contain newChildPattern node because of Builder.createNewNodes()
        R newTarget = match.get(newChildPattern);
        int index = nthEdge.getIndex();
        logger.debug("Insert {} into {} at position #{}}", newTarget, parent, index);

        apply(ctx, parent, newTarget, index);

    }

    /**
     * Applies the InsertOperation on the given {@link Node}s.
     * @param ctx the translation context
     * @param parent the parent node
     * @param newTarget the new child node
     * @param index the insertion index
     */
    public void apply(TranslationContext ctx, T parent, R newTarget, int index) {
        PropertyEdge<R> newEdge = new PropertyEdge<>(parent, newTarget);
        newEdge.addProperty(Properties.INDEX, index);

        // Set AST edge
        List<PropertyEdge<R>> edges = nthEdge.getMultiEdge().getAllEdges(parent);
        edges.add(index, newEdge);
        IntStream.range(index, edges.size()).forEach(i -> edges.get(i).addProperty(Properties.INDEX, i + 1));

        Scope parentScope = Objects.requireNonNullElse(ctx.getScopeManager().lookupScope(parent), parent.getScope());
        newTarget.setScope(parentScope);
        Scope childScope = ctx.getScopeManager().lookupScope(newTarget);
        if (!Objects.isNull(childScope)) {
            childScope.setParent(parentScope);
        }

        if (!connectEog) {
            return;
        }

        if (0 == index && edges.size() > 1) {
            // successor exists
            R previouslyFirst = edges.get(index + 1).getEnd();
            TransformationUtil.transferEogPredecessor(previouslyFirst, newTarget);
            TransformationUtil.insertBefore(newTarget, previouslyFirst);
        } else if (0 < index && index < edges.size() - 1) {
            R successor = edges.get(index + 1).getEnd();
            TransformationUtil.transferEogPredecessor(successor, newTarget);
            R predecessor = edges.get(index - 1).getEnd();
            TransformationUtil.transferEogSuccessor(predecessor, newTarget);
            TransformationUtil.insertBefore(newTarget, successor);
        } else if (index == edges.size() - 1 && edges.size() > 1) {
            // predecessor exists
            R previouslyLast = edges.get(index - 1).getEnd();
            TransformationUtil.transferEogSuccessor(previouslyLast, newTarget);
            TransformationUtil.insertAfter(newTarget, previouslyLast);

        }
    }

    @Override
    public GraphOperation instantiateWildcard(Match match) {
        throw new TransformationException(WILDCARD_ERROR_MESSAGE);
    }

    @Override
    public GraphOperation instantiateAnyOfNEdge(Match match) {
        AnyOfNEdge<T, R> anyOfNEdge = (AnyOfNEdge<T, R>) nthEdge;
        CpgNthEdge<T, R> edge1 = match.getEdge(this.parentPattern, anyOfNEdge);
        if (Objects.isNull(edge1)) {
            edge1 = new CpgNthEdge<>(anyOfNEdge.getMultiEdge(), anyOfNEdge.getMinimalIndex());
        }
        return new InsertOperation<>(parentPattern, edge1, newChildPattern, this.connectEog);
    }

}
