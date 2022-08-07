package de.jplag.scala;

import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
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

class ScalaFrontendTest {

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
    private static final String EMPTY_STRING = "";
    private static final int NOT_SET = -1;
    private static final double EPSILON = 1E-6;

    private final Logger logger = LoggerFactory.getLogger("Scala frontend test");
    private final String[] testFiles = new String[] {"Complete.scala", "Parser.scala"};
    private final File testFileLocation = Path.of("src", "test", "resources", "de", "jplag", "scala").toFile();
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

            if (fileName.equals(COMPLETE_TEST_FILE)) {
                testTokenCoverage(tokens, fileName);
            }
            testSourceCoverage(fileName, tokens);

        }
    }

    /**
     * Confirms that every type of ScalaToken has a Sting representation associated to it.
     */
    @Test
    void testTokenToString() {
        var missingTokens = IntStream.range(0, ScalaTokenConstants.numberOfTokens())
                .mapToObj(type -> new ScalaToken(type, EMPTY_STRING, NOT_SET, NOT_SET, NOT_SET))
                .filter(token -> token.type2string().contains("UNKNOWN")).toList();

        if (!missingTokens.isEmpty()) {
            var typeList = missingTokens.stream().map(Token::getType).map(Object::toString).collect(Collectors.joining(", "));
            fail("Found token types with no string representation: %s".formatted(typeList));
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
                if (coverage - 0.9 > EPSILON) {
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

        return codeLines.boxed().toList();

    }

    /**
     * Confirms that all Token types are 'reachable' with a complete code example.
     * @param tokens TokenList which is supposed to contain all types of tokens
     * @param fileName The file name of the complete code example
     */
    private void testTokenCoverage(TokenList tokens, String fileName) {
        var foundTokens = StreamSupport.stream(tokens.allTokens().spliterator(), true).mapToInt(Token::getType).distinct().boxed().toList();
        var allTokens = IntStream.range(0, ScalaTokenConstants.numberOfTokens()).boxed().toList();
        allTokens = new ArrayList<>(allTokens);

        // Only non-found tokens are left
        allTokens.removeAll(foundTokens);
        // Exclude SEPARATOR_TOKEN, as it does not occur
        allTokens.remove((Integer) (TokenConstants.SEPARATOR_TOKEN));

        if (!allTokens.isEmpty()) {
            var notFoundTypes = allTokens.stream().map(ScalaTokenConstants::apply).toList();
            fail("Some %d token types were not found in the complete code example '%s':\n%s".formatted(notFoundTypes.size(), fileName,
                    notFoundTypes));
        }

    }

}