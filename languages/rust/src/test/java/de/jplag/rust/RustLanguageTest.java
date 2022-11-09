package de.jplag.rust;

import static org.junit.jupiter.api.Assertions.assertTrue;

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

import de.jplag.ParsingException;
import de.jplag.SharedTokenType;
import de.jplag.Token;
import de.jplag.TokenPrinter;

class RustLanguageTest {

    /**
     * Regular expression for empty lines and single line comments.
     */
    private static final String RUST_EMPTY_OR_SINGLE_LINE_COMMENT = "\\s*(?://.*)?";
    private static final String RUST_MULTILINE_COMMENT_BEGIN = "\\s*/\\*.*";
    private static final String RUST_MULTILINE_COMMENT_END = ".*\\*/\\s*";

    /**
     * Test source file that is supposed to produce a complete set of tokens, i.e. all types of tokens.
     */
    private static final String COMPLETE_TEST_FILE = "complete.rs";
    private static final String RUST_SHEBANG = "#!.*$";
    private static final double EPSILON = 1E-6;
    public static final double BASELINE_COVERAGE = 0.75;

    private final Logger logger = LoggerFactory.getLogger(RustLanguageTest.class);
    private final String[] testFiles = new String[] {"deno_core_runtime.rs", COMPLETE_TEST_FILE};
    private final File testFileLocation = Path.of("src", "test", "resources", "de", "jplag", "rust").toFile();
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

            // All lines that contain code
            var codeLines = new ArrayList<>(getCodeLines(lines));
            // All lines that contain token
            var tokenLines = tokens.stream().mapToInt(Token::getLine).filter(line -> line != Token.NO_VALUE).distinct().boxed().toList();

            // Keep only lines that have no tokens
            codeLines.removeAll(tokenLines);

            double coverage = 1.d - (codeLines.size() * 1.d / (codeLines.size() + tokenLines.size()));
            if (coverage == 1) {
                logger.info("All lines covered.");
            } else {
                logger.info("Coverage: %.1f%%.".formatted(coverage * 100));
                logger.info("Missing lines {}", codeLines);
                assertTrue(coverage - BASELINE_COVERAGE >= EPSILON, "Source coverage is unsatisfactory");
            }

        } catch (IOException exception) {
            logger.info("Error while reading test file %s".formatted(fileName), exception);
            assertTrue(false);
        }
    }

    private List<Integer> getCodeLines(List<String> lines) {
        var state = new Object() {
            boolean insideMultilineComment = false;

        };

        return IntStream.range(1, lines.size() + 1).sequential().filter(idx -> {
            String line = lines.get(idx - 1);
            if (line.matches(RUST_EMPTY_OR_SINGLE_LINE_COMMENT)) {
                return false;
            } else if (idx == 1 && line.matches(RUST_SHEBANG)) {
                return false;
            } else if (line.matches(RUST_MULTILINE_COMMENT_BEGIN)) {
                state.insideMultilineComment = true;
                return false;
            } else if (state.insideMultilineComment && line.matches(RUST_MULTILINE_COMMENT_END)) {
                state.insideMultilineComment = false;
                return false;
            } else {
                return !state.insideMultilineComment;
            }
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
        var annotatedRustTokens = annotatedTokens.stream().filter(RustTokenType.class::isInstance).collect(Collectors.toSet());
        var allRustTokens = RustTokenType.values();
        var missingRustTokens = Arrays.stream(allRustTokens).filter(token -> !annotatedRustTokens.contains(token)).toList();
        assertTrue(missingRustTokens.isEmpty(), "The following rust tokens are missing in the code example '%s':\n".formatted(fileName)
                + String.join("\n", missingRustTokens.stream().map(RustTokenType::getDescription).toList()));
    }

}
