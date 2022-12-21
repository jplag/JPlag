package de.jplag.normalization;

import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.stream.Collectors;

import org.jgrapht.Graphs;
import org.jgrapht.graph.DirectedMultigraph;

import de.jplag.Token;

public class Normalizer {

    private Normalizer() {
    }

    public static List<Token> normalize(List<Token> tokens) {
        List<TokenGroup> tokenGroups = TokenGroup.group(tokens);
        List<TokenGroup> originalTokenGroups = new LinkedList<>(tokenGroups);
        DirectedMultigraph<TokenGroup, Dependency> graph = constructGraph(tokenGroups);
        tokenGroups = linearizeGraph(graph);
        assert tokenGroups.equals(originalTokenGroups);
        return TokenGroup.ungroup(tokenGroups);
    }

    private static DirectedMultigraph<TokenGroup, Dependency> constructGraph(List<TokenGroup> tokenGroups) {
        DirectedMultigraph<TokenGroup, Dependency> graph = new DirectedMultigraph<>(Dependency.class);
        TokenGroup startGroup = tokenGroups.remove(0);
        graph.addVertex(startGroup);
        for (TokenGroup endGroup : tokenGroups) {
            graph.addVertex(endGroup);
            graph.addEdge(startGroup, endGroup, new Dependency(DependencyType.DATA, null));
            startGroup = endGroup;
        }
        return graph;
    }

    private static List<TokenGroup> linearizeGraph(DirectedMultigraph<TokenGroup, Dependency> graph) {
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
                    graph.removeAllEdges(group, successorGroup);
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
