package de.jplag.java_cpg.transformation.matching.edges;

import de.fraunhofer.aisec.cpg.graph.Component;
import de.fraunhofer.aisec.cpg.graph.Name;
import de.fraunhofer.aisec.cpg.graph.Node;
import de.fraunhofer.aisec.cpg.graph.declarations.*;
import de.fraunhofer.aisec.cpg.graph.statements.*;
import de.fraunhofer.aisec.cpg.graph.statements.expressions.*;
import de.fraunhofer.aisec.cpg.graph.types.FunctionType;
import de.fraunhofer.aisec.cpg.graph.types.IncompleteType;
import de.fraunhofer.aisec.cpg.graph.types.ObjectType;
import de.fraunhofer.aisec.cpg.graph.types.Type;
import de.fraunhofer.aisec.cpg.sarif.PhysicalLocation;
import kotlin.Pair;

import java.util.*;

import static de.jplag.java_cpg.transformation.matching.edges.IEdge.EdgeCategory.*;

/**
 * A constant class containing relevant {@link IEdge} objects.
 */
public class Edges {
    /**
     * A {@link Map} to retrieve all {@link IEdge}s with a specific source type.
     */
    private static final Map<Class<?>, List<IEdge<?, ?>>> fromType;
    /**
     * A {@link Map} to retrieve all {@link IEdge}s with a specific target type.
     */
    private static final Map<Class<?>, List<IEdge<?, ?>>> toType;

    /**
     * A {@link Map} to retrieve all {@link IEdge}s with a specific source and target type
     */
    private static final Map<Pair<Class<? extends Node>, Class<? extends Node>>, List<IEdge<?,?>>> fromToType;

    private Edges() { /* should not be instantiated */}

    public static CpgEdge<AssignExpression, Expression> ASSIGN_EXPRESSION__LHS = CpgEdge.listValued(AssignExpression::getLhs, AssignExpression::setLhs);
    public static CpgEdge<AssignExpression, Expression> ASSIGN_EXPRESSION__RHS = CpgEdge.listValued(AssignExpression::getRhs, AssignExpression::setRhs);
    public static CpgEdge<BinaryOperator, Expression> BINARY_OPERATOR__LHS = new CpgEdge<>(BinaryOperator::getLhs, BinaryOperator::setLhs);
    public static CpgPropertyEdge<BinaryOperator, String> BINARY_OPERATOR__OPERATOR_CODE = new CpgPropertyEdge<>(BinaryOperator::getOperatorCode, BinaryOperator::setOperatorCode);
    public static CpgEdge<BinaryOperator, Expression> BINARY_OPERATOR__RHS = new CpgEdge<>(BinaryOperator::getRhs, BinaryOperator::setRhs);
    public static CpgMultiEdge<Block, Declaration> BLOCK__DECLARATIONS = CpgMultiEdge.nodeValued(Block::getDeclarations);
    public static CpgMultiEdge<Block, Statement> BLOCK__STATEMENTS = CpgMultiEdge.edgeValued(Block::getStatementEdges);
    public static CpgMultiEdge<CallExpression, Expression> CALL_EXPRESSION__ARGUMENTS = CpgMultiEdge.edgeValued(CallExpression::getArgumentEdges);
    public static CpgEdge<CallExpression, Expression> CALL_EXPRESSION__CALLEE = new CpgEdge<>(CallExpression::getCallee, CallExpression::setCallee);
    public static CpgMultiEdge<CallExpression, FunctionDeclaration> CALL_EXPRESSION__INVOKES = CpgMultiEdge.edgeValued(CallExpression::getInvokeEdges, REFERENCE);
    public static CpgMultiEdge<Component, TranslationUnitDeclaration> COMPONENT__TRANSLATION_UNITS = CpgMultiEdge.nodeValued(Component::getTranslationUnits);
    public static CpgMultiEdge<DeclarationStatement, Declaration> DECLARATION_STATEMENT__DECLARATIONS = CpgMultiEdge.edgeValued(DeclarationStatement::getDeclarationEdges);
    public static CpgPropertyEdge<Declaration, Name> DECLARATION__NAME = new CpgPropertyEdge<>(Node::getName, Node::setName);
    public static CpgPropertyEdge<FieldDeclaration, List<String>> FIELD_DECLARATION__MODIFIERS = new CpgPropertyEdge<>(FieldDeclaration::getModifiers, FieldDeclaration::setModifiers);
    public static CpgEdge<ForStatement, Expression> FOR_STATEMENT__CONDITION = new CpgEdge<>(ForStatement::getCondition, ForStatement::setCondition);
    public static CpgEdge<ForStatement, Statement> FOR_STATEMENT__INITIALIZER_STATEMENT = new CpgEdge<>(ForStatement::getInitializerStatement, ForStatement::setInitializerStatement);
    public static CpgEdge<ForStatement, Statement> FOR_STATEMENT__ITERATION_STATEMENT = new CpgEdge<>(ForStatement::getIterationStatement, ForStatement::setIterationStatement);
    public static CpgEdge<ForStatement, Statement> FOR_STATEMENT__STATEMENT = new CpgEdge<>(ForStatement::getStatement, ForStatement::setStatement);
    public static CpgMultiEdge<FunctionDeclaration, FunctionDeclaration> FUNCTION_DECLARATION__OVERRIDES = CpgMultiEdge.nodeValued(FunctionDeclaration::getOverrides, REFERENCE);
    public static CpgMultiEdge<FunctionDeclaration, FunctionDeclaration> FUNCTION_DECLARATION__OVERRIDDEN_BY = CpgMultiEdge.nodeValued(FunctionDeclaration::getOverriddenBy, REFERENCE);
    public static CpgMultiEdge<FunctionType, Type> FUNCTION_TYPE__PARAMETERS = CpgMultiEdge.nodeValued(FunctionType::getParameters);
    public static CpgMultiEdge<FunctionType, Type> FUNCTION_TYPE__RETURN_TYPES = CpgMultiEdge.nodeValued(FunctionType::getReturnTypes);
    public static CpgEdge<IfStatement, Expression> IF_STATEMENT__CONDITION = new CpgEdge<>(IfStatement::getCondition, IfStatement::setCondition);
    public static CpgEdge<IfStatement, Statement> IF_STATEMENT__ELSE_STATEMENT = new CpgEdge<>(IfStatement::getElseStatement, IfStatement::setElseStatement);
    public static CpgEdge<IfStatement, Statement> IF_STATEMENT__THEN_STATEMENT = new CpgEdge<>(IfStatement::getThenStatement, IfStatement::setThenStatement);
    public static CpgPropertyEdge<IncompleteType, String> INCOMPLETE_TYPE__TYPE_NAME = new CpgPropertyEdge<>(IncompleteType::getTypeName, null);

    public static CpgEdge<MemberExpression, RecordDeclaration> MEMBER_EXPRESSION__RECORD_DECLARATION = new CpgEdge<>(EdgeUtil::getRecord, null, ANALYTIC);
    public static CpgEdge<MemberExpression, Expression> MEMBER_EXPRESSION__BASE = new CpgEdge<>(MemberExpression::getBase, MemberExpression::setBase);
    public static CpgEdge<MethodDeclaration, Statement> METHOD_DECLARATION__BODY = new CpgEdge<>(MethodDeclaration::getBody, MethodDeclaration::setBody);
    public static CpgMultiEdge<MethodDeclaration, ParameterDeclaration> METHOD_DECLARATION__PARAMETERS = CpgMultiEdge.nodeValued(MethodDeclaration::getParameters);
    public static CpgEdge<MethodDeclaration, RecordDeclaration> METHOD_DECLARATION__RECORD_DECLARATION = new CpgEdge<>(MethodDeclaration::getRecordDeclaration, MethodDeclaration::setRecordDeclaration);
    public static CpgMultiEdge<NamespaceDeclaration, Declaration> NAMESPACE_DECLARATION__RECORDS = CpgMultiEdge.nodeValued(NamespaceDeclaration::getDeclarations);
    public static CpgEdge<ObjectType, RecordDeclaration> OBJECT_TYPE__RECORD_DECLARATION = new CpgEdge<>(ObjectType::getRecordDeclaration, ObjectType::setRecordDeclaration);
    public static CpgEdge<ParameterDeclaration, Type> PARAMETER_DECLARATION__TYPE = new CpgEdge<>(ParameterDeclaration::getType, ParameterDeclaration::setType);
    public static CpgMultiEdge<RecordDeclaration, FieldDeclaration> RECORD_DECLARATION__FIELDS = CpgMultiEdge.edgeValued(RecordDeclaration::getFieldEdges);
    public static CpgPropertyEdge<RecordDeclaration, PhysicalLocation> RECORD_DECLARATION__LOCATION = new CpgPropertyEdge<>(RecordDeclaration::getLocation, RecordDeclaration::setLocation);
    public static CpgPropertyEdge<Node, PhysicalLocation> NODE__LOCATION = new CpgPropertyEdge<>(Node::getLocation, Node::setLocation);

    public static CpgMultiEdge<RecordDeclaration, MethodDeclaration> RECORD_DECLARATION__METHODS = CpgMultiEdge.edgeValued(RecordDeclaration::getMethodEdges);
    public static CpgMultiEdge<RecordDeclaration, ConstructorDeclaration> RECORD_DECLARATION__CONSTRUCTORS = CpgMultiEdge.edgeValued(RecordDeclaration::getConstructorEdges);
    public static CpgEdge<Reference, Declaration> REFERENCE__REFERS_TO = new CpgEdge<>(Reference::getRefersTo, Reference::setRefersTo, REFERENCE);
    public static CpgMultiEdge<ReturnStatement, Expression> RETURN_STATEMENT__RETURN_VALUES = CpgMultiEdge.nodeValued(ReturnStatement::getReturnValues);
    public static CpgMultiEdge<TranslationUnitDeclaration, Declaration> TRANSLATION_UNIT__DECLARATIONS = CpgMultiEdge.edgeValued(TranslationUnitDeclaration::getDeclarationEdges);
    public static CpgEdge<UnaryOperator, Expression> UNARY_OPERATOR__INPUT = new CpgEdge<>(UnaryOperator::getInput, UnaryOperator::setInput);
    public static CpgPropertyEdge<UnaryOperator, String> UNARY_OPERATOR__OPERATOR_CODE = new CpgPropertyEdge<>(UnaryOperator::getOperatorCode, UnaryOperator::setOperatorCode);
    public static CpgMultiEdge<ValueDeclaration, Reference> VALUE_DECLARATION__USAGES = CpgMultiEdge.edgeValued(ValueDeclaration::getUsageEdges, REFERENCE);
    public static CpgEdge<ValueDeclaration, Type> VALUE_DECLARATION__TYPE = new CpgEdge<>(ValueDeclaration::getType, ValueDeclaration::setType);
    public static CpgEdge<VariableDeclaration, Expression> VARIABLE_DECLARATION__INITIALIZER = new CpgEdge<>(VariableDeclaration::getInitializer, VariableDeclaration::setInitializer);
    public static CpgEdge<WhileStatement, Statement> WHILE_STATEMENT__STATEMENT = new CpgEdge<>(WhileStatement::getStatement, WhileStatement::setStatement);
    public static CpgEdge<WhileStatement, Expression> WHILE_STATEMENT__CONDITION = new CpgEdge<>(WhileStatement::getCondition, WhileStatement::setCondition);

    static {
        fromType = new HashMap<>();
        toType = new HashMap<>();
        fromToType = new HashMap<>();

        register(ASSIGN_EXPRESSION__LHS, AssignExpression.class, Expression.class);
        register(ASSIGN_EXPRESSION__RHS, AssignExpression.class, Expression.class);
        register(BINARY_OPERATOR__LHS, BinaryOperator.class, Expression.class);
        register(BINARY_OPERATOR__RHS, BinaryOperator.class, Expression.class);
        register(BLOCK__STATEMENTS, Block.class, Statement.class);
        register(CALL_EXPRESSION__ARGUMENTS, CallExpression.class, Expression.class);
        register(CALL_EXPRESSION__CALLEE, CallExpression.class, Expression.class);
        register(DECLARATION_STATEMENT__DECLARATIONS, DeclarationStatement.class, Declaration.class);
        register(FOR_STATEMENT__CONDITION, ForStatement.class, Expression.class);
        register(FOR_STATEMENT__INITIALIZER_STATEMENT, ForStatement.class, Statement.class);
        register(FOR_STATEMENT__ITERATION_STATEMENT, ForStatement.class, Statement.class);
        register(FOR_STATEMENT__STATEMENT, ForStatement.class, Statement.class);
        register(FUNCTION_TYPE__PARAMETERS, FunctionType.class, Type.class);
        register(FUNCTION_TYPE__RETURN_TYPES, FunctionType.class, Type.class);
        register(IF_STATEMENT__CONDITION, IfStatement.class, Expression.class);
        register(IF_STATEMENT__ELSE_STATEMENT, IfStatement.class, Statement.class);
        register(IF_STATEMENT__THEN_STATEMENT, IfStatement.class, Statement.class);
        register(MEMBER_EXPRESSION__BASE, MemberExpression.class, Expression.class);
        register(METHOD_DECLARATION__BODY, MethodDeclaration.class, Statement.class);
        register(METHOD_DECLARATION__PARAMETERS, MethodDeclaration.class, ParameterDeclaration.class);
        register(METHOD_DECLARATION__RECORD_DECLARATION, MethodDeclaration.class, RecordDeclaration.class);
        register(OBJECT_TYPE__RECORD_DECLARATION, ObjectType.class, RecordDeclaration.class);
        register(PARAMETER_DECLARATION__TYPE, ParameterDeclaration.class, Type.class);
        register(RECORD_DECLARATION__FIELDS, RecordDeclaration.class, FieldDeclaration.class);
        register(RECORD_DECLARATION__METHODS, RecordDeclaration.class, MethodDeclaration.class);
        register(RETURN_STATEMENT__RETURN_VALUES, ReturnStatement.class, Expression.class);
        register(TRANSLATION_UNIT__DECLARATIONS, TranslationUnitDeclaration.class, Declaration.class);
        register(UNARY_OPERATOR__INPUT, UnaryOperator.class, Expression.class);
        register(VALUE_DECLARATION__TYPE, ValueDeclaration.class, Type.class);
        register(VARIABLE_DECLARATION__INITIALIZER, VariableDeclaration.class, Expression.class);
        register(WHILE_STATEMENT__CONDITION, WhileStatement.class, Expression.class);
        register(WHILE_STATEMENT__STATEMENT, WhileStatement.class, Statement.class);
    }
    private static <S extends Node, T extends Node> void register(IEdge<S, T> edge, Class<S> sClass, Class<T> tClass) {
        edge.setFromClass(sClass);
        edge.setToClass(tClass);
        fromType.computeIfAbsent(sClass, c -> new ArrayList<>()).add(edge);
        toType.computeIfAbsent(tClass, c -> new ArrayList<>()).add(edge);
        fromToType.computeIfAbsent(new Pair<>(sClass, tClass), p -> new ArrayList<>()).add(edge);
    }

    public <S extends Node> List<IEdge<S, ?>> getEdgesFromType(Class<S> sClass) {
        List<IEdge<S, ?>> result = new ArrayList<>();
        fromType.getOrDefault(sClass, List.of()).stream().map(e -> (IEdge<S, ?>) e).forEach(result::add);
        return result;
    }

    public static <T extends Node> List<IEdge<? extends Node, ? super T>> getEdgesToType(Class<T> tClass) {
        List<IEdge<?, ? super T>> result = new ArrayList<>();
        Class<? super T> type = tClass;
        while (Node.class.isAssignableFrom(type)) {
            toType.getOrDefault(type, List.of()).stream().map(e -> (IEdge<?, ? super T>) e).forEach(result::add);
            type = getSuperclass(type);
        }
        return result;
    }

    private static <T extends Node> Class<? super T> getSuperclass(Class<? super T> type) {
        if (!type.getSuperclass().equals(type)) {
            return type.getSuperclass();
        }
        return (Class<? super T>) type.getGenericSuperclass();
    }

    public static <S extends Node, T extends Node> List<IEdge<S,T>> getEdgesFromTypeToType(Class<S> sClass, Class<T> tClass) {
        List<IEdge<S, T>> result = new ArrayList<>();
        fromToType.getOrDefault(new Pair<>(sClass,tClass), List.of()).stream().map(e -> (IEdge<S, T>) e).forEach(result::add);
        return result;
    }
}
