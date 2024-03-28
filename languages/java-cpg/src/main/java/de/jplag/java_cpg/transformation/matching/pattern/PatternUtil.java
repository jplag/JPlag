package de.jplag.java_cpg.transformation.matching.pattern;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import de.fraunhofer.aisec.cpg.graph.Node;
import de.fraunhofer.aisec.cpg.graph.declarations.Declaration;
import de.fraunhofer.aisec.cpg.graph.declarations.RecordDeclaration;
import de.fraunhofer.aisec.cpg.graph.statements.expressions.*;
import de.jplag.java_cpg.transformation.TransformationException;
import de.jplag.java_cpg.transformation.matching.edges.CpgAttributeEdge;
import de.jplag.java_cpg.transformation.matching.edges.CpgEdge;
import de.jplag.java_cpg.transformation.matching.edges.CpgMultiEdge;
import de.jplag.java_cpg.transformation.matching.edges.CpgNthEdge;

/**
 * Contains convenience methods to create elements of {@link GraphPattern}s and {@link NodePattern}s.
 */
public class PatternUtil {

    private PatternUtil() {
        // should not be instantiated
    }

    /**
     * Creates a Predicate that checks if a node has a non-null related Node via the given edge.
     * @param edge the edge
     * @param <T> the source node type
     * @param <R> the target node type
     * @return the predicate
     */
    public static <T extends Node, R extends Node> Predicate<T> notNull(CpgEdge<T, R> edge) {
        return s -> !Objects.isNull(edge.getRelated(s));
    }

    /**
     * Creates a {@link Predicate} property for an edge that specifies that its target shall not be null.
     * @param edge the edge
     * @param <T> the source node type
     * @param <P> the target property type
     * @return the predicate
     */
    public static <T extends Node, P> Predicate<T> notNull(CpgAttributeEdge<T, P> edge) {
        return s -> !Objects.isNull(edge.getter().apply(s));
    }

    /**
     * Creates a {@link Predicate} property for an edge that specifies that it target shall not be an instant of the given
     * type.
     * @param edge the edge
     * @param clazz the concrete node class
     * @param <T> the source node type
     * @param <R> the target node type as specified by the edge
     * @param <C> the concrete target node type
     * @return the predicate
     */
    public static <T extends Node, R extends Node, C extends R> Predicate<T> notInstanceOf(CpgEdge<T, R> edge, Class<C> clazz) {
        return s -> !clazz.isInstance(edge.getter().apply(s));
    }

    /**
     * Creates a proxy for the nth element of a 1:n relation.
     * @param edge the 1:n relation edge
     * @param n the index of the edge
     * @param <T> the source node type
     * @param <R> the target node type
     * @return the nth edge
     */
    public static <T extends Node, R extends Node> CpgEdge<T, R> nthElement(CpgMultiEdge<T, R> edge, int n) {
        return new CpgNthEdge<>(edge, n);
    }

    /**
     * Creates a {@link Predicate} that checks if the related attribute is equal to the given value.
     * @param attributeEdge a function to get the related attribute
     * @param value the value to check against
     * @param <T> the source node type
     * @param <P> the predicate type
     * @return the predicate
     */
    public static <T extends Node, P> Predicate<T> attributeEquals(CpgAttributeEdge<T, P> attributeEdge, P value) {
        return t -> Objects.equals(attributeEdge.get(t), value);
    }

    /**
     * Creates a predicate property for an edge that specifies that its target attribute shall be equal to the given String.
     * @param attributeEdge the attribute edge
     * @param value the required value
     * @param <T> the source node type
     * @param <P> the attribute type
     * @return the predicate
     */
    public static <T extends Node, P> Predicate<T> attributeToStringEquals(CpgAttributeEdge<T, P> attributeEdge, String value) {
        return t -> Objects.equals(attributeEdge.get(t).toString(), value);
    }

    /**
     * Creates a {@link Predicate} property for an edge that specifies that the target attribute as a {@link String} starts
     * with the given {@link String}.
     * @param attributeEdge the attribute edge
     * @param value the required starting substring
     * @param <T> the source node type
     * @param <P> the target attribute type
     * @return the predicate
     */
    public static <T extends Node, P> Predicate<T> attributeToStringStartsWith(CpgAttributeEdge<T, P> attributeEdge, String value) {
        return t -> attributeEdge.get(t).toString().startsWith(value);
    }

    /**
     * Creates a {@link Predicate} property for an edge that specifies that the target attribute list contains the given
     * value.
     * @param attributeEdge the attribute edge
     * @param value the required contained value
     * @param <T> the source node type
     * @param <P> The target attributes type
     * @return the predicate
     */
    public static <T extends Node, P> Predicate<T> attributeContains(CpgAttributeEdge<T, List<P>> attributeEdge, P value) {
        return t -> attributeEdge.get(t).contains(value);
    }

    /**
     * Creates a predicate property for an edge that specifies that the target node list is not empty.
     * @param edge the edge
     * @param <T> the source node type
     * @param <R> the target node type
     * @return the predicate
     */
    public static <T extends Node, R extends Node> Predicate<T> notEmpty(CpgMultiEdge<T, R> edge) {
        return t -> !edge.getAllTargets(t).isEmpty();
    }

    /**
     * Creates a {@link Predicate} property for an edge that specifies that the target node list is empty.
     * @param edge the edge
     * @param <T> the source node type
     * @param <R> the target node type
     * @return the predicate
     */
    public static <T extends Node, R extends Node> Predicate<T> isEmpty(CpgMultiEdge<T, R> edge) {
        return t -> edge.getAllTargets(t).isEmpty();
    }

    /**
     * Creates a {@link Predicate} that checks if the 1:n relation targets exactly the number of nodes specified.
     * @param edge the 1:n edge
     * @param n the number to check against
     * @param <T> the source node type
     * @param <R> the target node type
     * @return the predicate
     */
    public static <T extends Node, R extends Node> Predicate<T> nElements(CpgMultiEdge<T, R> edge, int n) {
        return t -> edge.getAllTargets(t).size() == n;
    }

    /**
     * Creates a {@link Predicate} that checks if the property value is contained in the list of values given.
     * @param getter a function to get the property value
     * @param acceptedValues a list of accepted values
     * @param <T> the source node type
     * @param <P> the property value type
     * @return the predicate
     */
    public static <T extends Node, P> Predicate<T> oneOf(CpgAttributeEdge<T, P> getter, List<P> acceptedValues) {
        return t -> acceptedValues.contains(getter.get(t));
    }

    /**
     * Creates a new {@link Node} of the type specified by the given {@link NodePattern}.
     * @param pattern the pattern
     * @param <R> the node pattern type
     * @return the new {@link Node}
     */
    public static <R extends Node> R instantiate(NodePattern<R> pattern) {
        try {
            // every Node type has a constructor without parameters
            return pattern.getRootClass().getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new TransformationException(e);
        }
    }

    /**
     * Computes the list of {@link Node}s that may influence the given {@link Node}'s value.
     * @param node the node
     * @return the nodes with data flow dependencies towards the given node
     */
    public static Set<Node> dfgReferences(Node node) {
        LinkedList<Node> workList = new LinkedList<>(List.of(node));
        Set<Declaration> references = new HashSet<>();
        while (!workList.isEmpty()) {
            Node candidate = workList.pop();

            switch (candidate) {
                case Literal<?> ignored -> {
                    // Literal is constant
                }
                case null -> {
                    // do nothing
                }
                case CallExpression call -> {
                    workList.addAll(call.getArguments());
                    if (call instanceof MemberCallExpression memberCall) {
                        workList.add(memberCall.getBase());
                    }
                }
                case Reference ref -> references.add(ref.getRefersTo());
                default -> workList.addAll(candidate.getPrevDFG());
            }
        }
        return references.stream().flatMap(decl -> decl.getPrevDFG().stream()).collect(Collectors.toSet());
    }

    /**
     * Creates a {@link Predicate} property that specifies that the given expression is constant.
     * @return the predicate
     */
    public static Predicate<Expression> isConstant() {
        return expression -> expression instanceof Literal<?>;
    }

    /**
     * Creates a {@link Predicate} property that specifies that the given {@link Expression} is a field reference.
     * @return the predicate
     */
    public static Predicate<Expression> isFieldReference() {
        return expression -> {
            if (!(expression instanceof MemberExpression fieldAccess)) {
                return false;
            }
            if (!(fieldAccess.getBase() instanceof Reference thisOrClassReference))
                return false;
            return thisOrClassReference.getName().toString().equals("this") || thisOrClassReference.getRefersTo() instanceof RecordDeclaration;
        };
    }

    /**
     * Creates a {@link Predicate} property that specifies that either of the given predicates must hold.
     * @param predicate1 the first predicate
     * @param predicate2 the second predicate
     * @param <T> the target node type
     * @return the predicate
     */
    public static <T extends Node> Predicate<T> or(Predicate<T> predicate1, Predicate<T> predicate2) {
        return t -> predicate1.test(t) || predicate2.test(t);
    }

    /**
     * Computes a brief description for a {@link Node}.
     * @param node the node
     * @return the description
     */
    public static String desc(Node node) {
        node.getName();
        return "%s(%s%s)".formatted(node.getClass().getSimpleName(),
                node.getName().getLocalName().isEmpty() ? "" : "\"" + node.getName().getLocalName() + "\", ",
                Objects.requireNonNullElse(node.getLocation(), "<no location>"));
    }
}
