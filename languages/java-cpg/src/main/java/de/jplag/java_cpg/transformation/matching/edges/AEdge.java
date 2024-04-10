package de.jplag.java_cpg.transformation.matching.edges;

import static de.jplag.java_cpg.transformation.matching.edges.IEdge.EdgeCategory.*;

import de.fraunhofer.aisec.cpg.graph.Node;

/**
 * This abstract class contains the method implementations common to all {@link IEdge}s.
 * @param <T> the source node type
 * @param <R> the related node type
 */
public abstract class AEdge<T extends Node, R extends Node> implements IEdge<T, R> {
    /**
     * The {@link EdgeCategory} of the edge.
     */
    protected final EdgeCategory category;
    private Class<T> sourceClass;
    private Class<R> relatedClass;

    /**
     * Creates a new AEdge of the given category
     * @param category the category
     */
    protected AEdge(EdgeCategory category) {
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
    public Class<T> getSourceClass() {
        return this.sourceClass;
    }

    /**
     * Gets the related node class of this edge.
     * @return the related node class
     */
    public Class<R> getRelatedClass() {
        return relatedClass;
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

    public void setSourceClass(Class<T> sourceClass) {
        this.sourceClass = sourceClass;
    }

    public void setRelatedClass(Class<R> relatedClass) {
        this.relatedClass = relatedClass;
    }
}
