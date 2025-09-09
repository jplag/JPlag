package de.jplag.testutils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertLinesMatch;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jplag.Language;
import de.jplag.ParsingException;
import de.jplag.SharedTokenType;
import de.jplag.Token;
import de.jplag.TokenPrinter;
import de.jplag.TokenType;
import de.jplag.testutils.datacollector.TestData;
import de.jplag.testutils.datacollector.TestDataCollector;
import de.jplag.testutils.datacollector.TestSourceIgnoredLinesCollector;
import de.jplag.testutils.datacollector.TokenPositionTestData;

/**
 * Base class for language module tests. Automatically adds all common tests types for jplag languages.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class LanguageModuleTest {
    private static final Path DEFAULT_TEST_CODE_PATH_BASE = Path.of("src", "test", "resources", "de", "jplag");

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final TestDataCollector collector;
    private final Language language;
    private final List<TokenType> languageTokens;

    /**
     * Creates a new language module test.
     * @param language The language to test
     * @param languageTokens All tokens, that can be reported by the module. The end file token can be omitted.
     */
    public LanguageModuleTest(Language language, List<TokenType> languageTokens) {
        this.language = language;
        this.languageTokens = languageTokens;
        this.collector = new TestDataCollector(this.getTestFileLocation());
    }

    /**
     * Creates a new language module test.
     * @param language The language to test
     * @param languageTokens All tokens, that can be reported by the module. The end file token can be omitted.
     */
    public LanguageModuleTest(Language language, TokenType[] languageTokens) {
        this(language, Arrays.asList(languageTokens));
    }

    /**
     * Creates a new language module test.
     * @param <T> the enum type implementing {@link TokenType}.
     * @param language the language under test.
     * @param tokenEnum the enum class representing token types.
     */
    public <T extends Enum<T> & TokenType> LanguageModuleTest(Language language, Class<T> tokenEnum) {
        this(language, tokenEnum.getEnumConstants());
    }

    /**
     * Test the configured source files for source line coverage.
     * @param data The source to check
     * @throws ParsingException If the parser throws some error
     * @throws IOException If any IO Exception occurs
     */
    @ParameterizedTest
    @MethodSource("sourceCoverageFiles")
    @DisplayName("Test that every line leads to at least one token")
    final void testSourceCoverage(TestData data) throws ParsingException, IOException {
        List<Token> tokens = parseTokens(data);

        TestSourceIgnoredLinesCollector ignoredLines = new TestSourceIgnoredLinesCollector(data.getSourceLines());
        ignoredLines.ignoreEmptyLines();
        this.configureIgnoredLines(ignoredLines);
        List<Integer> relevantLines = new ArrayList<>(ignoredLines.getRelevantLines());

        tokens.stream().flatMap(t -> IntStream.rangeClosed(t.getStartLine(), t.getEndLine()).boxed()).forEach(relevantLines::remove);

        assertTrue(relevantLines.isEmpty(),
                "Test test source " + data.describeTestSource() + " contained uncovered lines:" + System.lineSeparator() + relevantLines);
    }

    /**
     * Returns all test sources, that need to be checked for source line coverage.
     * @return The test sources
     */
    final List<TestData> sourceCoverageFiles() {
        return ignoreEmptyTestType(this.collector.getSourceCoverageData());
    }

    /**
     * Checks the configured source files for token coverage.
     * @param data The source to check
     * @throws ParsingException If the parser throws some error
     * @throws IOException If any IO Exception occurs
     */
    @ParameterizedTest
    @MethodSource("tokenCoverageFiles")
    @DisplayName("Test that every token occurs at least once")
    final void testTokenCoverage(TestData data) throws ParsingException, IOException {
        List<TokenType> actualTokens = extractTokenTypes(data);
        List<TokenType> languageTokens = new ArrayList<>(this.languageTokens);

        languageTokens.removeAll(actualTokens);

        assertTrue(languageTokens.isEmpty(), "Some tokens were not found in " + data.describeTestSource() + System.lineSeparator() + languageTokens);
    }

    /**
     * Returns all test sources, that need to be checked for token coverage.
     * @return The test sources
     */
    final List<TestData> tokenCoverageFiles() {
        return ignoreEmptyTestType(this.collector.getTokenCoverageData());
    }

    /**
     * Tests the configured test sources for contained tokens. The tokens neither have to occur exclusively nor in the given
     * order.
     * @param test The source to test
     * @throws ParsingException If the parser throws some error
     * @throws IOException If any IO Exception occurs
     */
    @ParameterizedTest
    @MethodSource("testTokensContainedData")
    @DisplayName("Test that the specified tokens at least occur")
    final void testTokensContained(TestDataCollector.TokenListTest test) throws ParsingException, IOException {
        List<TokenType> actualTokens = extractTokenTypes(test.data());
        List<TokenType> expectedTokens = new ArrayList<>(test.tokens());

        for (TokenType foundToken : actualTokens) {
            expectedTokens.remove(foundToken);
        }

        assertTrue(expectedTokens.isEmpty(),
                "Some expected tokens were not found in " + test.data().describeTestSource() + System.lineSeparator() + expectedTokens);
    }

    /**
     * Returns all test sources, that need to be checked for contained tokens.
     * @return The test sources
     */
    final List<TestDataCollector.TokenListTest> testTokensContainedData() {
        return ignoreEmptyTestType(this.collector.getContainedTokenData());
    }

    /**
     * Checks the given test sources for an exact token sequence.
     * @param test The source to check
     * @throws ParsingException If the parser throws some error
     * @throws IOException If any IO Exception occurs
     */
    @ParameterizedTest
    @MethodSource("testTokenSequenceData")
    @DisplayName("Test if extracted token sequence matches")
    final void testTokenSequence(TestDataCollector.TokenListTest test) throws ParsingException, IOException {
        List<TokenType> actual = extractTokenTypes(test.data());
        List<TokenType> expected = new ArrayList<>(test.tokens());
        if (expected.getLast() != SharedTokenType.FILE_END) {
            expected.add(SharedTokenType.FILE_END);
        }
        assertTokensMatch(expected, actual, "Extracted token from " + test.data().describeTestSource() + " does not match expected sequence.");
        assertIterableEquals(expected, actual);
    }

    /**
     * Convenience method for using assertLinesMatch with token lists.
     */
    private void assertTokensMatch(List<TokenType> expected, List<TokenType> actual, String message) {
        assertLinesMatch(expected.stream().map(Object::toString), actual.stream().map(Object::toString), message);
    }

    /**
     * Returns all test sources, that need to be checked for a matching token sequence.
     * @return The test sources
     */
    final List<TestDataCollector.TokenListTest> testTokenSequenceData() {
        return ignoreEmptyTestType(this.collector.getTokenSequenceTest());
    }

    /**
     * Tests if the tokens specified for the token position tests are present in the sources.
     * @param testData The specifications of the expected tokens and the test source
     * @throws ParsingException If the parsing fails
     * @throws IOException If IO operations fail. If this happens, that should be unrelated to the test itself.
     */
    @ParameterizedTest
    @MethodSource("getTokenPositionTestData")
    @DisplayName("Tests if the extracted tokens contain the tokens specified in the test files.")
    final void testTokenPositions(TokenPositionTestData testData) throws ParsingException, IOException {
        List<Token> extractedTokens = parseTokens(testData);
        List<TokenPositionTestData.TokenData> failedTokens = new ArrayList<>();

        for (TokenPositionTestData.TokenData expectedToken : testData.getExpectedTokens()) {
            TokenType expectedType = this.languageTokens.stream().filter(type -> type.toString().equals(expectedToken.typeName())).findFirst()
                    .orElseThrow(() -> new IOException(String.format("The token type %s does not exist.", expectedToken.typeName())));

            if (extractedTokens.stream().noneMatch(token -> testTokenMatch(token, expectedType, expectedToken))) {
                failedTokens.add(expectedToken);
            }
        }

        if (!failedTokens.isEmpty()) {
            String failureDescriptors = String.join(System.lineSeparator(), failedTokens.stream().map(token -> token.typeName() + " at ("
                    + token.startLine() + ":" + token.startColumn() + ") to (" + token.endLine() + ":" + token.endColumn() + ")").toList());
            fail("Some tokens weren't extracted with the correct properties:" + System.lineSeparator() + failureDescriptors);
        }
    }

    private boolean testTokenMatch(Token token, TokenType expectedType, TokenPositionTestData.TokenData expectedToken) {
        boolean typeMatch = token.getType() == expectedType;
        boolean startPositionMatch = token.getStartLine() == expectedToken.startLine() && token.getStartColumn() == expectedToken.startColumn();
        boolean endPositionMatch = token.getEndLine() == expectedToken.endLine() && token.getEndColumn() == expectedToken.endColumn();
        return typeMatch && startPositionMatch && endPositionMatch;
    }

    /**
     * @return All token positions tests that are configured
     */
    final List<TokenPositionTestData> getTokenPositionTestData() {
        return ignoreEmptyTestType(this.collector.getTokenPositionTestData());
    }

    /**
     * Tests all configured test sources for a monotone order of tokens.
     * @param data The test source
     * @throws ParsingException If the parser throws some error
     * @throws IOException If any IO Exception occurs
     */
    @ParameterizedTest
    @MethodSource("getAllTestData")
    @DisplayName("Test that the tokens map to ascending line numbers")
    final void testMonotoneTokenOrder(TestData data) throws ParsingException, IOException {
        List<Token> tokens = parseTokens(data).stream().filter(it -> !getIgnoredTokensForMonotoneTokenOrder().contains(it.getType())).toList();

        for (int i = 0; i < tokens.size() - 2; i++) {
            Token first = tokens.get(i);
            Token second = tokens.get(i + 1);

            if (first.getStartLine() > second.getStartLine()) {
                fail(String.format("Invalid token order. Token %s (%s:%s) comes after %s (%s:%s)", first.getType(), first.getStartLine(),
                        first.getStartColumn(), second.getType(), second.getStartLine(), second.getStartColumn()));
            }
        }
    }

    /**
     * Checks that all configured test sources end with a FileEnd token.
     * @param data The test source
     * @throws ParsingException If the parser throws some error
     * @throws IOException If any IO Exception occurs
     */
    @ParameterizedTest
    @MethodSource("getAllTestData")
    @DisplayName("Test that the last token is the file end token")
    final void testTokenSequencesEndsWithFileEnd(TestData data) throws ParsingException, IOException {
        List<Token> tokens = parseTokens(data);

        assertEquals(SharedTokenType.FILE_END, tokens.getLast().getType(), "Last token in " + data.describeTestSource() + " is not file end.");
    }

    /**
     * Returns all configured test sources.
     * @return The test sources
     */
    final List<TestData> getAllTestData() {
        return ignoreEmptyTestType(this.collector.getAllTestData());
    }

    /**
     * Collects the test sources.
     */
    @BeforeAll
    final void collectTestData() {
        collectTestData(this.collector);
    }

    @AfterAll
    final void deleteTemporaryFiles() {
        TemporaryFileHolder.deleteTemporaryFiles();
    }

    private List<Token> parseTokens(TestData source) throws ParsingException, IOException {
        List<Token> tokens = source.parseTokens(this.language);
        logger.info(TokenPrinter.printTokens(tokens));
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
    protected abstract void collectTestData(TestDataCollector collector);

    /**
     * Configure which lines should not be checked for source coverage.
     * @param collector Used to ignore lines
     */
    protected abstract void configureIgnoredLines(TestSourceIgnoredLinesCollector collector);

    /**
     * Returns the default directory structure by default.
     * @return The test file location
     */
    protected File getTestFileLocation() {
        return new File(DEFAULT_TEST_CODE_PATH_BASE.toFile(), this.language.getIdentifier());
    }

    protected List<TokenType> getIgnoredTokensForMonotoneTokenOrder() {
        return Collections.emptyList();
    }
}
