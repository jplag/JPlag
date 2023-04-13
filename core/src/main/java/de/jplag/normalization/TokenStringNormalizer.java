package de.jplag.normalization;

import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.stream.Collectors;

import org.jgrapht.Graphs;
import org.jgrapht.graph.SimpleDirectedGraph;

import de.jplag.Token;

/**
 * The class that performs token string normalization.
 */
public class TokenStringNormalizer {

    private TokenStringNormalizer() {
    }

    /**
     * Normalizes the token string it receives inplace. Tokens representing dead code have been eliminated and tokens
     * representing subsequent independent statements have been put in a fixed order. Works by first constructing a
     * Normalization Graph and then turning it back into a token string.
     */
    public static void normalize(List<Token> tokens) {
        SimpleDirectedGraph<Statement, Edge> normalizationGraph = new NormalizationGraphConstructor(tokens).get();
        tokens.clear();
        spreadKeep(normalizationGraph);
        PriorityQueue<Statement> roots = normalizationGraph.vertexSet().stream() //
                .filter(v -> !Graphs.vertexHasPredecessors(normalizationGraph, v)) //
                .collect(Collectors.toCollection(PriorityQueue::new));
        while (!roots.isEmpty()) {
            PriorityQueue<Statement> newRoots = new PriorityQueue<>();
            do {
                Statement statement = roots.poll();
                if (statement.semantics().keep()) {
                    tokens.addAll(statement.tokens());
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
    }

    /**
     * Spread keep status to every node that does not represent dead code. Nodes without keep status are later eliminated.
     */
    private static void spreadKeep(SimpleDirectedGraph<Statement, Edge> normalizationGraph) {
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
