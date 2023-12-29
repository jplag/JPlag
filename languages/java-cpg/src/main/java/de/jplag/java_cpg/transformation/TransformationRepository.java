package de.jplag.java_cpg.transformation;

import de.fraunhofer.aisec.cpg.graph.Node;
import de.fraunhofer.aisec.cpg.graph.declarations.Declaration;
import de.fraunhofer.aisec.cpg.graph.declarations.VariableDeclaration;
import de.fraunhofer.aisec.cpg.graph.statements.DeclarationStatement;
import de.fraunhofer.aisec.cpg.graph.statements.EmptyStatement;
import de.fraunhofer.aisec.cpg.graph.statements.IfStatement;
import de.fraunhofer.aisec.cpg.graph.statements.Statement;
import de.fraunhofer.aisec.cpg.graph.statements.expressions.Expression;
import de.fraunhofer.aisec.cpg.graph.statements.expressions.UnaryOperator;

import de.jplag.java_cpg.transformation.matching.pattern.GraphPattern;
import de.jplag.java_cpg.transformation.matching.pattern.GraphPatternBuilder;
import de.jplag.java_cpg.transformation.matching.pattern.PatternUtil;

import static de.jplag.java_cpg.transformation.matching.edges.Edges.*;
import static de.jplag.java_cpg.transformation.matching.pattern.PatternUtil.*;

/**
 * Contains factory methods to create different {@link GraphTransformation}s.
 */
public class TransformationRepository {

    private TransformationRepository() {}

    /**
     * Creates a {@link GraphTransformation} that un-negates the condition of an if statement and swaps the then and else blocks.
     * @return the graph transformation object
     */
    public static GraphTransformation<IfStatement> ifWithNegatedConditionResolution() {
        GraphPattern<IfStatement> sourcePattern = new GraphPatternBuilder<IfStatement>() {
            @Override
            public GraphPattern<IfStatement> build() {
                return create(IfStatement.class, "ifStatement",
                    related(IF_STATEMENT__CONDITION,  UnaryOperator.class,
                        "condition",
                        property(areEqual(UnaryOperator::getOperatorCode, "!")),
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

        return GraphTransformation.Builder.from(sourcePattern, targetPattern, "ifWithNegatedConditionResolution").build();
    }

    /**
     * Creates a {@link GraphTransformation} that removes a {@link DeclarationStatement} with exactly one unused {@link VariableDeclaration}.
     * @return the graph transformation object
     */
    public static GraphTransformation<Node> removeUnusedVariableDeclarationStatements() {
        GraphPattern<Node> sourcePattern = new GraphPatternBuilder<Node>() {
            @Override
            public GraphPattern<Node> build() {
                return wildcardParent(DeclarationStatement.class, "declStmt",
                    property(nElements(DECLARATION_STATEMENT__DECLARATIONS, 1)),
                    related(nthElement(DECLARATION_STATEMENT__DECLARATIONS,0), VariableDeclaration.class, "varDecl",
                        property(nElements(VARIABLE_DECLARATION__USAGES, 0))
                    )
                );
            }
        }.build();

        GraphPattern<Node> targetPattern = new GraphPatternBuilder<Node>() {
            @Override
            public GraphPattern<Node> build() {
                return wildcardParent(remove(DeclarationStatement.class, "declStmt"));
            }
        }.build();
        return GraphTransformation.Builder.from(sourcePattern, targetPattern, "removeUnusedVariableDeclarationStatements").build();
    }


    /**
     * Creates a {@link GraphTransformation} that replaces a {@link VariableDeclaration} of an unused variable by an {@link EmptyStatement}.
     *
     * May target the following edges:<br><ul>
     *  <li>Statement --LOCALS*--> VariableDeclaration</li>
     *  <li>AssignExpression --DECLARATIONS*--> VariableDeclaration</li>
     *  <li>CatchClause --PARAMETER--> VariableDeclaration</li>
     *  <li>MethodDeclaration --RECEIVER--> VariableDeclaration</li>
     *  </ul>
     *
     *  @return the graph transformation object
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
            return wildcardParent(remove(VariableDeclaration.class, "variableDecl"));
            }
        }.build();

        return GraphTransformation.Builder.from(sourcePattern, targetPattern, "removeUnusedVariableDeclarations").build();
    }

    /**
     * Creates a {@link GraphTransformation} that removes a {@link DeclarationStatement} with no {@link Declaration}s.
     * @return the graph transformation object
     */
    public static GraphTransformation<Node> removeEmptyDeclarationStatement() {
        GraphPattern<Node> sourcePattern = new GraphPatternBuilder<Node>() {            @Override
            public GraphPattern<Node> build() {
            return wildcardParent(DeclarationStatement.class, "declStatement",
                property(PatternUtil.nElements(DECLARATION_STATEMENT__DECLARATIONS, 0))
                );
            }
        }.build();
        GraphPattern<Node> targetPattern = new GraphPatternBuilder<Node>() {
            @Override
            public GraphPattern<Node> build() {
                return wildcardParent(remove(DeclarationStatement.class, "declStatement"));
            }
        }.build();

        return GraphTransformation.Builder.from(sourcePattern, targetPattern, "removeEmptyDeclarationStatement").build();
    }
}
