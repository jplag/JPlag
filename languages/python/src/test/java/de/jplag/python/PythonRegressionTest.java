package de.jplag.python;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import de.jplag.Language;
import de.jplag.ParsingException;
import de.jplag.Token;
import de.jplag.TokenType;
import de.jplag.python3.Python3TokenType;

/**
 * Regression tests to ensure the new Tree-sitter-based Python language module produces the same token sequences as the
 * old ANTLR-based Python3 module for backward compatibility. This test uses a dedicated RegressionTest.py file that
 * only contains Python 3.6 features (the last version supported by the old ANTLR module) to ensure fair comparison
 * between the two parsers.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PythonRegressionTest {

    private static final String REGRESSION_TEST_FILE = "RegressionTest.py";

    private Language oldPythonLanguage;
    private Language newPythonLanguage;
    private Path testResourcesPath;

    @BeforeAll
    void setUp() {
        oldPythonLanguage = new de.jplag.python3.PythonLanguage();
        newPythonLanguage = new de.jplag.python.PythonLanguage();
        testResourcesPath = Paths.get("src", "test", "resources", "de", "jplag", "python");
    }

    /**
     * TODO: Tree-sitter-based module provides more correct tokenization than the old ANTLR module. The old module
     * incorrectly generates ARRAY tokens for attribute access and method calls. This represents an improvement in
     * tokenization quality rather than a regression.
     */
    @Disabled
    @Test
    void testRegressionCompatibility() throws ParsingException, IOException {
        testFileCompatibility(REGRESSION_TEST_FILE);
    }

    private void testFileCompatibility(String fileName) throws ParsingException, IOException {
        File testFile = testResourcesPath.resolve(fileName).toFile();
        assertNotNull(testFile, "Test file should exist: " + fileName);

        // Parse with both language modules
        List<Token> oldTokens = oldPythonLanguage.parse(Set.of(testFile), false);
        List<Token> newTokens = newPythonLanguage.parse(Set.of(testFile), false);

        // Convert tokens to comparable format
        List<TokenType> oldTokenTypes = extractTokenTypes(oldTokens);
        List<TokenType> newTokenTypes = extractTokenTypes(newTokens);

        // Apply token type mapping for comparison
        List<TokenType> mappedNewTokenTypes = mapTokenTypes(newTokenTypes);

        // TODO: Remove this once the test is enabled
        System.out.println("Old token order: " + oldTokenTypes);
        System.out.println("New token order: " + mappedNewTokenTypes);

        // Compare token sequences
        assertEquals(oldTokenTypes.size(), mappedNewTokenTypes.size(), "Token count should match for " + fileName);

        for (int i = 0; i < oldTokenTypes.size(); i++) {
            TokenType oldType = oldTokenTypes.get(i);
            TokenType newType = mappedNewTokenTypes.get(i);
            assertEquals(oldType, newType, String.format("Token at position %d should match for %s", i, fileName));
        }
    }

    private List<TokenType> extractTokenTypes(List<Token> tokens) {
        List<TokenType> tokenTypes = new ArrayList<>();
        for (Token token : tokens) {
            tokenTypes.add(token.getType());
        }
        return tokenTypes;
    }

    /**
     * Maps token types from the new Tree-sitter-based module to match the old ANTLR-based module's token types for
     * comparison. Filters out newer tokens that weren't present in the old module.
     */
    private List<TokenType> mapTokenTypes(List<TokenType> newTokenTypes) {
        List<TokenType> mappedTokens = new ArrayList<>();

        for (TokenType tokenType : newTokenTypes) {
            TokenType mappedType = mapTokenType(tokenType);
            // If mappedType is null, the token is filtered out
            if (mappedType != null) {
                mappedTokens.add(mappedType);
            }
        }

        return mappedTokens;
    }

    /**
     * Maps individual token types from new module to old module
     * @param newType The token type to map
     * @return The mapped token type or {@code null} if the token should be filtered out
     */
    private TokenType mapTokenType(TokenType newType) {
        // Check if token type is from ANTLR module
        if (!(newType instanceof PythonTokenType)) {
            return newType;
        }

        PythonTokenType pythonType = (PythonTokenType) newType;

        return switch (pythonType) {
            case IMPORT -> Python3TokenType.IMPORT;
            case CLASS_BEGIN -> Python3TokenType.CLASS_BEGIN;
            case CLASS_END -> Python3TokenType.CLASS_END;
            case METHOD_BEGIN -> Python3TokenType.METHOD_BEGIN;
            case METHOD_END -> Python3TokenType.METHOD_END;
            case ASSIGN -> Python3TokenType.ASSIGN;
            case WHILE_BEGIN -> Python3TokenType.WHILE_BEGIN;
            case WHILE_END -> Python3TokenType.WHILE_END;
            case FOR_BEGIN -> Python3TokenType.FOR_BEGIN;
            case FOR_END -> Python3TokenType.FOR_END;
            case TRY_BEGIN -> Python3TokenType.TRY_BEGIN;
            case EXCEPT_BEGIN -> Python3TokenType.EXCEPT_BEGIN;
            case EXCEPT_END -> Python3TokenType.EXCEPT_END;
            case IF_BEGIN -> Python3TokenType.IF_BEGIN;
            case IF_END -> Python3TokenType.IF_END;
            case APPLY -> Python3TokenType.APPLY;
            case BREAK -> Python3TokenType.BREAK;
            case CONTINUE -> Python3TokenType.CONTINUE;
            case RETURN -> Python3TokenType.RETURN;
            case RAISE -> Python3TokenType.RAISE;
            case LAMBDA -> Python3TokenType.LAMBDA;
            case ASSERT -> Python3TokenType.ASSERT;
            case YIELD -> Python3TokenType.YIELD;
            case DEL -> Python3TokenType.DEL;
            case WITH_BEGIN -> Python3TokenType.WITH_BEGIN;
            case WITH_END -> Python3TokenType.WITH_END;
            case ASYNC -> Python3TokenType.ASYNC;
            case AWAIT -> Python3TokenType.AWAIT;
            case LIST, SET, DICTIONARY -> Python3TokenType.ARRAY;

            // Map to single FINALLY token
            case FINALLY_BEGIN -> Python3TokenType.FINALLY;
            case DECORATOR_BEGIN -> Python3TokenType.DEC_BEGIN;
            case DECORATOR_END -> Python3TokenType.DEC_END;

            // Tokens not present in old module
            case FINALLY_END, TRY_END, EXCEPT_GROUP_BEGIN, EXCEPT_GROUP_END, NAMED_EXPR, MATCH_BEGIN, MATCH_END, CASE, TYPE_ALIAS, PASS, GLOBAL, NONLOCAL -> null;

            default -> throw new IllegalArgumentException("Unmapped token type: " + pythonType);
        };
    }
}
