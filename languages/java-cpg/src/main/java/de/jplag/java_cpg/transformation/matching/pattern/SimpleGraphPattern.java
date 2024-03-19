package de.jplag.java_cpg.transformation.matching.pattern;

import java.util.*;
import java.util.function.BiConsumer;

import de.fraunhofer.aisec.cpg.graph.Node;

/**
 * A {@link de.jplag.java_cpg.transformation.matching.pattern.SimpleGraphPattern} describes the occurrence and relation
 * of {@link de.fraunhofer.aisec.cpg.graph.Node}s in a Graph and their properties. A SimpleGraphPattern has exactly one
 * root {@link de.jplag.java_cpg.transformation.matching.pattern.NodePattern}.
 * @param <T> the root {@link de.fraunhofer.aisec.cpg.graph.Node} type of the graph pattern
 * @author robin
 * @version $Id: $Id
 */
public class SimpleGraphPattern<T extends Node> extends GraphPatternImpl {

    private final NodePattern<T> root;

    /**
     * Creates a new {@link de.jplag.java_cpg.transformation.matching.pattern.SimpleGraphPattern} with the given root
     * {@link de.jplag.java_cpg.transformation.matching.pattern.NodePattern}.
     * @param root the root {@link de.jplag.java_cpg.transformation.matching.pattern.NodePattern}
     * @param patterns a {@link de.jplag.java_cpg.transformation.matching.pattern.PatternRegistry} object
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
     * Gets the root {@link de.jplag.java_cpg.transformation.matching.pattern.NodePattern} of the
     * {@link de.jplag.java_cpg.transformation.matching.pattern.SimpleGraphPattern}.
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
     * Checks this {@link de.jplag.java_cpg.transformation.matching.pattern.SimpleGraphPattern} against the given concrete
     * {@link de.fraunhofer.aisec.cpg.graph.Node} for {@link de.jplag.java_cpg.transformation.matching.pattern.Match}es.
     * @param rootCandidate the possible root {@link de.fraunhofer.aisec.cpg.graph.Node} of
     * {@link de.jplag.java_cpg.transformation.matching.pattern.Match}es
     * @return the list of {@link de.jplag.java_cpg.transformation.matching.pattern.Match}es found
     * @param <C> a C class
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

}
