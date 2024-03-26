package de.jplag.java_cpg.transformation.operations;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fraunhofer.aisec.cpg.TranslationContext;
import de.fraunhofer.aisec.cpg.graph.Node;
import de.fraunhofer.aisec.cpg.graph.scopes.Scope;
import de.jplag.java_cpg.transformation.matching.edges.AnyOfNEdge;
import de.jplag.java_cpg.transformation.matching.edges.CpgEdge;
import de.jplag.java_cpg.transformation.matching.pattern.Match;
import de.jplag.java_cpg.transformation.matching.pattern.NodePattern;
import de.jplag.java_cpg.transformation.matching.pattern.WildcardGraphPattern;

/**
 * Replaces the target {@link Node} of an edge by another {@link Node}.
 * @param <T> type of the target node, defined by the edge
 * @param <R> type of the related node, defined by the edge
 */
public final class ReplaceOperation<T extends Node, R extends Node> extends GraphOperationImpl<T, R> {

    private static final Logger logger;

    static {
        logger = LoggerFactory.getLogger(ReplaceOperation.class);
    }

    private final NodePattern<? extends R> newChildPattern;
    private final boolean disconnectEog;

    /**
     * Constructs a new ReplaceOperation.
     * @param parentPattern source node of the edge
     * @param edge edge of which the target shall be replaced
     * @param newChildPattern replacement node
     * @param disconnectEog if true, the replaced element is inserted into the EOG graph at the target
     */
    public ReplaceOperation(NodePattern<? extends T> parentPattern, CpgEdge<T, R> edge, NodePattern<? extends R> newChildPattern,
            boolean disconnectEog) {
        super(parentPattern, edge);
        this.newChildPattern = newChildPattern;
        this.disconnectEog = disconnectEog;
    }

    /** {@inheritDoc} */
    @Override
    public void resolveAndApply(Match match, TranslationContext ctx) {
        T parent = match.get(parentPattern);
        // match should contain newChildPattern node because of Builder.createNewNodes()
        R newTarget = match.get(newChildPattern);

        // Replace AST edge
        R oldTarget = edge.getter().apply(parent);
        logger.debug("Replace {} by {}", oldTarget, newTarget);
        if (Objects.isNull(newTarget.getLocation())) {
            newTarget.setLocation(oldTarget.getLocation());
        }
        edge.setter().accept(parent, newTarget);

        Scope parentScope = Objects.requireNonNullElse(ctx.getScopeManager().lookupScope(parent), parent.getScope());
        newTarget.setScope(parentScope);
        Scope childScope = ctx.getScopeManager().lookupScope(newTarget);
        if (!Objects.isNull(childScope)) {
            childScope.setParent(parentScope);
        }

        if (!disconnectEog) {
            return;
        }

        TransformationUtil.transferEogPredecessor(oldTarget, newTarget);
        TransformationUtil.transferEogSuccessor(oldTarget, newTarget);
    }

    @Override
    public GraphOperation instantiateWildcard(Match match) {
        WildcardGraphPattern.ParentNodePattern<R> wcParent = (WildcardGraphPattern.ParentNodePattern<R>) this.parentPattern;
        return match.instantiateGraphOperation(wcParent, this);
    }

    public <T2 extends Node> ReplaceOperation<T2, R> fromWildcardMatch(NodePattern<? extends T2> pattern, CpgEdge<T2, R> edge) {
        return new ReplaceOperation<>(pattern, edge, this.newChildPattern, this.disconnectEog);
    }

    @Override
    public GraphOperation instantiateAnyOfNEdge(Match match) {
        AnyOfNEdge<T, R> any1ofNEdge = (AnyOfNEdge<T, R>) edge;
        return new ReplaceOperation<>(parentPattern, match.getEdge(this.parentPattern, any1ofNEdge), newChildPattern, this.disconnectEog);
    }

}
