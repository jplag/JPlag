package de.jplag.java_cpg.transformation.matching.pattern;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

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
     * <p>
     * Constructor for PatternRegistry.
     * </p>
     */
    public PatternRegistry() {
        this.patternByRole = new HashMap<>();
        this.roleByPattern = new HashMap<>();
    }

    /**
     * <p>
     * addAll.
     * </p>
     * @param patternByRoleName a {@link java.util.Map} object
     */
    public void addAll(Map<Role, NodePattern<?>> patternByRoleName) {
        this.patternByRole.putAll(patternByRoleName);
        this.roleByPattern.putAll(patternByRoleName.keySet().stream().collect(Collectors.toMap(patternByRoleName::get, k -> k)));
    }

    /**
     * <p>
     * getPattern.
     * </p>
     * @param nodePatternRole a {@link java.lang.String} object
     * @return a {@link NodePattern} object
     */
    public <T extends Node> NodePattern<T> getPattern(Role nodePatternRole, Class<T> targetClass) {
        NodePattern<?> nodePattern = patternByRole.get(nodePatternRole);
        if (!targetClass.isAssignableFrom(nodePattern.getRootClass())) {
            throw new ClassCastException("Pattern %s is incompatible with target class %s".formatted(nodePatternRole, targetClass));
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
     * <p>
     * Setter for the field <code>representingNode</code>.
     * </p>
     * @param representingNode a {@link NodePattern} object
     */
    public void setRepresentingNode(NodePattern<?> representingNode) {
        this.representingNode = (NodePattern<Node>) representingNode;
    }

    /**
     * <p>
     * Getter for the field <code>representingNode</code>.
     * </p>
     * @return a {@link NodePattern} object
     */
    public NodePattern<Node> getRepresentingNode() {
        return this.representingNode;
    }

    /**
     * <p>
     * createWildcardId.
     * </p>
     * @return a {@link java.lang.String} object
     */
    public Role createWildcardId() {
        return new Role(WILDCARD_PARENT_ID + wildcardCounter++);
    }

    /**
     * <p>
     * containsPattern.
     * </p>
     * @param notePatternRole a {@link java.lang.String} object
     * @return a boolean
     */
    public boolean containsPattern(Role notePatternRole) {
        return patternByRole.containsKey(notePatternRole);
    }
}
