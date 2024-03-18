package de.jplag.java_cpg.transformation;

import static de.jplag.java_cpg.transformation.GraphTransformation.ExecutionOrder.ASCENDING_LOCATION;
import static de.jplag.java_cpg.transformation.GraphTransformation.ExecutionPhase.*;
import static de.jplag.java_cpg.transformation.matching.edges.Edges.*;
import static de.jplag.java_cpg.transformation.matching.pattern.PatternUtil.*;

import de.fraunhofer.aisec.cpg.graph.Component;
import de.fraunhofer.aisec.cpg.graph.Node;
import de.fraunhofer.aisec.cpg.graph.declarations.*;
import de.fraunhofer.aisec.cpg.graph.statements.*;
import de.fraunhofer.aisec.cpg.graph.statements.expressions.*;
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
    public static final GraphTransformation ifWithNegatedConditionResolution = ifWithNegatedConditionResolution();
    public static final GraphTransformation forStatementToWhileStatement = forStatementToWhileStatement();
    public static final GraphTransformation removeGetterMethod = removeGetterMethod();
    public static final GraphTransformation removeUnusedVariableDeclaration = removeUnusedVariableDeclaration();
    public static final GraphTransformation removeUnusedVariableDeclarationStatement = removeUnusedVariableDeclarationStatement();
    public static final GraphTransformation removeEmptyDeclarationStatement = removeEmptyDeclarationStatement();
    public static final GraphTransformation removeLibraryRecord = removeLibraryRecord();
    public static final GraphTransformation removeLibraryField = removeLibraryField();
    public static final GraphTransformation moveConstantToOnlyUsingClass = moveConstantToOnlyUsingClass();
    public static final GraphTransformation inlineSingleUseVariable = inlineSingleUseVariable();

    public static final GraphTransformation inlineSingleUseConstant = inlineSingleUseConstant();
    public static final GraphTransformation removeEmptyConstructor = removeEmptyConstructor();

    public static final GraphTransformation removeEmptyRecord = removeEmptyRecord();
    public static final GraphTransformation removeImplicitStandardConstructor = removeImplicitStandardConstructor();
    public static final GraphTransformation removeOptionalOfCall = removeOptionalOfCall();
    public static final GraphTransformation removeOptionalGetCall = removeOptionalGetCall();
    public static final GraphTransformation removeUnsupportedConstructor = removeUnsupportedConstructor();
    public static final GraphTransformation removeUnsupportedMethod = removeUnsupportedMethod();
    public static final GraphTransformation wrapElseStatement = wrapElseStatement();
    public static final GraphTransformation wrapForStatement = wrapForStatement();
    public static final GraphTransformation wrapThenStatement = wrapThenStatement();
    public static final GraphTransformation wrapWhileStatement = wrapWhileStatement();
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
                return create(IfStatement.class, "ifStatement",
                        related(IF_STATEMENT__CONDITION, UnaryOperator.class, "condition",
                                property(attributeEquals(UNARY_OPERATOR__OPERATOR_CODE, "!")),
                                related(UNARY_OPERATOR__INPUT, Expression.class, "innerCondition")),
                        related(IF_STATEMENT__THEN_STATEMENT, Statement.class, "thenStatement"),
                        related(IF_STATEMENT__ELSE_STATEMENT, Statement.class, "elseStatement"));
            }
        }.build();

        SimpleGraphPattern<IfStatement> targetPattern = new GraphPatternBuilder() {

            @Override
            public SimpleGraphPattern<IfStatement> build() {
                return create(IfStatement.class, "ifStatement", related(IF_STATEMENT__CONDITION, Expression.class, "innerCondition"),
                        related(IF_STATEMENT__THEN_STATEMENT, Statement.class, "elseStatement"),
                        related(IF_STATEMENT__ELSE_STATEMENT, Statement.class, "thenStatement"));
            }
        }.build();

        return GraphTransformation.Builder.from(sourcePattern, targetPattern, "ifWithNegatedConditionResolution", AST_TRANSFORM)
                .setExecutionOrder(ASCENDING_LOCATION).build();
    }

    /**
     * Creates a {@link GraphTransformation} that removes a {@link DeclarationStatement} with exactly one unused
     * {@link VariableDeclaration}.
     * @return the graph transformation object
     */
    private static GraphTransformation removeUnusedVariableDeclarationStatement() {
        SimpleGraphPattern<Node> sourcePattern = new GraphPatternBuilder() {
            @Override
            public SimpleGraphPattern<Node> build() {
                return wildcardParent(DeclarationStatement.class, "declStmt", setRepresentingNode(),
                        property(notEmpty(DECLARATION_STATEMENT__DECLARATIONS)), forAllRelated(DECLARATION_STATEMENT__DECLARATIONS,
                                VariableDeclaration.class, "varDecl", property(nElements(VALUE_DECLARATION__USAGES, 0))));
            }
        }.build();

        SimpleGraphPattern<Node> targetPattern = new GraphPatternBuilder() {
            @Override
            public SimpleGraphPattern<Node> build() {
                return emptyWildcardParent();
            }
        }.build();
        return GraphTransformation.Builder.from(sourcePattern, targetPattern, "removeUnusedVariableDeclarationStatements", CPG_TRANSFORM).build();
    }

    /**
     * Creates a {@link GraphTransformation} that replaces a {@link VariableDeclaration} of an unused variable by an
     * {@link EmptyStatement}.
     * <p>
     * May target the following edges:<br>
     * <ul>
     * <li>Statement --LOCALS*--> VariableDeclaration</li>
     * <li>AssignExpression --DECLARATIONS*--> VariableDeclaration</li>
     * <li>CatchClause --PARAMETER--> VariableDeclaration</li>
     * <li>MethodDeclaration --RECEIVER--> VariableDeclaration</li>
     * </ul>
     * @return the graph transformation object
     */
    private static GraphTransformation removeUnusedVariableDeclaration() {
        SimpleGraphPattern<Node> sourcePattern = new GraphPatternBuilder() {
            @Override
            public SimpleGraphPattern<Node> build() {
                return wildcardParent(VariableDeclaration.class, "variableDecl", setRepresentingNode(),
                        property(nElements(VALUE_DECLARATION__USAGES, 0)));
            }
        }.build();
        SimpleGraphPattern<Node> targetPattern = new GraphPatternBuilder() {
            @Override
            public SimpleGraphPattern<Node> build() {
                return emptyWildcardParent();
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
                return wildcardParent(DeclarationStatement.class, "declStatement", setRepresentingNode(),
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
                return wildcardParent(ForStatement.class, "forStatement", setRepresentingNode(),
                        related(FOR_STATEMENT__INITIALIZER_STATEMENT, Statement.class, "initStatement"),
                        related(FOR_STATEMENT__CONDITION, Expression.class, "condition"),
                        related(FOR_STATEMENT__ITERATION_STATEMENT, Statement.class, "iterationStatement"),
                        related(FOR_STATEMENT__STATEMENT, Statement.class, "body"));
            }
        }.build();

        SimpleGraphPattern<Node> targetPattern = new GraphPatternBuilder() {
            @Override
            public SimpleGraphPattern<Node> build() {
                return wildcardParent(Block.class, "surroundingBlock",
                        related1ToNSequence(BLOCK__STATEMENTS, Statement.class, node(Statement.class, "initStatement"),
                                node(WhileStatement.class, "whileStatement", related(WHILE_STATEMENT__CONDITION, Expression.class, "condition"),
                                        related(WHILE_STATEMENT__STATEMENT, Block.class, "whileStatementBody", related1ToNSequence(BLOCK__STATEMENTS,
                                                Statement.class, node(Statement.class, "body"), node(Statement.class, "iterationStatement"))))));
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
                return create(RecordDeclaration.class, "recordDecl", related1ToN(RECORD_DECLARATION__FIELDS, FieldDeclaration.class, "fieldDecl",
                        setRepresentingNode(), property(attributeEquals(NODE__LOCATION, null))));
            }
        }.build();
        SimpleGraphPattern<RecordDeclaration> targetPattern = new GraphPatternBuilder() {
            @Override
            public SimpleGraphPattern<RecordDeclaration> build() {
                return create(RecordDeclaration.class, "recordDecl");
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
                return create(TranslationUnitDeclaration.class, "declarationContainer", related1ToN(TRANSLATION_UNIT__DECLARATIONS,
                        RecordDeclaration.class, "declaration", setRepresentingNode(), property(attributeEquals(NODE__LOCATION, null))));
            }
        }.build();
        SimpleGraphPattern<TranslationUnitDeclaration> targetPattern = new GraphPatternBuilder() {
            @Override
            public SimpleGraphPattern<TranslationUnitDeclaration> build() {
                return create(TranslationUnitDeclaration.class, "declarationContainer");
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
                        create(RecordDeclaration.class, "definingRecord", related1ToN(RECORD_DECLARATION__FIELDS, FieldDeclaration.class,
                                "fieldDeclaration", setRepresentingNode(), stopRecursion(), // no transformations beyond this point
                                property(attributeContains(FIELD_DECLARATION__MODIFIERS, "final")),
                                property(attributeContains(FIELD_DECLARATION__MODIFIERS, "static")), property(notEmpty(VALUE_DECLARATION__USAGES)),
                                related(nthElement(VALUE_DECLARATION__USAGES, 0), MemberExpression.class, "firstUsage",
                                        related(MEMBER_EXPRESSION__RECORD_DECLARATION, RecordDeclaration.class, "usingRecord",
                                                notEqualTo("definingRecord"))),
                                forAllRelated(VALUE_DECLARATION__USAGES, MemberExpression.class, "fieldUsages",
                                        relatedExisting(MEMBER_EXPRESSION__RECORD_DECLARATION, RecordDeclaration.class, "usingRecord")))),
                        create(RecordDeclaration.class, "usingRecord"
                // field declaration should go here
                ), create(MemberExpression.class, "firstUsage", related(MEMBER_EXPRESSION__BASE, Reference.class, "defRecordReference",
                        relatedExisting(REFERENCE__REFERS_TO, RecordDeclaration.class, "definingRecord")))

                );
            }
        }.build();
        MultiGraphPattern targetPattern = new GraphPatternBuilder() {
            @Override
            public MultiGraphPattern build() {
                return multiRoot(create(RecordDeclaration.class, "definingRecord"
                // field declaration removed from here
                ), create(RecordDeclaration.class, "usingRecord",
                        related1ToN(RECORD_DECLARATION__FIELDS, FieldDeclaration.class, "fieldDeclaration")),
                        create(MemberExpression.class, "firstUsage", related(MEMBER_EXPRESSION__BASE, Reference.class, "defRecordReference",
                                // adjust reference
                                relatedExisting(REFERENCE__REFERS_TO, RecordDeclaration.class, "usingRecord"))));
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
                        wildcardParent(VariableDeclaration.class, "varDecl", setRepresentingNode(), property(notNull(NODE__LOCATION)),
                                property(nElements(VALUE_DECLARATION__USAGES, 1)),
                                related(nthElement(VALUE_DECLARATION__USAGES, 0), Reference.class, "variableUsage"),
                                related(VARIABLE_DECLARATION__INITIALIZER, Expression.class, "varValue",
                                        assignedValueStableBetween("varDecl", "variableUsage"))),
                        wildcardParent(Reference.class, "variableUsage", relatedExisting(REFERENCE__REFERS_TO, VariableDeclaration.class, "varDecl")),
                        wildcardParent(Block.class, "scopeBlock", relatedExisting1ToN(STATEMENT__LOCALS, VariableDeclaration.class, "varDecl")));
            }
        }.build();

        MultiGraphPattern targetPattern = new GraphPatternBuilder() {
            @Override
            public MultiGraphPattern build() {
                return multiRoot(
                        // remove variable declaration from AST parent
                        emptyWildcardParent(),
                        // replace variable reference by value
                        wildcardParent(Expression.class, "varValue"),
                        // delete "local" edge
                        wildcardParent(Block.class, "scopeBlock"));
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
                        create(RecordDeclaration.class, "containingClass",
                                related1ToN(RECORD_DECLARATION__FIELDS, FieldDeclaration.class, "fieldDecl", setRepresentingNode(),
                                        property(notNull(NODE__LOCATION)), property(attributeContains(FIELD_DECLARATION__MODIFIERS, "final")),
                                        property(attributeContains(FIELD_DECLARATION__MODIFIERS, "static")),
                                        property(nElements(VALUE_DECLARATION__USAGES, 1)),
                                        related(nthElement(VALUE_DECLARATION__USAGES, 0), Reference.class, "fieldUsage"),
                                        related(VARIABLE_DECLARATION__INITIALIZER, Expression.class, "fieldValue"))),
                        wildcardParent(Reference.class, "fieldUsage", relatedExisting(REFERENCE__REFERS_TO, FieldDeclaration.class, "fieldDecl")));
            }
        }.build();

        MultiGraphPattern targetPattern = new GraphPatternBuilder() {
            @Override
            public MultiGraphPattern build() {
                return multiRoot(
                        // remove field declaration from class
                        create(RecordDeclaration.class, "containingClass"),
                        // replace variable reference by value
                        wildcardParent(Expression.class, "fieldValue"));
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

                return create(RecordDeclaration.class, "definingRecord", related1ToN(RECORD_DECLARATION__CONSTRUCTORS, ConstructorDeclaration.class,
                        "constructor", property(attributeEquals(NODE__LOCATION, null))));
            }
        }.build();

        SimpleGraphPattern<RecordDeclaration> targetPattern = new GraphPatternBuilder() {
            @Override
            public SimpleGraphPattern<RecordDeclaration> build() {
                // remove constructor declaration from record
                return create(RecordDeclaration.class, "definingRecord");
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
                return create(RecordDeclaration.class, "containingRecord",
                        related1ToN(RECORD_DECLARATION__CONSTRUCTORS, ConstructorDeclaration.class, "constructorDecl", setRepresentingNode(),
                                stopRecursion(),
                                related(METHOD_DECLARATION__BODY, Block.class, "methodBlock", property(nElements(BLOCK__STATEMENTS, 1)),
                                        related(nthElement(BLOCK__STATEMENTS, 0), ReturnStatement.class, "returnStatement",
                                                property(attributeEquals(NODE__LOCATION, null))))));

            }
        }.build();

        SimpleGraphPattern<RecordDeclaration> targetPattern = new GraphPatternBuilder() {
            @Override
            public SimpleGraphPattern<RecordDeclaration> build() {
                return create(RecordDeclaration.class, "containingRecord");
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
                return create(RecordDeclaration.class, "containingRecord",
                        related1ToN(RECORD_DECLARATION__CONSTRUCTORS, ConstructorDeclaration.class, "constructorDecl", setRepresentingNode(),
                                stopRecursion(),
                                related(METHOD_DECLARATION__BODY, Block.class, "methodBlock", property(nElements(BLOCK__STATEMENTS, 2)),
                                        related(nthElement(BLOCK__STATEMENTS, 0), UnaryOperator.class, "throwException",
                                                property(attributeEquals(UNARY_OPERATOR__OPERATOR_CODE, "throw"))),
                                        related(nthElement(BLOCK__STATEMENTS, 1), ReturnStatement.class, "returnStatement",
                                                property(attributeEquals(NODE__LOCATION, null))))));

            }
        }.build();

        SimpleGraphPattern<RecordDeclaration> targetPattern = new GraphPatternBuilder() {
            @Override
            public SimpleGraphPattern<RecordDeclaration> build() {
                return create(RecordDeclaration.class, "containingRecord");
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
                return create(RecordDeclaration.class, "containingRecord",
                        related1ToN(RECORD_DECLARATION__METHODS, MethodDeclaration.class, "methodDecl", setRepresentingNode(), stopRecursion(),
                                related(METHOD_DECLARATION__BODY, Block.class, "methodBlock", property(nElements(BLOCK__STATEMENTS, 2)),
                                        related(nthElement(BLOCK__STATEMENTS, 0), UnaryOperator.class, "throwException",
                                                property(attributeEquals(UNARY_OPERATOR__OPERATOR_CODE, "throw"))),
                                        related(nthElement(BLOCK__STATEMENTS, 1), ReturnStatement.class, "returnStatement",
                                                property(attributeEquals(NODE__LOCATION, null))))));

            }
        }.build();

        SimpleGraphPattern<RecordDeclaration> targetPattern = new GraphPatternBuilder() {
            @Override
            public SimpleGraphPattern<RecordDeclaration> build() {
                return create(RecordDeclaration.class, "containingRecord");
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
                return create(NamespaceDeclaration.class, "containingFile",
                        related1ToN(NAMESPACE_DECLARATION__DECLARATIONS, RecordDeclaration.class, "emptyRecord", setRepresentingNode(),
                                property(nElements(RECORD_DECLARATION__FIELDS, 0)), property(nElements(RECORD_DECLARATION__METHODS, 0))));
            }
        }.build();

        SimpleGraphPattern<NamespaceDeclaration> targetPattern = new GraphPatternBuilder() {
            @Override
            public SimpleGraphPattern<NamespaceDeclaration> build() {
                // remove RecordDeclaration from NamespaceDeclaration
                return create(NamespaceDeclaration.class, "containingFile");
            }
        }.build();
        return GraphTransformation.Builder.from(sourcePattern, targetPattern, "removeEmptyRecord", CPG_TRANSFORM).build();
    }

    /**
     * <b>BEWARE:</b> This transformation breaks the comparison algorithm, leading to a higher similarity value. <br>
     * <br>
     * Creates a {@link GraphTransformation} that removes TranslationUnits with no declarations.
     * @return the graph transformation
     */
    private static GraphTransformation removeEmptyFile() {
        SimpleGraphPattern<Component> sourcePattern = new GraphPatternBuilder() {
            @Override
            public SimpleGraphPattern<Component> build() {
                return create(Component.class, "project", related1ToN(COMPONENT__TRANSLATION_UNITS, TranslationUnitDeclaration.class, "emptyFile",
                        property(nElements(TRANSLATION_UNIT__DECLARATIONS, 0))));
            }
        }.build();

        SimpleGraphPattern<Component> targetPattern = new GraphPatternBuilder() {
            @Override
            public SimpleGraphPattern<Component> build() {
                // remove RecordDeclaration from TranslationUnitDeclaration
                return create(Component.class, "project");
            }
        }.build();
        return GraphTransformation.Builder.from(sourcePattern, targetPattern, "removeEmptyFile", CPG_TRANSFORM).build();
    }

    /**
     * Creates a {@link GraphTransformation} that wraps the "then" statement of an {@link IfStatement} in a {@link Block} if
     * is not a {@link Block}.
     * @return the graph transformation
     */
    private static GraphTransformation wrapThenStatement() {
        return wrapInBlock(IfStatement.class, IF_STATEMENT__THEN_STATEMENT, "wrapThenStatement");
    }

    /**
     * Creates a {@link GraphTransformation} that wraps the "else" statement of an {@link IfStatement} in a {@link Block} if
     * is not a {@link Block}.
     * @return the graph transformation
     */
    private static GraphTransformation wrapElseStatement() {
        return wrapInBlock(IfStatement.class, IF_STATEMENT__ELSE_STATEMENT, "wrapElseStatement");
    }

    /**
     * Creates a {@link GraphTransformation} that wraps the "do" statement of a {@link ForStatement} in a {@link Block} if
     * is not a {@link Block}.
     * @return the graph transformation
     */
    private static GraphTransformation wrapForStatement() {
        return wrapInBlock(ForStatement.class, FOR_STATEMENT__STATEMENT, "wrapForStatement");
    }

    /**
     * Creates a {@link GraphTransformation} that wraps the "do" statement of a {@link WhileStatement} in a {@link Block} if
     * is not a {@link Block}.
     * @return the graph transformation
     */
    private static GraphTransformation wrapWhileStatement() {
        return wrapInBlock(null, WHILE_STATEMENT__STATEMENT, "wrapWhileStatement");
    }

    /**
     * Creates a {@link GraphTransformation} that wraps the "do" statement of a {@link WhileStatement} in a {@link Block} if
     * is not a {@link Block}.
     * @return the graph transformation
     */
    private static GraphTransformation wrapDoStatement() {
        return wrapInBlock(DoStatement.class, DO_STATEMENT__STATEMENT, "wrapDoWhileStatement");
    }

    private static <T extends Node> GraphTransformation wrapInBlock(Class<T> tClass, final CpgEdge<T, Statement> blockEdge, String name) {
        SimpleGraphPattern<T> sourcePattern = new GraphPatternBuilder() {
            @Override
            public SimpleGraphPattern<T> build() {
                return create(tClass, "whileStatement", property(notInstanceOf(blockEdge, Block.class)),
                        related(blockEdge, Statement.class, "doStmt"));
            }
        }.build();

        SimpleGraphPattern<T> targetPattern = new GraphPatternBuilder() {
            @Override
            public SimpleGraphPattern<T> build() {
                return create(tClass, "whileStatement",
                        related(blockEdge, Block.class, "whileBlock", related(nthElement(BLOCK__STATEMENTS, 0), Statement.class, "doStmt")));
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
                return create(RecordDeclaration.class, "classDeclaration", related1ToN(RECORD_DECLARATION__METHODS, MethodDeclaration.class,
                        "methodDeclaration", setRepresentingNode(), stopRecursion(),
                        related(VALUE_DECLARATION__TYPE, FunctionType.class, "methodType", property(nElements(FUNCTION_TYPE__RETURN_TYPES, 1)),
                                // ObjectType in contrast to void, which is an IncompleteType
                                related(nthElement(FUNCTION_TYPE__RETURN_TYPES, 0), ObjectType.class, "returnType")),
                        related(METHOD_DECLARATION__BODY, Block.class, "methodBlock", property(nElements(BLOCK__STATEMENTS, 1)),
                                related(nthElement(BLOCK__STATEMENTS, 0), ReturnStatement.class, "returnStatement",
                                        property(nElements(RETURN_STATEMENT__RETURN_VALUES, 1)),
                                        related(nthElement(RETURN_STATEMENT__RETURN_VALUES, 0), Expression.class, "returnValue",
                                                property(or(isConstant(), isFieldReference())))))));
            }
        }.build();

        SimpleGraphPattern<RecordDeclaration> targetPattern = new GraphPatternBuilder() {

            @Override
            public SimpleGraphPattern<RecordDeclaration> build() {
                return create(RecordDeclaration.class, "classDeclaration"
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
                return multiRoot(wildcardParent(MemberCallExpression.class, "memberCall",
                        related1ToN(CALL_EXPRESSION__INVOKES, MethodDeclaration.class, "methodDeclaration",
                                property(attributeToStringEquals(NODE__NAME, "Optional.of"))),
                        property(nElements(CALL_EXPRESSION__ARGUMENTS, 1)),
                        related(nthElement(CALL_EXPRESSION__ARGUMENTS, 0), Expression.class, "argument")));
            }
        }.build();

        MultiGraphPattern targetPattern = new GraphPatternBuilder() {

            @Override
            public MultiGraphPattern build() {
                return multiRoot(
                        // replace Optional.of(x) by x
                        wildcardParent(Expression.class, "argument"));

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
                return multiRoot(wildcardParent(MemberCallExpression.class, "memberCall", setRepresentingNode(), stopRecursion(),
                        related(CALL_EXPRESSION__CALLEE, MemberExpression.class, "getMethodReference",
                                related(MEMBER_EXPRESSION__BASE, Expression.class, "optionalObject")),

                        related1ToN(CALL_EXPRESSION__INVOKES, MethodDeclaration.class, "methodDeclaration",
                                property(attributeToStringEquals(NODE__LOCAL_NAME, "get")),
                                related(METHOD_DECLARATION__RECORD_DECLARATION, RecordDeclaration.class, "optionalClass",
                                        property(attributeToStringStartsWith(NODE__NAME, "java.util.Optional")))),
                        property(nElements(CALL_EXPRESSION__ARGUMENTS, 0))));
            }
        }.build();

        MultiGraphPattern targetPattern = new GraphPatternBuilder() {

            @Override
            public MultiGraphPattern build() {
                return multiRoot(
                        // replace Optional.get() by the proper value
                        wildcardParent(Expression.class, "optionalObject"));
            }
        }.build();

        return GraphTransformation.Builder.from(sourcePattern, targetPattern, "removeOptionalGetCall", CPG_TRANSFORM).build();
    }

}
