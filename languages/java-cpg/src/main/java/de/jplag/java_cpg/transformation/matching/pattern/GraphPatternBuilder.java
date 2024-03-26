package de.jplag.java_cpg.transformation.matching.pattern;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.IntStream;

import org.jetbrains.annotations.NotNull;

import de.fraunhofer.aisec.cpg.graph.Node;
import de.jplag.java_cpg.transformation.GraphTransformation;
import de.jplag.java_cpg.transformation.Role;
import de.jplag.java_cpg.transformation.matching.edges.CpgAttributeEdge;
import de.jplag.java_cpg.transformation.matching.edges.CpgEdge;
import de.jplag.java_cpg.transformation.matching.edges.CpgMultiEdge;
import de.jplag.java_cpg.transformation.matching.pattern.relation.ForAllRelatedNode;
import de.jplag.java_cpg.transformation.matching.pattern.relation.RelatedNode;
import de.jplag.java_cpg.transformation.matching.pattern.relation.RelatedOneToNNode;

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
     * @param role a role for this {@link NodePattern}
     * @param patterns the pattern registry as in-out parameter
     * @param modifications the modifications to be applied to the new {@link NodePattern}
     * @param <T> the {@link Node} type of the represented {@link Node}
     * @return the node pattern
     */
    @NotNull
    private static <T extends Node> NodePattern<T> createNodePattern(Class<T> tClass, Role role, PatternRegistry patterns,
            List<PatternModification<? super T>> modifications) {
        NodePattern<T> related = NodePattern.forNodeType(tClass);
        patterns.put(role, related);
        modifications.forEach(m -> m.apply(related, patterns));
        return related;
    }

    /**
     * Creates a {@link PatternModification} that adds the {@link MatchProperty} to a {@link NodePattern} that a specific
     * attribute of a matching {@link Node} must be equal to the same attribute of another {@link Node}.
     * @param propertyEdge the property edge
     * @param otherRole the role of the other {@link NodePattern}
     * @param <T> the {@link Node} type
     * @param <P> the attribute type
     * @return the pattern modification
     */
    public static <T extends Node, P> PatternModification<T> equalAttributes(CpgAttributeEdge<T, P> propertyEdge, Class<T> sClass, Role otherRole) {
        return new AddEqualAttributes<>(propertyEdge, sClass, otherRole);
    }

    /**
     * Creates a {@link PatternModification} that adds the {@link MatchProperty} to a {@link NodePattern} that the assigned
     * value is unchanged between the evaluation of the two given {@link Node}s.
     * @param startRole the role of the starting node role
     * @param endRole the role of the end node role
     * @param <T> the type of
     * @return the pattern modification
     */
    public static <T extends Node> PatternModification<T> assignedValueStableBetween(Role startRole, Role endRole) {
        return new AddAssignedValueStableBetween<>(startRole, endRole);
    }

    /**
     * Creates a {@link PatternModification} that adds a {@link Predicate} property to a {@link NodePattern} that specifies
     * that matching {@link Node}s not be equal to the {@link Node} given by the role.
     * @param otherRole the role of the other {@link Node}
     * @param <T> the {@link Node} type of the target node
     * @return the pattern modification
     */
    public static <T extends Node> PatternModification<T> notEqualTo(Role otherRole) {
        return new AddNotEqualTo<>(otherRole);
    }

    @SafeVarargs
    public static <T extends Node, R extends Node, C extends R> PatternModification<T> relatedConsecutive(CpgMultiEdge<T, R> edge, Class<C> cClass,
            PatternListModification<? extends C>... listModifications) {
        return new AddConsecutive<>(edge, cClass, Arrays.asList(listModifications));
    }

    /**
     * Builds a {@link SimpleGraphPattern}. The specifics of the structure of the SimpleGraphPattern are defined in the
     * concrete subclasses of {@link GraphPatternBuilder}.
     * @return the graph patterns
     */
    public abstract GraphPattern build();

    /**
     * Convenience method to create a {@link NodePattern}.
     * @param tClass the {@link Node} class of the pattern
     * @param role the role for the pattern
     * @param modifications a list of modifications to the {@link NodePattern}
     * @param <T> the node type
     * @return the node pattern
     */
    @SafeVarargs
    public final <T extends Node> SimpleGraphPattern<T> create(Class<T> tClass, Role role, PatternModification<? super T>... modifications) {
        NodePattern<T> pattern;
        if (patterns.containsPattern(role)) {
            pattern = patterns.getPattern(role, tClass);
        } else {
            pattern = NodePattern.forNodeType(tClass);
            patterns.put(role, pattern);
        }
        Arrays.asList(modifications).forEach(m -> m.apply(pattern, patterns));
        return new SimpleGraphPattern<>(pattern, patterns);
    }

    /**
     * Creates an empty {@link WildcardGraphPattern}. It can be used as a target pattern for a {@link GraphTransformation}
     * where a {@link Node} shall be removed.
     * @return the wildcard graph patterns
     */
    protected SimpleGraphPattern<Node> emptyWildcardParent() {
        return new WildcardGraphPattern<>(Node.class, null, patterns);
    }

    /**
     * Creates a {@link PatternModification} that adds a {@link ForAllRelatedNode} to a {@link NodePattern}.
     * @param multiEdge The multi edge
     * @param cClass the concrete class object of the for-all related nodes
     * @param role the role for the related nodes
     * @param modifications the modifications for the created node patterns
     * @param <T> the type of source node
     * @param <R> the type of related node, defined by the edge
     * @param <C> the concrete type of related node
     * @return the pattern modification
     */
    @SafeVarargs
    public final <T extends Node, R extends Node, C extends R> PatternModification<T> forAllRelated(CpgMultiEdge<T, R> multiEdge, Class<C> cClass,
            Role role, PatternModification<? super C>... modifications) {
        return new AddForAllRelated<>(multiEdge, cClass, role, Arrays.asList(modifications));
    }

    /**
     * Creates a {@link MultiGraphPattern} from the given {@link SimpleGraphPattern}s.
     * @param subgraphs the subgraph patterns
     * @return the multi graph patterns
     */
    public final MultiGraphPattern multiRoot(SimpleGraphPattern<?>... subgraphs) {
        return new MultiGraphPattern(List.of(subgraphs), patterns);
    }

    /**
     * Creates a {@link PatternListModification} that adds a related {@link NodePattern} to another
     * {@link NodePattern}ePattern in sequence.
     * @param cClass the concrete class of the added NodePattern
     * @param role the role for the node patterns
     * @param modifications the modifications to be applied to the node
     * @param <C> the concrete type of the added node
     * @return the patterns list modification
     */
    @SafeVarargs
    public final <C extends Node> PatternListModification<C> node(Class<C> cClass, Role role, PatternModification<? super C>... modifications) {
        return new AddNode<>(cClass, role, Arrays.asList(modifications));
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
     * Creates a {@link PatternModification} that adds a new node patterns that is related to the reference node patterns
     * via a 1:n relation.
     * @param edge the edge type connecting the node patterns
     * @param rClass the class object indicating the specified target's class type
     * @param role the name of the new related node patterns
     * @param modifications a list of modifications targeting the new node patterns
     * @param <T> the type of the source node patterns
     * @param <R> the type of the relation target, defined by the edge
     * @param <C> the concrete type of the related node patterns
     * @return the pattern modification object
     */
    @SafeVarargs
    public final <T extends Node, R extends Node, C extends R> PatternModification<T> related(CpgEdge<T, R> edge, Class<C> rClass, Role role,
            PatternModification<? super C>... modifications) {
        return new AddRelatedNode<>(edge, rClass, role, Arrays.asList(modifications));
    }

    /**
     * Creates a {@link PatternModification} that adds a new node patterns that is related to the reference node patterns
     * via a 1:n relation.
     * @param edge the edge type connecting the node patterns
     * @param rClass the class object indicating the specified target's class type
     * @param role the name of the new related node patterns
     * @param modifications a list of modifications targeting the new node patterns
     * @param <T> the type of the source node patterns
     * @param <R> the type of the relation target, defined by the edge
     * @param <C> the concrete type of the related node patterns
     * @return the pattern modification object
     */
    @SafeVarargs
    public final <T extends Node, R extends Node, C extends R> PatternModification<T> related1ToN(CpgMultiEdge<T, R> edge, Class<C> rClass, Role role,
            PatternModification<? super C>... modifications) {
        return new AddRelated1ToNNode<>(edge, rClass, role, Arrays.asList(modifications));
    }

    /**
     * Creates a {@link PatternModification} to add a 1:1 relation to an existing {@link NodePattern}.
     * @param edge the edge establishing the relation
     * @param role the {@link Role} of the existing target {@link NodePattern}
     * @param cClass the class of the related node
     * @param modifications modifications to the related node
     * @param <T> the target node type, as specified by the edge
     * @param <R> the related node type, as specified by the edge
     * @param <C> the concrete node type of the related node
     * @return the pattern modification
     */
    @SafeVarargs
    public final <T extends Node, R extends Node, C extends R> PatternModification<T> relatedExisting(CpgEdge<T, R> edge, Class<C> cClass, Role role,
            PatternModification<? super C>... modifications) {
        return new AddRelatedExistingNode<>(edge, cClass, role, List.of(modifications));
    }

    /**
     * Creates a {@link PatternModification} to add a 1:n relation to an existing {@link NodePattern}.
     * @param edge the multi-edge establishing the relation
     * @param role the {@link Role} of the existing target {@link NodePattern}
     * @param modifications modifications to the related node
     * @param <T> the target node type, as specified by the edge
     * @param <R> the related node type, as specified by the edge
     * @param <C> the concrete node type of the related node
     * @return the pattern modification
     */
    @SafeVarargs
    public final <T extends Node, R extends Node, C extends R> PatternModification<T> relatedExisting1ToN(CpgMultiEdge<T, R> edge, Class<C> cClass,
            Role role, PatternModification<? super C>... modifications) {
        return new AddRelatedExisting1ToNNode<>(edge, cClass, role, List.of(modifications));
    }

    /**
     * Creates a {@link PatternModification} that sets the target {@link NodePattern} as the representative of the
     * NodePattern. {@link Node}s matching this {@link NodePattern} will be the representative of the {@link Match}.
     * @param <T> the {@link Node} type
     * @return the pattern modification
     */
    public final <T extends Node> PatternModification<T> setRepresentingNode() {
        return new SetRepresentingNode<>();
    }

    /**
     * Creates a {@link PatternModification} that sets a flag to indicate that the child patterns contained in this patterns
     * are not relevant for the transformation calculation, but only for the patterns matching.
     * @param <T> the target node patterns type
     * @return the pattern modification
     */
    public final <T extends Node> PatternModification<T> stopRecursion() {
        return new StopRecursion<>();
    }

    /**
     * Convenience method to create a {@link WildcardGraphPattern} with the specified child {@link NodePattern}.
     * @param tClass the child {@link Node} class
     * @param childRole the {@link Role} for the child patterns
     * @param modifications a list of modifications targeting the child node patterns
     * @param <T> the child {@link Node} type
     * @return the {@link WildcardGraphPattern}
     */
    @SafeVarargs
    public final <T extends Node> WildcardGraphPattern<T> wildcardParent(Class<T> tClass, Role childRole,
            PatternModification<? super T>... modifications) {
        NodePattern<T> child;
        if (!patterns.containsPattern(childRole)) {
            child = createNodePattern(tClass, childRole, patterns, Arrays.asList(modifications));
        } else {
            child = patterns.getPattern(childRole, tClass);
            Arrays.stream(modifications).forEach(m -> m.apply(child, patterns));
        }
        return new WildcardGraphPattern<>(tClass, child, patterns);
    }

    /**
     * {@link PatternModification}s serve to modify a {@link NodePattern}, e.g. add relations and properties to it.
     * @param <T> the target {@link Node} type
     */
    public sealed interface PatternModification<T extends Node>
            permits AddAssignedValueStableBetween, AddConsecutive, AddEqualAttributes, AddForAllRelated, AddNotEqualTo, AddProperty,
            AddRelated1ToNNode, AddRelatedExisting1ToNNode, AddRelatedExistingNode, AddRelatedNode, SetRepresentingNode, StopRecursion {
        /**
         * Applies this {@link PatternModification} to the given target {@link NodePattern}.
         * @param target the target {@link NodePattern}
         * @param patterns the current {@link SimpleGraphPattern}'s patterns
         */
        void apply(NodePattern<? extends T> target, PatternRegistry patterns);

    }

    /**
     * {@link PatternModification}s serve to modify a {@link NodePattern}, e.g. add relations and properties to it.
     * @param <R> the target {@link Node} type
     */
    public sealed interface PatternListModification<R extends Node> permits AddNode {
        /**
         * Applies this {@link PatternListModification} to the given target {@link NodePattern}.
         * @param target the target {@link NodePattern}
         * @param patterns the current {@link SimpleGraphPattern}'s patterns
         */
        <T extends Node> void apply(NodePattern<? extends T> target, CpgMultiEdge<T, ? super R> edge, PatternRegistry patterns,
                Consumer<NodePattern<? extends R>> register);
    }

    /**
     * A PatternModification to add a {@link NodePattern} related via a {@link CpgEdge}.
     * @param <T> The source {@link Node} type
     * @param <R> The target {@link Node} type, specified by the edge
     * @param <C> The concrete target {@link Node} type
     */
    static final class AddRelatedNode<T extends Node, R extends Node, C extends R> implements PatternModification<T> {

        private final CpgEdge<T, R> getter;
        private final Class<C> cClass;
        private final Role role;

        private final List<PatternModification<? super C>> modifications;

        /**
         * Creates a new {@link AddRelatedNode} object.
         * @param edge the edge connecting the source node with the target node
         * @param cClass the concrete class of the target node
         * @param role the role for the related node
         * @param modifications list of modifications to the target node
         */
        public AddRelatedNode(CpgEdge<T, R> edge, Class<C> cClass, Role role, List<PatternModification<? super C>> modifications) {
            this.getter = edge;
            this.cClass = cClass;
            this.role = role;
            this.modifications = modifications;
        }

        @Override
        public void apply(NodePattern<? extends T> target, PatternRegistry patterns) {
            // C extends R -> safe
            @SuppressWarnings("unchecked")
            NodePattern<R> related = (NodePattern<R>) createNodePattern(cClass, role, patterns, modifications);
            target.addRelation(new RelatedNode<>(related, getter));
        }

    }

    static final class AddNode<R extends Node, C extends R> implements PatternListModification<R> {
        private final Class<C> cClass;
        private final Role role;
        private final List<PatternModification<? super C>> modifications;

        public AddNode(Class<C> cClass, Role role, List<PatternModification<? super C>> modifications) {
            this.cClass = cClass;
            this.role = role;
            this.modifications = modifications;
        }

        @Override
        public <T extends Node> void apply(NodePattern<? extends T> target, CpgMultiEdge<T, ? super R> edge, PatternRegistry patterns,
                Consumer<NodePattern<? extends R>> register) {
            NodePattern<C> related = createNodePattern(cClass, role, patterns, modifications);
            register.accept(related);
            target.addRelation(new RelatedOneToNNode<>(related, edge));
        }
    }

    /**
     * A {@link PatternModification} to add a {@link NodePattern} related via a CpgEdge that has already been created.
     * @param <T> The source {@link Node} type
     * @param <R> The target {@link Node} type, specified by the edge
     */
    static final class AddRelatedExistingNode<T extends Node, R extends Node, C extends R> implements PatternModification<T> {

        private final CpgEdge<T, R> getter;
        private final Class<C> cClass;
        private final Role role;
        private final List<PatternModification<? super C>> modifications;

        /**
         * Creates a new {@link AddRelatedExistingNode} object.
         * @param edge the edge connecting the source node with the target node
         * @param cClass the node class of the related node
         * @param role the role for the related node
         */
        public AddRelatedExistingNode(CpgEdge<T, R> edge, Class<C> cClass, Role role, List<PatternModification<? super C>> modifications) {
            this.getter = edge;
            this.cClass = cClass;
            this.role = role;
            this.modifications = modifications;
        }

        public void apply(NodePattern<? extends T> target, PatternRegistry patterns) {
            NodePattern<C> related = patterns.getPattern(role, cClass);
            target.addRelation(new RelatedNode<>(related, getter));
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
     * @param <T> The source {@link Node} type
     * @param <R> The target {@link Node} type, specified by the edge
     */
    static final class AddRelatedExisting1ToNNode<T extends Node, R extends Node, C extends R> implements PatternModification<T> {

        private final CpgMultiEdge<T, R> edge;
        private final Class<C> cClass;
        private final Role role;
        private final List<PatternModification<? super C>> modifications;

        /**
         * Creates a new {@link AddRelatedExisting1ToNNode} object.
         * @param edge the edge connecting the source node with the target node
         * @param role the role for the related node
         */
        public AddRelatedExisting1ToNNode(CpgMultiEdge<T, R> edge, Class<C> cClass, Role role, List<PatternModification<? super C>> modifications) {
            this.edge = edge;
            this.cClass = cClass;
            this.role = role;
            this.modifications = modifications;

        }

        public void apply(NodePattern<? extends T> target, PatternRegistry patterns) {
            NodePattern<C> related = patterns.getPattern(role, cClass);
            target.addRelation(new RelatedOneToNNode<>(related, edge));
            modifications.forEach(m -> m.apply(related, patterns));
        }
    }

    /**
     * A {@link PatternModification} to add a {@link NodePattern} related via a CpgMultiEdge.
     * @param <T> The target {@link Node} type
     * @param <R> The related {@link Node} type, specified by the edge
     * @param <C> The concrete target {@link Node} type
     */
    static final class AddRelated1ToNNode<T extends Node, R extends Node, C extends R> implements PatternModification<T> {
        private final CpgMultiEdge<T, R> edge;
        private final Class<C> cClass;
        private final Role role;
        private final List<PatternModification<? super C>> modifications;

        /**
         * Creates a new {@link AddRelated1ToNNode} object.
         * @param edge the edge connecting the source node with the target node
         * @param cClass the concrete class of the target node
         * @param role the role for the related node
         * @param modifications list of modifications to the target node
         */
        public AddRelated1ToNNode(CpgMultiEdge<T, R> edge, Class<C> cClass, Role role, List<PatternModification<? super C>> modifications) {
            this.edge = edge;
            this.cClass = cClass;
            this.role = role;
            this.modifications = modifications;
        }

        @Override
        public void apply(NodePattern<? extends T> target, PatternRegistry patterns) {
            NodePattern<C> related = NodePattern.forNodeType(cClass);
            patterns.put(role, related);
            modifications.forEach(m -> m.apply(related, patterns));
            target.addRelation(new RelatedOneToNNode<>(related, edge));
        }
    }

    static final class AddForAllRelated<T extends Node, R extends Node, C extends R> implements PatternModification<T> {
        private final CpgMultiEdge<T, R> edge;
        private final Class<C> cClass;
        private final Role role;
        private final List<PatternModification<? super C>> modifications;

        /**
         * Creates a new {@link AddForAllRelated} object.
         * @param edge the edge connecting the source node with the target node
         * @param cClass the concrete class of the target node
         * @param modifications list of modifications to the target node
         */
        public AddForAllRelated(CpgMultiEdge<T, R> edge, Class<C> cClass, Role role, List<PatternModification<? super C>> modifications) {
            this.edge = edge;
            this.cClass = cClass;
            this.role = role;
            this.modifications = modifications;
        }

        @Override
        public void apply(NodePattern<? extends T> target, PatternRegistry patterns) {
            NodePattern<C> related = NodePattern.forNodeType(cClass);
            patterns.put(role, related);
            modifications.forEach(m -> m.apply(related, patterns));
            target.addRelation(new ForAllRelatedNode<>(related, edge));
        }
    }

    private record AddEqualAttributes<P, T extends Node>(CpgAttributeEdge<T, P> propertyEdge, Class<T> sClass, Role otherRole)
            implements PatternModification<T> {
        @Override
        public void apply(NodePattern<? extends T> target, PatternRegistry patterns) {
            NodePattern<T> otherPattern = patterns.getPattern(otherRole, sClass);
            MatchProperty<T> property = (t, match) -> {
                P value = propertyEdge.get(t);
                P otherValue = propertyEdge.get(match.get(otherPattern));
                return Objects.equals(value, otherValue);
            };
            target.addMatchProperty(property);
        }
    }

    private record AddNotEqualTo<T extends Node>(Role otherRole) implements PatternModification<T> {
        @Override
        public void apply(NodePattern<? extends T> target, PatternRegistry patterns) {
            NodePattern<?> otherPattern = patterns.getPattern(otherRole, Node.class);
            MatchProperty<Node> property = (s, match) -> !Objects.equals(s, match.get(otherPattern));
            target.addMatchProperty(property);
        }
    }

    public static final class AddConsecutive<T extends Node, R extends Node, C extends R> implements PatternModification<T> {
        private final CpgMultiEdge<T, R> edge;
        private final Class<C> cClass;
        private final List<PatternListModification<? extends C>> modifications;
        private final List<NodePattern<? extends C>> elements;

        public AddConsecutive(CpgMultiEdge<T, R> edge, Class<C> cClass, List<PatternListModification<? extends C>> modifications) {
            this.edge = edge;
            this.cClass = cClass;
            this.modifications = modifications;
            this.elements = new ArrayList<>();
        }

        @Override
        public void apply(NodePattern<? extends T> target, PatternRegistry patterns) {
            modifications.forEach(m -> m.apply(target, edge, patterns, elements::addLast));

            IntStream.range(0, elements.size()).forEach(idx ->
            // set min index
            edge.saveSequenceIndex(elements.get(idx), idx));

            MatchProperty<T> property = (parent, match) -> {
                List<R> allTargets = edge.getAllTargets(parent);
                int firstChildIndex = allTargets.indexOf(match.get(elements.getFirst()));
                for (int idx = 1; idx < modifications.size(); idx++) {
                    int childIdx = allTargets.indexOf(match.get(elements.get(idx)));
                    if (childIdx != firstChildIndex + idx) {
                        return false;
                    }
                }
                return true;
            };
            target.addMatchProperty(property);
        }

        public CpgMultiEdge<T, R> edge() {
            return edge;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this)
                return true;
            if (obj == null || obj.getClass() != this.getClass())
                return false;
            var that = (AddConsecutive) obj;
            return Objects.equals(this.edge, that.edge) && Objects.equals(this.cClass, that.cClass)
                    && Objects.equals(this.modifications, that.modifications);
        }

        @Override
        public int hashCode() {
            return Objects.hash(edge, cClass, modifications);
        }

        @Override
        public String toString() {
            return "AddConsecutive[" + "edge=" + edge + ", " + "cClass=" + cClass + ", " + "modifications=" + modifications + ']';
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

    private record AddAssignedValueStableBetween<T extends Node>(Role startRole, Role endRole) implements PatternModification<T> {

        @Override
        public void apply(NodePattern<? extends T> target, PatternRegistry patterns) {
            NodePattern<Node> startNP = patterns.getPattern(startRole, Node.class);
            NodePattern<Node> endNP = patterns.getPattern(endRole, Node.class);
            MatchProperty<T> matchProperty = (t, match) -> {
                Set<Node> assignNodes = PatternUtil.dfgReferences(t);
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
