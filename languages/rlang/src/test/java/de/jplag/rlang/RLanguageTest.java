package de.jplag.rlang;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jplag.ParsingException;
import de.jplag.SharedTokenType;
import de.jplag.Token;
import de.jplag.TokenPrinter;

class RLanguageTest {

    /**
     * Regular expression for lines that contain no code.
     */
    private static final String R_NO_CODE_LINE = "\\s*(?:#.*)?";

    /**
     * Test source file that is supposed to produce a complete set of tokens, i.e. all types of tokens.
     */
    private static final String COMPLETE_TEST_FILE = "Complete.R";

    private final Logger logger = LoggerFactory.getLogger(RLanguageTest.class);
    private final String[] testFiles = new String[] {"Game.R", COMPLETE_TEST_FILE};
    private final File testFileLocation = Path.of("src", "test", "resources", "de", "jplag", "rlang").toFile();
    private Language language;

    @BeforeEach
    void setup() {
        language = new Language();
    }

    @Test
    void parseTestFiles() throws ParsingException {
        for (String fileName : testFiles) {
            List<Token> tokens = language.parse(Set.of(new File(testFileLocation, fileName)));
            String output = TokenPrinter.printTokens(tokens, testFileLocation);
            logger.info(output);

            testSourceCoverage(fileName, tokens);
            if (fileName.equals(COMPLETE_TEST_FILE))
                testTokenCoverage(tokens, fileName);
        }
    }

    /**
     * Confirms that the code is covered to a basic extent, i.e. each line of code contains at least one token.
     * @param fileName a code sample file name
     * @param tokens the list of tokens generated from the sample
     */
    private void testSourceCoverage(String fileName, List<Token> tokens) {
        File testFile = new File(testFileLocation, fileName);

        try {
            List<String> lines = Files.readAllLines(testFile.toPath());
            String emptyLineExpression = getNoCodeLineExpression();

            // All lines that contain code
            var codeLines = IntStream.range(1, lines.size() + 1).filter(idx -> !lines.get(idx - 1).matches(emptyLineExpression)).toArray();
            // All lines that contain token
            var tokenLines = tokens.stream().mapToInt(Token::getLine).filter(line -> line != Token.NO_VALUE).distinct().toArray();

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
     * @param tokens list of tokens which is supposed to contain all types of tokens
     * @param fileName The file name of the complete code example
     */
    private void testTokenCoverage(List<Token> tokens, String fileName) {
        var annotatedTokens = tokens.stream().map(Token::getType).collect(Collectors.toSet());
        assertTrue(annotatedTokens.contains(SharedTokenType.FILE_END));
        var annotatedRTokens = annotatedTokens.stream().filter(RTokenType.class::isInstance).collect(Collectors.toSet());
        var allRTokens = RTokenType.values();
        var missingRTokens = Arrays.stream(allRTokens).filter(token -> !annotatedRTokens.contains(token)).toList();
        assertTrue(missingRTokens.isEmpty(), "The following R tokens are missing in the code example '%s':\n".formatted(fileName)
                + String.join("\n", missingRTokens.stream().map(RTokenType::getDescription).toList()));
    }

    private static String getNoCodeLineExpression() {
        return R_NO_CODE_LINE;
    }

}
