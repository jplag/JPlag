package de.jplag.java_cpg.transformation.matching.pattern;

import java.util.ArrayList;
import java.util.List;

import de.fraunhofer.aisec.cpg.graph.Node;

/**
 * A {@link NodeListPattern} is a pattern that involves a sequence of {@link Node}s.
 * @param <T> the base type of the nodes
 */
public class NodeListPattern<T extends Node> {

    /*
     * Not used but aids in debugging
     */
    private final Class<T> tClass;
    /**
     * The elements of the node list
     */
    private final ArrayList<NodePattern<? extends T>> elements;

    /**
     * Constructs a new {@link NodeListPattern}.
     * @param tClass the common supertype of the {@link Node}s in the list.
     */
    public NodeListPattern(Class<T> tClass) {
        this.tClass = tClass;
        this.elements = new ArrayList<>();
    }

    /**
     * Adds a {@link NodePattern} to the {@link NodeListPattern}.
     * @param nodePattern the node pattern
     */
    public void addElement(NodePattern<? extends T> nodePattern) {
        elements.add(nodePattern);
    }

    /**
     * Gets the nth {@link Node} of this {@link NodeListPattern}
     * @param index the index
     * @return the element at the given index
     */
    public NodePattern<? extends T> get(int index) {
        return elements.get(index);
    }

    /**
     * Gets the list of all {@link Node}s in this {@link NodeListPattern}.
     * @return list of all elements
     */
    public List<NodePattern<? extends T>> getElements() {
        return new ArrayList<>(elements);
    }

    /**
     * Gets the size of the {@link NodeListPattern}.
     * @return the element count
     */
    public int size() {
        return elements.size();
    }
}
