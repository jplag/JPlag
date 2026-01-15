package de.jplag.java_cpg.token.characteristic;

import de.fraunhofer.aisec.cpg.graph.Node;
import de.fraunhofer.aisec.cpg.graph.declarations.MethodDeclaration;
import de.fraunhofer.aisec.cpg.graph.declarations.VariableDeclaration;
import de.fraunhofer.aisec.cpg.graph.statements.*;
import de.fraunhofer.aisec.cpg.graph.statements.expressions.*;

/**
 * Maps CPG nodes to characteristic vector dimensions.
 */
public final class CharacteristicVectorDimensionsMapper {
    /**
     * Private constructor to prevent instantiation.
     */
    private CharacteristicVectorDimensionsMapper() {
        // private constructor to prevent instantiation
    }

    /**
     * Maps a CPG node to a characteristic vector dimension depending mainly on its type.
     * @param node the CPG node
     * @return the characteristic vector dimension, or null if the node type is not mapped
     */
    public static CharacteristicVectorDimension mapNodeType(Node node) {
        if (node instanceof MethodDeclaration) {
            return CharacteristicVectorDimension.METHOD_DECLARATION;
        } else if (node instanceof VariableDeclaration) {
            if(node.getLocation() == null){
                //skip auto generated declarations
                return null;
            }
            return CharacteristicVectorDimension.VARIABLE_DECLARATION;
        } else if (node instanceof BinaryOperator binaryOperator) {
            switch (binaryOperator.getOperatorCode()) {
                case "&&", "||" -> {
                    return CharacteristicVectorDimension.LOGICAL_EXPRESSION;
                }
                case "+", "-", "*", "/", "%", "++", "--", "<<", ">>", ">>>", "&", "|", "^" -> {
                    return CharacteristicVectorDimension.NUMERICAL_EXPRESSION;
                }
                case "==", "!=", "<", "<=", ">", ">=" -> {
                    return CharacteristicVectorDimension.CONDITIONAL_EXPRESSION;
                }
                case null, default -> {
                    return null;
                }
            }
        } else if (node instanceof ReturnStatement) {
            if(node.getLocation() == null){
                //skip auto generated returns
                return null;
            }
            return CharacteristicVectorDimension.RETURN_STATEMENT;
        } else if (node instanceof CaseStatement) {
            return CharacteristicVectorDimension.CASE;
        } else if (node instanceof SwitchStatement) {
            return CharacteristicVectorDimension.SWITCH;
        } else if (node instanceof LambdaExpression) {
            return CharacteristicVectorDimension.LAMBDA_EXPRESSION;
        } else if (node instanceof NewArrayExpression || node instanceof NewExpression) {
            return CharacteristicVectorDimension.CLASS_OR_ARRAY_CREATOR;
        } else if (node instanceof IfStatement) {
            return CharacteristicVectorDimension.IF;
        } else if (node instanceof AssertStatement) {
            return CharacteristicVectorDimension.ASSERT;
        } else if (node instanceof CatchClause) {
            return CharacteristicVectorDimension.CATCH;
        } else if (node instanceof UnaryOperator unaryOperator) {
            if (unaryOperator.getOperatorCode() == null) {
                return null;
            } else if (unaryOperator.getOperatorCode().equals("throw")) {
                return CharacteristicVectorDimension.THROW;
            } else if (unaryOperator.getOperatorCode().equals("++") || unaryOperator.getOperatorCode().equals("--")) {
                return CharacteristicVectorDimension.ASSIGNMENT_EXPRESSION;
            } else {
                return null;
            }
        } else if (node instanceof TryStatement) {
            return CharacteristicVectorDimension.TRY;
        } else if (node instanceof AssignExpression) {
            return CharacteristicVectorDimension.ASSIGNMENT_EXPRESSION;
        } else if (node instanceof SubscriptExpression) {
            return CharacteristicVectorDimension.ARRAY_SELECTOR;
        } else if (node instanceof ConstructExpression) {
            return CharacteristicVectorDimension.CONSTRUCTOR_INVOCATION;
        } else if (node instanceof CallExpression) {
            return CharacteristicVectorDimension.METHOD_INVOCATION;
        } else if (node instanceof ForEachStatement || node instanceof ForStatement || node instanceof WhileStatement
                || node instanceof DoStatement) {
            return CharacteristicVectorDimension.LOOP;
        } else {
            return null;
        }
    }

}
