package de.jplag.java_cpg;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import de.fraunhofer.aisec.cpg.graph.Node;
import de.jplag.java_cpg.token.characteristic.CharacteristicVector;

/**
 * A registry to store characteristic vectors for each node.
 */
public final class NodeRegistry {
    /**
     * The singleton instance of the NodeRegistry.
     */
    public static final NodeRegistry INSTANCE = new NodeRegistry();

    private NodeRegistry() {
        // private constructor to hide implicit public one
    }

    private final Map<Node, CharacteristicVector> nodeMap = new ConcurrentHashMap<>();

    /**
     * Registers a characteristic vector for a node.
     * @param node the node
     * @param value the characteristic vector
     */
    public void registerNodeData(Node node, CharacteristicVector value) {
        nodeMap.put(node, value);
    }

    /**
     * Gets the characteristic vector for a node.
     * @param node the node
     * @return the characteristic vector
     */
    public CharacteristicVector getNodeData(Node node) {
        return nodeMap.get(node);
    }

    /**
     * Clears the registry. Removes all stored nodes and their characteristic vectors.
     */
    public void clear() {
        nodeMap.clear();
    }

    /**
     * Checks if a characteristic vector is stored for a node.
     * @param node the node
     * @return true if a characteristic vector is stored for the node, false otherwise
     */
    public boolean containsNode(Node node) {
        return nodeMap.containsKey(node);
    }
}