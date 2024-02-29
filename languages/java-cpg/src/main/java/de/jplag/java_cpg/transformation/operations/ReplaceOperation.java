package de.jplag.java_cpg.transformation.operations;

import de.fraunhofer.aisec.cpg.TranslationContext;
import de.fraunhofer.aisec.cpg.graph.Node;
import de.fraunhofer.aisec.cpg.graph.scopes.Scope;
import de.jplag.java_cpg.transformation.matching.edges.CpgEdge;
import de.jplag.java_cpg.transformation.matching.edges.CpgMultiEdge;
import de.jplag.java_cpg.transformation.matching.pattern.Match;
import de.jplag.java_cpg.transformation.matching.pattern.NodePattern;
import de.jplag.java_cpg.transformation.matching.pattern.WildcardGraphPattern;
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
        LOGGER = LoggerFactory.getLogger(ReplaceOperation.class);
    }

    private final NodePattern<? extends T> newChildPattern;
    private final boolean disconnectEog;

    /**
     * @param parentPattern   source node of the edge
     * @param edge            edge of which the target shall be replaced
     * @param newChildPattern replacement node
     * @param disconnectEog   if true, the replaced element is inserted into the EOG graph at the target
     */
    public ReplaceOperation(NodePattern<? extends S> parentPattern,
                            CpgEdge<S, T> edge,
                            NodePattern<? extends T> newChildPattern, boolean disconnectEog) {
        super(parentPattern, edge);
        this.newChildPattern = newChildPattern;
        this.disconnectEog = disconnectEog;
    }

    @Override
    public void resolve(Match match, TranslationContext ctx) {
        S parent = match.get(parentPattern);
        // match should contain newChildPattern node because of Builder.createNewNodes()
        T newTarget = match.get(newChildPattern);

        // Replace AST edge
        T oldTarget = edge.getter().apply(parent);
        LOGGER.debug("Replace %s by %s".formatted(desc(oldTarget), desc(newTarget)));
        if (Objects.isNull(newTarget.getLocation())) {
            newTarget.setLocation(oldTarget.getLocation());
        }
        edge.setter().accept(parent, newTarget);

        Scope parentScope = Objects.requireNonNullElse(ctx.getScopeManager().lookupScope(parent), parent.getScope());
        newTarget.setScope(parentScope);

        if (!disconnectEog) {
            return;
        }

        TransformationHelper.transferEogPredecessor(oldTarget, newTarget);
        TransformationHelper.transferEogSuccessor(oldTarget, newTarget);
    }

    @Override
    public NodePattern<?> getTarget() {
        return parentPattern;
    }

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

    @Override
    public GraphOperation instantiateAny1ofNEdge(Match match) {
        CpgMultiEdge<S,T>.Any1ofNEdge any1ofNEdge = (CpgMultiEdge<S,T>.Any1ofNEdge) edge;
        return new ReplaceOperation<>(parentPattern, match.getEdge(any1ofNEdge), newChildPattern, this.disconnectEog);
    }



}
