package de.jplag.java_cpg.transformation.matching.edges;

import de.fraunhofer.aisec.cpg.graph.Node;

/**
 * A {@link AnyOfNEdge} serves as a placeholder for a {@link CpgNthEdge} during transformation calculation as long as
 * the index is not known.
 */
public class AnyOfNEdge<T extends Node, R extends Node> extends CpgNthEdge<T, R> {

    private final CpgMultiEdge<T, R> cpgMultiEdge;
    private final int minIndex;

    /**
     * Creates a new {@link AnyOfNEdge} for the corresponding {@link CpgMultiEdge}.
     */
    public AnyOfNEdge(CpgMultiEdge<T, R> cpgMultiEdge, int minIndex) {
        super(cpgMultiEdge, -1);
        this.cpgMultiEdge = cpgMultiEdge;
        this.minIndex = minIndex;
    }

    public int getMinimalIndex() {
        return minIndex;
    }

    /**
     * Gets the corresponding {@link CpgMultiEdge}.
     * @return the multi edge
     */
    @Override
    public CpgMultiEdge<T, R> getMultiEdge() {
        return cpgMultiEdge;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        if (!super.equals(o))
            return false;

        AnyOfNEdge<?, ?> that = (AnyOfNEdge<?, ?>) o;

        return cpgMultiEdge.equals(that.cpgMultiEdge);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + cpgMultiEdge.hashCode();
        return result;
    }
}
