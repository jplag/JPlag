package de.jplag.java_cpg.transformation.operations;

import de.fraunhofer.aisec.cpg.TranslationContext;
import de.fraunhofer.aisec.cpg.graph.Node;
import de.fraunhofer.aisec.cpg.graph.edge.Properties;
import de.fraunhofer.aisec.cpg.graph.edge.PropertyEdge;
import de.jplag.java_cpg.transformation.TransformationException;
import de.jplag.java_cpg.transformation.matching.edges.CpgEdge;
import de.jplag.java_cpg.transformation.matching.edges.CpgMultiEdge;
import de.jplag.java_cpg.transformation.matching.edges.CpgNthEdge;
import de.jplag.java_cpg.transformation.matching.pattern.Match;
import de.jplag.java_cpg.transformation.matching.pattern.NodePattern;
import de.jplag.java_cpg.transformation.matching.pattern.WildcardGraphPattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;

/**
 * This operation removes a {@link Node} from its AST context.
 * @param <S> the parent {@link Node} type
 * @param <T> the target {@link Node} type
 */
public final class RemoveOperation<S extends Node, T extends Node> extends GraphOperationImpl<S, T> {

    private final boolean disconnectEog;

    public RemoveOperation(NodePattern<? extends S> parentPattern, CpgEdge<S, T> edge, boolean disconnectEog) {
        super(parentPattern, edge);
        this.disconnectEog = disconnectEog;
        if (Objects.isNull(parentPattern) || Objects.isNull(edge)) {
            throw new RuntimeException("Invalid RemoveOperation: the pattern root needs to be wrapped into a WildcardParentPattern.");
        }
    }

    private static final Logger LOGGER;

    static {
        LOGGER = LoggerFactory.getLogger(RemoveOperation.class);

    }

    @Override
    public void resolve(Match match, TranslationContext ctx) throws TransformationException {
        S parent = match.get(parentPattern);
        T element = edge.getter().apply(parent);
        apply(element, parent, edge, disconnectEog);
    }

    public static <S extends Node, T extends Node> void apply(T element, S parent, CpgEdge<S, T> edge, boolean disconnectEog) {
        LOGGER.debug("Remove " + element.toString());

        if (!(edge instanceof CpgNthEdge<S, T> nthEdge)) {
            edge.setter().accept(parent, null);
        } else if (nthEdge.getMultiEdge().isEdgeValued()) {
            // set edge indices of successors
            List<PropertyEdge<T>> siblingEdges = nthEdge.getMultiEdge().getAllEdges(parent);
            int index = nthEdge.getIndex();

            // remove edge
            siblingEdges.remove(siblingEdges.get(index));

            for (int i = index; i <= siblingEdges.size() - 1; i++) {
                PropertyEdge<T> sibling = siblingEdges.get(i);
                sibling.addProperty(Properties.INDEX, i);
                siblingEdges.set(i, sibling);
            }

        } else {
            //nthEdge is node-valued
            List<T> siblings = nthEdge.getMultiEdge().getAllTargets(parent);
            siblings.remove(element);
        }

        if (!disconnectEog) {
            return;
        }

        List<Node> predExits = TransformationHelper.disconnectFromPredecessor(element);
        Node succEntry = TransformationHelper.disconnectFromSuccessor(element);

        if (!Objects.isNull(succEntry) && succEntry != DummyNeighbor.getInstance()) {
            predExits.forEach(exit -> TransformationHelper.connectNewSuccessor(exit, succEntry, false));
        }
    }

    @Override
    public NodePattern<?> getTarget() {
        return parentPattern;
    }

    @Override
    public GraphOperation instantiateWildcard(Match match) {
        WildcardGraphPattern.ParentNodePattern<T> wcParent = (WildcardGraphPattern.ParentNodePattern<T>) this.parentPattern;
        Match.WildcardMatch<?, T> wildcardMatch = match.getWildcardMatch(wcParent);
        return fromWildcardMatch(wildcardMatch);
    }

    private <S extends Node, T extends Node> RemoveOperation<S, T> fromWildcardMatch(Match.WildcardMatch<S, T> wildcardMatch) {
        return new RemoveOperation<>(wildcardMatch.parentPattern(), wildcardMatch.edge(), this.disconnectEog);
    }

    @Override
    public GraphOperation instantiateAny1ofNEdge(Match match) {
        CpgMultiEdge<S, T>.Any1ofNEdge any1OfNEdge = (CpgMultiEdge<S, T>.Any1ofNEdge) edge;
        return new RemoveOperation<>(parentPattern, match.getEdge(any1OfNEdge), this.disconnectEog);
    }

}
