package de.jplag.java_cpg.transformation.matching.pattern;

import de.fraunhofer.aisec.cpg.graph.Node;

@FunctionalInterface
public interface MatchProperty<S extends Node> {

    boolean test(S s, Match match);
}
