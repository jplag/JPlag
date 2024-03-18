package de.jplag.java_cpg.transformation.matching.pattern;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import de.fraunhofer.aisec.cpg.graph.*;
import de.fraunhofer.aisec.cpg.graph.declarations.Declaration;
import de.fraunhofer.aisec.cpg.graph.declarations.MethodDeclaration;
import de.fraunhofer.aisec.cpg.graph.declarations.RecordDeclaration;
import de.fraunhofer.aisec.cpg.graph.declarations.ValueDeclaration;
import de.fraunhofer.aisec.cpg.graph.statements.*;
import de.fraunhofer.aisec.cpg.graph.statements.expressions.*;
import de.fraunhofer.aisec.cpg.helpers.SubgraphWalker;
import de.jplag.java_cpg.transformation.matching.edges.CpgEdge;
import de.jplag.java_cpg.transformation.matching.edges.CpgMultiEdge;
import de.jplag.java_cpg.transformation.matching.edges.CpgNthEdge;
import de.jplag.java_cpg.transformation.matching.edges.CpgPropertyEdge;

/**
 * Contains convenience methods to create elements of {@link GraphPattern}s and {@link NodePattern}s.
 */
public class PatternUtil {
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

    public static <S extends Node, P> Predicate<S> notNull(CpgPropertyEdge<S, P> edge) {
        return s -> !Objects.isNull(edge.getter().apply(s));
    }

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
     * Creates a {@link Predicate} that checks if the related object is equal to the given value.
     * @param propertyEdge a function to get the related object
     * @param value the value to check against
     * @param <S> the source node type
     * @param <P> the predicate type
     * @return the predicate
     */
    public static <S extends Node, P> Predicate<S> attributeEquals(CpgPropertyEdge<S, P> propertyEdge, P value) {
        return s -> Objects.equals(propertyEdge.get(s), value);
    }

    public static <S extends Node, P> Predicate<S> attributeToStringEquals(CpgPropertyEdge<S, P> propertyEdge, String value) {
        return s -> Objects.equals(propertyEdge.get(s).toString(), value);
    }

    public static <S extends Node, P> Predicate<S> attributeToStringStartsWith(CpgPropertyEdge<S, P> propertyEdge, String value) {
        return s -> propertyEdge.get(s).toString().startsWith(value);
    }

    public static <S extends Node, P> Predicate<S> attributeContains(CpgPropertyEdge<S, List<P>> propertyEdge, P value) {
        return s -> propertyEdge.get(s).contains(value);
    }

    public static <S extends Node, T extends Node> Predicate<S> notEmpty(CpgMultiEdge<S, T> propertyEdge) {
        return s -> !propertyEdge.getAllTargets(s).isEmpty();
    }

    public static <S extends Node, T extends Node> Predicate<S> isEmpty(CpgMultiEdge<S, T> propertyEdge) {
        return s -> propertyEdge.getAllTargets(s).isEmpty();
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
    public static <S extends Node, P> Predicate<S> oneOf(CpgPropertyEdge<S, P> getter, List<P> acceptedValues) {
        return s -> acceptedValues.contains(getter.get(s));
    }

    /**
     * Creates a new {@link Node} of the type specified by the given {@link NodePattern}.
     * @param pattern the pattern
     * @param <T> the node pattern type
     * @return the new {@link Node}
     */
    public static <T extends Node> T instantiate(NodePattern<T> pattern) {
        try {
            // every Node type has a constructor without parameters
            return pattern.getRootClass().getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

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

    public static Set<Assignment> incomingDeclarations(Node node) {
        Set<Assignment> res = new HashSet<>();
        node.getPrevEOG().forEach(it -> res.addAll(incomingDeclarations(it)));

        if (node instanceof AssignmentHolder assignmentHolder) {
            for (Assignment assignment : assignmentHolder.getAssignments()) {
                res.removeIf(it -> assignment.getTarget().equals(it.getTarget()));
            }
            res.addAll(assignmentHolder.getAssignments());
        }

        return res;
    }

    public static void sortByDfgFlow(MethodDeclaration methodDeclaration) {
        List<Statement> movableStatements = getMovableStatements(methodDeclaration.getBody());
        Map<Declaration, List<Reference>> currentReferences = new HashMap<>();
        Map<Reference, List<Assignment>> possibleValues = new HashMap<>();
        Map<Statement, List<Reference>> statementToReferences = new HashMap<>();
        Set<Node> seen = new HashSet<>();

        List<Node> workList = SubgraphWalker.INSTANCE.getEOGPathEdges(methodDeclaration.getBody()).getExits();
        Statement currentStatement = null;

        while (!workList.isEmpty()) {
            Node node = workList.remove(0);
            if (seen.contains(node))
                continue;

            if (node instanceof Reference reference) {
                currentReferences.computeIfAbsent(reference.getRefersTo(), declaration -> new ArrayList<>()).add(reference);
                // statementToReferences.computeIfAbsent(currentStatement, statement -> new ArrayList<>())
                // .add(reference);
            } else if (node instanceof AssignmentHolder assignmentHolder) {
                for (Assignment assignment : assignmentHolder.getAssignments()) {
                    ValueDeclaration declaration = (ValueDeclaration) assignment.getTarget();
                    List<Reference> valueReferences = currentReferences.getOrDefault(declaration, List.of());
                    valueReferences.forEach(reference -> possibleValues.computeIfAbsent(reference, it -> new ArrayList<>()).add(assignment));
                    valueReferences.clear();
                }
            } else if (node instanceof UnaryOperator operator && operator.getInput() instanceof Reference ref
                    && ref.getAccess().equals(AccessValues.READWRITE)) {
                Assignment assignment = new Assignment(operator, ref, null);

                ValueDeclaration refersTo = (ValueDeclaration) ref.getRefersTo();
                currentReferences.getOrDefault(refersTo, List.of());

            }

            seen.add(node);
            workList.addAll(0, node.getPrevEOG());
        }

    }

    private static List<Statement> getMovableStatements(Statement body) {
        if (body instanceof Block block) {
            return block.getStatements().stream().map(PatternUtil::getMovableStatements).flatMap(List::stream).toList();
        } else if (body instanceof ArgumentHolder holder) {
            if (holder instanceof IfStatement || holder instanceof WhileStatement || holder instanceof DoStatement) {

            }
        }
        return null;
    }

    /**
     * Creates a new {@link Predicate} that checks whether this node contains exactly one exit. This should hold e.g. for
     * non-branching statements, loop statements, and method bodies if they do not contain a return statement other than the
     * last return statement of the method.
     * @return the predicate
     */
    public static Predicate<Node> isEogConfluent() {
        return node -> SubgraphWalker.INSTANCE.getEOGPathEdges(node).getExits().size() == 1;
    }

    public static Predicate<Expression> isConstant() {
        return expression -> expression instanceof Literal<?>;
    }

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

    public static <T extends Node> Predicate<T> or(Predicate<T> predicate1, Predicate<T> predicate2) {
        return t -> predicate1.test(t) || predicate2.test(t);
    }

    public static String desc(Node node) {
        node.getName();
        return "%s(%s%s)".formatted(node.getClass().getSimpleName(),
                node.getName().getLocalName().isEmpty() ? "" : "\"" + node.getName().getLocalName() + "\", ",
                Objects.requireNonNullElse(node.getLocation(), "<no location>"));
    }
}
