package de.jplag.highlightextraction.strategy;

import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
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
    /**
     * Minimum length of a submatch or a match window.
     */
    private static final int MIN_LENGTH = 300;
    /**
     * Stores the result of the pairwise comparison of submissions, used in all test methods.
     */
    private static JPlagResult result;
    /**
     * Logger for outputting test information.
     */
    private static final Logger logger = LoggerFactory.getLogger(StrategyIntegrationTest.class);

    /**
     * Prepares the comparison result before each test.
     * @throws ExitException if building the submissionSet or the comparisons fails.
     */
    @BeforeAll
    void prepareMatchResult() throws ExitException {
        JPlagOptions options = getDefaultOptions("PartialPlagiarism");
        SubmissionSetBuilder builder = new SubmissionSetBuilder(options);
        SubmissionSet submissionSet = builder.buildSubmissionSet();
        LongestCommonSubsequenceSearch strategy = new LongestCommonSubsequenceSearch(options);
        result = strategy.compareSubmissions(submissionSet);
    }

    /**
     * Tests frequency determination using the CompleteMatchesStrategy.
     */
    @Test
    @DisplayName("Test match frequency completeMatches strategy")
    void testFrequencyAnalysisStrategiesCompleteMatches() {
        FrequencyStrategy strategy = new CompleteMatchesStrategy();
        List<JPlagComparison> comparisons = result.getAllComparisons();
        strategy.processMatches(comparisons);
        Map<List<TokenType>, Integer> tokenFrequencyMap = strategy.getResult();
        assertFalse(tokenFrequencyMap.isEmpty(), "Map should not be empty");
        printTestResult(tokenFrequencyMap);
    }

    /**
     * Tests frequency determination using the ContainedMatchesStrategy.
     */
    @Test
    @DisplayName("Test match frequency with containedMatches strategy")
    void testFrequencyAnalysisStrategiesContainedMatches() {
        FrequencyStrategy strategy = new ContainedMatchesStrategy(MIN_LENGTH);
        List<JPlagComparison> comparisons = result.getAllComparisons();
        strategy.processMatches(comparisons);
        Map<List<TokenType>, Integer> tokenFrequencyMap = strategy.getResult();
        assertFalse(tokenFrequencyMap.isEmpty(), "Map should not be empty");
        printTestResult(tokenFrequencyMap);
    }

    /**
     * Tests frequency determination using the SubMatchesStrategy.
     */
    @Test
    @DisplayName("Test match frequency with subMatches strategy")
    void testFrequencyAnalysisStrategiesSubMatches() {
        FrequencyStrategy strategy = new SubmatchesStrategy(MIN_LENGTH);
        List<JPlagComparison> comparisons = result.getAllComparisons();
        strategy.processMatches(comparisons);
        Map<List<TokenType>, Integer> tokenFrequencyMap = strategy.getResult();
        assertFalse(tokenFrequencyMap.isEmpty(), "Map should not be empty");
        printTestResult(tokenFrequencyMap);
    }

    /**
     * Tests frequency determination using the WindowOfMatchesStrategy.
     */
    @Test
    @DisplayName("Test match frequency with windows of Matches strategy")
    void testFrequencyAnalysisStrategiesWindowOfMatches() {
        FrequencyStrategy strategy = new WindowOfMatchesStrategy(MIN_LENGTH);
        List<JPlagComparison> comparisons = result.getAllComparisons();
        strategy.processMatches(comparisons);
        Map<List<TokenType>, Integer> tokenFrequencyMap = strategy.getResult();
        assertFalse(tokenFrequencyMap.isEmpty(), "Map should not be empty");
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
