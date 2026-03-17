package de.jplag.java_cpg.transformation.matching;

import static de.jplag.java_cpg.transformation.StructuralRole.ASSIGN_EXPRESSION;
import static de.jplag.java_cpg.transformation.StructuralRole.CLASS_DECLARATION;
import static de.jplag.java_cpg.transformation.StructuralRole.CONDITION;
import static de.jplag.java_cpg.transformation.StructuralRole.ELSE_STATEMENT;
import static de.jplag.java_cpg.transformation.StructuralRole.FIELD_DECLARATION;
import static de.jplag.java_cpg.transformation.StructuralRole.FIELD_REFERENCE;
import static de.jplag.java_cpg.transformation.StructuralRole.FIELD_TYPE;
import static de.jplag.java_cpg.transformation.StructuralRole.IF_STATEMENT;
import static de.jplag.java_cpg.transformation.StructuralRole.INNER_CONDITION;
import static de.jplag.java_cpg.transformation.StructuralRole.METHOD_BODY;
import static de.jplag.java_cpg.transformation.StructuralRole.METHOD_DECLARATION;
import static de.jplag.java_cpg.transformation.StructuralRole.METHOD_TYPE;
import static de.jplag.java_cpg.transformation.StructuralRole.PARAMETER_DECLARATION;
import static de.jplag.java_cpg.transformation.StructuralRole.PARAMETER_REFERENCE;
import static de.jplag.java_cpg.transformation.StructuralRole.RETURN_STATEMENT;
import static de.jplag.java_cpg.transformation.StructuralRole.THEN_STATEMENT;
import static de.jplag.java_cpg.transformation.StructuralRole.VOID_TYPE;
import static de.jplag.java_cpg.transformation.matching.edges.Edges.ASSIGN_EXPRESSION__LHS;
import static de.jplag.java_cpg.transformation.matching.edges.Edges.ASSIGN_EXPRESSION__RHS;
import static de.jplag.java_cpg.transformation.matching.edges.Edges.BLOCK__STATEMENTS;
import static de.jplag.java_cpg.transformation.matching.edges.Edges.FUNCTION_TYPE__PARAMETERS;
import static de.jplag.java_cpg.transformation.matching.edges.Edges.FUNCTION_TYPE__RETURN_TYPES;
import static de.jplag.java_cpg.transformation.matching.edges.Edges.IF_STATEMENT__CONDITION;
import static de.jplag.java_cpg.transformation.matching.edges.Edges.IF_STATEMENT__ELSE_STATEMENT;
import static de.jplag.java_cpg.transformation.matching.edges.Edges.IF_STATEMENT__THEN_STATEMENT;
import static de.jplag.java_cpg.transformation.matching.edges.Edges.METHOD_DECLARATION__BODY;
import static de.jplag.java_cpg.transformation.matching.edges.Edges.METHOD_DECLARATION__PARAMETERS;
import static de.jplag.java_cpg.transformation.matching.edges.Edges.METHOD_DECLARATION__RECORD_DECLARATION;
import static de.jplag.java_cpg.transformation.matching.edges.Edges.RECORD_DECLARATION__FIELDS;
import static de.jplag.java_cpg.transformation.matching.edges.Edges.RECORD_DECLARATION__METHODS;
import static de.jplag.java_cpg.transformation.matching.edges.Edges.REFERENCE__REFERS_TO;
import static de.jplag.java_cpg.transformation.matching.edges.Edges.RETURN_STATEMENT__RETURN_VALUES;
import static de.jplag.java_cpg.transformation.matching.edges.Edges.TYPE__TYPE_NAME;
import static de.jplag.java_cpg.transformation.matching.edges.Edges.UNARY_OPERATOR__INPUT;
import static de.jplag.java_cpg.transformation.matching.edges.Edges.UNARY_OPERATOR__OPERATOR_CODE;
import static de.jplag.java_cpg.transformation.matching.edges.Edges.VALUE_DECLARATION__TYPE;
import static de.jplag.java_cpg.transformation.matching.pattern.PatternUtil.nElements;
import static de.jplag.java_cpg.transformation.matching.pattern.PatternUtil.notEmpty;
import static de.jplag.java_cpg.transformation.matching.pattern.PatternUtil.nthElement;

import de.fraunhofer.aisec.cpg.graph.declarations.FieldDeclaration;
import de.fraunhofer.aisec.cpg.graph.declarations.MethodDeclaration;
import de.fraunhofer.aisec.cpg.graph.declarations.ParameterDeclaration;
import de.fraunhofer.aisec.cpg.graph.declarations.RecordDeclaration;
import de.fraunhofer.aisec.cpg.graph.statements.IfStatement;
import de.fraunhofer.aisec.cpg.graph.statements.ReturnStatement;
import de.fraunhofer.aisec.cpg.graph.statements.Statement;
import de.fraunhofer.aisec.cpg.graph.statements.expressions.AssignExpression;
import de.fraunhofer.aisec.cpg.graph.statements.expressions.Block;
import de.fraunhofer.aisec.cpg.graph.statements.expressions.Expression;
import de.fraunhofer.aisec.cpg.graph.statements.expressions.Reference;
import de.fraunhofer.aisec.cpg.graph.statements.expressions.UnaryOperator;
import de.fraunhofer.aisec.cpg.graph.types.FunctionType;
import de.fraunhofer.aisec.cpg.graph.types.IncompleteType;
import de.fraunhofer.aisec.cpg.graph.types.ObjectType;
import de.jplag.java_cpg.transformation.matching.pattern.GraphPattern;
import de.jplag.java_cpg.transformation.matching.pattern.GraphPatternBuilder;
import de.jplag.java_cpg.transformation.matching.pattern.PatternUtil;

/**
 * This class is used to collect sub-patterns that may appear repetitively, or used in tests.
 */
public final class PatternRepository {

    private PatternRepository() {
        /* should not be instantiated */}

    /**
     * Creates a {@link GraphPatternBuilder} for an {@link IfStatement} with an else statement.
     * @return the graph pattern builder
     */
    public static GraphPatternBuilder ifElseWithNegatedCondition() {

        return new GraphPatternBuilder() {
            @Override
            public GraphPattern build() {
                return create(IfStatement.class, IF_STATEMENT,
                        related(IF_STATEMENT__CONDITION, UnaryOperator.class, CONDITION,
                                property(PatternUtil.attributeEquals(UNARY_OPERATOR__OPERATOR_CODE, "!")),
                                related(UNARY_OPERATOR__INPUT, Expression.class, INNER_CONDITION)),
                        related(IF_STATEMENT__THEN_STATEMENT, Statement.class, THEN_STATEMENT),
                        related(IF_STATEMENT__ELSE_STATEMENT, Statement.class, ELSE_STATEMENT));
            }
        };
    }

    /**
     * Creates a {@link GraphPatternBuilder} for a setter method.
     * @return the {@link GraphPatternBuilder}
     */
    public static GraphPatternBuilder setterMethod() {
        return new GraphPatternBuilder() {

            @Override
            public GraphPattern build() {
                return create(MethodDeclaration.class, METHOD_DECLARATION,
                        related(METHOD_DECLARATION__RECORD_DECLARATION, RecordDeclaration.class, CLASS_DECLARATION, //
                                related1ToN(RECORD_DECLARATION__FIELDS, FieldDeclaration.class, FIELD_DECLARATION,  //
                                        related(VALUE_DECLARATION__TYPE, ObjectType.class, FIELD_TYPE) //
                ), //
                                relatedExisting1ToN(RECORD_DECLARATION__METHODS, MethodDeclaration.class, METHOD_DECLARATION)//
                ),//
                        related(VALUE_DECLARATION__TYPE, FunctionType.class, METHOD_TYPE, //
                                property(nElements(FUNCTION_TYPE__PARAMETERS, 1)), //
                                relatedExisting(nthElement(FUNCTION_TYPE__PARAMETERS, 0), ObjectType.class, FIELD_TYPE), //
                                property(nElements(FUNCTION_TYPE__RETURN_TYPES, 1)), //
                                related(nthElement(FUNCTION_TYPE__RETURN_TYPES, 0), IncompleteType.class, VOID_TYPE, //
                                        property(PatternUtil.attributeEquals(TYPE__TYPE_NAME, "void")) //
                ) //
                ), //
                        property(notEmpty(METHOD_DECLARATION__PARAMETERS)), //
                        related(nthElement(METHOD_DECLARATION__PARAMETERS, 0), ParameterDeclaration.class, PARAMETER_DECLARATION), //
                        property(MethodDeclaration::hasBody), //
                        related(METHOD_DECLARATION__BODY, Block.class, METHOD_BODY, //
                                property(nElements(BLOCK__STATEMENTS, 2)), //
                                related(nthElement(BLOCK__STATEMENTS, 0), AssignExpression.class, ASSIGN_EXPRESSION, //
                                        related(ASSIGN_EXPRESSION__LHS, Reference.class, FIELD_REFERENCE), //
                                        related(ASSIGN_EXPRESSION__RHS, Reference.class, PARAMETER_REFERENCE, //
                                                relatedExisting(REFERENCE__REFERS_TO, ParameterDeclaration.class, PARAMETER_DECLARATION) //
                ) //
                ), //
                                related(nthElement(BLOCK__STATEMENTS, 1), ReturnStatement.class, RETURN_STATEMENT,
                                        property(nElements(RETURN_STATEMENT__RETURN_VALUES, 0)) //
                ) //
                ) //
                );
            }
        };

    }

}
