package de.jplag.java_cpg.transformation.matching;

import static de.jplag.java_cpg.transformation.matching.edges.Edges.*;
import static de.jplag.java_cpg.transformation.matching.pattern.PatternUtil.*;

import java.util.List;

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

/**
 *  This class is used to collect sub-patterns that may appear repetitively, or used in tests.
 */
public final class PatternRepository {

    private PatternRepository() {
        /* should not be instantiated */}

    public static GraphPatternBuilder ifElseWithNegatedCondition() {

        return new GraphPatternBuilder() {
            @Override
            public GraphPattern build() {
                return create(IfStatement.class, "root",
                        related(IF_STATEMENT__CONDITION, UnaryOperator.class, "condition",
                                property(PatternUtil.attributeEquals(UNARY_OPERATOR__OPERATOR_CODE, "!")),
                                related(UNARY_OPERATOR__INPUT, Expression.class, "innerCondition")),
                        related(IF_STATEMENT__THEN_STATEMENT, Statement.class, "thenStatement"),
                        related(IF_STATEMENT__ELSE_STATEMENT, Statement.class, "elseStatement"));
            }
        };
    }

    public static GraphPatternBuilder setterMethod() {
        return new GraphPatternBuilder() {

            @Override
            public GraphPattern build() {
                return create(MethodDeclaration.class, "methodDecl",
                        related(METHOD_DECLARATION__RECORD_DECLARATION, RecordDeclaration.class, "classDecl",
                                related1ToN(RECORD_DECLARATION__FIELDS, FieldDeclaration.class, "fieldDecl",
                                        related(VALUE_DECLARATION__TYPE, ObjectType.class, "fieldType")),
                                relatedExisting1ToN(RECORD_DECLARATION__METHODS, MethodDeclaration.class, "methodDecl")),
                        related(VALUE_DECLARATION__TYPE, FunctionType.class, "methodType", property(nElements(FUNCTION_TYPE__PARAMETERS, 1)),
                                relatedExisting(nthElement(FUNCTION_TYPE__PARAMETERS, 0), ObjectType.class, "fieldType"),
                                property(nElements(FUNCTION_TYPE__RETURN_TYPES, 1)),
                                related(nthElement(FUNCTION_TYPE__RETURN_TYPES, 0), IncompleteType.class, "voidType",
                                        property(PatternUtil.attributeEquals(TYPE__TYPE_NAME, "void")))),
                        property(notEmpty(METHOD_DECLARATION__PARAMETERS)),
                        related(nthElement(METHOD_DECLARATION__PARAMETERS, 0), ParameterDeclaration.class, "paramDecl"),
                        property(MethodDeclaration::hasBody),
                        related(METHOD_DECLARATION__BODY, Block.class, "methodBody", property(nElements(BLOCK__STATEMENTS, 2)),
                                related(nthElement(BLOCK__STATEMENTS, 0), AssignExpression.class, "assignExpr",
                                        related(ASSIGN_EXPRESSION__LHS, Reference.class, "fieldReference"),

                                        related(ASSIGN_EXPRESSION__RHS, Reference.class, "paramReference",
                                                relatedExisting(REFERENCE__REFERS_TO, ParameterDeclaration.class, "paramDecl"))),
                                related(nthElement(BLOCK__STATEMENTS, 1), ReturnStatement.class, "returnStmt",
                                        property(nElements(RETURN_STATEMENT__RETURN_VALUES, 0)))));
            }
        };

    }


}
