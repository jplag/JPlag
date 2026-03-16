package de.jplag.java_cpg;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import de.jplag.ParsingException;
import de.jplag.Token;
import de.jplag.TokenType;
import de.jplag.java_cpg.token.CpgTokenType;

/**
 * Test class for testing the interaction between AiPass and TokenizationPass.
 */
class CombinedPassTest extends AbstractJavaCpgLanguageTest {

    /**
     * @author Gemini 3 Flash
     * @throws ParsingException if parsing fails.
     */
    @Test
    void testDeadCodeRemovalInTokens() throws ParsingException {
        String fileName = "ai/deadCode5/Submission-01/Main.java";
        JavaCpgLanguage language = new JavaCpgLanguage();
        // Parse the file with normalization enabled (which triggers AiPass)
        List<Token> parsedTokens = language.parse(Set.of(new File(baseDirectory.getAbsolutePath(), fileName)), true);
        // Check if any token belongs to DeadClass (lines 11-15)
        boolean foundDeadClassToken = false;
        for (Token token : parsedTokens) {
            if (token.getLine() >= 11 && token.getLine() <= 15) {
                foundDeadClassToken = true;
                break;
            }
        }
        assertFalse(foundDeadClassToken, "Tokens from DeadClass should have been removed by AiPass");
        // Also verify that we still have tokens from Main class (lines 3-9)
        boolean foundMainClassToken = false;
        for (Token token : parsedTokens) {
            if (token.getLine() >= 3 && token.getLine() <= 9) {
                foundMainClassToken = true;
                break;
            }
        }
        assertTrue(foundMainClassToken, "Tokens from Main class should be present");
    }

    @Test
    @Disabled
    void testDeadCodeRemovalInTokens2() throws ParsingException {
        List<TokenType> parsedTokens = parseJavaFile("combined/One", true);
        assertEquals(87, parsedTokens.size(), "Unexpected number of tokens after dead code removal");
        assertEquals(CpgTokenType.RECORD_DECL_BEGIN, parsedTokens.getFirst());
        assertEquals(CpgTokenType.METHOD_CALL, parsedTokens.get(10));
        assertEquals(CpgTokenType.IF_STATEMENT, parsedTokens.get(20));
        assertEquals(CpgTokenType.FIELD_DECL, parsedTokens.get(30));
        // assertEquals(CpgTokenType.IF_BLOCK_END, parsedTokens.get(40)); //FixMe
        assertEquals(CpgTokenType.METHOD_BODY_BEGIN, parsedTokens.get(50));
        assertEquals(CpgTokenType.RECORD_DECL_BEGIN, parsedTokens.get(61));
        assertEquals(CpgTokenType.ELSE_BLOCK_BEGIN, parsedTokens.get(70));
        assertEquals(CpgTokenType.METHOD_CALL, parsedTokens.get(80));
        assertEquals(CpgTokenType.RECORD_DECL_END, parsedTokens.get(85));
    }

    @Test
    @Disabled
    void testDeadCodeRemovalInTokensInheritance() throws ParsingException {
        List<TokenType> parsedTokens = parseJavaFile("combined/Two", true);
        assertEquals(117, parsedTokens.size(), "Unexpected number of tokens after dead code removal");
        assertEquals(CpgTokenType.RECORD_DECL_BEGIN, parsedTokens.getFirst());
        assertEquals(CpgTokenType.CONSTRUCTOR_CALL, parsedTokens.get(10));
        assertEquals(CpgTokenType.IF_BLOCK_END, parsedTokens.get(20));
        assertEquals(CpgTokenType.METHOD_DECL_BEGIN, parsedTokens.get(30));
        assertEquals(CpgTokenType.METHOD_BODY_END, parsedTokens.get(40));
        assertEquals(CpgTokenType.METHOD_DECL_BEGIN, parsedTokens.get(50));
        assertEquals(CpgTokenType.METHOD_BODY_END, parsedTokens.get(60));
        assertEquals(CpgTokenType.METHOD_BODY_BEGIN, parsedTokens.get(70));
        assertEquals(CpgTokenType.METHOD_BODY_END, parsedTokens.get(80));
        assertEquals(CpgTokenType.FIELD_DECL, parsedTokens.get(90));
        assertEquals(CpgTokenType.ASSIGNMENT, parsedTokens.get(100));
        assertEquals(CpgTokenType.METHOD_PARAM, parsedTokens.get(110));
    }

    @Test
    @Disabled
    void testDeadCodeRemovalInTokensInheritanceComplex() throws ParsingException {
        List<TokenType> parsedTokens = parseJavaFile("combined/multiInheritance1", true);
        assertEquals(117, parsedTokens.size(), "Unexpected number of tokens after dead code removal");  // ToDo
        assertEquals(CpgTokenType.RECORD_DECL_BEGIN, parsedTokens.getFirst());

    }

}
