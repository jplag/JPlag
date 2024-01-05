package de.jplag.java_cpg.transformation.matching.pattern;

import de.fraunhofer.aisec.cpg.graph.Node;
import de.jplag.java_cpg.transformation.matching.edges.CpgEdge;
import de.jplag.java_cpg.transformation.matching.edges.CpgMultiEdge;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

/**
 * Abstract builder class for {@link GraphPattern}s, offering convenience methods.
 *
 * @param <Root> The root {@link Node} class
 */
public abstract class GraphPatternBuilder<Root extends Node> {

    private static final Logger LOGGER = LoggerFactory.getLogger(GraphPatternBuilder.class);
    private final HashMap<String, NodePattern<?>> mapping;

    /**
     * Creates a new {@link GraphPatternBuilder}.
     */
    protected GraphPatternBuilder() {
        this.mapping = new HashMap<>();
    }

    @NotNull
    private static <T extends Node> NodePattern<T> createNodePattern(Class<T> tClass, String id, Map<String, NodePattern<?>> mapping, List<PatternModification<T>> modifications) {
        NodePattern<T> related = NodePattern.forNodeType(tClass);
        modifications.forEach(m -> m.apply(related, mapping));
        if (mapping.containsKey(id)) {
            LOGGER.warn("A NodePattern with the id '%s' is already present in the GraphPattern's mapping".formatted(id));
        }
        mapping.put(id, related);
        return related;
    }

    /**
     * Builds a {@link GraphPattern}. The specifics of the structure of the GraphPattern are defined in the concrete subclasses of {@link GraphPatternBuilder}.
     *
     * @return the graph pattern
     */
    public abstract GraphPattern<Root> build();

    public HashMap<String, NodePattern<?>> getMapping() {
        return mapping;
    }

    /**
     * Convenience method to create a {@link NodePattern}.
     *
     * @param tClass        the {@link Node} class of the pattern
     * @param id            the ID for the pattern
     * @param modifications a list of modifications to the {@link NodePattern}
     * @param <T>           the node type
     * @return the node pattern
     */
    @SafeVarargs
    public final <T extends Node> GraphPattern<T> create(Class<T> tClass, String id, PatternModification<T>... modifications) {
        NodePattern<T> pattern = NodePattern.forNodeType(tClass);
        Arrays.asList(modifications).forEach(m -> m.apply(pattern, mapping));
        mapping.put(id, pattern);
        return new GraphPattern<>(pattern, mapping);
    }

    /**
     * Convenience method to create a {@link NodePattern} and mark matches for removal.
     *
     * @param tClass the {@link Node} class of the pattern
     * @param id     the ID for the pattern
     * @param <T>    the node type
     * @return the node pattern
     */
    public final <T extends Node> GraphPattern<T> remove(Class<T> tClass, String id) {
        NodePattern<T> pattern = NodePattern.forNodeType(tClass);
        pattern.markForRemoval();
        mapping.put(id, pattern);
        return new GraphPattern<>(pattern, mapping);
    }

    /**
     * Convenience method to create a {@link WildcardGraphPattern} with the specified child {@link NodePattern}.
     *
     * @param tClass        the child {@link Node} class
     * @param childId       the ID for the child pattern
     * @param modifications a list of modifications targeting the child node pattern
     * @param <T>           the child {@link Node} type
     * @return the {@link WildcardGraphPattern}
     */
    @SafeVarargs
    public final <T extends Node> GraphPattern<Node> wildcardParent(Class<T> tClass, String childId, PatternModification<T>... modifications) {
        NodePattern<T> child = createNodePattern(tClass, childId, mapping, Arrays.asList(modifications));
        return new WildcardGraphPattern<>(tClass, child, mapping);
    }

    /**
     * Convenience method to wrap an existing {@link GraphPattern} into a {@link WildcardGraphPattern}. The argument's
     * root node becomes the child node of the {@link WildcardGraphPattern}.
     *
     * @param <T> the child {@link Node} type
     * @return the {@link WildcardGraphPattern}
     */
    public final <T extends Node> GraphPattern<Node> wildcardParent(GraphPattern<T> childPattern) {
        NodePattern<T> root = childPattern.getRoot();
        return new WildcardGraphPattern<>(root.getRootClass(), root, mapping);
    }

    @SafeVarargs
    public final <T extends Node, R extends Node, C extends R> PatternModification<T> related(CpgEdge<T, R> getter, Class<C> rClass, String id, PatternModification<C>... modifications) {
        return new AddRelatedNode<>(getter, rClass, id, Arrays.asList(modifications));
    }

    /**
     * Creates a sourceGraph pattern modification that adds a new node pattern that is related to the
     * reference node pattern via a 1:n relation.
     *
     * @param edge          the edge type connecting the node patterns
     * @param rClass        the class object indicating the specified target's class type
     * @param id            the name of the new related node pattern
     * @param modifications a list of modifications targeting the new node pattern
     * @param <T>           the type of the source node pattern
     * @param <R>           the type of the relation target, defined by the edge
     * @param <C>           the concrete type of the related node pattern
     * @return the pattern modification object
     */
    @SafeVarargs
    public final <T extends Node, R extends Node, C extends R> PatternModification<T> related1ToN(CpgMultiEdge<T, R> edge, Class<C> rClass, String id, PatternModification<C>... modifications) {
        return new AddRelated1ToNNode<>(edge, rClass, id, Arrays.asList(modifications));
    }

    /**
     * Creates a {@link PatternModification} to add a 1:1 relation to an existing {@link NodePattern}.
     *
     * @param edge the edge establishing the relation
     * @param id   the ID of the existing target {@link NodePattern}
     * @param <T>  the target {@link Node} type
     * @return the pattern modification
     */
    public final <T extends Node> PatternModification<T> relatedExisting(CpgEdge<T, ? extends Node> edge, String id) {
        return new AddRelatedExistingNode<>(edge, id);
    }

    /**
     * Creates a {@link PatternModification} to add a 1:n relation to an existing {@link NodePattern}.
     *
     * @param edge the multi-edge establishing the relation
     * @param id   the ID of the existing target {@link NodePattern}
     * @param <T>  the target {@link Node} type
     * @param <R>  the related {@link Node} type
     * @return the pattern modification
     */
    public final <T extends Node, R extends Node> PatternModification<T> relatedExisting1ToN(CpgMultiEdge<T, R> edge, String id) {
        return new AddRelatedExisting1ToNNode<>(edge, id);
    }

    /**
     * Creates a {@link PatternModification} to add a property to a {@link NodePattern}.
     *
     * @param property the predicate establishing the property
     * @param <T>      the target {@link Node} type
     * @return the pattern modification
     */
    public final <T extends Node> PatternModification<T> property(Predicate<T> property) {
        return new AddProperty<>(property);
    }

    /**
     * Creates a {@link PatternModification} to add a sequence of related node pattern to a {@link NodePattern}.
     *
     * @param edge   the multi-edge establishing the relation
     * @param tClass the (super)type class of the related nodes
     * @param <S>    the source {@link Node} type
     * @param <T>    the target {@link Node} type
     * @return the pattern modification
     */
    @SafeVarargs
    public final <S extends Node, T extends Node> PatternModification<S> related1ToNSequence(CpgMultiEdge<S, T> edge, Class<T> tClass, PatternListModification<T>... modifications) {
        return new AddRelated1ToNSequence<>(edge, tClass, Arrays.asList(modifications));
    }

    @SafeVarargs
    public final <T extends Node, C extends T> PatternListModification<T> node(Class<C> cClass, Class<T> tClass, String id, PatternModification<C>... modifications) {
        return new AddNode<T, C>(cClass, tClass, id, Arrays.asList(modifications));
    }

    /**
     * {@link PatternModification} serve to modify a {@link NodePattern}, e.g. add relations and properties to it.
     *
     * @param <T> the target {@link Node} type
     */
    public sealed interface PatternModification<T extends Node> {
        /**
         * Applies this {@link PatternModification} to the given target {@link NodePattern}.
         *
         * @param target  the target {@link NodePattern}
         * @param mapping the current {@link GraphPattern}'s mapping
         */
        void apply(NodePattern<T> target, Map<String, NodePattern<?>> mapping);
    }

    public sealed interface PatternListModification<T extends Node> {
        void apply(NodeListPattern<T> target, Map<String, NodePattern<?>> mapping);
    }

    /**
     * A PatternModification to add a {@link NodePattern} related via a {@link CpgEdge}.
     *
     * @param <S> The source {@link Node} type
     * @param <T> The target {@link Node} type, specified by the edge
     * @param <C> The concrete target {@link Node} type
     */
    final static class AddRelatedNode<S extends Node, T extends Node, C extends T> implements PatternModification<S> {

        private final CpgEdge<S, T> getter;
        private final Class<C> rClass;
        private final String id;

        private final List<PatternModification<C>> modifications;

        /**
         * Creates a new {@link AddRelatedNode} object.
         *
         * @param edge          the edge connecting the source node with the target node
         * @param rClass        the concrete class of the target node
         * @param id            the id for the related node
         * @param modifications list of modifications to the target node
         */
        public AddRelatedNode(CpgEdge<S, T> edge, Class<C> rClass, String id, List<PatternModification<C>> modifications) {
            this.getter = edge;
            this.rClass = rClass;
            this.id = id;
            this.modifications = modifications;
        }

        @Override
        public void apply(NodePattern<S> target, Map<String, NodePattern<?>> mapping) {
            NodePattern<C> related = createNodePattern(rClass, id, mapping, modifications);
            target.addRelatedNodePattern(related, getter);
        }

    }

    final static class AddNode<T extends Node, C extends T> implements PatternListModification<T> {
        private final Class<C> cClass;
        private final String id;
        private final List<PatternModification<C>> modifications;

        public AddNode(Class<C> cClass, Class<T> tClass, String id, List<PatternModification<C>> modifications) {
            this.cClass = cClass;
            this.id = id;
            this.modifications = modifications;
        }

        @Override
        public void apply(NodeListPattern<T> target, Map<String, NodePattern<?>> mapping) {
            NodePattern<C> nodePattern = createNodePattern(cClass, id, mapping, modifications);
            target.addElement(nodePattern);
        }
    }

    /**
     * A {@link PatternModification} to add a {@link NodePattern} related via a CpgEdge that has already been created.
     *
     * @param <S> The source {@link Node} type
     * @param <T> The target {@link Node} type, specified by the edge
     */
    final static class AddRelatedExistingNode<S extends Node, T extends Node> implements PatternModification<S> {

        private final CpgEdge<S, T> getter;
        private final String id;

        /**
         * Creates a new {@link AddRelatedExistingNode} object.
         *
         * @param edge the edge connecting the source node with the target node
         * @param id   the id for the related node
         */
        public AddRelatedExistingNode(CpgEdge<S, T> edge, String id) {
            this.getter = edge;
            this.id = id;
        }

        public void apply(NodePattern<S> target, Map<String, NodePattern<?>> mapping) {
            NodePattern<? extends T> related = (NodePattern<? extends T>) mapping.get(id);
            target.addRelatedNodePattern(related, getter);
        }
    }

    static final class AddProperty<T extends Node> implements PatternModification<T> {
        private final Predicate<T> property;

        public AddProperty(Predicate<T> property) {
            this.property = property;
        }

        @Override
        public void apply(NodePattern<T> target, Map<String, NodePattern<?>> mapping) {
            target.addProperty(property);
        }
    }

    /**
     * A {@link PatternModification} to add a {@link NodePattern} related via a CpgMultiEdge that has already been created.
     *
     * @param <S> The source {@link Node} type
     * @param <T> The target {@link Node} type, specified by the edge
     */
    static final class AddRelatedExisting1ToNNode<S extends Node, T extends Node> implements PatternModification<S> {

        private final CpgMultiEdge<S, T> edge;
        private final String id;

        /**
         * Creates a new {@link AddRelatedExisting1ToNNode} object.
         *
         * @param edge the edge connecting the source node with the target node
         * @param id   the id for the related node
         */
        public AddRelatedExisting1ToNNode(CpgMultiEdge<S, T> edge, String id) {
            this.edge = edge;
            this.id = id;
        }

        public void apply(NodePattern<S> target, Map<String, NodePattern<?>> mapping) {
            NodePattern<T> related = (NodePattern<T>) mapping.get(id);
            target.addRelated1ToNNodePattern(related, edge);
        }
    }

    /**
     * A {@link PatternModification} to add a {@link NodePattern} related via a CpgMultiEdge.
     *
     * @param <S> The source {@link Node} type
     * @param <T> The target {@link Node} type, specified by the edge
     * @param <C> The concrete target {@link Node} type
     */
    static final class AddRelated1ToNNode<S extends Node, T extends Node, C extends T> implements PatternModification<S> {
        private final CpgMultiEdge<S, T> edge;
        private final Class<C> cClass;
        private final String id;
        private final List<PatternModification<C>> modifications;

        /**
         * Creates a new {@link AddRelated1ToNNode} object.
         *
         * @param edge          the edge connecting the source node with the target node
         * @param cClass        the concrete class of the target node
         * @param id            the id for the related node
         * @param modifications list of modifications to the target node
         */
        public AddRelated1ToNNode(CpgMultiEdge<S, T> edge, Class<C> cClass, String id, List<PatternModification<C>> modifications) {
            this.edge = edge;
            this.cClass = cClass;
            this.id = id;
            this.modifications = modifications;
        }

        @Override
        public void apply(NodePattern<S> target, Map<String, NodePattern<?>> mapping) {
            NodePattern<C> related = NodePattern.forNodeType(cClass);
            modifications.forEach(m -> m.apply(related, mapping));
            target.addRelated1ToNNodePattern(related, edge);
            mapping.put(id, related);
        }
    }

    static final class AddRelated1ToNSequence<S extends Node, T extends Node> implements PatternModification<S> {
        private final CpgMultiEdge<S, T> edge;
        private final Class<T> tClass;
        private final List<PatternListModification<T>> modifications;

        public AddRelated1ToNSequence(CpgMultiEdge<S, T> edge, Class<T> tClass, List<PatternListModification<T>> modifications) {
            this.edge = edge;
            this.tClass = tClass;
            this.modifications = modifications;
        }

        @Override
        public void apply(NodePattern<S> target, Map<String, NodePattern<?>> mapping) {
            NodeListPattern<T> nodeList = new NodeListPattern<>(tClass);
            modifications.forEach(m -> m.apply(nodeList, mapping));
            target.addRelated1ToNSequence(nodeList, edge);
        }
    }
}
