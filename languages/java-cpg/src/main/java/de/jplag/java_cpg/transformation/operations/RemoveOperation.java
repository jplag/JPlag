package de.jplag.java_cpg.transformation.operations;

import de.fraunhofer.aisec.cpg.graph.Node;
import de.fraunhofer.aisec.cpg.graph.edge.Properties;
import de.fraunhofer.aisec.cpg.graph.edge.PropertyEdge;
import de.fraunhofer.aisec.cpg.helpers.SubgraphWalker;
import de.jplag.java_cpg.transformation.TransformationException;
import de.jplag.java_cpg.transformation.matching.edges.CpgEdge;
import de.jplag.java_cpg.transformation.matching.edges.CpgNthEdge;
import de.jplag.java_cpg.transformation.matching.pattern.GraphPattern;
import de.jplag.java_cpg.transformation.matching.pattern.NodePattern;
import de.jplag.java_cpg.transformation.matching.pattern.WildcardGraphPattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;

import static de.jplag.java_cpg.transformation.operations.TransformationHelper.getEntryEdges;
import static de.jplag.java_cpg.transformation.operations.TransformationHelper.getExitEdges;

/**
 *
 * @param parentPattern
 * @param edge
 * @param <S>
 * @param <T>
 */
public record RemoveOperation<S extends Node, T extends Node>(NodePattern<? extends S> parentPattern, CpgEdge<S, T> edge) implements GraphOperation {

    public RemoveOperation {
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

        if (!(edge instanceof CpgNthEdge<S,T> nthEdge)) {
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

        // replace EOG edges
        SubgraphWalker.Border eogOldBorders = TransformationHelper.getEogBorders(element);

        Node entry = eogOldBorders.getEntries().get(0);
        List<Node> exits = eogOldBorders.getExits();

        List<PropertyEdge<Node>> entryEdges = getEntryEdges(element, entry);
        List<PropertyEdge<Node>> exitEdges = getExitEdges(element, exits);
        List<Node> successors = exitEdges.stream().map(PropertyEdge::getEnd).toList();
        if (successors.size() > 1) {
            LOGGER.warn("This AST unit has more than one successor.");
        }
        Node successorEntry = successors.get(0);

        // EOG subgraphs induced by AST subtrees can have just one valid EOG entry, but many valid EOG exits
        // Replace the exiting edges (oldTarget -->> succ) by the entering edges (pred -->> oldTarget)
        exitEdges.forEach(e -> e.getEnd().removePrevEOGEntry(e.getStart()));
        entryEdges.forEach(e -> {
            e.setEnd(successorEntry);
            successorEntry.addPrevEOG(e);
        });
    }

    @Override
    public NodePattern<?> getTarget() {
        return parentPattern;
    }

    @Override
    public <S extends Node, T extends Node> GraphOperation instantiate(GraphPattern.Match.WildcardMatch<S, T> match) {
        if (this.parentPattern instanceof WildcardGraphPattern<?>.ParentNodePattern && this.edge instanceof WildcardGraphPattern<?>.Edge) {
            return new RemoveOperation<>(match.parentPattern(), match.edge());
        } else {
            return this;
        }
    }
}
