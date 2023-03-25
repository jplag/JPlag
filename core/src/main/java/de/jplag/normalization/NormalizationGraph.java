package de.jplag.normalization;

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

    public NormalizationGraph(List<Token> tokens) {
        graph = new NormalizationGraphConstructor(tokens).get();
    }

    // todo java doc
    public List<Token> linearize() {
        spreadKeep();
        PriorityQueue<Statement> roots = graph.vertexSet().stream() //
                .filter(v -> !Graphs.vertexHasPredecessors(graph, v)) //
                .collect(Collectors.toCollection(PriorityQueue::new));
        List<Token> tokens = new LinkedList<>();
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
