package de.jplag.java_cpg.transformation.matching.pattern.relation;

import de.fraunhofer.aisec.cpg.graph.Node;
import de.jplag.java_cpg.transformation.matching.edges.CpgMultiEdge;
import de.jplag.java_cpg.transformation.matching.pattern.NodePattern;

/**
 * Pair of a node patterns of a related node and a multi edge from a reference node to a list of candidate related
 * nodes.
 * @param <T> type of the target node
 * @param <R> type of the related node
 */
public final class RelatedOneToNNode<T extends Node, R extends Node> extends OneToNRelation<T, R> {

    /**
     * @param pattern the patterns describing the related node
     * @param edge edge from a reference node to the related nodes
     */
    public RelatedOneToNNode(NodePattern<? extends R> pattern, CpgMultiEdge<T, R> edge) {
        super(pattern, edge);
    }

    /**
     * Creates a relation for a child within a consecutive sequence.
     * @param pattern the pattern describing the related node
     * @param edge edge from a reference node to the related nodes
     * @param minIndex the minimum index of the child within the sequence
     */
    public RelatedOneToNNode(NodePattern<? extends R> pattern, CpgMultiEdge<T, R> edge, int minIndex) {
        super(pattern, edge, minIndex);
    }

    @Override
    public String toString() {
        return "RelatedOneToNNode[" + "pattern=" + pattern + ", " + "edge=" + edge + ']';
    }

}
