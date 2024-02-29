package de.jplag.java_cpg.transformation.matching.pattern;

import de.fraunhofer.aisec.cpg.graph.Node;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

public interface GraphPattern {
    String REPRESENTING_NODE = "representant";

    Collection<String> getAllIds();

    List<Class<? extends Node>> getCandidateNodeClasses();

    void compareTo(GraphPattern targetPattern, BiConsumer<NodePattern<?>, NodePattern<?>> compareFunction);

    <T extends Node> NodePattern<T> addNode(String roleName, NodePattern<T> newNode);

    NodePattern<?> getPattern(String roleName);

    String getId(NodePattern<?> source);

    NodePattern<?> getRepresentingNode();

    <T extends Node> List<Match> recursiveMatch(T candidate);

    List<NodePattern<?>> getRoots();

    Iterator<Match> multiMatch(Map<NodePattern<?>, List<? extends Node>> rootCandidates);

    boolean validate(Match match);
}
