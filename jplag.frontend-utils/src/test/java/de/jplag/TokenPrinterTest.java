package de.jplag;

import static de.jplag.TokenConstants.FILE_END;
import static de.jplag.simple.TestTokenConstants.STRING;
import static org.junit.jupiter.api.Assertions.fail;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class TokenPrinterTest {

    private static final Path TEST_FILE_LOCATION = Path.of("src", "test", "resources", "de", "jplag", "samples");
    private static final String TEST_FILE_NAME = "TokenPrinterTest.txt";
    private static final String TOKEN_SEPARATOR = "(\\|)?\s*\\|";

    private static final Logger logger = LoggerFactory.getLogger(TokenPrinterTest.class);

    /**
     * Tests the function of the TokenPrinter
     */
    @Test
    void printMockDirectoriesAsSubmissions() {

        // See TokenPrinterTest.txt for the intended behaviour
        TokenList tokens = new TokenList();
        tokens.addToken(new TestToken(STRING, TEST_FILE_NAME, 1, 1, "STRING".length()));
        tokens.addToken(new TestToken(STRING, TEST_FILE_NAME, 2, 1, "STRING".length() + 1));
        tokens.addToken(new TestToken(STRING, TEST_FILE_NAME, 3, 1, "STRING".length() + 2));
        tokens.addToken(new TestToken(STRING, TEST_FILE_NAME, 4, 1, "STRING".length() + 10));

        tokens.addToken(new TestToken(STRING, TEST_FILE_NAME, 6, 3, 1));
        tokens.addToken(new TestToken(STRING, TEST_FILE_NAME, 7, 9, 1));

        tokens.addToken(new TestToken(STRING, TEST_FILE_NAME, 9, 1, 1));
        tokens.addToken(new TestToken(STRING, TEST_FILE_NAME, 9, 10, 1));

        tokens.addToken(new TestToken(STRING, TEST_FILE_NAME, 10, 1, 1));
        tokens.addToken(new TestToken(STRING, TEST_FILE_NAME, 10, 5, 1));

        tokens.addToken(new TestToken(STRING, TEST_FILE_NAME, 12, 1, 1));
        tokens.addToken(new TestToken(STRING, TEST_FILE_NAME, 10, 5, 1));
        tokens.addToken(new TestToken(STRING, TEST_FILE_NAME, 12, 10, 1));

        tokens.addToken(new TestToken(STRING, TEST_FILE_NAME, 14, 10, 1));
        tokens.addToken(new TestToken(STRING, TEST_FILE_NAME, 14, 5, 1));
        tokens.addToken(new TestToken(STRING, TEST_FILE_NAME, 14, 1, 1));

        tokens.addToken(new TestToken(STRING, TEST_FILE_NAME, 16, -5, 1));

        tokens.addToken(new TestToken(STRING, TEST_FILE_NAME, 19, 100, 1));

        tokens.addToken(new TestToken(STRING, TEST_FILE_NAME, 22, 1, 100));

        tokens.addToken(new TestToken(FILE_END, TEST_FILE_NAME, 24, 1, -1));

        tokens.addToken(new TestToken(STRING, TEST_FILE_NAME, 100, 1, 1));

        String output = TokenPrinter.printTokens(tokens, TEST_FILE_LOCATION.toFile());
        logger.debug(output); // no additional newline required

        testOutputCorrectness(TEST_FILE_NAME, tokens, output);
    }

    private static void testOutputCorrectness(String fileName, TokenList tokens, String output) {
        int lineIndex = 0;
        int tokenIndex = 0;
        boolean seenFileName = false;
        for (String line : output.lines().toList()) {
            if (line.isEmpty()) {
                continue;
            } else if (lineIndex == 0 && line.equals(fileName)) {
                seenFileName = true;
            } else if (line.startsWith("" + (lineIndex + 1))) {
                lineIndex++;
            } else {
                line = line.trim();
                String[] lineTokens = line.split(TOKEN_SEPARATOR);
                for (String lineToken : lineTokens) {
                    if (lineToken.isEmpty()) {
                        continue;
                    }
                    Token currentToken = tokens.getToken(tokenIndex);
                    if (lineToken.equalsIgnoreCase(currentToken.toString()) && currentToken.getLine() == lineIndex) {
                        tokenIndex++;
                    } else {
                        fail("Expected token %s, but found %s".formatted(currentToken, lineToken));
                    }
                }
            }
        }
        if (!seenFileName) {
            fail("Expected file name");
        }
    }
}
