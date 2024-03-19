package de.jplag.java_cpg.transformation.operations;

import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fraunhofer.aisec.cpg.TranslationContext;
import de.fraunhofer.aisec.cpg.graph.Node;
import de.fraunhofer.aisec.cpg.graph.edge.Properties;
import de.fraunhofer.aisec.cpg.graph.edge.PropertyEdge;
import de.jplag.java_cpg.transformation.matching.edges.CpgEdge;
import de.jplag.java_cpg.transformation.matching.edges.CpgMultiEdge;
import de.jplag.java_cpg.transformation.matching.edges.CpgNthEdge;
import de.jplag.java_cpg.transformation.matching.pattern.Match;
import de.jplag.java_cpg.transformation.matching.pattern.NodePattern;
import de.jplag.java_cpg.transformation.matching.pattern.WildcardGraphPattern;

/**
 * This operation removes a {@link de.fraunhofer.aisec.cpg.graph.Node} from its AST context.
 * @param <S> the parent {@link de.fraunhofer.aisec.cpg.graph.Node} type
 * @param <T> the target {@link de.fraunhofer.aisec.cpg.graph.Node} type
 * @author robin
 * @version $Id: $Id
 */
public final class RemoveOperation<S extends Node, T extends Node> extends GraphOperationImpl<S, T> {

    private final boolean disconnectEog;

    /**
     * Creates a new {@link RemoveOperation}.
     * @param sourcePattern The source pattern of which a related node shall be removed
     * @param edge the edge
     * @param disconnectEog if true, the target node is disconnected in the EOG graph
     */
    public RemoveOperation(NodePattern<? extends S> sourcePattern, CpgEdge<S, T> edge, boolean disconnectEog) {
        super(sourcePattern, edge);
        this.disconnectEog = disconnectEog;
        if (Objects.isNull(sourcePattern) || Objects.isNull(edge)) {
            throw new RuntimeException("Invalid RemoveOperation: the pattern root needs to be wrapped into a WildcardParentPattern.");
        }
    }

    private static final Logger logger;

    static {
        logger = LoggerFactory.getLogger(RemoveOperation.class);

    }

    @Override
    public void resolveAndApply(Match match, TranslationContext ctx) {
        S parent = match.get(parentPattern);
        T element = edge.getter().apply(parent);
        apply(parent, element, edge, disconnectEog);
    }

    /**
     * Applies a {@link RemoveOperation} to the given nodes.
     * @param <S> the source node type
     * @param <T> the target node type
     * @param source the source node
     * @param child the target node
     * @param edge the edge
     * @param disconnectEog if true, the target node will be disconnected from the EOG graph
     */
    public static <S extends Node, T extends Node> void apply(S source, T child, CpgEdge<S, T> edge, boolean disconnectEog) {

        if (!(edge instanceof CpgNthEdge<S, T> nthEdge)) {
            logger.debug("Remove " + child.toString());
            edge.setter().accept(source, null);
        } else if (nthEdge.getMultiEdge().isEdgeValued()) {
            logger.debug("Remove %s (Element no. %d of %s)".formatted(child.toString(), nthEdge.getIndex(), source));
            // set edge indices of successors
            List<PropertyEdge<T>> siblingEdges = nthEdge.getMultiEdge().getAllEdges(source);
            int index = nthEdge.getIndex();

            // remove edge
            siblingEdges.remove(siblingEdges.get(index));

            for (int i = index; i <= siblingEdges.size() - 1; i++) {
                PropertyEdge<T> sibling = siblingEdges.get(i);
                sibling.addProperty(Properties.INDEX, i);
                siblingEdges.set(i, sibling);
            }

        } else {
            // nthEdge is node-valued
            List<T> siblings = nthEdge.getMultiEdge().getAllTargets(source);
            siblings.remove(child);
        }

        if (!disconnectEog) {
            return;
        }

        List<Node> predExits = TransformationUtil.disconnectFromPredecessor(child);
        Node succEntry = TransformationUtil.disconnectFromSuccessor(child);

        if (!Objects.isNull(succEntry) && succEntry != DummyNeighbor.getInstance()) {
            predExits.forEach(exit -> TransformationUtil.connectNewSuccessor(exit, succEntry, false));
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
    public GraphOperation instantiateAnyOfNEdge(Match match) {
        CpgMultiEdge<S, T>.AnyOfNEdge anyOfNEdge = (CpgMultiEdge<S, T>.AnyOfNEdge) edge;
        return new RemoveOperation<>(parentPattern, match.getEdge(this.parentPattern, anyOfNEdge), this.disconnectEog);
    }

}
