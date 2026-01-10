package de.jplag.java_cpg;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import de.fraunhofer.aisec.cpg.graph.Node;
import de.jplag.java_cpg.token.semantic.SemanticVector;

public final class NodeRegistry {
    public static final NodeRegistry INSTANCE = new NodeRegistry();

    private NodeRegistry() {
    }

    private final Map<Node, SemanticVector> nodeMap = new ConcurrentHashMap<>();

    public void registerNodeData(Node node, SemanticVector value) {
        nodeMap.put(node, value);
    }

    public SemanticVector getNodeData(Node node) {
        return nodeMap.get(node);
    }

    public void clear() {
        nodeMap.clear();
    }

    public boolean containsNode(Node node) {
        return nodeMap.containsKey(node);
    }
}