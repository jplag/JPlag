package de.jplag.bash;

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

import de.jplag.ParsingException;
import de.jplag.SharedTokenType;
import de.jplag.Token;
import de.jplag.TokenPrinter;

class BashLanguageTest {

    private static final String BASH_EMPTY_OR_SINGLE_LINE_COMMENT = "\\s*(?:#.*)?";

    /**
     * Test source file that is supposed to produce a complete set of tokens, i.e. all types of tokens.
     */
    private static final String COMPLETE_TEST_FILE = "complete.sh";
    private static final double EPSILON = 1E-6;
    public static final double BASELINE_COVERAGE = 0.75;

    private final Logger logger = LoggerFactory.getLogger(BashLanguageTest.class);
    private final String[] testFiles = {COMPLETE_TEST_FILE};
    private final File testFileLocation = Path.of("src", "test", "resources", "de", "jplag", "bash").toFile();
    private BashLanguage language;

    @BeforeEach
    void setup() {
        language = new BashLanguage();
    }

    @Test
    void parseTestFiles() throws ParsingException {
        for (String fileName : testFiles) {
            List<Token> tokens = language.parse(Set.of(new File(testFileLocation, fileName)), false);
            String output = TokenPrinter.printTokens(tokens, testFileLocation);
            logger.info(output);

            testSourceCoverage(fileName, tokens);
            if (COMPLETE_TEST_FILE.equals(fileName)) {
                testTokenCoverage(tokens, fileName);
            }
        }
    }

    private void testSourceCoverage(String fileName, List<Token> tokens) {
        File testFile = new File(testFileLocation, fileName);

        try {
            List<String> lines = Files.readAllLines(testFile.toPath());

            var codeLines = new ArrayList<>(getCodeLines(lines));
            var tokenLines = tokens.stream().mapToInt(Token::getStartLine).filter(line -> line != Token.NO_VALUE).distinct().boxed().toList();

            codeLines.removeAll(tokenLines);

            double coverage = 1.d - codeLines.size() * 1.d / (codeLines.size() + tokenLines.size());
            if (coverage == 1) {
                logger.info("All lines covered.");
            } else {
                logger.info("Coverage: %.1f%%.".formatted(coverage * 100));
                logger.info("Missing lines {}", codeLines);
                assertTrue(coverage - BASELINE_COVERAGE >= EPSILON, "Source coverage is unsatisfactory");
            }

        } catch (IOException exception) {
            logger.info("Error while reading test file %s".formatted(fileName), exception);
            fail();
        }
    }

    private List<Integer> getCodeLines(List<String> lines) {
        return IntStream.range(1, lines.size() + 1).sequential().filter(idx -> {
            String line = lines.get(idx - 1);
            return !line.matches(BASH_EMPTY_OR_SINGLE_LINE_COMMENT);
        }).boxed().toList();
    }

    private void testTokenCoverage(List<Token> tokens, String fileName) {
        var annotatedTokens = tokens.stream().map(Token::getType).collect(Collectors.toSet());
        assertTrue(annotatedTokens.contains(SharedTokenType.FILE_END));
        var annotatedBashTokens = annotatedTokens.stream().filter(BashTokenType.class::isInstance).collect(Collectors.toSet());
        var allBashTokens = BashTokenType.values();
        var missingBashTokens = Arrays.stream(allBashTokens).filter(token -> !annotatedBashTokens.contains(token)).toList();
        assertTrue(missingBashTokens.isEmpty(), "The following bash tokens are missing in the code example '%s':\n".formatted(fileName)
                + String.join("\n", missingBashTokens.stream().map(BashTokenType::getDescription).toList()));
    }
}
