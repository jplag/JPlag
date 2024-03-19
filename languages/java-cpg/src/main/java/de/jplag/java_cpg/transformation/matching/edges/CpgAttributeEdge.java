package de.jplag.java_cpg.transformation.matching.edges;

import java.util.function.BiConsumer;
import java.util.function.Function;

import de.fraunhofer.aisec.cpg.graph.Node;

/**
 * This represents a property, an object related to a node other than another node, e.g. a String name.
 * @param getter function to get the property
 * @param setter function to set the property
 * @param <S> the node type of the source
 * @param <P> the type of the property
 */
public record CpgAttributeEdge<S extends Node, P>(Function<S, P> getter, BiConsumer<S, P> setter) {

    /**
     * Gets the property related to the given node.
     * @param s the source node
     * @return the property
     */
    public P get(S s) {
        return getter.apply(s);
    }
}
