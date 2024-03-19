package de.jplag.java_cpg.transformation.matching.pattern;

import de.fraunhofer.aisec.cpg.graph.Node;

/**
 * A {@link de.jplag.java_cpg.transformation.matching.pattern.MatchProperty} can be used to represent a property of a
 * {@link de.jplag.java_cpg.transformation.matching.pattern.Match} involving multiple
 * {@link de.fraunhofer.aisec.cpg.graph.Node}s and their relations.
 * @param <N> The node type of the node that the property is assigned to
 */
@FunctionalInterface
public interface MatchProperty<N extends Node> {

    /**
     * Tests whether the {@link Node} and the {@link Match} satisfy this {@link MatchProperty}.
     * @param n the node
     * @param match the match
     * @return true iff the match satisfies the match property
     */
    boolean test(N n, Match match);
}
