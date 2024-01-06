package de.jplag.java_cpg.transformation;

import de.fraunhofer.aisec.cpg.graph.Node;
import de.fraunhofer.aisec.cpg.graph.declarations.Declaration;
import de.fraunhofer.aisec.cpg.graph.declarations.RecordDeclaration;
import de.fraunhofer.aisec.cpg.graph.declarations.TranslationUnitDeclaration;
import de.fraunhofer.aisec.cpg.graph.declarations.VariableDeclaration;
import de.fraunhofer.aisec.cpg.graph.statements.*;
import de.fraunhofer.aisec.cpg.graph.statements.expressions.Block;
import de.fraunhofer.aisec.cpg.graph.statements.expressions.Expression;
import de.fraunhofer.aisec.cpg.graph.statements.expressions.UnaryOperator;
import de.jplag.java_cpg.transformation.matching.pattern.GraphPattern;
import de.jplag.java_cpg.transformation.matching.pattern.GraphPatternBuilder;
import de.jplag.java_cpg.transformation.matching.pattern.PatternUtil;

import static de.jplag.java_cpg.transformation.GraphTransformation.ExecutionPhase.PHASE_ONE;
import static de.jplag.java_cpg.transformation.GraphTransformation.ExecutionPhase.PHASE_TWO;
import static de.jplag.java_cpg.transformation.matching.edges.Edges.*;
import static de.jplag.java_cpg.transformation.matching.pattern.PatternUtil.*;

/**
 * Contains factory methods to create different {@link GraphTransformation}s.
 */
public class TransformationRepository {

    private TransformationRepository() {}

    /**
     * Creates a {@link GraphTransformation} that un-negates the condition of an if statement and swaps the then and else blocks.
     *
     * @return the graph transformation object
     */
    public static GraphTransformation<IfStatement> ifWithNegatedConditionResolution() {
        GraphPattern<IfStatement> sourcePattern = new GraphPatternBuilder<IfStatement>() {
            @Override
            public GraphPattern<IfStatement> build() {
                return create(IfStatement.class, "ifStatement",
                    related(IF_STATEMENT__CONDITION, UnaryOperator.class,
                        "condition",
                        property(attributeEquals(UNARY_OPERATOR__OPERATOR_CODE, "!")),
                        related(UNARY_OPERATOR__INPUT, Expression.class, "innerCondition")),
                    related(IF_STATEMENT__THEN_STATEMENT, Statement.class, "thenStatement"),
                    related(IF_STATEMENT__ELSE_STATEMENT, Statement.class, "elseStatement")
                );
            }
        }.build();

        GraphPattern<IfStatement> targetPattern = new GraphPatternBuilder<IfStatement>() {

            @Override
            public GraphPattern<IfStatement> build() {
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
    public static GraphTransformation<Node> removeUnusedVariableDeclarationStatements() {
        GraphPattern<Node> sourcePattern = new GraphPatternBuilder<Node>() {
            @Override
            public GraphPattern<Node> build() {
                return wildcardParent(DeclarationStatement.class, "declStmt",
                    property(nElements(DECLARATION_STATEMENT__DECLARATIONS, 1)),
                    related(nthElement(DECLARATION_STATEMENT__DECLARATIONS, 0), VariableDeclaration.class, "varDecl",
                        property(nElements(VARIABLE_DECLARATION__USAGES, 0))
                    )
                );
            }
        }.build();

        GraphPattern<Node> targetPattern = new GraphPatternBuilder<Node>() {
            @Override
            public GraphPattern<Node> build() {
                return wildcardParent(DeclarationStatement.class, "declStmt",
                    removeMatch()
                );
            }
        }.build();
        return GraphTransformation.Builder.from(sourcePattern, targetPattern, "removeUnusedVariableDeclarationStatements", PHASE_ONE).build();
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
    public static GraphTransformation<Node> removeUnusedVariableDeclarations() {
        GraphPattern<Node> sourcePattern = new GraphPatternBuilder<Node>() {
            @Override
            public GraphPattern<Node> build() {
                return wildcardParent(VariableDeclaration.class, "variableDecl",
                    property(nElements(VARIABLE_DECLARATION__USAGES, 0))
                );
            }
        }.build();
        GraphPattern<Node> targetPattern = new GraphPatternBuilder<Node>() {
            @Override
            public GraphPattern<Node> build() {
                return wildcardParent(VariableDeclaration.class, "variableDecl",
                    removeMatch()
                );
            }
        }.build();

        return GraphTransformation.Builder.from(sourcePattern, targetPattern, "removeUnusedVariableDeclarations", PHASE_ONE).build();
    }

    /**
     * Creates a {@link GraphTransformation} that removes a {@link DeclarationStatement} with no {@link Declaration}s.
     *
     * @return the graph transformation object
     */
    public static GraphTransformation<Node> removeEmptyDeclarationStatement() {
        GraphPattern<Node> sourcePattern = new GraphPatternBuilder<Node>() {
            @Override
            public GraphPattern<Node> build() {
                return wildcardParent(DeclarationStatement.class, "declStatement",
                    property(PatternUtil.nElements(DECLARATION_STATEMENT__DECLARATIONS, 0))
                );
            }
        }.build();
        GraphPattern<Node> targetPattern = new GraphPatternBuilder<Node>() {
            @Override
            public GraphPattern<Node> build() {
                return wildcardParent(DeclarationStatement.class, "declStatement",
                    removeMatch());
            }
        }.build();

        return GraphTransformation.Builder.from(sourcePattern, targetPattern, "removeEmptyDeclarationStatement", PHASE_ONE).build();
    }

    /**
     * Creates a {@link GraphTransformation} that replaces {@link ForStatement}s by equivalent {@link WhileStatement}s.
     * @return the graph transformation
     */
    public static GraphTransformation<Node> forStatementToWhileStatement() {
        GraphPattern<Node> sourcePattern = new GraphPatternBuilder<Node>() {
            @Override
            public GraphPattern<Node> build() {
                return wildcardParent(ForStatement.class, "forStatement",
                    related(FOR_STATEMENT__INITIALIZER_STATEMENT, Statement.class, "initStatement"),
                    related(FOR_STATEMENT__CONDITION, Expression.class, "condition"),
                    related(FOR_STATEMENT__ITERATION_STATEMENT, Statement.class, "iterationStatement"),
                    related(FOR_STATEMENT__STATEMENT, Statement.class, "body")
                );
            }
        }.build();

        GraphPattern<Node> targetPattern = new GraphPatternBuilder<Node>() {
            @Override
            public GraphPattern<Node> build() {
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
     * @return the graph transformation
     */
    public static GraphTransformation<Block> inlineInnerBlocks() {
        new GraphPatternBuilder<Block>() {
            @Override
            public GraphPattern<Block> build() {
            return create(Block.class, "outerBlock",
                related1ToN(BLOCK__STATEMENTS, Block.class, "innerBlock")
            );
            }
        }.build();
        return null;
    }

    public static GraphTransformation<TranslationUnitDeclaration> removeLibraryRecords() {
        GraphPattern<TranslationUnitDeclaration> sourcePattern = new GraphPatternBuilder<TranslationUnitDeclaration>() {
            @Override
            public GraphPattern<TranslationUnitDeclaration> build() {
                return create(TranslationUnitDeclaration.class, "file",
                    related1ToN(TRANSLATION_UNIT__DECLARATIONS, RecordDeclaration.class, "declaration",
                        property(attributeEquals(RECORD_DECLARATION__LOCATION, null))));
            }
        }.build();
        GraphPattern<TranslationUnitDeclaration> targetPattern = new GraphPatternBuilder<TranslationUnitDeclaration>() {
            @Override
            public GraphPattern<TranslationUnitDeclaration> build() {
                return create(TranslationUnitDeclaration.class, "file",
                    related1ToN(TRANSLATION_UNIT__DECLARATIONS, Declaration.class, "declaration",
                        removeMatch()
                    )
                );
            }
        }.build();

        return GraphTransformation.Builder.from(sourcePattern, targetPattern, "removeLibraryRecords", PHASE_TWO).build();
    }
}
