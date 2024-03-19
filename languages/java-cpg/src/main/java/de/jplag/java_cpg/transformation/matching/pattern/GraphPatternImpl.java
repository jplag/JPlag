package de.jplag.java_cpg.transformation.matching.pattern;

import java.util.*;
import java.util.stream.Collectors;

import de.fraunhofer.aisec.cpg.graph.Node;

/**
 * This abstract class contains the method implementations common to all types of concrete {@link GraphPattern}s.
 */
public abstract class GraphPatternImpl implements GraphPattern {
    protected final PatternRegistry patternRegistry;
    protected NodePattern<?> representingNode;

    /**
     * Constructs a new {@link GraphPatternImpl} from a {@link PatternRegistry}.
     * @param patterns a {@link PatternRegistry} object
     */
    public GraphPatternImpl(PatternRegistry patterns) {
        representingNode = patterns.getRepresentingNode();
        this.patternRegistry = patterns;
    }

    static List<Match> copy(List<Match> matches) {
        return matches.stream().map(Match::copy).collect(Collectors.toList());
    }

    /**
     * Gets the {@link String} ID of the given {@link NodePattern}
     * @param pattern the node pattern
     * @return the ID
     */
    public String getId(NodePattern<?> pattern) {
        return patternRegistry.getId(pattern);
    }

    public NodePattern<?> getPattern(String id) {
        return patternRegistry.getPattern(id);
    }

    public Collection<String> getAllIds() {
        return patternRegistry.allIds();
    }

    public <T extends Node> NodePattern<T> addNode(String roleName, NodePattern<T> pattern) {
        NodePattern<T> patternCopy = pattern.deepCopy();
        this.patternRegistry.put(roleName, patternCopy);
        return patternCopy;
    }

    /**
     * Gets the <code>representingNode</code> of this {@link GraphPatternImpl}.
     * @return the representative {@link de.jplag.java_cpg.transformation.matching.pattern.NodePattern}
     */
    public NodePattern<?> getRepresentingNode() {
        return representingNode;
    }

}
