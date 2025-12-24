package de.jplag.java_cpg.token.semantic;

import de.fraunhofer.aisec.cpg.graph.Node;
import de.fraunhofer.aisec.cpg.graph.declarations.MethodDeclaration;
import de.fraunhofer.aisec.cpg.graph.declarations.VariableDeclaration;
import de.fraunhofer.aisec.cpg.graph.statements.*;
import de.fraunhofer.aisec.cpg.graph.statements.expressions.*;

public final class SemanticDimensionsMapper {
    public static SemanticDimension mapNodeType(Node node) {
        if (node instanceof MethodDeclaration) {
            return SemanticDimension.METHOD_DECLARATION;
        } else if (node instanceof VariableDeclaration) {
            return SemanticDimension.VARIABLE_DECLARATION;
        } else if (node instanceof BinaryOperator binaryOperator) {
            switch (binaryOperator.getOperatorCode()) {
                case "&&", "||" -> {
                    return SemanticDimension.LOGICAL_EXPRESSION;
                }
                case "+", "-", "*", "/", "%", "++", "--", "<<", ">>", ">>>", "&", "|", "^" -> {
                    return SemanticDimension.NUMERICAL_EXPRESSION;
                }
                case "==", "!=", "<", "<=", ">", ">=" -> {
                    return SemanticDimension.CONDITIONAL_EXPRESSION;
                }
                case null, default -> {
                    return null;
                }
            }
        } else if (node instanceof ReturnStatement) {
            // skip auto generated return statements
            if (node.getLocation() == null) {
                return null;
            }
            return SemanticDimension.RETURN_STATEMENT;
        } else if (node instanceof CaseStatement) {
            return SemanticDimension.CASE;
        } else if (node instanceof SwitchStatement) {
            return SemanticDimension.SWITCH;
        } else if (node instanceof LambdaExpression) {
            return SemanticDimension.LAMBDA_EXPRESSION;
        } else if (node instanceof NewArrayExpression || node instanceof NewExpression) {
            return SemanticDimension.CLASS_OR_ARRAY_CREATOR;
        } else if (node instanceof IfStatement) {
            return SemanticDimension.IF;
        } else if (node instanceof AssertStatement) {
            return SemanticDimension.ASSERT;
        } else if (node instanceof CatchClause) {
            return SemanticDimension.CATCH;
        } else if (node instanceof UnaryOperator unaryOperator) {
            if ((unaryOperator.getOperatorCode() != null && unaryOperator.getOperatorCode().equals("throw"))) {
                return SemanticDimension.THROW;
            } else {
                return null;
            }
        } else if (node instanceof TryStatement) {
            return SemanticDimension.TRY;
        } else if (node instanceof AssignExpression) {
            return SemanticDimension.ASSIGNMENT_EXPRESSION;
        } else if (node instanceof SubscriptExpression) {
            return SemanticDimension.ARRAY_SELECTOR;
        } else if (node instanceof ConstructExpression) {
            return SemanticDimension.CONSTRUCTOR_INVOCATION;
        } else if (node instanceof CallExpression) {
            return SemanticDimension.METHOD_INVOCATION;
        } else if (node instanceof ForEachStatement || node instanceof ForStatement || node instanceof WhileStatement
                || node instanceof DoStatement) {
            return SemanticDimension.LOOP;
        } else {
            return null;
        }
    }

}
