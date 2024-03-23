package de.jplag.java_cpg.transformation.operations;

import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fraunhofer.aisec.cpg.TranslationContext;
import de.fraunhofer.aisec.cpg.graph.Node;
import de.fraunhofer.aisec.cpg.graph.edge.Properties;
import de.fraunhofer.aisec.cpg.graph.edge.PropertyEdge;
import de.jplag.java_cpg.transformation.TransformationException;
import de.jplag.java_cpg.transformation.matching.edges.AnyOfNEdge;
import de.jplag.java_cpg.transformation.matching.edges.CpgEdge;
import de.jplag.java_cpg.transformation.matching.edges.CpgNthEdge;
import de.jplag.java_cpg.transformation.matching.pattern.Match;
import de.jplag.java_cpg.transformation.matching.pattern.NodePattern;
import de.jplag.java_cpg.transformation.matching.pattern.WildcardGraphPattern;

/**
 * This operation removes a {@link de.fraunhofer.aisec.cpg.graph.Node} from its AST context.
 * @param <T> the parent {@link de.fraunhofer.aisec.cpg.graph.Node} type
 * @param <R> the target {@link de.fraunhofer.aisec.cpg.graph.Node} type
 * @author robin
 * @version $Id: $Id
 */
public final class RemoveOperation<T extends Node, R extends Node> extends GraphOperationImpl<T, R> {

    private final boolean disconnectEog;

    /**
     * Creates a new {@link RemoveOperation}.
     * @param sourcePattern The source pattern of which a related node shall be removed
     * @param edge the edge
     * @param disconnectEog if true, the target node is disconnected in the EOG graph
     */
    public RemoveOperation(NodePattern<? extends T> sourcePattern, CpgEdge<T, R> edge, boolean disconnectEog) {
        super(sourcePattern, edge);
        this.disconnectEog = disconnectEog;
        if (Objects.isNull(sourcePattern) || Objects.isNull(edge)) {
            throw new TransformationException("Invalid RemoveOperation: the pattern root needs to be wrapped into a WildcardParentPattern.");
        }
    }

    private static final Logger logger;

    static {
        logger = LoggerFactory.getLogger(RemoveOperation.class);

    }

    @Override
    public void resolveAndApply(Match match, TranslationContext ctx) {
        T parent = match.get(parentPattern);
        R element = edge.getter().apply(parent);
        apply(parent, element, edge, disconnectEog);
    }

    /**
     * Applies a {@link RemoveOperation} to the given nodes.
     * @param <T> the target node type
     * @param <R> the target node type
     * @param target the target node
     * @param child the related node
     * @param edge the edge
     * @param disconnectEog if true, the target node will be disconnected from the EOG graph
     */
    public static <T extends Node, R extends Node> void apply(T target, R child, CpgEdge<T, R> edge, boolean disconnectEog) {

        if (!(edge instanceof CpgNthEdge<T, R> nthEdge)) {
            logger.debug("Remove {}", child);
            edge.setter().accept(target, null);
        } else if (nthEdge.getMultiEdge().isEdgeValued()) {
            logger.debug("Remove {}} (Element no. {} of {})", child, nthEdge.getIndex(), target);
            // set edge indices of successors
            List<PropertyEdge<R>> siblingEdges = nthEdge.getMultiEdge().getAllEdges(target);
            int index = nthEdge.getIndex();

            // remove edge
            siblingEdges.remove(siblingEdges.get(index));

            for (int i = index; i <= siblingEdges.size() - 1; i++) {
                PropertyEdge<R> sibling = siblingEdges.get(i);
                sibling.addProperty(Properties.INDEX, i);
                siblingEdges.set(i, sibling);
            }

        } else {
            // nthEdge is node-valued
            List<R> siblings = nthEdge.getMultiEdge().getAllTargets(target);
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
    public GraphOperation instantiateWildcard(Match match) {
        WildcardGraphPattern.ParentNodePattern<R> wcParent = (WildcardGraphPattern.ParentNodePattern<R>) this.parentPattern;
        return match.instantiateGraphOperation(wcParent, this);

    }

    public <T2 extends Node> RemoveOperation<T2, R> fromWildcardMatch(NodePattern<? extends T2> pattern, CpgEdge<T2, R> edge) {
        return new RemoveOperation<>(pattern, edge, this.disconnectEog);
    }

    @Override
    public GraphOperation instantiateAnyOfNEdge(Match match) {
        AnyOfNEdge<T, R> anyOfNEdge = (AnyOfNEdge<T, R>) edge;
        return new RemoveOperation<>(parentPattern, match.getEdge(this.parentPattern, anyOfNEdge), this.disconnectEog);
    }

}
