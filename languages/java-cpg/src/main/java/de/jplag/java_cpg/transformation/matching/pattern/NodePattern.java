package de.jplag.java_cpg.transformation.matching.pattern;

import de.fraunhofer.aisec.cpg.graph.Node;
import de.jplag.java_cpg.transformation.matching.edges.CpgEdge;
import de.jplag.java_cpg.transformation.matching.edges.CpgMultiEdge;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

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
     * Adds a related {@link NodePattern} to the pattern.
     *
     * @param <R>     the type of the related node
     * @param related the related NodePattern
     * @param edge    an edge to the related node given a concrete match for this pattern
     */
    <R extends Node> void addRelatedNodePattern(NodePattern<? extends R> related, CpgEdge<? super T, R> edge);

    /**
     * Adds a related {@link NodePattern} to the pattern.
     *
     * @param <R>     the type of the related node
     * @param related the related NodePattern
     * @param edge    an edge to candidates for the related node given a concrete match for this pattern
     */
    <R extends Node, C extends R> void addRelated1ToNNodePattern(NodePattern<C> related, CpgMultiEdge<? super T, R> edge);
    <R extends Node> void addForAllRelated(NodePattern<? extends R> related, CpgMultiEdge<? super T, R> edge);

    /**
     * Adds a property to the pattern that has to hold for this pattern to match.
     *
     * @param property the property
     */
    void addProperty(Predicate<? super T> property);
    void addMatchProperty(MatchProperty<? super T> property);

    /**
     * Adds a sequence of {@link NodePattern}s related to this node pattern.
     *
     * @param <R>          the (super)type of the related nodes, as specified by the edge
     * @param nodePatterns the node pattern
     * @param edge         a multi edge to the list of nodes of which a subsequence shall match the nodePatterns
     */
    <R extends Node> void addRelated1ToNSequence(NodeListPattern<? extends R> nodePatterns, CpgMultiEdge<? super T, R> edge);

    /**
     * Checks whether the given concrete node matches this pattern.
     *
     * @param node     a candidate node
     * @param matches  matches of pattern nodes to sourceGraph nodes
     * @param incoming the incoming edge
     */
    void recursiveMatch(Node node, List<Match> matches, CpgEdge<?, ?> incoming);

    /**
     * Gets the {@link List} of 1:1-related {@link NodePattern}s of this {@link NodePattern}.
     *
     * @return the related node pattern
     */
    List<RelatedNode<? super T, ?>> getRelatedNodes();

    /**
     * Gets the {@link List} of 1:n-related {@link NodePattern}s of this {@link NodePattern}.
     *
     * @return the related node pattern
     */
    List<Related1ToNNode<? super T, ?>> getRelated1ToNNodes();

    List<Related1ToNSequence<? super T, ?>> getRelated1ToNSequences();

    /**
     * Creates a copy of this {@link NodePattern} and all related nodes and properties.
     *
     * @return the copied {@link NodePattern}
     */
    NodePattern<T> deepCopy();

    /**
     * Gets a {@link Class} object for the indicated {@link Node} class.
     *
     * @return the class object
     */
    Class<T> getRootClass();

    /**
     * Sets a flag indicating that a {@link Match} of this {@link NodePattern} shall be removed.
     */
    void markForRemoval(boolean disconnectEog);

    void markStopRecursion();

    boolean shouldStopRecursion();

    List<Class<? extends T>> getCandidateClasses();


    /**
     * Standard implementation of the {@link NodePattern}.
     *
     * @param <T> The {@link Node} type of a target node.
     */
    class NodePatternImpl<T extends Node> implements NodePattern<T> {

        protected final Class<T> clazz;
        private final List<Predicate<? super T>> properties;
        private List<MatchProperty<? super T>> matchProperties;
        private final List<RelatedNode<? super T, ?>> relatedNodes;
        private final List<Related1ToNNode<? super T, ?>> related1ToNNodes;
        private final List<Related1ToNSequence<? super T, ?>> related1ToNSequences;
        private final List<ForAllRelatedNode<? super T, ?>> forAllRelatedNodes;

        private final EnumSet<NodeAnnotation> annotations;

        public NodePatternImpl(Class<T> clazz) {
            this.clazz = clazz;
            this.properties = new ArrayList<>();
            this.matchProperties = new ArrayList<>();
            this.relatedNodes = new ArrayList<>();
            this.related1ToNNodes = new ArrayList<>();
            this.related1ToNSequences = new ArrayList<>();
            this.forAllRelatedNodes = new ArrayList<>();
            this.annotations = EnumSet.noneOf(NodeAnnotation.class);
        }

        @Override
        public <R extends Node> void addRelatedNodePattern(NodePattern<? extends R> related, CpgEdge<? super T, R> edge) {
            relatedNodes.add(new RelatedNode<>(related, edge));
        }

        @Override
        public <R extends Node, C extends R> void addRelated1ToNNodePattern(NodePattern<C> related, CpgMultiEdge<? super T, R> edge) {
            related1ToNNodes.add(new Related1ToNNode<>(related, edge));
        }

        @Override
        public <R extends Node> void addForAllRelated(NodePattern<? extends R> related, CpgMultiEdge<? super T, R> edge) {
            forAllRelatedNodes.add(new ForAllRelatedNode<>(related, edge));
        }

        @Override
        public void addProperty(Predicate<? super T> property) {
            properties.add(property);
        }

        @Override
        public void addMatchProperty(MatchProperty<? super T> property) {
            matchProperties.add(property);
        }

        @Override
        public <R extends Node> void addRelated1ToNSequence(NodeListPattern<? extends R> nodeListPattern, CpgMultiEdge<? super T, R> edge) {
            related1ToNSequences.add(new Related1ToNSequence<>(nodeListPattern, edge));
        }

        public void recursiveMatch(Node node, List<Match> matches, CpgEdge<?, ?> incoming) {
            // We encountered this pattern before. If we have not also arrived at the same node, it's a mismatch.
            matches.removeIf(match -> match.contains(this) && !match.get(this).equals(node));
            var splitList = matches.stream().collect(Collectors.groupingBy(match -> match.contains(this)));
            var finishedMatches = splitList.getOrDefault(true, new ArrayList<>());

            // unencountered only
            var openMatches = splitList.getOrDefault(false, new ArrayList<>());
            if (openMatches.isEmpty()) return;

            // check node properties

            boolean localPropertiesMismatch = !localMatch(node);
            if (localPropertiesMismatch) {
                matches.clear();
                return;
            }

            //if !localPropertiesMismatch, then this cast is valid
            T tNode = (T) node;
            openMatches.forEach(match -> match.register(this, tNode));


            // all related nodes must match
            relatedNodes.forEach(pair -> {
                Node candidateNode = pair.getNode(tNode);
                if (Objects.isNull(candidateNode)) {
                    openMatches.clear();
                    return;
                }
                pair.getPattern().recursiveMatch(candidateNode, openMatches, pair.edge);
            });

            // all related 1:n nodes must match once in every open match
            related1ToNNodes.forEach(pair -> {
                List<? extends Node> candidates = pair.getCandidates(tNode);
                var resultMatches = new ArrayList<Match>();
                // but, they might match all for a different candidate
                // decrementing -> removing ith node does not affect nodes i+1 etc.
                for (int i = candidates.size() - 1; i >= 0; i--) {
                    Node candidate = candidates.get(i);
                    var openMatchesCopy = new ArrayList<>(openMatches.stream().map(Match::copy).toList());
                    int finalI = i;
                    openMatchesCopy.forEach(match -> match.resolveAny1ofNEdge(pair.edge().getAny1ofNEdgeTo(pair.pattern), finalI));
                    pair.getPattern().recursiveMatch(candidate, openMatchesCopy, nthElement(pair.edge, i));
                    resultMatches.addAll(openMatchesCopy);
                }
                openMatches.clear();
                openMatches.addAll(resultMatches);
            });

            forAllRelatedNodes.forEach(pair -> {
                List<? extends Node> candidates = pair.getCandidates(tNode);
                // use the same matches for all candidates -> first mismatch removes match
                // decrementing -> removing ith node does not affect nodes i+1 etc.
                for (int i = candidates.size() - 1; i >= 0; i--) {
                    Node candidate = candidates.get(i);
                    pair.getPattern().recursiveMatch(candidate, openMatches, nthElement(pair.edge, i));
                    openMatches.forEach(match -> match.remove(pair.getPattern()));
                }
            });


            openMatches.removeIf(match ->
                !matchProperties.isEmpty() && matchProperties.stream().anyMatch(mp -> !mp.test(tNode, match))
            );

            matches.clear();
            matches.addAll(finishedMatches);
            matches.addAll(openMatches);
        }

        /**
         * Checks the local properties of the {@link NodePattern} against the concrete {@link Node}.
         *
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
        public List<RelatedNode<? super T, ?>> getRelatedNodes() {
            return relatedNodes;
        }

        @Override
        public List<Related1ToNNode<? super T, ?>> getRelated1ToNNodes() {
            return related1ToNNodes;
        }

        @Override
        public List<Related1ToNSequence<? super T, ?>> getRelated1ToNSequences() {
            return related1ToNSequences;
        }

        @Override
        public NodePattern<T> deepCopy() {
            NodePatternImpl<T> copy = new NodePatternImpl<>(this.clazz);
            copy.properties.addAll(this.properties);
            // TODO does a shallow copy cause trouble?
            copy.relatedNodes.addAll(this.relatedNodes);
            copy.related1ToNNodes.addAll(this.related1ToNNodes);
            copy.related1ToNSequences.addAll(this.related1ToNSequences);
            return copy;
        }

        @Override
        public void markForRemoval(boolean disconnectEog) {
            this.annotations.add(NodeAnnotation.DISCONNECT_AST);
            if (disconnectEog) {
                this.annotations.add(NodeAnnotation.DISCONNECT_EOG);
            }

        }

        @Override
        public void markStopRecursion() {
            annotations.add(NodeAnnotation.STOP_RECURSION);
        }

        @Override
        public boolean shouldStopRecursion() {
            return annotations.contains(NodeAnnotation.STOP_RECURSION);
        }

        @Override
        public List<Class<? extends T>> getCandidateClasses() {
            return List.of(getRootClass());
        }

        @Override
        public Class<T> getRootClass() {
            return clazz;
        }

        @Override
        public String toString() {
            return "NodePattern{%s}".formatted(clazz.getSimpleName());
        }

        private enum NodeAnnotation {
            DISCONNECT_AST, STOP_RECURSION, REPRESENTING_NODE, DISCONNECT_EOG
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
     * Pair of a node pattern of a related node and a multi edge from a reference node to a list of candidate related nodes.
     *
     * @param <T>     type of the reference node
     * @param <R>     type of the related node
     * @param pattern the pattern describing the related node
     * @param edge    edge from a reference node to the related nodes
     */
    record Related1ToNNode<T extends Node, R extends Node>(NodePattern<? extends R> pattern,
                                                           CpgMultiEdge<T, R> edge) {
        List<? extends Node> getCandidates(T from) {
            return edge.getAllTargets(from);
        }

        NodePattern<? extends R> getPattern() {
            return pattern;
        }

    }

    record ForAllRelatedNode<T extends Node, R extends Node>(NodePattern<? extends R> pattern,
                                                           CpgMultiEdge<T, R> edge) {
        List<? extends Node> getCandidates(T from) {
            return edge.getAllTargets(from);
        }

        NodePattern<? extends R> getPattern() {
            return pattern;
        }

    }

    /**
     * Pair of a sequence of node pattern of related nodes and a multi edge from a reference node to all related nodes.
     *
     * @param <T>      type of the reference node
     * @param <R>      type of the related node
     * @param pattern the pattern describing the related nodes
     * @param edge     edge from a reference node to the related nodes
     */
    record Related1ToNSequence<T extends Node, R extends Node>(NodeListPattern<? extends R> pattern,
                                                               CpgMultiEdge<T, R> edge) {
        List<? extends Node> getCandidates(T from) {
            return edge.getAllTargets(from);
        }

        public NodePattern<? extends R> getPattern(int index) {
            return pattern.get(index);
        }

    }

}


