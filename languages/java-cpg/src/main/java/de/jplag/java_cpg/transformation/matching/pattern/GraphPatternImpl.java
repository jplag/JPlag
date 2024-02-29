package de.jplag.java_cpg.transformation.matching.pattern;

import de.fraunhofer.aisec.cpg.graph.Node;

import java.util.*;
import java.util.stream.Collectors;

public abstract class GraphPatternImpl implements GraphPattern {
    protected final PatternRegistry patternRegistry;
    protected NodePattern<?> representingNode;

    public GraphPatternImpl(PatternRegistry patterns) {
        representingNode = patterns.getRepresentingNode();
        this.patternRegistry = patterns;
    }

    static List<Match> copy(List<Match> matches) {
        return matches.stream().map(Match::copy).collect(Collectors.toList());
    }

    /**
     * Gets the {@link String} ID of the given {@link NodePattern}
     *
     * @param source the node pattern
     * @return the ID
     */
    public String getId(NodePattern<?> source) {
        return patternRegistry.getId(source);
    }

    /**
     * Gets the {@link NodePattern} corresponding to the given {@link String} ID.
     *
     * @param roleName the ID
     * @return the node pattern
     */
    public NodePattern<?> getPattern(String roleName) {
        return patternRegistry.getPattern(roleName);
    }

    public Collection<String> getAllIds() {
        return patternRegistry.allIds();
    }

    /**
     * Adds a copy of the given (transformation target) {@link NodePattern} to this (transformation source) {@link SimpleGraphPattern}.
     *
     * @param roleName the {@link String} ID of the {@link NodePattern}
     * @param pattern  the node pattern
     * @param <T>      The node type of the {@link NodePattern}
     * @return a copy of the given {@link NodePattern}
     */
    public <T extends Node> NodePattern<T> addNode(String roleName, NodePattern<T> pattern) {
        NodePattern<T> patternCopy = pattern.deepCopy();
        this.patternRegistry.put(roleName, patternCopy);
        return patternCopy;
    }

    public NodePattern<?> getRepresentingNode() {
        return representingNode;
    }

}
