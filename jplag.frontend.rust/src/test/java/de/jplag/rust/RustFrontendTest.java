package de.jplag.rust;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
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

public class RustFrontendTest {

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
    public static final int NOT_SET = -1;
    private static final String RUST_SHEBANG = "#!.*$";
    private static final double EPSILON = 1E-6;

    private final Logger logger = LoggerFactory.getLogger("Rust frontend test");
    private final String[] testFiles = new String[] {"deno_core_runtime.rs", COMPLETE_TEST_FILE};
    private final File testFileLocation = Path.of("src", "test", "resources", "de", "jplag", "rust").toFile();
    private Language language;

    @BeforeEach
    void setup() {
        language = new Language();
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

            // All lines that contain code
            var codeLines = new ArrayList<>(getCodeLines(lines));
            // All lines that contain token
            var tokenLines = IntStream.range(0, tokens.size()).mapToObj(tokens::getToken).mapToInt(Token::getLine).distinct().boxed().toList();

            // Keep only lines that have no tokens
            codeLines.removeAll(tokenLines);

            double coverage = 1.d - (codeLines.size() * 1.d / (codeLines.size() + tokenLines.size()));
            if (coverage == 1) {
                logger.info("All lines covered.");
            } else {
                logger.info("Coverage: %.1f%%.".formatted(coverage * 100));
                logger.info("Missing lines {}", codeLines);
                if (coverage - 0.9 <= EPSILON) {
                    // TODO use fail() instead when frontend is ready
                    logger.error("Source coverage is unsatisfactory");
                }
            }

        } catch (IOException exception) {
            logger.info("Error while reading test file %s".formatted(fileName), exception);
            fail();
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
     * @param tokens TokenList which is supposed to contain all types of tokens
     * @param fileName The file name of the complete code example
     */
    private void testTokenCoverage(TokenList tokens, String fileName) {
        var foundTokens = StreamSupport.stream(tokens.allTokens().spliterator(), true).mapToInt(Token::getType).sorted().distinct().toArray();
        // Exclude SEPARATOR_TOKEN, as it does not occur
        var allTokens = IntStream.range(0, RustTokenConstants.NUMBER_DIFF_TOKENS).filter(i -> i != TokenConstants.SEPARATOR_TOKEN).toArray();

        if (allTokens.length > foundTokens.length) {
            var diffLine = IntStream.range(0, allTokens.length)
                    .dropWhile(lineIndex -> lineIndex < foundTokens.length && allTokens[lineIndex] == foundTokens[lineIndex]).findFirst();
            diffLine.ifPresent(lineIdx -> fail("Token type %s was not found in the complete code example '%s'."
                    .formatted(new RustToken(allTokens[lineIdx], fileName, NOT_SET, NOT_SET, NOT_SET).type2string(), fileName)));
        }
        assertArrayEquals(allTokens, foundTokens);
    }

}
