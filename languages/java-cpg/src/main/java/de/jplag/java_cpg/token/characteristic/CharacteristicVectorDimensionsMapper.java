package de.jplag.java_cpg.token.characteristic;

import de.fraunhofer.aisec.cpg.graph.Node;
import de.fraunhofer.aisec.cpg.graph.declarations.MethodDeclaration;
import de.fraunhofer.aisec.cpg.graph.declarations.VariableDeclaration;
import de.fraunhofer.aisec.cpg.graph.statements.AssertStatement;
import de.fraunhofer.aisec.cpg.graph.statements.CaseStatement;
import de.fraunhofer.aisec.cpg.graph.statements.CatchClause;
import de.fraunhofer.aisec.cpg.graph.statements.DoStatement;
import de.fraunhofer.aisec.cpg.graph.statements.ForEachStatement;
import de.fraunhofer.aisec.cpg.graph.statements.ForStatement;
import de.fraunhofer.aisec.cpg.graph.statements.IfStatement;
import de.fraunhofer.aisec.cpg.graph.statements.ReturnStatement;
import de.fraunhofer.aisec.cpg.graph.statements.SwitchStatement;
import de.fraunhofer.aisec.cpg.graph.statements.TryStatement;
import de.fraunhofer.aisec.cpg.graph.statements.WhileStatement;
import de.fraunhofer.aisec.cpg.graph.statements.expressions.AssignExpression;
import de.fraunhofer.aisec.cpg.graph.statements.expressions.BinaryOperator;
import de.fraunhofer.aisec.cpg.graph.statements.expressions.CallExpression;
import de.fraunhofer.aisec.cpg.graph.statements.expressions.ConstructExpression;
import de.fraunhofer.aisec.cpg.graph.statements.expressions.LambdaExpression;
import de.fraunhofer.aisec.cpg.graph.statements.expressions.NewArrayExpression;
import de.fraunhofer.aisec.cpg.graph.statements.expressions.NewExpression;
import de.fraunhofer.aisec.cpg.graph.statements.expressions.SubscriptExpression;
import de.fraunhofer.aisec.cpg.graph.statements.expressions.UnaryOperator;

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
        return switch (node) {
            case MethodDeclaration _ -> CharacteristicVectorDimension.METHOD_DECLARATION;
            case VariableDeclaration variableDeclaration -> {
                if (variableDeclaration.getLocation() == null)
                    yield null;
                yield CharacteristicVectorDimension.VARIABLE_DECLARATION;
            }
            case BinaryOperator binaryOperator -> mapBinaryOperator(binaryOperator);
            case ReturnStatement returnStatement -> {
                if (returnStatement.getLocation() == null)
                    yield null;
                yield CharacteristicVectorDimension.RETURN_STATEMENT;
            }
            case CaseStatement _ -> CharacteristicVectorDimension.CASE;
            case SwitchStatement _ -> CharacteristicVectorDimension.SWITCH;
            case LambdaExpression _ -> CharacteristicVectorDimension.LAMBDA_EXPRESSION;
            case NewArrayExpression _ -> CharacteristicVectorDimension.CLASS_OR_ARRAY_CREATOR;
            case NewExpression _ -> CharacteristicVectorDimension.CLASS_OR_ARRAY_CREATOR;
            case IfStatement _ -> CharacteristicVectorDimension.IF;
            case AssertStatement _ -> CharacteristicVectorDimension.ASSERT;
            case CatchClause _ -> CharacteristicVectorDimension.CATCH;
            case UnaryOperator unaryOperator -> mapUnaryOperator(unaryOperator);
            case TryStatement _ -> CharacteristicVectorDimension.TRY;
            case AssignExpression _ -> CharacteristicVectorDimension.ASSIGNMENT_EXPRESSION;
            case SubscriptExpression _ -> CharacteristicVectorDimension.ARRAY_SELECTOR;
            case ConstructExpression _ -> CharacteristicVectorDimension.CONSTRUCTOR_INVOCATION;
            case CallExpression _ -> CharacteristicVectorDimension.METHOD_INVOCATION;
            case ForEachStatement _,ForStatement _,WhileStatement _,DoStatement _ -> CharacteristicVectorDimension.LOOP;
            default -> null;
        };
    }

    private static CharacteristicVectorDimension mapBinaryOperator(BinaryOperator binaryOperator) {
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
    }

    private static CharacteristicVectorDimension mapUnaryOperator(UnaryOperator unaryOperator) {
        if (unaryOperator.getOperatorCode() == null) {
            return null;
        } else if (unaryOperator.getOperatorCode().equals("throw")) {
            return CharacteristicVectorDimension.THROW;
        } else if (unaryOperator.getOperatorCode().equals("++") || unaryOperator.getOperatorCode().equals("--")) {
            return CharacteristicVectorDimension.ASSIGNMENT_EXPRESSION;
        } else {
            return null;
        }
    }
}
