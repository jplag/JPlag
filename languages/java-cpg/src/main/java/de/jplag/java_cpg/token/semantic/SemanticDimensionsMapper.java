package de.jplag.java_cpg.token.semantic;

import de.fraunhofer.aisec.cpg.graph.Node;
import de.fraunhofer.aisec.cpg.graph.declarations.MethodDeclaration;
import de.fraunhofer.aisec.cpg.graph.declarations.VariableDeclaration;
import de.fraunhofer.aisec.cpg.graph.statements.*;
import de.fraunhofer.aisec.cpg.graph.statements.expressions.BinaryOperator;
import de.fraunhofer.aisec.cpg.graph.statements.expressions.ConstructExpression;
import de.fraunhofer.aisec.cpg.graph.statements.expressions.LambdaExpression;

public final class SemanticDimensionsMapper {
    public static SemanticDimension mapNodeType(Node node) {
        switch (node) {
            case (node instanceof ReturnStatement) {
                return SemanticDimension.METHOD_DECLARATION;
            }
            case (node instanceof VariableDeclaration) {
                return SemanticDimension.VARIABLE_DECLARATION;
            }
            case (node instanceof BinaryOperator) {
                BinaryOperator binaryOperator = (BinaryOperator) node;
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
                    case null -> {
                        throw new IllegalStateException("BinaryOperator with null operator code");
                    }
                    default -> throw new IllegalStateException("Unexpected value: " + binaryOperator.getOperatorCode());
                }
            }
            case (node instanceof ReturnStatement) {
                return SemanticDimension.RETURN_STATEMENT;
            }
            case (node instanceof CaseStatement) {
                return SemanticDimension.CASE;
            }
            case (node instanceof SwitchStatement){
                return SemanticDimension.SWITCH;
            }
            case (node instanceof LambdaExpression) {
                return SemanticDimension.LAMBDA_EXPRESSION;
            }
            case (node instanceof ConstructExpression) {
                return SemanticDimension.CLASS_OR_ARRAY_CREATOR;
            }
            case (node instanceof IfStatement) {
                return SemanticDimension.IF;
            }
            case (node instanceof AssertStatement) {
                return SemanticDimension.ASSERT;
            }
            case (node instanceof CatchClause) {
                return SemanticDimension.CATCH;
            }
            case (node instanceof TryStatement) {
                return SemanticDimension.TRY;
            }
            case (node instanceof ForEachStatement || node instanceof ForStatement || node instanceof WhileStatement || node instanceof DoStatement) {
                return SemanticDimension.LOOP;
            }
            default -> {
                return null;
            }
        }

    }

}
