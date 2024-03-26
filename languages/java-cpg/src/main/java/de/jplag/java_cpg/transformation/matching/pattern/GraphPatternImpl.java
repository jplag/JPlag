package de.jplag.java_cpg.transformation.matching.pattern;

import java.util.*;
import java.util.stream.Collectors;

import de.fraunhofer.aisec.cpg.graph.Node;
import de.jplag.java_cpg.transformation.Role;

/**
 * This abstract class contains the method implementations common to all types of concrete {@link GraphPattern}s.
 */
public abstract class GraphPatternImpl implements GraphPattern {
    protected final PatternRegistry patternRegistry;
    protected NodePattern<? extends Node> representingNode;

    /**
     * Constructs a new {@link GraphPatternImpl} from a {@link PatternRegistry}.
     * @param patterns the {@link PatternRegistry} for this graph pattern
     */
    protected GraphPatternImpl(PatternRegistry patterns) {
        representingNode = patterns.getRepresentingNode();
        this.patternRegistry = patterns;
    }

    static List<Match> copy(List<Match> matches) {
        return matches.stream().map(Match::copy).collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Gets the {@link Role} of the given {@link NodePattern}
     * @param pattern the node pattern
     * @return the role
     */
    public <T extends Node> Role getRole(NodePattern<T> pattern) {
        return patternRegistry.getRole(pattern);
    }

    @Override
    public <T extends Node> NodePattern<T> getPattern(Role role, Class<T> tClass) {
        return patternRegistry.getPattern(role, tClass);
    }

    @Override
    public NodePattern<Node> getPattern(Role role) {
        return patternRegistry.getPattern(role, Node.class);
    }

    @Override
    public Collection<Role> getAllRoles() {
        return patternRegistry.allRoles();
    }

    @Override
    public <T extends Node> NodePattern<T> addNode(Role roleName, NodePattern<T> pattern) {
        NodePattern<T> patternCopy = pattern.deepCopy();
        this.patternRegistry.put(roleName, patternCopy);
        return patternCopy;
    }

    /**
     * Gets the <code>representingNode</code> of this {@link GraphPatternImpl}.
     * @return the representative {@link NodePattern}
     */
    public NodePattern<Node> getRepresentingNode() {
        return (NodePattern<Node>) representingNode;
    }

}
