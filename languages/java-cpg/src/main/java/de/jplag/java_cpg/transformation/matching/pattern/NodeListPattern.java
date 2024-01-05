package de.jplag.java_cpg.transformation.matching.pattern;

import de.fraunhofer.aisec.cpg.graph.Node;

import java.util.ArrayList;

public class NodeListPattern<T extends Node> {

    private final Class<T> tClass;
    private final ArrayList<NodePattern<? extends T>> elements;
    private int elementCount;

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
}
