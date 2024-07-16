package de.jplag.normalization;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.stream.Collectors;

import org.jgrapht.Graphs;
import org.jgrapht.graph.SimpleDirectedGraph;

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
     * Normalization Graph and then turning it back into a token sequence.
     * @param tokens The original token sequence, remains unaltered.
     * @param sorting Boolean flag to control if the tokens should be topologically sorted.
     * @return The normalized token sequence.
     */
    public static List<Token> normalize(List<Token> tokens, boolean sorting) {
        SimpleDirectedGraph<Statement, MultipleEdge> normalizationGraph = new NormalizationGraphConstructor(tokens).get();
        propagateKeepStatus(normalizationGraph);
        if (sorting) {
            return normalizeWithSorting(tokens, normalizationGraph);
        }
        return normalizeWithoutSorting(normalizationGraph, tokens);
    }

    // Add tokens in normalized original order, removing dead tokens
    private static List<Token> normalizeWithSorting(List<Token> tokens, SimpleDirectedGraph<Statement, MultipleEdge> normalizationGraph) {
        List<Token> normalizedTokens = new ArrayList<>(tokens.size());
        PriorityQueue<Statement> roots = normalizationGraph.vertexSet().stream().filter(v -> !Graphs.vertexHasPredecessors(normalizationGraph, v))
                .collect(Collectors.toCollection(PriorityQueue::new));
        while (!roots.isEmpty()) {
            PriorityQueue<Statement> newRoots = new PriorityQueue<>();
            do {
                Statement statement = roots.poll();
                if (statement.semantics().keep()) {
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

    // Add tokens in the original order, removing dead tokens
    private static List<Token> normalizeWithoutSorting(SimpleDirectedGraph<Statement, MultipleEdge> normalizationGraph, List<Token> tokens) {
        List<Token> normalizedTokens = new ArrayList<>(tokens.size());
        for (Statement statement : normalizationGraph.vertexSet()) {
            if (statement.semantics().keep()) {
                normalizedTokens.addAll(statement.tokens());
            }
        }
        return normalizedTokens;
    }

    /**
     * Spread keep status to every node that does not represent dead code. Nodes without keep status are later eliminated.
     */
    private static void propagateKeepStatus(SimpleDirectedGraph<Statement, MultipleEdge> normalizationGraph) {
        Queue<Statement> visit = new LinkedList<>(normalizationGraph.vertexSet().stream() //
                .filter(tl -> tl.semantics().keep()).toList());
        while (!visit.isEmpty()) {
            Statement current = visit.remove();
            for (Statement predecessor : Graphs.predecessorListOf(normalizationGraph, current)) {  // performance of iteration?
                if (!predecessor.semantics().keep() && normalizationGraph.getEdge(predecessor, current).isVariableFlow()) {
                    predecessor.markKeep();
                    visit.add(predecessor);
                }
            }
            for (Statement successor : Graphs.successorListOf(normalizationGraph, current)) {
                if (!successor.semantics().keep() && normalizationGraph.getEdge(current, successor).isVariableReverseFlow()) {
                    successor.markKeep();
                    visit.add(successor);
                }
            }
        }
    }
}
