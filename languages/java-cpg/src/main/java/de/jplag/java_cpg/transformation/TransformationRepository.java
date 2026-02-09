package de.jplag.java_cpg.transformation;

import static de.jplag.java_cpg.transformation.GraphTransformation.ExecutionOrder.ASCENDING_LOCATION;
import static de.jplag.java_cpg.transformation.GraphTransformation.ExecutionPhase.AST_TRANSFORM;
import static de.jplag.java_cpg.transformation.GraphTransformation.ExecutionPhase.CPG_TRANSFORM;
import static de.jplag.java_cpg.transformation.GraphTransformation.ExecutionPhase.OBLIGATORY;
import static de.jplag.java_cpg.transformation.Role.ARGUMENT;
import static de.jplag.java_cpg.transformation.Role.BODY;
import static de.jplag.java_cpg.transformation.Role.CLASS_DECLARATION;
import static de.jplag.java_cpg.transformation.Role.CONDITION;
import static de.jplag.java_cpg.transformation.Role.CONSTRUCTOR_DECLARATION;
import static de.jplag.java_cpg.transformation.Role.CONTAINING_FILE;
import static de.jplag.java_cpg.transformation.Role.CONTAINING_RECORD;
import static de.jplag.java_cpg.transformation.Role.CONTAINING_STATEMENT;
import static de.jplag.java_cpg.transformation.Role.DECLARATION;
import static de.jplag.java_cpg.transformation.Role.DECLARATION_CONTAINER;
import static de.jplag.java_cpg.transformation.Role.DECLARATION_STATEMENT;
import static de.jplag.java_cpg.transformation.Role.DEFINING_RECORD;
import static de.jplag.java_cpg.transformation.Role.DEFINING_RECORD_REFERENCE;
import static de.jplag.java_cpg.transformation.Role.DO_STATEMENT;
import static de.jplag.java_cpg.transformation.Role.ELSE_STATEMENT;
import static de.jplag.java_cpg.transformation.Role.EMPTY_RECORD;
import static de.jplag.java_cpg.transformation.Role.FIELD_DECLARATION;
import static de.jplag.java_cpg.transformation.Role.FIELD_USAGE;
import static de.jplag.java_cpg.transformation.Role.FIELD_VALUE;
import static de.jplag.java_cpg.transformation.Role.FIRST_CONSTANT_USAGE;
import static de.jplag.java_cpg.transformation.Role.FOR_STATEMENT;
import static de.jplag.java_cpg.transformation.Role.GETTER_METHOD_REFERENCE;
import static de.jplag.java_cpg.transformation.Role.IF_STATEMENT;
import static de.jplag.java_cpg.transformation.Role.INITIALIZATION_STATEMENT;
import static de.jplag.java_cpg.transformation.Role.INNER_CONDITION;
import static de.jplag.java_cpg.transformation.Role.ITERATION_STATEMENT;
import static de.jplag.java_cpg.transformation.Role.MEMBER_CALL;
import static de.jplag.java_cpg.transformation.Role.METHOD_BLOCK;
import static de.jplag.java_cpg.transformation.Role.METHOD_DECLARATION;
import static de.jplag.java_cpg.transformation.Role.METHOD_TYPE;
import static de.jplag.java_cpg.transformation.Role.OPTIONAL_CLASS;
import static de.jplag.java_cpg.transformation.Role.OPTIONAL_OBJECT;
import static de.jplag.java_cpg.transformation.Role.RECORD_DECLARATION;
import static de.jplag.java_cpg.transformation.Role.RETURN_STATEMENT;
import static de.jplag.java_cpg.transformation.Role.RETURN_TYPE;
import static de.jplag.java_cpg.transformation.Role.RETURN_VALUE;
import static de.jplag.java_cpg.transformation.Role.SCOPE_BLOCK;
import static de.jplag.java_cpg.transformation.Role.SURROUNDING_BLOCK;
import static de.jplag.java_cpg.transformation.Role.THEN_STATEMENT;
import static de.jplag.java_cpg.transformation.Role.THROW_EXCEPTION;
import static de.jplag.java_cpg.transformation.Role.USING_RECORD;
import static de.jplag.java_cpg.transformation.Role.VARIABLE_DECLARATION;
import static de.jplag.java_cpg.transformation.Role.VARIABLE_USAGE;
import static de.jplag.java_cpg.transformation.Role.VARIABLE_VALUE;
import static de.jplag.java_cpg.transformation.Role.WHILE_STATEMENT;
import static de.jplag.java_cpg.transformation.Role.WHILE_STATEMENT_BODY;
import static de.jplag.java_cpg.transformation.matching.edges.Edges.BLOCK__STATEMENTS;
import static de.jplag.java_cpg.transformation.matching.edges.Edges.CALL_EXPRESSION__ARGUMENTS;
import static de.jplag.java_cpg.transformation.matching.edges.Edges.CALL_EXPRESSION__CALLEE;
import static de.jplag.java_cpg.transformation.matching.edges.Edges.CALL_EXPRESSION__INVOKES;
import static de.jplag.java_cpg.transformation.matching.edges.Edges.DECLARATION_STATEMENT__DECLARATIONS;
import static de.jplag.java_cpg.transformation.matching.edges.Edges.DO_STATEMENT__STATEMENT;
import static de.jplag.java_cpg.transformation.matching.edges.Edges.FIELD_DECLARATION__MODIFIERS;
import static de.jplag.java_cpg.transformation.matching.edges.Edges.FOR_STATEMENT__CONDITION;
import static de.jplag.java_cpg.transformation.matching.edges.Edges.FOR_STATEMENT__INITIALIZER_STATEMENT;
import static de.jplag.java_cpg.transformation.matching.edges.Edges.FOR_STATEMENT__ITERATION_STATEMENT;
import static de.jplag.java_cpg.transformation.matching.edges.Edges.FOR_STATEMENT__STATEMENT;
import static de.jplag.java_cpg.transformation.matching.edges.Edges.FUNCTION_TYPE__RETURN_TYPES;
import static de.jplag.java_cpg.transformation.matching.edges.Edges.IF_STATEMENT__CONDITION;
import static de.jplag.java_cpg.transformation.matching.edges.Edges.IF_STATEMENT__ELSE_STATEMENT;
import static de.jplag.java_cpg.transformation.matching.edges.Edges.IF_STATEMENT__THEN_STATEMENT;
import static de.jplag.java_cpg.transformation.matching.edges.Edges.MEMBER_EXPRESSION__BASE;
import static de.jplag.java_cpg.transformation.matching.edges.Edges.MEMBER_EXPRESSION__RECORD_DECLARATION;
import static de.jplag.java_cpg.transformation.matching.edges.Edges.METHOD_DECLARATION__BODY;
import static de.jplag.java_cpg.transformation.matching.edges.Edges.METHOD_DECLARATION__LOCAL_NAME;
import static de.jplag.java_cpg.transformation.matching.edges.Edges.METHOD_DECLARATION__RECORD_DECLARATION;
import static de.jplag.java_cpg.transformation.matching.edges.Edges.NAMESPACE_DECLARATION__DECLARATIONS;
import static de.jplag.java_cpg.transformation.matching.edges.Edges.NODE__LOCATION;
import static de.jplag.java_cpg.transformation.matching.edges.Edges.NODE__NAME;
import static de.jplag.java_cpg.transformation.matching.edges.Edges.RECORD_DECLARATION__CONSTRUCTORS;
import static de.jplag.java_cpg.transformation.matching.edges.Edges.RECORD_DECLARATION__FIELDS;
import static de.jplag.java_cpg.transformation.matching.edges.Edges.RECORD_DECLARATION__METHODS;
import static de.jplag.java_cpg.transformation.matching.edges.Edges.RECORD_DECLARATION__NAME;
import static de.jplag.java_cpg.transformation.matching.edges.Edges.REFERENCE__REFERS_TO;
import static de.jplag.java_cpg.transformation.matching.edges.Edges.RETURN_STATEMENT__RETURN_VALUES;
import static de.jplag.java_cpg.transformation.matching.edges.Edges.STATEMENT__LOCALS;
import static de.jplag.java_cpg.transformation.matching.edges.Edges.TRANSLATION_UNIT__DECLARATIONS;
import static de.jplag.java_cpg.transformation.matching.edges.Edges.UNARY_OPERATOR__INPUT;
import static de.jplag.java_cpg.transformation.matching.edges.Edges.UNARY_OPERATOR__OPERATOR_CODE;
import static de.jplag.java_cpg.transformation.matching.edges.Edges.VALUE_DECLARATION__TYPE;
import static de.jplag.java_cpg.transformation.matching.edges.Edges.VALUE_DECLARATION__USAGES;
import static de.jplag.java_cpg.transformation.matching.edges.Edges.VARIABLE_DECLARATION__INITIALIZER;
import static de.jplag.java_cpg.transformation.matching.edges.Edges.WHILE_STATEMENT__CONDITION;
import static de.jplag.java_cpg.transformation.matching.edges.Edges.WHILE_STATEMENT__STATEMENT;
import static de.jplag.java_cpg.transformation.matching.pattern.PatternUtil.attributeContains;
import static de.jplag.java_cpg.transformation.matching.pattern.PatternUtil.attributeEquals;
import static de.jplag.java_cpg.transformation.matching.pattern.PatternUtil.attributeToStringEquals;
import static de.jplag.java_cpg.transformation.matching.pattern.PatternUtil.attributeToStringStartsWith;
import static de.jplag.java_cpg.transformation.matching.pattern.PatternUtil.isConstant;
import static de.jplag.java_cpg.transformation.matching.pattern.PatternUtil.isFieldReference;
import static de.jplag.java_cpg.transformation.matching.pattern.PatternUtil.nElements;
import static de.jplag.java_cpg.transformation.matching.pattern.PatternUtil.notEmpty;
import static de.jplag.java_cpg.transformation.matching.pattern.PatternUtil.notInstanceOf;
import static de.jplag.java_cpg.transformation.matching.pattern.PatternUtil.notNull;
import static de.jplag.java_cpg.transformation.matching.pattern.PatternUtil.nthElement;
import static de.jplag.java_cpg.transformation.matching.pattern.PatternUtil.or;

import de.fraunhofer.aisec.cpg.graph.Node;
import de.fraunhofer.aisec.cpg.graph.declarations.ConstructorDeclaration;
import de.fraunhofer.aisec.cpg.graph.declarations.Declaration;
import de.fraunhofer.aisec.cpg.graph.declarations.FieldDeclaration;
import de.fraunhofer.aisec.cpg.graph.declarations.MethodDeclaration;
import de.fraunhofer.aisec.cpg.graph.declarations.NamespaceDeclaration;
import de.fraunhofer.aisec.cpg.graph.declarations.RecordDeclaration;
import de.fraunhofer.aisec.cpg.graph.declarations.TranslationUnitDeclaration;
import de.fraunhofer.aisec.cpg.graph.declarations.VariableDeclaration;
import de.fraunhofer.aisec.cpg.graph.statements.DeclarationStatement;
import de.fraunhofer.aisec.cpg.graph.statements.DoStatement;
import de.fraunhofer.aisec.cpg.graph.statements.EmptyStatement;
import de.fraunhofer.aisec.cpg.graph.statements.ForStatement;
import de.fraunhofer.aisec.cpg.graph.statements.IfStatement;
import de.fraunhofer.aisec.cpg.graph.statements.ReturnStatement;
import de.fraunhofer.aisec.cpg.graph.statements.Statement;
import de.fraunhofer.aisec.cpg.graph.statements.WhileStatement;
import de.fraunhofer.aisec.cpg.graph.statements.expressions.Block;
import de.fraunhofer.aisec.cpg.graph.statements.expressions.Expression;
import de.fraunhofer.aisec.cpg.graph.statements.expressions.MemberCallExpression;
import de.fraunhofer.aisec.cpg.graph.statements.expressions.MemberExpression;
import de.fraunhofer.aisec.cpg.graph.statements.expressions.Reference;
import de.fraunhofer.aisec.cpg.graph.statements.expressions.UnaryOperator;
import de.fraunhofer.aisec.cpg.graph.types.FunctionType;
import de.fraunhofer.aisec.cpg.graph.types.ObjectType;
import de.jplag.java_cpg.transformation.matching.edges.CpgEdge;
import de.jplag.java_cpg.transformation.matching.pattern.GraphPatternBuilder;
import de.jplag.java_cpg.transformation.matching.pattern.MultiGraphPattern;
import de.jplag.java_cpg.transformation.matching.pattern.PatternUtil;
import de.jplag.java_cpg.transformation.matching.pattern.SimpleGraphPattern;

/**
 * Contains factory methods to create different {@link GraphTransformation}s.
 */
public class TransformationRepository {
    /*
     * These constants are supposed to avoid uselessly building the same graph transformations multiple times.
     * Alternatively, all factory methods could be public and use private fields to create a kind-of singleton pattern.
     */
    /**
     * Constant <code>ifWithNegatedConditionResolution</code>.
     */
    public static final GraphTransformation ifWithNegatedConditionResolution = ifWithNegatedConditionResolution();
    /**
     * Constant <code>forStatementToWhileStatement</code>.
     */
    public static final GraphTransformation forStatementToWhileStatement = forStatementToWhileStatement();
    /**
     * Constant <code>removeGetterMethod</code>.
     */
    public static final GraphTransformation removeGetterMethod = removeGetterMethod();
    /**
     * Constant <code>removeUnusedVariableDeclaration</code>.
     */
    public static final GraphTransformation removeUnusedVariableDeclaration = removeUnusedVariableDeclaration();

    /**
     * Constant <code>removeEmptyDeclarationStatement</code>.
     */
    public static final GraphTransformation removeEmptyDeclarationStatement = removeEmptyDeclarationStatement();
    /**
     * Constant <code>removeLibraryRecord</code>.
     */
    public static final GraphTransformation removeLibraryRecord = removeLibraryRecord();
    /**
     * Constant <code>removeLibraryField</code>.
     */
    public static final GraphTransformation removeLibraryField = removeLibraryField();
    /**
     * Constant <code>moveConstantToOnlyUsingClass</code>.
     */
    public static final GraphTransformation moveConstantToOnlyUsingClass = moveConstantToOnlyUsingClass();
    /**
     * Constant <code>inlineSingleUseVariable</code>.
     */
    public static final GraphTransformation inlineSingleUseVariable = inlineSingleUseVariable();

    /**
     * Constant <code>inlineSingleUseConstant</code>.
     */
    public static final GraphTransformation inlineSingleUseConstant = inlineSingleUseConstant();
    /**
     * Constant <code>removeEmptyConstructor</code>.
     */
    public static final GraphTransformation removeEmptyConstructor = removeEmptyConstructor();

    /**
     * Constant <code>removeEmptyRecord</code>.
     */
    public static final GraphTransformation removeEmptyRecord = removeEmptyRecord();
    /**
     * Constant <code>removeImplicitStandardConstructor</code>.
     */
    public static final GraphTransformation removeImplicitStandardConstructor = removeImplicitStandardConstructor();
    /**
     * Constant <code>removeOptionalOfCall</code>.
     */
    public static final GraphTransformation removeOptionalOfCall = removeOptionalOfCall();
    /**
     * Constant <code>removeOptionalGetCall</code>.
     */
    public static final GraphTransformation removeOptionalGetCall = removeOptionalGetCall();
    /**
     * Constant <code>removeUnsupportedConstructor</code>.
     */
    public static final GraphTransformation removeUnsupportedConstructor = removeUnsupportedConstructor();
    /**
     * Constant <code>removeUnsupportedMethod</code>.
     */
    public static final GraphTransformation removeUnsupportedMethod = removeUnsupportedMethod();
    /**
     * Constant <code>wrapElseStatement</code>.
     */
    public static final GraphTransformation wrapElseStatement = wrapElseStatement();
    /**
     * Constant <code>wrapForStatement</code>.
     */
    public static final GraphTransformation wrapForStatement = wrapForStatement();
    /**
     * Constant <code>wrapThenStatement</code>.
     */
    public static final GraphTransformation wrapThenStatement = wrapThenStatement();
    /**
     * Constant <code>wrapWhileStatement</code>.
     */
    public static final GraphTransformation wrapWhileStatement = wrapWhileStatement();
    /**
     * Constant <code>wrapDoStatement</code>.
     */
    public static final GraphTransformation wrapDoStatement = wrapDoStatement();

    private TransformationRepository() {
    }

    /**
     * Creates a {@link GraphTransformation} that un-negates the condition of an if statement and swaps the then and else
     * blocks.
     * @return the graph transformation object
     */
    private static GraphTransformation ifWithNegatedConditionResolution() {
        SimpleGraphPattern<IfStatement> sourcePattern = new GraphPatternBuilder() {
            @Override
            public SimpleGraphPattern<IfStatement> build() {
                return create(IfStatement.class, IF_STATEMENT,
                        related(IF_STATEMENT__CONDITION, UnaryOperator.class, CONDITION,
                                property(attributeEquals(UNARY_OPERATOR__OPERATOR_CODE, "!")),
                                related(UNARY_OPERATOR__INPUT, Expression.class, INNER_CONDITION)),
                        related(IF_STATEMENT__THEN_STATEMENT, Statement.class, THEN_STATEMENT),
                        related(IF_STATEMENT__ELSE_STATEMENT, Statement.class, ELSE_STATEMENT));
            }
        }.build();

        SimpleGraphPattern<IfStatement> targetPattern = new GraphPatternBuilder() {

            @Override
            public SimpleGraphPattern<IfStatement> build() {
                return create(IfStatement.class, IF_STATEMENT, related(IF_STATEMENT__CONDITION, Expression.class, INNER_CONDITION),
                        related(IF_STATEMENT__THEN_STATEMENT, Statement.class, ELSE_STATEMENT),
                        related(IF_STATEMENT__ELSE_STATEMENT, Statement.class, THEN_STATEMENT));
            }
        }.build();

        return GraphTransformation.Builder.from(sourcePattern, targetPattern, "ifWithNegatedConditionResolution", AST_TRANSFORM)
                .setExecutionOrder(ASCENDING_LOCATION).build();
    }

    /**
     * Creates a {@link GraphTransformation} that replaces a {@link VariableDeclaration} of an unused variable by an
     * {@link EmptyStatement}.
     * @return the graph transformation object
     */
    private static GraphTransformation removeUnusedVariableDeclaration() {
        MultiGraphPattern sourcePattern = new GraphPatternBuilder() {
            @Override
            public MultiGraphPattern build() {
                return multiRoot(
                        wildcardParent(VariableDeclaration.class, VARIABLE_DECLARATION, setRepresentingNode(),
                                property(nElements(VALUE_DECLARATION__USAGES, 0))),
                        create(Statement.class, CONTAINING_STATEMENT,
                                relatedExisting1ToN(STATEMENT__LOCALS, VariableDeclaration.class, VARIABLE_DECLARATION)));
            }
        }.build();
        MultiGraphPattern targetPattern = new GraphPatternBuilder() {
            @Override
            public MultiGraphPattern build() {
                return multiRoot(
                        // Remove VariableDeclaration
                        emptyWildcardParent(),
                        // Remove reference to local variable
                        create(Statement.class, CONTAINING_STATEMENT));
            }
        }.build();

        return GraphTransformation.Builder.from(sourcePattern, targetPattern, "removeUnusedVariableDeclarations", CPG_TRANSFORM).build();
    }

    /**
     * Creates a {@link GraphTransformation} that removes a {@link DeclarationStatement} with no {@link Declaration}s.
     * @return the graph transformation object
     */
    private static GraphTransformation removeEmptyDeclarationStatement() {
        SimpleGraphPattern<Node> sourcePattern = new GraphPatternBuilder() {
            @Override
            public SimpleGraphPattern<Node> build() {
                return wildcardParent(DeclarationStatement.class, DECLARATION_STATEMENT, setRepresentingNode(),
                        property(PatternUtil.nElements(DECLARATION_STATEMENT__DECLARATIONS, 0)));
            }
        }.build();
        SimpleGraphPattern<Node> targetPattern = new GraphPatternBuilder() {
            @Override
            public SimpleGraphPattern<Node> build() {
                return emptyWildcardParent();
            }
        }.build();

        return GraphTransformation.Builder.from(sourcePattern, targetPattern, "removeEmptyDeclarationStatement", CPG_TRANSFORM).build();
    }

    /**
     * Creates a {@link GraphTransformation} that replaces {@link ForStatement}s by equivalent {@link WhileStatement}s.
     * @return the graph transformation
     */
    private static GraphTransformation forStatementToWhileStatement() {
        SimpleGraphPattern<Node> sourcePattern = new GraphPatternBuilder() {
            @Override
            public SimpleGraphPattern<Node> build() {
                return wildcardParent(ForStatement.class, FOR_STATEMENT, setRepresentingNode(),
                        related(FOR_STATEMENT__INITIALIZER_STATEMENT, Statement.class, INITIALIZATION_STATEMENT),
                        related(FOR_STATEMENT__CONDITION, Expression.class, CONDITION),
                        related(FOR_STATEMENT__ITERATION_STATEMENT, Statement.class, ITERATION_STATEMENT),
                        related(FOR_STATEMENT__STATEMENT, Statement.class, BODY));
            }
        }.build();

        SimpleGraphPattern<Node> targetPattern = new GraphPatternBuilder() {
            @Override
            public SimpleGraphPattern<Node> build() {
                return wildcardParent(Block.class, SURROUNDING_BLOCK,
                        relatedConsecutive(BLOCK__STATEMENTS, Statement.class, node(Statement.class, INITIALIZATION_STATEMENT),
                                node(WhileStatement.class, WHILE_STATEMENT, related(WHILE_STATEMENT__CONDITION, Expression.class, CONDITION),
                                        related(WHILE_STATEMENT__STATEMENT, Block.class, WHILE_STATEMENT_BODY, relatedConsecutive(BLOCK__STATEMENTS,
                                                Statement.class, node(Statement.class, BODY), node(Statement.class, ITERATION_STATEMENT))))));
            }

        }.build();
        return GraphTransformation.Builder.from(sourcePattern, targetPattern, "forStmtToWhileStmt", AST_TRANSFORM)
                .setExecutionOrder(ASCENDING_LOCATION).build();

    }

    /**
     * Creates a {@link GraphTransformation} that removes fields without a proper location from RecordDeclarations. These
     * are fields of library classes that the CPG is unable to resolve correctly.
     * @return the graph transformation
     */
    private static GraphTransformation removeLibraryField() {
        /*
         * The SymbolResolver pass may interpret references to the standard library (e.g. java.util.List) as fields of the
         * current record. This transformation removes these fields.
         */
        SimpleGraphPattern<RecordDeclaration> sourcePattern = new GraphPatternBuilder() {
            @Override
            public SimpleGraphPattern<RecordDeclaration> build() {
                return create(RecordDeclaration.class, RECORD_DECLARATION, related1ToN(RECORD_DECLARATION__FIELDS, FieldDeclaration.class,
                        FIELD_DECLARATION, setRepresentingNode(), property(attributeEquals(NODE__LOCATION, null))));
            }
        }.build();
        SimpleGraphPattern<RecordDeclaration> targetPattern = new GraphPatternBuilder() {
            @Override
            public SimpleGraphPattern<RecordDeclaration> build() {
                return create(RecordDeclaration.class, RECORD_DECLARATION);
            }
        }.build();

        // SymbolResolver comes after TransformationPass -> Phase 2
        return GraphTransformation.Builder.from(sourcePattern, targetPattern, "removeLibraryFields", CPG_TRANSFORM).build();
    }

    /**
     * Creates a {@link GraphTransformation} that removes records without a proper location from
     * TranslationUnitDeclarations. These are classes from libraries that the CPG is unable to resolve correctly.
     * @return the graph transformation
     */
    private static GraphTransformation removeLibraryRecord() {
        SimpleGraphPattern<TranslationUnitDeclaration> sourcePattern = new GraphPatternBuilder() {
            @Override
            public SimpleGraphPattern<TranslationUnitDeclaration> build() {
                return create(TranslationUnitDeclaration.class, DECLARATION_CONTAINER, related1ToN(TRANSLATION_UNIT__DECLARATIONS,
                        RecordDeclaration.class, DECLARATION, setRepresentingNode(), property(attributeEquals(NODE__LOCATION, null))));
            }
        }.build();
        SimpleGraphPattern<TranslationUnitDeclaration> targetPattern = new GraphPatternBuilder() {
            @Override
            public SimpleGraphPattern<TranslationUnitDeclaration> build() {
                return create(TranslationUnitDeclaration.class, DECLARATION_CONTAINER);
            }
        }.build();

        return GraphTransformation.Builder.from(sourcePattern, targetPattern, "removeLibraryRecords", CPG_TRANSFORM).build();
    }

    /**
     * Creates a {@link GraphTransformation} that moves constant declarations that are only used in one class to that class.
     * This serves to counter the formation of constant classes.
     * @return the graph transformation
     */
    private static GraphTransformation moveConstantToOnlyUsingClass() {

        MultiGraphPattern sourcePattern = new GraphPatternBuilder() {
            @Override
            public MultiGraphPattern build() {
                return multiRoot(
                        create(RecordDeclaration.class, DEFINING_RECORD, related1ToN(RECORD_DECLARATION__FIELDS, FieldDeclaration.class,
                                FIELD_DECLARATION, setRepresentingNode(), stopRecursion(), // no transformations beyond this point
                                property(attributeContains(FIELD_DECLARATION__MODIFIERS, "final")),
                                property(attributeContains(FIELD_DECLARATION__MODIFIERS, "static")), property(notEmpty(VALUE_DECLARATION__USAGES)),
                                related(nthElement(VALUE_DECLARATION__USAGES, 0), MemberExpression.class, FIRST_CONSTANT_USAGE,
                                        related(MEMBER_EXPRESSION__RECORD_DECLARATION, RecordDeclaration.class, USING_RECORD,
                                                notEqualTo(DEFINING_RECORD))),
                                forAllRelated(VALUE_DECLARATION__USAGES, MemberExpression.class, FIELD_USAGE,
                                        relatedExisting(MEMBER_EXPRESSION__RECORD_DECLARATION, RecordDeclaration.class, USING_RECORD)))),
                        create(RecordDeclaration.class, USING_RECORD
                // field declaration should go here
                ), create(MemberExpression.class, FIRST_CONSTANT_USAGE, related(MEMBER_EXPRESSION__BASE, Reference.class, DEFINING_RECORD_REFERENCE,
                        relatedExisting(REFERENCE__REFERS_TO, RecordDeclaration.class, DEFINING_RECORD)))

                );
            }
        }.build();
        MultiGraphPattern targetPattern = new GraphPatternBuilder() {
            @Override
            public MultiGraphPattern build() {
                return multiRoot(create(RecordDeclaration.class, DEFINING_RECORD
                // field declaration removed from here
                ), create(RecordDeclaration.class, USING_RECORD, related1ToN(RECORD_DECLARATION__FIELDS, FieldDeclaration.class, FIELD_DECLARATION)),
                        create(MemberExpression.class, FIRST_CONSTANT_USAGE,
                                related(MEMBER_EXPRESSION__BASE, Reference.class, DEFINING_RECORD_REFERENCE,
                                        // adjust reference
                                        relatedExisting(REFERENCE__REFERS_TO, RecordDeclaration.class, USING_RECORD))));
            }
        }.build();

        return GraphTransformation.Builder.from(sourcePattern, targetPattern, "moveConstantToOnlyUsingClass", CPG_TRANSFORM).build();
    }

    /**
     * Creates a {@link GraphTransformation} that, for a variable that is only referenced once, replaces that reference by
     * the value of the variable, if the value is determined to be stable between the variable declaration and the
     * reference.
     * @return the graph transformation
     */
    private static GraphTransformation inlineSingleUseVariable() {
        MultiGraphPattern sourcePattern = new GraphPatternBuilder() {
            @Override
            public MultiGraphPattern build() {
                return multiRoot(
                        // parent pointer
                        wildcardParent(VariableDeclaration.class, VARIABLE_DECLARATION, setRepresentingNode(), property(notNull(NODE__LOCATION)),
                                property(nElements(VALUE_DECLARATION__USAGES, 1)),
                                related(nthElement(VALUE_DECLARATION__USAGES, 0), Reference.class, VARIABLE_USAGE),
                                related(VARIABLE_DECLARATION__INITIALIZER, Expression.class, VARIABLE_VALUE,
                                        assignedValueStableBetween(VARIABLE_DECLARATION, VARIABLE_USAGE))),
                        wildcardParent(Reference.class, VARIABLE_USAGE,
                                relatedExisting(REFERENCE__REFERS_TO, VariableDeclaration.class, VARIABLE_DECLARATION)),
                        wildcardParent(Block.class, SCOPE_BLOCK,
                                relatedExisting1ToN(STATEMENT__LOCALS, VariableDeclaration.class, VARIABLE_DECLARATION)));
            }
        }.build();

        MultiGraphPattern targetPattern = new GraphPatternBuilder() {
            @Override
            public MultiGraphPattern build() {
                return multiRoot(
                        // remove variable declaration from AST parent
                        emptyWildcardParent(),
                        // replace variable reference by value
                        wildcardParent(Expression.class, VARIABLE_VALUE),
                        // delete "local" edge
                        wildcardParent(Block.class, SCOPE_BLOCK));
            }
        }.build();

        return GraphTransformation.Builder.from(sourcePattern, targetPattern, "inlineSingleUseVariable", CPG_TRANSFORM)
                .setExecutionOrder(ASCENDING_LOCATION).build();
    }

    /**
     * Creates a {@link GraphTransformation} that, for a constant that is only referenced once, replaces that reference by
     * the value of the variable.
     * @return the graph transformation
     */
    private static GraphTransformation inlineSingleUseConstant() {
        MultiGraphPattern sourcePattern = new GraphPatternBuilder() {
            @Override
            public MultiGraphPattern build() {
                return multiRoot(
                        // parent pointer
                        create(RecordDeclaration.class, CONTAINING_RECORD,
                                related1ToN(RECORD_DECLARATION__FIELDS, FieldDeclaration.class, FIELD_DECLARATION, setRepresentingNode(),
                                        property(notNull(NODE__LOCATION)), property(attributeContains(FIELD_DECLARATION__MODIFIERS, "final")),
                                        property(attributeContains(FIELD_DECLARATION__MODIFIERS, "static")),
                                        property(nElements(VALUE_DECLARATION__USAGES, 1)),
                                        related(nthElement(VALUE_DECLARATION__USAGES, 0), Reference.class, FIELD_USAGE),
                                        related(VARIABLE_DECLARATION__INITIALIZER, Expression.class, FIELD_VALUE))),
                        wildcardParent(Reference.class, FIELD_USAGE,
                                relatedExisting(REFERENCE__REFERS_TO, FieldDeclaration.class, FIELD_DECLARATION)));
            }
        }.build();

        MultiGraphPattern targetPattern = new GraphPatternBuilder() {
            @Override
            public MultiGraphPattern build() {
                return multiRoot(
                        // remove field declaration from class
                        create(RecordDeclaration.class, CONTAINING_RECORD),
                        // replace variable reference by value
                        wildcardParent(Expression.class, FIELD_VALUE));
            }
        }.build();

        return GraphTransformation.Builder.from(sourcePattern, targetPattern, "inlineSingleUseConstant", CPG_TRANSFORM).build();
    }

    /**
     * Creates a {@link GraphTransformation} that removes ConstructorDeclarations without proper locations from records.
     * These are representations of the implicit constructor without parameters inherited from the Object class.
     * @return the graph transformation
     */
    private static GraphTransformation removeImplicitStandardConstructor() {
        SimpleGraphPattern<RecordDeclaration> sourcePattern = new GraphPatternBuilder() {
            @Override
            public SimpleGraphPattern<RecordDeclaration> build() {

                return create(RecordDeclaration.class, DEFINING_RECORD, related1ToN(RECORD_DECLARATION__CONSTRUCTORS, ConstructorDeclaration.class,
                        CONSTRUCTOR_DECLARATION, property(attributeEquals(NODE__LOCATION, null))));
            }
        }.build();

        SimpleGraphPattern<RecordDeclaration> targetPattern = new GraphPatternBuilder() {
            @Override
            public SimpleGraphPattern<RecordDeclaration> build() {
                // remove constructor declaration from record
                return create(RecordDeclaration.class, DEFINING_RECORD);
            }
        }.build();

        // Must run in PHASE_TWO, otherwise any reference to the standard constructor reinserts a ConstructorDeclaration
        return GraphTransformation.Builder.from(sourcePattern, targetPattern, "removeImplicitStandardConstructor", CPG_TRANSFORM).build();
    }

    /**
     * Creates a {@link GraphTransformation} that removes ConstructorDeclarations with an empty body.
     * @return the graph transformation
     */
    private static GraphTransformation removeEmptyConstructor() {
        SimpleGraphPattern<RecordDeclaration> sourcePattern = new GraphPatternBuilder() {
            @Override
            public SimpleGraphPattern<RecordDeclaration> build() {
                return create(RecordDeclaration.class, CONTAINING_RECORD,
                        related1ToN(RECORD_DECLARATION__CONSTRUCTORS, ConstructorDeclaration.class, CONSTRUCTOR_DECLARATION, setRepresentingNode(),
                                stopRecursion(),
                                related(METHOD_DECLARATION__BODY, Block.class, METHOD_BLOCK, property(nElements(BLOCK__STATEMENTS, 1)),
                                        related(nthElement(BLOCK__STATEMENTS, 0), ReturnStatement.class, RETURN_STATEMENT,
                                                property(attributeEquals(NODE__LOCATION, null))))));

            }
        }.build();

        SimpleGraphPattern<RecordDeclaration> targetPattern = new GraphPatternBuilder() {
            @Override
            public SimpleGraphPattern<RecordDeclaration> build() {
                return create(RecordDeclaration.class, CONTAINING_RECORD);
            }
        }.build();

        return GraphTransformation.Builder.from(sourcePattern, targetPattern, "removeEmptyConstructor", CPG_TRANSFORM).build();
    }

    /**
     * Creates a {@link GraphTransformation} that removes ConstructorDeclarations where the body consists only of a
     * ThrowStatement.
     * @return the graph transformation
     */
    private static GraphTransformation removeUnsupportedConstructor() {
        SimpleGraphPattern<RecordDeclaration> sourcePattern = new GraphPatternBuilder() {
            @Override
            public SimpleGraphPattern<RecordDeclaration> build() {
                return create(RecordDeclaration.class, CONTAINING_RECORD,
                        related1ToN(RECORD_DECLARATION__CONSTRUCTORS, ConstructorDeclaration.class, CONSTRUCTOR_DECLARATION, setRepresentingNode(),
                                stopRecursion(),
                                related(METHOD_DECLARATION__BODY, Block.class, METHOD_BLOCK, property(nElements(BLOCK__STATEMENTS, 2)),
                                        related(nthElement(BLOCK__STATEMENTS, 0), UnaryOperator.class, THROW_EXCEPTION,
                                                property(attributeEquals(UNARY_OPERATOR__OPERATOR_CODE, "throw"))),
                                        related(nthElement(BLOCK__STATEMENTS, 1), ReturnStatement.class, RETURN_STATEMENT,
                                                property(attributeEquals(NODE__LOCATION, null))))));

            }
        }.build();

        SimpleGraphPattern<RecordDeclaration> targetPattern = new GraphPatternBuilder() {
            @Override
            public SimpleGraphPattern<RecordDeclaration> build() {
                return create(RecordDeclaration.class, CONTAINING_RECORD);
            }
        }.build();

        return GraphTransformation.Builder.from(sourcePattern, targetPattern, "removeUnsupportedConstructor", CPG_TRANSFORM).build();
    }

    /**
     * Creates a {@link GraphTransformation} that removes MethodDeclarations where the body consists only of a
     * ThrowStatement.
     * @return the graph transformation
     */
    private static GraphTransformation removeUnsupportedMethod() {
        SimpleGraphPattern<RecordDeclaration> sourcePattern = new GraphPatternBuilder() {
            @Override
            public SimpleGraphPattern<RecordDeclaration> build() {
                return create(RecordDeclaration.class, CONTAINING_RECORD,
                        related1ToN(RECORD_DECLARATION__METHODS, MethodDeclaration.class, METHOD_DECLARATION, setRepresentingNode(), stopRecursion(),
                                related(METHOD_DECLARATION__BODY, Block.class, METHOD_BLOCK, property(nElements(BLOCK__STATEMENTS, 2)),
                                        related(nthElement(BLOCK__STATEMENTS, 0), UnaryOperator.class, THROW_EXCEPTION,
                                                property(attributeEquals(UNARY_OPERATOR__OPERATOR_CODE, "throw"))),
                                        related(nthElement(BLOCK__STATEMENTS, 1), ReturnStatement.class, RETURN_STATEMENT,
                                                property(attributeEquals(NODE__LOCATION, null))))));

            }
        }.build();

        SimpleGraphPattern<RecordDeclaration> targetPattern = new GraphPatternBuilder() {
            @Override
            public SimpleGraphPattern<RecordDeclaration> build() {
                return create(RecordDeclaration.class, CONTAINING_RECORD);
            }
        }.build();

        return GraphTransformation.Builder.from(sourcePattern, targetPattern, "removeUnsupportedMethod", CPG_TRANSFORM).build();
    }

    /**
     * Creates a {@link GraphTransformation} that removes MethodDeclarations with an empty body.
     * @return the graph transformation
     */
    private static GraphTransformation removeEmptyRecord() {
        SimpleGraphPattern<NamespaceDeclaration> sourcePattern = new GraphPatternBuilder() {
            @Override
            public SimpleGraphPattern<NamespaceDeclaration> build() {
                return create(NamespaceDeclaration.class, CONTAINING_FILE,
                        related1ToN(NAMESPACE_DECLARATION__DECLARATIONS, RecordDeclaration.class, EMPTY_RECORD, setRepresentingNode(),
                                property(nElements(RECORD_DECLARATION__FIELDS, 0)), property(nElements(RECORD_DECLARATION__METHODS, 0))));
            }
        }.build();

        SimpleGraphPattern<NamespaceDeclaration> targetPattern = new GraphPatternBuilder() {
            @Override
            public SimpleGraphPattern<NamespaceDeclaration> build() {
                // remove RecordDeclaration from NamespaceDeclaration
                return create(NamespaceDeclaration.class, CONTAINING_FILE);
            }
        }.build();
        return GraphTransformation.Builder.from(sourcePattern, targetPattern, "removeEmptyRecord", CPG_TRANSFORM).build();
    }

    /**
     * Creates a {@link GraphTransformation} that wraps the "then" statement of an {@link IfStatement} in a {@link Block} if
     * is not a {@link Block}.
     * @return the graph transformation
     */
    private static GraphTransformation wrapThenStatement() {
        return wrapInBlock(IfStatement.class, IF_STATEMENT, IF_STATEMENT__THEN_STATEMENT, "wrapThenStatement");
    }

    /**
     * Creates a {@link GraphTransformation} that wraps the "else" statement of an {@link IfStatement} in a {@link Block} if
     * is not a {@link Block}.
     * @return the graph transformation
     */
    private static GraphTransformation wrapElseStatement() {
        return wrapInBlock(IfStatement.class, IF_STATEMENT, IF_STATEMENT__ELSE_STATEMENT, "wrapElseStatement");
    }

    /**
     * Creates a {@link GraphTransformation} that wraps the "do" statement of a {@link ForStatement} in a {@link Block} if
     * is not a {@link Block}.
     * @return the graph transformation
     */
    private static GraphTransformation wrapForStatement() {
        return wrapInBlock(ForStatement.class, FOR_STATEMENT, FOR_STATEMENT__STATEMENT, "wrapForStatement");
    }

    /**
     * Creates a {@link GraphTransformation} that wraps the "do" statement of a {@link WhileStatement} in a {@link Block} if
     * is not a {@link Block}.
     * @return the graph transformation
     */
    private static GraphTransformation wrapWhileStatement() {
        return wrapInBlock(WhileStatement.class, WHILE_STATEMENT, WHILE_STATEMENT__STATEMENT, "wrapWhileStatement");
    }

    /**
     * Creates a {@link GraphTransformation} that wraps the "do" statement of a {@link WhileStatement} in a {@link Block} if
     * is not a {@link Block}.
     * @return the graph transformation
     */
    private static GraphTransformation wrapDoStatement() {
        return wrapInBlock(DoStatement.class, DO_STATEMENT, DO_STATEMENT__STATEMENT, "wrapDoWhileStatement");
    }

    private static <T extends Node> GraphTransformation wrapInBlock(Class<T> tClass, Role role, final CpgEdge<T, Statement> blockEdge, String name) {
        SimpleGraphPattern<T> sourcePattern = new GraphPatternBuilder() {
            @Override
            public SimpleGraphPattern<T> build() {
                return create(tClass, role, property(notInstanceOf(blockEdge, Block.class)), related(blockEdge, Statement.class, THEN_STATEMENT));
            }
        }.build();

        SimpleGraphPattern<T> targetPattern = new GraphPatternBuilder() {
            @Override
            public SimpleGraphPattern<T> build() {
                return create(tClass, role, related(blockEdge, Block.class, Role.WRAPPING_BLOCK,
                        related(nthElement(BLOCK__STATEMENTS, 0), Statement.class, THEN_STATEMENT)));
            }
        }.build();
        return GraphTransformation.Builder.from(sourcePattern, targetPattern, name, OBLIGATORY).setExecutionOrder(ASCENDING_LOCATION).build();
    }

    /**
     * Creates a {@link GraphTransformation} that removes all {@link MethodDeclaration}s of a {@link RecordDeclaration} that
     * contain only a {@link ReturnStatement} with a constant or field reference as the return value.
     * @return the graph transformation
     */
    private static GraphTransformation removeGetterMethod() {
        SimpleGraphPattern<RecordDeclaration> sourcePattern = new GraphPatternBuilder() {
            @Override
            public SimpleGraphPattern<RecordDeclaration> build() {
                return create(RecordDeclaration.class, CLASS_DECLARATION,
                        related1ToN(RECORD_DECLARATION__METHODS, MethodDeclaration.class, METHOD_DECLARATION, setRepresentingNode(), stopRecursion(),
                                related(VALUE_DECLARATION__TYPE, FunctionType.class, METHOD_TYPE, property(nElements(FUNCTION_TYPE__RETURN_TYPES, 1)),
                                        // ObjectType in contrast to void, which is an IncompleteType
                                        related(nthElement(FUNCTION_TYPE__RETURN_TYPES, 0), ObjectType.class, RETURN_TYPE)),
                                related(METHOD_DECLARATION__BODY, Block.class, METHOD_BLOCK, property(nElements(BLOCK__STATEMENTS, 1)),
                                        related(nthElement(BLOCK__STATEMENTS, 0), ReturnStatement.class, RETURN_STATEMENT,
                                                property(nElements(RETURN_STATEMENT__RETURN_VALUES, 1)),
                                                related(nthElement(RETURN_STATEMENT__RETURN_VALUES, 0), Expression.class, RETURN_VALUE,
                                                        property(or(isConstant(), isFieldReference())))))));
            }
        }.build();

        SimpleGraphPattern<RecordDeclaration> targetPattern = new GraphPatternBuilder() {

            @Override
            public SimpleGraphPattern<RecordDeclaration> build() {
                return create(RecordDeclaration.class, CLASS_DECLARATION
                // remove MethodDeclaration
                );
            }
        }.build();

        return GraphTransformation.Builder.from(sourcePattern, targetPattern, "removeGetterMethod", CPG_TRANSFORM).build();
    }

    /**
     * Creates a {@link GraphTransformation} that replaces calls to Optional.of(x) with their argument x.
     * @return the graph transformation
     */
    private static GraphTransformation removeOptionalOfCall() {
        MultiGraphPattern sourcePattern = new GraphPatternBuilder() {
            @Override
            public MultiGraphPattern build() {
                return multiRoot(wildcardParent(MemberCallExpression.class, MEMBER_CALL,
                        related1ToN(CALL_EXPRESSION__INVOKES, MethodDeclaration.class, METHOD_DECLARATION,
                                property(attributeToStringEquals(NODE__NAME, "Optional.of"))),
                        property(nElements(CALL_EXPRESSION__ARGUMENTS, 1)),
                        related(nthElement(CALL_EXPRESSION__ARGUMENTS, 0), Expression.class, ARGUMENT)));
            }
        }.build();

        MultiGraphPattern targetPattern = new GraphPatternBuilder() {

            @Override
            public MultiGraphPattern build() {
                return multiRoot(
                        // replace Optional.of(x) by x
                        wildcardParent(Expression.class, ARGUMENT));

            }
        }.build();

        return GraphTransformation.Builder.from(sourcePattern, targetPattern, "removeOptionalOfCall", CPG_TRANSFORM).build();
    }

    /**
     * Creates a {@link GraphTransformation} that replaces calls to Optional.get() with the variable on which the method was
     * called.
     * @return the graph transformation
     */
    private static GraphTransformation removeOptionalGetCall() {
        MultiGraphPattern sourcePattern = new GraphPatternBuilder() {
            @Override
            public MultiGraphPattern build() {
                return multiRoot(wildcardParent(MemberCallExpression.class, MEMBER_CALL, setRepresentingNode(), stopRecursion(),
                        related(CALL_EXPRESSION__CALLEE, MemberExpression.class, GETTER_METHOD_REFERENCE,
                                related(MEMBER_EXPRESSION__BASE, Expression.class, OPTIONAL_OBJECT)),
                        related1ToN(CALL_EXPRESSION__INVOKES, MethodDeclaration.class, METHOD_DECLARATION,
                                property(attributeToStringEquals(METHOD_DECLARATION__LOCAL_NAME, "get")),
                                related(METHOD_DECLARATION__RECORD_DECLARATION, RecordDeclaration.class, OPTIONAL_CLASS,
                                        property(attributeToStringStartsWith(RECORD_DECLARATION__NAME, "java.util.Optional")))),
                        property(nElements(CALL_EXPRESSION__ARGUMENTS, 0))));
            }
        }.build();

        MultiGraphPattern targetPattern = new GraphPatternBuilder() {

            @Override
            public MultiGraphPattern build() {
                return multiRoot(
                        // replace Optional.get() by the proper value
                        wildcardParent(Expression.class, OPTIONAL_OBJECT));
            }
        }.build();

        return GraphTransformation.Builder.from(sourcePattern, targetPattern, "removeOptionalGetCall", CPG_TRANSFORM).build();
    }

}
