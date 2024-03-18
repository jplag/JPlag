package de.jplag.java_cpg.transformation.matching.pattern;

import de.fraunhofer.aisec.cpg.graph.Node;

/**
 * A {@link MatchProperty} can be used to represent a property of a {@link Match} involving multiple {@link Node}s and their relations.
 *
 * @param <S> The node type of the node that the property is assigned to
 */
@FunctionalInterface
public interface MatchProperty<S extends Node> {

    boolean test(S s, Match match);
}
