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
 * Contains convenience methods to create elements of
 * {@link de.jplag.java_cpg.transformation.matching.pattern.GraphPattern}s and
 * {@link de.jplag.java_cpg.transformation.matching.pattern.NodePattern}s.
 */
public class PatternUtil {

    private PatternUtil() {
        // should not be instantiated
    }

    /**
     * Creates a Predicate that checks if a node has a non-null related Node via the given edge.
     * @param edge the edge
     * @param <S> the source node type
     * @param <T> the target node type
     * @return the predicate
     */
    public static <S extends Node, T extends Node> Predicate<S> notNull(CpgEdge<S, T> edge) {
        return s -> !Objects.isNull(edge.getRelated(s));
    }

    /**
     * Creates a {@link Predicate} property for an edge that specifies that its target shall not be null.
     * @param edge the edge
     * @param <S> the source node type
     * @param <P> the target property type
     * @return the predicate
     */
    public static <S extends Node, P> Predicate<S> notNull(CpgAttributeEdge<S, P> edge) {
        return s -> !Objects.isNull(edge.getter().apply(s));
    }

    /**
     * Creates a {@link Predicate} property for an edge that specifies that it target shall not be an instant of the given
     * type.
     * @param edge the edge
     * @param clazz the concrete node class
     * @param <S> the source node type
     * @param <T> the target node type as specified by the edge
     * @param <C> the concrete target node type
     * @return the predicate
     */
    public static <S extends Node, T extends Node, C extends T> Predicate<S> notInstanceOf(CpgEdge<S, T> edge, Class<C> clazz) {
        return s -> !clazz.isInstance(edge.getter().apply(s));
    }

    /**
     * Creates a proxy for the nth element of a 1:n relation.
     * @param edge the 1:n relation edge
     * @param n the index of the edge
     * @param <S> the source node type
     * @param <T> the target node type
     * @return the nth edge
     */
    public static <S extends Node, T extends Node> CpgEdge<S, T> nthElement(CpgMultiEdge<S, T> edge, int n) {
        return new CpgNthEdge<>(edge, n);
    }

    /**
     * Creates a {@link Predicate} that checks if the related attribute is equal to the given value.
     * @param attributeEdge a function to get the related attribute
     * @param value the value to check against
     * @param <S> the source node type
     * @param <P> the predicate type
     * @return the predicate
     */
    public static <S extends Node, P> Predicate<S> attributeEquals(CpgAttributeEdge<S, P> attributeEdge, P value) {
        return s -> Objects.equals(attributeEdge.get(s), value);
    }

    /**
     * Creates a predicate property for an edge that specifies that its target attribute shall be equal to the given String.
     * @param attributeEdge the attribute edge
     * @param value the required value
     * @param <S> the source node type
     * @param <P> the attribute type
     * @return the predicate
     */
    public static <S extends Node, P> Predicate<S> attributeToStringEquals(CpgAttributeEdge<S, P> attributeEdge, String value) {
        return s -> Objects.equals(attributeEdge.get(s).toString(), value);
    }

    /**
     * Creates a {@link Predicate} property for an edge that specifies that the target attribute as a {@link String} starts
     * with the given {@link String}.
     * @param attributeEdge the attribute edge
     * @param value the required starting substring
     * @param <S> the source node type
     * @param <P> the target attribute type
     * @return the predicate
     */
    public static <S extends Node, P> Predicate<S> attributeToStringStartsWith(CpgAttributeEdge<S, P> attributeEdge, String value) {
        return s -> attributeEdge.get(s).toString().startsWith(value);
    }

    /**
     * Creates a {@link Predicate} property for an edge that specifies that the target attribute list contains the given
     * value.
     * @param attributeEdge the attribute edge
     * @param value the required contained value
     * @param <S> the source node type
     * @param <P> The target attributes type
     * @return the predicate
     */
    public static <S extends Node, P> Predicate<S> attributeContains(CpgAttributeEdge<S, List<P>> attributeEdge, P value) {
        return s -> attributeEdge.get(s).contains(value);
    }

    /**
     * Creates a predicate property for an edge that specifies that the target node list is not empty.
     * @param edge the edge
     * @param <S> the source node type
     * @param <T> the target node type
     * @return the predicate
     */
    public static <S extends Node, T extends Node> Predicate<S> notEmpty(CpgMultiEdge<S, T> edge) {
        return s -> !edge.getAllTargets(s).isEmpty();
    }

    /**
     * Creates a {@link Predicate} property for an edge that specifies that the target node list is empty.
     * @param edge the edge
     * @param <S> the source node type
     * @param <T> the target node type
     * @return the predicate
     */
    public static <S extends Node, T extends Node> Predicate<S> isEmpty(CpgMultiEdge<S, T> edge) {
        return s -> edge.getAllTargets(s).isEmpty();
    }

    /**
     * Creates a {@link Predicate} that checks if the 1:n relation targets exactly the number of nodes specified.
     * @param edge the 1:n edge
     * @param n the number to check against
     * @param <S> the source node type
     * @param <T> the target node type
     * @return the predicate
     */
    public static <S extends Node, T extends Node> Predicate<S> nElements(CpgMultiEdge<S, T> edge, int n) {
        return s -> edge.getAllTargets(s).size() == n;
    }

    /**
     * Creates a {@link Predicate} that checks if the property value is contained in the list of values given.
     * @param getter a function to get the property value
     * @param acceptedValues a list of accepted values
     * @param <S> the source node type
     * @param <P> the property value type
     * @return the predicate
     */
    public static <S extends Node, P> Predicate<S> oneOf(CpgAttributeEdge<S, P> getter, List<P> acceptedValues) {
        return s -> acceptedValues.contains(getter.get(s));
    }

    /**
     * Creates a new {@link de.fraunhofer.aisec.cpg.graph.Node} of the type specified by the given
     * {@link de.jplag.java_cpg.transformation.matching.pattern.NodePattern}.
     * @param pattern the pattern
     * @param <T> the node pattern type
     * @return the new {@link de.fraunhofer.aisec.cpg.graph.Node}
     */
    public static <T extends Node> T instantiate(NodePattern<T> pattern) {
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

            if (candidate instanceof Literal<?>) {
                // Literal is constant
            } else if (candidate instanceof CallExpression call) {
                workList.addAll(call.getArguments());
                if (call instanceof MemberCallExpression memberCall) {
                    workList.add(memberCall.getBase());
                }
            } else if (candidate instanceof Reference ref) {
                references.add(ref.getRefersTo());
            } else {
                workList.addAll(candidate.getPrevDFG());
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
