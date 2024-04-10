package de.jplag.java_cpg.transformation.matching.pattern;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fraunhofer.aisec.cpg.graph.Node;
import de.jplag.java_cpg.transformation.GraphTransformation;
import de.jplag.java_cpg.transformation.Role;

/**
 * The {@link PatternRegistry} saves the {@link NodePattern}s involved in a {@link GraphTransformation} and their
 * identifiers.
 */
public class PatternRegistry {
    private static final String WILDCARD_PARENT_ID = "wildcardParent#";
    private final Map<Role, NodePattern<?>> patternByRole;
    private final Map<NodePattern<?>, Role> roleByPattern;

    /**
     * A NodePattern that represents the {@link GraphPattern}. If not set, it is the (first) root of the
     * {@link GraphPattern}.
     */
    private NodePattern<Node> representingNode;

    private static final Logger logger = LoggerFactory.getLogger(PatternRegistry.class);
    private int wildcardCounter;

    /**
     * Creates a new {@link PatternRegistry}.
     */
    public PatternRegistry() {
        this.patternByRole = new HashMap<>();
        this.roleByPattern = new HashMap<>();
    }

    /**
     * Gets the {@link NodePattern} with the given {@link Role}, cast to the given {@link Node} class.
     * @param role the role
     * @return the node pattern associated to the role
     */
    public <T extends Node> NodePattern<T> getPattern(Role role, Class<T> targetClass) {
        NodePattern<?> nodePattern = patternByRole.get(role);
        if (!targetClass.isAssignableFrom(nodePattern.getRootClass())) {
            throw new ClassCastException("Pattern %s is incompatible with target class %s".formatted(role, targetClass));
        }
        @SuppressWarnings("unchecked")
        NodePattern<T> castNodePattern = (NodePattern<T>) nodePattern;
        return castNodePattern;
    }

    /**
     * Gets the {@link Role} of a {@link NodePattern}.
     * @param nodePattern the node pattern
     * @return the role
     */
    public Role getRole(NodePattern<?> nodePattern) {
        return roleByPattern.get(nodePattern);
    }

    /**
     * Gets a list of all registered roles.
     * @return list of roles
     */
    public Collection<Role> allRoles() {
        return patternByRole.keySet();
    }

    /**
     * Registers the given {@link NodePattern} with its {@link Role}.
     * @param role the role
     * @param pattern the node pattern
     */
    public void put(Role role, NodePattern<?> pattern) {
        if (patternByRole.containsKey(role)) {
            logger.warn("A NodePattern with the role '{}' is already present in the PatternRegistry", role.name());
        }
        pattern.setRole(role);
        this.patternByRole.put(role, pattern);
        this.roleByPattern.put(pattern, role);
    }

    /**
     * Sets the representing node of the associated graph pattern.
     * @param representingNode the representing node
     */
    public void setRepresentingNode(NodePattern<?> representingNode) {
        this.representingNode = (NodePattern<Node>) representingNode;
    }

    /**
     * Gets the {@link NodePattern} of the associated {@link GraphPattern} that is marked representative.
     * @return the representative node pattern
     */
    public NodePattern<Node> getRepresentingNode() {
        return this.representingNode;
    }

    /**
     * Creates a new Role for a wildcard parent pattern.
     * @return the role
     */
    public Role createWildcardRole() {
        return new Role(WILDCARD_PARENT_ID + wildcardCounter++);
    }

    /**
     * Determines whether a pattern with the given role is present in this {@link PatternRegistry}.
     * @param role the role
     * @return true iff the pattern is in the registry
     */
    public boolean containsPattern(Role role) {
        return patternByRole.containsKey(role);
    }
}
