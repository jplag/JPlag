package de.jplag.java_cpg.transformation.matching.pattern;

import de.fraunhofer.aisec.cpg.graph.Node;
import de.jplag.java_cpg.transformation.GraphTransformation;
import de.jplag.java_cpg.transformation.matching.edges.CpgEdge;
import de.jplag.java_cpg.transformation.matching.edges.CpgMultiEdge;
import de.jplag.java_cpg.transformation.matching.edges.CpgNthEdge;

import java.util.*;
import java.util.stream.Collectors;

/**
 * A {@link GraphPattern} describes the occurrence and relation of {@link Node}s in a Graph and their properties.
 * @param <T> the root {@link Node} type of the graph pattern
 */
public class GraphPattern<T extends Node> {
    protected NodePattern<T> root;
    protected final Map<String, NodePattern<?>> patternByRoleName;
    protected final Map<NodePattern<?>, String> roleNameByPattern;
    protected Match<T> currentMatch;

    /**
     * Creates a new {@link GraphPattern} with the given root {@link NodePattern}.
     * @param root the root {@link NodePattern}
     * @param patternByRoleName a map of {@link String} IDs to {@link NodePattern}s.
     */
    public GraphPattern(NodePattern<T> root, Map<String, NodePattern<?>> patternByRoleName) {
        this.root = root;
        this.patternByRoleName = patternByRoleName;
        this.roleNameByPattern = invert(patternByRoleName);
    }

    private static Map<NodePattern<?>, String> invert(Map<String, NodePattern<?>> mapping) {
        return mapping.entrySet().stream().collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));
    }

    /**
     * Gets the root {@link NodePattern} of the {@link GraphPattern}.
     * @return the root
     */
    public NodePattern<T> getRoot() {
        return root;
    }

    /**
     * Gets the {@link NodePattern} where the {@link GraphTransformation} algorithm should step in.
     * @return the transformation start {@link NodePattern}
     */
    public NodePattern<?> getTransformationStart() {
        return root;
    }

    /**
     * Gets the {@link String} ID of the given {@link NodePattern}
     * @param source the node pattern
     * @return the ID
     */
    public String getRole(NodePattern<?> source) {
        return roleNameByPattern.get(source);
    }

    /**
     * Gets the {@link NodePattern} corresponding to the given {@link String} ID.
     * @param roleName the ID
     * @return the node pattern
     */
    public NodePattern<?> getPattern(String roleName) {
        return patternByRoleName.get(roleName);
    }

    /**
     * Checks this {@link GraphPattern} against the given concrete {@link Node} for {@link Match}es.
     * @param rootCandidate the possible root {@link Node} of {@link Match}es
     * @return the list of {@link Match}es found
     */
    public List<Match<T>> recursiveMatch(Node rootCandidate) {
        List<Match<T>> matches = new ArrayList<>();
        matches.add(new Match<>(this));
        this.root.recursiveMatch(rootCandidate, matches, null);
        return matches;
    }

    public Collection<? extends String> getAllRoles() {
        return patternByRoleName.keySet();
    }

    /**
     * Adds a copy of the given (transformation target) {@link NodePattern} to this (transformation source) {@link GraphPattern}.
     * @param roleName the {@link String} ID of the {@link NodePattern}
     * @param pattern the node pattern
     * @return a copy of the given {@link NodePattern}
     * @param <T> The node type of the {@link NodePattern}
     */
    public <T extends Node> NodePattern<T> addNode(String roleName, NodePattern<T> pattern) {
        NodePattern<T> patternCopy = pattern.deepCopy();
        this.patternByRoleName.put(roleName, patternCopy);
        this.roleNameByPattern.put(patternCopy, roleName);
        return patternCopy;
    }

    /**
     * Gets a list of {@link Class} objects indicating the class types of {@link Node}s that may match with this {@link GraphPattern}.
     * @return the list of classes
     */
    public List<Class<? extends Node>> getCandidateNodeClasses() {
        return List.of(root.getRootClass());
    }

    /**
     * Sets the root {@link NodePattern} of this {@link GraphPattern}.
     * @param rootPattern the root
     */
    protected void setRoot(NodePattern<T> rootPattern) {
        this.root = rootPattern;
    }

    /**
     * Loads the given {@link Match} for transformation.
     * @param match the match
     */
    public void loadMatch(Match<T> match) {
        this.currentMatch = match;
    }

    /**
     * A {@link Match} stores the mapping between a {@link GraphPattern} and {@link Node}s matching the pattern.
     * Especially, a {@link WildcardGraphPattern.ParentNodePattern}'s match in the sourceGraph can be saved.
     * @param <T> The node type of the root (possibly the target of a {@link WildcardGraphPattern.Edge}
     */
    public static class Match<T extends Node> {

        private final Map<NodePattern<? extends Node>, Node> patternToNode;
        private final GraphPattern<T> pattern;
        private WildcardMatch<?,? super T> wildcardMatch;
        private final Map<CpgMultiEdge<?, ?>.Any1ofNEdge, CpgEdge<?, ?>> edgeMap;

        /**
         * Creates a new {@link Match}.
         * @param pattern the {@link GraphPattern} of which this is a {@link Match}.
         */
        public Match(GraphPattern<T> pattern) {
            this.pattern = pattern;
            patternToNode = new HashMap<>();
            edgeMap = new HashMap<>();
        }

        /**
         * Adds a matching concrete {@link Node} to this {@link Match}.
         * @param pattern the {@link NodePattern} matching the node
         * @param node the node
         * @param <N> the concrete {@link Node} type
         */
        public <N extends Node> void register(NodePattern<? super N> pattern, N node) {
            patternToNode.put(pattern, node);
        }

        /**
         * Saves the concrete parent {@link Node} and edge corresponding to a {@link WildcardGraphPattern}.
         * @param parent the parent
         * @param edge the edge
         * @param <S> the concrete node type of the parent
         */
        public <S extends Node> void resolveWildcard(S parent, CpgEdge<S, ? super T> edge) {
            NodePattern<S> concreteRoot = NodePattern.forNodeType(edge.getFromClass());
            concreteRoot.addRelatedNodePattern(pattern.getRoot(), edge);

            Map<String, NodePattern<?>> patternByRoleName = new HashMap<>(pattern.patternByRoleName);
            patternByRoleName.put(WildcardGraphPattern.WILDCARD_PARENT_ID, concreteRoot);

            GraphPattern<S> concreteGraphPattern = new GraphPattern<>(concreteRoot, patternByRoleName);
            this.wildcardMatch = new WildcardMatch<>(concreteGraphPattern, parent, concreteRoot, edge);

            this.patternToNode.put(concreteRoot, parent);
        }

        /**
         * Gets the count of {@link Node}s that are part of this {@link Match}.
         * @return the number of nodes
         */
        public int getSize() {
            return patternToNode.size();
        }

        /**
         * Checks if the given {@link NodePattern} is contained in this {@link Match}.
         * @param pattern the pattern
         * @return true if the pattern is contained in this {@link Match}
         */
        public boolean contains(NodePattern<?> pattern) {
            return patternToNode.containsKey(pattern);
        }

        /**
         * Gets the concrete {@link Node} corresponding to the given {@link NodePattern}.
         * @param pattern the pattern
         * @return the concrete {@link Node}
         * @param <T> the node type
         */
        public <T extends Node> T get(NodePattern<T> pattern) {
            return (T) patternToNode.get(pattern);
        }

        /**
         * Gets the concrete wildcard edge of this {@link Match}.
         * @return the wildcard edge
         */
        public CpgEdge<?, ? super T> getWildcardEdge() {
            return wildcardMatch.edge;
        }

        /**
         * Gets the concrete wildcard parent of this {@link Match}.
         * @return the wildcard parent
         */
        public NodePattern<?> getWildcardParent() {
            return wildcardMatch.parentPattern;
        }

        /**
         * Gets the current wildcard match.
         * @return the wildcard match
         */
        public WildcardMatch<?, ? super T> getWildcardMatch() {
            return wildcardMatch;
        }

        public <S extends Node,T extends Node> void resolveAny1ofNEdge(CpgMultiEdge<S,T>.Any1ofNEdge any1ofNEdge, int index) {
            this.edgeMap.put(any1ofNEdge, new CpgNthEdge<S,T>(any1ofNEdge.getMultiEdge(), index));
        }

        /**
         * Creates a copy of this {@link Match} in its current state.
         * @return the copy
         */
        public Match<T> copy() {
            Match<T> copy = new Match<>(pattern);
            copy.patternToNode.putAll(patternToNode);
            copy.wildcardMatch = wildcardMatch;
            return copy;
        }

        public <S extends Node, T extends Node> CpgEdge<S, T> getEdge(CpgMultiEdge<S,T>.Any1ofNEdge any1OfNEdge) {
            // key-value pairs of this map are type-compatible
            return (CpgEdge<S, T>) this.edgeMap.get(any1OfNEdge);
        }

        /**
         * Saves the data related to a concrete occurrence of a {@link WildcardGraphPattern}.
         * @param concretePattern A concrete {@link GraphPattern} for use in a transformation
         * @param parent the parent
         * @param parentPattern A concrete {@link NodePattern} for the parent
         * @param edge the edge
         * @param <S> the concrete type of the parent, specified by the edge
         * @param <T> the concrete type of the child, specified by the edge
         */
        /*package-private */ public record WildcardMatch<S extends Node, T extends Node>(
            GraphPattern<S> concretePattern,
            S parent,
            NodePattern<S> parentPattern,
            CpgEdge<S, ? super T> edge) {
        }
    }
}
