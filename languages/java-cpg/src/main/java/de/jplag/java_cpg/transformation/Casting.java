package de.jplag.java_cpg.transformation;

import java.lang.reflect.Type;

import de.fraunhofer.aisec.cpg.graph.Node;
import de.jplag.java_cpg.transformation.matching.edges.CpgEdge;
import de.jplag.java_cpg.transformation.matching.edges.CpgNthEdge;
import de.jplag.java_cpg.transformation.matching.edges.IEdge;
import de.jplag.java_cpg.transformation.matching.pattern.Match;
import de.jplag.java_cpg.transformation.matching.pattern.NodePattern;
import de.jplag.java_cpg.transformation.matching.pattern.WildcardGraphPattern;
import de.jplag.java_cpg.transformation.matching.pattern.relation.Relation;

/**
 * Contains methods for type conversion which are deemed unsafe, but in reality safed by graph invariants.
 */
public class Casting {

    /**
     * Casts a node to the desired type.
     * @param node the node
     * @param <T> the type
     * @return the cast node
     */
    @SuppressWarnings("unchecked")
    public static <T extends Node> T castNode(Node node) {
        return (T) node;
    }

    /**
     * Casts a relation to the desired type.
     * @param relation the relation
     * @param <T> the target node type
     * @param <R> the related node type
     * @return the cast relation
     */
    @SuppressWarnings("unchecked")
    public static <T extends Node, R extends Node> Relation<T, R, ?> castRelation(Relation<?, ?, ?> relation) {
        return (Relation<T, R, ?>) relation;
    }

    /**
     * Casts a node pattern to the desired type.
     * @param pattern the node pattern
     * @param <R> the type
     * @return the cast node pattern
     */
    @SuppressWarnings("unchecked")
    public static <R extends Node> NodePattern<R> castNodePattern(NodePattern<?> pattern) {
        return (NodePattern<R>) pattern;
    }

    /**
     * Casts an edge to the desired related node type.
     * @param edge the edge
     * @param <R> the related node type
     * @return the cast edge
     */
    @SuppressWarnings("unchecked")
    public static <R extends Node> IEdge<? extends Node, ? super R> cast(IEdge<?, ?> edge) {
        return (IEdge<? extends Node, ? super R>) edge;
    }

    /**
     * Casts an edge to the desired type.
     * @param cpgEdge the edge
     * @param <T> the target node type
     * @param <R> the related node type
     * @return the cast edge
     */
    @SuppressWarnings("unchecked")
    public static <T extends Node, R extends Node> CpgNthEdge<T, R> castAnyOfNEdge(CpgEdge<?, ?> cpgEdge) {
        return (CpgNthEdge<T, R>) cpgEdge;
    }

    /**
     * Casts a wildcard match to the desired type.
     * @param wildcardMatch the wildcard match
     * @param <T> the type
     * @return the cast wildcard match
     */
    @SuppressWarnings("unchecked")
    public static <T extends Node> Match.WildcardMatch<?, T> castWildcardMatch(Match.WildcardMatch<?, ?> wildcardMatch) {
        return (Match.WildcardMatch<?, T>) wildcardMatch;
    }

    /**
     * Casts a parent node pattern to the desired type.
     * @param parentPattern the parent pattern
     * @param <T> the target node pattern type
     * @param <R> the related node pattern type
     * @return the cast parent node pattern
     */
    @SuppressWarnings("unchecked")
    public static <T extends Node, R extends Node> WildcardGraphPattern.ParentNodePattern<R> castParentNodePattern(
            NodePattern<? extends T> parentPattern) {
        return (WildcardGraphPattern.ParentNodePattern<R>) parentPattern;
    }

    /**
     * Casts a type to a subtype of the desired type.
     * @param tClass the type
     * @param <T> the supertype
     * @return the cast class
     */
    @SuppressWarnings("unchecked")
    public static <T extends Node> Class<? super T> castType(Type tClass) {
        return (Class<? super T>) tClass;
    }
}
