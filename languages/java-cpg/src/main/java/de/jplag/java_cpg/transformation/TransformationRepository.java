package de.jplag.java_cpg.transformation;

import de.fraunhofer.aisec.cpg.graph.Component;
import de.fraunhofer.aisec.cpg.graph.Node;
import de.fraunhofer.aisec.cpg.graph.declarations.*;
import de.fraunhofer.aisec.cpg.graph.statements.*;
import de.fraunhofer.aisec.cpg.graph.statements.expressions.*;
import de.fraunhofer.aisec.cpg.graph.types.FunctionType;
import de.fraunhofer.aisec.cpg.graph.types.IncompleteType;
import de.fraunhofer.aisec.cpg.graph.types.NumericType;
import de.jplag.java_cpg.transformation.matching.pattern.*;

import static de.jplag.java_cpg.transformation.GraphTransformation.ExecutionPhase.PHASE_ONE;
import static de.jplag.java_cpg.transformation.GraphTransformation.ExecutionPhase.PHASE_TWO;
import static de.jplag.java_cpg.transformation.matching.edges.Edges.*;
import static de.jplag.java_cpg.transformation.matching.pattern.PatternUtil.*;

/**
 * Contains factory methods to create different {@link GraphTransformation}s.
 */
public class TransformationRepository {

    public static final GraphTransformation<IfStatement> ifWithNegatedConditionResolution = ifWithNegatedConditionResolution();
    public static final GraphTransformation<Node> forStatementToWhileStatement = forStatementToWhileStatement();
    public static final GraphTransformation<Node> removeUnusedVariableDeclarations = removeUnusedVariableDeclarations();
    public static final GraphTransformation<Node> removeUnusedVariableDeclarationStatements = removeUnusedVariableDeclarationStatements();
    public static final GraphTransformation<Node> removeEmptyDeclarationStatement = removeEmptyDeclarationStatement();
    public static final GraphTransformation<TranslationUnitDeclaration> removeLibraryRecords = removeLibraryRecords();
    public static final GraphTransformation<Node> moveConstantToOnlyUsingClass = moveConstantToOnlyUsingClass();
    public static final GraphTransformation<Node> inlineSingleUseVariable = inlineSingleUseVariable();
    public static final GraphTransformation<RecordDeclaration> removeImplicitStandardConstructor = removeImplicitStandardConstructor();
    public static final GraphTransformation<Node> inlineMagicNumber = inlineMagicNumber();
    public static final GraphTransformation<Node> inlineSingleUseConstant = inlineSingleUseConstant();
    public static final GraphTransformation<TranslationUnitDeclaration> removeEmptyRecord = removeEmptyRecord();
    public static final GraphTransformation<Component> removeEmptyFile = removeEmptyFile();
    public static final GraphTransformation<IfStatement> wrapThenStatement = wrapThenStatement();
    public static final GraphTransformation<IfStatement> wrapElseStatement = wrapElseStatement();


    private TransformationRepository() {
    }


    /**
     * Creates a {@link GraphTransformation} that un-negates the condition of an if statement and swaps the then and else blocks.
     *
     * @return the graph transformation object
     */
    private static GraphTransformation<IfStatement> ifWithNegatedConditionResolution() {
        SimpleGraphPattern<IfStatement> sourcePattern = new GraphPatternBuilder() {
            @Override
            public SimpleGraphPattern<IfStatement> build() {
                return create(IfStatement.class, "ifStatement",
                    related(IF_STATEMENT__CONDITION, UnaryOperator.class, "condition",
                        property(attributeEquals(UNARY_OPERATOR__OPERATOR_CODE, "!")),
                        related(UNARY_OPERATOR__INPUT, Expression.class, "innerCondition")),
                    related(IF_STATEMENT__THEN_STATEMENT, Statement.class, "thenStatement"),
                    related(IF_STATEMENT__ELSE_STATEMENT, Statement.class, "elseStatement")
                );
            }
        }.build();

        SimpleGraphPattern<IfStatement> targetPattern = new GraphPatternBuilder() {

            @Override
            public SimpleGraphPattern<IfStatement> build() {
                return create(IfStatement.class, "ifStatement",
                    related(IF_STATEMENT__CONDITION, Expression.class, "innerCondition"),
                    related(IF_STATEMENT__THEN_STATEMENT, Statement.class, "elseStatement"),
                    related(IF_STATEMENT__ELSE_STATEMENT, Statement.class, "thenStatement")
                );
            }
        }.build();

        return GraphTransformation.Builder.from(sourcePattern, targetPattern, "ifWithNegatedConditionResolution", PHASE_ONE).build();
    }

    /**
     * Creates a {@link GraphTransformation} that removes a {@link DeclarationStatement} with exactly one unused {@link VariableDeclaration}.
     *
     * @return the graph transformation object
     */
    private static GraphTransformation<Node> removeUnusedVariableDeclarationStatements() {
        SimpleGraphPattern<Node> sourcePattern = new GraphPatternBuilder() {
            @Override
            public SimpleGraphPattern<Node> build() {
                return wildcardParent(DeclarationStatement.class, "declStmt",
                    setRepresentingNode(),
                    property(notEmpty(DECLARATION_STATEMENT__DECLARATIONS)),
                    forAllRelated(DECLARATION_STATEMENT__DECLARATIONS, VariableDeclaration.class, "varDecl",
                        property(nElements(VALUE_DECLARATION__USAGES, 0))
                    )
                );
            }
        }.build();

        SimpleGraphPattern<Node> targetPattern = new GraphPatternBuilder() {
            @Override
            public SimpleGraphPattern<Node> build() {
                return emptyWildcardParent();
            }
        }.build();
        return GraphTransformation.Builder.from(sourcePattern, targetPattern, "removeUnusedVariableDeclarationStatements", PHASE_TWO).build();
    }


    /**
     * Creates a {@link GraphTransformation} that replaces a {@link VariableDeclaration} of an unused variable by an {@link EmptyStatement}.
     * <p>
     * May target the following edges:<br><ul>
     * <li>Statement --LOCALS*--> VariableDeclaration</li>
     * <li>AssignExpression --DECLARATIONS*--> VariableDeclaration</li>
     * <li>CatchClause --PARAMETER--> VariableDeclaration</li>
     * <li>MethodDeclaration --RECEIVER--> VariableDeclaration</li>
     * </ul>
     *
     * @return the graph transformation object
     */
    private static GraphTransformation<Node> removeUnusedVariableDeclarations() {
        SimpleGraphPattern<Node> sourcePattern = new GraphPatternBuilder() {
            @Override
            public SimpleGraphPattern<Node> build() {
                return wildcardParent(VariableDeclaration.class, "variableDecl",
                    setRepresentingNode(),
                    property(nElements(VALUE_DECLARATION__USAGES, 0))
                );
            }
        }.build();
        SimpleGraphPattern<Node> targetPattern = new GraphPatternBuilder() {
            @Override
            public SimpleGraphPattern<Node> build() {
                return emptyWildcardParent();
            }
        }.build();

        return GraphTransformation.Builder.from(sourcePattern, targetPattern, "removeUnusedVariableDeclarations", PHASE_TWO).build();
    }

    /**
     * Creates a {@link GraphTransformation} that removes a {@link DeclarationStatement} with no {@link Declaration}s.
     *
     * @return the graph transformation object
     */
    private static GraphTransformation<Node> removeEmptyDeclarationStatement() {
        SimpleGraphPattern<Node> sourcePattern = new GraphPatternBuilder() {
            @Override
            public SimpleGraphPattern<Node> build() {
                return wildcardParent(DeclarationStatement.class, "declStatement",
                    setRepresentingNode(),
                    property(PatternUtil.nElements(DECLARATION_STATEMENT__DECLARATIONS, 0))
                );
            }
        }.build();
        SimpleGraphPattern<Node> targetPattern = new GraphPatternBuilder() {
            @Override
            public SimpleGraphPattern<Node> build() {
                return emptyWildcardParent();
            }
        }.build();

        return GraphTransformation.Builder.from(sourcePattern, targetPattern, "removeEmptyDeclarationStatement", PHASE_TWO).build();
    }

    /**
     * Creates a {@link GraphTransformation} that replaces {@link ForStatement}s by equivalent {@link WhileStatement}s.
     *
     * @return the graph transformation
     */
    private static GraphTransformation<Node> forStatementToWhileStatement() {
        SimpleGraphPattern<Node> sourcePattern = new GraphPatternBuilder() {
            @Override
            public SimpleGraphPattern<Node> build() {
                return wildcardParent(ForStatement.class, "forStatement",
                    setRepresentingNode(),
                    related(FOR_STATEMENT__INITIALIZER_STATEMENT, Statement.class, "initStatement"),
                    related(FOR_STATEMENT__CONDITION, Expression.class, "condition"),
                    related(FOR_STATEMENT__ITERATION_STATEMENT, Statement.class, "iterationStatement"),
                    related(FOR_STATEMENT__STATEMENT, Statement.class, "body")
                );
            }
        }.build();

        SimpleGraphPattern<Node> targetPattern = new GraphPatternBuilder() {
            @Override
            public SimpleGraphPattern<Node> build() {
                return wildcardParent(Block.class, "surroundingBlock",
                    related1ToNSequence(BLOCK__STATEMENTS, Statement.class,
                        node(Statement.class, Statement.class, "initStatement"),
                        node(WhileStatement.class, Statement.class, "whileStatement",
                            related(WHILE_STATEMENT__CONDITION, Expression.class, "condition"),
                            related(WHILE_STATEMENT__STATEMENT, Block.class, "whileStatementBody",
                                related1ToNSequence(BLOCK__STATEMENTS, Statement.class,
                                    node(Statement.class, Statement.class, "body"),
                                    node(Statement.class, Statement.class, "iterationStatement")
                                )
                            )
                        )
                    )
                );
            }

        }.build();
        return GraphTransformation.Builder.from(sourcePattern, targetPattern, "forStmtToWhileStmt", PHASE_ONE).build();

    }

    /**
     * Creates a {@link GraphTransformation} that inlines {@link Block}s that are not enforced by control structures.
     *
     * @return the graph transformation
     */
    private static GraphTransformation<Block> inlineInnerBlocks() {
        //TODO
        new GraphPatternBuilder() {
            @Override
            public SimpleGraphPattern<Block> build() {
                return create(Block.class, "outerBlock",
                    related1ToN(BLOCK__DECLARATIONS, Declaration.class, "outerDeclaration"),
                    related1ToN(BLOCK__STATEMENTS, Block.class, "innerBlock",
                        related1ToN(BLOCK__DECLARATIONS, Declaration.class, "innerDeclarations",
                            equalAttributes(DECLARATION__NAME, "outerDeclaration"))
                    )
                );
            }
        }.build();
        return null;
    }

    private static GraphTransformation<TranslationUnitDeclaration> removeLibraryRecords() {
        SimpleGraphPattern<TranslationUnitDeclaration> sourcePattern = new GraphPatternBuilder() {
            @Override
            public SimpleGraphPattern<TranslationUnitDeclaration> build() {
                return create(TranslationUnitDeclaration.class, "file",
                    related1ToN(TRANSLATION_UNIT__DECLARATIONS, RecordDeclaration.class, "declaration",
                        setRepresentingNode(),
                        property(attributeEquals(RECORD_DECLARATION__LOCATION, null))));
            }
        }.build();
        SimpleGraphPattern<TranslationUnitDeclaration> targetPattern = new GraphPatternBuilder() {
            @Override
            public SimpleGraphPattern<TranslationUnitDeclaration> build() {
                return create(TranslationUnitDeclaration.class, "file"
                    // remove declaration
                );
            }
        }.build();

        return GraphTransformation.Builder.from(sourcePattern, targetPattern, "removeLibraryRecords", PHASE_TWO).build();
    }

    private static GraphTransformation<Node> moveConstantToOnlyUsingClass() {
        MultiGraphPattern sourcePattern = new GraphPatternBuilder() {
            @Override
            public MultiGraphPattern build() {
                return multiRoot(
                    create(RecordDeclaration.class, "definingRecord",
                        related1ToN(RECORD_DECLARATION__FIELDS, FieldDeclaration.class, "fieldDeclaration",
                            setRepresentingNode(),
                            stopRecursion(), // no transformations beyond this point
                            property(attributeContains(FIELD_DECLARATION__MODIFIERS, "final")),
                            property(attributeContains(FIELD_DECLARATION__MODIFIERS, "static")),
                            property(notEmpty(VALUE_DECLARATION__USAGES)),
                            related(nthElement(VALUE_DECLARATION__USAGES, 0), MemberExpression.class, "firstUsage",
                                related(MEMBER_EXPRESSION__RECORD_DECLARATION, RecordDeclaration.class, "usingRecord",
                                    notEqualTo("definingRecord"))
                            ),
                            forAllRelated(VALUE_DECLARATION__USAGES, MemberExpression.class, "fieldUsages",
                                relatedExisting(MEMBER_EXPRESSION__RECORD_DECLARATION, RecordDeclaration.class, "usingRecord")
                            )
                        )
                    ),
                    create(RecordDeclaration.class, "usingRecord"
                        // field declaration should go here
                    ),
                    create(MemberExpression.class, "firstUsage",
                        related(MEMBER_EXPRESSION__BASE, Reference.class, "defRecordReference",
                            relatedExisting(REFERENCE__REFERS_TO, RecordDeclaration.class, "definingRecord")
                        )
                    )

                );
            }
        }.build();
        MultiGraphPattern targetPattern = new GraphPatternBuilder() {
            @Override
            public MultiGraphPattern build() {
                return multiRoot(
                    create(RecordDeclaration.class, "definingRecord"
                        // field declaration removed from here
                    ),
                    create(RecordDeclaration.class, "usingRecord",
                        related1ToN(RECORD_DECLARATION__FIELDS, FieldDeclaration.class, "fieldDeclaration")
                    ),
                    create(MemberExpression.class, "firstUsage",
                        related(MEMBER_EXPRESSION__BASE, Reference.class, "defRecordReference",
                            // adjust reference
                            relatedExisting(REFERENCE__REFERS_TO, RecordDeclaration.class, "usingRecord")
                        )
                    )
                );
            }
        }.build();

        return GraphTransformation.Builder.from(sourcePattern, targetPattern, "moveConstantToOnlyUsingClass", PHASE_TWO).build();
    }

    private static GraphTransformation<Node> inlineSingleUseVariable() {
        MultiGraphPattern sourcePattern = new GraphPatternBuilder() {
            @Override
            public MultiGraphPattern build() {
                return multiRoot(
                    // parent pointer
                    wildcardParent(VariableDeclaration.class, "varDecl",
                        setRepresentingNode(),
                        property(notNull(NODE__LOCATION)),
                        property(nElements(VALUE_DECLARATION__USAGES, 1)),
                        related(nthElement(VALUE_DECLARATION__USAGES, 0), Reference.class, "variableUsage"),
                        related(VARIABLE_DECLARATION__INITIALIZER, Expression.class, "varValue",
                            assignedValueStableBetween("varDecl", "variableUsage")
                        )
                    ),
                    wildcardParent(Reference.class, "variableUsage",
                        relatedExisting(REFERENCE__REFERS_TO, VariableDeclaration.class, "varDecl")
                    )
                );
            }
        }.build();

        MultiGraphPattern targetPattern = new GraphPatternBuilder() {
            @Override
            public MultiGraphPattern build() {
                return multiRoot(
                    // remove variable declaration from AST parent
                    emptyWildcardParent(),
                    // replace variable reference by value
                    wildcardParent(Expression.class, "varValue")

                );
            }
        }.build();

        return GraphTransformation.Builder.from(sourcePattern, targetPattern, "inlineSingleUseVariable", PHASE_TWO).build();
    }

    private static GraphTransformation<Node> inlineSingleUseConstant() {
        MultiGraphPattern sourcePattern = new GraphPatternBuilder() {
            @Override
            public MultiGraphPattern build() {
                return multiRoot(
                    // parent pointer
                    create(RecordDeclaration.class, "containingClass",
                        related1ToN(RECORD_DECLARATION__FIELDS, FieldDeclaration.class, "fieldDecl",
                            setRepresentingNode(),
                            property(notNull(NODE__LOCATION)),
                            property(attributeContains(FIELD_DECLARATION__MODIFIERS, "final")),
                            property(attributeContains(FIELD_DECLARATION__MODIFIERS, "static")),
                            property(nElements(VALUE_DECLARATION__USAGES, 1)),
                            related(nthElement(VALUE_DECLARATION__USAGES, 0), Reference.class, "fieldUsage"),
                            related(VARIABLE_DECLARATION__INITIALIZER, Expression.class, "fieldValue")
                        )
                    ),
                    wildcardParent(Reference.class, "fieldUsage",
                        relatedExisting(REFERENCE__REFERS_TO, FieldDeclaration.class, "fieldDecl")
                    )
                );
            }
        }.build();

        MultiGraphPattern targetPattern = new GraphPatternBuilder() {
            @Override
            public MultiGraphPattern build() {
                return multiRoot(
                    // remove field declaration from class
                    create(RecordDeclaration.class, "containingClass"),
                    // replace variable reference by value
                    wildcardParent(Expression.class, "fieldValue")
                );
            }
        }.build();

        return GraphTransformation.Builder.from(sourcePattern, targetPattern, "inlineSingleUseConstant", PHASE_TWO).build();
    }

    private static GraphTransformation<RecordDeclaration> removeImplicitStandardConstructor() {
        SimpleGraphPattern<RecordDeclaration> sourcePattern = new GraphPatternBuilder() {
            @Override
            public SimpleGraphPattern<RecordDeclaration> build() {

                return create(RecordDeclaration.class, "definingRecord",
                    related1ToN(RECORD_DECLARATION__CONSTRUCTORS, ConstructorDeclaration.class, "constructor",
                        property(attributeEquals(NODE__LOCATION, null))
                    )
                );
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
        return GraphTransformation.Builder.from(sourcePattern, targetPattern, "removeImplicitStandardConstructor", PHASE_TWO).build();
    }

    private static GraphTransformation<Node> inlineMagicNumber() {
        /* defective */
        MultiGraphPattern sourcePattern = new GraphPatternBuilder() {
            @Override
            public MultiGraphPattern build() {
                return multiRoot(
                    create(RecordDeclaration.class, "definingRecord",
                        stopRecursion(),
                        related1ToN(RECORD_DECLARATION__FIELDS, FieldDeclaration.class, "fieldDeclaration",
                            setRepresentingNode(),
                            property(attributeContains(FIELD_DECLARATION__MODIFIERS, "final")),
                            property(attributeContains(FIELD_DECLARATION__MODIFIERS, "static")),
                            property(notEmpty(VALUE_DECLARATION__USAGES)),
                            related1ToN(VALUE_DECLARATION__USAGES, Reference.class, "constantUsage"),
                            related(VALUE_DECLARATION__TYPE, NumericType.class, "fieldType"),
                            related(VARIABLE_DECLARATION__INITIALIZER, Literal.class, "constantValue")
                        )
                    ),
                    wildcardParent(Reference.class, "constantUsage")
                );
            }
        }.build();

        MultiGraphPattern targetPattern = new GraphPatternBuilder() {
            @Override
            public MultiGraphPattern build() {
                return multiRoot(
                    create(RecordDeclaration.class, "definingRecord"),
                    // replace variable reference by value
                    wildcardParent(Expression.class, "constantValue")

                );
            }
        }.build();

        return GraphTransformation.Builder.from(sourcePattern, targetPattern, "inlineMagicNumber", PHASE_TWO).build();
    }

    private static GraphTransformation<Node> inlineAuxiliaryMethod() {
        MultiGraphPattern sourcePattern = new GraphPatternBuilder() {
            @Override
            public MultiGraphPattern build() {
                /*return multiRoot(
                    create(RecordDeclaration.class, "record",
                        related1ToN(RECORD_DECLARATION__METHODS, MethodDeclaration.class, "method",
                            property(nElements(FUNCTION_DECLARATION__OVERRIDES, 0)),
                            property(nElements(FUNCTION_DECLARATION__OVERRIDDEN_BY, 0)),
                            property(nElements(FUNCTION_DECLARATION__INVOCATIONS, 1))
                            related(METHOD_DECLARATION__BODY, Statement.class, "body",
                                property(isEogConfluent())
                            )
                        )
                    ),
                    wildcardParent(CallExpression.class, "methodCall", )
                 */
                return null;
            }
        }.build();
        return null;
    }

    private static GraphTransformation<Block> unmergeMultiDeclarationStatement() {

        // Untested: SetOperation for nthElement

        GraphPattern sourcePattern = new GraphPatternBuilder() {
            @Override
            public GraphPattern build() {
                return create(Block.class, "surroundingBlock",
                    related1ToN(BLOCK__STATEMENTS, DeclarationStatement.class, "declStatement",
                        stopRecursion(),
                        property(notEmpty(DECLARATION_STATEMENT__DECLARATIONS)),
                        related(nthElement(DECLARATION_STATEMENT__DECLARATIONS, 0), VariableDeclaration.class, "firstVarDecl"),
                        related1ToN(DECLARATION_STATEMENT__DECLARATIONS, VariableDeclaration.class, "otherDeclaration",
                            notEqualTo("firstVarDecl")
                        )
                    )
                );
            }
        }.build();

        GraphPattern targetPattern = new GraphPatternBuilder() {
            @Override
            public GraphPattern build() {
                return create(Block.class, "surroundingBlock",
                    related1ToNSequence(BLOCK__STATEMENTS, Statement.class,
                        node(DeclarationStatement.class, Statement.class, "declStatement"),
                        node(DeclarationStatement.class, Statement.class,  "otherVarDecl",
                            relatedExisting(nthElement(DECLARATION_STATEMENT__DECLARATIONS, 0), VariableDeclaration.class, "otherDeclaration")
                        )
                    )
                );
            }
        }.build();

        return null;
    }

    private static GraphTransformation<Node> removeEmptyVoidMethod() {
        MultiGraphPattern sourcePattern = new GraphPatternBuilder() {
            @Override
            public MultiGraphPattern build() {
                return multiRoot(
                    create(RecordDeclaration.class, "containingRecord",
                        related1ToN(RECORD_DECLARATION__METHODS, MethodDeclaration.class, "methodDecl",
                            related(VALUE_DECLARATION__TYPE, FunctionType.class, "returnType",
                                property(nElements(FUNCTION_TYPE__RETURN_TYPES, 1)),
                                related(nthElement(FUNCTION_TYPE__RETURN_TYPES, 0), IncompleteType.class, "voidType",
                                    property(attributeEquals(INCOMPLETE_TYPE__TYPE_NAME, "void"))
                                )
                            ),
                            related(METHOD_DECLARATION__BODY, Block.class, "methodBlock",
                                property(isEmpty(BLOCK__STATEMENTS))
                            )
                        )
                    ),
                    wildcardParent(CallExpression.class, "methodCall",
                        relatedExisting1ToN(CALL_EXPRESSION__INVOKES, MethodDeclaration.class, "methodDecl")
                    )
                );
            }
        }.build();

        MultiGraphPattern targetPattern = new GraphPatternBuilder() {
            @Override
            public MultiGraphPattern build() {
                return multiRoot(
                    create(RecordDeclaration.class, "containingRecord",
                        stopRecursion(),
                        related1ToN(RECORD_DECLARATION__METHODS, MethodDeclaration.class, "methodDecl",
                            related(VALUE_DECLARATION__TYPE, FunctionType.class, "returnType",
                                property(nElements(FUNCTION_TYPE__RETURN_TYPES, 1)),
                                related(nthElement(FUNCTION_TYPE__RETURN_TYPES, 0), IncompleteType.class, "voidType",
                                    property(attributeEquals(INCOMPLETE_TYPE__TYPE_NAME, "void"))
                                )
                            ),
                            related(METHOD_DECLARATION__BODY, Block.class, "methodBlock",
                                property(isEmpty(BLOCK__STATEMENTS))
                            )
                        )
                    ),
                    wildcardParent(CallExpression.class, "methodCall"
                        // remove call
                    )
                );
            }
        }.build();

        return GraphTransformation.Builder.from(sourcePattern, targetPattern, "removeEmptyVoidMethod", PHASE_TWO).build();
    }

    private static GraphTransformation<TranslationUnitDeclaration> removeEmptyRecord() {
        SimpleGraphPattern<TranslationUnitDeclaration> sourcePattern = new GraphPatternBuilder() {
            @Override
            public SimpleGraphPattern<TranslationUnitDeclaration> build() {
                return create(TranslationUnitDeclaration.class, "containingFile",
                    related1ToN(TRANSLATION_UNIT__DECLARATIONS, RecordDeclaration.class, "emptyRecord",
                        property(nElements(RECORD_DECLARATION__FIELDS, 0)),
                        property(nElements(RECORD_DECLARATION__METHODS, 0))
                    )
                );
            }
        }.build();

        SimpleGraphPattern<TranslationUnitDeclaration> targetPattern = new GraphPatternBuilder() {
            @Override
            public SimpleGraphPattern<TranslationUnitDeclaration> build() {
                // remove RecordDeclaration from TranslationUnitDeclaration
                return create(TranslationUnitDeclaration.class, "containingFile");
            }
        }.build();
        return GraphTransformation.Builder.from(sourcePattern, targetPattern, "removeEmptyRecord", PHASE_TWO).build();
    }

    private static GraphTransformation<Component> removeEmptyFile() {
        SimpleGraphPattern<Component> sourcePattern = new GraphPatternBuilder() {
            @Override
            public SimpleGraphPattern<Component> build() {
                return create(Component.class, "project",
                    related1ToN(COMPONENT__TRANSLATION_UNITS, TranslationUnitDeclaration.class, "emptyFile",
                        property(nElements(TRANSLATION_UNIT__DECLARATIONS, 0))
                    )
                );
            }
        }.build();

        SimpleGraphPattern<Component> targetPattern = new GraphPatternBuilder() {
            @Override
            public SimpleGraphPattern<Component> build() {
                // remove RecordDeclaration from TranslationUnitDeclaration
                return create(Component.class, "project");
            }
        }.build();
        return GraphTransformation.Builder.from(sourcePattern, targetPattern, "removeEmptyFile", PHASE_TWO).build();
    }

    private static GraphTransformation<IfStatement> wrapThenStatement() {
        SimpleGraphPattern<IfStatement> sourcePattern = new GraphPatternBuilder() {
            @Override
            public SimpleGraphPattern<IfStatement> build() {
                return create(IfStatement.class, "ifStatement",
                    property(notInstanceOf(IF_STATEMENT__THEN_STATEMENT, Block.class)),
                    related(IF_STATEMENT__THEN_STATEMENT, Statement.class, "thenStatement")
                );
            }
        }.build();

        SimpleGraphPattern<IfStatement> targetPattern = new GraphPatternBuilder() {
            @Override
            public SimpleGraphPattern<IfStatement> build() {
                return create(IfStatement.class, "ifStatement",
                    related(IF_STATEMENT__THEN_STATEMENT, Block.class, "thenBlock",
                        related(nthElement(BLOCK__STATEMENTS, 0), Statement.class, "thenStatement")
                    )
                );
            }
        }.build();
        return GraphTransformation.Builder.from(sourcePattern, targetPattern, "wrapThenStatement", PHASE_ONE).build();
    }


    private static GraphTransformation<IfStatement> wrapElseStatement() {
        SimpleGraphPattern<IfStatement> sourcePattern = new GraphPatternBuilder() {
            @Override
            public SimpleGraphPattern<IfStatement> build() {
                return create(IfStatement.class, "ifStatement",
                    property(notNull(IF_STATEMENT__ELSE_STATEMENT)),
                    property(notInstanceOf(IF_STATEMENT__ELSE_STATEMENT, Block.class)),
                    related(IF_STATEMENT__ELSE_STATEMENT, Statement.class, "elseStatement")
                );
            }
        }.build();

        SimpleGraphPattern<IfStatement> targetPattern = new GraphPatternBuilder() {
            @Override
            public SimpleGraphPattern<IfStatement> build() {
                return create(IfStatement.class, "ifStatement",
                    related(IF_STATEMENT__ELSE_STATEMENT, Block.class, "elseBlock",
                        related(nthElement(BLOCK__STATEMENTS, 0), Statement.class, "elseStatement")
                    )
                );
            }
        }.build();
        return GraphTransformation.Builder.from(sourcePattern, targetPattern, "wrapElseStatement", PHASE_ONE).build();
    }

}
