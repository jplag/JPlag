package de.jplag.java_cpg.transformation.matching.edges;

import de.fraunhofer.aisec.cpg.graph.Node;
import de.fraunhofer.aisec.cpg.graph.edge.PropertyEdge;
import de.jplag.java_cpg.transformation.matching.pattern.GraphPattern;

/**
 * This serves as an interface to wrap any kind of {@link PropertyEdge}.
 * @param <S> the source node type
 * @param <T> the target node type
 */
public interface IEdge<S extends Node, T extends Node> {

    /**
     * Sets the class object representing the source {@link Node} type.
     * @param sourceClass the source {@link Node} class
     */
    void setSourceClass(Class<S> sourceClass);

    /**
     * Sets the class object representing the target {@link Node} type.
     * @param targetClass the target {@link Node} class
     */
    void setTargetClass(Class<T> targetClass);

    /**
     * Gets the class object representing the source {@link Node} type.
     * @return the source {@link Node} class
     */
    Class<S> getSourceClass();

    /**
     * Gets the class object representing the target {@link Node} type.
     * @return the target {@link Node} class
     */
    Class<T> getTargetClass();

    /**
     * If true, this edge should be treated as equivalent to this one in the context of stepping through the source and
     * target {@link GraphPattern}s.
     * @param other the edge to check for equivalence
     * @return true if the other edge is equivalent
     */
    boolean isEquivalentTo(IEdge<?, ?> other);

    /**
     * If true, this is an {@link EdgeCategory#AST} edge.
     * @return true iff this is an AST edge
     */
    boolean isAst();

    /**
     * If true, this is an {@link EdgeCategory#ANALYTIC} edge.
     * @return true iff this is an analytic edge
     */
    boolean isAnalytic();

    /**
     * If true, this is an {@link EdgeCategory#REFERENCE} edge.
     * @return true iff this is a reference edge
     */
    boolean isReference();

    enum EdgeCategory {
        /**
         * An edge that represents the inherent structure of the code.
         */
        AST,

        /**
         * An edge that represents that nodes are connected by reference (e.g. method call -> method definition).
         */
        REFERENCE,

        /**
         * An edge that represents a connection that is the result of a calculation. These edges can never be set.
         */
        ANALYTIC
    }
}
