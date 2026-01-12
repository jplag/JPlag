package de.jplag.java_cpg;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import de.fraunhofer.aisec.cpg.graph.Node;
import de.jplag.java_cpg.token.characteristic.CharacteristicVector;

public final class NodeRegistry {
    public static final NodeRegistry INSTANCE = new NodeRegistry();

    private NodeRegistry() {
    }

    private final Map<Node, CharacteristicVector> nodeMap = new ConcurrentHashMap<>();

    public void registerNodeData(Node node, CharacteristicVector value) {
        nodeMap.put(node, value);
    }

    public CharacteristicVector getNodeData(Node node) {
        return nodeMap.get(node);
    }

    public void clear() {
        nodeMap.clear();
    }

    public boolean containsNode(Node node) {
        return nodeMap.containsKey(node);
    }
}