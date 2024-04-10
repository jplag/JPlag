package de.jplag.java_cpg.transformation.operations;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fraunhofer.aisec.cpg.TranslationContext;
import de.fraunhofer.aisec.cpg.graph.Node;
import de.fraunhofer.aisec.cpg.graph.scopes.Scope;
import de.jplag.java_cpg.transformation.TransformationException;
import de.jplag.java_cpg.transformation.matching.edges.CpgEdge;
import de.jplag.java_cpg.transformation.matching.pattern.Match;
import de.jplag.java_cpg.transformation.matching.pattern.NodePattern;
import de.jplag.java_cpg.transformation.matching.pattern.WildcardGraphPattern;

/**
 * Sets the target {@link Node} of a previously newly created edge to a {@link Node}.
 * @param <T> type of the parent node, defined by the edge
 * @param <R> type of the related node, defined by the edge
 */
public final class SetOperation<T extends Node, R extends Node> extends GraphOperationImpl<T, R> {
    private static final Logger logger;
    public static final String WILDCARD_ERROR_MESSAGE = "Cannot apply SetOperation with WildcardGraphPattern.ParentPattern as parentPattern.";
    public static final String MULTI_EDGE_ERROR_MESSAGE = "Cannot apply SetOperation with Any1ofNEdge.";
    private final NodePattern<? extends R> newChildPattern;

    /**
     * Creates a new {@link SetOperation}.
     * @param parentPattern the parent pattern of which a child shall be set
     * @param edge the edge relating the parent and child
     * @param newChildPattern the new child node pattern
     */
    public SetOperation(NodePattern<? extends T> parentPattern, CpgEdge<T, R> edge, NodePattern<? extends R> newChildPattern) {
        super(parentPattern, edge);
        this.newChildPattern = newChildPattern;
    }

    static {
        logger = LoggerFactory.getLogger(SetOperation.class);
    }

    @Override
    public <S2 extends Node> GraphOperationImpl<S2, R> fromWildcardMatch(NodePattern<? extends S2> pattern, CpgEdge<S2, R> edge) {
        throw new TransformationException(WILDCARD_ERROR_MESSAGE);
    }

    @Override
    public void resolveAndApply(Match match, TranslationContext ctx) {
        T parent = match.get(parentPattern);
        // match should contain newChildPattern node because of Builder.createNewNodes()
        R newChild = match.get(newChildPattern);
        logger.debug("Set {} as AST child of {}", newChild, parent);

        assert Objects.isNull(edge.getter().apply(parent));
        edge.setter().accept(parent, newChild);

        Scope parentScope = Objects.requireNonNullElse(ctx.getScopeManager().lookupScope(parent), parent.getScope());
        newChild.setScope(parentScope);
        Scope childScope = ctx.getScopeManager().lookupScope(newChild);
        if (!Objects.isNull(childScope)) {
            childScope.setParent(parentScope);
        }

        // Here, the EOG would be connected. Yet, this would be very hard because we do not have any EOG neighbors to
        // get hold onto. So, better leave SetOperations in the AstTransformationPass and let the EvaluationOrderGraphPass
        // create the EOG for us.
    }

    @Override
    public GraphOperation instantiateWildcard(Match match) {
        if (!(this.parentPattern instanceof WildcardGraphPattern.ParentNodePattern<?>)) {
            return this;
        }

        throw new TransformationException(WILDCARD_ERROR_MESSAGE);
    }

    @Override
    public GraphOperation instantiateAnyOfNEdge(Match match) {
        throw new TransformationException(MULTI_EDGE_ERROR_MESSAGE);
    }

}
