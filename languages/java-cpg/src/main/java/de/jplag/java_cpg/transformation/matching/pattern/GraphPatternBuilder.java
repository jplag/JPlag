package de.jplag.java_cpg.transformation.matching.pattern;

import java.util.*;
import java.util.function.Predicate;

import org.jetbrains.annotations.NotNull;

import de.fraunhofer.aisec.cpg.graph.Node;
import de.jplag.java_cpg.transformation.matching.edges.CpgEdge;
import de.jplag.java_cpg.transformation.matching.edges.CpgMultiEdge;
import de.jplag.java_cpg.transformation.matching.edges.CpgPropertyEdge;
import de.jplag.java_cpg.transformation.matching.pattern.NodePattern.ForAllRelatedNode;

/**
 * Abstract builder class for {@link GraphPattern}s, offering convenience methods.
 */
public abstract class GraphPatternBuilder {
    /**
     * A registry for {@link NodePattern}s, which may be re-referenced after their creation.
     */
    private final PatternRegistry patterns;

    /**
     * Creates a new {@link GraphPatternBuilder}.
     */
    protected GraphPatternBuilder() {
        this.patterns = new PatternRegistry();
    }

    /**
     * Creates a {@link NodePattern} of the given {@link Node} {@link Class}.
     * @param tClass the class of the represented {@link Node}
     * @param id an identifier for this {@link NodePattern}
     * @param patterns the pattern registry as in-out parameter
     * @param modifications the modifications to be applied to the new {@link NodePattern}
     * @param <T> the {@link Node} type of the represented {@link Node}
     * @return the node pattern
     */
    @NotNull
    private static <T extends Node> NodePattern<T> createNodePattern(Class<T> tClass, String id, PatternRegistry patterns,
            List<PatternModification<? super T>> modifications) {
        NodePattern<T> related = NodePattern.forNodeType(tClass);
        patterns.put(id, related);
        modifications.forEach(m -> m.apply(related, patterns));
        return related;
    }

    /**
     * Creates a {@link PatternModification} that adds the {@link MatchProperty} to a {@link NodePattern} that a specific
     * attribute of a matching {@link Node} must be equal to the same attribute of another {@link Node}.
     * @param propertyEdge the property edge
     * @param otherId the identifier of the other {@link NodePattern}
     * @param <S> the {@link Node} type
     * @param <P> the attribute type
     * @return the {@link PatternModification}
     */
    public static <S extends Node, P> PatternModification<S> equalAttributes(CpgPropertyEdge<S, P> propertyEdge, String otherId) {
        return new AddEqualAttributes<>(propertyEdge, otherId);
    }

    /**
     * Creates a {@link PatternModification} that adds the {@link MatchProperty} to a {@link NodePattern} that the assigned
     * value is unchanged between the evaluation of the two given {@link Node}s.
     * @param startId the identifier of the starting node id
     * @param endId the identifier of the end node id
     * @param <S> the type of
     * @return the {@link PatternModification}
     */
    public static <S extends Node> PatternModification<S> assignedValueStableBetween(String startId, String endId) {
        return new AddAssignedValueStableBetween<>(startId, endId);
    }

    public static <S extends Node> PatternModification<S> notEqualTo(String otherId) {
        return new AddNotEqualTo<>(otherId);
    }

    /**
     * Builds a {@link SimpleGraphPattern}. The specifics of the structure of the SimpleGraphPattern are defined in the
     * concrete subclasses of {@link GraphPatternBuilder}.
     * @return the graph pattern
     */
    public abstract GraphPattern build();

    /**
     * Convenience method to create a {@link NodePattern}.
     * @param tClass the {@link Node} class of the pattern
     * @param id the ID for the pattern
     * @param modifications a list of modifications to the {@link NodePattern}
     * @param <T> the node type
     * @return the node pattern
     */
    @SafeVarargs
    public final <T extends Node> SimpleGraphPattern<T> create(Class<T> tClass, String id, PatternModification<? super T>... modifications) {
        NodePattern<T> pattern;
        if (patterns.containsPattern(id)) {
            pattern = (NodePattern<T>) patterns.getPattern(id);
        } else {
            pattern = NodePattern.forNodeType(tClass);
            patterns.put(id, pattern);
        }
        Arrays.asList(modifications).forEach(m -> m.apply(pattern, patterns));
        return new SimpleGraphPattern<>(pattern, patterns);
    }

    /**
     * Creates an empty {@link WildcardGraphPattern}. It can be used as a target pattern for a
     * {@link de.jplag.java_cpg.transformation.GraphTransformation} where a {@link Node} shall be removed.
     * @return the wildcard graph pattern
     */
    protected SimpleGraphPattern<Node> emptyWildcardParent() {
        return new WildcardGraphPattern<>(Node.class, null, patterns);
    }

    /**
     * Creates a {@link PatternModification} that adds a {@link ForAllRelatedNode} to a {@link NodePattern}.
     * @param multiEdge The multi edge
     * @param cClass the concrete class object of the for-all related nodes
     * @param id the identifier for the related nodes
     * @param modifications the modifications for the created node pattern
     * @return the pattern modification
     * @param <T> the type of source node
     * @param <R> the type of related node, defined by the edge
     * @param <C> the concrete type of related node
     */
    @SafeVarargs
    public final <T extends Node, R extends Node, C extends R> PatternModification<T> forAllRelated(CpgMultiEdge<T, R> multiEdge, Class<C> cClass,
            String id, PatternModification<? super C>... modifications) {
        return new AddForAllRelated<>(multiEdge, cClass, id, Arrays.asList(modifications));
    }

    /**
     * Creates a {@link MultiGraphPattern} from the given {@link SimpleGraphPattern}s.
     * @param subgraphs the subgraph patterns
     * @return the multi graph pattern
     */
    public final MultiGraphPattern multiRoot(SimpleGraphPattern<?>... subgraphs) {
        return new MultiGraphPattern(List.of(subgraphs), patterns);
    }

    /**
     * Creates a {@link PatternListModification} that adds a {@link NodePattern} to a {@link NodeListPattern}.
     * @param cClass the concrete class of the added NodePattern
     * @param id the identifier for the node pattern
     * @param modifications the modifications to be applied to the node
     * @return the pattern list modification
     * @param <T> the node type of the added node as defined by the edge
     * @param <C> the concrete type of the added node
     */
    @SafeVarargs
    public final <T extends Node, C extends T> PatternListModification<T> node(Class<C> cClass, String id, PatternModification<C>... modifications) {
        return new AddNode<>(cClass, id, Arrays.asList(modifications));
    }

    /**
     * Creates a {@link PatternModification} to add a property to a {@link NodePattern}.
     * @param property the predicate establishing the property
     * @param <T> the target {@link Node} type
     * @return the pattern modification
     */
    public final <T extends Node> PatternModification<T> property(Predicate<T> property) {
        return new AddProperty<>(property);
    }

    /**
     * Creates a {@link PatternModification} that adds a new node pattern that is related to the reference node pattern via
     * a 1:n relation.
     * @param edge the edge type connecting the node patterns
     * @param rClass the class object indicating the specified target's class type
     * @param id the name of the new related node pattern
     * @param modifications a list of modifications targeting the new node pattern
     * @param <T> the type of the source node pattern
     * @param <R> the type of the relation target, defined by the edge
     * @param <C> the concrete type of the related node pattern
     * @return the pattern modification object
     */
    @SafeVarargs
    public final <T extends Node, R extends Node, C extends R> PatternModification<T> related(CpgEdge<T, R> edge, Class<C> rClass, String id,
            PatternModification<? super C>... modifications) {
        return new AddRelatedNode<>(edge, rClass, id, Arrays.asList(modifications));
    }

    /**
     * Creates a {@link PatternModification} that adds a new node pattern that is related to the reference node pattern via
     * a 1:n relation.
     * @param edge the edge type connecting the node patterns
     * @param rClass the class object indicating the specified target's class type
     * @param id the name of the new related node pattern
     * @param modifications a list of modifications targeting the new node pattern
     * @param <T> the type of the source node pattern
     * @param <R> the type of the relation target, defined by the edge
     * @param <C> the concrete type of the related node pattern
     * @return the pattern modification object
     */
    @SafeVarargs
    public final <T extends Node, R extends Node, C extends R> PatternModification<T> related1ToN(CpgMultiEdge<T, R> edge, Class<C> rClass, String id,
            PatternModification<? super C>... modifications) {
        return new AddRelated1ToNNode<>(edge, rClass, id, Arrays.asList(modifications));
    }

    /**
     * Creates a {@link PatternModification} to add a sequence of related node pattern to a {@link NodePattern}.
     * @param edge the multi-edge establishing the relation
     * @param cClass the (super)type class of the related nodes
     * @param <S> the source {@link Node} type
     * @param <T> the target {@link Node} type
     * @return the pattern modification
     */
    @SafeVarargs
    public final <S extends Node, T extends Node, C extends T> PatternModification<S> related1ToNSequence(CpgMultiEdge<S, T> edge, Class<C> cClass,
            PatternListModification<C>... modifications) {
        return new AddRelated1ToNSequence<>(edge, cClass, Arrays.asList(modifications));
    }

    /**
     * Creates a {@link PatternModification} to add a 1:1 relation to an existing {@link NodePattern}.
     * @param edge the edge establishing the relation
     * @param id the ID of the existing target {@link NodePattern}
     * @param <S> the target {@link Node} type
     * @return the pattern modification
     */
    @SafeVarargs
    public final <S extends Node, T extends Node, C extends T> PatternModification<S> relatedExisting(CpgEdge<S, T> edge, Class<C> cClass, String id,
            PatternModification<? super C>... modifications) {
        return new AddRelatedExistingNode<>(edge, cClass, id, List.of(modifications));
    }

    /**
     * Creates a {@link PatternModification} to add a 1:n relation to an existing {@link NodePattern}.
     * @param edge the multi-edge establishing the relation
     * @param id the ID of the existing target {@link NodePattern}
     * @param <T> the target {@link Node} type
     * @param <R> the related {@link Node} type
     * @return the pattern modification
     */
    @SafeVarargs
    public final <T extends Node, R extends Node, C extends R> PatternModification<T> relatedExisting1ToN(CpgMultiEdge<T, R> edge, Class<C> cClass,
            String id, PatternModification<C>... modifications) {
        return new AddRelatedExisting1ToNNode<>(edge, cClass, id, List.of(modifications));
    }

    public final <T extends Node> PatternModification<T> setRepresentingNode() {
        return new SetRepresentingNode<>();
    }

    /**
     * Creates a {@link PatternModification} that sets a flag to indicate that the child patterns contained in this pattern
     * are not relevant for the transformation calculation, but only for the pattern matching.
     * @param <T> the target node pattern type
     * @return the pattern modification
     */
    public final <T extends Node> PatternModification<T> stopRecursion() {
        return new StopRecursion<>();
    }

    /**
     * Convenience method to create a {@link WildcardGraphPattern} with the specified child {@link NodePattern}.
     * @param tClass the child {@link Node} class
     * @param childId the ID for the child pattern
     * @param modifications a list of modifications targeting the child node pattern
     * @param <T> the child {@link Node} type
     * @return the {@link WildcardGraphPattern}
     */
    @SafeVarargs
    public final <T extends Node> WildcardGraphPattern<T> wildcardParent(Class<T> tClass, String childId,
            PatternModification<? super T>... modifications) {
        NodePattern<T> child;
        if (!patterns.containsPattern(childId)) {
            child = createNodePattern(tClass, childId, patterns, Arrays.asList(modifications));
        } else {
            child = (NodePattern<T>) patterns.getPattern(childId);
            Arrays.stream(modifications).forEach(m -> m.apply(child, patterns));
        }
        return new WildcardGraphPattern<>(tClass, child, patterns);
    }

    /**
     * {@link PatternModification}s serve to modify a {@link NodePattern}, e.g. add relations and properties to it.
     * @param <T> the target {@link Node} type
     */
    public sealed interface PatternModification<T extends Node> {
        /**
         * Applies this {@link PatternModification} to the given target {@link NodePattern}.
         * @param target the target {@link NodePattern}
         * @param patterns the current {@link SimpleGraphPattern}'s patterns
         */
        void apply(NodePattern<? extends T> target, PatternRegistry patterns);

    }

    public sealed interface PatternListModification<T extends Node> {
        void apply(NodeListPattern<T> target, PatternRegistry patterns);
    }

    /**
     * A PatternModification to add a {@link NodePattern} related via a {@link CpgEdge}.
     * @param <S> The source {@link Node} type
     * @param <T> The target {@link Node} type, specified by the edge
     * @param <C> The concrete target {@link Node} type
     */
    final static class AddRelatedNode<S extends Node, T extends Node, C extends T> implements PatternModification<S> {

        private final CpgEdge<S, T> getter;
        private final Class<C> rClass;
        private final String id;

        private final List<PatternModification<? super C>> modifications;

        /**
         * Creates a new {@link AddRelatedNode} object.
         * @param edge the edge connecting the source node with the target node
         * @param rClass the concrete class of the target node
         * @param id the id for the related node
         * @param modifications list of modifications to the target node
         */
        public AddRelatedNode(CpgEdge<S, T> edge, Class<C> rClass, String id, List<PatternModification<? super C>> modifications) {
            this.getter = edge;
            this.rClass = rClass;
            this.id = id;
            this.modifications = modifications;
        }

        @Override
        public void apply(NodePattern<? extends S> target, PatternRegistry patterns) {
            NodePattern<C> related = createNodePattern(rClass, id, patterns, modifications);
            target.addRelatedNodePattern(related, getter);
        }

    }

    final static class AddNode<T extends Node, C extends T> implements PatternListModification<T> {
        private final Class<C> cClass;
        private final String id;
        private final List<PatternModification<? super C>> modifications;

        public AddNode(Class<C> cClass, String id, List<PatternModification<? super C>> modifications) {
            this.cClass = cClass;
            this.id = id;
            this.modifications = modifications;
        }

        @Override
        public void apply(NodeListPattern<T> target, PatternRegistry patterns) {
            NodePattern<C> nodePattern = createNodePattern(cClass, id, patterns, modifications);
            target.addElement(nodePattern);
        }
    }

    /**
     * A {@link PatternModification} to add a {@link NodePattern} related via a CpgEdge that has already been created.
     * @param <S> The source {@link Node} type
     * @param <T> The target {@link Node} type, specified by the edge
     */
    final static class AddRelatedExistingNode<S extends Node, T extends Node, C extends T> implements PatternModification<S> {

        private final CpgEdge<S, T> getter;
        private final Class<C> cClass;
        private final String id;
        private final List<PatternModification<? super C>> modifications;

        /**
         * Creates a new {@link AddRelatedExistingNode} object.
         * @param edge the edge connecting the source node with the target node
         * @param cClass the node class of the related node
         * @param id the id for the related node
         */
        public AddRelatedExistingNode(CpgEdge<S, T> edge, Class<C> cClass, String id, List<PatternModification<? super C>> modifications) {
            this.getter = edge;
            this.cClass = cClass;
            this.id = id;
            this.modifications = modifications;
        }

        public void apply(NodePattern<? extends S> target, PatternRegistry patterns) {
            NodePattern<C> related = (NodePattern<C>) patterns.getPattern(id);
            target.addRelatedNodePattern(related, getter);
            modifications.forEach(m -> m.apply(related, patterns));
        }
    }

    /**
     * A {@link PatternModification} to add a required {@link Predicate} property to a NodePattern.
     * @param <T> The target {@link Node} type
     */

    static final class AddProperty<T extends Node> implements PatternModification<T> {
        private final Predicate<T> property;

        public AddProperty(Predicate<T> property) {
            this.property = property;
        }

        @Override
        public void apply(NodePattern<? extends T> target, PatternRegistry patterns) {
            target.addProperty(property);
        }
    }

    /**
     * A {@link PatternModification} to add a {@link NodePattern} related via a CpgMultiEdge that has already been created.
     * @param <S> The source {@link Node} type
     * @param <T> The target {@link Node} type, specified by the edge
     */
    static final class AddRelatedExisting1ToNNode<S extends Node, T extends Node, C extends T> implements PatternModification<S> {

        private final CpgMultiEdge<S, T> edge;
        private final String id;
        private final List<PatternModification<C>> modifications;

        /**
         * Creates a new {@link AddRelatedExisting1ToNNode} object.
         * @param edge the edge connecting the source node with the target node
         * @param id the id for the related node
         */
        public AddRelatedExisting1ToNNode(CpgMultiEdge<S, T> edge, Class<C> cClass, String id, List<PatternModification<C>> modifications) {
            this.edge = edge;
            this.id = id;
            this.modifications = modifications;

        }

        public void apply(NodePattern<? extends S> target, PatternRegistry patterns) {
            NodePattern<C> related = (NodePattern<C>) patterns.getPattern(id);
            target.addRelated1ToNNodePattern(related, edge);
            modifications.forEach(m -> m.apply(related, patterns));
        }
    }

    /**
     * A {@link PatternModification} to add a {@link NodePattern} related via a CpgMultiEdge.
     * @param <S> The source {@link Node} type
     * @param <T> The target {@link Node} type, specified by the edge
     * @param <C> The concrete target {@link Node} type
     */
    static final class AddRelated1ToNNode<S extends Node, T extends Node, C extends T> implements PatternModification<S> {
        private final CpgMultiEdge<S, T> edge;
        private final Class<C> cClass;
        private final String id;
        private final List<PatternModification<? super C>> modifications;

        /**
         * Creates a new {@link AddRelated1ToNNode} object.
         * @param edge the edge connecting the source node with the target node
         * @param cClass the concrete class of the target node
         * @param id the id for the related node
         * @param modifications list of modifications to the target node
         */
        public AddRelated1ToNNode(CpgMultiEdge<S, T> edge, Class<C> cClass, String id, List<PatternModification<? super C>> modifications) {
            this.edge = edge;
            this.cClass = cClass;
            this.id = id;
            this.modifications = modifications;
        }

        @Override
        public void apply(NodePattern<? extends S> target, PatternRegistry patterns) {
            NodePattern<C> related = NodePattern.forNodeType(cClass);
            patterns.put(id, related);
            modifications.forEach(m -> m.apply(related, patterns));
            target.addRelated1ToNNodePattern(related, edge);
        }
    }

    static final class AddForAllRelated<S extends Node, T extends Node, C extends T> implements PatternModification<S> {
        private final CpgMultiEdge<S, T> edge;
        private final Class<C> cClass;
        private final String id;
        private final List<PatternModification<? super C>> modifications;

        /**
         * Creates a new {@link AddForAllRelated} object.
         * @param edge the edge connecting the source node with the target node
         * @param cClass the concrete class of the target node
         * @param modifications list of modifications to the target node
         */
        public AddForAllRelated(CpgMultiEdge<S, T> edge, Class<C> cClass, String id, List<PatternModification<? super C>> modifications) {
            this.edge = edge;
            this.cClass = cClass;
            this.id = id;
            this.modifications = modifications;
        }

        @Override
        public void apply(NodePattern<? extends S> target, PatternRegistry patterns) {
            NodePattern<C> related = NodePattern.forNodeType(cClass);
            patterns.put(id, related);
            modifications.forEach(m -> m.apply(related, patterns));
            target.addForAllRelated(related, edge);
        }
    }

    static final class AddRelated1ToNSequence<S extends Node, T extends Node, C extends T> implements PatternModification<S> {
        private final CpgMultiEdge<S, T> edge;
        private final Class<C> cClass;
        private final List<PatternListModification<C>> modifications;

        public AddRelated1ToNSequence(CpgMultiEdge<S, T> edge, Class<C> cClass, List<PatternListModification<C>> modifications) {
            this.edge = edge;
            this.cClass = cClass;
            this.modifications = modifications;
        }

        @Override
        public void apply(NodePattern<? extends S> target, PatternRegistry patterns) {
            NodeListPattern<C> nodeList = new NodeListPattern<>(cClass);
            modifications.forEach(m -> m.apply(nodeList, patterns));
            target.addRelated1ToNSequence(nodeList, edge);
        }
    }

    private record AddEqualAttributes<P, S extends Node>(CpgPropertyEdge<S, P> propertyEdge, String otherId) implements PatternModification<S> {
        @Override
        public void apply(NodePattern<? extends S> target, PatternRegistry patterns) {
            NodePattern<S> otherPattern = (NodePattern<S>) patterns.getPattern(otherId);
            MatchProperty<S> property = (s, match) -> {
                P value = propertyEdge.get(s);
                P otherValue = propertyEdge.get(match.get(otherPattern));
                return Objects.equals(value, otherValue);
            };
            target.addMatchProperty(property);
        }
    }

    private record AddNotEqualTo<S extends Node>(String otherId) implements PatternModification<S> {
        @Override
        public void apply(NodePattern<? extends S> target, PatternRegistry patterns) {
            NodePattern<?> otherPattern = patterns.getPattern(otherId);
            MatchProperty<Node> property = (s, match) -> !Objects.equals(s, match.get(otherPattern));
            target.addMatchProperty(property);
        }
    }

    private static final class StopRecursion<T extends Node> implements PatternModification<T> {
        @Override
        public void apply(NodePattern<? extends T> target, PatternRegistry patterns) {
            target.markStopRecursion();
        }
    }

    private static final class SetRepresentingNode<T extends Node> implements PatternModification<T> {

        @Override
        public void apply(NodePattern<? extends T> target, PatternRegistry patterns) {
            patterns.setRepresentingNode(target);
        }
    }

    private record AddAssignedValueStableBetween<S extends Node>(String startId, String endId) implements PatternModification<S> {

        @Override
        public void apply(NodePattern<? extends S> target, PatternRegistry patterns) {
            NodePattern<?> startNP = patterns.getPattern(startId);
            NodePattern<?> endNP = patterns.getPattern(endId);
            MatchProperty<S> matchProperty = (s, match) -> {
                Set<Node> assignNodes = PatternUtil.dfgReferences(s);
                // s is a constant term
                if (assignNodes.isEmpty()) {
                    return true;
                }

                Set<Node> seenList = new HashSet<>();
                Node start = match.get(startNP);
                Node end = match.get(endNP);
                LinkedList<Node> workList = new LinkedList<>(start.getNextEOG());
                seenList.add(start);
                while (!workList.isEmpty()) {
                    Node node = workList.removeFirst();
                    if (!seenList.containsAll(node.getPrevEOG())) {
                        // Process all predecessors first
                        continue;
                    }
                    List<Node> eogSuccessors = new LinkedList<>(node.getNextEOG());
                    eogSuccessors.removeAll(seenList);
                    eogSuccessors.removeAll(assignNodes);

                    if (eogSuccessors.contains(end)) {
                        return true;
                    }

                    workList.addAll(eogSuccessors); // add to the end
                    seenList.add(node);
                }
                return false;
            };
            target.addMatchProperty(matchProperty);
        }
    }
}
