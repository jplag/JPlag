package de.jplag.java_cpg.transformation.matching.edges;

import de.fraunhofer.aisec.cpg.graph.Node;

/**
 *  A {@link CpgNthEdge} represents an individual {@link de.fraunhofer.aisec.cpg.graph.edge.PropertyEdge} out of a {@link CpgMultiEdge}.
 */
public class CpgNthEdge<S extends Node, T extends Node> extends CpgEdge<S, T>{
    private final CpgMultiEdge<S, T> multiEdge;
    private final int index;

    /**
     * Creates a new {@link CpgNthEdge}.
     * @param edge The {@link CpgMultiEdge} that represents multiple edges
     * @param index The index of this edge
     */
    public CpgNthEdge(CpgMultiEdge<S, T> edge, int index) {
        super(t -> edge.getAllTargets(t).get(index), (t, r) -> edge.setter().accept(t, index, r));
        this.multiEdge = edge;
        this.index = index;
        this.setFromClass(edge.getFromClass());
        this.setToClass(edge.getToClass());
    }

    /**
     * Returns the multi edge object of which this edge is one.
     * @return the multi edge
     */
    public CpgMultiEdge<S, T> getMultiEdge() {
        return multiEdge;
    }

    /**
     * Returns the index n of this nth edge.
     * @return the index
     */
    public int getIndex() {
        return index;
    }
}
