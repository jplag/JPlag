package de.jplag.java_cpg.transformation.matching.pattern;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fraunhofer.aisec.cpg.graph.Node;
import de.jplag.java_cpg.transformation.Casting;
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
     * @param targetClass the target node pattern class
     * @param <T> the target node pattern type
     * @return the node pattern associated to the role
     * @throws ClassCastException if the pattern assigned to the role is incompatible with the given target class
     */
    public <T extends Node> NodePattern<T> getPattern(Role role, Class<T> targetClass) throws ClassCastException {
        NodePattern<?> nodePattern = patternByRole.get(role);
        if (!targetClass.isAssignableFrom(nodePattern.getRootClass())) {
            throw new ClassCastException("Pattern %s is incompatible with target class %s".formatted(role, targetClass));
        }
        return Casting.castNodePattern(nodePattern);
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
            logger.warn("A NodePattern with the role '{}' is already present in the PatternRegistry", role.getName());
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
        this.representingNode = Casting.castNodePattern(representingNode);
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
        return new WildcardParentRole(wildcardCounter++);
    }

    /**
     * Determines whether a pattern with the given role is present in this {@link PatternRegistry}.
     * @param role the role
     * @return true iff the pattern is in the registry
     */
    public boolean containsPattern(Role role) {
        return patternByRole.containsKey(role);
    }

    record WildcardParentRole(int id) implements Role {

        @Override
        public String getName() {
            return WILDCARD_PARENT_ID + id;
        }
    }

}
