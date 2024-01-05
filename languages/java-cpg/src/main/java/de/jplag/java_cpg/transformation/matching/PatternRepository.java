package de.jplag.java_cpg.transformation.matching;

import de.fraunhofer.aisec.cpg.graph.declarations.FieldDeclaration;
import de.fraunhofer.aisec.cpg.graph.declarations.MethodDeclaration;
import de.fraunhofer.aisec.cpg.graph.declarations.ParameterDeclaration;
import de.fraunhofer.aisec.cpg.graph.declarations.RecordDeclaration;
import de.fraunhofer.aisec.cpg.graph.statements.IfStatement;
import de.fraunhofer.aisec.cpg.graph.statements.ReturnStatement;
import de.fraunhofer.aisec.cpg.graph.statements.Statement;
import de.fraunhofer.aisec.cpg.graph.statements.expressions.*;
import de.fraunhofer.aisec.cpg.graph.types.FunctionType;
import de.fraunhofer.aisec.cpg.graph.types.IncompleteType;
import de.fraunhofer.aisec.cpg.graph.types.ObjectType;
import de.jplag.java_cpg.transformation.matching.pattern.GraphPattern;
import de.jplag.java_cpg.transformation.matching.pattern.GraphPatternBuilder;
import de.jplag.java_cpg.transformation.matching.pattern.NodePattern;
import de.jplag.java_cpg.transformation.matching.pattern.PatternUtil;

import java.util.List;

import static de.jplag.java_cpg.transformation.matching.edges.Edges.*;
import static de.jplag.java_cpg.transformation.matching.pattern.PatternUtil.*;

public final class PatternRepository {

    private PatternRepository() {/* should not be instantiated */}

    public static GraphPatternBuilder<IfStatement> ifElseWithNegatedCondition() {

        return new GraphPatternBuilder<>() {
            @Override
            public GraphPattern<IfStatement> build() {
                return create(IfStatement.class, "root",
                    related(IF_STATEMENT__CONDITION, UnaryOperator.class,
                        "condition",
                        property(PatternUtil.attributeEquals(UNARY_OPERATOR__OPERATOR_CODE, "!")),
                        related(UNARY_OPERATOR__INPUT, Expression.class, "innerCondition")),
                    related(IF_STATEMENT__THEN_STATEMENT, Statement.class, "thenStatement"),
                    related(IF_STATEMENT__ELSE_STATEMENT, Statement.class, "elseStatement")
                );
            }
        };
    }

    static GraphPatternBuilder<BinaryOperator> leftFacingComparison() {
       return new GraphPatternBuilder<>() {
            @Override
            public GraphPattern<BinaryOperator> build() {
                return create(BinaryOperator.class, "comparison",
                    related(BINARY_OPERATOR__LHS, Expression.class, "leftHandSide"),
                    property(oneOf(BINARY_OPERATOR__OPERATOR_CODE, List.of(">=", ">"))),
                    related(BINARY_OPERATOR__RHS, Expression.class, "rightHandSide")
                );
            }
        };

    }

    static NodePattern<MethodDeclaration> methodWithNCalls(int n) {
        var methodDecl = NodePattern.forNodeType(MethodDeclaration.class);
        methodDecl.addProperty(nElements(METHOD_DECLARATION__USAGES, n));

        return methodDecl;
    }

    public static GraphPatternBuilder<MethodDeclaration> setterMethod() {
        return new GraphPatternBuilder<>() {

            @Override
            public GraphPattern<MethodDeclaration> build() {
                return create(MethodDeclaration.class, "methodDecl",
                    related(METHOD_DECLARATION__RECORD_DECLARATION, RecordDeclaration.class, "classDecl",
                        related1ToN(RECORD_DECLARATION__FIELDS, FieldDeclaration.class, "fieldDecl",
                            related(FIELD_DECLARATION__TYPE, ObjectType.class, "fieldType")
                        ),
                        relatedExisting1ToN(RECORD_DECLARATION__METHODS, "methodDecl")
                    ),
                    related(METHOD_DECLARATION__TYPE, FunctionType.class, "methodType",
                        property(nElements(FUNCTION_TYPE__PARAMETERS, 1)),
                        relatedExisting(nthElement(FUNCTION_TYPE__PARAMETERS, 0), "fieldType"),
                        property(nElements(FUNCTION_TYPE__RETURN_TYPES, 1)),
                        related(nthElement(FUNCTION_TYPE__RETURN_TYPES, 0), IncompleteType.class, "voidType",
                            property(PatternUtil.attributeEquals(INCOMPLETE_TYPE__TYPE_NAME, "void"))
                        )
                    ),
                    related(nthElement(METHOD_DECLARATION__PARAMETERS, 0), ParameterDeclaration.class, "paramDecl"),
                    property(MethodDeclaration::hasBody),
                    related(METHOD_DECLARATION__BODY, Block.class,"methodBody",
                        property(nElements(BLOCK__STATEMENTS, 2)),
                        related(nthElement(BLOCK__STATEMENTS, 0), AssignExpression.class, "assignExpr",
                            property(nElements(ASSIGN_EXPRESSION__LHS, 1)),
                            related(nthElement(ASSIGN_EXPRESSION__LHS, 0), Reference.class, "fieldReference"),
                            property(nElements(ASSIGN_EXPRESSION__RHS, 1)),
                            related(nthElement(ASSIGN_EXPRESSION__RHS, 0), Reference.class, "paramReference",
                                relatedExisting(REFERENCE__REFERS_TO, "paramDecl")
                            )
                        ),
                        related(nthElement(BLOCK__STATEMENTS, 1), ReturnStatement.class, "returnStmt",
                            property(nElements(RETURN_STATEMENT__RETURN_VALUES, 0))
                        )
                    )
                );
            }
        };

    }

    public static NodePattern<IfStatement> ifWithElse() {
        var ifStatement = NodePattern.forNodeType(IfStatement.class);
        var condition = NodePattern.forNodeType(Expression.class);
        ifStatement.addRelatedNodePattern(condition, IF_STATEMENT__CONDITION);
        var thenStatement = NodePattern.forNodeType(Block.class);
        ifStatement.addRelatedNodePattern(thenStatement, IF_STATEMENT__THEN_STATEMENT);
        ifStatement.addProperty(notNull(IF_STATEMENT__ELSE_STATEMENT));
        var elseStatement = NodePattern.forNodeType(Block.class);
        ifStatement.addRelatedNodePattern(elseStatement, IF_STATEMENT__ELSE_STATEMENT);

        return ifStatement;
    }

}
