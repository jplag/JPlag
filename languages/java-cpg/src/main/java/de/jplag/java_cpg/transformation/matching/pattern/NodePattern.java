package de.jplag.java_cpg.transformation.matching.pattern;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.jetbrains.annotations.NotNull;

import de.fraunhofer.aisec.cpg.graph.Node;
import de.jplag.java_cpg.transformation.GraphTransformation.Builder.RelationComparisonFunction;
import de.jplag.java_cpg.transformation.Role;
import de.jplag.java_cpg.transformation.matching.edges.CpgMultiEdge;
import de.jplag.java_cpg.transformation.matching.pattern.relation.ForAllRelatedNode;
import de.jplag.java_cpg.transformation.matching.pattern.relation.Relation;

/**
 * This class represents a Graph pattern on node level. It can be used to match against a concrete node and its related
 * nodes.
 * @param <T> the type of node represented.
 */
public interface NodePattern<T extends Node> {

    /**
     * Creates a new {@link NodePattern} for the given {@link Node} type.
     * @param tClass the node class
     * @param <T> the node type
     * @return the node pattern
     */
    @NotNull
    static <T extends Node> NodePattern<T> forNodeType(Class<T> tClass) {
        return new NodePatternImpl<>(tClass);
    }

    /**
     * Adds a for-all relation to this pattern
     * @param related the pattern to match against all related nodes
     * @param edge the edge that points to the related nodes
     * @param <R> the related node type
     */
    <R extends Node> void addForAllRelated(NodePattern<? extends R> related, CpgMultiEdge<? super T, R> edge);

    /**
     * Adds the given {@link MatchProperty} to this {@link NodePattern}.
     * @param property the match property
     */
    void addMatchProperty(MatchProperty<? super T> property);

    /**
     * Adds a property to the pattern that has to hold for this pattern to match.
     * @param property the property
     */
    void addProperty(Predicate<? super T> property);

    <R extends Node> void addRelation(Relation<? super T, R, ?> trRelatedOneToNNode);

    /**
     * Creates a copy of this {@link NodePattern} and all related nodes and properties.
     * @return the copied {@link NodePattern}
     */
    NodePattern<T> deepCopy();

    /**
     * Gets the list of node classes of potential candidates for this node pattern.
     * @return the candidate classes
     */
    List<Class<? extends T>> getCandidateClasses();

    Role getRole();

    void setRole(Role role);

    /**
     * Gets a {@link Class} object for the indicated {@link Node} class.
     * @return the class object
     */
    Class<T> getRootClass();

    void handleRelationships(NodePattern<T> target, RelationComparisonFunction comparator);

    /**
     * Set a flag that indicates that no graph operations should be created beyond this node pattern.
     */
    void markStopRecursion();

    /**
     * Checks whether the given concrete node matches this pattern.
     * @param node a candidate node
     * @param matches matches of pattern nodes to sourceGraph nodes
     */
    void recursiveMatch(Node node, List<Match> matches);

    /**
     * Determines whether the STOP_RECURSION flag has been set for this node pattern.
     * @return true if STOP_RECURSION is set
     */
    boolean shouldStopRecursion();

    /**
     * Standard implementation of the {@link NodePattern}.
     * @param <T> The {@link Node} type of a target node.
     */
    class NodePatternImpl<T extends Node> implements NodePattern<T> {

        private static long candidateCounter = 0;
        /**
         * The class object of the concrete type of the represented node.
         */
        protected final Class<T> clazz;
        /**
         * List of properties that a matching node must fulfil.
         */
        private final List<Predicate<? super T>> properties;
        /**
         * List of properties that a match must fulfil.
         */
        private final List<MatchProperty<? super T>> matchProperties;

        private final List<Relation<? super T, ?, ?>> relations;

        /**
         * List of annotations.
         */
        private final EnumSet<NodeAnnotation> annotations;
        private Role role;

        public NodePatternImpl(Class<T> clazz) {
            this.clazz = clazz;
            this.properties = new ArrayList<>();
            this.matchProperties = new ArrayList<>();
            this.relations = new ArrayList<>();
            this.annotations = EnumSet.noneOf(NodeAnnotation.class);
        }

        private static void incrementCandidateCounter() {
            candidateCounter++;
        }

        public static long getCounter() {
            return candidateCounter;
        }

        public static void resetCounter() {
            candidateCounter = 0;
        }

        @Override
        public <R extends Node> void addForAllRelated(NodePattern<? extends R> related, CpgMultiEdge<? super T, R> edge) {
            // Could be refactored to a map by the edge type instead of a list
            relations.add(new ForAllRelatedNode<>(related, edge));
        }

        @Override
        public void addMatchProperty(MatchProperty<? super T> property) {
            matchProperties.add(property);
        }

        @Override
        public void addProperty(Predicate<? super T> property) {
            properties.add(property);
        }

        @Override
        public <R extends Node> void addRelation(Relation<? super T, R, ?> relation) {
            relations.add(relation);
        }

        @Override
        public NodePattern<T> deepCopy() {
            NodePatternImpl<T> copy = new NodePatternImpl<>(this.clazz);
            copy.properties.addAll(this.properties);
            copy.relations.addAll(this.relations);
            copy.matchProperties.addAll(this.matchProperties);
            return copy;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;

            NodePatternImpl<?> that = (NodePatternImpl<?>) o;

            if (!clazz.equals(that.clazz))
                return false;
            if (!properties.equals(that.properties))
                return false;
            if (!Objects.equals(matchProperties, that.matchProperties))
                return false;
            if (!Objects.equals(relations, that.relations))
                return false;
            return annotations.equals(that.annotations);
        }

        @Override
        public List<Class<? extends T>> getCandidateClasses() {
            return List.of(getRootClass());
        }

        public Role getRole() {
            return role;
        }

        @Override
        public void setRole(Role role) {
            this.role = role;
        }

        @Override
        public Class<T> getRootClass() {
            return clazz;
        }

        @Override
        public void handleRelationships(NodePattern<T> target, RelationComparisonFunction comparator) {
            List<Relation<? super T, ?, ?>> unprocessedTargetRelated = ((NodePatternImpl<T>) target).relations;
            boolean multipleCandidates = unprocessedTargetRelated.size() > 1;

            for (Relation<? super T, ?, ?> sourceRelated : relations) {
                if (sourceRelated.getEdge().isAnalytic()) {
                    continue;
                }

                Optional<Relation<? super T, ?, ?>> matchingTargetRelated = unprocessedTargetRelated.stream()
                        .filter(targetRelated -> sourceRelated.isEquivalentTo(targetRelated, multipleCandidates)).findFirst();

                matchingTargetRelated.ifPresentOrElse(targetRelated -> {
                    unprocessedTargetRelated.remove(targetRelated);
                    comparator.castAndCompare(sourceRelated, targetRelated, this);
                }, () ->
                // no target candidate -> remove
                comparator.castAndCompare(sourceRelated, sourceRelated, this));
            }

            for (Relation<? super T, ?, ?> targetRelated : unprocessedTargetRelated) {
                // no source candidate -> insert
                comparator.castAndCompare(targetRelated, targetRelated, this);
            }
        }

        @Override
        public int hashCode() {
            // hashCode must not use list fields, as their hashCode changes with their content
            // this would lead to HashMaps failing to recognize the same NodePattern.
            int result = clazz != null ? clazz.hashCode() : 0;
            result = 31 * result + super.hashCode();
            return result;
        }

        /**
         * Checks the local properties of the {@link NodePattern} against the concrete {@link Node}.
         * @param node the node
         * @return true if the node has the corresponding type and properties
         */
        protected boolean localMatch(Node node) {
            boolean typeMismatch = !clazz.isAssignableFrom(node.getClass());
            if (typeMismatch) {
                return false;
            }

            T tNode = clazz.cast(node);

            boolean propertyMismatch = properties.stream().anyMatch(p -> !p.test(tNode));
            return !propertyMismatch;
        }

        @Override
        public void markStopRecursion() {
            annotations.add(NodeAnnotation.STOP_RECURSION);
        }

        @Override
        public void recursiveMatch(Node node, List<Match> matches) {
            // We may have encountered this pattern before. If we have not also arrived at the same node, it's a mismatch.
            matches.removeIf(match -> match.contains(this) && !match.get(this).equals(node));
            var splitList = matches.stream().collect(Collectors.groupingBy(match -> match.contains(this)));
            var finishedMatches = splitList.getOrDefault(true, new ArrayList<>());

            // unencountered only
            var openMatches = splitList.getOrDefault(false, new ArrayList<>());
            if (openMatches.isEmpty())
                return;

            incrementCandidateCounter();

            // check node properties
            boolean localPropertiesMismatch = !localMatch(node);
            if (localPropertiesMismatch) {
                matches.clear();
                return;
            }

            // if !localPropertiesMismatch, then this cast is valid
            T tNode = (T) node;
            openMatches.forEach(match -> match.register(this, tNode));

            // all relations must match in a specific way according to their relation type
            relations.forEach(relation -> relation.recursiveMatch(this, tNode, openMatches));

            openMatches.removeIf(match -> !matchProperties.isEmpty() && matchProperties.stream().anyMatch(mp -> !mp.test(tNode, match)));

            matches.clear();
            matches.addAll(finishedMatches);
            matches.addAll(openMatches);
        }

        @Override
        public boolean shouldStopRecursion() {
            return annotations.contains(NodeAnnotation.STOP_RECURSION);
        }

        @Override
        public String toString() {
            return "NodePattern{%s \"%s\"}".formatted(clazz.getSimpleName(), role.name());
        }

        private enum NodeAnnotation {
            DISCONNECT_AST,
            STOP_RECURSION,
            REPRESENTING_NODE,
            DISCONNECT_EOG
        }

    }

}
