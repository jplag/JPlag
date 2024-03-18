package de.jplag.java_cpg.transformation.matching.pattern;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import de.jplag.java_cpg.transformation.GraphTransformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fraunhofer.aisec.cpg.graph.Node;

/**
 * The {@link PatternRegistry} saves the {@link NodePattern}s involved in a {@link GraphTransformation} and their identifiers.
 */
class PatternRegistry {
    public static final String WILDCARD_PARENT_ID = "wildcardParent#";
    private final Map<String, NodePattern<?>> patternById;
    private final Map<NodePattern<?>, String> idByPattern;
    /**
     *  A NodePattern that represents the {@link GraphPattern}. If not set, it is the (first) root of the {@link GraphPattern}.
     */
    private NodePattern<?> representingNode;

    private static final Logger logger = LoggerFactory.getLogger(PatternRegistry.class);
    private int wildcardCounter;

    public PatternRegistry() {
        this.patternById = new HashMap<>();
        this.idByPattern = new HashMap<>();
        this.wildcardCounter = 0;
    }

    public void addAll(Map<String, NodePattern<?>> patternByRoleName) {
        this.patternById.putAll(patternByRoleName);
        this.idByPattern.putAll(patternByRoleName.keySet().stream().collect(Collectors.toMap(patternByRoleName::get, k -> k)));
    }

    public NodePattern<?> getPattern(String nodePatternId) {
        return patternById.get(nodePatternId);
    }

    public String getId(NodePattern<?> nodePattern) {
        return idByPattern.get(nodePattern);
    }

    public Collection<String> allIds() {
        return patternById.keySet();
    }

    public <T extends Node> void put(String id, NodePattern<T> pattern) {
        if (patternById.containsKey(id)) {
            logger.warn("A NodePattern with the id '%s' is already present in the PatternRegistry");
        }
        this.patternById.put(id, pattern);
        this.idByPattern.put(pattern, id);
    }

    public void setRepresentingNode(NodePattern<?> representingNode) {
        this.representingNode = representingNode;
    }

    public NodePattern<?> getRepresentingNode() {
        return this.representingNode;
    }

    public String createWildcardId() {
        return WILDCARD_PARENT_ID + wildcardCounter++;
    }

    public boolean containsPattern(String notePatternId) {
        return patternById.containsKey(notePatternId);
    }
}