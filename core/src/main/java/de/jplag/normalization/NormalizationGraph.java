package de.jplag.normalization;

import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;
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
                if (tokenLine.semantics().keep()) {
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
        Set<TokenLine> originalKeep = graph.vertexSet().stream() //
                .filter(tl -> tl.semantics().keep()).collect(Collectors.toSet());
        Deque<TokenLine> visit = new LinkedList<>(originalKeep);
        while (!visit.isEmpty()) {
            TokenLine current = visit.pop();
            if (originalKeep.contains(current) || !current.semantics().keep()) {
                current.markKeep();
                visit.addAll(Graphs.predecessorListOf(graph, current).stream() //
                        .filter(pred -> graph.getEdge(pred, current).isData()).toList());
                visit.addAll(Graphs.successorListOf(graph, current).stream() //
                        .filter(succ -> graph.getEdge(current, succ).isReverseData()).toList());
            }
        }
    }
}
