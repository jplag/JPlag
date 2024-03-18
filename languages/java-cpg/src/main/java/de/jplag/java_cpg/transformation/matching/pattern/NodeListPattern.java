package de.jplag.java_cpg.transformation.matching.pattern;

import java.util.ArrayList;

import de.fraunhofer.aisec.cpg.graph.Node;

/**
 * A {@link NodeListPattern} is a pattern that involves a sequence of {@link Node}s.
 * @param <T>
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

    public NodeListPattern(Class<T> tClass) {
        this.tClass = tClass;
        this.elements = new ArrayList<>();
    }

    public void addElement(NodePattern<? extends T> nodePattern) {
        elements.add(nodePattern);
    }

    public NodePattern<? extends T> get(int index) {
        return elements.get(index);
    }

    public ArrayList<NodePattern<? extends T>> getElements() {
        return new ArrayList<>(elements);
    }

    public int size() {
        return elements.size();
    }
}
