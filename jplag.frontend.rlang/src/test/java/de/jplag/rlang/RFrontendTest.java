package de.jplag.rlang;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jplag.Token;
import de.jplag.TokenConstants;
import de.jplag.TokenList;
import de.jplag.TokenPrinter;
import de.jplag.testutils.TestErrorConsumer;

public class RFrontendTest {

    /**
     * Regular expression for lines that contain no code.
     */
    private static final String R_NO_CODE_LINE = "\\s*(?:#.*)?";

    /**
     * Test source file that is supposed to produce a complete set of tokens, i.e. all types of tokens.
     */
    private static final String COMPLETE_TEST_FILE = "Complete.R";
    public static final int NOT_SET = -1;

    private final Logger logger = LoggerFactory.getLogger("R frontend test");
    private final String[] testFiles = new String[] {"Game.R", COMPLETE_TEST_FILE};
    private final File testFileLocation = Path.of("src", "test", "resources", "de", "jplag", "rlang").toFile();
    private Language language;

    @BeforeEach
    void setup() {
        TestErrorConsumer consumer = new TestErrorConsumer();
        language = new Language(consumer);
    }

    @Test
    void parseTestFiles() {
        for (String fileName : testFiles) {
            TokenList tokens = language.parse(testFileLocation, new String[] {fileName});
            String output = TokenPrinter.printTokens(tokens, testFileLocation, List.of(fileName));
            logger.info(output);

            testSourceCoverage(fileName, tokens);
            if (fileName.equals(COMPLETE_TEST_FILE))
                testTokenCoverage(tokens, fileName);
        }
    }

    /**
     * Confirms that the code is covered to a basic extent, i.e. each line of code contains at least one token.
     * @param fileName a code sample file name
     * @param tokens the TokenList generated from the sample
     */
    private void testSourceCoverage(String fileName, TokenList tokens) {
        File testFile = new File(testFileLocation, fileName);

        try {
            List<String> lines = Files.readAllLines(testFile.toPath());
            String emptyLineExpression = getNoCodeLineExpression();

            // All lines that contain code
            var codeLines = IntStream.range(1, lines.size() + 1).filter(idx -> !lines.get(idx - 1).matches(emptyLineExpression)).toArray();
            // All lines that contain token
            var tokenLines = IntStream.range(0, tokens.size()).mapToObj(tokens::getToken).mapToInt(Token::getLine).distinct().toArray();

            if (codeLines.length > tokenLines.length) {
                var diffLine = IntStream.range(0, codeLines.length)
                        .dropWhile(lineIndex -> lineIndex < tokenLines.length && codeLines[lineIndex] == tokenLines[lineIndex]).findFirst();
                diffLine.ifPresent(
                        lineIdx -> fail("Line %d of file '%s' is not represented in the token list.".formatted(codeLines[lineIdx], fileName)));
            }
            assertArrayEquals(codeLines, tokenLines);
        } catch (IOException exception) {
            logger.info("Error while reading test file %s".formatted(fileName), exception);
            fail();
        }
    }

    /**
     * Confirms that all Token types are 'reachable' with a complete code example.
     * @param tokens TokenList which is supposed to contain all types of tokens
     * @param fileName The file name of the complete code example
     */
    private void testTokenCoverage(TokenList tokens, String fileName) {
        var foundTokens = StreamSupport.stream(tokens.allTokens().spliterator(), true).mapToInt(Token::getType).sorted().distinct().toArray();
        // Exclude SEPARATOR_TOKEN, as it does not occur
        var allTokens = IntStream.range(0, RTokenConstants.NUM_DIFF_TOKENS).filter(i -> i != TokenConstants.SEPARATOR_TOKEN).toArray();

        if (allTokens.length > foundTokens.length) {
            var diffLine = IntStream.range(0, allTokens.length)
                    .dropWhile(lineIndex -> lineIndex < foundTokens.length && allTokens[lineIndex] == foundTokens[lineIndex]).findFirst();
            diffLine.ifPresent(lineIdx -> fail("Token type %s was not found in the complete code example '%s'."
                    .formatted(new RToken(allTokens[lineIdx], fileName, NOT_SET, NOT_SET, NOT_SET).type2string(), fileName)));
        }
        assertArrayEquals(allTokens, foundTokens);
    }

    private static String getNoCodeLineExpression() {
        return R_NO_CODE_LINE;
    }

}
