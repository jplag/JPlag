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
        if (node instanceof MethodDeclaration){
            return SemanticDimension.METHOD_DECLARATION;
        }
        else if (node instanceof VariableDeclaration){
            return SemanticDimension.VARIABLE_DECLARATION;
        }
        else if (node instanceof BinaryOperator binaryOperator){
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
        else if (node instanceof ReturnStatement){
            return SemanticDimension.RETURN_STATEMENT;
        }
        else if (node instanceof CaseStatement){
            return SemanticDimension.CASE;
        }
        else if (node instanceof SwitchStatement){
            return SemanticDimension.SWITCH;
        }
        else if (node instanceof LambdaExpression){
            return SemanticDimension.LAMBDA_EXPRESSION;
        }
        else if (node instanceof ConstructExpression){
            return SemanticDimension.CLASS_OR_ARRAY_CREATOR;
        }
        else if (node instanceof IfStatement){
            return SemanticDimension.IF;
        }
        else if (node instanceof AssertStatement){
            return SemanticDimension.ASSERT;
        }
        else if (node instanceof CatchClause){
            return SemanticDimension.CATCH;
        }
        else if (node instanceof TryStatement){
                return SemanticDimension.TRY;
        }
        else if (node instanceof ForEachStatement || node instanceof ForStatement || node instanceof WhileStatement || node instanceof DoStatement){
            return SemanticDimension.LOOP;
        }
        else {
            return null;
        }
    }

}
