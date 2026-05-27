package de.jplag.normalization;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.stream.Collectors;

import org.jgrapht.Graphs;

import de.jplag.Token;

/**
 * Performs token sequence normalization.
 */
public final class TokenSequenceNormalizer {

    private TokenSequenceNormalizer() {
        // private constructor for non-instantiability.
    }

    /**
     * Performs token sequence normalization. Tokens representing dead code have been eliminated and tokens representing
     * subsequent independent statements have been put in a fixed order if sorting is true. Works by first constructing a
     * Normalization Graph and then turning it back into a token sequence. For more information refer to the
     * <a href="https://doi.org/10.1145/3639478.3643074">corresponding paper</a>.
     * @param tokens The original token sequence, remains unaltered.
     * @return The normalized token sequence.
     */
    public static List<Token> normalize(List<Token> tokens) {
        NormalizationGraph graph = new NormalizationGraph(tokens);
        propagateCriticalityStatus(graph);
        return normalizeWithSorting(tokens, graph);
    }

    // Add tokens in normalized original order, removing dead tokens
    private static List<Token> normalizeWithSorting(List<Token> tokens, NormalizationGraph normalizationGraph) {
        List<Token> normalizedTokens = new ArrayList<>(tokens.size());
        PriorityQueue<Statement> roots = normalizationGraph.vertexSet().stream() //
                .filter(v -> !Graphs.vertexHasPredecessors(normalizationGraph, v)) //
                .collect(Collectors.toCollection(PriorityQueue::new));
        while (!roots.isEmpty()) {
            PriorityQueue<Statement> newRoots = new PriorityQueue<>();
            do {
                Statement statement = roots.poll();
                if (statement.semantics().isCritical()) {
                    normalizedTokens.addAll(statement.tokens());
                }
                for (Statement successor : Graphs.successorListOf(normalizationGraph, statement)) {
                    normalizationGraph.removeEdge(statement, successor);
                    if (!Graphs.vertexHasPredecessors(normalizationGraph, successor)) {
                        newRoots.add(successor);
                    }
                }
            } while (!roots.isEmpty());
            roots = newRoots;
        }
        return normalizedTokens;
    }

    /**
     * Spread criticality status to every node that does not represent dead code. Nodes without keep criticality are later
     * eliminated (dead nodes). Before calling this method, only the statements that directly affect the behavior are marked
     * as critical. After calling this method, this also holds true for statement that (transitively) depend (read/write) on
     * the critical ones.
     */
    private static void propagateCriticalityStatus(NormalizationGraph normalizationGraph) {
        Queue<Statement> visit = new LinkedList<>(normalizationGraph.vertexSet().stream() //
                .filter(tl -> tl.semantics().isCritical()).toList());
        while (!visit.isEmpty()) {
            Statement current = visit.remove();
            for (Statement predecessor : Graphs.predecessorListOf(normalizationGraph, current)) {  // performance of iteration?
                if (!predecessor.semantics().isCritical() && normalizationGraph.getEdge(predecessor, current).isVariableFlow()) {
                    predecessor.markAsCritical();
                    visit.add(predecessor);
                }
            }
            for (Statement successor : Graphs.successorListOf(normalizationGraph, current)) {
                if (!successor.semantics().isCritical() && normalizationGraph.getEdge(current, successor).isVariableReverseFlow()) {
                    successor.markAsCritical();
                    visit.add(successor);
                }
            }
        }
    }
}
