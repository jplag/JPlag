package de.jplag.java_cpg.transformation.matching.edges;

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

    public static CpgMultiEdge<AssignExpression, Expression> ASSIGN_EXPRESSION__LHS = CpgMultiEdge.nodeValued(AssignExpression::getLhs);
    public static CpgMultiEdge<AssignExpression, Expression> ASSIGN_EXPRESSION__RHS = CpgMultiEdge.nodeValued(AssignExpression::getRhs);
    public static CpgEdge<BinaryOperator, Expression> BINARY_OPERATOR__LHS = new CpgEdge<>(BinaryOperator::getLhs, BinaryOperator::setLhs);
    public static CpgPropertyEdge<BinaryOperator, String> BINARY_OPERATOR__OPERATOR_CODE = new CpgPropertyEdge<>(BinaryOperator::getOperatorCode, BinaryOperator::setOperatorCode);
    public static CpgEdge<BinaryOperator, Expression> BINARY_OPERATOR__RHS = new CpgEdge<>(BinaryOperator::getRhs, BinaryOperator::setRhs);
    public static CpgMultiEdge<Block, Statement> BLOCK__STATEMENTS = CpgMultiEdge.edgeValued(Block::getStatementEdges);
    public static CpgMultiEdge<DeclarationStatement, Declaration> DECLARATION_STATEMENT__DECLARATIONS = CpgMultiEdge.edgeValued(DeclarationStatement::getDeclarationEdges);
    public static CpgEdge<FieldDeclaration, Type> FIELD_DECLARATION__TYPE = new CpgEdge<>(FieldDeclaration::getType, FieldDeclaration::setType);
    public static CpgEdge<ForStatement, Expression> FOR_STATEMENT__CONDITION = new CpgEdge<>(ForStatement::getCondition, ForStatement::setCondition);
    public static CpgEdge<ForStatement, Statement> FOR_STATEMENT__INITIALIZER_STATEMENT = new CpgEdge<>(ForStatement::getInitializerStatement, ForStatement::setInitializerStatement);
    public static CpgEdge<ForStatement, Statement> FOR_STATEMENT__ITERATION_STATEMENT = new CpgEdge<>(ForStatement::getIterationStatement, ForStatement::setIterationStatement);
    public static CpgEdge<ForStatement, Statement> FOR_STATEMENT__STATEMENT = new CpgEdge<>(ForStatement::getStatement, ForStatement::setStatement);
    public static CpgMultiEdge<FunctionType, Type> FUNCTION_TYPE__PARAMETERS = CpgMultiEdge.nodeValued(FunctionType::getParameters);
    public static CpgMultiEdge<FunctionType, Type> FUNCTION_TYPE__RETURN_TYPES = CpgMultiEdge.nodeValued(FunctionType::getReturnTypes);
    public static CpgEdge<IfStatement, Expression> IF_STATEMENT__CONDITION = new CpgEdge<>(IfStatement::getCondition, IfStatement::setCondition);
    public static CpgEdge<IfStatement, Statement> IF_STATEMENT__ELSE_STATEMENT = new CpgEdge<>(IfStatement::getElseStatement, IfStatement::setElseStatement);
    public static CpgEdge<IfStatement, Statement> IF_STATEMENT__THEN_STATEMENT = new CpgEdge<>(IfStatement::getThenStatement, IfStatement::setThenStatement);
    public static CpgPropertyEdge<IncompleteType, String> INCOMPLETE_TYPE__TYPE_NAME = new CpgPropertyEdge<>(IncompleteType::getTypeName, null);
    public static CpgEdge<MethodDeclaration, Statement> METHOD_DECLARATION__BODY = new CpgEdge<>(MethodDeclaration::getBody, MethodDeclaration::setBody);
    public static CpgMultiEdge<MethodDeclaration, ParameterDeclaration> METHOD_DECLARATION__PARAMETERS = CpgMultiEdge.nodeValued(MethodDeclaration::getParameters);
    public static CpgEdge<MethodDeclaration, RecordDeclaration> METHOD_DECLARATION__RECORD_DECLARATION = new CpgEdge<>(MethodDeclaration::getRecordDeclaration, MethodDeclaration::setRecordDeclaration);
    public static CpgEdge<MethodDeclaration, Type> METHOD_DECLARATION__TYPE = new CpgEdge<>(MethodDeclaration::getType, MethodDeclaration::setType);
    public static CpgMultiEdge<MethodDeclaration, Reference> METHOD_DECLARATION__USAGES = CpgMultiEdge.nodeValued(MethodDeclaration::getUsages);
    public static CpgEdge<ObjectType, RecordDeclaration> OBJECT_TYPE__RECORD_DECLARATION = new CpgEdge<>(ObjectType::getRecordDeclaration, ObjectType::setRecordDeclaration);
    public static CpgEdge<ParameterDeclaration, Type> PARAMETER_DECLARATION__TYPE = new CpgEdge<>(ParameterDeclaration::getType, ParameterDeclaration::setType);
    public static CpgMultiEdge<RecordDeclaration, FieldDeclaration> RECORD_DECLARATION__FIELDS = CpgMultiEdge.edgeValued(RecordDeclaration::getFieldEdges);
    public static CpgPropertyEdge<RecordDeclaration, PhysicalLocation> RECORD_DECLARATION__LOCATION = new CpgPropertyEdge<>(RecordDeclaration::getLocation, RecordDeclaration::setLocation);
    public static CpgMultiEdge<RecordDeclaration, MethodDeclaration> RECORD_DECLARATION__METHODS = CpgMultiEdge.edgeValued(RecordDeclaration::getMethodEdges);
    public static CpgEdge<Reference, Declaration> REFERENCE__REFERS_TO = new CpgEdge<>(Reference::getRefersTo, Reference::setRefersTo);
    public static CpgMultiEdge<ReturnStatement, Expression> RETURN_STATEMENT__RETURN_VALUES = CpgMultiEdge.nodeValued(ReturnStatement::getReturnValues);
    public static CpgMultiEdge<TranslationUnitDeclaration, Declaration> TRANSLATION_UNIT__DECLARATIONS = CpgMultiEdge.edgeValued(TranslationUnitDeclaration::getDeclarationEdges);
    public static CpgEdge<UnaryOperator, Expression> UNARY_OPERATOR__INPUT = new CpgEdge<>(UnaryOperator::getInput, UnaryOperator::setInput);
    public static CpgPropertyEdge<UnaryOperator, String> UNARY_OPERATOR__OPERATOR_CODE = new CpgPropertyEdge<>(UnaryOperator::getOperatorCode, UnaryOperator::setOperatorCode);
    public static CpgMultiEdge<VariableDeclaration, Reference> VARIABLE_DECLARATION__USAGES = CpgMultiEdge.nodeValued(VariableDeclaration::getUsages);
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
        register(DECLARATION_STATEMENT__DECLARATIONS, DeclarationStatement.class, Declaration.class);
        register(FIELD_DECLARATION__TYPE, FieldDeclaration.class, Type.class);
        register(FOR_STATEMENT__CONDITION, ForStatement.class, Expression.class);
        register(FOR_STATEMENT__INITIALIZER_STATEMENT, ForStatement.class, Statement.class);
        register(FOR_STATEMENT__ITERATION_STATEMENT, ForStatement.class, Statement.class);
        register(FOR_STATEMENT__STATEMENT, ForStatement.class, Statement.class);
        register(FUNCTION_TYPE__PARAMETERS, FunctionType.class, Type.class);
        register(FUNCTION_TYPE__RETURN_TYPES, FunctionType.class, Type.class);
        register(IF_STATEMENT__CONDITION, IfStatement.class, Expression.class);
        register(IF_STATEMENT__ELSE_STATEMENT, IfStatement.class, Statement.class);
        register(IF_STATEMENT__THEN_STATEMENT, IfStatement.class, Statement.class);
        register(METHOD_DECLARATION__BODY, MethodDeclaration.class, Statement.class);
        register(METHOD_DECLARATION__PARAMETERS, MethodDeclaration.class, ParameterDeclaration.class);
        register(METHOD_DECLARATION__RECORD_DECLARATION, MethodDeclaration.class, RecordDeclaration.class);
        register(METHOD_DECLARATION__TYPE, MethodDeclaration.class, Type.class);
        register(METHOD_DECLARATION__USAGES, MethodDeclaration.class, Reference.class);
        register(OBJECT_TYPE__RECORD_DECLARATION, ObjectType.class, RecordDeclaration.class);
        register(PARAMETER_DECLARATION__TYPE, ParameterDeclaration.class, Type.class);
        register(RECORD_DECLARATION__FIELDS, RecordDeclaration.class, FieldDeclaration.class);
        register(RECORD_DECLARATION__METHODS, RecordDeclaration.class, MethodDeclaration.class);
        register(REFERENCE__REFERS_TO, Reference.class, Declaration.class);
        register(RETURN_STATEMENT__RETURN_VALUES, ReturnStatement.class, Expression.class);
        register(TRANSLATION_UNIT__DECLARATIONS, TranslationUnitDeclaration.class, Declaration.class);
        register(UNARY_OPERATOR__INPUT, UnaryOperator.class, Expression.class);
        register(VARIABLE_DECLARATION__USAGES, VariableDeclaration.class, Reference.class);
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
        Class<? super T> type = tClass.getSuperclass();
        do  {
            toType.getOrDefault(type, List.of()).stream().map(e -> (IEdge<?, ? super T>) e).forEach(result::add);
            type = getSuperclass(type);
        } while (!Objects.equals(type, Node.class));
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
