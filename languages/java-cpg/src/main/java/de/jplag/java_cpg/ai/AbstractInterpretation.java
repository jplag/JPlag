package de.jplag.java_cpg.ai;

import static de.jplag.java_cpg.ai.variables.VariableStore.ANONYMOUS_THIS_NAME;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.checkerframework.dataflow.qual.Impure;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.TestOnly;

import de.fraunhofer.aisec.cpg.graph.BranchingNode;
import de.fraunhofer.aisec.cpg.graph.Name;
import de.fraunhofer.aisec.cpg.graph.Node;
import de.fraunhofer.aisec.cpg.graph.declarations.ConstructorDeclaration;
import de.fraunhofer.aisec.cpg.graph.declarations.Declaration;
import de.fraunhofer.aisec.cpg.graph.declarations.EnumConstantDeclaration;
import de.fraunhofer.aisec.cpg.graph.declarations.FieldDeclaration;
import de.fraunhofer.aisec.cpg.graph.declarations.FunctionDeclaration;
import de.fraunhofer.aisec.cpg.graph.declarations.MethodDeclaration;
import de.fraunhofer.aisec.cpg.graph.declarations.NamespaceDeclaration;
import de.fraunhofer.aisec.cpg.graph.declarations.RecordDeclaration;
import de.fraunhofer.aisec.cpg.graph.declarations.TranslationUnitDeclaration;
import de.fraunhofer.aisec.cpg.graph.declarations.VariableDeclaration;
import de.fraunhofer.aisec.cpg.graph.scopes.TryScope;
import de.fraunhofer.aisec.cpg.graph.statements.AssertStatement;
import de.fraunhofer.aisec.cpg.graph.statements.BreakStatement;
import de.fraunhofer.aisec.cpg.graph.statements.CaseStatement;
import de.fraunhofer.aisec.cpg.graph.statements.CatchClause;
import de.fraunhofer.aisec.cpg.graph.statements.ContinueStatement;
import de.fraunhofer.aisec.cpg.graph.statements.DeclarationStatement;
import de.fraunhofer.aisec.cpg.graph.statements.DefaultStatement;
import de.fraunhofer.aisec.cpg.graph.statements.DoStatement;
import de.fraunhofer.aisec.cpg.graph.statements.EmptyStatement;
import de.fraunhofer.aisec.cpg.graph.statements.ForEachStatement;
import de.fraunhofer.aisec.cpg.graph.statements.ForStatement;
import de.fraunhofer.aisec.cpg.graph.statements.IfStatement;
import de.fraunhofer.aisec.cpg.graph.statements.ReturnStatement;
import de.fraunhofer.aisec.cpg.graph.statements.Statement;
import de.fraunhofer.aisec.cpg.graph.statements.SwitchStatement;
import de.fraunhofer.aisec.cpg.graph.statements.TryStatement;
import de.fraunhofer.aisec.cpg.graph.statements.WhileStatement;
import de.fraunhofer.aisec.cpg.graph.statements.expressions.AssignExpression;
import de.fraunhofer.aisec.cpg.graph.statements.expressions.BinaryOperator;
import de.fraunhofer.aisec.cpg.graph.statements.expressions.Block;
import de.fraunhofer.aisec.cpg.graph.statements.expressions.CastExpression;
import de.fraunhofer.aisec.cpg.graph.statements.expressions.ConditionalExpression;
import de.fraunhofer.aisec.cpg.graph.statements.expressions.ConstructExpression;
import de.fraunhofer.aisec.cpg.graph.statements.expressions.Expression;
import de.fraunhofer.aisec.cpg.graph.statements.expressions.ExpressionList;
import de.fraunhofer.aisec.cpg.graph.statements.expressions.InitializerListExpression;
import de.fraunhofer.aisec.cpg.graph.statements.expressions.LambdaExpression;
import de.fraunhofer.aisec.cpg.graph.statements.expressions.Literal;
import de.fraunhofer.aisec.cpg.graph.statements.expressions.MemberCallExpression;
import de.fraunhofer.aisec.cpg.graph.statements.expressions.MemberExpression;
import de.fraunhofer.aisec.cpg.graph.statements.expressions.NewArrayExpression;
import de.fraunhofer.aisec.cpg.graph.statements.expressions.NewExpression;
import de.fraunhofer.aisec.cpg.graph.statements.expressions.ProblemExpression;
import de.fraunhofer.aisec.cpg.graph.statements.expressions.Reference;
import de.fraunhofer.aisec.cpg.graph.statements.expressions.ShortCircuitOperator;
import de.fraunhofer.aisec.cpg.graph.statements.expressions.SubscriptExpression;
import de.fraunhofer.aisec.cpg.graph.statements.expressions.UnaryOperator;
import de.fraunhofer.aisec.cpg.graph.types.FloatingPointType;
import de.fraunhofer.aisec.cpg.graph.types.FunctionType;
import de.fraunhofer.aisec.cpg.graph.types.HasType;
import de.fraunhofer.aisec.cpg.graph.types.IntegerType;
import de.fraunhofer.aisec.cpg.graph.types.ObjectType;
import de.fraunhofer.aisec.cpg.graph.types.ParameterizedType;
import de.fraunhofer.aisec.cpg.graph.types.PointerType;
import de.fraunhofer.aisec.cpg.graph.types.Type;
import de.jplag.java_cpg.ai.variables.Variable;
import de.jplag.java_cpg.ai.variables.VariableName;
import de.jplag.java_cpg.ai.variables.VariableStore;
import de.jplag.java_cpg.ai.variables.values.BooleanValue;
import de.jplag.java_cpg.ai.variables.values.IJavaObject;
import de.jplag.java_cpg.ai.variables.values.IValue;
import de.jplag.java_cpg.ai.variables.values.JavaObject;
import de.jplag.java_cpg.ai.variables.values.NullValue;
import de.jplag.java_cpg.ai.variables.values.Value;
import de.jplag.java_cpg.ai.variables.values.VoidValue;
import de.jplag.java_cpg.ai.variables.values.arrays.IJavaArray;
import de.jplag.java_cpg.ai.variables.values.numbers.INumberValue;
import de.jplag.java_cpg.transformation.operations.DummyNeighbor;
import de.jplag.java_cpg.transformation.operations.TransformationUtil;

/**
 * Abstract Interpretation engine for Java programs. This class is the interface between the CPG Graph and the Abstract
 * Interpretation Data Structures.
 * @author ujiqk
 * @version 1.0
 */
public class AbstractInterpretation {

    // protected static int visitedNodesCounter = 0;
    /**
     * Helper to detect recursive method calls.
     */
    private static final List<Node> lastVisitedMethod = new ArrayList<>();
    private static boolean continueOnError = false;
    /**
     * Recorder for visited lines to detect dead methods/classes later.
     */
    private final VisitedLinesRecorder visitedLinesRecorder;
    /**
     * Helper: if we are recording changes for while loops.
     */
    @NotNull
    private final RecordingChanges recordingChanges;
    private List<IValue> returnStorage;
    private boolean removeDeadCode;
    /**
     * Helper stack to work around cpg limitations.
     */
    private List<Node> lastVisitedLoopOrIf;
    /**
     * Stack for EOG traversal.
     */
    @NotNull
    private ArrayList<Node> nodeStack;
    /**
     * Stack for values during EOG traversal.
     */
    @NotNull
    private ArrayList<IValue> valueStack;
    /**
     * The scoped variable store for the current scope.
     */
    @NotNull
    private VariableStore variables;
    /**
     * The object this AI engine is currently interpreting.
     */
    private IJavaObject object;
    /**
     * Helper counter for nested if-else statements because cpg does not provide enough information.
     */
    private int ifElseCounter = 0;
    /**
     * Helper to detect if we are currently in a constructor. ConstructExpressions behave differently inside constructors.
     */
    private boolean inConstructor = false;
    /**
     * Flag: if we only analyze one method and ignore method calls.
     */
    private boolean methodAnalysisMode = false;

    /**
     * @param visitedLinesRecorder Recorder for visited lines to detect dead methods/classes later.
     * @param removeDeadCode Whether dead code should be removed after the interpretation.
     */
    public AbstractInterpretation(VisitedLinesRecorder visitedLinesRecorder, boolean removeDeadCode) {
        this(visitedLinesRecorder, removeDeadCode, new RecordingChanges(false));
    }

    private AbstractInterpretation(VisitedLinesRecorder visitedLinesRecorder, boolean removeDeadCode, @NotNull RecordingChanges recordingChanges) {
        variables = new VariableStore();
        nodeStack = new ArrayList<>();
        valueStack = new ArrayList<>();
        lastVisitedLoopOrIf = new ArrayList<>();
        returnStorage = new ArrayList<>();
        this.visitedLinesRecorder = visitedLinesRecorder;
        this.removeDeadCode = removeDeadCode;
        this.recordingChanges = recordingChanges;
    }

    private AbstractInterpretation(VisitedLinesRecorder visitedLinesRecorder, boolean removeDeadCode, @NotNull RecordingChanges recordingChanges,
            @NotNull VariableName relatedClassName) {
        this(visitedLinesRecorder, removeDeadCode, recordingChanges);
        variables.setThisName(relatedClassName);
    }

    protected static IJavaObject createNewObject(@NotNull ConstructExpression ce) {
        IJavaObject newObject;
        String name = ce.getType().getName().toString();
        name = name.split("<")[0]; // remove generics
        switch (name) {
            case "java.lang.String" -> {
                newObject = Value.getNewStringValue();
            }
            case "java.util.HashMap", "java.util.Map" -> newObject = new de.jplag.java_cpg.ai.variables.objects.HashMap();
            case "java.util.HashSet", "java.util.Set", "java.util.TreeSet" -> newObject = new de.jplag.java_cpg.ai.variables.objects.HashSet();
            case "java.util.Scanner" -> newObject = new de.jplag.java_cpg.ai.variables.objects.Scanner();
            case "java.util.ArrayList", "java.util.List", "java.util.Vector", "java.util.LinkedList", "java.util.PriorityQueue", "java.util.Deque", "java.util.Stack", "java.util.ListIterator", "java.util.Queue" -> {
                de.jplag.java_cpg.ai.variables.Type innerCpgType;
                if (ce.getType() instanceof ObjectType objectType) {
                    if (objectType.getGenerics().isEmpty()) {
                        innerCpgType = new de.jplag.java_cpg.ai.variables.Type(de.jplag.java_cpg.ai.variables.Type.TypeEnum.UNKNOWN);
                    } else {
                        assert objectType.getGenerics().size() == 1;
                        Type innerType = objectType.getGenerics().getFirst();
                        innerCpgType = de.jplag.java_cpg.ai.variables.Type.fromCpgType(innerType);
                    }
                } else {
                    throw new IllegalStateException("Expected parameterized type for collection, but got " + ce.getType().getClass());
                }
                newObject = Value.getNewArayValue(innerCpgType);
            }
            case "java.util.Comparator" -> throw new JavaLanguageFeatureNotSupportedException(
                    "Comparator objects are not supported in abstract interpretation yet.");
            case "java.lang.StringBuilder" -> newObject = new de.jplag.java_cpg.ai.variables.objects.StringBuilder();
            default -> {
                de.jplag.java_cpg.ai.variables.Type objectType = new de.jplag.java_cpg.ai.variables.Type(
                        de.jplag.java_cpg.ai.variables.Type.TypeEnum.OBJECT, name);
                newObject = new JavaObject(objectType);
            }
        }
        return newObject;
    }

    /**
     * @param object the object this AI engine is currently interpreting.
     */
    public void setRelatedObject(@NotNull IJavaObject object) {
        this.object = object;
    }

    /**
     * Starts the abstract interpretation by running the main method.
     * @param tud TranslationUnitDeclaration graph node representing the whole program.
     * @throws CpgErrorException if the number of classes or namespaces is unexpected.
     */
    public void runMain(@NotNull TranslationUnitDeclaration tud) {
        RecordDeclaration mainClas;
        if (tud.getDeclarations().stream().map(Declaration::getClass).filter(x -> x.equals(NamespaceDeclaration.class)).count() == 1) {
            // Code in a package
            mainClas = tud.getDeclarations().stream().filter(NamespaceDeclaration.class::isInstance).map(NamespaceDeclaration.class::cast).findFirst()
                    .flatMap(ns -> ns.getDeclarations().stream().filter(RecordDeclaration.class::isInstance).map(RecordDeclaration.class::cast)
                            .filter(rd -> rd.getMethods().stream()
                                    .anyMatch(m -> m.getName().getLocalName().equals("main") && m.isStatic() && m.hasBody()))
                            .findFirst())
                    .orElseThrow(() -> new CpgErrorException("No RecordDeclaration with public static main method found in translation unit"));
        } else if (tud.getDeclarations().stream().map(Declaration::getClass).anyMatch(x -> x.equals(RecordDeclaration.class))) {
            // Code without a package
            List<RecordDeclaration> mainClasses = tud.getDeclarations().stream().filter(RecordDeclaration.class::isInstance)
                    .map(RecordDeclaration.class::cast)
                    .filter(rd -> rd.getMethods().stream().anyMatch(m -> m.getName().getLocalName().equals("main") && m.isStatic() && m.hasBody()))
                    .toList();
            if (mainClasses.isEmpty()) {
                throw new CpgErrorException("No RecordDeclaration with public static main method found in translation unit");
            } else if (mainClasses.size() > 1) {
                throw new CpgErrorException("Multiple RecordDeclarations with public static main method found in translation unit");
            }
            mainClas = mainClasses.getFirst();
        } else {
            throw new CpgErrorException("Unexpected number of classes or namespaces in translation unit generated by cpg"
                    + tud.getDeclarations().stream().map(Declaration::getClass).map(Class::getName).toList());
        }
        de.jplag.java_cpg.ai.variables.Type objectType = new de.jplag.java_cpg.ai.variables.Type(de.jplag.java_cpg.ai.variables.Type.TypeEnum.OBJECT,
                mainClas.getName().toString());
        JavaObject mainClassVar = new JavaObject(objectType);
        setupClass(mainClas, mainClassVar);
        assert mainClas.getMethods().stream().map(MethodDeclaration::getName).filter(x -> x.getLocalName().equals("main"))
                .count() == 1 : "Unexpected number of main methods in class " + mainClas.getName() + ": "
                        + mainClas.getMethods().stream().filter(m -> m.getName().getLocalName().equals("main")).count();
        for (MethodDeclaration md : mainClas.getMethods()) {
            if (md.getName().getLocalName().equals("main")) {
                visitedLinesRecorder.recordFirstLineVisited(md);
                // Run main method
                List<Node> eog = md.getNextEOG();
                assert eog.size() == 1;
                variables.newScope();
                variables.addVariable(new Variable(new VariableName("args"),
                        Value.getNewArayValue(new de.jplag.java_cpg.ai.variables.Type(de.jplag.java_cpg.ai.variables.Type.TypeEnum.STRING))));
                graphWalker(eog.getFirst());
                variables.removeScope();
            }
        }
        // ignores include declaration
    }

    /**
     * Sets up the abstract interpretation for the given class and runs its constructor.
     * @param rd RecordDeclaration node representing the class.
     * @param objectInstance the object instance that should represent the class.
     * @param constructorArgs the arguments for the constructor.
     */
    private void runClass(@NotNull RecordDeclaration rd, @NotNull IJavaObject objectInstance, List<IValue> constructorArgs,
            @Nullable ConstructorDeclaration constructor) {
        setupClass(rd, objectInstance);
        visitedLinesRecorder.recordFirstLineVisited(constructor);
        for (Type type : rd.getImplementedInterfaces()) {
            visitedLinesRecorder.recordFirstLineVisited(((ObjectType) type).getRecordDeclaration());
        }
        // Run constructor method
        if (constructor == null) {
            return;
        }
        this.inConstructor = true;
        if (constructor.getBody() == null && constructor.getRecordDeclaration() != null) {     // cpg has lost the method body -> try to restore
            constructor = (ConstructorDeclaration) restoreFunctionDeclaration(constructor.getRecordDeclaration(), constructor,
                    new ArrayList<>(constructor.getRecordDeclaration().getConstructors()));
        }
        List<Node> eog = constructor.getNextEOG();
        if (eog.size() == 1) {
            variables.newScope();
            assert constructor.getParameters().size() == constructorArgs.size();
            for (int i = 0; i < constructorArgs.size(); i++) {
                variables
                        .addVariable(new Variable(new VariableName(constructor.getParameters().get(i).getName().toString()), constructorArgs.get(i)));
            }
            boolean removeDeadCodeBackup = this.removeDeadCode;
            removeDeadCode = false; // do not remove dead code inside constructors as we do not have usage information
            graphWalker(eog.getFirst());
            removeDeadCode = removeDeadCodeBackup;
            variables.removeScope();
        } else if (eog.isEmpty()) {
            // empty constructor -> return
        } else {
            throw new IllegalStateException("Unexpected value: " + eog.size());
        }
        this.inConstructor = false;
    }

    protected void setupObject(@NotNull IJavaObject objectInstance, @NotNull String name) {
        objectInstance.setAbstractInterpretation(this);
        variables.addVariable(new Variable(new VariableName(name), objectInstance));
        variables.setThisName(new VariableName(name));
        variables.addVariable(
                new Variable(de.jplag.java_cpg.ai.variables.objects.Arrays.getName(), new de.jplag.java_cpg.ai.variables.objects.Arrays()));
        variables.addVariable(
                new Variable(de.jplag.java_cpg.ai.variables.objects.Boolean.getName(), new de.jplag.java_cpg.ai.variables.objects.Boolean()));
        variables.addVariable(
                new Variable(de.jplag.java_cpg.ai.variables.objects.Double.getName(), new de.jplag.java_cpg.ai.variables.objects.Double()));
        variables.addVariable(
                new Variable(de.jplag.java_cpg.ai.variables.objects.Integer.getName(), new de.jplag.java_cpg.ai.variables.objects.Integer()));
        variables.addVariable(new Variable(de.jplag.java_cpg.ai.variables.objects.Math.getName(), new de.jplag.java_cpg.ai.variables.objects.Math()));
        variables.addVariable(
                new Variable(de.jplag.java_cpg.ai.variables.objects.Pattern.getName(), new de.jplag.java_cpg.ai.variables.objects.Pattern()));
        variables.addVariable(
                new Variable(de.jplag.java_cpg.ai.variables.objects.Random.getName(), new de.jplag.java_cpg.ai.variables.objects.Random()));
        variables.addVariable(
                new Variable(de.jplag.java_cpg.ai.variables.objects.String.getName(), new de.jplag.java_cpg.ai.variables.objects.String()));
        variables.addVariable(
                new Variable(de.jplag.java_cpg.ai.variables.objects.System.getName(), new de.jplag.java_cpg.ai.variables.objects.System()));
        this.object = objectInstance;
    }

    /**
     * Sets up the abstract interpretation for the given class. Adds fields and methods to the object instance.
     * @param rd RecordDeclaration node representing the class.
     * @param objectInstance the object instance that should represent the class.
     */
    @Impure
    private void setupClass(@NotNull RecordDeclaration rd, @NotNull IJavaObject objectInstance) {
        setupObject(objectInstance, rd.getName().toString());
        visitedLinesRecorder.recordFirstLineVisited(rd);
        setupFieldDeclarations(rd, objectInstance);
        RecordDeclaration currentClass = rd;
        while (true) {
            Set<RecordDeclaration> superClass = currentClass.getSuperTypeDeclarations();
            List<Type> superClassTypes = currentClass.getSuperClasses();
            superClass = superClass.stream()    // necessary to filter out interfaces
                    .filter(x -> superClassTypes.stream().anyMatch(t -> x.toType().equals(t))).collect(java.util.stream.Collectors.toSet());
            if (superClass.isEmpty() || superClassTypes.isEmpty()) {
                break;
            }
            assert superClass.size() == 1 : superClass.size() + " inheritance is not supported in Java.";
            RecordDeclaration superRd = superClass.stream().findFirst().orElseThrow();
            for (Type type : superRd.getImplementedInterfaces()) {
                visitedLinesRecorder.recordFirstLineVisited(((ObjectType) type).getRecordDeclaration());
            }
            setupFieldDeclarations(superRd, objectInstance);
            currentClass = superRd;
        }
    }

    private void setupFieldDeclarations(@NotNull RecordDeclaration rd, @NotNull IJavaObject objectInstance) {
        for (FieldDeclaration fd : rd.getFields()) {
            visitedLinesRecorder.recordLinesVisited(fd);
            Type type = fd.getType();
            Name name = fd.getName();
            if (fd.getInitializer() == null) {      // no initial value
                de.jplag.java_cpg.ai.variables.Type aiType = de.jplag.java_cpg.ai.variables.Type.fromCpgType(type);
                Variable newVar;
                if (aiType.getTypeEnum() == de.jplag.java_cpg.ai.variables.Type.TypeEnum.ARRAY
                        || aiType.getTypeEnum() == de.jplag.java_cpg.ai.variables.Type.TypeEnum.LIST) {
                    IJavaArray arrayValue = Value.getNewArayValue(aiType.getInnerType());
                    newVar = new Variable(new VariableName(name.toString()), arrayValue);
                } else {
                    newVar = new Variable(new VariableName(name.toString()), aiType);
                }
                newVar.setInitialValue();
                objectInstance.setField(newVar);
            } else if (!(fd.getInitializer() instanceof ProblemExpression)) {
                if (fd.getInitializer() instanceof UnaryOperator unop) {
                    assert Objects.equals(unop.getOperatorCode(), "-");
                    IValue value = graphWalker(fd.getNextEOG().getFirst());
                    assert value != null;
                    objectInstance.setField(new Variable(new VariableName(name.toString()), value));
                } else {
                    if (fd.getInitializer() instanceof NewExpression newExpr        // filter out recursive constructor calls
                            && newExpr.getInitializer() instanceof ConstructExpression ce && ce.getConstructor() != null
                            && ce.getConstructor().getRecordDeclaration() != null && ce.getConstructor().getRecordDeclaration().equals(rd)) {
                        objectInstance.setField(new Variable(new VariableName(name.toString()), new VoidValue()));
                    } else {
                        IValue result = graphWalker(fd.getNextEOG().getFirst());
                        assert result != null;
                        objectInstance.setField(new Variable(new VariableName(name.toString()), result));
                    }
                }
            }
        }
    }

    /**
     * Graph walker for EOG traversal. Walks the EOG starting from the given node until the current block ends. If present,
     * returns the value produced by the return statement.
     * @param node the starting graph node.
     * @return the value resulting from the traversal, or null if no value is produced.
     */
    @Nullable
    private IValue graphWalker(@NotNull Node node) {
        if (node instanceof FieldDeclaration || node instanceof RecordDeclaration) {
            IValue value = valueStack.getLast();
            valueStack.removeLast();
            nodeStack.removeLast();
            return value;   // return so that the class setup method can use the graph walker
        }
        List<Node> nextEOG = node.getNextEOG();
        Node nextNode;
        visitedLinesRecorder.recordLinesVisited(node);
        // visitedNodesCounter++;
        // System.out.println(visitedNodesCounter + " " + node);
        switch (node) {
            case VariableDeclaration vd -> {
                nodeStack.add(vd);
                assert nextEOG.size() == 1;
                nextNode = nextEOG.getFirst();
            }
            case Literal<?> l -> {  // adds its value to the value stack
                nodeStack.add(l);
                valueStack.add(Value.valueFactory(l.getValue()));
                if (nextEOG.isEmpty()) {    // when used as a field initializer
                    IValue value = valueStack.getLast();
                    valueStack.removeLast();
                    return value;
                }
                assert nextEOG.size() == 1;
                nextNode = nextEOG.getFirst();
            }
            case MemberExpression me -> {
                walkMemberExpression(me);
                if (nextEOG.isEmpty()) {    // when used as a field initializer
                    IValue value = valueStack.getLast();
                    valueStack.removeLast();
                    return value;
                }
                assert nextEOG.size() == 1 || (nextEOG.size() == 2 && nextEOG.getLast() instanceof ShortCircuitOperator);
                nextNode = nextEOG.getFirst();
            }
            case Reference ref -> {     // adds its value to the value stack
                walkReference(ref);
                nextNode = nextEOG.getFirst();
            }
            case SubscriptExpression se -> nextNode = walkSubscriptExpression(se);
            case MemberCallExpression mce -> {  // adds its value to the value stack
                walkMemberCallExpression(mce);
                if (nextEOG.isEmpty()) {    // when used as a field initializer
                    IValue value = valueStack.getLast();
                    valueStack.removeLast();
                    return value;
                }
                if (nextEOG.size() == 2 && nextEOG.getLast() instanceof ShortCircuitOperator) {
                    nextNode = nextEOG.getFirst();
                } else {
                    assert nextEOG.size() == 1;
                    nextNode = nextEOG.getFirst();
                }
            }
            case DeclarationStatement ds -> {
                walkDeclarationStatement(ds);
                nextNode = nextEOG.getFirst();
            }
            case AssignExpression ae -> nextNode = walkAssignExpression(ae);
            case ShortCircuitOperator scop -> nextNode = walkShortCircuitOperator(scop);
            case BinaryOperator bop -> {
                walkBinaryOperator(bop);
                if (nextEOG.isEmpty()) {    // when used as a field initializer
                    IValue value = valueStack.getLast();
                    valueStack.removeLast();
                    return value;
                }
                assert nextEOG.size() == 1 || (nextEOG.size() == 2 && nextEOG.getLast() instanceof ShortCircuitOperator);
                nextNode = nextEOG.getFirst();
            }
            case UnaryOperator uop -> nextNode = walkUnaryOperator(uop);
            case IfStatement ifStmt -> {
                nextNode = walkIfStatement(ifStmt);
                if (nextNode == null) {
                    return null;
                }
            }
            case SwitchStatement sw -> {
                nextNode = walkSwitchStatement(sw);
                if (nextNode == null) {
                    return new VoidValue(); // ToDo: function return (CropArea:31)
                }
            }
            case CaseStatement _ -> {
                IValue caseValue = valueStack.getLast();
                IValue switchValue = valueStack.getLast();
                if (!Objects.equals(caseValue, switchValue)) {
                    return null;
                }
                assert nextEOG.size() == 1;
                nextNode = nextEOG.getFirst();
            }
            case DefaultStatement _ -> {
                assert nextEOG.size() == 1;
                nextNode = nextEOG.getFirst();
            }
            case Block b -> {
                if (b.getScope() instanceof TryScope) {
                    assert nextEOG.size() == 1;
                    nextNode = nextEOG.getFirst();
                } else {
                    // assert block is exited
                    if (nextEOG.size() == 1) {          // end of if
                        nodeStack.add(nextEOG.getFirst());
                        return null;
                    } else if (nextEOG.isEmpty()) {     // at the end of a while loop or after throw statement
                        nodeStack.add(null);
                        return null;
                    } else {
                        assert false;
                        return null;
                    }
                }
            }
            case ReturnStatement rs -> {
                return walkReturnStatement(rs);
            }
            case ConstructExpression ce -> nextNode = walkConstructExpression(ce);
            case NewExpression ne -> {
                walkNewExpression(ne);
                if (nextEOG.isEmpty() || nextEOG.getFirst() instanceof DummyNeighbor) {    // when used as a field initializer
                    IValue value = valueStack.getLast();
                    valueStack.removeLast();
                    return value;
                }
                assert nextEOG.size() == 1;
                nextNode = nextEOG.getFirst();
            }
            case WhileStatement ws -> {
                nextNode = walkWhileStatement(ws);
                if (nextNode == null) {
                    return null;
                }
            }
            case ForStatement ws -> {
                nextNode = walkForStatement(ws);
                if (nextNode == null) {
                    return null;
                }
            }
            case ForEachStatement fes -> {
                nextNode = walkForEachStatement(fes);
                if (nextNode == null) {
                    return null;
                }
            }
            case InitializerListExpression ile -> {
                IJavaArray list = walkInitializerListExpression(ile);
                if (nextEOG.isEmpty()) {    // when used as a field initializer
                    return list;
                }
                valueStack.add(list);
                nodeStack.add(ile);
                assert nextEOG.size() == 1;
                nextNode = nextEOG.getFirst();
            }
            case NewArrayExpression nae -> {
                walkNewArrayExpression(nae);
                if (nextEOG.isEmpty() || nextEOG.getFirst() instanceof RecordDeclaration) {    // when used as a field initializer
                    IValue value = valueStack.getLast();
                    valueStack.removeLast();
                    return value;
                }
                nodeStack.add(nae);
                assert nextEOG.size() == 1;
                nextNode = nextEOG.getFirst();
            }
            case ConditionalExpression _ -> {
                assert nextEOG.size() == 2;
                if (valueStack.getLast() instanceof VoidValue) {
                    valueStack.removeLast();
                    valueStack.add(new BooleanValue());
                }
                BooleanValue condition = (BooleanValue) valueStack.getLast();
                valueStack.removeLast();
                nodeStack.removeLast();
                // paths have no block statements at the end
                // ToDo
                throw new JavaLanguageFeatureNotSupportedException("ConditionalExpression not supported yet");
            }
            case CatchClause _ -> {
                // nothing for now
                assert nextEOG.size() == 1;
                nextNode = nextEOG.getFirst();
            }
            case TryStatement _ -> {
                // ignore for now
                assert nextEOG.size() == 1;
                nextNode = nextEOG.getFirst();
            }
            case LambdaExpression le -> {
                FunctionDeclaration lambda = le.getFunction();
                // ToDo
                valueStack.add(Value.valueFactory(new de.jplag.java_cpg.ai.variables.Type(de.jplag.java_cpg.ai.variables.Type.TypeEnum.FUNCTION)));
                nodeStack.add(le);
                assert nextEOG.size() == 1;
                nextNode = nextEOG.getFirst();
            }
            case EmptyStatement es -> {
                // occurs, for example, when the while loop body is empty or when ";" is only statement in a line -> we should not
                // return then
                assert nextEOG.size() == 1;
                if (";".equals(es.getCode())) {
                    nextNode = nextEOG.getFirst();
                } else {
                    return null;
                }
            }
            case ExpressionList _ -> {
                // indicates the end of an expression list, for example ("for (i2 = 6, i4 = 4; i2 < j; i2++)"), can be skipped
                assert nextEOG.size() == 1;
                nextNode = nextEOG.getFirst();
            }
            case CastExpression _ -> {
                // ignore casts for now as java types are not tracked precisely yet
                assert nextEOG.size() == 1;
                nextNode = nextEOG.getFirst();
            }
            case AssertStatement _ -> {
                // ignore for now, is technically dead code
                assert nextEOG.size() == 1;
                nextNode = nextEOG.getFirst();
            }
            case ContinueStatement _ -> throw new JavaLanguageFeatureNotSupportedException("ContinueStatement not supported yet");
            case BreakStatement _ -> {
                if (methodAnalysisMode) {
                    assert nextEOG.size() == 1;
                    nodeStack.add(nextEOG.getFirst());
                    return null;
                }
                throw new JavaLanguageFeatureNotSupportedException("BreakStatement not supported yet");
            }
            case DoStatement _ -> throw new JavaLanguageFeatureNotSupportedException("DoStatement not supported yet");
            default -> throw new IllegalStateException("Unexpected value: " + node);
        }
        assert nextNode != null;
        return graphWalker(nextNode);
    }

    private void walkMemberExpression(@NotNull MemberExpression me) {
        de.jplag.java_cpg.ai.variables.Type expectedCpgType = de.jplag.java_cpg.ai.variables.Type.fromCpgType(me.getType());
        if (me.getAssignedTypes().size() != 1) {        // sometimes cpg does not set the type right (mostly with arraylists defined in class)
            expectedCpgType = new de.jplag.java_cpg.ai.variables.Type(de.jplag.java_cpg.ai.variables.Type.TypeEnum.UNKNOWN);
        }
        if (me.getRefersTo() instanceof FieldDeclaration || me.getRefersTo() instanceof EnumConstantDeclaration) {
            if (valueStack.getLast() instanceof IJavaObject javaObject) {
                nodeStack.removeLast();
                // like Reference
                nodeStack.add(me);
                assert me.getName().getParent() != null;
                valueStack.removeLast();    // remove object reference
                IValue result = javaObject.accessField(me.getName().getLocalName(), expectedCpgType);
                result.setParentObject(javaObject);
                valueStack.add(result);
            } else {
                nodeStack.removeLast();
                nodeStack.add(me);
                valueStack.removeLast();    // remove object reference
                Value result = new VoidValue();
                result.setParentObject((IJavaObject) Value
                        .valueFactory(new de.jplag.java_cpg.ai.variables.Type(de.jplag.java_cpg.ai.variables.Type.TypeEnum.OBJECT)));
                valueStack.add(result);
            }
        } else if (me.getRefersTo() instanceof MethodDeclaration) {
            nodeStack.removeLast();
            nodeStack.add(me);
        } else {
            // unknown: look at the last item on the value stack
            IValue value = valueStack.getLast();
            if (value instanceof VoidValue) {
                valueStack.removeLast();    // remove object reference
                valueStack.add(new VoidValue());
            } else {
                if (me.getRefersTo() == null) {
                    throw new CpgErrorException("MemberExpression refers to null");
                }
                throw new IllegalStateException("Unexpected value: " + value);
            }
            nodeStack.removeLast();
            nodeStack.add(me);
        }
    }

    private void walkReference(@NotNull Reference ref) {     // adds its value to the value stack
        de.jplag.java_cpg.ai.variables.Type expectedType = de.jplag.java_cpg.ai.variables.Type.fromCpgType(ref.getType());
        if (ref.getName().getLocalName().equals("this")) {
            valueStack.add(this.object);
        } else if (ref.getRefersTo() instanceof FieldDeclaration fieldDeclaration && fieldDeclaration.getName().toString().equals("Comparator")) {
            throw new JavaLanguageFeatureNotSupportedException("Comparators are not supported yet.");
        } else {
            Variable variable = variables.getVariable(new VariableName(ref.getName().toString()));
            if (variable != null) {
                valueStack.add(Objects.requireNonNull(variables.getVariable(new VariableName(ref.getName().toString()))).getValue());
            } else if (object.accessField(ref.getName().toString(),
                    new de.jplag.java_cpg.ai.variables.Type(de.jplag.java_cpg.ai.variables.Type.TypeEnum.UNKNOWN)) != null) {
                // sometimes cpg does not insert "this".
                IValue value = object.accessField(ref.getName().toString(), expectedType);
                if (value.getType().equals(new de.jplag.java_cpg.ai.variables.Type(de.jplag.java_cpg.ai.variables.Type.TypeEnum.VOID))) {
                    // value isn't known
                    value = Value.valueFactory(de.jplag.java_cpg.ai.variables.Type.fromCpgType(ref.getType()));
                }
                valueStack.add(value);
            } else {    // unknown reference
                assert false;
            }
        }
        nodeStack.add(ref);
        assert ref.getNextEOG().size() == 1 || (ref.getNextEOG().size() == 2 && ref.getNextEOG().getLast() instanceof ShortCircuitOperator);

    }

    private Node walkSubscriptExpression(@NotNull SubscriptExpression se) { // adds its value to the value stack
        assert nodeStack.getLast() instanceof Literal || nodeStack.getLast() instanceof Reference || nodeStack.getLast() instanceof BinaryOperator
                || nodeStack.getLast() instanceof MemberCallExpression || nodeStack.getLast() instanceof UnaryOperator
                || nodeStack.getLast() instanceof SubscriptExpression;
        assert !valueStack.isEmpty();
        if ((valueStack.getLast() instanceof VoidValue)) {
            valueStack.removeLast();
            valueStack.add(Value.valueFactory(new de.jplag.java_cpg.ai.variables.Type(de.jplag.java_cpg.ai.variables.Type.TypeEnum.INT)));
        }
        INumberValue indexLiteral = (INumberValue) valueStack.getLast();
        valueStack.removeLast();    // remove index value
        assert indexLiteral != null;
        IValue ref = valueStack.getLast();
        valueStack.removeLast();    // remove array reference
        if (!(ref instanceof IJavaArray)) {
            // array might not be initialized yet
            ref = Value.valueFactory(de.jplag.java_cpg.ai.variables.Type.fromCpgType(se.getArrayExpression().getType()));
            if (!(ref instanceof IJavaArray)) { // cpg error
                ref = Value.getNewArayValue();
            }
        }
        IValue result = ((IJavaArray) ref).arrayAccess(indexLiteral);
        result.setArrayPosition((IJavaArray) ref, indexLiteral);
        valueStack.add(result);
        nodeStack.removeLast();
        nodeStack.removeLast();
        nodeStack.add(se);
        assert se.getNextEOG().size() == 1 || (se.getNextEOG().size() == 2 && se.getNextEOG().getLast() instanceof ShortCircuitOperator);
        return se.getNextEOG().getFirst();
    }

    private void walkMemberCallExpression(@NotNull MemberCallExpression mce) { // adds its value to the value stack
        IValue result;
        de.jplag.java_cpg.ai.variables.Type expectedType = de.jplag.java_cpg.ai.variables.Type.fromCpgType(mce.getType());
        if (mce.getArguments().isEmpty()) {     // no arguments
            MemberExpression me = (MemberExpression) nodeStack.getLast();
            Name memberName = me.getName();
            if (valueStack.getLast() instanceof VoidValue || valueStack.getLast() instanceof NullValue) {
                // null value can happen: "if (opts.name == null || opts.name.isBlank())" where we dont strictly follow evaluation
                // order.
                valueStack.removeLast();
                de.jplag.java_cpg.ai.variables.Type objectType = new de.jplag.java_cpg.ai.variables.Type(
                        de.jplag.java_cpg.ai.variables.Type.TypeEnum.OBJECT);    // ToDo: insert right object name
                valueStack.add(new JavaObject(new AbstractInterpretation(visitedLinesRecorder, removeDeadCode, recordingChanges, ANONYMOUS_THIS_NAME),
                        objectType));
            }
            if (!(valueStack.getLast() instanceof IJavaObject) && memberName.getLocalName().equals("intValue")) { // special case
                assert valueStack.getLast() instanceof INumberValue;
                result = valueStack.getLast();
            } else if (!(valueStack.getLast() instanceof IJavaObject) && memberName.getLocalName().equals("toString")) {
                result = valueStack.getLast().unaryOperation("toString");
            } else {
                IJavaObject javaObject = (JavaObject) valueStack.getLast();
                if (mce.getInvokes().isEmpty()) {   // CPG sometimes cannot find the method declaration
                    result = Value.valueFactory(de.jplag.java_cpg.ai.variables.Type.fromCpgType(mce.getType()));
                } else {
                    if (!javaObject.hasAbstractInterpretation() && !methodAnalysisMode) {
                        javaObject.setAbstractInterpretation(
                                new AbstractInterpretation(visitedLinesRecorder, removeDeadCode, recordingChanges, ANONYMOUS_THIS_NAME));
                    }
                    result = javaObject.callMethod(memberName.getLocalName(), null, (MethodDeclaration) mce.getInvokes().getLast(), expectedType);
                }
            }
        } else {
            List<IValue> argumentList = new ArrayList<>();
            for (int i = 0; i < mce.getArguments().size(); i++) {
                if (mce.getArguments().get(i) instanceof ProblemExpression) {
                    continue;
                }
                argumentList.add(valueStack.getLast());
                nodeStack.removeLast();
                valueStack.removeLast();
            }
            Collections.reverse(argumentList);
            while (!(nodeStack.getLast() instanceof MemberExpression me)) {
                // necessary for calls like g.inserirLigacao(v1,almax>=lmin && acmax>=cmin && ahmax>=hmin,v2);
                // where the arguments contain operations
                nodeStack.removeLast();
            }
            Name memberName = me.getName();
            assert memberName.getParent() != null;
            assert !valueStack.isEmpty();
            if (valueStack.getLast() instanceof VoidValue) {
                valueStack.removeLast();
                de.jplag.java_cpg.ai.variables.Type objectType = new de.jplag.java_cpg.ai.variables.Type(
                        de.jplag.java_cpg.ai.variables.Type.TypeEnum.OBJECT);    // ToDo: insert right object name
                valueStack.add(new JavaObject(new AbstractInterpretation(visitedLinesRecorder, removeDeadCode, recordingChanges, ANONYMOUS_THIS_NAME),
                        objectType));
            }
            if (!(valueStack.getLast() instanceof IJavaObject) && memberName.getLocalName().equals("equals")) { // special case
                assert argumentList.size() == 1;
                result = valueStack.getLast().binaryOperation("==", argumentList.getFirst());
            } else if (!(valueStack.getLast() instanceof IJavaObject) && memberName.getLocalName().equals("compareTo")) { // special case
                assert argumentList.size() == 1;
                argumentList.add(valueStack.getLast());
                Collections.reverse(argumentList);
                result = new de.jplag.java_cpg.ai.variables.objects.Double().callMethod("compare", argumentList, null, expectedType);
            } else {
                JavaObject javaObject = (JavaObject) valueStack.getLast();
                if (!javaObject.hasAbstractInterpretation() && !methodAnalysisMode) {
                    javaObject.setAbstractInterpretation(
                            new AbstractInterpretation(visitedLinesRecorder, removeDeadCode, recordingChanges, ANONYMOUS_THIS_NAME));
                }
                result = javaObject.callMethod(memberName.getLocalName(), argumentList,
                        (!mce.getInvokes().isEmpty()) ? (MethodDeclaration) mce.getInvokes().getLast() : null, expectedType);
            }
        }
        valueStack.removeLast();    // remove object reference
        if (result == null) {       // if method reference isn't known
            result = Value.valueFactory(de.jplag.java_cpg.ai.variables.Type.fromCpgType(mce.getType()));
        }
        valueStack.add(result);
        nodeStack.removeLast();
        nodeStack.add(mce);
    }

    private void walkDeclarationStatement(@NotNull DeclarationStatement ds) {
        for (int i = ds.getDeclarations().size() - 1; i >= 0; i--) {
            de.jplag.java_cpg.ai.variables.Type expectedType = de.jplag.java_cpg.ai.variables.Type
                    .fromCpgType(((VariableDeclaration) ds.getDeclarations().get(i)).getType());
            if (((VariableDeclaration) ds.getDeclarations().get(i)).getInitializer() == null) {
                Variable newVar = new Variable(new VariableName((ds.getDeclarations().get(i)).getName().toString()),
                        de.jplag.java_cpg.ai.variables.Type.fromCpgType(((VariableDeclaration) ds.getDeclarations().get(i)).getType()));
                newVar.setInitialValue();
                variables.addVariable(newVar);
                nodeStack.removeLast();
            } else {
                if (valueStack.isEmpty()) {
                    valueStack.add(new VoidValue());
                    nodeStack.addLast(new Node());
                }
                if (valueStack.getLast() instanceof IJavaObject javaObject && javaObject.isNull()
                        && expectedType.getTypeEnum() != de.jplag.java_cpg.ai.variables.Type.TypeEnum.OBJECT) {
                    // Fix for missing types in null literals
                    valueStack.removeLast();
                    valueStack.add(Value.valueFactory(expectedType));
                    valueStack.getLast().setInitialValue();
                }
                assert !valueStack.isEmpty();
                Variable variable = new Variable((ds.getDeclarations().get(i)).getName().toString(), valueStack.getLast());
                variables.addVariable(variable);
                nodeStack.removeLast();
                nodeStack.removeLast();
                valueStack.removeLast();
                if (ds.getNextEOG().getFirst() instanceof ForEachStatement) {
                    valueStack.add(variable.getValue());
                }
            }
        }
        assert ds.getNextEOG().size() == 1;
    }

    private Node walkAssignExpression(@NotNull AssignExpression ae) {
        if (!ae.getRhs().isEmpty() && ae.getRhs().getFirst() instanceof ProblemExpression) {
            // cpg does not properly translate x = switch() {...}
            throw new CpgErrorException("CPG error: switch expression is not properly translated, cannot analyze assignment expression");
        }
        assert !valueStack.isEmpty();
        if (ae.getLhs().getFirst() instanceof SubscriptExpression) {
            assert ae.getLhs().size() == 1;
            IValue newValue = valueStack.getLast();
            valueStack.removeLast();
            IValue oldValue = valueStack.getLast();
            // sometimes the value of assign is used after, so don't remove it
            oldValue.getArrayPosition().component1().arrayAssign(oldValue.getArrayPosition().component2(), newValue);
        } else {
            Variable variable = variables.getVariable((nodeStack.get(nodeStack.size() - 2)).getName().toString());
            if (variable == null || nodeStack.get(nodeStack.size() - 2) instanceof MemberExpression) { // class access
                IJavaObject classVal;
                if (nodeStack.get(nodeStack.size() - 2).getName().getParent() == null) {    // this class
                    classVal = variables.getThisObject();
                } else {
                    assert nodeStack.get(nodeStack.size() - 2).getName().getParent() != null;
                    classVal = valueStack.get(valueStack.size() - 2).getParentObject();
                }
                assert classVal != null;
                if (ae.getOperatorCode().equals("+=") || ae.getOperatorCode().equals("-=")) {
                    de.jplag.java_cpg.ai.variables.Type expectedType = new de.jplag.java_cpg.ai.variables.Type(
                            de.jplag.java_cpg.ai.variables.Type.TypeEnum.UNKNOWN);
                    IValue oldValue = classVal.accessField((nodeStack.get(nodeStack.size() - 2)).getName().getLocalName(), expectedType);
                    assert oldValue != null;
                    IValue newValue;
                    if (ae.getOperatorCode().equals("+=")) {
                        newValue = oldValue.binaryOperation("+", valueStack.getLast());
                    } else {
                        newValue = oldValue.binaryOperation("-", valueStack.getLast());
                    }
                    classVal.changeField((nodeStack.get(nodeStack.size() - 2)).getName().getLocalName(), newValue);
                } else {
                    classVal.changeField((nodeStack.get(nodeStack.size() - 2)).getName().getLocalName(), valueStack.getLast());
                }
            } else {
                if (ae.getOperatorCode().equals("+=")) {
                    variable.setValue(variable.getValue().binaryOperation("+", valueStack.getLast()));
                } else if (ae.getOperatorCode().equals("-=")) {
                    variable.setValue(variable.getValue().binaryOperation("-", valueStack.getLast()));
                } else {
                    variable.setValue(valueStack.getLast());
                }
            }
            nodeStack.removeLast();
            // sometimes the value of assign is used after, so don't remove it
        }
        assert ae.getNextEOG().size() == 1;
        return ae.getNextEOG().getFirst();
    }

    private Node walkShortCircuitOperator(@NotNull ShortCircuitOperator scop) {
        assert scop.getPrevEOG().size() == 2;
        if (valueStack.get(valueStack.size() - 2) instanceof VoidValue) {
            valueStack.set(valueStack.size() - 2, new BooleanValue());
        }
        if (!(valueStack.get(valueStack.size() - 2) instanceof BooleanValue)) {
            valueStack.remove(valueStack.size() - 2);
        }
        BooleanValue value1 = (BooleanValue) valueStack.get(valueStack.size() - 2);
        if (valueStack.getLast() instanceof VoidValue) {
            valueStack.removeLast();
            valueStack.add(new BooleanValue());
        }
        BooleanValue value2 = (BooleanValue) valueStack.getLast();
        valueStack.removeLast();
        valueStack.removeLast();
        if (scop.getNextEOG().size() == 1 && scop.getNextEOG().getFirst() instanceof AssignExpression) {
            nodeStack.removeLast();
            nodeStack.removeLast();
        }
        if (Objects.equals(scop.getOperatorCode(), "||")) {
            valueStack.add(value1.binaryOperation("||", value2));
        } else if (Objects.equals(scop.getOperatorCode(), "&&")) {
            valueStack.add(value1.binaryOperation("&&", value2));
        } else {
            throw new JavaLanguageFeatureNotSupportedException(scop.getOperatorCode() + " is not supported in ShortCircuitOperator");
        }
        assert scop.getNextEOG().size() == 1 || scop.getNextEOG().size() == 2;
        return scop.getNextEOG().getFirst();
    }

    private void walkBinaryOperator(@NotNull BinaryOperator bop) {
        assert valueStack.size() >= 2 && !nodeStack.isEmpty();
        String operator = bop.getOperatorCode();
        assert operator != null;
        IValue result = valueStack.get(valueStack.size() - 2).binaryOperation(operator, valueStack.getLast());
        assert nodeStack.size() >= 2;
        nodeStack.removeLast();
        nodeStack.removeLast();
        nodeStack.add(bop);
        valueStack.removeLast();
        valueStack.removeLast();
        valueStack.add(result);
    }

    private Node walkUnaryOperator(@NotNull UnaryOperator uop) {
        assert !valueStack.isEmpty() && !nodeStack.isEmpty();
        String operator = uop.getOperatorCode();
        assert operator != null;
        IValue result = valueStack.getLast().unaryOperation(operator);
        nodeStack.removeLast();
        nodeStack.add(uop);
        valueStack.removeLast();
        valueStack.add(result);
        if (uop.getNextEOG().isEmpty()) {      // when throw is the last statement in a block
            ReturnStatement nextNode = new ReturnStatement();
            nextNode.setReturnValue(new Expression() {
            });
            return nextNode;
        } else {
            assert uop.getNextEOG().size() == 1 || (uop.getNextEOG().size() == 2 && uop.getNextEOG().getLast() instanceof ShortCircuitOperator);
            return uop.getNextEOG().getFirst();
        }
    }

    @Nullable
    private Node walkIfStatement(@NotNull IfStatement ifStmt) {
        Node nextNode;
        List<Node> nextEOG = ifStmt.getNextEOG();
        boolean elsePresent = ifStmt.getElseStatement() != null;
        // detect infinite loops when no Block inserted by cpg
        if (!lastVisitedLoopOrIf.isEmpty() && lastVisitedLoopOrIf.contains(ifStmt)) {
            nodeStack.add(null);
            return null;
        }
        lastVisitedLoopOrIf.addLast(ifStmt);
        if (valueStack.getLast() instanceof VoidValue) {
            valueStack.removeLast();
            valueStack.add(new BooleanValue());
        }
        assert valueStack.getLast() instanceof BooleanValue : "Expected BooleanValue on value stack, but found: " + valueStack.getLast().getClass();
        BooleanValue condition = (BooleanValue) valueStack.getLast();
        valueStack.removeLast();
        boolean runThenBranch = true;
        boolean runElseBranch = true;
        Node thenBlock = ifStmt.getThenStatement(); // not always a block
        Node elseBlock = ifStmt.getElseStatement();
        if (thenBlock == null || nextEOG.getFirst() instanceof DummyNeighbor) {
            runThenBranch = false;
        }
        if (elseBlock == null || nextEOG.getLast() instanceof DummyNeighbor) {
            runElseBranch = false;
        }
        if (condition.getInformation() && !recordingChanges.isRecording()) {
            if (condition.getValue()) {
                runElseBranch = false;
                if (ifStmt.getElseStatement() != null) {
                    visitedLinesRecorder.recordLinesVisited(ifStmt.getElseStatement());
                }
                if (ifStmt.getElseStatement() != null && removeDeadCode) {
                    // Dead code detected -> remove else branch
                    TransformationUtil.disconnectFromPredecessor(nextEOG.getLast());
                    ifStmt.setElseStatement(null);
                }
            } else {
                runThenBranch = false;
                // Dead code detected
                visitedLinesRecorder.recordDetectedDeadLines(ifStmt.getThenStatement());
                if (ifStmt.getElseStatement() == null) {
                    visitedLinesRecorder.recordDetectedDeadLines(ifStmt);
                }
                if (removeDeadCode) {
                    TransformationUtil.disconnectFromPredecessor(nextEOG.getFirst());
                    ifStmt.setThenStatement(null);
                    if (ifStmt.getElseStatement() == null) {
                        TransformationUtil.disconnectFromPredecessor(ifStmt);
                        assert ifStmt.getScope() != null;
                        Block containingBlock = (Block) ifStmt.getScope().getAstNode(); // ToDo: can be other ifStatement and not block
                        // (BoardGame/human/subm304)
                        assert containingBlock != null;
                        List<Statement> statements = containingBlock.getStatements();
                        statements.remove(ifStmt);
                        containingBlock.setStatements(statements);
                    }
                }
            }
        }
        nodeStack.removeLast();     // remove condition
        if (nextEOG.size() != 2) {
            throw new CpgErrorException("Expected 2 statements in if next EOG, found: " + nextEOG.size());
        }
        VariableStore originalVariables = variables;
        VariableStore thenVariables = new VariableStore(variables);
        VariableStore elseVariables = new VariableStore(variables);
        // then statement
        if (runThenBranch) {
            boolean restoreBlock = false;
            if (!(ifStmt.getThenStatement() instanceof Block) && !(ifStmt.getThenStatement() instanceof BranchingNode)) {
                restoreBlock = true;
                Block block = new Block();
                block.setNextEOG(ifStmt.getThenStatement().getNextEOG());
                ifStmt.getThenStatement().setNextEOG(List.of(block));
            }
            variables.newScope();
            graphWalker(nextEOG.getFirst());
            variables.removeScope();
            if (restoreBlock) {
                ifStmt.getThenStatement().setNextEOG(ifStmt.getThenStatement().getNextEOG().getFirst().getNextEOG());
            }
            if (!nodeStack.isEmpty()
                    && (nodeStack.getLast() == null && elseBlock != null && ifStmt.getElseStatement() == null && !elseBlock.getNextEOG().isEmpty())) {
                // special case for dead else branch
                assert elseBlock.getNextEOG().size() == 1;
                nodeStack.add(elseBlock.getNextEOG().getFirst());
            }
            if (nodeStack.isEmpty() || nodeStack.getLast() == null) {
                nodeStack.add(nextEOG.getLast());
            }
        }
        // else statement
        if (runElseBranch) {
            if (ifStmt.getElseStatement() instanceof IfStatement) {  // this loop is a loop with if else
                ifElseCounter++;
            }
            boolean restoreBlock = false;
            if (!(ifStmt.getElseStatement() instanceof Block) && !(ifStmt.getElseStatement() instanceof BranchingNode)) {
                restoreBlock = true;
                Block block = new Block();
                block.setNextEOG(ifStmt.getElseStatement().getNextEOG());
                ifStmt.getElseStatement().setNextEOG(List.of(block));
            }
            if (runThenBranch) {
                variables = elseVariables;
                this.object = variables.getThisObject();
            }
            variables.newScope();
            graphWalker(nextEOG.getLast());
            variables.removeScope();
            if (nodeStack.getLast() == null) {
                nodeStack.add(nextEOG.getFirst());
            }
            if (restoreBlock) {
                ifStmt.getElseStatement().setNextEOG(ifStmt.getElseStatement().getNextEOG().getFirst().getNextEOG());
            }
        }
        // merge branches
        if (runThenBranch && runElseBranch) {
            originalVariables.merge(elseVariables);
        } else if (runThenBranch) {
            if (!condition.getInformation()) {
                thenVariables.merge(originalVariables);
                nodeStack.add(nextEOG.getLast());
            }
        } else if (runElseBranch) {
            if (!condition.getInformation()) {
                originalVariables.merge(elseVariables);
            }
        } else {    // no branch is run
            nodeStack.add(nextEOG.getLast());
        }
        this.object = variables.getThisObject(); // Update object reference
        nextNode = nodeStack.getLast();
        lastVisitedLoopOrIf.remove(ifStmt);
        if (ifElseCounter > 0) {
            ifElseCounter--;
            return null;
        }
        if ((elsePresent || condition.getInformation())
                && (returnStorage.size() >= 2 || (!returnStorage.isEmpty() && (runThenBranch != runElseBranch) && condition.getInformation()))) {
            if (ifStmt.getElseStatement() instanceof IfStatement) { // problem: if else branch is if else
                return nextNode;
            }
            // return in every branch
            valueStack.add(returnStorage.getLast());
            nextNode = new ReturnStatement();
            ((ReturnStatement) nextNode).setReturnValue(new Expression() {
            });
        }
        return nextNode;
    }

    private IValue walkReturnStatement(@NotNull ReturnStatement rs) {
        IValue result;
        if (rs.getReturnValues().isEmpty() || valueStack.isEmpty()) {
            result = new VoidValue();
        } else {
            result = valueStack.getLast();
            valueStack.removeLast();
        }
        if (!lastVisitedLoopOrIf.isEmpty()) {
            // we are inside a loop or if statement
            returnStorage.addLast(result);
        } else {
            // merge other returns
            for (IValue value : returnStorage) {
                if (result instanceof NullValue || result instanceof JavaObject javaObject && javaObject.isNull()) {
                    result = Value.valueFactory(value.getType());
                    result.setInitialValue();
                }
                result.merge(value);
            }
            returnStorage.clear();
        }
        nodeStack.add(null);
        return result;
    }

    @NotNull
    private Node walkConstructExpression(@NotNull ConstructExpression ce) {
        // inside Constructors, no NewExpression nodes come after ConstructExpression nodes
        if (inConstructor && !(ce.getNextEOG().getFirst() instanceof NewExpression)) {
            ConstructorDeclaration constructor = ce.getConstructor();
            if (constructor == null) {
                if (ce.getName().toString().equals("java.lang.Exception") || ce.getName().toString().equals("java.lang.RuntimeException")
                        || ce.getName().toString().equals("java.lang.Throwable")
                        || ce.getName().toString().equals("java.lang.IllegalArgumentException")) {
                    // skip exception
                    assert ce.getNextEOG().size() == 1;
                    return ce.getNextEOG().getFirst();
                }
                throw new CpgErrorException("Constructor not present in ConstructExpression");
            }
            List<Node> eog = constructor.getNextEOG();
            if (!(eog.isEmpty())) { // Constructor has a body
                List<IValue> arguments = new ArrayList<>();
                if (!ce.getArguments().isEmpty()) {
                    int size = ce.getArguments().size();
                    for (int i = 0; i < size; i++) {
                        arguments.add(valueStack.getLast());
                        valueStack.removeLast();
                        nodeStack.removeLast();
                    }
                }
                Collections.reverse(arguments);
                for (int i = 0; i < constructor.getParameters().size(); i++) {
                    variables.addVariable(new Variable(new VariableName(constructor.getParameters().get(i).getName().toString()), arguments.get(i)));
                }
                graphWalker(eog.getFirst());
            }
        } else {
            nodeStack.add(ce);
        }
        assert ce.getNextEOG().size() == 1;
        return ce.getNextEOG().getFirst();
    }

    private Node walkSwitchStatement(@NotNull SwitchStatement sw) { // ToDo delete dead Code in switch
        Node nextNode;
        assert !valueStack.isEmpty();
        VariableStore originalVariables = new VariableStore(variables);
        VariableStore result = null;
        nodeStack.removeLast();
        for (Node branch : sw.getNextEOG()) {
            variables = new VariableStore(originalVariables);
            this.object = variables.getThisObject();
            variables.newScope();
            graphWalker(branch);
            variables.removeScope();
            if (result == null) {
                result = variables;
            } else {
                result.merge(variables);
            }
        }
        variables = result;
        assert variables != null;
        this.object = variables.getThisObject();
        nextNode = nodeStack.getLast();
        if (nextNode instanceof Block block) {  // scoped switch statements have an extra block
            assert block.getNextEOG().size() == 1;
            nextNode = block.getNextEOG().getFirst();
        }
        return nextNode;
    }

    private void walkNewExpression(@NotNull NewExpression ne) {
        ConstructExpression ce = (ConstructExpression) nodeStack.getLast();
        RecordDeclaration classNode = (RecordDeclaration) ce.getInstantiates();
        List<IValue> arguments = new ArrayList<>();
        nodeStack.removeLast();     // remove ConstructExpression
        if (!ce.getArguments().isEmpty()) {
            if (ce.getArguments().stream().anyMatch(ProblemExpression.class::isInstance)) {
                throw new CpgErrorException("ProblemExpression found in constructor arguments");
            }
            int size = ce.getArguments().size();
            for (int i = 0; i < size; i++) {
                arguments.add(valueStack.getLast());
                valueStack.removeLast();
                nodeStack.removeLast();
            }
        }
        Collections.reverse(arguments);
        IJavaObject newObject = createNewObject(ce);
        valueStack.add(newObject);
        if (classNode == null) {        // cpg has not found it, try to restore it
            try {
                ObjectType classType = (ObjectType) ce.getType();
                classNode = classType.getRecordDeclaration();
            } catch (Exception _) {
                // give up
            }
        }
        // run constructor
        if (classNode != null && !methodAnalysisMode) {
            AbstractInterpretation classAi = new AbstractInterpretation(visitedLinesRecorder, removeDeadCode, recordingChanges);
            assert classNode != null;
            classAi.runClass(classNode, newObject, arguments, ce.getConstructor());
        }
        nodeStack.add(ne);
    }

    @Nullable
    private Node walkWhileStatement(@NotNull WhileStatement ws) {
        Node nextNode;
        List<Node> nextEOG = ws.getNextEOG();
        assert nextEOG.size() == 2;
        // detect infinite loops when no Block inserted by cpg
        if (!lastVisitedLoopOrIf.isEmpty() && lastVisitedLoopOrIf.contains(ws)) {
            nodeStack.add(null);
            return null;
        }
        lastVisitedLoopOrIf.addLast(ws);
        // evaluate condition
        if (valueStack.getLast() instanceof VoidValue) {
            valueStack.removeLast();
            valueStack.add(new BooleanValue());
        }
        assert !valueStack.isEmpty() && valueStack.getLast() instanceof BooleanValue;
        BooleanValue condition = (BooleanValue) valueStack.getLast();
        valueStack.removeLast();
        nodeStack.removeLast();
        if (!condition.getInformation() || condition.getValue()) {  // run body if the condition is true or unknown
            if (recordingChanges.isRecording()) {     // higher level loop wants to know which variables change
                variables.recordChanges();
                variables.newScope();
                graphWalker(nextEOG.getFirst());
                variables.removeScope();
                Set<Variable> changedVariables = variables.stopRecordingChanges();
                for (Variable variable : changedVariables) {
                    if (variables.getVariable(variable.getName()) != null) {
                        variables.getVariable(variable.getName()).setToUnknown();
                    }
                }
            } else {
                VariableStore originalVariables = this.variables;
                // 1: first loop run: detect variables that change in loop -> run loop with completely unknown variables + record
                // changes
                this.variables = new VariableStore(variables);
                variables.setEverythingUnknown();
                variables.recordChanges();
                recordingChanges.setRecording(true);
                variables.newScope();
                List<IValue> returnStorageBefore = returnStorage;
                returnStorage = new ArrayList<>();
                graphWalker(nextEOG.getFirst());
                variables.removeScope();
                recordingChanges.setRecording(false);
                Set<Variable> changedVariables = variables.stopRecordingChanges();
                // 2: second loop run with only changed variables unknown
                this.variables = new VariableStore(originalVariables);
                for (Variable variable : changedVariables) {
                    if (variables.getVariable(variable.getName()) != null) {
                        variables.getVariable(variable.getName()).setToUnknown();
                    }
                }
                variables.newScope();
                returnStorage = returnStorageBefore;
                graphWalker(nextEOG.getFirst());
                variables.removeScope();
                // 3: restore variables and set changed variables to unknown
                this.variables = originalVariables;
                for (Variable variable : changedVariables) {
                    if (variables.getVariable(variable.getName()) != null) {
                        Objects.requireNonNull(variables.getVariable(variable.getName())).setToUnknown();
                    }
                }
            }
        } else if (!recordingChanges.isRecording()) {
            // Dead code detected, loop never runs
            if (removeDeadCode) {
                TransformationUtil.disconnectFromPredecessor(nextEOG.getFirst());
                TransformationUtil.disconnectFromPredecessor(ws);
                assert ws.getScope() != null;
                Block containingBlock = (Block) ws.getScope().getAstNode();
                assert containingBlock != null;
                List<Statement> statements = containingBlock.getStatements();
                statements.remove(ws);
                containingBlock.setStatements(statements);
            }
            visitedLinesRecorder.recordDetectedDeadLines(ws);
        }
        // continue with the next node after the while loop
        lastVisitedLoopOrIf.removeLast();
        nextNode = nextEOG.getLast();
        return nextNode;
    }

    @Nullable
    private Node walkForStatement(@NotNull ForStatement ws) {
        Node nextNode;
        List<Node> nextEOG = ws.getNextEOG();
        assert nextEOG.size() == 2;
        // detect infinite loops when no Block inserted by cpg
        if (!lastVisitedLoopOrIf.isEmpty() && lastVisitedLoopOrIf.contains(ws)) {
            nodeStack.add(null);
            return null;
        }
        lastVisitedLoopOrIf.addLast(ws);
        // evaluate condition
        if (valueStack.getLast() instanceof VoidValue) {
            valueStack.removeLast();
            valueStack.add(new BooleanValue());
        }
        assert !valueStack.isEmpty() && valueStack.getLast() instanceof BooleanValue;
        BooleanValue condition = (BooleanValue) valueStack.getLast();
        valueStack.removeLast();
        nodeStack.removeLast();
        if (!condition.getInformation() || condition.getValue()) {  // run body if the condition is true or unknown
            if (recordingChanges.isRecording()) {     // higher level loop wants to know which variables change
                variables.recordChanges();
                variables.newScope();
                graphWalker(nextEOG.getFirst());
                variables.removeScope();
                Set<Variable> changedVariables = variables.stopRecordingChanges();
                for (Variable variable : changedVariables) {
                    variables.getVariable(variable.getName()).setToUnknown();
                }
            } else {
                VariableStore originalVariables = this.variables;
                // 1: first loop run: detect variables that change in loop -> run loop with completely unknown variables + record
                // changes
                this.variables = new VariableStore(variables);
                variables.setEverythingUnknown();
                variables.recordChanges();
                recordingChanges.setRecording(true);
                variables.newScope();
                List<IValue> returnStorageBefore = returnStorage;
                returnStorage = new ArrayList<>();
                graphWalker(nextEOG.getFirst());
                variables.removeScope();
                recordingChanges.setRecording(false);
                Set<Variable> changedVariables = variables.stopRecordingChanges();
                // 2: second loop run with only changed variables unknown
                this.variables = new VariableStore(originalVariables);
                for (Variable variable : changedVariables) {
                    if (variables.getVariable(variable.getName()) != null) {
                        variables.getVariable(variable.getName()).setToUnknown();
                    }
                }
                // for loop special: iteration variable also unknown
                if (ws.getIterationStatement() != null) {
                    Variable iterVar;
                    if (ws.getIterationStatement() instanceof UnaryOperator unaryOperator) {
                        iterVar = variables.getVariable(new VariableName(unaryOperator.getInput().getName().toString()));
                    } else if (ws.getIterationStatement() instanceof AssignExpression assignExpression) {
                        assert assignExpression.getLhs().size() == 1;
                        iterVar = variables.getVariable(new VariableName(assignExpression.getLhs().getFirst().getName().toString()));
                    } else if (ws.getIterationStatement() instanceof ExpressionList) {
                        throw new JavaLanguageFeatureNotSupportedException(" Expression List in for each not supported yet.");
                    } else {
                        throw new IllegalStateException();
                    }
                    if (iterVar != null) {
                        iterVar.setToUnknown();
                    }
                }
                variables.newScope();
                returnStorage = returnStorageBefore;
                graphWalker(nextEOG.getFirst());
                variables.removeScope();
                // 3: restore variables and set changed variables to unknown
                this.variables = originalVariables;
                for (Variable variable : changedVariables) {
                    if (variables.getVariable(variable.getName()) != null) {
                        Objects.requireNonNull(variables.getVariable(variable.getName())).setToUnknown();
                    }
                }
                // for loop special: iteration variable also unknown
                if (ws.getIterationStatement() != null) {
                    Variable iterVar;
                    if (ws.getIterationStatement() instanceof UnaryOperator unaryOperator) {
                        iterVar = variables.getVariable(new VariableName(unaryOperator.getInput().getName().toString()));
                    } else if (ws.getIterationStatement() instanceof AssignExpression assignExpression) {
                        assert assignExpression.getLhs().size() == 1;
                        iterVar = variables.getVariable(new VariableName(assignExpression.getLhs().getFirst().getName().toString()));
                    } else {
                        throw new IllegalStateException();
                    }
                    if (iterVar != null) {
                        iterVar.setToUnknown();
                    }
                }
            }
        } else if (!recordingChanges.isRecording()) {
            // Dead code detected, loop never runs
            if (removeDeadCode) {
                TransformationUtil.disconnectFromPredecessor(nextEOG.getFirst());
                TransformationUtil.disconnectFromPredecessor(ws);
                assert ws.getScope() != null;
                Block containingBlock = (Block) ws.getScope().getAstNode();
                assert containingBlock != null;
                List<Statement> statements = containingBlock.getStatements();
                statements.remove(ws);
                containingBlock.setStatements(statements);
            }
            visitedLinesRecorder.recordDetectedDeadLines(ws);
        }
        // continue with the next node after the for loop
        lastVisitedLoopOrIf.removeLast();
        nextNode = nextEOG.getLast();
        return nextNode;
    }

    @Nullable
    private Node walkForEachStatement(@NotNull ForEachStatement fes) {
        Node nextNode;
        List<Node> nextEOG = fes.getNextEOG();
        assert nextEOG.size() == 2;
        if (!lastVisitedLoopOrIf.isEmpty() && fes == lastVisitedLoopOrIf.getLast()) {
            nodeStack.add(null);
            return null;
        }
        lastVisitedLoopOrIf.addLast(fes);
        assert !valueStack.isEmpty();
        if (valueStack.getLast() instanceof VoidValue) {
            valueStack.removeLast();
            assert fes.getIterable() != null;
            de.jplag.java_cpg.ai.variables.Type arrayType = de.jplag.java_cpg.ai.variables.Type.fromCpgType(((HasType) fes.getIterable()).getType());
            if (arrayType.getTypeEnum() == de.jplag.java_cpg.ai.variables.Type.TypeEnum.ARRAY
                    || arrayType.getTypeEnum() == de.jplag.java_cpg.ai.variables.Type.TypeEnum.LIST) {
                valueStack.add(Value.valueFactory(arrayType));
            } else {
                valueStack.add(Value.valueFactory(new de.jplag.java_cpg.ai.variables.Type(de.jplag.java_cpg.ai.variables.Type.TypeEnum.LIST,
                        new de.jplag.java_cpg.ai.variables.Type(de.jplag.java_cpg.ai.variables.Type.TypeEnum.UNKNOWN))));
            }
        }
        assert valueStack.getLast() instanceof IJavaArray : "Expected array value in for each, but got: " + valueStack.getLast();
        IJavaArray collection = (IJavaArray) valueStack.getLast();
        // ToDo: set right variable value
        valueStack.removeLast();
        assert fes.getVariable() != null;
        String varName = (fes.getVariable().getDeclarations().getFirst()).getName().toString();
        Variable variable1 = new Variable(new VariableName(varName), collection.arrayAccess((INumberValue) Value.valueFactory(0)));
        variables.addVariable(variable1);
        if (collection.accessField("length",
                new de.jplag.java_cpg.ai.variables.Type(de.jplag.java_cpg.ai.variables.Type.TypeEnum.INT)) instanceof INumberValue length
                && length.getInformation() && (length.getValue() == 0)) {
            if (!recordingChanges.isRecording()) {
                // Dead code detected, loop never runs
                if (removeDeadCode) {
                    TransformationUtil.disconnectFromPredecessor(nextEOG.getFirst());
                    TransformationUtil.disconnectFromPredecessor(fes);
                    assert fes.getScope() != null;
                    Block containingBlock = (Block) fes.getScope().getAstNode();
                    assert containingBlock != null;
                    List<Statement> statements = containingBlock.getStatements();
                    statements.remove(fes);
                    containingBlock.setStatements(statements);
                }
                visitedLinesRecorder.recordDetectedDeadLines(fes);
            }
        } else {
            if (recordingChanges.isRecording()) {     // higher level loop wants to know which variables change
                variables.recordChanges();
                variables.newScope();
                graphWalker(nextEOG.getFirst());
                variables.removeScope();
                Set<Variable> changedVariables = variables.stopRecordingChanges();
                for (Variable variable : changedVariables) {
                    Objects.requireNonNull(variables.getVariable(variable.getName())).setToUnknown();
                }
            } else {
                VariableStore originalVariables = this.variables;
                // 1: first loop run: detect variables that change in loop -> run loop with completely unknown variables + record
                // changes
                this.variables = new VariableStore(variables);
                variables.setEverythingUnknown();
                variables.recordChanges();
                recordingChanges.setRecording(true);
                variables.newScope();
                List<IValue> returnStorageBefore = returnStorage;
                returnStorage = new ArrayList<>();
                graphWalker(nextEOG.getFirst());
                variables.removeScope();
                recordingChanges.setRecording(false);
                Set<Variable> changedVariables = variables.stopRecordingChanges();
                // 2: second loop run with only changed variables unknown
                this.variables = new VariableStore(originalVariables);
                for (Variable variable : changedVariables) {
                    if (variables.getVariable(variable.getName()) != null) {
                        Objects.requireNonNull(variables.getVariable(variable.getName())).setToUnknown();
                    }
                }
                variables.newScope();
                returnStorage = returnStorageBefore;
                graphWalker(nextEOG.getFirst());
                variables.removeScope();
                // 3: restore variables and set changed variables to unknown
                this.variables = originalVariables;
                for (Variable variable : changedVariables) {
                    Variable variable2 = variables.getVariable(variable.getName());
                    if (variable2 != null) {
                        variable2.setToUnknown();
                    }
                }
            }
        }
        // continue with the next node after for each
        lastVisitedLoopOrIf.removeLast();
        nextNode = nextEOG.getLast();
        return nextNode;
    }

    @NotNull
    private IJavaArray walkInitializerListExpression(@NotNull InitializerListExpression ile) {
        IJavaArray list;
        if (ile.getInitializers().stream().anyMatch(ProblemExpression.class::isInstance)) { // catch cpg issues
            list = Value.getNewArayValue();
        } else {
            assert valueStack.size() >= ile.getInitializers().size();
            assert nodeStack.size() >= ile.getInitializers().size();
            List<IValue> arguments = new ArrayList<>();
            for (int i = 0; i < ile.getInitializers().size(); i++) {
                nodeStack.removeLast();
                arguments.add(valueStack.getLast());
                valueStack.removeLast();
            }
            list = Value.getNewArayValue(arguments);
        }
        return list;
    }

    private void walkNewArrayExpression(@NotNull NewArrayExpression nae) {
        // either dimension or initializer is present
        if (!nae.getDimensions().isEmpty()) {
            List<INumberValue> dimensions = new ArrayList<>();
            for (int i = 0; i < nae.getDimensions().size(); i++) {
                if (valueStack.getLast() instanceof VoidValue) {
                    dimensions.add((INumberValue) Value
                            .valueFactory(new de.jplag.java_cpg.ai.variables.Type(de.jplag.java_cpg.ai.variables.Type.TypeEnum.INT)));
                } else {
                    dimensions.add((INumberValue) valueStack.getLast());
                }
                valueStack.removeLast();
                nodeStack.removeLast();
            }
            Collections.reverse(dimensions);  // Dimensions are popped in reverse order
            // recover inner type (element type, not array type)
            de.jplag.java_cpg.ai.variables.Type innerType = null;
            if (nae.getTypeObservers().iterator().hasNext()
                    && ((HasType) nae.getTypeObservers().iterator().next()).getType() instanceof PointerType pointerType) {
                de.fraunhofer.aisec.cpg.graph.types.Type elementType = pointerType.getElementType();
                while (elementType instanceof PointerType pt) {
                    elementType = pt.getElementType();
                }
                innerType = de.jplag.java_cpg.ai.variables.Type.fromCpgType(elementType);
            }
            // Build from innermost to outermost
            IJavaArray newArray = Value.getNewArayValue(innerType, dimensions.getLast());
            for (int i = dimensions.size() - 2; i >= 0; i--) {
                INumberValue dimension = dimensions.get(i);
                if (dimension.getInformation()) {
                    List<IValue> innerArrays = new ArrayList<>();
                    for (int j = 0; j < dimension.getValue(); j++) {
                        innerArrays.add(Value.getNewArayValue(innerType, dimensions.get(i + 1)));
                    }
                    newArray = Value.getNewArayValue(innerArrays);
                } else {
                    // Dimension is unknown - create an array with an unknown size
                    newArray = Value.getNewArayValue(
                            new de.jplag.java_cpg.ai.variables.Type(de.jplag.java_cpg.ai.variables.Type.TypeEnum.ARRAY, innerType), dimension);
                    break;
                }
            }
            valueStack.add(newArray);
        } else if (nae.getInitializer() != null) {
            if (nae.getPrevEOG().getFirst() instanceof InitializerListExpression) {
                // initializer has already been processed
                assert valueStack.getLast() instanceof IJavaArray;
                assert nodeStack.getLast() instanceof InitializerListExpression;
            } else {
                throw new IllegalStateException("Unexpected value: " + nae);
            }
        } else {
            throw new IllegalStateException("Unexpected value: " + nae);
        }
    }

    @NotNull
    @TestOnly
    protected VariableStore getVariables() {
        return variables;
    }

    private @NotNull FunctionDeclaration restoreFunctionDeclaration(RecordDeclaration recordDeclaration, FunctionDeclaration original,
            List<FunctionDeclaration> candidates) {
        try {
            assert recordDeclaration != null;
            // match on name and arguments
            for (FunctionDeclaration candidate : candidates) {
                if (candidate.getName().getLocalName().equals(original.getName().getLocalName())
                        && candidate.getParameters().size() == original.getParameters().size()) {
                    boolean parametersMatch = true;
                    for (int i = 0; i < original.getParameters().size(); i++) {
                        Type candidateType = candidate.getParameters().get(i).getType();
                        Type methodType = original.getParameters().get(i).getType();
                        // some of them can be wrong, but this is necessary
                        boolean typesMatch = candidateType.equals(methodType)
                                || (candidateType instanceof ParameterizedType && methodType instanceof ObjectType)
                                || (candidateType instanceof ObjectType && methodType instanceof ObjectType
                                        && methodType.getTypeOrigin() == Type.Origin.UNRESOLVED)
                                || (candidateType instanceof ObjectType && methodType instanceof ObjectType
                                        && methodType.getName().getLocalName().equals("E"))
                                || (candidateType.getTypeName().contains("List") && methodType.getTypeName().contains("List"))
                                || (candidateType instanceof IntegerType && methodType instanceof IntegerType)
                                || (candidateType instanceof FloatingPointType && methodType instanceof FloatingPointType)
                                || (candidateType instanceof FloatingPointType && methodType instanceof IntegerType);
                        if (!typesMatch) {
                            parametersMatch = false;
                            break;
                        }
                    }
                    if (parametersMatch && candidate.getBody() != null) {
                        visitedLinesRecorder.recordFirstLineVisited(candidate);
                        return candidate;
                    }
                }
            }
        } catch (Exception _) {
            // cannot restore method body -> give up
        }
        return original;
    }

    /**
     * Runs a method in this abstract interpretation engine context with the given name and parameters.
     * @param name the name of the method to run.
     * @param paramVars the parameters to pass to the method.
     * @param method the cpg method declaration to this method.
     * @param expectedType the expected return type of the method; used for type checking and to determine the return type
     * of this method.
     * @return null if the method is not known.
     */
    public IValue runMethod(@NotNull String name, List<IValue> paramVars, @Nullable MethodDeclaration method,
            @NotNull de.jplag.java_cpg.ai.variables.Type expectedType) {
        if (method == null) {
            return Value.valueFactory(expectedType);
        }
        if (method.getBody() == null) {     // cpg has lost the method body -> try to restore
            assert method.getRecordDeclaration() != null;
            method = (MethodDeclaration) restoreFunctionDeclaration(method.getRecordDeclaration(), method,
                    new ArrayList<>(method.getRecordDeclaration().getMethods()));
        }
        if (lastVisitedMethod.contains(method)) {
            // recursive call detected
            // ToDo: just set all changed variables to unknown like in loops
            this.variables.setEverythingUnknown();
            return Value.valueFactory(expectedType);
        }
        lastVisitedMethod.add(method);
        visitedLinesRecorder.recordFirstLineVisited(method);
        for (FunctionDeclaration subMethod : method.getOverriddenBy()) {
            visitedLinesRecorder.recordFirstLineVisited(subMethod);
        }
        boolean removeDeadCodeBackup = this.removeDeadCode;
        int numberOfCalls = method.getUsages().size();
        if (numberOfCalls > 1) {
            // method is called multiple times
            removeDeadCode = false;
            // ToDo: collect dead code in this methods and only remove if dead in all calls
        }
        ArrayList<Node> oldNodeStack = this.nodeStack;      // Save stack
        ArrayList<IValue> oldValueStack = this.valueStack;
        List<Node> oldLastVisitedLoopOrIf = this.lastVisitedLoopOrIf;
        int oldIfElseCounter = this.ifElseCounter;
        List<IValue> oldReturnStorage = this.returnStorage;
        this.returnStorage = new ArrayList<>();
        this.ifElseCounter = 0;
        this.nodeStack = new ArrayList<>();
        this.valueStack = new ArrayList<>();
        this.lastVisitedLoopOrIf = new ArrayList<>();
        visitedLinesRecorder.recordFirstLineVisited(method);
        variables.newScope();
        if (paramVars != null) {
            assert method.getParameters().size() == paramVars.size() || method.getType() instanceof FunctionType;
            for (int i = 0; i < paramVars.size(); i++) {
                variables.addVariable(new Variable(new VariableName(method.getParameters().get(i).getName().getLocalName()), paramVars.get(i)));
            }
        } else {
            assert method.getParameters().isEmpty();
        }
        IValue result;
        assert method.getNextEOG().size() <= 1;
        if (method.getNextEOG().size() == 1) {
            if (continueOnError) {
                try {
                    result = graphWalker(method.getNextEOG().getFirst());
                } catch (Exception _) {
                    variables.setEverythingUnknown();
                    result = Value.valueFactory(expectedType);
                }
            } else {
                result = graphWalker(method.getNextEOG().getFirst());
            }
        } else {
            result = Value.valueFactory(expectedType);
        }
        variables.removeScope();
        this.removeDeadCode = removeDeadCodeBackup;
        this.nodeStack = oldNodeStack;      // restore stack
        this.valueStack = oldValueStack;
        this.lastVisitedLoopOrIf = oldLastVisitedLoopOrIf;
        lastVisitedMethod.removeLast();
        ifElseCounter = oldIfElseCounter;
        this.returnStorage = oldReturnStorage;
        return result;
    }

    protected void setMethodAnalysisMode() {
        this.methodAnalysisMode = true;
    }

    /**
     * @return true if this abstract interpretation engine is currently only analyzing a method without running any other
     * methods or constructors, false otherwise.
     */
    public boolean isMethodAnalysisMode() {
        return this.methodAnalysisMode;
    }

    /**
     * If set to true, the abstract interpretation engine will continue execution even if it encounters an error during
     * execution. This can lead to more imprecise results, but can be useful to still get some results even if the cpg is
     * not perfect.
     * @param continueOnError whether to continue on error or not
     */
    public static void setContinueOnError(boolean continueOnError) {
        AbstractInterpretation.continueOnError = continueOnError;
    }

}
