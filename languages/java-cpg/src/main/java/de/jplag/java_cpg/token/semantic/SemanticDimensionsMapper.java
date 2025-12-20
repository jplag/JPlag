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
            case MethodDeclaration _ -> {
                return SemanticDimension.METHOD_DECLARATION;
            }
            case VariableDeclaration _ -> {
                return SemanticDimension.VARIABLE_DECLARATION;
            }
            case BinaryOperator _ -> {
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
            case ReturnStatement _ -> {
                return SemanticDimension.RETURN_STATEMENT;
            }
            case CaseStatement _ -> {
                return SemanticDimension.CASE;
            }
            case SwitchStatement _ -> {
                return SemanticDimension.SWITCH;
            }
            case LambdaExpression _ -> {
                return SemanticDimension.LAMBDA_EXPRESSION;
            }
            case ConstructExpression _ -> {
                return SemanticDimension.CLASS_OR_ARRAY_CREATOR;
            }
            case IfStatement _ -> {
                return SemanticDimension.IF;
            }
            case AssertStatement _ -> {
                return SemanticDimension.ASSERT;
            }
            case CatchClause _ -> {
                return SemanticDimension.CATCH;
            }
            case TryStatement _ -> {
                return SemanticDimension.TRY;
            }
            case ForEachStatement _, ForStatement _, WhileStatement _, DoStatement _ -> {
                return SemanticDimension.LOOP;
            }
            default -> {
                return null;
            }
        }

    }

}
