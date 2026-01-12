package de.jplag.java_cpg;

import static org.junit.jupiter.api.Assertions.assertIterableEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import de.jplag.SharedTokenType;
import de.jplag.Token;
import de.jplag.TokenType;
import de.jplag.java_cpg.token.cpg.CpgTokenType;
import de.jplag.java_cpg.token.semantic.SemanticCpgTokenType;
import de.jplag.java_cpg.token.semantic.SemanticDimension;
import de.jplag.java_cpg.token.semantic.SemanticVector;

public class SemanticTokenCreationTest extends AbstractJavaCpgLanguageTest {

    @Test
    @DisplayName("Test creation of characteristic tokens for loops.")
    void testBasicCharacteristicTokens() throws Exception {
        List<TokenType> tokens = new ArrayList<>();
        tokens.add(token(CpgTokenType.RECORD_DECL_BEGIN,
                Map.of(SemanticDimension.VARIABLE_DECLARATION, 4, SemanticDimension.METHOD_DECLARATION, 1, SemanticDimension.ARRAY_SELECTOR, 1,
                        SemanticDimension.NUMERICAL_EXPRESSION, 1, SemanticDimension.CONDITIONAL_EXPRESSION, 1,
                        SemanticDimension.ASSIGNMENT_EXPRESSION, 2, SemanticDimension.METHOD_INVOCATION, 1, SemanticDimension.RETURN_STATEMENT, 1,
                        SemanticDimension.LOOP, 1)));

        tokens.add(token(CpgTokenType.FIELD_DECL, Map.of(SemanticDimension.VARIABLE_DECLARATION, 1)));

        tokens.add(token(CpgTokenType.METHOD_DECL_BEGIN,
                Map.of(SemanticDimension.VARIABLE_DECLARATION, 3, SemanticDimension.METHOD_DECLARATION, 1, SemanticDimension.ARRAY_SELECTOR, 1,
                        SemanticDimension.NUMERICAL_EXPRESSION, 1, SemanticDimension.CONDITIONAL_EXPRESSION, 1,
                        SemanticDimension.ASSIGNMENT_EXPRESSION, 2, SemanticDimension.METHOD_INVOCATION, 1, SemanticDimension.RETURN_STATEMENT, 1,
                        SemanticDimension.LOOP, 1)));

        tokens.add(token(CpgTokenType.METHOD_PARAM, Map.of()));

        tokens.add(token(CpgTokenType.METHOD_BODY_BEGIN,
                Map.of(SemanticDimension.VARIABLE_DECLARATION, 3, SemanticDimension.ARRAY_SELECTOR, 1, SemanticDimension.NUMERICAL_EXPRESSION, 1,
                        SemanticDimension.CONDITIONAL_EXPRESSION, 1, SemanticDimension.ASSIGNMENT_EXPRESSION, 2, SemanticDimension.METHOD_INVOCATION,
                        1, SemanticDimension.RETURN_STATEMENT, 1, SemanticDimension.LOOP, 1)));

        tokens.add(token(CpgTokenType.VARIABLE_DECL, Map.of(SemanticDimension.VARIABLE_DECLARATION, 1)));

        tokens.add(token(CpgTokenType.VARIABLE_DECL, Map.of(SemanticDimension.VARIABLE_DECLARATION, 1)));

        tokens.add(token(CpgTokenType.FOR_STATEMENT,
                Map.of(SemanticDimension.VARIABLE_DECLARATION, 1, SemanticDimension.ARRAY_SELECTOR, 1, SemanticDimension.NUMERICAL_EXPRESSION, 1,
                        SemanticDimension.CONDITIONAL_EXPRESSION, 1, SemanticDimension.ASSIGNMENT_EXPRESSION, 2, SemanticDimension.METHOD_INVOCATION,
                        1, SemanticDimension.LOOP, 1)));

        tokens.add(token(CpgTokenType.FOR_BLOCK_BEGIN, Map.of(SemanticDimension.ARRAY_SELECTOR, 1, SemanticDimension.NUMERICAL_EXPRESSION, 1,
                SemanticDimension.ASSIGNMENT_EXPRESSION, 1, SemanticDimension.METHOD_INVOCATION, 1)));

        tokens.add(token(CpgTokenType.ASSIGNMENT, Map.of(SemanticDimension.ARRAY_SELECTOR, 1, SemanticDimension.NUMERICAL_EXPRESSION, 1,
                SemanticDimension.ASSIGNMENT_EXPRESSION, 1, SemanticDimension.METHOD_INVOCATION, 1)));

        tokens.add(token(CpgTokenType.METHOD_CALL, Map.of(SemanticDimension.ARRAY_SELECTOR, 1, SemanticDimension.METHOD_INVOCATION, 1)));

        tokens.add(token(CpgTokenType.FOR_BLOCK_END, Map.of(SemanticDimension.ARRAY_SELECTOR, 1, SemanticDimension.NUMERICAL_EXPRESSION, 1,
                SemanticDimension.ASSIGNMENT_EXPRESSION, 1, SemanticDimension.METHOD_INVOCATION, 1)));

        tokens.add(token(CpgTokenType.RETURN, Map.of(SemanticDimension.RETURN_STATEMENT, 1)));

        tokens.add(token(CpgTokenType.METHOD_BODY_END,
                Map.of(SemanticDimension.VARIABLE_DECLARATION, 3, SemanticDimension.ARRAY_SELECTOR, 1, SemanticDimension.NUMERICAL_EXPRESSION, 1,
                        SemanticDimension.CONDITIONAL_EXPRESSION, 1, SemanticDimension.ASSIGNMENT_EXPRESSION, 2, SemanticDimension.METHOD_INVOCATION,
                        1, SemanticDimension.RETURN_STATEMENT, 1, SemanticDimension.LOOP, 1)));

        tokens.add(token(CpgTokenType.RECORD_DECL_END,
                Map.of(SemanticDimension.VARIABLE_DECLARATION, 4, SemanticDimension.METHOD_DECLARATION, 1, SemanticDimension.ARRAY_SELECTOR, 1,
                        SemanticDimension.NUMERICAL_EXPRESSION, 1, SemanticDimension.CONDITIONAL_EXPRESSION, 1,
                        SemanticDimension.ASSIGNMENT_EXPRESSION, 2, SemanticDimension.METHOD_INVOCATION, 1, SemanticDimension.RETURN_STATEMENT, 1,
                        SemanticDimension.LOOP, 1)));

        tokens.add(token(SharedTokenType.FILE_END,
                Map.of(SemanticDimension.VARIABLE_DECLARATION, 4, SemanticDimension.METHOD_DECLARATION, 1, SemanticDimension.ARRAY_SELECTOR, 1,
                        SemanticDimension.NUMERICAL_EXPRESSION, 1, SemanticDimension.CONDITIONAL_EXPRESSION, 1,
                        SemanticDimension.ASSIGNMENT_EXPRESSION, 2, SemanticDimension.METHOD_INVOCATION, 1, SemanticDimension.RETURN_STATEMENT, 1,
                        SemanticDimension.LOOP, 1)));
        assertIterableEquals(tokens, parseJavaFile("characteristicTokens/SimpleLoop.java", false, false).stream().map(Token::getType).toList());

    }

    @Test
    @DisplayName("Test creation of characteristic tokens with semantic analysis for loops.")
    void testCharacteristicTokensSemantic() throws Exception {
        List<SemanticCpgTokenType> tokens = List.of(

                token(CpgTokenType.RECORD_DECL_BEGIN, Map.of(SemanticDimension.VARIABLE_DECLARATION, 11, SemanticDimension.METHOD_DECLARATION, 1,
                        SemanticDimension.LOOP_CARRIED_DEPENDENCY, 2, SemanticDimension.ARRAY_SELECTOR, 2, SemanticDimension.NUMERICAL_EXPRESSION, 2,
                        SemanticDimension.CONDITIONAL_EXPRESSION, 1, SemanticDimension.ASSIGNMENT_EXPRESSION, 5, SemanticDimension.METHOD_INVOCATION,
                        2, SemanticDimension.RETURN_STATEMENT, 1, SemanticDimension.LOOP, 1)),

                token(CpgTokenType.FIELD_DECL, Map.of(SemanticDimension.VARIABLE_DECLARATION, 1)),

                token(CpgTokenType.METHOD_DECL_BEGIN, Map.of(SemanticDimension.VARIABLE_DECLARATION, 10, SemanticDimension.METHOD_DECLARATION, 1,
                        SemanticDimension.LOOP_CARRIED_DEPENDENCY, 2, SemanticDimension.ARRAY_SELECTOR, 2, SemanticDimension.NUMERICAL_EXPRESSION, 2,
                        SemanticDimension.CONDITIONAL_EXPRESSION, 1, SemanticDimension.ASSIGNMENT_EXPRESSION, 5, SemanticDimension.METHOD_INVOCATION,
                        2, SemanticDimension.RETURN_STATEMENT, 1, SemanticDimension.LOOP, 1)),

                token(CpgTokenType.METHOD_PARAM, Map.of()),

                token(CpgTokenType.METHOD_BODY_BEGIN,
                        Map.of(SemanticDimension.VARIABLE_DECLARATION, 10, SemanticDimension.LOOP_CARRIED_DEPENDENCY, 2,
                                SemanticDimension.ARRAY_SELECTOR, 2, SemanticDimension.NUMERICAL_EXPRESSION, 2,
                                SemanticDimension.CONDITIONAL_EXPRESSION, 1, SemanticDimension.ASSIGNMENT_EXPRESSION, 5,
                                SemanticDimension.METHOD_INVOCATION, 2, SemanticDimension.RETURN_STATEMENT, 1, SemanticDimension.LOOP, 1)),

                token(CpgTokenType.VARIABLE_DECL, Map.of(SemanticDimension.VARIABLE_DECLARATION, 1)),

                token(CpgTokenType.VARIABLE_DECL, Map.of(SemanticDimension.VARIABLE_DECLARATION, 1)),

                token(CpgTokenType.FOR_STATEMENT, Map.of(SemanticDimension.VARIABLE_DECLARATION, 6, SemanticDimension.LOOP_CARRIED_DEPENDENCY, 2,
                        SemanticDimension.ARRAY_SELECTOR, 1, SemanticDimension.NUMERICAL_EXPRESSION, 1, SemanticDimension.CONDITIONAL_EXPRESSION, 1,
                        SemanticDimension.ASSIGNMENT_EXPRESSION, 3, SemanticDimension.METHOD_INVOCATION, 1, SemanticDimension.LOOP, 1)),

                token(CpgTokenType.FOR_BLOCK_BEGIN,
                        Map.of(SemanticDimension.VARIABLE_DECLARATION, 2, SemanticDimension.ARRAY_SELECTOR, 1, SemanticDimension.NUMERICAL_EXPRESSION,
                                1, SemanticDimension.ASSIGNMENT_EXPRESSION, 2, SemanticDimension.METHOD_INVOCATION, 1)),

                token(CpgTokenType.ASSIGNMENT,
                        Map.of(SemanticDimension.VARIABLE_DECLARATION, 2, SemanticDimension.ARRAY_SELECTOR, 1, SemanticDimension.NUMERICAL_EXPRESSION,
                                1, SemanticDimension.ASSIGNMENT_EXPRESSION, 2, SemanticDimension.METHOD_INVOCATION, 1)),

                token(CpgTokenType.METHOD_CALL,
                        Map.of(SemanticDimension.VARIABLE_DECLARATION, 2, SemanticDimension.ARRAY_SELECTOR, 1,
                                SemanticDimension.ASSIGNMENT_EXPRESSION, 1, SemanticDimension.METHOD_INVOCATION, 1)),

                token(CpgTokenType.FOR_BLOCK_END,
                        Map.of(SemanticDimension.VARIABLE_DECLARATION, 2, SemanticDimension.ARRAY_SELECTOR, 1, SemanticDimension.NUMERICAL_EXPRESSION,
                                1, SemanticDimension.ASSIGNMENT_EXPRESSION, 2, SemanticDimension.METHOD_INVOCATION, 1)),

                token(CpgTokenType.RETURN,
                        Map.of(SemanticDimension.VARIABLE_DECLARATION, 2, SemanticDimension.ARRAY_SELECTOR, 1, SemanticDimension.NUMERICAL_EXPRESSION,
                                1, SemanticDimension.ASSIGNMENT_EXPRESSION, 2, SemanticDimension.METHOD_INVOCATION, 1,
                                SemanticDimension.RETURN_STATEMENT, 1)),

                token(CpgTokenType.METHOD_BODY_END,
                        Map.of(SemanticDimension.VARIABLE_DECLARATION, 10, SemanticDimension.LOOP_CARRIED_DEPENDENCY, 2,
                                SemanticDimension.ARRAY_SELECTOR, 2, SemanticDimension.NUMERICAL_EXPRESSION, 2,
                                SemanticDimension.CONDITIONAL_EXPRESSION, 1, SemanticDimension.ASSIGNMENT_EXPRESSION, 5,
                                SemanticDimension.METHOD_INVOCATION, 2, SemanticDimension.RETURN_STATEMENT, 1, SemanticDimension.LOOP, 1)),

                token(CpgTokenType.RECORD_DECL_END, Map.of(SemanticDimension.VARIABLE_DECLARATION, 11, SemanticDimension.METHOD_DECLARATION, 1,
                        SemanticDimension.LOOP_CARRIED_DEPENDENCY, 2, SemanticDimension.ARRAY_SELECTOR, 2, SemanticDimension.NUMERICAL_EXPRESSION, 2,
                        SemanticDimension.CONDITIONAL_EXPRESSION, 1, SemanticDimension.ASSIGNMENT_EXPRESSION, 5, SemanticDimension.METHOD_INVOCATION,
                        2, SemanticDimension.RETURN_STATEMENT, 1, SemanticDimension.LOOP, 1)),

                token(SharedTokenType.FILE_END, Map.of(SemanticDimension.VARIABLE_DECLARATION, 11, SemanticDimension.METHOD_DECLARATION, 1,
                        SemanticDimension.LOOP_CARRIED_DEPENDENCY, 2, SemanticDimension.ARRAY_SELECTOR, 2, SemanticDimension.NUMERICAL_EXPRESSION, 2,
                        SemanticDimension.CONDITIONAL_EXPRESSION, 1, SemanticDimension.ASSIGNMENT_EXPRESSION, 5, SemanticDimension.METHOD_INVOCATION,
                        2, SemanticDimension.RETURN_STATEMENT, 1, SemanticDimension.LOOP, 1)));
        assertIterableEquals(tokens, parseJavaFile("characteristicTokens/SimpleLoop.java", false, true).stream().map(Token::getType).toList());
    }

    private SemanticVector vector(Map<SemanticDimension, Integer> values) {
        SemanticVector vector = new SemanticVector();
        for (Map.Entry<SemanticDimension, Integer> entry : values.entrySet()) {
            vector.getValue()[entry.getKey().ordinal()] = entry.getValue();
        }
        return vector;
    }

    private SemanticCpgTokenType token(TokenType type, Map<SemanticDimension, Integer> vectorValues) {
        return new SemanticCpgTokenType(type, vector(vectorValues));
    }
}
