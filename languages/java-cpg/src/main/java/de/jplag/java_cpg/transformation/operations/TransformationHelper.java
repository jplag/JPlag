package de.jplag.java_cpg.transformation.operations;

import de.fraunhofer.aisec.cpg.graph.Node;
import de.fraunhofer.aisec.cpg.graph.edge.PropertyEdge;
import de.fraunhofer.aisec.cpg.graph.statements.ReturnStatement;
import de.fraunhofer.aisec.cpg.graph.statements.expressions.Block;
import de.fraunhofer.aisec.cpg.graph.statements.expressions.UnaryOperator;
import de.fraunhofer.aisec.cpg.helpers.SubgraphWalker;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;

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
    public static SubgraphWalker.Border getEogBorders(Node astRoot) {
        SubgraphWalker.Border result;
        if (astRoot instanceof Block block && !block.getStatements().isEmpty() && block.getNextEOG().isEmpty() && block.getPrevEOG().isEmpty()) {
            result = new SubgraphWalker.Border();
            SubgraphWalker.Border firstStatementBorder = getEogBorders(block.get(0));
            result.setEntries(firstStatementBorder.getEntries());
            SubgraphWalker.Border lastStatementBorder = getEogBorders(block.get(block.getStatements().size() - 1));
            result.setExits(lastStatementBorder.getExits());
        } else {
            result = SubgraphWalker.INSTANCE.getEOGPathEdges(astRoot);
            if (result.getEntries().isEmpty()) {
                Node entry = astRoot;
                while (!entry.getPrevEOG().isEmpty()) entry = entry.getPrevEOG().get(0);
                result.setEntries(List.of(entry));
            } if (result.getExits().isEmpty()) {
                Node exit = astRoot;
                while (!exit.getNextEOG().isEmpty()) exit = exit.getNextEOG().get(0);
                result.setExits(List.of(exit));
            }

        }

        checkBorder(astRoot, result);

        return result;
    }

    private static void checkBorder(Node astRoot, SubgraphWalker.Border result) {
        if (result.getEntries().isEmpty()) {
            LOGGER.debug("AST subtree of %s has no EOG entry".formatted(astRoot));
        } else if (result.getEntries().size() > 1) {
            LOGGER.debug("AST subtree of %s has multiple EOG entries".formatted(astRoot));
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
     * @param useDummies
     */
    static void transferEogSuccessor(Node otherPredecessor, Node predecessor, boolean useDummies) {
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
        List<PropertyEdge<Node>> exitEdges = getExitEdges(predecessor, exits, false);
        List<Node> oldSuccessors = exitEdges.stream().map(PropertyEdge::getEnd).distinct().toList();
        if (oldSuccessors.size() > 1) {
            LOGGER.warn("AST subtree of %s has multiple EOG successors".formatted(predecessor.toString()));
        }
        Node oldSuccessor = oldSuccessors.get(0);

        List<Node> otherExits = getEogBorders(otherPredecessor).getExits();
        List<PropertyEdge<Node>> otherExitEdges = getExitEdges(otherPredecessor, otherExits, useDummies);
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

    static void transferEogSuccessor2(Node otherPredecessor, Node predecessor) {
        disconnectFromSuccessor(predecessor);
        Node otherEntry = disconnectFromSuccessor(otherPredecessor);
        Node exit = getExit(predecessor);
        connectNewSuccessor(exit, otherEntry, true);
    }

    static void transferEogPredecessor2(Node oldSuccessor, Node newSuccessor) {
        List<Node> exits = disconnectFromPredecessor(oldSuccessor);
        disconnectFromPredecessor(newSuccessor);
        exits.forEach(exit -> connectNewSuccessor(exit, newSuccessor,  true));
    }

    /**
     * Unlinks the predecessor(s) of {@code oldSuccessor} and {@code newSuccessor} and attaches the {@code newSuccessor} to the former predecessors of {@code oldSuccessor}.
     * If the oldSuccessor has been detached by other {@link ReplaceOperation}s and replaced, the edges are not detached again.
     *
     * @param oldSuccessor the original successor
     * @param newSuccessor the replacement successor
     */
    public static void transferEogPredecessor(Node oldSuccessor, Node newSuccessor, boolean useDummies) {
        /*
            Current situation:
                 predecessor -------entryEdges->> [oldEntry--oldSuccessor]
            otherPredecessor --otherEntryEdges->> [newEntry--newSuccessor]

            Target situation:
                 predecessor --entryEdges->> [newEntry--newSuccessor]
            otherPredecessor                 [oldEntry--oldSuccessor]
         */
        Node newEntry = getEntry(newSuccessor);
        Node oldEntry = getEntry(oldSuccessor);
        if (oldEntry.equals(newEntry)) {
            return;
        }

        List<PropertyEdge<Node>> otherEntryEdges = getEntryEdges(newSuccessor, newEntry, useDummies);

        // Disconnect otherPredecessor ->> newEntry (edges are removed)
        otherEntryEdges.forEach(e -> {
            DUMMY.saveOriginalTarget(e);
            e.getEnd().getPrevEOGEdges().remove(e);
            e.setEnd(DUMMY);
            DUMMY.addPrevEOG(e);
        });

        List<PropertyEdge<Node>> entryEdges = getEntryEdges(oldSuccessor, oldEntry, useDummies);
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

    public static Node disconnectFromSuccessor(Node astRoot) {
        Node exit = getExit(astRoot);
        List<PropertyEdge<Node>> exitEdges = getExitEdges(astRoot, List.of(exit), true);
        if (exitEdges.isEmpty()) return null;

        Node entry = exitEdges.get(0).getEnd();

        exitEdges.stream()
            .filter(e -> !Objects.equals(e.getEnd(), DUMMY))
            .filter(e -> !Objects.equals(e.getStart(), DUMMY))
            .forEach(e -> {
                PropertyEdge<Node> dummyEdge = new PropertyEdge<>(e);
                DUMMY.saveOriginalTarget(e);

                int index = entry.getPrevEOGEdges().indexOf(e);
                e.setEnd(DUMMY);
                DUMMY.addPrevEOG(e);

                DUMMY.saveOriginalSource(dummyEdge);
                dummyEdge.setStart(DUMMY);
                DUMMY.addNextEOG(dummyEdge);
                entry.getPrevEOGEdges().set(index, dummyEdge);
            });
        return entry;
    }

    static Node connectNewSuccessor(Node target, Node newSuccessor, boolean enforceEogConnection) {
        List<Node> exits = List.of(target);
        List<PropertyEdge<Node>> exitEdges = getExitEdges(target, exits, false);

        if (target == DUMMY) {
            return null;
        }
        if (target instanceof UnaryOperator unaryOperator &&
            Objects.equals(unaryOperator.getOperatorCode(), "throw")) {
                target.clearNextEOG();
                return null;
        }


        if (exitEdges.isEmpty()) {
            if (enforceEogConnection) {
                PropertyEdge<Node> exitEdge = new PropertyEdge<>(target, DUMMY);
                target.addNextEOG(exitEdge);
                exitEdges = List.of(exitEdge);
            } else {
                return target;
            }
        }
        exitEdges = exitEdges.stream().filter(e -> e.getEnd().equals(DUMMY)).toList();
        assert !exitEdges.isEmpty();
        Node entry = getEntry(newSuccessor);

        getEntryEdges(newSuccessor, entry, false).forEach(e -> {
            e.getStart().getNextEOGEdges().remove(e);
            entry.getPrevEOGEdges().remove(e);
        });

        exitEdges.forEach(e -> {
            DUMMY.getPrevEOGEdges().remove(e);
            e.setEnd(entry);
            entry.addPrevEOG(e);
        });

        return entry;
    }

    public static List<Node> disconnectFromPredecessor(Node astRoot) {
        Node entry = getEntry(astRoot);

        List<PropertyEdge<Node>> entryEdges = getEntryEdges(astRoot, entry, true);
        if (entryEdges.isEmpty()) return List.of();

        List<Node> predExits = entryEdges.stream().map(PropertyEdge::getStart).toList();

        entryEdges.stream()
            .filter(e -> !Objects.equals(e.getStart(), DUMMY))
            .filter(e -> !Objects.equals(e.getEnd(), DUMMY))
            .forEach(e -> {
                PropertyEdge<Node> dummyEdge = new PropertyEdge<>(e);
                DUMMY.saveOriginalSource(e);

                e.getStart().getNextEOGEdges().remove(e);
                e.setStart(DUMMY);
                DUMMY.addNextEOG(e);

                DUMMY.saveOriginalTarget(dummyEdge);
                dummyEdge.setEnd(DUMMY);
                DUMMY.addPrevEOG(dummyEdge);
                dummyEdge.getStart().addNextEOG(dummyEdge);
            });
        return predExits;
    }

    private static Node getEntry(Node astRoot) {
        return getEogBorders(astRoot).getEntries().get(0);
    }

    private static Node getExit(Node astRoot) {
        return getEogBorders(astRoot).getExits().get(0);
    }

    static Node connectNewPredecessor(Node target, Node newPredecessor, boolean asAstRoot) {
        Node entry = getEntry(target);
        List<Node> exits = getEogBorders(newPredecessor).getExits();
        List<PropertyEdge<Node>> exitEdges = getExitEdges(target, exits, true);

        assert exitEdges.stream().allMatch(e -> e.getEnd().equals(DUMMY));
        if (exitEdges.isEmpty()) return target;

        getEntryEdges(target, entry, true).forEach(e -> {
            e.getStart().getNextEOGEdges().remove(e);
            entry.getPrevEOGEdges().remove(e);
        });

        exitEdges.forEach(e -> {
            DUMMY.getPrevEOGEdges().remove(e);
            e.setEnd(entry);
            entry.addPrevEOG(e);
        });

        return entry;
    }

    @NotNull
    public static List<PropertyEdge<Node>> getEntryEdges(Node astParent, Node entry, boolean useDummies) {
        List<PropertyEdge<Node>> currentEntryEdges = entry.getPrevEOGEdges().stream()
            .filter(e -> !isAstChild(astParent, e.getStart())).toList();
        List<PropertyEdge<Node>> disconnectedEdges = currentEntryEdges.stream().filter(e -> e.getStart().equals(DUMMY)).toList();

        List<PropertyEdge<Node>> originalEntryEdges = DUMMY.getOriginalEdgeOfTarget(entry);

        if (originalEntryEdges.isEmpty() || !useDummies) {
            // Node is still in its proper place
            return currentEntryEdges;
        } else if (!disconnectedEdges.isEmpty()) {
            // Node is disconnected
            return disconnectedEdges;
        } else {
            // Node has been reattached; return original exit edges (and clear them)
            DUMMY.clearOriginalEdgesOfTarget(entry);
            return originalEntryEdges;
        }
    }

    @NotNull
    public static List<PropertyEdge<Node>> getExitEdges(Node astParent, List<Node> exits, boolean useDummies) {
        List<PropertyEdge<Node>> currentExitEdges = exits.stream().flatMap(n -> n.getNextEOGEdges().stream())
            .filter(e -> !isAstChild(astParent, e.getEnd())).toList();
        List<PropertyEdge<Node>> disconnectedEdges = currentExitEdges.stream().filter(e -> e.getEnd().equals(DUMMY)).toList();

        List<PropertyEdge<Node>> originalEdges = exits.stream().map(DUMMY::getOriginalEdgeOfSource).flatMap(List::stream).toList();
        if (originalEdges.isEmpty() || !useDummies) {
            // Node is still in its proper place
            return currentExitEdges;
        } else if (!disconnectedEdges.isEmpty()) {
            // Node is disconnected
            return disconnectedEdges;
        } else {
            // Node has been reattached; return original exit edges (and clear them)
            exits.forEach(DUMMY::clearOriginalEdgesOfTarget);
            return originalEdges;
        }

    }

    public static void insertBefore(Node target, Node newSuccessor) {
        Node entry = getEntry(target);
        List<Node> exits = getEogBorders(target).getExits();
        disconnectFromPredecessor(target);
        Node oldSucc = disconnectFromSuccessor(target);

        Node succEntry = getEntry(newSuccessor);
        List<Node> newPreds = disconnectFromPredecessor(newSuccessor);
        newPreds.forEach(pred -> connectNewSuccessor(pred, entry, false));
        exits.forEach(exit -> connectNewSuccessor(exit, succEntry, false));
    }

    public static void insertAfter(Node target, Node newPredecessor) {
        Node entry = getEntry(target);
        Node exit = getExit(target);
        disconnectFromPredecessor(target);
        disconnectFromSuccessor(target);

        Node predExit = getExit(newPredecessor);
        Node newSuccEntry = disconnectFromSuccessor(newPredecessor);
        connectNewSuccessor(exit, newSuccEntry, false);
        connectNewSuccessor(predExit, entry, false);
    }

    public static boolean isAstSuccessor(Node element, Node maybeSuccessor) {

        List<Node> exits = getEogBorders(element).getExits();
        Node entry = getEntry(maybeSuccessor);
        List<PropertyEdge<Node>> entryEdges = getEntryEdges(maybeSuccessor, entry, false);

        return entryEdges.stream().anyMatch(e -> exits.contains(e.getStart()));
    }

    public static boolean isEogSuccessor(Node exit, Node maybeSuccessor) {
        Node entry = getEntry(maybeSuccessor);
        List<PropertyEdge<Node>> entryEdges = getEntryEdges(maybeSuccessor, entry, false);

        return entryEdges.stream().anyMatch(e -> exit == e.getStart());
    }
}
