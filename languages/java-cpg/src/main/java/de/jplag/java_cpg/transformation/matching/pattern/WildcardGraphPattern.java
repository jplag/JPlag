package de.jplag.java_cpg.transformation.matching.pattern;

import de.fraunhofer.aisec.cpg.graph.Node;
import de.jplag.java_cpg.transformation.matching.edges.CpgEdge;
import de.jplag.java_cpg.transformation.matching.edges.CpgMultiEdge;
import de.jplag.java_cpg.transformation.matching.edges.Edges;
import de.jplag.java_cpg.transformation.matching.edges.IEdge;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import static de.jplag.java_cpg.transformation.matching.edges.IEdge.EdgeCategory.AST;
import static de.jplag.java_cpg.transformation.matching.pattern.PatternUtil.nthElement;

/**
 * This class represents a pattern where the root node's parent is unknown, but involved in a transformation (e.g. the root node is moved/deleted).
 * @param <T> The node type of the child node of the wildcard parent
 */
public class WildcardGraphPattern<T extends Node> extends SimpleGraphPattern<Node> {
    private final Class<T> tClass;
    private final ParentNodePattern<T> wildcardParent;
    private final List<IEdge<? extends Node, ? super T>> edgesToType;

    /**
     * Creates a new {@link WildcardGraphPattern}.
     *
     * @param tClass            The node type of the child node
     * @param child
     * @param patterns A mapping of {@link String} IDs to {@link NodePattern}s.
     */
    WildcardGraphPattern(Class<T> tClass, NodePattern<T> child, PatternRegistry patterns) {
        super(new ParentNodePattern<>(tClass, child), patterns);
        this.tClass = tClass;
        this.edgesToType = Edges.getEdgesToType(tClass);
        this.wildcardParent = (ParentNodePattern<T>) getRoot();
        patterns.put(patterns.createWildcardId(), wildcardParent);
    }

    @Override
    public List<Match> recursiveMatch(Node rootCandidate) {
        // rootCandidate is actually candidate for wildcard parentPattern pattern!
        List<Match> matches = new ArrayList<>();
        matches.add(new Match(this));

        this.wildcardParent.recursiveMatch(rootCandidate, matches, null);

        return matches;
    }

    @Override
    public boolean validate(Match match) {
        Node rootCandidate = match.get(this.wildcardParent);
        return recursiveMatch(rootCandidate).stream().anyMatch(match::equals);
    }

    @Override
    public List<Class<? extends Node>> getCandidateNodeClasses() {
        return edgesToType.stream().map(IEdge::getFromClass).distinct().collect(Collectors.toList());
    }

    /**
     * Pattern to describe the unknown AST context that a node may appear in.
     */
    public static class ParentNodePattern<T extends Node> extends NodePattern.NodePatternImpl<Node> {
        private final NodePattern<T> childPattern;
        private final Edge<T> edge;
        private final List<IEdge<? extends Node, ? super T>> edgesToType;

        /**
         * Creates a new {@link ParentNodePattern} for the given child {@link NodePattern}.
         * @param tClass The {@link Node} type class of the child
         * @param child the child node pattern
         */
        public ParentNodePattern(Class<T> tClass, NodePattern<T> child) {
            super(Node.class);
            this.childPattern = child;

            if (Objects.isNull(child)) {
                edge = null;
                edgesToType = null;
                return;
            }

            edge = new Edge(tClass);
            this.addRelatedNodePattern(child, edge);
            edgesToType = Edges.getEdgesToType(tClass);
        }

        @Override
        public void recursiveMatch(Node node, List<Match> matches, CpgEdge<?,?> incoming) {


            List<Match> resultMatches = new ArrayList<>();
            // This node should match if it has a fitting edge and child
            edgesToType.stream()
                .filter(e -> e.getFromClass().isAssignableFrom(node.getClass()))
                .forEach(e -> {
                    List<Match> matchesCopy = new ArrayList<>(matches.stream().map(Match::copy).toList());
                    matchesCopy.forEach(match -> match.register(this, node));
                    wildCardMatch(e, node, matchesCopy);

                    resultMatches.addAll(matchesCopy);
                });
            matches.clear();
            matches.addAll(resultMatches.stream().toList());
        }

        /**
         * Checks for a match of the wild card pattern starting with the parent node.
         *
         * @param <S>     The parent {@link Node} type
         * @param e       The edge from the parent to the child
         * @param parent  the parent {@link Node}
         * @param matches the current set of open matches
         */
        private <S extends Node> void wildCardMatch(IEdge<S, ? super T> e, Node parent, List<Match> matches) {
            S from = (S) parent;
            if (e instanceof CpgEdge<S, ? super T> singleEdge) {
                Node target = singleEdge.getRelated(from);
                if (Objects.isNull(target)) {
                    // target is not part of the graph or empty
                    matches.clear();
                } else {
                    childPattern.recursiveMatch(target, matches, singleEdge);
                    matches.forEach(match -> match.resolveWildcard(this, from, singleEdge));
                }
            } else if (e instanceof CpgMultiEdge<S, ? super T> multiEdge) {
                List<? extends Node> targets = multiEdge.getAllTargets(from);
                var resultMatches = new ArrayList<Match>();
                for (int i = targets.size() - 1; i >= 0 ; i--) {
                    var matchesCopy = new ArrayList<>(matches.stream().map(Match::copy).toList());
                    Node target = targets.get(i);
                    CpgEdge<S, ? super T> edge = nthElement(multiEdge, i);
                    childPattern.recursiveMatch(target, matchesCopy, edge);
                    matchesCopy.forEach(match -> match.resolveWildcard(this, from, edge));
                    resultMatches.addAll(matchesCopy);
                }
                matches.clear();
                matches.addAll(resultMatches);
            }
        }

        @Override
        public List<Class<? extends Node>> getCandidateClasses() {
            return edgesToType.stream().map(IEdge::getFromClass).collect(Collectors.toList());
        }

        public NodePattern<T> getChildPattern() {
            return childPattern;
        }
    }

    /**
     * This models an edge unknown at creation time, of which the target is a T node.
     *
     */
    public static class Edge<T extends Node> extends CpgEdge<Node, T> {

        private Edge(Class<T> tClass) {
            super(null, null, AST);
            this.setToClass(tClass);
        }

        private CpgEdge<?,? super T> matchedEdge;

        @Override
        public Function<Node, T> getter() {
            return nodeGetter(matchedEdge.getter());
        }

        private <S extends Node> Function<Node, T> nodeGetter(Function<S, ? super T> getter) {
            return (Node s) -> (T) getter.apply((S) s);
        }

        @Override
        public BiConsumer<Node, T> setter() {
            return nodeSetter(matchedEdge.setter());
        }

        @NotNull
        private <S extends Node> BiConsumer<Node, T> nodeSetter(BiConsumer<S, ? super T> setter) {
            return (p, t) -> setter.accept((S) p, t);
        }

        @Override
        public boolean isEquivalentTo(IEdge<?, ?> other) {
            // Wildcard edges should always be equivalent.
            return other.getClass().equals(this.getClass());
        }

    }


}
