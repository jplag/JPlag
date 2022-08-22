package de.jplag;

import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.List;

import static de.jplag.TokenConstants.FILE_END;
import static de.jplag.simple.TestTokenConstants.STRING;

public class TokenPrinterTest {

    private static final Path TEST_FILE_LOCATION = Path.of("src", "test", "resources", "de", "jplag", "samples");
    private static final String TEST_FILE_NAME = "TokenprinterTest.txt";

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

        String output = TokenPrinter.printTokens(tokens, TEST_FILE_LOCATION.toFile(), List.of(TEST_FILE_NAME));
        System.out.print(output); // no additional newline required
    }
}


