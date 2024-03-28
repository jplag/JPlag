package de.jplag.java_cpg.transformation.matching.pattern;

import static de.jplag.java_cpg.transformation.matching.edges.IEdge.EdgeCategory.AST;
import static de.jplag.java_cpg.transformation.matching.pattern.PatternUtil.nthElement;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import de.fraunhofer.aisec.cpg.graph.Node;
import de.jplag.java_cpg.transformation.Role;
import de.jplag.java_cpg.transformation.matching.edges.CpgEdge;
import de.jplag.java_cpg.transformation.matching.edges.CpgMultiEdge;
import de.jplag.java_cpg.transformation.matching.edges.Edges;
import de.jplag.java_cpg.transformation.matching.edges.IEdge;
import de.jplag.java_cpg.transformation.matching.pattern.NodePattern.NodePatternImpl;
import de.jplag.java_cpg.transformation.matching.pattern.relation.RelatedNode;

/**
 * This class represents a pattern where the root node's parent is unknown, but involved in a transformation (e.g. the
 * root node is moved/deleted).
 * @param <R> The node type of the child node of the wildcard parent
 */
public class WildcardGraphPattern<R extends Node> extends SimpleGraphPattern<Node> {
    private final ParentNodePattern<R> wildcardParent;

    /**
     * Creates a new {@link WildcardGraphPattern}.
     * @param tClass The node type of the child node
     * @param child The node pattern representing the child node
     * @param patterns A mapping of {@link Role}s to {@link NodePattern}s.
     */
    WildcardGraphPattern(Class<R> tClass, NodePattern<R> child, PatternRegistry patterns) {
        super(new ParentNodePattern<>(tClass, child), patterns);
        this.wildcardParent = (ParentNodePattern<R>) getRoot();
        patterns.put(patterns.createWildcardRole(), wildcardParent);
    }

    @Override
    public List<Match> recursiveMatch(Node rootCandidate) {
        // rootCandidate is actually candidate for wildcard parent patterns!
        List<Match> matches = new ArrayList<>();
        matches.add(new Match(this));

        this.wildcardParent.recursiveMatch(rootCandidate, matches);

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
     * @param <R> the child node type
     */
    public static class ParentNodePattern<R extends Node> extends NodePatternImpl<Node> {
        private final NodePattern<R> childPattern;
        private final List<IEdge<? extends Node, ? super R>> edgesToType;

        /**
         * Creates a new {@link ParentNodePattern} for the given child {@link NodePattern}.
         * @param tClass The {@link Node} type class of the child
         * @param child the child node pattern
         */
        public ParentNodePattern(Class<R> tClass, NodePattern<R> child) {
            super(Node.class);
            this.childPattern = child;

            Edge<R> edge;
            if (Objects.isNull(child)) {
                edgesToType = null;
                return;
            }

            edge = new Edge<>(tClass);
            this.addRelation(new RelatedNode<>(child, edge));
            edgesToType = new ArrayList<>();
            Edges.getEdgesToType(tClass, e -> edgesToType.addLast(e));
        }

        @Override
        public void recursiveMatch(Node node, List<Match> matches) {

            // This node should match if it has a fitting edge and child
            List<Match> resultMatches = edgesToType.stream().filter(e -> e.getSourceClass().isAssignableFrom(node.getClass())).map(e -> {
                List<Match> matchesCopy = new ArrayList<>(matches.stream().map(Match::copy).toList());
                matchesCopy.forEach(match -> match.register(this, node));
                wildCardMatch(e, node, matchesCopy);
                return matchesCopy;
            }).flatMap(List::stream).toList();
            matches.clear();
            matches.addAll(resultMatches);
        }

        /**
         * Checks for a match of the wild card pattern starting with the parent node.
         * @param <T> The parent {@link Node} type
         * @param e The edge from the parent to the child
         * @param parent the parent {@link Node}
         * @param matches the current set of open matches
         */
        private <T extends Node> void wildCardMatch(IEdge<T, ? super R> e, Node parent, List<Match> matches) {
            T from = (T) parent;
            if (e instanceof CpgEdge<T, ? super R> singleEdge) {
                Node target = singleEdge.getRelated(from);
                if (Objects.isNull(target)) {
                    // target is not part of the graph or empty
                    matches.clear();
                } else {
                    childPattern.recursiveMatch(target, matches);
                    matches.forEach(match -> match.resolveWildcard(this, from, singleEdge));
                }
            } else if (e instanceof CpgMultiEdge<T, ? super R> multiEdge) {
                List<? extends Node> targets = multiEdge.getAllTargets(from);
                var resultMatches = new ArrayList<Match>();
                for (int i = 0; i < targets.size(); i++) {
                    var matchesCopy = new ArrayList<>(matches.stream().map(Match::copy).toList());
                    Node target = targets.get(i);
                    CpgEdge<T, ? super R> edge = nthElement(multiEdge, i);
                    childPattern.recursiveMatch(target, matchesCopy);
                    matchesCopy.forEach(match -> match.resolveWildcard(this, from, edge));
                    resultMatches.addAll(matchesCopy);
                }
                matches.clear();
                matches.addAll(resultMatches);
            }
        }

        @Override
        public List<Class<? extends Node>> getCandidateClasses() {
            return edgesToType.stream().map(IEdge::getSourceClass).collect(Collectors.toCollection(ArrayList::new));
        }

        public NodePattern<R> getChildPattern() {
            return childPattern;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;
            if (!super.equals(o))
                return false;

            ParentNodePattern<?> that = (ParentNodePattern<?>) o;

            if (getChildPattern() != null ? !getChildPattern().equals(that.getChildPattern()) : that.getChildPattern() != null)
                return false;
            return Objects.equals(edgesToType, that.edgesToType);
        }

        @Override
        public int hashCode() {
            int result = super.hashCode();
            result = 31 * result + (getChildPattern() != null ? getChildPattern().hashCode() : 0);
            result = 31 * result + (edgesToType != null ? edgesToType.hashCode() : 0);
            return result;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        WildcardGraphPattern<?> that = (WildcardGraphPattern<?>) o;

        return Objects.equals(wildcardParent, that.wildcardParent);
    }

    @Override
    public int hashCode() {
        return wildcardParent != null ? wildcardParent.hashCode() : 0;
    }

    /**
     * This models a wildcard edge unknown at creation time, of which the target is an R node.
     * @param <R> the related node type
     */
    public static class Edge<R extends Node> extends CpgEdge<Node, R> {

        private Edge(Class<R> tClass) {
            super(null, null, AST);
            this.setRelatedClass(tClass);
        }

        @Override
        public boolean isEquivalentTo(IEdge<?, ?> other) {
            // Wildcard edges should always be equivalent.
            return other.getClass().equals(this.getClass());
        }

    }

}
