package de.jplag.java_cpg.transformation.operations;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;

import de.fraunhofer.aisec.cpg.graph.Node;
import de.fraunhofer.aisec.cpg.graph.edge.PropertyEdge;

/**
 * Mutable graph-editing state owned by one translation, whose passes execute sequentially.
 */
public final class GraphMutationContext implements AutoCloseable {
    private final DummyNeighbor dummyNeighbor;
    private final Set<PropertyEdge<Node>> temporaryEdges;
    private volatile boolean closed;

    /**
     * Creates independent intermediary state for one translation.
     */
    public GraphMutationContext() {
        dummyNeighbor = new DummyNeighbor();
        temporaryEdges = Collections.newSetFromMap(new IdentityHashMap<>());
        closed = false;
    }

    /**
     * Returns this translation's intermediary node.
     * @return the intermediary node
     */
    public DummyNeighbor getDummyNeighbor() {
        return dummyNeighbor;
    }

    /**
     * Registers an edge entering the dummy, retaining it for final reciprocal cleanup.
     * @param edge incoming edge
     */
    void registerIncomingEdge(PropertyEdge<Node> edge) {
        temporaryEdges.add(edge);
        dummyNeighbor.addPrevEOG(edge);
    }

    /**
     * Registers an edge leaving the dummy, retaining it for final reciprocal cleanup.
     * @param edge outgoing edge
     */
    void registerOutgoingEdge(PropertyEdge<Node> edge) {
        temporaryEdges.add(edge);
        dummyNeighbor.addNextEOG(edge);
    }

    /**
     * Clears saved registrations between editing operations without disconnecting pending edges.
     */
    public void resetBookkeeping() {
        dummyNeighbor.clear();
    }

    /**
     * Releases saved nodes and remaining intermediary edges after analysis has completed.
     */
    @Override
    public void close() {
        if (closed) {
            return;
        }
        // Pass cleanup may already have removed an edge from the dummy's own lists.
        temporaryEdges.addAll(dummyNeighbor.getNextEOGEdges());
        temporaryEdges.addAll(dummyNeighbor.getPrevEOGEdges());
        temporaryEdges.forEach(edge -> {
            if (edge.getStart() == dummyNeighbor) {
                edge.getEnd().getPrevEOGEdges().removeIf(candidate -> candidate == edge);
            }
            if (edge.getEnd() == dummyNeighbor) {
                edge.getStart().getNextEOGEdges().removeIf(candidate -> candidate == edge);
            }
        });
        dummyNeighbor.getNextEOGEdges().clear();
        dummyNeighbor.getPrevEOGEdges().clear();
        temporaryEdges.clear();
        resetBookkeeping();
        closed = true;
    }
}
