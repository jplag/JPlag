package de.jplag.java_cpg.transformation.operations;

import de.fraunhofer.aisec.cpg.graph.Node;
import de.fraunhofer.aisec.cpg.graph.edge.PropertyEdge;
import de.fraunhofer.aisec.cpg.graph.statements.expressions.Block;
import de.fraunhofer.aisec.cpg.helpers.SubgraphWalker;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public final class TransformationHelper {

    static final Logger LOGGER = LoggerFactory.getLogger(TransformationHelper.class);
    public static final DummyNeighbor DUMMY = DummyNeighbor.getInstance();

    private TransformationHelper() {
        /* should not be instantiated */
    }

    /**
     * Gets the {@link SubgraphWalker.Border} of the given node's sub-AST that links to outer nodes via EOG edges.
     *
     * @param astRoot the root of the sub-AST
     * @return the EOG {@link SubgraphWalker.Border} of the AST
     */
    static SubgraphWalker.Border getEogBorders(Node astRoot) {
        SubgraphWalker.Border result;
        if (astRoot instanceof Block block && !block.getStatements().isEmpty()) {
            result = new SubgraphWalker.Border();
            SubgraphWalker.Border firstStatementBorder = SubgraphWalker.INSTANCE.getEOGPathEdges(block.get(0));
            result.setEntries(firstStatementBorder.getEntries());
            SubgraphWalker.Border lastStatementBorder = SubgraphWalker.INSTANCE.getEOGPathEdges(block.get(block.getStatements().size() - 1));
            result.setExits(lastStatementBorder.getExits());
        } else if (astRoot.getNextEOG().isEmpty() && astRoot.getPrevEOG().isEmpty()) {
            // Isolated node: a case that the API method does not account for
            result = new SubgraphWalker.Border();
            result.setEntries(List.of(astRoot));
            result.setExits(List.of(astRoot));
        } else {
            result = SubgraphWalker.INSTANCE.getEOGPathEdges(astRoot);
        }

        checkBorder(astRoot, result);

        return result;
    }

    private static void checkBorder(Node astRoot, SubgraphWalker.Border result) {
        if (result.getEntries().isEmpty()) {
            LOGGER.warn("AST subtree of %s has no EOG entry".formatted(astRoot));
        } else if (result.getEntries().size() > 1) {
            LOGGER.warn("AST subtree of %s has multiple EOG entries".formatted(astRoot));
        }
    }

    /**
     * Checks if the given {@link Node} {@code maybeChild} is contained in the sub-AST with root {@code astRoot}.
     *
     * @param astRoot    the root of the sub-AST
     * @param maybeChild the node to check
     * @return true if {@code maybeChild} is contained in the sub-AST rooted at {@code astRoot}
     */
    static boolean isAstChild(Node astRoot, Node maybeChild) {
        return SubgraphWalker.INSTANCE.flattenAST(astRoot).contains(maybeChild);
    }

    /**
     * Unlinks the {@code otherPredecessor} and {@code predecessor} from their successors and links the predecessor to
     * the otherPredecessor's former successors.
     *
     * @param otherPredecessor the original predecessor
     * @param predecessor      the new predecessor
     */
    static void transferEogSuccessor(Node otherPredecessor, Node predecessor) {
        /*
            Current situation:
                 [predecessor--exits]      --exitEdges->>      oldSuccessor
            [otherPredecessor--otherExits] --otherExitEdges->> newSuccessor

            Target situation:
                 [predecessor--exits]      --exitEdges->> newSuccessor
            [otherPredecessor--otherExits]                oldSuccessor
         */

        List<Node> exits = getEogBorders(predecessor).getExits();
        // This is the case for all nodes involving ReturnStatements.
        if (exits.isEmpty()) {
            return;
        }
        List<PropertyEdge<Node>> exitEdges = getExitEdges(predecessor, exits);
        List<Node> oldSuccessors = exitEdges.stream().map(PropertyEdge::getEnd).distinct().toList();
        if (oldSuccessors.size() > 1) {
            LOGGER.warn("AST subtree of %s has multiple EOG successors".formatted(predecessor.toString()));
        }
        Node oldSuccessor = oldSuccessors.get(0);

        List<Node> otherExits = getEogBorders(otherPredecessor).getExits();
        List<PropertyEdge<Node>> otherExitEdges = getExitEdges(otherPredecessor, otherExits);
        List<Node> newSuccessors = otherExitEdges.stream().map(PropertyEdge::getEnd).distinct().toList();
        if (newSuccessors.size() > 1) {
            LOGGER.warn("AST subtree of %s has multiple EOG successors".formatted(predecessor.toString()));
        }
        Node newSuccessor = newSuccessors.get(0);

        if (oldSuccessor.equals(newSuccessor)) {
            return;
        }

        // Disconnect otherExit ->> newSuccessor (edges are removed)
        otherExitEdges.forEach(e -> {
            e.getEnd().getPrevEOGEdges().remove(e);
            DUMMY.saveOriginalTarget(e);
            e.setEnd(DUMMY);
            DUMMY.addPrevEOG(e);
        });

        exitEdges.forEach(e -> {
            // Disconnect exit ->> oldSuccessor (preserve outgoing edges)
            e.getEnd().getPrevEOGEdges().remove(e);

            PropertyEdge<Node> dummyEdge = new PropertyEdge<>(e);
            DUMMY.saveOriginalSource(dummyEdge);
            dummyEdge.setStart(DUMMY);
            DUMMY.addNextEOG(dummyEdge);
            e.getEnd().addPrevEOG(dummyEdge);

            // Connect exit ->> newSuccessor
            e.setEnd(newSuccessor);
            newSuccessor.addPrevEOG(e);
        });
    }


    /**
     * Unlinks the predecessor(s) of {@code oldSuccessor} and {@code newSuccessor} and attaches the {@code newSuccessor} to the former predecessors of {@code oldSuccessor}.
     * If the oldSuccessor has been detached by other {@link ReplaceOperation}s and replaced, the edges are not detached again.
     *
     * @param oldSuccessor the original successor
     * @param newSuccessor the replacement successor
     */
    public static void transferEogPredecessor(Node oldSuccessor, Node newSuccessor) {
        /*
            Current situation:
                 predecessor -------entryEdges->> [oldEntry--oldSuccessor]
            otherPredecessor --otherEntryEdges->> [newEntry--newSuccessor]

            Target situation:
                 predecessor --entryEdges->> [newEntry--newSuccessor]
            otherPredecessor                 [oldEntry--oldSuccessor]
         */
        Node newEntry = getEogBorders(newSuccessor).getEntries().get(0);
        Node oldEntry = getEogBorders(oldSuccessor).getEntries().get(0);
        if (oldEntry.equals(newEntry)) {
            return;
        }

        List<PropertyEdge<Node>> otherEntryEdges = getEntryEdges(newSuccessor, newEntry);

        // Disconnect otherPredecessor ->> newEntry (edges are removed)
        otherEntryEdges.forEach(e -> {
            DUMMY.saveOriginalTarget(e);
            e.getEnd().getPrevEOGEdges().remove(e);
            e.setEnd(DUMMY);
            DUMMY.addPrevEOG(e);
        });

        List<PropertyEdge<Node>> entryEdges = getEntryEdges(oldSuccessor, oldEntry);
        entryEdges.forEach(e -> {
                // Disconnect newPredecessor ->> oldEntry (preserve outgoing edges)
                e.getEnd().getPrevEOGEdges().remove(e);

                PropertyEdge<Node> dummyEdge = new PropertyEdge<>(e);
                DUMMY.saveOriginalSource(e);
                dummyEdge.setStart(DUMMY);
                DUMMY.addNextEOG(dummyEdge);
                oldEntry.addPrevEOG(dummyEdge);

                // Connect newPredecessor ->> newEntry
                e.setEnd(newEntry);
                newEntry.addPrevEOG(e);
            });

    }

    @NotNull
    static List<PropertyEdge<Node>> getEntryEdges(Node astParent, Node entry) {
        List<PropertyEdge<Node>> originalEdges = DUMMY.getOriginalEdgeOfTarget(entry);
        if (!originalEdges.isEmpty()) {
            // newEntry has been detached; reattach loose edges.
            DUMMY.clearOriginalEdgesOfTarget(entry);
            return originalEdges;
        }

        return entry.getPrevEOGEdges().stream()
            .filter(e -> !isAstChild(astParent, e.getStart())).toList();
    }

    @NotNull
    static List<PropertyEdge<Node>> getExitEdges(Node astParent, List<Node> exits) {
        List<PropertyEdge<Node>> originalEdges = exits.stream().map(DUMMY::getOriginalEdgeOfSource).flatMap(List::stream).toList();
        if (!originalEdges.isEmpty()) {
            // newEntry has been detached; reattach loose edges.
            exits.forEach(DUMMY::clearOriginalEdgesOfTarget);
            return originalEdges;
        }

        return exits.stream().flatMap(n -> n.getNextEOGEdges().stream())
            .filter(e -> !isAstChild(astParent, e.getEnd())).toList();
    }

}
