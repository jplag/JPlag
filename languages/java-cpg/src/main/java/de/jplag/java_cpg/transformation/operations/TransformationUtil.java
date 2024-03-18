package de.jplag.java_cpg.transformation.operations;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import de.jplag.java_cpg.transformation.GraphTransformation;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fraunhofer.aisec.cpg.graph.Node;
import de.fraunhofer.aisec.cpg.graph.edge.PropertyEdge;
import de.fraunhofer.aisec.cpg.graph.statements.expressions.Block;
import de.fraunhofer.aisec.cpg.graph.statements.expressions.UnaryOperator;
import de.fraunhofer.aisec.cpg.helpers.SubgraphWalker;

/**
 * This class is a collection of auxiliary methods related to {@link GraphTransformation}s.
 */
public final class TransformationUtil {

    static final Logger logger = LoggerFactory.getLogger(TransformationUtil.class);
    public static final DummyNeighbor DUMMY = DummyNeighbor.getInstance();

    private TransformationUtil() {
        /* should not be instantiated */
    }

    /**
     * Gets the {@link SubgraphWalker.Border} of the given node's sub-AST that links to outer nodes via EOG edges.
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
                while (!entry.getPrevEOG().isEmpty())
                    entry = entry.getPrevEOG().getFirst();
                result.setEntries(List.of(entry));
            }
            if (result.getExits().isEmpty()) {
                Node exit = astRoot;
                while (!exit.getNextEOG().isEmpty())
                    exit = exit.getNextEOG().getFirst();
                result.setExits(List.of(exit));
            }

        }

        checkBorder(astRoot, result);

        return result;
    }

    private static void checkBorder(Node astRoot, SubgraphWalker.Border result) {
        if (result.getEntries().isEmpty()) {
            logger.debug("AST subtree of %s has no EOG entry".formatted(astRoot));
        } else if (result.getEntries().size() > 1) {
            logger.debug("AST subtree of %s has multiple EOG entries".formatted(astRoot));
        }
    }

    /**
     * Checks if the given {@link Node} {@code maybeChild} is contained in the sub-AST with root {@code astRoot}.
     * @param astRoot the root of the sub-AST
     * @param maybeChild the node to check
     * @return true if {@code maybeChild} is contained in the sub-AST rooted at {@code astRoot}
     */
    static boolean isAstChild(Node astRoot, Node maybeChild) {
        return SubgraphWalker.INSTANCE.flattenAST(astRoot).contains(maybeChild);
    }

    static void transferEogSuccessor(Node otherPredecessor, Node predecessor) {
        disconnectFromSuccessor(predecessor);
        Node otherEntry = disconnectFromSuccessor(otherPredecessor);
        Node exit = getExit(predecessor);
        connectNewSuccessor(exit, otherEntry, true);
    }

    static void transferEogPredecessor(Node oldSuccessor, Node newSuccessor) {
        List<Node> exits = disconnectFromPredecessor(oldSuccessor);
        disconnectFromPredecessor(newSuccessor);
        exits.forEach(exit -> connectNewSuccessor(exit, newSuccessor, true));
    }

    public static Node disconnectFromSuccessor(Node astRoot) {
        Node exit = getExit(astRoot);
        List<PropertyEdge<Node>> exitEdges = new ArrayList<>(getExitEdges(astRoot, List.of(exit), true));

        exitEdges.removeIf(e -> Objects.equals(e.getEnd(), DUMMY));
        exitEdges.removeIf(e -> Objects.equals(e.getStart(), DUMMY));

        if (exitEdges.isEmpty())
            return null;

        Node entry = exitEdges.getFirst().getEnd();
        exitEdges.forEach(e -> {
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

        if (Objects.isNull(newSuccessor) || target == DUMMY) {
            return null;
        }
        if (target instanceof UnaryOperator unaryOperator && Objects.equals(unaryOperator.getOperatorCode(), "throw")) {
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
        if (entryEdges.isEmpty())
            return List.of();

        List<Node> predExits = entryEdges.stream().map(PropertyEdge::getStart).toList();

        entryEdges.stream().filter(e -> !Objects.equals(e.getStart(), DUMMY)).filter(e -> !Objects.equals(e.getEnd(), DUMMY)).forEach(e -> {
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

    public static Node getEntry(Node astRoot) {
        return getEogBorders(astRoot).getEntries().getFirst();
    }

    static Node getExit(Node astRoot) {
        return getEogBorders(astRoot).getExits().getFirst();
    }

    static Node connectNewPredecessor(Node target, Node newPredecessor, boolean asAstRoot) {
        Node entry = getEntry(target);
        List<Node> exits = getEogBorders(newPredecessor).getExits();
        List<PropertyEdge<Node>> exitEdges = getExitEdges(target, exits, true);

        assert exitEdges.stream().allMatch(e -> e.getEnd().equals(DUMMY));
        if (exitEdges.isEmpty())
            return target;

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
        List<PropertyEdge<Node>> currentEntryEdges = entry.getPrevEOGEdges().stream().filter(e -> !isAstChild(astParent, e.getStart())).toList();
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
                .filter(e -> !isAstChild(astParent, e.getEnd()) || (astParent instanceof Block && e.getEnd() == astParent)).toList();
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
        disconnectFromSuccessor(target);

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
