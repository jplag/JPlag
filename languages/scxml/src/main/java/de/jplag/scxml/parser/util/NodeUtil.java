package de.jplag.scxml.parser.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Utility class providing methods for conveniently iterating over nodes in the DOM.
 */
public final class NodeUtil {

    private NodeUtil() {

    }

    /**
     * Iterates over all immediate child nodes of the given root node and returns a list of child nodes whose node names
     * match any of the provided node names.
     * @param root the root node
     * @param childNames a set of child node names to consider
     * @return a list of matching child nodes
     */
    public static List<Node> getChildNodes(Node root, Set<String> childNames) {
        List<Node> matchingChildren = new ArrayList<>();
        NodeList children = root.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (childNames.contains(child.getNodeName())) {
                matchingChildren.add(children.item(i));
            }
        }
        return matchingChildren;
    }

    /**
     * Iterates over all immediate direct child nodes of the given root node and returns a list of child nodes whose node
     * names match the provided node name.
     * @param root the root node
     * @param childName the child node name to consider
     * @return a list of matching child nodes
     */
    public static List<Node> getChildNodes(Node root, String childName) {
        return getChildNodes(root, Set.of(childName));
    }

    /**
     * Iterates over all immediate direct child nodes of the given root node and returns the first child node whose node
     * name matches the provided node name. If there are no matching nodes, null is returned.
     * @param root the root node
     * @param childName the node name to consider
     * @return the first matching child node, or null if none are found
     */
    public static Node getFirstChild(Node root, String childName) {
        List<Node> children = getChildNodes(root, Set.of(childName));
        return children.isEmpty() ? null : children.get(0);
    }

    /**
     * Recursively iterates over all child nodes of the given root node and returns a list of child nodes whose node names
     * match the provided node name.
     * @param root the root node
     * @param childName the node name to consider
     * @return a list of matching child nodes
     */
    public static List<Node> getNodesRecursive(Node root, String childName) {
        List<Node> matchingNodes = new ArrayList<>();
        if (root.getNodeName().equals(childName)) {
            matchingNodes.add(root);
        }
        NodeList children = root.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            matchingNodes.addAll(getNodesRecursive(child, childName));
        }
        return matchingNodes;
    }

    /**
     * Retrieves the value of an attribute with the specified name from the given node. If the attribute is not present,
     * {@code null} is returned.
     * @param node the node containing the attribute
     * @param name the name of the attribute to retrieve
     * @return the value of the attribute, or {@code null} if the node does not contain an attribute with the given name
     */
    public static String getAttribute(Node node, String name) {
        Node attribute = node.getAttributes().getNamedItem(name);
        if (attribute != null) {
            return attribute.getNodeValue();
        }
        return null;
    }
}
