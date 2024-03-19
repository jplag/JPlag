package de.jplag.java_cpg.transformation.matching.edges;

import static de.jplag.java_cpg.transformation.matching.edges.IEdge.EdgeCategory.ANALYTIC;
import static de.jplag.java_cpg.transformation.matching.edges.IEdge.EdgeCategory.REFERENCE;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import de.jplag.java_cpg.transformation.GraphTransformation.Builder;
import de.jplag.java_cpg.transformation.matching.pattern.WildcardGraphPattern;

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

    public static CpgEdge<AssignExpression, Expression> ASSIGN_EXPRESSION__LHS = CpgEdge.listValued(AssignExpression::getLhs,
            AssignExpression::setLhs);
    public static CpgEdge<AssignExpression, Expression> ASSIGN_EXPRESSION__RHS = CpgEdge.listValued(AssignExpression::getRhs,
            AssignExpression::setRhs);
    public static CpgEdge<BinaryOperator, Expression> BINARY_OPERATOR__LHS = new CpgEdge<>(BinaryOperator::getLhs, BinaryOperator::setLhs);
    public static CpgAttributeEdge<BinaryOperator, String> BINARY_OPERATOR__OPERATOR_CODE = new CpgAttributeEdge<>(BinaryOperator::getOperatorCode,
            BinaryOperator::setOperatorCode);
    public static CpgEdge<BinaryOperator, Expression> BINARY_OPERATOR__RHS = new CpgEdge<>(BinaryOperator::getRhs, BinaryOperator::setRhs);
    public static CpgMultiEdge<Block, Declaration> BLOCK__DECLARATIONS = CpgMultiEdge.nodeValued(Block::getDeclarations, REFERENCE);
    public static CpgMultiEdge<Block, Statement> BLOCK__STATEMENTS = CpgMultiEdge.edgeValued(Block::getStatementEdges);
    public static CpgMultiEdge<CallExpression, Expression> CALL_EXPRESSION__ARGUMENTS = CpgMultiEdge.edgeValued(CallExpression::getArgumentEdges);
    public static CpgEdge<CallExpression, Expression> CALL_EXPRESSION__CALLEE = new CpgEdge<>(CallExpression::getCallee, CallExpression::setCallee);
    public static CpgMultiEdge<CallExpression, FunctionDeclaration> CALL_EXPRESSION__INVOKES = CpgMultiEdge.edgeValued(CallExpression::getInvokeEdges,
            REFERENCE);
    public static CpgMultiEdge<Component, TranslationUnitDeclaration> COMPONENT__TRANSLATION_UNITS = CpgMultiEdge
            .nodeValued(Component::getTranslationUnits);
    public static CpgMultiEdge<DeclarationStatement, Declaration> DECLARATION_STATEMENT__DECLARATIONS = CpgMultiEdge
            .edgeValued(DeclarationStatement::getDeclarationEdges);
    public static CpgAttributeEdge<FieldDeclaration, List<String>> FIELD_DECLARATION__MODIFIERS = new CpgAttributeEdge<>(
            FieldDeclaration::getModifiers, FieldDeclaration::setModifiers);
    public static CpgEdge<ForStatement, Expression> FOR_STATEMENT__CONDITION = new CpgEdge<>(ForStatement::getCondition, ForStatement::setCondition);
    public static CpgEdge<ForStatement, Statement> FOR_STATEMENT__INITIALIZER_STATEMENT = new CpgEdge<>(ForStatement::getInitializerStatement,
            ForStatement::setInitializerStatement);
    public static CpgEdge<ForStatement, Statement> FOR_STATEMENT__ITERATION_STATEMENT = new CpgEdge<>(ForStatement::getIterationStatement,
            ForStatement::setIterationStatement);
    public static CpgEdge<ForStatement, Statement> FOR_STATEMENT__STATEMENT = new CpgEdge<>(ForStatement::getStatement, ForStatement::setStatement);
    public static CpgMultiEdge<FunctionDeclaration, FunctionDeclaration> FUNCTION_DECLARATION__OVERRIDES = CpgMultiEdge
            .nodeValued(FunctionDeclaration::getOverrides, REFERENCE);
    public static CpgMultiEdge<FunctionDeclaration, FunctionDeclaration> FUNCTION_DECLARATION__OVERRIDDEN_BY = CpgMultiEdge
            .nodeValued(FunctionDeclaration::getOverriddenBy, REFERENCE);
    public static CpgMultiEdge<FunctionType, Type> FUNCTION_TYPE__PARAMETERS = CpgMultiEdge.nodeValued(FunctionType::getParameters);
    public static CpgMultiEdge<FunctionType, Type> FUNCTION_TYPE__RETURN_TYPES = CpgMultiEdge.nodeValued(FunctionType::getReturnTypes);
    public static CpgEdge<IfStatement, Expression> IF_STATEMENT__CONDITION = new CpgEdge<>(IfStatement::getCondition, IfStatement::setCondition);
    public static CpgEdge<IfStatement, Statement> IF_STATEMENT__ELSE_STATEMENT = new CpgEdge<>(IfStatement::getElseStatement,
            IfStatement::setElseStatement);
    public static CpgEdge<IfStatement, Statement> IF_STATEMENT__THEN_STATEMENT = new CpgEdge<>(IfStatement::getThenStatement,
            IfStatement::setThenStatement);
    public static CpgEdge<MemberExpression, RecordDeclaration> MEMBER_EXPRESSION__RECORD_DECLARATION = new CpgEdge<>(EdgeUtil::getRecord, null,
            ANALYTIC);
    public static CpgEdge<MemberExpression, Expression> MEMBER_EXPRESSION__BASE = new CpgEdge<>(MemberExpression::getBase, MemberExpression::setBase);
    public static CpgEdge<MethodDeclaration, Statement> METHOD_DECLARATION__BODY = new CpgEdge<>(MethodDeclaration::getBody,
            MethodDeclaration::setBody);
    public static CpgMultiEdge<MethodDeclaration, ParameterDeclaration> METHOD_DECLARATION__PARAMETERS = CpgMultiEdge
            .nodeValued(MethodDeclaration::getParameters);
    public static CpgEdge<MethodDeclaration, RecordDeclaration> METHOD_DECLARATION__RECORD_DECLARATION = new CpgEdge<>(
            MethodDeclaration::getRecordDeclaration, MethodDeclaration::setRecordDeclaration, REFERENCE);
    public static CpgMultiEdge<NamespaceDeclaration, Declaration> NAMESPACE_DECLARATION__DECLARATIONS = CpgMultiEdge
            .nodeValued(NamespaceDeclaration::getDeclarations);
    public static CpgAttributeEdge<Node, PhysicalLocation> NODE__LOCATION = new CpgAttributeEdge<>(Node::getLocation, Node::setLocation);
    public static CpgAttributeEdge<Declaration, Name> NODE__NAME = new CpgAttributeEdge<>(Node::getName, Node::setName);
    public static CpgAttributeEdge<Declaration, String> NODE__LOCAL_NAME = new CpgAttributeEdge<>(EdgeUtil::getLocalName, null);

    public static CpgEdge<ObjectType, RecordDeclaration> OBJECT_TYPE__RECORD_DECLARATION = new CpgEdge<>(ObjectType::getRecordDeclaration,
            ObjectType::setRecordDeclaration, REFERENCE);
    public static CpgMultiEdge<RecordDeclaration, FieldDeclaration> RECORD_DECLARATION__FIELDS = CpgMultiEdge
            .edgeValued(RecordDeclaration::getFieldEdges);
    public static CpgMultiEdge<RecordDeclaration, MethodDeclaration> RECORD_DECLARATION__METHODS = CpgMultiEdge
            .edgeValued(RecordDeclaration::getMethodEdges);
    public static CpgMultiEdge<RecordDeclaration, ConstructorDeclaration> RECORD_DECLARATION__CONSTRUCTORS = CpgMultiEdge
            .edgeValued(RecordDeclaration::getConstructorEdges);
    public static CpgEdge<Reference, Declaration> REFERENCE__REFERS_TO = new CpgEdge<>(Reference::getRefersTo, Reference::setRefersTo, REFERENCE);
    public static CpgMultiEdge<ReturnStatement, Expression> RETURN_STATEMENT__RETURN_VALUES = CpgMultiEdge
            .nodeValued(ReturnStatement::getReturnValues);
    public static CpgMultiEdge<Statement, VariableDeclaration> STATEMENT__LOCALS = CpgMultiEdge.edgeValued(Statement::getLocalEdges, REFERENCE);
    public static CpgEdge<SubscriptExpression, Expression> SUBSCRIPT_EXPRESSION__SUBSCRIPT_EXPRESSION = new CpgEdge<>(
            SubscriptExpression::getSubscriptExpression, SubscriptExpression::setSubscriptExpression);
    public static CpgEdge<SubscriptExpression, Expression> SUBSCRIPT_EXPRESSION__ARRAY_EXPRESSION = new CpgEdge<>(
            SubscriptExpression::getArrayExpression, SubscriptExpression::setArrayExpression);
    public static CpgMultiEdge<TranslationUnitDeclaration, Declaration> TRANSLATION_UNIT__DECLARATIONS = CpgMultiEdge
            .edgeValued(TranslationUnitDeclaration::getDeclarationEdges);
    public static CpgAttributeEdge<IncompleteType, String> TYPE__TYPE_NAME = new CpgAttributeEdge<>(Type::getTypeName, null);
    public static CpgEdge<UnaryOperator, Expression> UNARY_OPERATOR__INPUT = new CpgEdge<>(UnaryOperator::getInput, UnaryOperator::setInput);
    public static CpgAttributeEdge<UnaryOperator, String> UNARY_OPERATOR__OPERATOR_CODE = new CpgAttributeEdge<>(UnaryOperator::getOperatorCode,
            UnaryOperator::setOperatorCode);
    public static CpgMultiEdge<ValueDeclaration, Reference> VALUE_DECLARATION__USAGES = CpgMultiEdge.edgeValued(ValueDeclaration::getUsageEdges,
            REFERENCE);
    public static CpgEdge<ValueDeclaration, Type> VALUE_DECLARATION__TYPE = new CpgEdge<>(ValueDeclaration::getType, ValueDeclaration::setType);
    public static CpgEdge<VariableDeclaration, Expression> VARIABLE_DECLARATION__INITIALIZER = new CpgEdge<>(VariableDeclaration::getInitializer,
            VariableDeclaration::setInitializer);
    public static CpgEdge<WhileStatement, Statement> WHILE_STATEMENT__STATEMENT = new CpgEdge<>(WhileStatement::getStatement,
            WhileStatement::setStatement);
    public static CpgEdge<WhileStatement, Expression> WHILE_STATEMENT__CONDITION = new CpgEdge<>(WhileStatement::getCondition,
            WhileStatement::setCondition);

    public static CpgEdge<DoStatement, Statement> DO_STATEMENT__STATEMENT = new CpgEdge<>(DoStatement::getStatement, DoStatement::setStatement);
    public static CpgEdge<DoStatement, Expression> DO_STATEMENT__CONDITION = new CpgEdge<>(DoStatement::getCondition, DoStatement::setCondition);

    static {
        fromType = new HashMap<>();
        toType = new HashMap<>();

        register(ASSIGN_EXPRESSION__LHS, AssignExpression.class, Expression.class);
        register(ASSIGN_EXPRESSION__RHS, AssignExpression.class, Expression.class);
        register(BINARY_OPERATOR__LHS, BinaryOperator.class, Expression.class);
        register(BINARY_OPERATOR__RHS, BinaryOperator.class, Expression.class);
        register(BLOCK__STATEMENTS, Block.class, Statement.class);
        register(CALL_EXPRESSION__ARGUMENTS, CallExpression.class, Expression.class);
        register(CALL_EXPRESSION__CALLEE, CallExpression.class, Expression.class);
        register(DECLARATION_STATEMENT__DECLARATIONS, DeclarationStatement.class, Declaration.class);
        register(DO_STATEMENT__CONDITION, DoStatement.class, Expression.class);
        register(DO_STATEMENT__STATEMENT, DoStatement.class, Statement.class);
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
        register(NAMESPACE_DECLARATION__DECLARATIONS, NamespaceDeclaration.class, Declaration.class);
        register(RECORD_DECLARATION__FIELDS, RecordDeclaration.class, FieldDeclaration.class);
        register(RECORD_DECLARATION__METHODS, RecordDeclaration.class, MethodDeclaration.class);
        register(RETURN_STATEMENT__RETURN_VALUES, ReturnStatement.class, Expression.class);
        register(SUBSCRIPT_EXPRESSION__ARRAY_EXPRESSION, SubscriptExpression.class, Expression.class);
        register(SUBSCRIPT_EXPRESSION__SUBSCRIPT_EXPRESSION, SubscriptExpression.class, Expression.class);
        register(TRANSLATION_UNIT__DECLARATIONS, TranslationUnitDeclaration.class, Declaration.class);
        register(UNARY_OPERATOR__INPUT, UnaryOperator.class, Expression.class);
        register(VALUE_DECLARATION__TYPE, ValueDeclaration.class, Type.class);
        register(VARIABLE_DECLARATION__INITIALIZER, VariableDeclaration.class, Expression.class);
        register(WHILE_STATEMENT__CONDITION, WhileStatement.class, Expression.class);
        register(WHILE_STATEMENT__STATEMENT, WhileStatement.class, Statement.class);
    }

    private Edges() {
        /* should not be instantiated */
    }

    /**
     * Registers an AST edge type to be found by {@link WildcardGraphPattern}s and by the general {@link Builder}.
     * @param edge the edge
     * @param sClass node class of the edge source
     * @param tClass node class of the edge target
     * @param <S> type of the edge source
     * @param <T> type of the edge target
     */
    private static <S extends Node, T extends Node> void register(IEdge<S, T> edge, Class<S> sClass, Class<T> tClass) {
        edge.setSourceClass(sClass);
        edge.setTargetClass(tClass);
        fromType.computeIfAbsent(sClass, c -> new ArrayList<>()).add(edge);
        toType.computeIfAbsent(tClass, c -> new ArrayList<>()).add(edge);
    }

    /**
     * Gets the list of edges with the given node class as target.
     * @param tClass the target node class
     * @param <T> the target node type
     * @return the list of edges
     */
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

}
