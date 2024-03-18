package de.jplag.java_cpg.transformation.matching.edges;

import de.fraunhofer.aisec.cpg.graph.Node;
import de.fraunhofer.aisec.cpg.graph.edge.PropertyEdge;
import de.jplag.java_cpg.transformation.matching.pattern.GraphPattern;

/**
 * This serves as an interface to wrap any kind of {@link PropertyEdge}.
 * @param <S>
 * @param <T>
 */
public interface IEdge<S extends Node, T extends Node> {
    /**
     * Sets the class object representing the source {@link Node} type.
     * @param sClass the source {@link Node} class
     */
    void setFromClass(Class<S> sClass);

    /**
     * Sets the class object representing the target {@link Node} type.
     * @param tClass the target {@link Node} class
     */
    void setToClass(Class<T> tClass);

    /**
     * Gets the class object representing the source {@link Node} type.
     * @return the source {@link Node} class
     */
    Class<S> getFromClass();

    /**
     * Gets the class object representing the target {@link Node} type.
     * @return the target {@link Node} class
     */
    Class<T> getToClass();

    /**
     * If true, this edge should be treated as equivalent to this one in the context of stepping through the source and
     * target {@link GraphPattern}s.
     * @param other the edge to check for equivalence
     * @return true if the other edge is equivalent
     */
    boolean isEquivalentTo(IEdge<?, ?> other);

    boolean isAst();

    boolean isAnalytic();

    enum EdgeCategory {
        AST,
        REFERENCE,
        ANALYTIC
    }
}
