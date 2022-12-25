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
    private SimpleDirectedGraph<TokenGroup, Dependency> graph;

    public NormalizationGraph(List<Token> tokens) {
        graph = new NormalizationGraphConstructor(tokens).get();
    }

    public List<Token> linearize() {
        spreadKeep();
        PriorityQueue<TokenGroup> roots = graph.vertexSet().stream() //
                .filter(v -> !Graphs.vertexHasPredecessors(graph, v)) //
                .collect(Collectors.toCollection(PriorityQueue::new));
        List<Token> tokens = new LinkedList<>();
        while (!roots.isEmpty()) {
            PriorityQueue<TokenGroup> newRoots = new PriorityQueue<>();
            do {
                TokenGroup group = roots.poll();
                if (!group.keep()) {
                    System.out.println("removed " + group);
                }
                tokens.addAll(group.tokens());
                for (TokenGroup successorGroup : Graphs.successorListOf(graph, group)) {
                    graph.removeEdge(group, successorGroup);
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
        Deque<TokenGroup> visit = new LinkedList<>(graph.vertexSet().stream().filter(TokenGroup::keep).toList());
        while (!visit.isEmpty()) {
            TokenGroup current = visit.pop();
            for (TokenGroup pred : Graphs.predecessorListOf(graph, current)) { // performance?
                if (graph.getEdge(pred, current).isData() && !pred.keep()) {
                    pred.markKeep();
                    visit.add(pred);
                }
            }
            // not great performance-wise but I doubt it matters at this stage...
            // could instead insert data-through-loop edges the other way around, which arguably makes more sense semantically
            // and turn them around here, but too much code for me to bother right now
            for (TokenGroup succ : Graphs.successorListOf(graph, current)) {
                if (graph.getEdge(current, succ).isDataThroughLoop() && !succ.keep()) {
                    succ.markKeep();
                    visit.add(succ);
                }
            }
        }
    }
}
