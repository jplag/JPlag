package de.jplag.java_cpg.transformation.matching.edges;

import java.util.function.BiConsumer;
import java.util.function.Function;

import de.fraunhofer.aisec.cpg.graph.Node;

/**
 * This represents the relation to a property, i.e. an object related to a node other than another node, e.g. a String
 * name. To avoid confusion with a {@link de.fraunhofer.aisec.cpg.graph.edge.PropertyEdge}, which is an edge that has
 * properties itself, this is called {@link CpgAttributeEdge}.
 * @param getter function to get the property
 * @param setter function to set the property
 * @param <T> the node type of the source node
 * @param <P> the type of the property
 */
public record CpgAttributeEdge<T extends Node, P>(Function<T, P> getter, BiConsumer<T, P> setter) {

    /**
     * Gets the property related to the given node.
     * @param t the source node
     * @return the property
     */
    public P get(T t) {
        return getter.apply(t);
    }
}
