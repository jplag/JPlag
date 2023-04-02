package de.jplag.normalization;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.stream.Collectors;

import org.jgrapht.Graphs;
import org.jgrapht.graph.SimpleDirectedGraph;

import de.jplag.Token;

public class NormalizationGraph {
    private SimpleDirectedGraph<Statement, Edge> graph;

    /**
     * Construct a new normalization graph from the tokens.
     * @param tokens The tokens used to construct the normalization graph.
     */
    public NormalizationGraph(List<Token> tokens) {
        graph = new NormalizationGraphConstructor(tokens).get();
    }

    /**
     * Turns this normalization graph back into a list of tokens. Tokens representing dead code have been eliminated and
     * tokens representing subsequent independent statements have been put in a fixed order.
     * @return the normalized list of tokens.
     */
    public List<Token> linearize() {
        spreadKeep();
        PriorityQueue<Statement> roots = graph.vertexSet().stream() //
                .filter(v -> !Graphs.vertexHasPredecessors(graph, v)) //
                .collect(Collectors.toCollection(PriorityQueue::new));
        List<Token> tokens = new ArrayList<>();
        while (!roots.isEmpty()) {
            PriorityQueue<Statement> newRoots = new PriorityQueue<>();
            do {
                Statement statement = roots.poll();
                if (statement.semantics().keep()) {
                    tokens.addAll(statement.tokens());
                }
                for (Statement succ : Graphs.successorListOf(graph, statement)) {
                    graph.removeEdge(statement, succ);
                    if (!Graphs.vertexHasPredecessors(graph, succ)) {
                        newRoots.add(succ);
                    }
                }
            } while (!roots.isEmpty());
            roots = newRoots;
        }
        return tokens;
    }

    /**
     * Spread keep status to every node that does not represent dead code. Nodes without keep status are later eliminated.
     */
    private void spreadKeep() {
        Deque<Statement> visit = new LinkedList<>(graph.vertexSet().stream() //
                .filter(tl -> tl.semantics().keep()).toList());
        while (!visit.isEmpty()) {
            Statement current = visit.pop();
            for (Statement pred : Graphs.predecessorListOf(graph, current)) {  // performance of iteration?
                if (!pred.semantics().keep() && graph.getEdge(pred, current).isVariableFlow()) {
                    pred.markKeep();
                    visit.add(pred);
                }
            }
            for (Statement succ : Graphs.successorListOf(graph, current)) {
                if (!succ.semantics().keep() && graph.getEdge(current, succ).isVariableReverseFlow()) {
                    succ.markKeep();
                    visit.add(succ);
                }
            }
        }
    }
}
