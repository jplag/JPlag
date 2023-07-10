package de.jplag.testutils;

import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import de.jplag.*;
import de.jplag.testutils.datacollector.TestData;
import de.jplag.testutils.datacollector.TestDataCollector;
import de.jplag.testutils.datacollector.TestSourceIgnoredLinesCollector;

/**
 * Base class for language module tests. Automatically adds all common tests types for jplag languages.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class LanguageModuleTest {
    private static final Path DEFAULT_TEST_CODE_PATH_BASE = Path.of("src", "test", "resources", "de", "jplag");

    private final Logger LOG = Logger.getLogger(this.getClass().getName());

    private final TestDataCollector collector;
    private final Language language;
    private final List<TokenType> languageTokens;

    /**
     * Creates a new language module test
     * @param language The language to test
     * @param languageTokens All tokens, that can be reported by the module. The end file token can be omitted.
     */
    public LanguageModuleTest(Language language, List<TokenType> languageTokens) {
        this.language = language;
        this.languageTokens = languageTokens;
        this.collector = new TestDataCollector(this.getTestFileLocation());
    }

    /**
     * Creates a new language module test
     * @param language The language to test
     * @param languageTokens All tokens, that can be reported by the module. The end file token can be omitted.
     */
    public LanguageModuleTest(Language language, TokenType[] languageTokens) {
        this(language, Arrays.asList(languageTokens));
    }

    /**
     * Creates a new language module test
     * @param language The language to test
     * @param tokenEnum The enum containing the token types
     */
    public <T extends Enum<T> & TokenType> LanguageModuleTest(Language language, Class<T> tokenEnum) {
        this(language, tokenEnum.getEnumConstants());
    }

    /**
     * Test the configured source files for source line coverage
     * @param data The source to check
     * @throws ParsingException If the parser throws some error
     * @throws IOException If any IO Exception occurs
     */
    @ParameterizedTest
    @MethodSource("sourceCoverageFiles")
    final void testSourceCoverage(TestData data) throws ParsingException, IOException {
        List<Token> tokens = parseTokens(data);

        TestSourceIgnoredLinesCollector ignoredLines = new TestSourceIgnoredLinesCollector(data.getSourceLines());
        ignoredLines.ignoreEmptyLines();
        this.configureIgnoredLines(ignoredLines);
        List<Integer> relevantLines = new ArrayList<>(ignoredLines.getRelevantLines());

        tokens.stream().map(Token::getLine).forEach(relevantLines::remove);

        Assertions.assertTrue(relevantLines.isEmpty(),
                "Test test source " + data.describeTestSource() + " contained uncovered lines:" + System.lineSeparator() + relevantLines);
    }

    /**
     * Returns all test sources, that need to be checked for source line coverage
     * @return The test sources
     */
    final List<TestData> sourceCoverageFiles() {
        return ignoreEmptyTestType(this.collector.getSourceCoverageData());
    }

    /**
     * Checks the configured source files for token coverage
     * @param data The source to check
     * @throws ParsingException If the parser throws some error
     * @throws IOException If any IO Exception occurs
     */
    @ParameterizedTest
    @MethodSource("tokenCoverageFiles")
    final void testTokenCoverage(TestData data) throws ParsingException, IOException {
        List<TokenType> foundTokens = extractTokenTypes(data);
        List<TokenType> languageTokens = new ArrayList<>(this.languageTokens);

        languageTokens.removeAll(foundTokens);

        Assertions.assertTrue(languageTokens.isEmpty(),
                "Some tokens were not found in " + data.describeTestSource() + System.lineSeparator() + languageTokens);
    }

    /**
     * Returns all test sources, that need to be checked for token coverage.
     * @return The test sources
     */
    final List<TestData> tokenCoverageFiles() {
        return ignoreEmptyTestType(this.collector.getTokenCoverageData());
    }

    /**
     * Tests the configured test sources for contained tokens
     * @param test The source to test
     * @throws ParsingException If the parser throws some error
     * @throws IOException If any IO Exception occurs
     */
    @ParameterizedTest
    @MethodSource("testTokensContainedData")
    final void testTokensContained(TestDataCollector.TokenListTest test) throws ParsingException, IOException {
        List<TokenType> foundTokens = extractTokenTypes(test.data());
        List<TokenType> requiredTokens = new ArrayList<>(test.tokens());

        for (TokenType foundToken : foundTokens) {
            requiredTokens.remove(foundToken);
        }

        Assertions.assertTrue(requiredTokens.isEmpty(),
                "Some required tokens were not found in " + test.data().describeTestSource() + System.lineSeparator() + requiredTokens);
    }

    /**
     * Returns all test sources, that need to be checked for contained tokens
     * @return The test sources
     */
    final List<TestDataCollector.TokenListTest> testTokensContainedData() {
        return ignoreEmptyTestType(this.collector.getContainedTokenData());
    }

    /**
     * Checks the given test sources for an exact token sequence
     * @param test The source to check
     * @throws ParsingException If the parser throws some error
     * @throws IOException If any IO Exception occurs
     */
    @ParameterizedTest
    @MethodSource("testTokenSequenceData")
    final void testTokenSequence(TestDataCollector.TokenListTest test) throws ParsingException, IOException {
        List<TokenType> extracted = extractTokenTypes(test.data());
        List<TokenType> required = new ArrayList<>(test.tokens());
        if (required.get(required.size() - 1) != SharedTokenType.FILE_END) {
            required.add(SharedTokenType.FILE_END);
        }

        Assertions.assertEquals(required, extracted,
                "Extracted token from " + test.data().describeTestSource() + " does not match required sequence.");
    }

    /**
     * Returns all test sources, that need to be checked for a matching token sequence
     * @return The test sources
     */
    final List<TestDataCollector.TokenListTest> testTokenSequenceData() {
        return ignoreEmptyTestType(this.collector.getTokenSequenceTest());
    }

    /**
     * Tests all configured test sources for a monotone order of tokens
     * @param data The test source
     * @throws ParsingException If the parser throws some error
     * @throws IOException If any IO Exception occurs
     */
    @ParameterizedTest
    @MethodSource("getAllTestData")
    final void testMonotoneTokenOrder(TestData data) throws ParsingException, IOException {
        List<Token> extracted = parseTokens(data);

        for (int i = 0; i < extracted.size() - 2; i++) {
            Token first = extracted.get(i);
            Token second = extracted.get(i + 1);

            if (first.getLine() > second.getLine()) {
                fail(String.format("Invalid token order. Token %s has a higher line number (%s) than token %s (%s).", first.getType(),
                        first.getLine(), second.getType(), second.getLine()));
            }
        }
    }

    /**
     * Checks that all configured test sources end with a FileEnd token
     * @param data The test source
     * @throws ParsingException If the parser throws some error
     * @throws IOException If any IO Exception occurs
     */
    @ParameterizedTest
    @MethodSource("getAllTestData")
    final void testTokenSequencesEndsWithFileEnd(TestData data) throws ParsingException, IOException {
        List<Token> extracted = parseTokens(data);

        Assertions.assertEquals(SharedTokenType.FILE_END, extracted.get(extracted.size() - 1).getType(),
                "Last token in " + data.describeTestSource() + " is not file end.");
    }

    /**
     * Returns all configured test sources
     * @return The test sources
     */
    final List<TestData> getAllTestData() {
        return ignoreEmptyTestType(this.collector.getAllTestData());
    }

    /**
     * Collects the test sources
     */
    @BeforeAll
    final void collectTestData() {
        collectTestData(this.collector);
    }

    private List<Token> parseTokens(TestData source) throws ParsingException, IOException {
        List<Token> tokens = source.parseTokens(this.language);
        LOG.log(Level.INFO, TokenPrinter.printTokens(tokens));
        return tokens;
    }

    private List<TokenType> extractTokenTypes(TestData source) throws ParsingException, IOException {
        List<Token> tokens = parseTokens(source);
        return tokens.stream().map(Token::getType).toList();
    }

    /**
     * Ignores the test, if there is no data, by failing an assumption.
     * @param data The list containing the test data
     * @param <T> The type of items
     * @param <C> The collection type
     * @return The data
     */
    private <T, C extends Collection<T>> C ignoreEmptyTestType(C data) {
        Assumptions.assumeFalse(data.isEmpty(), "Ignoring empty test type.");
        return data;
    }

    /**
     * Collects all tests, that should be executed.
     * @param collector Use to collect the tests
     */
    abstract protected void collectTestData(TestDataCollector collector);

    /**
     * Configure which lines should not be checked for source coverage.
     * @param collector Used to ignore lines
     */
    abstract protected void configureIgnoredLines(TestSourceIgnoredLinesCollector collector);

    /**
     * Returns the default directory structure by default.
     * @return The test file location
     */
    protected File getTestFileLocation() {
        return new File(DEFAULT_TEST_CODE_PATH_BASE.toFile(), this.language.getIdentifier());
    }
}
