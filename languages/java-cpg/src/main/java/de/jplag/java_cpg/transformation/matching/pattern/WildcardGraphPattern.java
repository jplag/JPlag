package de.jplag.java_cpg.transformation.matching.pattern;

import static de.jplag.java_cpg.transformation.matching.edges.IEdge.EdgeCategory.AST;
import static de.jplag.java_cpg.transformation.matching.pattern.PatternUtil.nthElement;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import de.fraunhofer.aisec.cpg.graph.Node;
import de.jplag.java_cpg.transformation.matching.edges.CpgEdge;
import de.jplag.java_cpg.transformation.matching.edges.CpgMultiEdge;
import de.jplag.java_cpg.transformation.matching.edges.Edges;
import de.jplag.java_cpg.transformation.matching.edges.IEdge;

/**
 * This class represents a pattern where the root node's parent is unknown, but involved in a transformation (e.g. the
 * root node is moved/deleted).
 * @param <T> The node type of the child node of the wildcard parent
 */
public class WildcardGraphPattern<T extends Node> extends SimpleGraphPattern<Node> {
    private final ParentNodePattern<T> wildcardParent;

    /**
     * Creates a new {@link WildcardGraphPattern}.
     * @param tClass The node type of the child node
     * @param child The node pattern representing the child node
     * @param patterns A mapping of {@link String} IDs to {@link NodePattern}s.
     */
    WildcardGraphPattern(Class<T> tClass, NodePattern<T> child, PatternRegistry patterns) {
        super(new ParentNodePattern<>(tClass, child), patterns);
        this.wildcardParent = (ParentNodePattern<T>) getRoot();
        patterns.put(patterns.createWildcardId(), wildcardParent);
    }

    @Override
    public List<Match> recursiveMatch(Node rootCandidate) {
        // rootCandidate is actually candidate for wildcard parent pattern!
        List<Match> matches = new ArrayList<>();
        matches.add(new Match(this));

        this.wildcardParent.recursiveMatch(rootCandidate, matches, null);

        return matches;
    }

    @Override
    public boolean validate(Match match) {
        Node rootCandidate = match.get(this.wildcardParent);
        List<Match> matches = recursiveMatch(rootCandidate);
        return matches.stream().anyMatch(match::equals);
    }

    /**
     * Pattern to describe the unknown AST context that a node may appear in.
     */
    public static class ParentNodePattern<T extends Node> extends NodePattern.NodePatternImpl<Node> {
        private final NodePattern<T> childPattern;
        private final List<IEdge<? extends Node, ? super T>> edgesToType;

        /**
         * Creates a new {@link ParentNodePattern} for the given child {@link NodePattern}.
         * @param tClass The {@link Node} type class of the child
         * @param child the child node pattern
         */
        public ParentNodePattern(Class<T> tClass, NodePattern<T> child) {
            super(Node.class);
            this.childPattern = child;

            Edge<T> edge;
            if (Objects.isNull(child)) {
                edgesToType = null;
                return;
            }

            edge = new Edge<>(tClass);
            this.addRelatedNodePattern(child, edge);
            edgesToType = Edges.getEdgesToType(tClass);
        }

        @Override
        public void recursiveMatch(Node node, List<Match> matches, CpgEdge<?, ?> incoming) {

            List<Match> resultMatches = new ArrayList<>();
            // This node should match if it has a fitting edge and child
            edgesToType.stream().filter(e -> e.getFromClass().isAssignableFrom(node.getClass())).forEach(e -> {
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
         * @param <S> The parent {@link Node} type
         * @param e The edge from the parent to the child
         * @param parent the parent {@link Node}
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
                for (int i = 0; i < targets.size(); i++) {
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
     */
    public static class Edge<T extends Node> extends CpgEdge<Node, T> {

        private Edge(Class<T> tClass) {
            super(null, null, AST);
            this.setToClass(tClass);
        }

        @Override
        public boolean isEquivalentTo(IEdge<?, ?> other) {
            // Wildcard edges should always be equivalent.
            return other.getClass().equals(this.getClass());
        }

    }

}
