package de.jplag.java_cpg.transformation.matching.pattern;

import de.fraunhofer.aisec.cpg.graph.Node;
import de.jplag.java_cpg.transformation.matching.edges.CpgEdge;
import de.jplag.java_cpg.transformation.matching.edges.CpgMultiEdge;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import static de.jplag.java_cpg.transformation.matching.pattern.PatternUtil.nthElement;

/**
 * This class represents a Graph pattern on node level. It can be used to match against a concrete node and its related nodes.
 *
 * @param <T> the type of node represented.
 */
public interface NodePattern<T extends Node> {


    @NotNull
    static <T extends Node> NodePattern<T> forNodeType(Class<T> tClass) {
        return new NodePatternImpl<>(tClass);
    }

    /**
     * Adds a related NodePattern to the pattern.
     *
     * @param <R>     the type of the related node
     * @param related the related NodePattern
     * @param edge    a function to get the related node given a concrete match for this pattern
     */
    <R extends Node> void addRelatedNodePattern(NodePattern<? extends R> related, CpgEdge<T, R> edge);

    /**
     * Adds a related NodePattern to the pattern.
     *
     * @param <R>     the type of the related node
     * @param related the related NodePattern
     * @param edge    a function to get candidates for the related node given a concrete match for this pattern
     */
    <R extends Node> void addRelated1ToNNodePattern(NodePattern<? extends R> related, CpgMultiEdge<T, R> edge);

    /**
     * Adds a property to the pattern that has to hold for this pattern to match.
     *
     * @param property the property
     */
    void addProperty(Predicate<T> property);

    /**
     * Checks whether the given concrete node matches this pattern.
     *
     * @param node     a candidate node
     * @param matches  matches of pattern nodes to sourceGraph nodes
     * @param incoming the incoming edge
     */
    <Root extends Node> void recursiveMatch(Node node, List<GraphPattern.Match<Root>> matches, CpgEdge<?, ?> incoming);

    /**
     * Gets the {@link List} of 1:1-related {@link NodePattern}s of this {@link NodePattern}.
     *
     * @return the related node patterns
     */
    List<RelatedNode<T, ?>> getRelatedNodes();

    /**
     * Gets the {@link List} of 1:n-related {@link NodePattern}s of this {@link NodePattern}.
     *
     * @return the related node patterns
     */
    List<RelatedOneToNNode<T, ?>> getRelated1ToNNodes();

    /**
     * Creates a copy of this {@link NodePattern} and all related nodes and properties.
     *
     * @return the copied {@link NodePattern}
     */
    NodePattern<T> deepCopy();

    /**
     * Indicates whether a {@link de.jplag.java_cpg.transformation.matching.pattern.GraphPattern.Match} of this {@link NodePattern} shall be removed.
     * @return true if the node shall be removed
     */
    boolean isToBeRemoved();

    /**
     * Gets a {@link Class} object for the indicated {@link Node} class.
     * @return the class object
     */
    Class<T> getRootClass();

    /**
     * Sets a flag indicating that a {@link GraphPattern.Match} of this {@link NodePattern} shall be removed.
     */
    void markForRemoval();

    /**
     * Standard implementation of the {@link NodePattern}.
     * @param <T> The {@link Node} type of a target node.
     */
    class NodePatternImpl<T extends Node> implements NodePattern<T> {

        protected final Class<T> clazz;
        private final List<Predicate<T>> properties;
        private final List<RelatedNode<T, ?>> relatedNodes;
        private final List<RelatedOneToNNode<T, ?>> related1ToNNodes;

        private boolean toBeRemoved = false;

        public NodePatternImpl(Class<T> clazz) {
            this.clazz = clazz;
            this.properties = new ArrayList<>();
            this.relatedNodes = new ArrayList<>();
            this.related1ToNNodes = new ArrayList<>();
        }

        @Override
        public <R extends Node> void addRelatedNodePattern(NodePattern<? extends R> related, CpgEdge<T, R> edge) {
            relatedNodes.add(new RelatedNode<>(related, edge));
        }

        @Override
        public <R extends Node> void addRelated1ToNNodePattern(NodePattern<? extends R> related, CpgMultiEdge<T, R> edge) {
            related1ToNNodes.add(new RelatedOneToNNode<>(related, edge));
        }

        @Override
        public void addProperty(Predicate<T> property) {
            properties.add(property);
        }

        @Override
        public <Root extends Node> void recursiveMatch(Node node, List<GraphPattern.Match<Root>> matches, CpgEdge<?, ?> incoming) {

            // We encountered this pattern before. If we have not also arrived at the same node, it's a mismatch.
            var finishedMatches = matches.stream().filter(match -> match.contains(this) && match.get(this).equals(node)).toList();

            // unencountered only
            var openMatches = new ArrayList<>(matches.stream().filter(match -> !match.contains(this)).toList());

            // check node properties

            boolean localPropertiesMismatch = !localMatch(node);
            if (localPropertiesMismatch) {
                matches.clear();
                return;
            }

            //if !localPropertiesMismatch, then this cast is valid
            T tNode = (T) node;

            if (!relatedNodes.isEmpty()) {
                // all related nodes must match
                relatedNodes.forEach(pair -> {
                    Node candidateNode = pair.getNode(tNode);
                    pair.getPattern().recursiveMatch(candidateNode, openMatches, pair.edge);
                });

            }

            if (!related1ToNNodes.isEmpty()) {
                // all related 1:n nodes must match once in every open match
                related1ToNNodes.forEach(pair -> {
                    List<? extends Node> candidates = pair.getCandidates(tNode);
                    var resultMatches = new ArrayList<GraphPattern.Match<Root>>();
                    // but, they might match all for a different candidate
                    // decrementing -> removing ith node does not affect nodes i+1 etc.
                    for (int i = candidates.size() - 1; i >= 0; i--) {
                        Node candidate = candidates.get(i);
                        var openMatchesCopy = new ArrayList<>(openMatches.stream().map(GraphPattern.Match::copy).toList());
                        pair.getPattern().recursiveMatch(candidate, openMatchesCopy, nthElement(pair.edge, i));
                        resultMatches.addAll(openMatchesCopy);
                    }
                    openMatches.clear();
                    openMatches.addAll(resultMatches);
                });
            }

            openMatches.forEach(match -> match.register(this, tNode));
            matches.clear();
            matches.addAll(finishedMatches);
            matches.addAll(openMatches);
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
        public List<RelatedNode<T, ?>> getRelatedNodes() {
            return relatedNodes;
        }

        @Override
        public List<RelatedOneToNNode<T, ?>> getRelated1ToNNodes() {
            return related1ToNNodes;
        }

        @Override
        public NodePattern<T> deepCopy() {
            NodePatternImpl<T> copy = new NodePatternImpl<>(this.clazz);
            copy.properties.addAll(this.properties);
            // TODO does a shallow copy cause trouble?
            copy.relatedNodes.addAll(this.relatedNodes);
            copy.related1ToNNodes.addAll(this.related1ToNNodes);
            return copy;
        }

        @Override
        public void markForRemoval() {
            this.toBeRemoved = true;
        }

        @Override
        public boolean isToBeRemoved() {
            return toBeRemoved;
        }

        @Override
        public Class<T> getRootClass() {
            return clazz;
        }

        @Override
        public String toString() {
            return "NodePattern{%s}".formatted(clazz.getSimpleName());
        }
    }

    /**
     * Pair of a node pattern of a related node and a function to get parentPattern a reference node to a candidate related node.
     *
     * @param <T>     type of the reference node
     * @param <R>     type of the related node
     * @param pattern the pattern describing the related node
     * @param edge    function to get a related node given a reference node
     */
    record RelatedNode<T extends Node, R extends Node>(NodePattern<? extends R> pattern, CpgEdge<T, R> edge) {
        Node getNode(T from) {
            return edge.getRelated(from);
        }

        NodePattern<? extends R> getPattern() {
            return pattern;
        }

        @Override
        public String toString() {
            return "RelatedNode{%s}".formatted(pattern.toString());
        }
    }


    /**
     * Pair of a node pattern of a related node and a function to get parentPattern a reference node to a list of candidate related nodes.
     *
     * @param <T>     type of the reference node
     * @param <R>     type of the related node
     * @param pattern the pattern describing the related node
     * @param edge    function to get the related nodes given a reference node
     */
    record RelatedOneToNNode<T extends Node, R extends Node>(NodePattern<? extends R> pattern,
                                                             CpgMultiEdge<T, R> edge) {
        List<? extends Node> getCandidates(T from) {
            return edge.getAllTargets(from);
        }

        NodePattern<? extends R> getPattern() {
            return pattern;
        }

    }

}


