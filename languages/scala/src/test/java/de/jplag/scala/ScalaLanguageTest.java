package de.jplag.scala;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jplag.SharedTokenType;
import de.jplag.Token;
import de.jplag.TokenPrinter;

class ScalaLanguageTest {

    /**
     * Test source file that is supposed to produce a complete set of tokens, i.e. all types of tokens.
     */
    private static final String COMPLETE_TEST_FILE = "Complete.scala";

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
    private static final double EPSILON = 1E-6;

    private final Logger logger = LoggerFactory.getLogger(ScalaLanguageTest.class);
    private final String[] testFiles = new String[] {"Parser.scala", COMPLETE_TEST_FILE};
    private final File testFileLocation = Path.of("src", "test", "resources", "de", "jplag", "scala").toFile();
    private Language language;

    @BeforeEach
    void setup() {
        language = new Language();
    }

    @Test
    void parseTestFiles() {
        for (String fileName : testFiles) {
            List<Token> tokens = language.parse(Set.of(new File(testFileLocation, fileName)));
            String output = TokenPrinter.printTokens(tokens, testFileLocation);
            logger.info(output);

            if (fileName.equals(COMPLETE_TEST_FILE)) {
                testTokenCoverage(tokens, fileName);
            }
            testSourceCoverage(fileName, tokens);

        }
    }

    /**
     * Confirms that the code is covered to a basic extent, i.e. each line of code contains at least one token.
     * @param fileName a code sample file name
     * @param tokens the list of tokens generated from the sample
     */
    private void testSourceCoverage(String fileName, List<Token> tokens) {
        var testFile = new File(testFileLocation, fileName);

        try {
            var lines = Files.readAllLines(testFile.toPath());

            // All lines that contain code
            var codeLines = new ArrayList<>(getCodeLines(lines));
            // All lines that contain token
            var tokenLines = tokens.stream().map(Token::getLine).distinct().toList();

            // Keep only lines that have no tokens
            codeLines.removeAll(tokenLines);

            var coverage = 1.d - (codeLines.size() * 1.d / (codeLines.size() + tokenLines.size()));
            if (coverage == 1) {
                logger.info("All lines covered.");
            } else {
                logger.info("Coverage: %.1f%%.".formatted(coverage * 100));
                logger.info("Missing lines {}", codeLines);
                if (coverage - 0.9 <= EPSILON) {
                    fail("Source coverage is unsatisfactory");
                }
            }

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
    private List<Integer> getCodeLines(List<String> lines) {
        // This boxed boolean can be accessed from within the lambda method below
        var state = new Object() {
            boolean insideComment = false;
        };

        return IntStream.rangeClosed(1, lines.size()).sequential().filter(idx -> {
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
        }).boxed().toList();
    }

    /**
     * Confirms that all Token types are 'reachable' with a complete code example.
     * @param tokens list of tokens which is supposed to contain all types of tokens
     * @param fileName The file name of the complete code example
     */
    private void testTokenCoverage(List<Token> tokens, String fileName) {
        var annotatedTokens = tokens.stream().map(Token::getType).collect(Collectors.toSet());
        assertTrue(annotatedTokens.contains(SharedTokenType.FILE_END));
        var annotatedScalaTokens = annotatedTokens.stream().filter(ScalaTokenType.class::isInstance).collect(Collectors.toSet());
        var allScalaTokens = ScalaTokenType.values();
        var missingScalaTokens = Arrays.stream(allScalaTokens).filter(token -> !annotatedScalaTokens.contains(token)).toList();
        assertTrue(missingScalaTokens.isEmpty(), "The following scala tokens are missing in the code example '%s':\n".formatted(fileName)
                + String.join("\n", missingScalaTokens.stream().map(ScalaTokenType::getDescription).toList()));
    }
}
