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
import de.jplag.java_cpg.token.characteristic.CharacteristicCpgTokenType;
import de.jplag.java_cpg.token.characteristic.CharacteristicVector;
import de.jplag.java_cpg.token.characteristic.CharacteristicVectorDimension;
import de.jplag.java_cpg.token.cpg.CpgTokenType;

/**
 * Tests for the creation of characteristic tokens in the Java CPG. These tests check whether the expected
 * characteristic tokens are created for a simple loop example, both with and without semantic analysis.
 */
public class SemanticTokenCreationTest extends AbstractJavaCpgLanguageTest {

    @Test
    @DisplayName("Test creation of characteristic tokens for loops.")
    void testBasicCharacteristicTokens() throws Exception {
        List<TokenType> tokens = new ArrayList<>();
        tokens.add(token(CpgTokenType.RECORD_DECL_BEGIN,
                Map.of(CharacteristicVectorDimension.VARIABLE_DECLARATION, 3, CharacteristicVectorDimension.METHOD_DECLARATION, 1,
                        CharacteristicVectorDimension.ARRAY_SELECTOR, 1, CharacteristicVectorDimension.NUMERICAL_EXPRESSION, 1,
                        CharacteristicVectorDimension.CONDITIONAL_EXPRESSION, 1, CharacteristicVectorDimension.ASSIGNMENT_EXPRESSION, 2,
                        CharacteristicVectorDimension.METHOD_INVOCATION, 1, CharacteristicVectorDimension.RETURN_STATEMENT, 1,
                        CharacteristicVectorDimension.LOOP, 1)));

        tokens.add(token(CpgTokenType.METHOD_DECL_BEGIN,
                Map.of(CharacteristicVectorDimension.VARIABLE_DECLARATION, 3, CharacteristicVectorDimension.METHOD_DECLARATION, 1,
                        CharacteristicVectorDimension.ARRAY_SELECTOR, 1, CharacteristicVectorDimension.NUMERICAL_EXPRESSION, 1,
                        CharacteristicVectorDimension.CONDITIONAL_EXPRESSION, 1, CharacteristicVectorDimension.ASSIGNMENT_EXPRESSION, 2,
                        CharacteristicVectorDimension.METHOD_INVOCATION, 1, CharacteristicVectorDimension.RETURN_STATEMENT, 1,
                        CharacteristicVectorDimension.LOOP, 1)));

        tokens.add(token(CpgTokenType.METHOD_PARAM, Map.of()));

        tokens.add(token(CpgTokenType.METHOD_BODY_BEGIN,
                Map.of(CharacteristicVectorDimension.VARIABLE_DECLARATION, 3, CharacteristicVectorDimension.ARRAY_SELECTOR, 1,
                        CharacteristicVectorDimension.NUMERICAL_EXPRESSION, 1, CharacteristicVectorDimension.CONDITIONAL_EXPRESSION, 1,
                        CharacteristicVectorDimension.ASSIGNMENT_EXPRESSION, 2, CharacteristicVectorDimension.METHOD_INVOCATION, 1,
                        CharacteristicVectorDimension.RETURN_STATEMENT, 1, CharacteristicVectorDimension.LOOP, 1)));

        tokens.add(token(CpgTokenType.VARIABLE_DECL, Map.of(CharacteristicVectorDimension.VARIABLE_DECLARATION, 1)));

        tokens.add(token(CpgTokenType.VARIABLE_DECL, Map.of(CharacteristicVectorDimension.VARIABLE_DECLARATION, 1)));

        tokens.add(token(CpgTokenType.FOR_STATEMENT,
                Map.of(CharacteristicVectorDimension.VARIABLE_DECLARATION, 1, CharacteristicVectorDimension.ARRAY_SELECTOR, 1,
                        CharacteristicVectorDimension.NUMERICAL_EXPRESSION, 1, CharacteristicVectorDimension.CONDITIONAL_EXPRESSION, 1,
                        CharacteristicVectorDimension.ASSIGNMENT_EXPRESSION, 2, CharacteristicVectorDimension.METHOD_INVOCATION, 1,
                        CharacteristicVectorDimension.LOOP, 1)));

        tokens.add(token(CpgTokenType.FOR_BLOCK_BEGIN,
                Map.of(CharacteristicVectorDimension.ARRAY_SELECTOR, 1, CharacteristicVectorDimension.NUMERICAL_EXPRESSION, 1,
                        CharacteristicVectorDimension.ASSIGNMENT_EXPRESSION, 1, CharacteristicVectorDimension.METHOD_INVOCATION, 1)));

        tokens.add(token(CpgTokenType.ASSIGNMENT,
                Map.of(CharacteristicVectorDimension.ARRAY_SELECTOR, 1, CharacteristicVectorDimension.NUMERICAL_EXPRESSION, 1,
                        CharacteristicVectorDimension.ASSIGNMENT_EXPRESSION, 1, CharacteristicVectorDimension.METHOD_INVOCATION, 1)));

        tokens.add(token(CpgTokenType.METHOD_CALL,
                Map.of(CharacteristicVectorDimension.ARRAY_SELECTOR, 1, CharacteristicVectorDimension.METHOD_INVOCATION, 1)));

        tokens.add(token(CpgTokenType.FOR_BLOCK_END,
                Map.of(CharacteristicVectorDimension.ARRAY_SELECTOR, 1, CharacteristicVectorDimension.NUMERICAL_EXPRESSION, 1,
                        CharacteristicVectorDimension.ASSIGNMENT_EXPRESSION, 1, CharacteristicVectorDimension.METHOD_INVOCATION, 1)));

        tokens.add(token(CpgTokenType.RETURN, Map.of(CharacteristicVectorDimension.RETURN_STATEMENT, 1)));

        tokens.add(token(CpgTokenType.METHOD_BODY_END,
                Map.of(CharacteristicVectorDimension.VARIABLE_DECLARATION, 3, CharacteristicVectorDimension.ARRAY_SELECTOR, 1,
                        CharacteristicVectorDimension.NUMERICAL_EXPRESSION, 1, CharacteristicVectorDimension.CONDITIONAL_EXPRESSION, 1,
                        CharacteristicVectorDimension.ASSIGNMENT_EXPRESSION, 2, CharacteristicVectorDimension.METHOD_INVOCATION, 1,
                        CharacteristicVectorDimension.RETURN_STATEMENT, 1, CharacteristicVectorDimension.LOOP, 1)));

        tokens.add(token(CpgTokenType.RECORD_DECL_END,
                Map.of(CharacteristicVectorDimension.VARIABLE_DECLARATION, 3, CharacteristicVectorDimension.METHOD_DECLARATION, 1,
                        CharacteristicVectorDimension.ARRAY_SELECTOR, 1, CharacteristicVectorDimension.NUMERICAL_EXPRESSION, 1,
                        CharacteristicVectorDimension.CONDITIONAL_EXPRESSION, 1, CharacteristicVectorDimension.ASSIGNMENT_EXPRESSION, 2,
                        CharacteristicVectorDimension.METHOD_INVOCATION, 1, CharacteristicVectorDimension.RETURN_STATEMENT, 1,
                        CharacteristicVectorDimension.LOOP, 1)));

        tokens.add(token(SharedTokenType.FILE_END,
                Map.of(CharacteristicVectorDimension.VARIABLE_DECLARATION, 3, CharacteristicVectorDimension.METHOD_DECLARATION, 1,
                        CharacteristicVectorDimension.ARRAY_SELECTOR, 1, CharacteristicVectorDimension.NUMERICAL_EXPRESSION, 1,
                        CharacteristicVectorDimension.CONDITIONAL_EXPRESSION, 1, CharacteristicVectorDimension.ASSIGNMENT_EXPRESSION, 2,
                        CharacteristicVectorDimension.METHOD_INVOCATION, 1, CharacteristicVectorDimension.RETURN_STATEMENT, 1,
                        CharacteristicVectorDimension.LOOP, 1)));
        assertIterableEquals(tokens, parseJavaFile("characteristicTokens/SimpleLoop.java", false, false).stream().map(Token::getType).toList());

    }

    @Test
    @DisplayName("Test creation of characteristic tokens with semantic analysis for loops.")
    void testCharacteristicTokensSemantic() throws Exception {
        List<CharacteristicCpgTokenType> tokens = List.of(

                token(CpgTokenType.RECORD_DECL_BEGIN,
                        Map.of(CharacteristicVectorDimension.VARIABLE_DECLARATION, 8, CharacteristicVectorDimension.METHOD_DECLARATION, 1,
                                CharacteristicVectorDimension.LOOP_CARRIED_DEPENDENCY, 2, CharacteristicVectorDimension.ARRAY_SELECTOR, 2,
                                CharacteristicVectorDimension.NUMERICAL_EXPRESSION, 2, CharacteristicVectorDimension.CONDITIONAL_EXPRESSION, 1,
                                CharacteristicVectorDimension.ASSIGNMENT_EXPRESSION, 5, CharacteristicVectorDimension.METHOD_INVOCATION, 2,
                                CharacteristicVectorDimension.RETURN_STATEMENT, 1, CharacteristicVectorDimension.LOOP, 1)),

                token(CpgTokenType.METHOD_DECL_BEGIN,
                        Map.of(CharacteristicVectorDimension.VARIABLE_DECLARATION, 8, CharacteristicVectorDimension.METHOD_DECLARATION, 1,
                                CharacteristicVectorDimension.LOOP_CARRIED_DEPENDENCY, 2, CharacteristicVectorDimension.ARRAY_SELECTOR, 2,
                                CharacteristicVectorDimension.NUMERICAL_EXPRESSION, 2, CharacteristicVectorDimension.CONDITIONAL_EXPRESSION, 1,
                                CharacteristicVectorDimension.ASSIGNMENT_EXPRESSION, 5, CharacteristicVectorDimension.METHOD_INVOCATION, 2,
                                CharacteristicVectorDimension.RETURN_STATEMENT, 1, CharacteristicVectorDimension.LOOP, 1)),

                token(CpgTokenType.METHOD_PARAM, Map.of()),

                token(CpgTokenType.METHOD_BODY_BEGIN,
                        Map.of(CharacteristicVectorDimension.VARIABLE_DECLARATION, 8, CharacteristicVectorDimension.LOOP_CARRIED_DEPENDENCY, 2,
                                CharacteristicVectorDimension.ARRAY_SELECTOR, 2, CharacteristicVectorDimension.NUMERICAL_EXPRESSION, 2,
                                CharacteristicVectorDimension.CONDITIONAL_EXPRESSION, 1, CharacteristicVectorDimension.ASSIGNMENT_EXPRESSION, 5,
                                CharacteristicVectorDimension.METHOD_INVOCATION, 2, CharacteristicVectorDimension.RETURN_STATEMENT, 1,
                                CharacteristicVectorDimension.LOOP, 1)),

                token(CpgTokenType.VARIABLE_DECL, Map.of(CharacteristicVectorDimension.VARIABLE_DECLARATION, 1)),

                token(CpgTokenType.VARIABLE_DECL, Map.of(CharacteristicVectorDimension.VARIABLE_DECLARATION, 1)),

                token(CpgTokenType.FOR_STATEMENT,
                        Map.of(CharacteristicVectorDimension.VARIABLE_DECLARATION, 5, CharacteristicVectorDimension.LOOP_CARRIED_DEPENDENCY, 2,
                                CharacteristicVectorDimension.ARRAY_SELECTOR, 1, CharacteristicVectorDimension.NUMERICAL_EXPRESSION, 1,
                                CharacteristicVectorDimension.CONDITIONAL_EXPRESSION, 1, CharacteristicVectorDimension.ASSIGNMENT_EXPRESSION, 3,
                                CharacteristicVectorDimension.METHOD_INVOCATION, 1, CharacteristicVectorDimension.LOOP, 1)),

                token(CpgTokenType.FOR_BLOCK_BEGIN,
                        Map.of(CharacteristicVectorDimension.VARIABLE_DECLARATION, 1, CharacteristicVectorDimension.ARRAY_SELECTOR, 1,
                                CharacteristicVectorDimension.NUMERICAL_EXPRESSION, 1, CharacteristicVectorDimension.ASSIGNMENT_EXPRESSION, 2,
                                CharacteristicVectorDimension.METHOD_INVOCATION, 1)),

                token(CpgTokenType.ASSIGNMENT,
                        Map.of(CharacteristicVectorDimension.VARIABLE_DECLARATION, 1, CharacteristicVectorDimension.ARRAY_SELECTOR, 1,
                                CharacteristicVectorDimension.NUMERICAL_EXPRESSION, 1, CharacteristicVectorDimension.ASSIGNMENT_EXPRESSION, 2,
                                CharacteristicVectorDimension.METHOD_INVOCATION, 1)),

                token(CpgTokenType.METHOD_CALL,
                        Map.of(CharacteristicVectorDimension.VARIABLE_DECLARATION, 1, CharacteristicVectorDimension.ARRAY_SELECTOR, 1,
                                CharacteristicVectorDimension.ASSIGNMENT_EXPRESSION, 1, CharacteristicVectorDimension.METHOD_INVOCATION, 1)),

                token(CpgTokenType.FOR_BLOCK_END,
                        Map.of(CharacteristicVectorDimension.VARIABLE_DECLARATION, 1, CharacteristicVectorDimension.ARRAY_SELECTOR, 1,
                                CharacteristicVectorDimension.NUMERICAL_EXPRESSION, 1, CharacteristicVectorDimension.ASSIGNMENT_EXPRESSION, 2,
                                CharacteristicVectorDimension.METHOD_INVOCATION, 1)),

                token(CpgTokenType.RETURN,
                        Map.of(CharacteristicVectorDimension.VARIABLE_DECLARATION, 1, CharacteristicVectorDimension.ARRAY_SELECTOR, 1,
                                CharacteristicVectorDimension.NUMERICAL_EXPRESSION, 1, CharacteristicVectorDimension.ASSIGNMENT_EXPRESSION, 2,
                                CharacteristicVectorDimension.METHOD_INVOCATION, 1, CharacteristicVectorDimension.RETURN_STATEMENT, 1)),

                token(CpgTokenType.METHOD_BODY_END,
                        Map.of(CharacteristicVectorDimension.VARIABLE_DECLARATION, 8, CharacteristicVectorDimension.LOOP_CARRIED_DEPENDENCY, 2,
                                CharacteristicVectorDimension.ARRAY_SELECTOR, 2, CharacteristicVectorDimension.NUMERICAL_EXPRESSION, 2,
                                CharacteristicVectorDimension.CONDITIONAL_EXPRESSION, 1, CharacteristicVectorDimension.ASSIGNMENT_EXPRESSION, 5,
                                CharacteristicVectorDimension.METHOD_INVOCATION, 2, CharacteristicVectorDimension.RETURN_STATEMENT, 1,
                                CharacteristicVectorDimension.LOOP, 1)),

                token(CpgTokenType.RECORD_DECL_END,
                        Map.of(CharacteristicVectorDimension.VARIABLE_DECLARATION, 8, CharacteristicVectorDimension.METHOD_DECLARATION, 1,
                                CharacteristicVectorDimension.LOOP_CARRIED_DEPENDENCY, 2, CharacteristicVectorDimension.ARRAY_SELECTOR, 2,
                                CharacteristicVectorDimension.NUMERICAL_EXPRESSION, 2, CharacteristicVectorDimension.CONDITIONAL_EXPRESSION, 1,
                                CharacteristicVectorDimension.ASSIGNMENT_EXPRESSION, 5, CharacteristicVectorDimension.METHOD_INVOCATION, 2,
                                CharacteristicVectorDimension.RETURN_STATEMENT, 1, CharacteristicVectorDimension.LOOP, 1)),

                token(SharedTokenType.FILE_END,
                        Map.of(CharacteristicVectorDimension.VARIABLE_DECLARATION, 8, CharacteristicVectorDimension.METHOD_DECLARATION, 1,
                                CharacteristicVectorDimension.LOOP_CARRIED_DEPENDENCY, 2, CharacteristicVectorDimension.ARRAY_SELECTOR, 2,
                                CharacteristicVectorDimension.NUMERICAL_EXPRESSION, 2, CharacteristicVectorDimension.CONDITIONAL_EXPRESSION, 1,
                                CharacteristicVectorDimension.ASSIGNMENT_EXPRESSION, 5, CharacteristicVectorDimension.METHOD_INVOCATION, 2,
                                CharacteristicVectorDimension.RETURN_STATEMENT, 1, CharacteristicVectorDimension.LOOP, 1)));
        assertIterableEquals(tokens, parseJavaFile("characteristicTokens/SimpleLoop.java", false, true).stream().map(Token::getType).toList());
    }

    private CharacteristicVector vector(Map<CharacteristicVectorDimension, Integer> values) {
        CharacteristicVector vector = new CharacteristicVector();
        for (Map.Entry<CharacteristicVectorDimension, Integer> entry : values.entrySet()) {
            vector.getValue()[entry.getKey().ordinal()] = entry.getValue();
        }
        return vector;
    }

    private CharacteristicCpgTokenType token(TokenType type, Map<CharacteristicVectorDimension, Integer> vectorValues) {
        return new CharacteristicCpgTokenType(type, vector(vectorValues));
    }
}
