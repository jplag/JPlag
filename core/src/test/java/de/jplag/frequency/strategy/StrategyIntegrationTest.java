package de.jplag.frequency.strategy;

import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jplag.JPlagComparison;
import de.jplag.JPlagResult;
import de.jplag.SubmissionSet;
import de.jplag.SubmissionSetBuilder;
import de.jplag.TestBase;
import de.jplag.TokenType;
import de.jplag.comparison.LongestCommonSubsequenceSearch;
import de.jplag.exceptions.ExitException;
import de.jplag.options.JPlagOptions;

/**
 * Test class to validate the integration of the FrequencyStrategies using test code from the PartialPlagiarism sample.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class StrategyIntegrationTest extends TestBase {

    private static final int MIN_LENGTH = 300;
    private JPlagResult result;
    private static final Logger logger = LoggerFactory.getLogger(StrategyIntegrationTest.class);

    /**
     * Prepares the comparison result before running parameterized tests.
     * @throws ExitException if building the submission set or performing comparisons fails
     */
    @BeforeAll
    void prepareMatchResult() throws ExitException {
        JPlagOptions options = getDefaultOptions("PartialPlagiarism");
        SubmissionSetBuilder builder = new SubmissionSetBuilder(options);
        SubmissionSet submissionSet = builder.buildSubmissionSet();
        result = new LongestCommonSubsequenceSearch(options).compareSubmissions(submissionSet);
    }

    static Stream<Arguments> strategies() {
        return Stream.of(Arguments.of(new CompleteMatchesStrategy(), "completeMatches"),
                Arguments.of(new ContainedMatchesStrategy(MIN_LENGTH), "containedMatches"),
                Arguments.of(new SubMatchesStrategy(MIN_LENGTH), "subMatches"),
                Arguments.of(new WindowOfMatchesStrategy(MIN_LENGTH), "windowsOfMatches"));
    }

    @ParameterizedTest(name = "{1}")
    @MethodSource("strategies")
    @DisplayName("Each strategy produces a non-empty frequency map")
    void producesNonEmptyFrequencyMap(FrequencyStrategy strategy, String name) {
        List<JPlagComparison> comparisons = result.getAllComparisons();
        strategy.processMatches(comparisons);
        Map<List<TokenType>, Integer> tokenFrequencyMap = strategy.getResult();
        assertFalse(tokenFrequencyMap.isEmpty(), name + ": frequency map should not be empty");
        printTestResult(tokenFrequencyMap);
    }

    /**
     * Logs the frequency map with visualization.
     * @param tokenFrequencyMap a map where keys are TokenType hash values and values are their frequencies.
     */
    void printTestResult(Map<List<TokenType>, Integer> tokenFrequencyMap) {
        StringJoiner joiner = new StringJoiner(System.lineSeparator());
        joiner.add("");
        joiner.add("HashValue                       | Frequency | Histogram");
        joiner.add("---------------------------------------------------------------");

        for (Map.Entry<List<TokenType>, Integer> entry : tokenFrequencyMap.entrySet()) {
            String key = entry.getKey().toString();
            int count = entry.getValue();
            joiner.add(String.format("%-32.30s | %9d | %s", key, count, "*".repeat(Math.min(count, 50))));
        }
        logger.info(joiner.toString());
    }
}
