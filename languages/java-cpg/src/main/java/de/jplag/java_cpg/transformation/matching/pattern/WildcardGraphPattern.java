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

import static de.jplag.java_cpg.transformation.matching.pattern.PatternUtil.nthElement;

/**
 * This class represents a pattern where the root node's parent is unknown, but involved in a transformation (e.g. the root node is moved/deleted).
 * @param <T> The node type of the child node of the wildcard parent
 */
public class WildcardGraphPattern<T extends Node> extends GraphPattern<Node> {

    private final Class<T> tClass;
    private final ParentNodePattern wildcardParent;
    private final List<IEdge<? extends Node, ? super T>> edgesToType;
    /* package-private */ final static String WILDCARD_PARENT_ID = "wildcardParent";

    /**
     * Creates a new {@link WildcardGraphPattern}.
     *
     * @param tClass            The node type of the child node
     * @param child
     * @param patternByRoleName A mapping of {@link String} IDs to {@link NodePattern}s.
     */
    WildcardGraphPattern(Class<T> tClass, NodePattern<T> child, HashMap<String, NodePattern<?>> patternByRoleName) {
        super(null, patternByRoleName);
        setRoot(new ParentNodePattern(tClass, child));
        this.tClass = tClass;
        this.edgesToType = Edges.getEdgesToType(tClass);
        this.wildcardParent = (ParentNodePattern) root;
        this.patternByRoleName.put(WILDCARD_PARENT_ID, wildcardParent);
        this.roleNameByPattern.put(wildcardParent, WILDCARD_PARENT_ID);
    }

    @Override
    public List<Match<Node>> recursiveMatch(Node rootCandidate) {
        // rootCandidate is actually candidate for wildcard parentPattern pattern!
        List<Match<Node>> matches = new ArrayList<>();
        matches.add(new Match<>(this));

        this.wildcardParent.recursiveMatch(rootCandidate, matches, null);

        return matches;
    }

    @Override
    public List<Class<? extends Node>> getCandidateNodeClasses() {
        return edgesToType.stream().map(IEdge::getFromClass).distinct().collect(Collectors.toList());
    }

    @Override
    public NodePattern<?> getTransformationStart() {
        return currentMatch.getWildcardParent();
    }

    /**
     * Pattern to describe the unknown AST context that a node may appear in.
     */
    public class ParentNodePattern extends NodePattern.NodePatternImpl<Node> {
        private final NodePattern<T> childPattern;
        private final Edge edge;
        private final List<IEdge<? extends Node, ? super T>> edgesToType;

        /**
         * Creates a new {@link ParentNodePattern} for the given {@link NodePattern}.
         * @param tClass The {@link Node} type class of the child
         * @param child 
         */
        public ParentNodePattern(Class<T> tClass, NodePattern<T> child) {
            super(Node.class);
            this.childPattern = child;

            edge = new Edge();
            this.addRelatedNodePattern(child, edge);
            edgesToType = Edges.getEdgesToType(tClass);
        }

        @Override
        public <Root extends Node> void recursiveMatch(Node node, List<Match<Root>> matches, CpgEdge<?,?> incoming) {
            // Root == T
            List<Match<T>> tMatches = matches.stream().map (match -> (Match<T>) match).toList();
            List<Match<T>> resultMatches = new ArrayList<>();
            // This node should match if it has a fitting edge and child
            edgesToType.forEach(e -> {
                if (!e.getFromClass().isAssignableFrom(node.getClass())) {
                    return;
                }
                List<Match<T>> matchesCopy = new ArrayList<>(tMatches.stream().map(Match::copy).toList());
                wildCardMatch(e, node, matchesCopy);

                matchesCopy.forEach(match -> match.register(this, node));
                resultMatches.addAll(matchesCopy);
            });
            matches.clear();
            matches.addAll(resultMatches.stream().map(tMatch -> (Match<Root>) tMatch).toList());
        }

        /**
         * Checks for a match of the wild card pattern starting with the parent node.
         * @param e The edge from the parent to the child
         * @param parent the parent {@link Node}
         * @param matches the current set of open matches
         * @param <S> The parent {@link Node} type
         */
        private <S extends Node> void wildCardMatch(IEdge<S, ? super T> e, Node parent, List<Match<T>> matches) {
            S from = (S) parent;
            if (e instanceof CpgEdge<S, ? super T> singleEdge) {
                Node target = singleEdge.getRelated(from);
                if (Objects.isNull(target)) {
                    // target is not part of the graph or empty
                    matches.clear();
                } else {
                    childPattern.recursiveMatch(target, matches, singleEdge);
                    matches.forEach(match -> match.resolveWildcard(from, singleEdge));
                }
            } else if (e instanceof CpgMultiEdge<S, ? super T> multiEdge) {
                List<? extends Node> targets = multiEdge.getAllTargets(from);
                var resultMatches = new ArrayList<Match<T>>();
                for (int i = targets.size() - 1; i >= 0 ; i--) {
                    var matchesCopy = new ArrayList<>(matches.stream().map(Match::copy).toList());
                    Node target = targets.get(i);
                    CpgEdge<S, ? super T> edge = nthElement(multiEdge, i);
                    childPattern.recursiveMatch(target, matchesCopy, edge);
                    matchesCopy.forEach(match -> match.resolveWildcard(from, edge));
                    resultMatches.addAll(matchesCopy);
                }
                matches.clear();
                matches.addAll(resultMatches);
            }
        }

    }

    /**
     * This models an edge unknown at creation time, of which the target is a T node.
     *
     */
    public class Edge extends CpgEdge<Node, T> {

        private Edge() {
            super(null, null);
        }

        @Override
        public Function<Node, T> getter() {
            CpgEdge<?, ? super T> edge = currentMatch.getWildcardEdge();
            return nodeGetter(edge.getter());
        }

        private <S extends Node> Function<Node, T> nodeGetter(Function<S, ? super T> getter) {
            return (Node s) -> (T) getter.apply((S) s);
        }

        @Override
        public BiConsumer<Node, T> setter() {
            CpgEdge<?, ? super T> edge = currentMatch.getWildcardEdge();
            return nodeSetter(edge.setter());
        }

        @NotNull
        private <S extends Node> BiConsumer<Node, T> nodeSetter(BiConsumer<S, ? super T> setter) {
            return (p, t) -> setter.accept((S) p, t);
        }

        @Override
        public boolean isEquivalentTo(IEdge<Node, ?> other) {
            // Wildcard edges should always be equivalent.
            return other.getClass().equals(this.getClass());
        }

        @Override
        public Class<T> getToClass() {
            return WildcardGraphPattern.this.tClass;
        }

    }


}
