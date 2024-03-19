package de.jplag.java_cpg.transformation.matching.edges;

import static de.jplag.java_cpg.transformation.matching.edges.IEdge.EdgeCategory.*;

import de.fraunhofer.aisec.cpg.graph.Node;

/**
 * This abstract class contains the method implementations common to all {@link IEdge}s.
 * @param <S> the source node type
 * @param <T> the target node type
 */
public abstract class AEdge<S extends Node, T extends Node> implements IEdge<S, T> {
    /**
     * The {@link EdgeCategory} of the edge.
     */
    protected final EdgeCategory category;
    private Class<S> sourceClass;
    private Class<T> targetClass;

    /**
     * Creates a new AEdge of the given category
     * @param category the category
     */
    public AEdge(EdgeCategory category) {
        this.category = category;
    }

    /**
     * Gets the {@link EdgeCategory} of this edge.
     * @return the edge category
     */
    public EdgeCategory getCategory() {
        return category;
    }

    /**
     * Gets the source node class of this edge.
     * @return the source node class
     */
    public Class<S> getSourceClass() {
        return this.sourceClass;
    }

    /**
     * Gets the target node class of this edge.
     * @return the target node class
     */
    public Class<T> getTargetClass() {
        return targetClass;
    }

    /**
     * @return true iff this edge is {@link EdgeCategory#ANALYTIC}.
     */
    public boolean isAnalytic() {
        return category == ANALYTIC;
    }

    /**
     * @return true iff this edge is {@link EdgeCategory#AST}.
     */
    public boolean isAst() {
        return category == AST;
    }

    /**
     * @return true iff this edge is {@link EdgeCategory#REFERENCE}.
     */
    public boolean isReference() {
        return category == REFERENCE;
    }

    public abstract boolean isEquivalentTo(IEdge<?, ?> other);

    /**
     * {@inheritDoc}
     */
    public void setSourceClass(Class<S> sourceClass) {
        this.sourceClass = sourceClass;
    }

    /**
     * {@inheritDoc}
     */
    public void setTargetClass(Class<T> targetClass) {
        this.targetClass = targetClass;
    }
}
