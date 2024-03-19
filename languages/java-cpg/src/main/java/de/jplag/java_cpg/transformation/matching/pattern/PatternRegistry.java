package de.jplag.java_cpg.transformation.matching.pattern;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fraunhofer.aisec.cpg.graph.Node;

/**
 * The {@link de.jplag.java_cpg.transformation.matching.pattern.PatternRegistry} saves the
 * {@link de.jplag.java_cpg.transformation.matching.pattern.NodePattern}s involved in a
 * {@link de.jplag.java_cpg.transformation.GraphTransformation} and their identifiers.
 * @author robin
 * @version $Id: $Id
 */
public class PatternRegistry {
    /** Constant <code>WILDCARD_PARENT_ID="wildcardParent#"</code> */
    public static final String WILDCARD_PARENT_ID = "wildcardParent#";
    private final Map<String, NodePattern<?>> patternById;
    private final Map<NodePattern<?>, String> idByPattern;
    /**
     * A NodePattern that represents the {@link GraphPattern}. If not set, it is the (first) root of the
     * {@link GraphPattern}.
     */
    private NodePattern<?> representingNode;

    private static final Logger logger = LoggerFactory.getLogger(PatternRegistry.class);
    private int wildcardCounter;

    /**
     * <p>
     * Constructor for PatternRegistry.
     * </p>
     */
    public PatternRegistry() {
        this.patternById = new HashMap<>();
        this.idByPattern = new HashMap<>();
        this.wildcardCounter = 0;
    }

    /**
     * <p>
     * addAll.
     * </p>
     * @param patternByRoleName a {@link java.util.Map} object
     */
    public void addAll(Map<String, NodePattern<?>> patternByRoleName) {
        this.patternById.putAll(patternByRoleName);
        this.idByPattern.putAll(patternByRoleName.keySet().stream().collect(Collectors.toMap(patternByRoleName::get, k -> k)));
    }

    /**
     * <p>
     * getPattern.
     * </p>
     * @param nodePatternId a {@link java.lang.String} object
     * @return a {@link de.jplag.java_cpg.transformation.matching.pattern.NodePattern} object
     */
    public NodePattern<?> getPattern(String nodePatternId) {
        return patternById.get(nodePatternId);
    }

    /**
     * <p>
     * getId.
     * </p>
     * @param nodePattern a {@link de.jplag.java_cpg.transformation.matching.pattern.NodePattern} object
     * @return a {@link java.lang.String} object
     */
    public String getId(NodePattern<?> nodePattern) {
        return idByPattern.get(nodePattern);
    }

    /**
     * <p>
     * allIds.
     * </p>
     * @return a {@link java.util.Collection} object
     */
    public Collection<String> allIds() {
        return patternById.keySet();
    }

    /**
     * <p>
     * put.
     * </p>
     * @param id a {@link java.lang.String} object
     * @param pattern a {@link de.jplag.java_cpg.transformation.matching.pattern.NodePattern} object
     * @param <T> a T class
     */
    public <T extends Node> void put(String id, NodePattern<T> pattern) {
        if (patternById.containsKey(id)) {
            logger.warn("A NodePattern with the id '%s' is already present in the PatternRegistry");
        }
        this.patternById.put(id, pattern);
        this.idByPattern.put(pattern, id);
    }

    /**
     * <p>
     * Setter for the field <code>representingNode</code>.
     * </p>
     * @param representingNode a {@link de.jplag.java_cpg.transformation.matching.pattern.NodePattern} object
     */
    public void setRepresentingNode(NodePattern<?> representingNode) {
        this.representingNode = representingNode;
    }

    /**
     * <p>
     * Getter for the field <code>representingNode</code>.
     * </p>
     * @return a {@link de.jplag.java_cpg.transformation.matching.pattern.NodePattern} object
     */
    public NodePattern<?> getRepresentingNode() {
        return this.representingNode;
    }

    /**
     * <p>
     * createWildcardId.
     * </p>
     * @return a {@link java.lang.String} object
     */
    public String createWildcardId() {
        return WILDCARD_PARENT_ID + wildcardCounter++;
    }

    /**
     * <p>
     * containsPattern.
     * </p>
     * @param notePatternId a {@link java.lang.String} object
     * @return a boolean
     */
    public boolean containsPattern(String notePatternId) {
        return patternById.containsKey(notePatternId);
    }
}
