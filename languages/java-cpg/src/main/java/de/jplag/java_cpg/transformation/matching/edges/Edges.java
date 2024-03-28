package de.jplag.java_cpg.transformation.matching.edges;

import static de.jplag.java_cpg.transformation.matching.edges.IEdge.EdgeCategory.ANALYTIC;
import static de.jplag.java_cpg.transformation.matching.edges.IEdge.EdgeCategory.REFERENCE;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

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
    private static final Map<Class<?>, List<IEdge<?, ?>>> edgesBySourceType;
    /**
     * A {@link Map} to retrieve all {@link IEdge}s with a specific target type.
     */
    private static final Map<Class<?>, List<IEdge<?, ?>>> edgesByTargetType;

    public static final CpgEdge<AssignExpression, Expression> ASSIGN_EXPRESSION__LHS = CpgEdge.listValued(AssignExpression::getLhs,
            AssignExpression::setLhs);
    public static final CpgEdge<AssignExpression, Expression> ASSIGN_EXPRESSION__RHS = CpgEdge.listValued(AssignExpression::getRhs,
            AssignExpression::setRhs);
    public static final CpgEdge<BinaryOperator, Expression> BINARY_OPERATOR__LHS = new CpgEdge<>(BinaryOperator::getLhs, BinaryOperator::setLhs);
    public static final CpgAttributeEdge<BinaryOperator, String> BINARY_OPERATOR__OPERATOR_CODE = new CpgAttributeEdge<>(
            BinaryOperator::getOperatorCode, BinaryOperator::setOperatorCode);
    public static final CpgEdge<BinaryOperator, Expression> BINARY_OPERATOR__RHS = new CpgEdge<>(BinaryOperator::getRhs, BinaryOperator::setRhs);
    public static final CpgMultiEdge<Block, Declaration> BLOCK__DECLARATIONS = CpgMultiEdge.nodeValued(Block::getDeclarations, REFERENCE);
    public static final CpgMultiEdge<Block, Statement> BLOCK__STATEMENTS = CpgMultiEdge.edgeValued(Block::getStatementEdges);
    public static final CpgMultiEdge<CallExpression, Expression> CALL_EXPRESSION__ARGUMENTS = CpgMultiEdge
            .edgeValued(CallExpression::getArgumentEdges);
    public static final CpgEdge<CallExpression, Expression> CALL_EXPRESSION__CALLEE = new CpgEdge<>(CallExpression::getCallee,
            CallExpression::setCallee);
    public static final CpgMultiEdge<CallExpression, FunctionDeclaration> CALL_EXPRESSION__INVOKES = CpgMultiEdge
            .edgeValued(CallExpression::getInvokeEdges, REFERENCE);
    public static final CpgMultiEdge<Component, TranslationUnitDeclaration> COMPONENT__TRANSLATION_UNITS = CpgMultiEdge
            .nodeValued(Component::getTranslationUnits);
    public static final CpgMultiEdge<DeclarationStatement, Declaration> DECLARATION_STATEMENT__DECLARATIONS = CpgMultiEdge
            .edgeValued(DeclarationStatement::getDeclarationEdges);
    public static final CpgAttributeEdge<FieldDeclaration, List<String>> FIELD_DECLARATION__MODIFIERS = new CpgAttributeEdge<>(
            FieldDeclaration::getModifiers, FieldDeclaration::setModifiers);
    public static final CpgEdge<ForStatement, Expression> FOR_STATEMENT__CONDITION = new CpgEdge<>(ForStatement::getCondition,
            ForStatement::setCondition);
    public static final CpgEdge<ForStatement, Statement> FOR_STATEMENT__INITIALIZER_STATEMENT = new CpgEdge<>(ForStatement::getInitializerStatement,
            ForStatement::setInitializerStatement);
    public static final CpgEdge<ForStatement, Statement> FOR_STATEMENT__ITERATION_STATEMENT = new CpgEdge<>(ForStatement::getIterationStatement,
            ForStatement::setIterationStatement);
    public static final CpgEdge<ForStatement, Statement> FOR_STATEMENT__STATEMENT = new CpgEdge<>(ForStatement::getStatement,
            ForStatement::setStatement);
    public static final CpgMultiEdge<FunctionDeclaration, FunctionDeclaration> FUNCTION_DECLARATION__OVERRIDES = CpgMultiEdge
            .nodeValued(FunctionDeclaration::getOverrides, REFERENCE);
    public static final CpgMultiEdge<FunctionDeclaration, FunctionDeclaration> FUNCTION_DECLARATION__OVERRIDDEN_BY = CpgMultiEdge
            .nodeValued(FunctionDeclaration::getOverriddenBy, REFERENCE);
    public static final CpgMultiEdge<FunctionType, Type> FUNCTION_TYPE__PARAMETERS = CpgMultiEdge.nodeValued(FunctionType::getParameters);
    public static final CpgMultiEdge<FunctionType, Type> FUNCTION_TYPE__RETURN_TYPES = CpgMultiEdge.nodeValued(FunctionType::getReturnTypes);
    public static final CpgEdge<IfStatement, Expression> IF_STATEMENT__CONDITION = new CpgEdge<>(IfStatement::getCondition,
            IfStatement::setCondition);
    public static final CpgEdge<IfStatement, Statement> IF_STATEMENT__ELSE_STATEMENT = new CpgEdge<>(IfStatement::getElseStatement,
            IfStatement::setElseStatement);
    public static final CpgEdge<IfStatement, Statement> IF_STATEMENT__THEN_STATEMENT = new CpgEdge<>(IfStatement::getThenStatement,
            IfStatement::setThenStatement);
    public static final CpgEdge<MemberExpression, RecordDeclaration> MEMBER_EXPRESSION__RECORD_DECLARATION = new CpgEdge<>(EdgeUtil::getRecord, null,
            ANALYTIC);
    public static final CpgEdge<MemberExpression, Expression> MEMBER_EXPRESSION__BASE = new CpgEdge<>(MemberExpression::getBase,
            MemberExpression::setBase);
    public static final CpgEdge<MethodDeclaration, Statement> METHOD_DECLARATION__BODY = new CpgEdge<>(MethodDeclaration::getBody,
            MethodDeclaration::setBody);
    public static final CpgMultiEdge<MethodDeclaration, ParameterDeclaration> METHOD_DECLARATION__PARAMETERS = CpgMultiEdge
            .nodeValued(MethodDeclaration::getParameters);
    public static final CpgEdge<MethodDeclaration, RecordDeclaration> METHOD_DECLARATION__RECORD_DECLARATION = new CpgEdge<>(
            MethodDeclaration::getRecordDeclaration, MethodDeclaration::setRecordDeclaration, REFERENCE);
    public static final CpgMultiEdge<NamespaceDeclaration, Declaration> NAMESPACE_DECLARATION__DECLARATIONS = CpgMultiEdge
            .nodeValued(NamespaceDeclaration::getDeclarations);
    public static final CpgAttributeEdge<Node, PhysicalLocation> NODE__LOCATION = new CpgAttributeEdge<>(Node::getLocation, Node::setLocation);
    public static final CpgAttributeEdge<Declaration, Name> NODE__NAME = new CpgAttributeEdge<>(Node::getName, Node::setName);
    public static final CpgAttributeEdge<Declaration, String> NODE__LOCAL_NAME = new CpgAttributeEdge<>(EdgeUtil::getLocalName, null);
    public static final CpgAttributeEdge<MethodDeclaration, String> METHOD_DECLARATION__LOCAL_NAME = new CpgAttributeEdge<>(EdgeUtil::getLocalName,
            null);

    public static final CpgEdge<ObjectType, RecordDeclaration> OBJECT_TYPE__RECORD_DECLARATION = new CpgEdge<>(ObjectType::getRecordDeclaration,
            ObjectType::setRecordDeclaration, REFERENCE);
    public static final CpgMultiEdge<RecordDeclaration, FieldDeclaration> RECORD_DECLARATION__FIELDS = CpgMultiEdge
            .edgeValued(RecordDeclaration::getFieldEdges);
    public static final CpgMultiEdge<RecordDeclaration, MethodDeclaration> RECORD_DECLARATION__METHODS = CpgMultiEdge
            .edgeValued(RecordDeclaration::getMethodEdges);
    public static final CpgAttributeEdge<RecordDeclaration, Name> RECORD_DECLARATION__NAME = new CpgAttributeEdge<>(Node::getName, Node::setName);
    public static final CpgMultiEdge<RecordDeclaration, ConstructorDeclaration> RECORD_DECLARATION__CONSTRUCTORS = CpgMultiEdge
            .edgeValued(RecordDeclaration::getConstructorEdges);
    public static final CpgEdge<Reference, Declaration> REFERENCE__REFERS_TO = new CpgEdge<>(Reference::getRefersTo, Reference::setRefersTo,
            REFERENCE);
    public static final CpgMultiEdge<ReturnStatement, Expression> RETURN_STATEMENT__RETURN_VALUES = CpgMultiEdge
            .nodeValued(ReturnStatement::getReturnValues);
    public static final CpgMultiEdge<Statement, VariableDeclaration> STATEMENT__LOCALS = CpgMultiEdge.edgeValued(Statement::getLocalEdges, REFERENCE);
    public static final CpgEdge<SubscriptExpression, Expression> SUBSCRIPT_EXPRESSION__SUBSCRIPT_EXPRESSION = new CpgEdge<>(
            SubscriptExpression::getSubscriptExpression, SubscriptExpression::setSubscriptExpression);
    public static final CpgEdge<SubscriptExpression, Expression> SUBSCRIPT_EXPRESSION__ARRAY_EXPRESSION = new CpgEdge<>(
            SubscriptExpression::getArrayExpression, SubscriptExpression::setArrayExpression);
    public static final CpgMultiEdge<TranslationUnitDeclaration, Declaration> TRANSLATION_UNIT__DECLARATIONS = CpgMultiEdge
            .edgeValued(TranslationUnitDeclaration::getDeclarationEdges);
    public static final CpgAttributeEdge<IncompleteType, String> TYPE__TYPE_NAME = new CpgAttributeEdge<>(Type::getTypeName, null);
    public static final CpgEdge<UnaryOperator, Expression> UNARY_OPERATOR__INPUT = new CpgEdge<>(UnaryOperator::getInput, UnaryOperator::setInput);
    public static final CpgAttributeEdge<UnaryOperator, String> UNARY_OPERATOR__OPERATOR_CODE = new CpgAttributeEdge<>(UnaryOperator::getOperatorCode,
            UnaryOperator::setOperatorCode);
    public static final CpgMultiEdge<ValueDeclaration, Reference> VALUE_DECLARATION__USAGES = CpgMultiEdge.edgeValued(ValueDeclaration::getUsageEdges,
            REFERENCE);
    public static final CpgEdge<ValueDeclaration, Type> VALUE_DECLARATION__TYPE = new CpgEdge<>(ValueDeclaration::getType, ValueDeclaration::setType);
    public static final CpgEdge<VariableDeclaration, Expression> VARIABLE_DECLARATION__INITIALIZER = new CpgEdge<>(
            VariableDeclaration::getInitializer, VariableDeclaration::setInitializer);
    public static final CpgEdge<WhileStatement, Statement> WHILE_STATEMENT__STATEMENT = new CpgEdge<>(WhileStatement::getStatement,
            WhileStatement::setStatement);
    public static final CpgEdge<WhileStatement, Expression> WHILE_STATEMENT__CONDITION = new CpgEdge<>(WhileStatement::getCondition,
            WhileStatement::setCondition);

    public static final CpgEdge<DoStatement, Statement> DO_STATEMENT__STATEMENT = new CpgEdge<>(DoStatement::getStatement, DoStatement::setStatement);
    public static final CpgEdge<DoStatement, Expression> DO_STATEMENT__CONDITION = new CpgEdge<>(DoStatement::getCondition,
            DoStatement::setCondition);

    static {
        edgesBySourceType = new HashMap<>();
        edgesByTargetType = new HashMap<>();

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
     * @param <T> type of the edge source
     * @param <R> type of the related node
     */
    private static <T extends Node, R extends Node> void register(IEdge<T, R> edge, Class<T> sClass, Class<R> tClass) {
        edge.setSourceClass(sClass);
        edge.setRelatedClass(tClass);
        edgesBySourceType.computeIfAbsent(sClass, c -> new ArrayList<>()).add(edge);
        edgesByTargetType.computeIfAbsent(tClass, c -> new ArrayList<>()).add(edge);
    }

    /**
     * Gets the list of edges with the given node class as target.
     * @param tClass the target node class
     * @param <R> the related node type
     */
    public static <R extends Node> void getEdgesToType(Class<R> tClass, Consumer<IEdge<? extends Node, ? super R>> consumer) {
        Class<? super R> type = tClass;
        while (Node.class.isAssignableFrom(type)) {
            edgesByTargetType.getOrDefault(type, List.of()).stream().map(e -> (IEdge<? extends Node, ? super R>) e).forEach(consumer);
            type = getSuperclass(type);
        }
    }

    private static <T extends Node> Class<? super T> getSuperclass(Class<? super T> type) {
        if (!type.getSuperclass().equals(type)) {
            return type.getSuperclass();
        }
        return (Class<? super T>) type.getGenericSuperclass();
    }

}
