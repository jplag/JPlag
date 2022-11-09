package de.jplag.kotlin;

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

class KotlinLanguageTest {

    /**
     * Test source file that is supposed to produce a complete set of tokens, i.e. all types of tokens.
     */
    private static final String COMPLETE_TEST_FILE = "Complete.kt";

    /**
     * Regular expression that describes lines consisting only of whitespace and optionally a line comment.
     */
    private static final String EMPTY_OR_SINGLE_LINE_COMMENT = "\\s*(//.*|/\\*.*\\*/)?";

    /**
     * Regular expression that describes lines containing the start of a multiline comment and no code before it.
     */
    private static final String DELIMITED_COMMENT_START = "\\s*/\\*(?:(?!\\*/).)*$";

    /**
     * Regular expression that describes lines containing the end of a multiline comment and no more code after that.
     */
    private static final String DELIMITED_COMMENT_END = ".*\\*/\\s*$";

    private final Logger logger = LoggerFactory.getLogger(KotlinLanguageTest.class);
    private final String[] testFiles = new String[] {COMPLETE_TEST_FILE, "Game.kt"};
    private final File testFileLocation = Path.of("src", "test", "resources", "de", "jplag", "kotlin").toFile();
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
            if (fileName.equals(COMPLETE_TEST_FILE)) {
                testTokenCoverage(tokens, fileName);
            }

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

            // All lines that contain code
            var codeLines = getCodeLines(lines);
            // All lines that contain token
            var tokenLines = tokens.stream().mapToInt(Token::getLine).filter(line -> line != Token.NO_VALUE).distinct().toArray();

            if (codeLines.length > tokenLines.length) {
                var diffLine = IntStream.range(0, codeLines.length)
                        .dropWhile(lineIdx -> lineIdx < tokenLines.length && codeLines[lineIdx] == tokenLines[lineIdx]).findFirst();
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
     * Gets the line numbers of lines containing actual code, omitting empty lines and comment lines.
     * @param lines lines of a code file
     * @return an array of the line numbers of code lines
     */
    private int[] getCodeLines(List<String> lines) {
        // This boxed boolean can be accessed from within the lambda method below
        var state = new Object() {
            boolean insideComment = false;
        };

        var codeLines = IntStream.rangeClosed(1, lines.size()).sequential().filter(idx -> {
            String line = lines.get(idx - 1);
            if (line.matches(EMPTY_OR_SINGLE_LINE_COMMENT)) {
                return false;
            } else if (line.matches(DELIMITED_COMMENT_START)) {
                state.insideComment = true;
                return false;
            } else if (state.insideComment) {
                // This fails if code follows after '*/'. If the code is formatted well, this should not happen.
                if (line.matches(DELIMITED_COMMENT_END)) {
                    state.insideComment = false;
                }
                return false;
            }
            return true;
        });

        return codeLines.toArray();

    }

    /**
     * Confirms that all Token types are 'reachable' with a complete code example.
     * @param tokens list of tokens which is supposed to contain all types of tokens
     * @param fileName The file name of the complete code example
     */
    private void testTokenCoverage(List<Token> tokens, String fileName) {
        var annotatedTokens = tokens.stream().map(Token::getType).collect(Collectors.toSet());
        assertTrue(annotatedTokens.contains(SharedTokenType.FILE_END));
        var annotatedKotlinTokens = annotatedTokens.stream().filter(KotlinTokenType.class::isInstance).collect(Collectors.toSet());
        var allKotlinTokens = KotlinTokenType.values();
        var missingKotlinTokens = Arrays.stream(allKotlinTokens).filter(token -> !annotatedKotlinTokens.contains(token)).toList();
        assertTrue(missingKotlinTokens.isEmpty(), "The following kotlin tokens are missing in the code example '%s':\n".formatted(fileName)
                + String.join("\n", missingKotlinTokens.stream().map(KotlinTokenType::getDescription).toList()));
    }

}