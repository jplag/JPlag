package de.jplag.normalization;

import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.stream.Collectors;

import org.jgrapht.Graphs;
import org.jgrapht.graph.SimpleDirectedGraph;

import de.jplag.Token;

public class Normalizer {

    private Normalizer() {
    }

    public static List<Token> normalize(List<Token> tokens) {
        List<TokenGroup> tokenGroups = TokenGroup.group(tokens);
        SimpleDirectedGraph<TokenGroup, Dependency> graph = new GraphConstructor(tokenGroups).get();
        tokenGroups = linearizeGraph(graph);
        return TokenGroup.ungroup(tokenGroups);
    }

    private static List<TokenGroup> linearizeGraph(SimpleDirectedGraph<TokenGroup, Dependency> graph) {
        PriorityQueue<TokenGroup> roots = graph.vertexSet().stream() //
                .filter(v -> !Graphs.vertexHasPredecessors(graph, v)) //
                .collect(Collectors.toCollection(PriorityQueue::new));
        List<TokenGroup> tokenGroups = new LinkedList<>();
        while (!roots.isEmpty()) {
            PriorityQueue<TokenGroup> newRoots = new PriorityQueue<>();
            do {
                TokenGroup group = roots.poll();
                tokenGroups.add(group);
                for (TokenGroup successorGroup : Graphs.successorListOf(graph, group)) {
                    graph.removeEdge(group, successorGroup);
                    if (!Graphs.vertexHasPredecessors(graph, successorGroup)) {
                        newRoots.add(successorGroup);
                    }
                }
            } while (!roots.isEmpty());
            roots = newRoots;
        }
        assert tokenGroups.size() == graph.vertexSet().size();
        return tokenGroups;
    }
}
