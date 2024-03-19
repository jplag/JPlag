package de.jplag.java_cpg.transformation.operations;

import static de.jplag.java_cpg.transformation.matching.pattern.PatternUtil.desc;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fraunhofer.aisec.cpg.TranslationContext;
import de.fraunhofer.aisec.cpg.graph.Node;
import de.fraunhofer.aisec.cpg.graph.scopes.Scope;
import de.jplag.java_cpg.transformation.matching.edges.CpgEdge;
import de.jplag.java_cpg.transformation.matching.edges.CpgMultiEdge;
import de.jplag.java_cpg.transformation.matching.pattern.Match;
import de.jplag.java_cpg.transformation.matching.pattern.NodePattern;
import de.jplag.java_cpg.transformation.matching.pattern.WildcardGraphPattern;

/**
 * Replaces the target {@link de.fraunhofer.aisec.cpg.graph.Node} of an edge by another
 * {@link de.fraunhofer.aisec.cpg.graph.Node}.
 * @param <S> type of the parentPattern node, defined by the edge
 * @param <T> type of the destination node, defined by the edge
 * @author robin
 * @version $Id: $Id
 */
public final class ReplaceOperation<S extends Node, T extends Node> extends GraphOperationImpl<S, T> {

    private static final Logger logger;

    static {
        logger = LoggerFactory.getLogger(ReplaceOperation.class);
    }

    private final NodePattern<? extends T> newChildPattern;
    private final boolean disconnectEog;

    /**
     * <p>
     * Constructor for ReplaceOperation.
     * </p>
     * @param parentPattern source node of the edge
     * @param edge edge of which the target shall be replaced
     * @param newChildPattern replacement node
     * @param disconnectEog if true, the replaced element is inserted into the EOG graph at the target
     */
    public ReplaceOperation(NodePattern<? extends S> parentPattern, CpgEdge<S, T> edge, NodePattern<? extends T> newChildPattern,
            boolean disconnectEog) {
        super(parentPattern, edge);
        this.newChildPattern = newChildPattern;
        this.disconnectEog = disconnectEog;
    }

    /** {@inheritDoc} */
    @Override
    public void resolveAndApply(Match match, TranslationContext ctx) {
        S parent = match.get(parentPattern);
        // match should contain newChildPattern node because of Builder.createNewNodes()
        T newTarget = match.get(newChildPattern);

        // Replace AST edge
        T oldTarget = edge.getter().apply(parent);
        logger.debug("Replace %s by %s".formatted(desc(oldTarget), desc(newTarget)));
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

    /** {@inheritDoc} */
    @Override
    public NodePattern<?> getTarget() {
        return parentPattern;
    }

    /** {@inheritDoc} */
    @Override
    public GraphOperation instantiateWildcard(Match match) {
        WildcardGraphPattern.ParentNodePattern<T> wcParent = (WildcardGraphPattern.ParentNodePattern<T>) this.parentPattern;
        Match.WildcardMatch<?, T> wcMatch = match.getWildcardMatch(wcParent);
        return fromWildcardMatch(wcMatch);
    }

    private <S extends Node> ReplaceOperation<S, T> fromWildcardMatch(Match.WildcardMatch<S, T> wildcardMatch) {
        NodePattern<? extends S> sNodePattern = wildcardMatch.parentPattern();
        CpgEdge<S, T> edge1 = wildcardMatch.edge();
        return new ReplaceOperation<>(sNodePattern, edge1, newChildPattern, this.disconnectEog);
    }

    /** {@inheritDoc} */
    @Override
    public GraphOperation instantiateAnyOfNEdge(Match match) {
        CpgMultiEdge<S, T>.AnyOfNEdge any1ofNEdge = (CpgMultiEdge<S, T>.AnyOfNEdge) edge;
        return new ReplaceOperation<>(parentPattern, match.getEdge(this.parentPattern, any1ofNEdge), newChildPattern, this.disconnectEog);
    }

}
