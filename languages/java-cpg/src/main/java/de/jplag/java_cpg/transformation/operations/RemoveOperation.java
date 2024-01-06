package de.jplag.java_cpg.transformation.operations;

import de.fraunhofer.aisec.cpg.graph.Node;
import de.fraunhofer.aisec.cpg.graph.edge.Properties;
import de.fraunhofer.aisec.cpg.graph.edge.PropertyEdge;
import de.jplag.java_cpg.transformation.TransformationException;
import de.jplag.java_cpg.transformation.matching.edges.CpgEdge;
import de.jplag.java_cpg.transformation.matching.edges.CpgMultiEdge;
import de.jplag.java_cpg.transformation.matching.edges.CpgNthEdge;
import de.jplag.java_cpg.transformation.matching.pattern.GraphPattern;
import de.jplag.java_cpg.transformation.matching.pattern.NodePattern;
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

    public RemoveOperation(NodePattern<? extends S> parentPattern, CpgEdge<S, T> edge) {
        super(parentPattern, edge);
        if (Objects.isNull(parentPattern) || Objects.isNull(edge)) {
            throw new RuntimeException("Invalid RemoveOperation: the pattern root needs to be wrapped into a WildcardParentPattern.");
        }
    }

    private static final Logger LOGGER;

    static {
        LOGGER = LoggerFactory.getLogger(RemoveOperation.class);
    }

    @Override
    public void apply(GraphPattern.Match<?> match) throws TransformationException {
        S parent = match.get(parentPattern);
        T element = edge.getter().apply(parent);
        LOGGER.info("Remove " + element.toString());

        if (!(edge instanceof CpgNthEdge<S, T> nthEdge)) {
            edge.setter().accept(parent, null);
        } else if (nthEdge.getMultiEdge().isEdgeValued()) {
            // set edge indices of successors
            List<PropertyEdge<T>> siblingEdges = nthEdge.getMultiEdge().getAllEdges(parent);
            int index = nthEdge.getIndex();
            for (int i = index + 1; i < siblingEdges.size(); i++) {
                siblingEdges.get(i).addProperty(Properties.INDEX, i - 1);
            }
            // remove edge
            siblingEdges.remove(siblingEdges.get(index));
        } else {
            //nthEdge is node-valued
            List<T> siblings = nthEdge.getMultiEdge().getAllTargets(parent);
            siblings.remove(element);
        }

    }

    @Override
    public NodePattern<?> getTarget() {
        return parentPattern;
    }

    @Override
    public <S extends Node, T extends Node> GraphOperation instantiateWildcard(GraphPattern.Match.WildcardMatch<S, T> match) {
        return new RemoveOperation<>(match.parentPattern(), match.edge());
    }

    @Override
    public GraphOperation instantiateAny1ofNEdge(GraphPattern.Match<?> match) {
        CpgMultiEdge<S, T>.Any1ofNEdge any1OfNEdge = (CpgMultiEdge<S, T>.Any1ofNEdge) edge;
        return new RemoveOperation<>(parentPattern, match.getEdge(any1OfNEdge));
    }

}
