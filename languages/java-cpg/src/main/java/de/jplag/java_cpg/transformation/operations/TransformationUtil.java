package de.jplag.java_cpg.transformation.operations;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.SequencedCollection;
import java.util.function.Function;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fraunhofer.aisec.cpg.graph.Node;
import de.fraunhofer.aisec.cpg.graph.edge.PropertyEdge;
import de.fraunhofer.aisec.cpg.graph.statements.expressions.Block;
import de.fraunhofer.aisec.cpg.graph.statements.expressions.UnaryOperator;
import de.fraunhofer.aisec.cpg.helpers.SubgraphWalker;
import de.jplag.java_cpg.transformation.GraphTransformation;

/**
 * This class is a collection of auxiliary methods related to {@link GraphTransformation}s.
 */
public final class TransformationUtil {

    private static final Logger logger = LoggerFactory.getLogger(TransformationUtil.class);

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
                List<Node> entries = worklist(List.of(astRoot), Node::getPrevEOG);
                result.setEntries(entries);
            }
            if (result.getExits().isEmpty()) {
                List<Node> exits = worklist(List.of(astRoot), Node::getNextEOG);
                result.setExits(exits);
            }

        }

        checkBorder(astRoot, result);

        return result;
    }

    private static void checkBorder(Node astRoot, SubgraphWalker.Border result) {
        if (result.getEntries().isEmpty()) {
            logger.debug("AST subtree of {} has no EOG entry", astRoot);
        } else if (result.getEntries().size() > 1) {
            logger.debug("AST subtree of {} has multiple EOG entries", astRoot);
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

    static void transferEogSuccessor(Node otherPredecessor, Node predecessor, GraphMutationContext mutationContext) {
        disconnectFromSuccessor(predecessor, mutationContext);
        Node otherEntry = disconnectFromSuccessor(otherPredecessor, mutationContext);
        Node exit = getExit(predecessor);
        connectNewSuccessor(exit, otherEntry, true, mutationContext);
    }

    static void transferEogPredecessor(Node oldSuccessor, Node newSuccessor, GraphMutationContext mutationContext) {
        List<Node> exits = disconnectFromPredecessor(oldSuccessor, mutationContext);
        disconnectFromPredecessor(newSuccessor, mutationContext);
        exits.forEach(exit -> connectNewSuccessor(exit, newSuccessor, true, mutationContext));
    }

    /**
     * Disconnects the given {@link Node} from its EOG successor.
     * @param node the node
     * @param mutationContext graph mutation state owned by this translation
     * @return the EOG successor
     */
    public static Node disconnectFromSuccessor(Node node, GraphMutationContext mutationContext) {
        DummyNeighbor dummy = mutationContext.getDummyNeighbor();
        Node exit = getExit(node);
        SequencedCollection<PropertyEdge<Node>> exitEdges = new ArrayList<>(getExitEdges(node, List.of(exit), true, mutationContext));

        exitEdges.removeIf(e -> Objects.equals(e.getEnd(), dummy));
        exitEdges.removeIf(e -> Objects.equals(e.getStart(), dummy));

        if (exitEdges.isEmpty())
            return null;

        Node entry = exitEdges.getFirst().getEnd();
        exitEdges.forEach(e -> {
            PropertyEdge<Node> dummyEdge = new PropertyEdge<>(e);
            dummy.saveOriginalTarget(e);

            int index = entry.getPrevEOGEdges().indexOf(e);
            e.setEnd(dummy);
            mutationContext.registerIncomingEdge(e);

            dummy.saveOriginalSource(dummyEdge);
            dummyEdge.setStart(dummy);
            mutationContext.registerOutgoingEdge(dummyEdge);
            entry.getPrevEOGEdges().set(index, dummyEdge);
        });
        return entry;
    }

    /**
     * Connects the EOG exit edges of the given target {@link Node} to the entries of the newSuccessor {@link Node}.
     * @param target the node to be connected
     * @param newSuccessor the node to be connected to
     * @param enforceEogConnection if true, the two nodes will be connected even if target has no exit edges.
     * @param mutationContext graph mutation state owned by this translation
     */
    static void connectNewSuccessor(Node target, Node newSuccessor, boolean enforceEogConnection, GraphMutationContext mutationContext) {
        DummyNeighbor dummy = mutationContext.getDummyNeighbor();
        List<Node> exits = List.of(target);
        List<PropertyEdge<Node>> exitEdges = getExitEdges(target, exits, false, mutationContext);

        if (Objects.isNull(newSuccessor) || target == dummy) {
            return;
        }
        if (target instanceof UnaryOperator unaryOperator && Objects.equals(unaryOperator.getOperatorCode(), "throw")) {
            target.clearNextEOG();
            return;
        }

        if (exitEdges.isEmpty()) {
            if (enforceEogConnection) {
                PropertyEdge<Node> exitEdge = new PropertyEdge<>(target, dummy);
                target.addNextEOG(exitEdge);
                mutationContext.registerIncomingEdge(exitEdge);
                exitEdges = List.of(exitEdge);
            } else {
                return;
            }
        }
        exitEdges = exitEdges.stream().filter(e -> e.getEnd().equals(dummy)).toList();
        assert !exitEdges.isEmpty();
        Node entry = getEntry(newSuccessor);

        getEntryEdges(newSuccessor, entry, false, mutationContext).forEach(e -> {
            e.getStart().getNextEOGEdges().remove(e);
            entry.getPrevEOGEdges().remove(e);
        });

        exitEdges.forEach(e -> {
            dummy.getPrevEOGEdges().remove(e);
            e.setEnd(entry);
            entry.addPrevEOG(e);
        });

    }

    /**
     * Disconnects the given {@link Node} from its EOG predecessor.
     * @param node the node
     * @param mutationContext graph mutation state owned by this translation
     * @return the EOG predecessor
     */
    public static List<Node> disconnectFromPredecessor(Node node, GraphMutationContext mutationContext) {
        DummyNeighbor dummy = mutationContext.getDummyNeighbor();
        Node entry = getEntry(node);

        List<PropertyEdge<Node>> entryEdges = getEntryEdges(node, entry, true, mutationContext);
        if (entryEdges.isEmpty())
            return List.of();

        List<Node> predExits = entryEdges.stream().map(PropertyEdge::getStart).toList();

        entryEdges.stream().filter(e -> !Objects.equals(e.getStart(), dummy)).filter(e -> !Objects.equals(e.getEnd(), dummy)).forEach(e -> {
            PropertyEdge<Node> dummyEdge = new PropertyEdge<>(e);
            dummy.saveOriginalSource(e);

            e.getStart().getNextEOGEdges().remove(e);
            e.setStart(dummy);
            mutationContext.registerOutgoingEdge(e);

            dummy.saveOriginalTarget(dummyEdge);
            dummyEdge.setEnd(dummy);
            mutationContext.registerIncomingEdge(dummyEdge);
            dummyEdge.getStart().addNextEOG(dummyEdge);
        });
        return predExits;
    }

    /**
     * Gets the first EOG entry {@link Node} of the given {@link Node}.
     * @param node the node
     * @return the EOG entry node
     */
    public static Node getEntry(Node node) {
        return getEogBorders(node).getEntries().getFirst();
    }

    /**
     * Gets the first EOG exit {@link Node} of the given {@link Node}.
     * @param node the node
     * @return the EOG entry node
     */
    static Node getExit(Node node) {
        return getEogBorders(node).getExits().getFirst();
    }

    static Node connectNewPredecessor(Node target, Node newPredecessor, GraphMutationContext mutationContext) {
        DummyNeighbor dummy = mutationContext.getDummyNeighbor();
        Node entry = getEntry(target);
        List<Node> exits = getEogBorders(newPredecessor).getExits();
        List<PropertyEdge<Node>> exitEdges = getExitEdges(target, exits, true, mutationContext);

        assert exitEdges.stream().allMatch(e -> e.getEnd().equals(dummy));
        if (exitEdges.isEmpty())
            return target;

        getEntryEdges(target, entry, true, mutationContext).forEach(e -> {
            e.getStart().getNextEOGEdges().remove(e);
            entry.getPrevEOGEdges().remove(e);
        });

        exitEdges.forEach(e -> {
            dummy.getPrevEOGEdges().remove(e);
            e.setEnd(entry);
            entry.addPrevEOG(e);
        });

        return entry;
    }

    /**
     * Gets the EOG entry edges to the AST subtree represented by astParent, that connect to the given entry node.
     * @param astParent the root of the AST subtree
     * @param entry the EOG entry to the astParent
     * @param useDummies if true, the returned edges may not be the current entries to the astParent, but instead earlier
     * entry edges to the astParent that have been disconnected and saved for use later.
     * @param mutationContext graph mutation state owned by this translation
     * @return the entry edges
     */
    @NotNull
    public static List<PropertyEdge<Node>> getEntryEdges(Node astParent, Node entry, boolean useDummies, GraphMutationContext mutationContext) {
        DummyNeighbor dummy = mutationContext.getDummyNeighbor();
        List<PropertyEdge<Node>> currentEntryEdges = entry.getPrevEOGEdges().stream().filter(e -> !isAstChild(astParent, e.getStart())).toList();
        List<PropertyEdge<Node>> disconnectedEdges = currentEntryEdges.stream().filter(e -> e.getStart().equals(dummy)).toList();

        List<PropertyEdge<Node>> originalEntryEdges = dummy.getOriginalEdgeOfTarget(entry);

        if (originalEntryEdges.isEmpty() || !useDummies) {
            // Node is still in its proper place
            return currentEntryEdges;
        } else if (!disconnectedEdges.isEmpty()) {
            // Node is disconnected
            return disconnectedEdges;
        } else {
            // Node has been reattached; return original exit edges (and clear them)
            dummy.clearOriginalEdgesOfTarget(entry);
            return originalEntryEdges;
        }
    }

    /**
     * Gets the EOG exit edges to the AST subtree represented by astParent, that connect to the given entry node.
     * @param astParent the root of the AST subtree
     * @param exits the EOG exit nodes of the astParent
     * @param useDummies if true, the returned edges may not be the current exits of the astParent, but instead earlier exit
     * edges of the astParent that have been disconnected and saved for use later.
     * @param mutationContext graph mutation state owned by this translation
     * @return the exit edges
     */
    @NotNull
    public static List<PropertyEdge<Node>> getExitEdges(Node astParent, List<Node> exits, boolean useDummies, GraphMutationContext mutationContext) {
        DummyNeighbor dummy = mutationContext.getDummyNeighbor();
        List<PropertyEdge<Node>> currentExitEdges = exits.stream().flatMap(n -> n.getNextEOGEdges().stream())
                .filter(e -> !isAstChild(astParent, e.getEnd()) || (astParent instanceof Block && e.getEnd() == astParent)).toList();
        List<PropertyEdge<Node>> disconnectedEdges = currentExitEdges.stream().filter(e -> e.getEnd().equals(dummy)).toList();

        List<PropertyEdge<Node>> originalEdges = exits.stream().map(dummy::getOriginalEdgeOfSource).flatMap(List::stream).toList();
        if (originalEdges.isEmpty() || !useDummies) {
            // Node is still in its proper place
            return currentExitEdges;
        } else if (!disconnectedEdges.isEmpty()) {
            // Node is disconnected
            return disconnectedEdges;
        } else {
            // Node has been reattached; return original exit edges (and clear them)
            exits.forEach(dummy::clearOriginalEdgesOfTarget);
            return originalEdges;
        }

    }

    /**
     * Inserts the given {@link Node} before the given newSuccessor {@link Node} in the EOG graph.
     * @param target the node
     * @param newSuccessor the new successor node
     * @param mutationContext graph mutation state owned by this translation
     */
    public static void insertBefore(Node target, Node newSuccessor, GraphMutationContext mutationContext) {
        Node entry = getEntry(target);
        List<Node> exits = getEogBorders(target).getExits();
        disconnectFromPredecessor(target, mutationContext);
        disconnectFromSuccessor(target, mutationContext);

        Node succEntry = getEntry(newSuccessor);
        List<Node> newPreds = disconnectFromPredecessor(newSuccessor, mutationContext);
        newPreds.forEach(pred -> connectNewSuccessor(pred, entry, false, mutationContext));
        exits.forEach(exit -> connectNewSuccessor(exit, succEntry, false, mutationContext));
    }

    /**
     * Inserts the given {@link Node} after the given newSuccessor {@link Node} in the EOG graph.
     * @param target the node
     * @param newPredecessor the new predecessor node
     * @param mutationContext graph mutation state owned by this translation
     */
    public static void insertAfter(Node target, Node newPredecessor, GraphMutationContext mutationContext) {
        Node entry = getEntry(target);
        Node exit = getExit(target);
        disconnectFromPredecessor(target, mutationContext);
        disconnectFromSuccessor(target, mutationContext);

        Node predExit = getExit(newPredecessor);
        Node newSuccEntry = disconnectFromSuccessor(newPredecessor, mutationContext);
        connectNewSuccessor(exit, newSuccEntry, false, mutationContext);
        connectNewSuccessor(predExit, entry, false, mutationContext);
    }

    /**
     * Returns true if the {@code maybeSuccessor} {@link Node} is the AST neighbor following the {@code element} node.
     * @param element a {@link Node} object
     * @param maybeSuccessor the potential successor {@link Node}
     * @param mutationContext graph mutation state owned by this translation
     * @return true if {@code maybeSuccessor} is an AST successor of {@code element}
     */
    public static boolean isAstSuccessor(Node element, Node maybeSuccessor, GraphMutationContext mutationContext) {
        // If maybeSuccessor is the AST successor of element, then an exit edge of element points to maybeSuccessor
        // The exit is likely to be a child node, not element itself
        List<Node> exits = getEogBorders(element).getExits();
        Node entry = getEntry(maybeSuccessor);
        List<PropertyEdge<Node>> entryEdges = getEntryEdges(maybeSuccessor, entry, false, mutationContext);

        return entryEdges.stream().anyMatch(e -> exits.contains(e.getStart()));
    }

    /**
     * Returns true if the {@code maybeSuccessor} {@link Node} is the EOG successor of the {@code element} node.
     * @param exit a {@link Node} object
     * @param maybeSuccessor the potential successor {@link Node}
     * @param mutationContext graph mutation state owned by this translation
     * @return true if {@code maybeSuccessor} is the EOG successor of {@code element}
     */
    public static boolean isEogSuccessor(Node exit, Node maybeSuccessor, GraphMutationContext mutationContext) {
        // Unlike isAstSuccessor, we check for edges from exit directly
        Node entry = getEntry(maybeSuccessor);
        List<PropertyEdge<Node>> entryEdges = getEntryEdges(maybeSuccessor, entry, false, mutationContext);

        return entryEdges.stream().anyMatch(e -> exit == e.getStart());
    }

    /**
     * Computes the iterative successors of a list of elements w.r.t. a successor function, collecting those elements with
     * no successors.
     * @param initialList the initial list of elements
     * @param successorFunction the successor function
     * @param <T> the element type
     * @return the list of successors with no successors
     */
    private static <T> List<T> worklist(Collection<T> initialList, Function<T, ? extends List<T>> successorFunction) {
        List<T> seen = new ArrayList<>(initialList);
        List<T> worklist = new LinkedList<>(initialList);
        List<T> result = new ArrayList<>();
        while (!worklist.isEmpty()) {
            T element = worklist.removeFirst();
            List<T> successors = successorFunction.apply(element);
            if (successors.isEmpty()) {
                result.add(element);
            } else {
                successors.removeAll(seen);
                worklist.addAll(successors);
                seen.addAll(successors);
            }
        }
        return result;
    }
}
