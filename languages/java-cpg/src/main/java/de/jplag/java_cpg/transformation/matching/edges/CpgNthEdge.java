package de.jplag.java_cpg.transformation.matching.edges;

import de.fraunhofer.aisec.cpg.graph.Node;
import de.fraunhofer.aisec.cpg.graph.edge.PropertyEdge;

/**
 * A {@link CpgNthEdge} represents an individual {@link PropertyEdge} out of a {@link CpgMultiEdge}.
 * @param <T> source node type
 * @param <R> target node type
 */
public class CpgNthEdge<T extends Node, R extends Node> extends CpgEdge<T, R> {
    private final CpgMultiEdge<T, R> multiEdge;
    private final int index;

    /**
     * Creates a new {@link CpgNthEdge}.
     * @param edge The {@link CpgMultiEdge} that represents multiple edges
     * @param index The index of this edge
     */
    public CpgNthEdge(CpgMultiEdge<T, R> edge, int index) {
        super(t -> edge.getAllTargets(t).get(index), (t, r) -> edge.setter().accept(t, index, r), edge.getCategory());
        this.multiEdge = edge;
        this.index = index;
        this.setSourceClass(edge.getSourceClass());
        this.setRelatedClass(edge.getRelatedClass());
    }

    @Override
    public boolean isEquivalentTo(IEdge<?, ?> other) {
        if (!(other instanceof CpgNthEdge<?, ?> otherNthEdge)) {
            return false;
        }
        return multiEdge.isEquivalentTo(otherNthEdge.multiEdge) && index == otherNthEdge.getIndex();
    }

    /**
     * Returns the multi edge object of which this edge is one.
     * @return the multi edge
     */
    public CpgMultiEdge<T, R> getMultiEdge() {
        return multiEdge;
    }

    /**
     * Returns the index n of this nth edge.
     * @return the index
     */
    public int getIndex() {
        return index;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        CpgNthEdge<?, ?> that = (CpgNthEdge<?, ?>) o;

        if (getIndex() != that.getIndex())
            return false;
        return getMultiEdge().equals(that.getMultiEdge());
    }

    @Override
    public int hashCode() {
        return 0;
    }
}
