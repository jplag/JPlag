package de.jplag.java_cpg.visitor;

import java.util.*;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fraunhofer.aisec.cpg.TranslationResult;
import de.fraunhofer.aisec.cpg.graph.Name;
import de.fraunhofer.aisec.cpg.graph.Node;
import de.fraunhofer.aisec.cpg.graph.declarations.*;
import de.fraunhofer.aisec.cpg.graph.statements.expressions.CallExpression;
import de.fraunhofer.aisec.cpg.graph.statements.expressions.Expression;
import de.fraunhofer.aisec.cpg.graph.types.ObjectType;
import de.fraunhofer.aisec.cpg.graph.types.ParameterizedType;
import de.fraunhofer.aisec.cpg.graph.types.Type;
import de.fraunhofer.aisec.cpg.graph.types.UnknownType;
import de.fraunhofer.aisec.cpg.helpers.SubgraphWalker;

/**
 * This class contains methods to put a call graph oriented order on methods.
 */
public class MethodOrderStrategy {
    private static final String START_OF_GENERIC_CLASS = ".*class\\s*\\w+\\s*<.*";
    private static final Logger logger = LoggerFactory.getLogger(MethodOrderStrategy.class);
    private final NodeOrderStrategy nodeOrderStrategy;
    private int index;
    private List<MethodDeclaration> allMethods;

    /**
     * Creates a new {@link MethodOrderStrategy}.
     */
    public MethodOrderStrategy() {
        this.index = 0;
        this.nodeOrderStrategy = new NodeOrderStrategy();
    }

    private static List<MethodDeclaration> getFunctionCandidates(MethodDeclaration methodDeclaration, CallExpression call) {

        RecordDeclaration recordDeclaration = methodDeclaration.getRecordDeclaration();
        if (Objects.isNull(recordDeclaration)) {
            return List.of();
        }
        List<MethodDeclaration> candidates;
        if (methodDeclaration instanceof ConstructorDeclaration constructor) {
            // CPG cannot handle super constructor calls at the moment
            // we have to find a candidate ourselves
            if (call.getCode() != null && call.getCode().startsWith("super")) {
                // already filtered
                return getSuperConstructorCandidates(constructor, call);
            } else {
                candidates = new ArrayList<>(recordDeclaration.getConstructors());
            }
        } else {
            candidates = recordDeclaration.getMethods().stream().filter(m -> m.getName().equals(methodDeclaration.getName()))
                    .collect(Collectors.toCollection(ArrayList::new));
            // add overriding methods
            List.copyOf(candidates).stream().map(MethodDeclaration::getOverriddenBy).flatMap(List::stream).map(MethodDeclaration.class::cast)
                    .forEach(candidates::add);
        }

        return candidates.stream().filter(m -> m.getLocation() != null).filter(m -> m.getBody() != null).filter(m -> hasSignature(m, call)).toList();
    }

    private static boolean hasSignature(MethodDeclaration m, CallExpression call) {
        List<Expression> arguments = call.getArguments();
        List<ParameterDeclaration> parameters = m.getParameters();

        if (arguments.size() < parameters.size())
            return false;
        for (int i = 0; i < parameters.size(); i++) {
            Expression argument = arguments.get(i);
            ParameterDeclaration parameter = parameters.get(i);

            Type argumentType = argument.getType();
            Type parameterType = parameter.getType();
            if (!(argumentType instanceof UnknownType) && !(argumentType instanceof ParameterizedType) && !isGeneric(parameterType)
                    && !isTypeCompatible(argumentType, parameterType))
                return false;

        }
        return true;

    }

    private static boolean isTypeCompatible(Type type, Type target) {
        if (type.isSimilar(target))
            return true;
        if (!(type instanceof ObjectType oType && target instanceof ObjectType oTarget && oType.getRecordDeclaration() != null
                && oTarget.getRecordDeclaration() != null))
            return false;

        Name typeName = oType.getName();
        if (typeName.toString().equals("java.lang.Object") || typeName.getParent() == null)
            return true;
        Name targetName = oTarget.getName();
        if (targetName.toString().equals("java.lang.Object") || targetName.getParent() == null)
            return true;

        List<Type> superTypes = oType.getRecordDeclaration().getSuperTypes();
        List<Type> superTargets = oTarget.getRecordDeclaration().getSuperTypes();

        return superTypes.stream().anyMatch(t -> isTypeCompatible(t, target)) || superTargets.stream().anyMatch(t -> isTypeCompatible(type, t));
    }

    private static boolean isGeneric(Type type) {
        if (type instanceof ParameterizedType)
            return true;

        if (!(type instanceof ObjectType objectType))
            return false;

        // primitive types are subclasses of ObjectType
        if (type.getClass() != ObjectType.class)
            return false;

        // sufficient approximation: type arguments are unqualified -> no parent package
        boolean isTypeArgument = Objects.isNull(objectType.getName().getParent());
        if (isTypeArgument)
            return true;

        RecordDeclaration recordDeclaration = objectType.getRecordDeclaration();
        if (Objects.isNull(recordDeclaration))
            return false;
        String code = recordDeclaration.getCode();
        if (Objects.isNull(code))
            return false;
        String firstLine = code.split("\\{")[0];
        return firstLine.matches(START_OF_GENERIC_CLASS);
    }

    private static List<MethodDeclaration> getSuperConstructorCandidates(MethodDeclaration methodDeclaration, CallExpression call) {
        RecordDeclaration recordDeclaration = methodDeclaration.getRecordDeclaration();
        if (recordDeclaration == null)
            return List.of();

        List<RecordDeclaration> superClassDeclarations = new ArrayList<>();
        superClassDeclarations.add(recordDeclaration);
        do {
            RecordDeclaration superClassDeclaration = superClassDeclarations.removeFirst();
            List<MethodDeclaration> candidates = superClassDeclaration.getConstructors().stream()
                    .filter(constructor -> isImplicitStandardConstructor(constructor) || constructor.getLocation() != null)
                    .filter(constructor -> hasSignature(constructor, call)).map(MethodDeclaration.class::cast).toList();
            if (!candidates.isEmpty()) {
                return candidates;
            }
            Set<RecordDeclaration> superTypes = superClassDeclaration.getSuperTypeDeclarations();
            superClassDeclarations.addAll(superTypes);
        } while (!superClassDeclarations.isEmpty());
        return List.of();
    }

    private static boolean isImplicitStandardConstructor(ConstructorDeclaration constructor) {
        return constructor.getParameters().isEmpty() && constructor.getLocation() == null;
    }

    private void handleNode(Node node, List<MethodDeclaration> callGraphIndex, List<MethodDeclaration> newFunctions) {
        if (!(node instanceof CallExpression call)) {
            return;
        }

        /*
         * Due to the DeclarationHandler/ExpressionHandler and JavaParser not delivering all necessary information, we may need
         * to reconstruct which methods are being referred to here.
         */
        List<MethodDeclaration> invokes = getFunctionCandidatesByName(call);

        invokes.forEach(methodDeclaration -> {
            var candidates = getFunctionCandidates(methodDeclaration, call);
            if (candidates.isEmpty()) {
                logger.warn("No candidate for {}", call);
            }
            candidates.forEach(candidate -> {
                if ((!callGraphIndex.contains(methodDeclaration) || callGraphIndex.indexOf(methodDeclaration) >= index)
                        && !newFunctions.contains(methodDeclaration)) {
                    newFunctions.add(methodDeclaration);
                }
            });
        });

    }

    private List<MethodDeclaration> getFunctionCandidatesByName(CallExpression call) {
        if (Objects.isNull(call.getCallee())) {
            return List.of();
        }
        String methodName = call.getCallee().getName().getLocalName();

        List<MethodDeclaration> candidatesWithSameName = allMethods.stream().filter(method -> method.getName().getLocalName().equals(methodName))
                .toList();
        return candidatesWithSameName.stream().filter(method -> hasSignature(method, call)).toList();
    }

    List<MethodDeclaration> setupMethodCallGraphOrder(TranslationResult result) {
        List<Node> astChildren = SubgraphWalker.INSTANCE.flattenAST(result);
        List<MethodDeclaration> methods = astChildren.stream().filter(MethodDeclaration.class::isInstance).map(MethodDeclaration.class::cast)
                .toList();
        this.allMethods = methods;

        List<MethodDeclaration> mainMethods = methods.stream().filter(NodeOrderStrategy::isMainMethod).toList();
        List<List<MethodDeclaration>> indices = new ArrayList<>();
        mainMethods.forEach(mainMethod -> {
            List<MethodDeclaration> functionIndex = traverseCallGraph(mainMethod);
            indices.add(functionIndex);
        });
        Optional<List<MethodDeclaration>> maybeLargestIndex = indices.stream().max(Comparator.comparing(List::size));
        if (maybeLargestIndex.isPresent()) {
            List<MethodDeclaration> largestIndex = maybeLargestIndex.get();
            allMethods.stream().filter(m -> !largestIndex.contains(m)).filter(m -> m.getLocation() != null)
                    .sorted(Comparator.comparing(m -> m.getLocation().getRegion())).forEach(largestIndex::add);
            return largestIndex;
        }
        return allMethods;
    }

    private List<MethodDeclaration> traverseCallGraph(MethodDeclaration mainMethod) {
        List<MethodDeclaration> callGraphIndex = new ArrayList<>();
        callGraphIndex.add(mainMethod);

        SubgraphWalker.IterativeGraphWalker walker = new SubgraphWalker.IterativeGraphWalker();
        walker.setStrategy(nodeOrderStrategy::getIterator);

        index = 0;
        List<MethodDeclaration> newFunctions = new ArrayList<>();
        walker.registerOnNodeVisit(node -> handleNode(node, callGraphIndex, newFunctions));
        while (index < callGraphIndex.size()) {
            MethodDeclaration next = callGraphIndex.get(index++);
            walker.iterate(next);
            callGraphIndex.removeAll(newFunctions);
            callGraphIndex.addAll(index, newFunctions);
            newFunctions.clear();
        }
        return callGraphIndex;

    }

}
