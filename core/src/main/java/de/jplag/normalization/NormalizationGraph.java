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
    private SimpleDirectedGraph<TokenLine, Dependency> graph;

    public NormalizationGraph(List<Token> tokens) {
        graph = new NormalizationGraphConstructor(tokens).get();
    }

    // todo java doc
    public List<Token> linearize() {
        spreadKeep();
        PriorityQueue<TokenLine> roots = graph.vertexSet().stream() //
                .filter(v -> !Graphs.vertexHasPredecessors(graph, v)) //
                .collect(Collectors.toCollection(PriorityQueue::new));
        List<Token> tokens = new LinkedList<>();
        while (!roots.isEmpty()) {
            PriorityQueue<TokenLine> newRoots = new PriorityQueue<>();
            do {
                TokenLine tokenLine = roots.poll();
                if (tokenLine.keep()) {
                    tokens.addAll(tokenLine.tokens());
                }
                for (TokenLine successorGroup : Graphs.successorListOf(graph, tokenLine)) {
                    graph.removeEdge(tokenLine, successorGroup);
                    if (!Graphs.vertexHasPredecessors(graph, successorGroup)) {
                        newRoots.add(successorGroup);
                    }
                }
            } while (!roots.isEmpty());
            roots = newRoots;
        }
        return tokens;
    }

    private void spreadKeep() {
        Deque<TokenLine> visit = new LinkedList<>(graph.vertexSet().stream().filter(TokenLine::keep).toList());
        while (!visit.isEmpty()) {
            TokenLine current = visit.pop();
            for (TokenLine pred : Graphs.predecessorListOf(graph, current)) { // performance of iteration?
                if (!pred.keep() && graph.getEdge(pred, current).isData()) {
                    pred.markKeep();
                    visit.add(pred);
                }
            }
            for (TokenLine succ : Graphs.successorListOf(graph, current)) {
                if (!succ.keep() && graph.getEdge(current, succ).isReverseData()) {
                    succ.markKeep();
                    visit.add(succ);
                }
            }
        }
    }
}
