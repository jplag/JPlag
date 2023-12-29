package de.jplag.java_cpg.transformation.matching.pattern;

import de.fraunhofer.aisec.cpg.graph.Node;
import de.jplag.java_cpg.transformation.matching.edges.CpgEdge;
import de.jplag.java_cpg.transformation.matching.edges.CpgMultiEdge;
import de.jplag.java_cpg.transformation.matching.edges.CpgNthEdge;
import de.jplag.java_cpg.transformation.matching.edges.CpgPropertyEdge;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;


/**
 *  Contains convenience methods to create elements of {@link GraphPattern}s and {@link NodePattern}s.
 */
public class PatternUtil {
    /**
     * Creates a Predicate that checks if a node has a non-null related Node via the given getter method.
     * @param getter the getter method
     * @return the predicate
     * @param <S> the source node type
     * @param <T> the target node type
     */
    public static <S extends Node, T extends Node> Predicate<S> notNull(CpgEdge<S, T> getter) {
        return s -> !Objects.isNull(getter.getRelated(s));
    }

    /**
     * Creates a proxy for the nth element of a 1:n relation.
     * @param edge the 1:n relation edge
     * @param n the index of the edge
     * @return the nth edge
     * @param <S> the source node type
     * @param <T> the target node type
     */
    public static <S extends Node, T extends Node> CpgEdge<S, T> nthElement(CpgMultiEdge<S, T> edge, int n) {
        return new CpgNthEdge<>(edge, n);
    }

    /**
     * Creates a {@link Predicate} that checks if the related object is equal to the given value.
     * @param propertyGetter a function to get the related object
     * @param value the value to check against
     * @return the predicate
     * @param <S> the source node type
     * @param <P> the predicate type
     */
    public static <S extends Node, P> Predicate<S> areEqual(Function<S, P> propertyGetter, P value) {
        return s -> propertyGetter.apply(s).equals(value);
    }

    /**
     * Creates a {@link Predicate} that checks if the 1:n relation targets exactly the number of nodes specified.
     * @param edge the 1:n edge
     * @param n the number to check against
     * @return the predicate
     * @param <S> the source node type
     * @param <T> the target node type
     */
    public static <S extends Node, T extends Node> Predicate<S> nElements(CpgMultiEdge<S, T> edge, int n) {
        return s -> edge.getAllTargets(s).size() == n;
    }

    /**
     * Creates a {@link Predicate} that checks if the property value is contained in the list of values given.
     * @param getter a function to get the property value
     * @param acceptedValues a list of accepted values
     * @return the predicate
     * @param <S> the source node type
     * @param <P> the property value type
     */
    public static <S extends Node, P> Predicate<S> oneOf(CpgPropertyEdge<S, P> getter, List<P> acceptedValues) {
        return s -> acceptedValues.contains(getter.get(s));
    }

    /**
     * Creates a new {@link Node} of the type specified by the given {@link NodePattern}.
     * @param pattern the pattern
     * @return the new {@link Node}
     * @param <T> the node pattern type
     */
    public static <T extends Node> T instantiate(NodePattern<T> pattern) {
        try {
            // every Node type has a constructor without parameters
            return pattern.getRootClass().getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
}
