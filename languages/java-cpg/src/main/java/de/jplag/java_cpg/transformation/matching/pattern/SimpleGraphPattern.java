package de.jplag.java_cpg.transformation.matching.pattern;

import java.util.*;
import java.util.function.BiConsumer;

import de.fraunhofer.aisec.cpg.graph.Node;

/**
 * A {@link SimpleGraphPattern} describes the occurrence and relation of {@link Node}s in a Graph and their properties.
 * A SimpleGraphPattern has exactly one root {@link NodePattern}.
 * @param <T> the root {@link Node} type of the graph pattern
 */
public class SimpleGraphPattern<T extends Node> extends GraphPatternImpl {

    private NodePattern<T> root;

    /**
     * Creates a new {@link SimpleGraphPattern} with the given root {@link NodePattern}.
     * @param root the root {@link NodePattern}
     */
    public SimpleGraphPattern(NodePattern<T> root, PatternRegistry patterns) {
        super(patterns);
        this.root = root;
        if (Objects.isNull(representingNode)) {
            // may be null as well, that is allowed (for target graph patterns)
            representingNode = root;
        }
    }

    /**
     * Gets the root {@link NodePattern} of the {@link SimpleGraphPattern}.
     * @return the root
     */
    public NodePattern<T> getRoot() {
        return root;
    }

    @Override
    public List<NodePattern<?>> getRoots() {
        return List.of(root);
    }

    @Override
    public List<Match> match(Map<NodePattern<?>, List<? extends Node>> rootCandidates) {
        return rootCandidates.get(root).stream().map(this::recursiveMatch).flatMap(List::stream).toList();
    }

    /**
     * Checks this {@link SimpleGraphPattern} against the given concrete {@link Node} for {@link Match}es.
     * @param rootCandidate the possible root {@link Node} of {@link Match}es
     * @return the list of {@link Match}es found
     */
    public <C extends Node> List<Match> recursiveMatch(C rootCandidate) {
        List<Match> matches = new ArrayList<>();
        matches.add(new Match(this));
        root.recursiveMatch(rootCandidate, matches, null);
        return matches;
    }

    @Override
    public boolean validate(Match match) {
        Node rootCandidate = match.get(this.root);
        List<Match> matches = recursiveMatch(rootCandidate);
        return matches.stream().anyMatch(match::equals);
    }

    @Override
    public void compareTo(GraphPattern targetPattern, BiConsumer<NodePattern<?>, NodePattern<?>> compareFunction) {
        if (!(targetPattern instanceof SimpleGraphPattern<?> tTarget && Objects.equals(root.getClass(), tTarget.root.getClass()))) {
            throw new RuntimeException(
                    "Invalid Transformation: SimpleGraphPattern %s is incompatible with %s".formatted(this.toString(), targetPattern.toString()));
        }
        compareFunction.accept(this.root, tTarget.getRoot());
    }

    /**
     * Sets the root {@link NodePattern} of this {@link SimpleGraphPattern}.
     * @param rootPattern the root
     */
    protected void setRoot(NodePattern<T> rootPattern) {
        this.root = rootPattern;
    }

}
