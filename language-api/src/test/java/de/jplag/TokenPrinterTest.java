package de.jplag;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class TokenPrinterTest {

    private static final Path TEST_FILE_LOCATION = Path.of("src", "test", "resources", "de", "jplag", "samples");
    private static final String TEST_FILE_NAME = "TokenPrinterTest.txt";
    private static final String TOKEN_SEPARATOR = "(\\|)?\s*\\|";

    private static final Logger logger = LoggerFactory.getLogger(TokenPrinterTest.class);

    /**
     * Tests the function of the TokenPrinter.
     */
    @Test
    void printMockDirectoriesAsSubmissions() {

        // See TokenPrinterTest.txt for the intended behaviour
        List<Token> tokens = new ArrayList<>();
        File testFile = new File(TEST_FILE_LOCATION.toFile(), TEST_FILE_NAME);
        tokens.add(new Token(TestTokenType.STRING, testFile, 1, 1, 1, 7, "STRING".length()));
        tokens.add(new Token(TestTokenType.STRING, testFile, 2, 1, 2, 8, "STRING".length() + 1));
        tokens.add(new Token(TestTokenType.STRING, testFile, 3, 1, 3, 12, "STRING".length() + 2));
        tokens.add(new Token(TestTokenType.STRING, testFile, 4, 1, 4, 17, "STRING".length() + 10));

        tokens.add(new Token(TestTokenType.STRING, testFile, 6, 3, 6, 4, 1));
        tokens.add(new Token(TestTokenType.STRING, testFile, 7, 9, 7, 10, 1));

        tokens.add(new Token(TestTokenType.STRING, testFile, 9, 1, 9, 10, 1));
        tokens.add(new Token(TestTokenType.STRING, testFile, 9, 10, 9, 11, 1));

        tokens.add(new Token(TestTokenType.STRING, testFile, 10, 1, 10, 2, 1));
        tokens.add(new Token(TestTokenType.STRING, testFile, 10, 5, 10, 6, 1));

        tokens.add(new Token(TestTokenType.STRING, testFile, 12, 1, 12, 2, 1));
        tokens.add(new Token(TestTokenType.STRING, testFile, 12, 5, 12, 6, 1));
        tokens.add(new Token(TestTokenType.STRING, testFile, 12, 10, 12, 11, 1));

        tokens.add(new Token(TestTokenType.STRING, testFile, 14, 10, 14, 11, 1));
        tokens.add(new Token(TestTokenType.STRING, testFile, 14, 5, 14, 6, 1));
        tokens.add(new Token(TestTokenType.STRING, testFile, 14, 1, 14, 2, 1));

        tokens.add(new Token(TestTokenType.STRING, testFile, 16, -5, 16, -4, 1));

        tokens.add(new Token(TestTokenType.STRING, testFile, 19, 100, 19, 101, 1));

        tokens.add(new Token(TestTokenType.STRING, testFile, 22, 1, 22, 101, 100));

        tokens.add(Token.fileEnd(testFile));

        tokens.add(new Token(TestTokenType.STRING, testFile, 100, 1, 100, 2, 1));

        String output = TokenPrinter.printTokens(tokens, TEST_FILE_LOCATION.toFile());
        logger.info(output); // no additional newline required

        testOutputCorrectness(TEST_FILE_NAME, tokens, output);
    }

    private static void testOutputCorrectness(String fileName, List<Token> tokens, String output) {
        int lineIndex = -1;
        int tokenIndex = 0;
        for (String line : output.lines().toList()) {
            if (line.isEmpty()) {
                continue;
            } else if (lineIndex == -1) {
                assertEquals(fileName, line);
                lineIndex = 0;
            } else if (line.startsWith(String.valueOf(lineIndex + 1))) {
                lineIndex++;
            } else {
                line = line.trim();
                String[] lineTokens = line.split(TOKEN_SEPARATOR);
                for (String lineToken : lineTokens) {
                    if (lineToken.isEmpty()) {
                        continue;
                    }
                    Token currentToken = tokens.get(tokenIndex);
                    assertTrue(lineToken.equalsIgnoreCase(currentToken.getType().getDescription()),
                            "expected: %s, actual: %s".formatted(lineToken, currentToken));
                    if (currentToken.getStartLine() != Token.NO_VALUE) {
                        assertEquals(lineIndex, currentToken.getStartLine(), "invalid line for token " + currentToken);
                    }
                    tokenIndex++;
                }
            }
        }
        assertEquals(tokens.size() - 1, tokenIndex, "incorrect number of tokens printed");
    }

    private enum TestTokenType implements TokenType {
        /**
         * Represents a token of type STRING, used for testing purposes.
         */
        STRING("STRING");

        private final String description;

        @Override
        public String getDescription() {
            return description;
        }

        TestTokenType(String description) {
            this.description = description;
        }
    }
}
