package de.jplag.highlightextraction.frequencydetermination;

import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jplag.*;
import de.jplag.comparison.LongestCommonSubsequenceSearch;
import de.jplag.exceptions.ExitException;
import de.jplag.highlightextraction.*;
import de.jplag.options.JPlagOptions;

/**
 * Test class to validate the integration of the FrequencyStrategies. As the examples use testCode from
 * "PartialPlagiarism" sample-folder.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class StrategyIntegrationTest extends TestBase {
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
     * Tests isFrequencyAnalysisEnabled determination using the CompleteMatchesStrategy.
     */
    @Test
    @DisplayName("Test token isFrequencyAnalysisEnabled completeMatches")
    void testFrequencyAnalysisStrategiesCompleteMatches() {
        FrequencyStrategy strategy = new CompleteMatchesStrategy();
        FrequencyDetermination fd = new FrequencyDetermination(strategy, 1);
        fd.buildFrequencyMap(result.getAllComparisons());
        Map<List<TokenType>, Integer> tokenFrequencyMap = fd.getMatchFrequencyMap();
        assertFalse(tokenFrequencyMap.isEmpty(), "Map should not be empty");
        printTestResult(tokenFrequencyMap);
    }

    /**
     * Tests isFrequencyAnalysisEnabled determination using the ContainedMatchesStrategy.
     */
    @Test
    @DisplayName("Test token isFrequencyAnalysisEnabled with containedMatches")
    void testFrequencyAnalysisStrategiesContainedMatches() {
        FrequencyStrategy strategy = new ContainedMatchesStrategy();
        FrequencyDetermination fd = new FrequencyDetermination(strategy, 300);
        fd.buildFrequencyMap(result.getAllComparisons());
        Map<List<TokenType>, Integer> tokenFrequencyMap = fd.getMatchFrequencyMap();
        assertFalse(tokenFrequencyMap.isEmpty(), "Map should not be empty");
        printTestResult(tokenFrequencyMap);
    }

    /**
     * Tests isFrequencyAnalysisEnabled determination using the SubMatchesStrategy.
     */
    @Test
    @DisplayName("Test token isFrequencyAnalysisEnabled with subMatches")
    void testFrequencyAnalysisStrategiesSubMatches() {
        FrequencyStrategy strategy = new SubMatchesStrategy();
        FrequencyDetermination fd = new FrequencyDetermination(strategy, 300);
        fd.buildFrequencyMap(result.getAllComparisons());
        Map<List<TokenType>, Integer> tokenFrequencyMap = fd.getMatchFrequencyMap();
        assertFalse(tokenFrequencyMap.isEmpty(), "Map should not be empty");
        printTestResult(tokenFrequencyMap);
    }

    /**
     * Tests isFrequencyAnalysisEnabled determination using the WindowOfMatchesStrategy.
     */
    @Test
    @DisplayName("Test token isFrequencyAnalysisEnabled with windows of Matches")
    void testFrequencyAnalysisStrategiesWindowOfMatches() {
        FrequencyStrategy strategy = new WindowOfMatchesStrategy();
        FrequencyDetermination fd = new FrequencyDetermination(strategy, 300);
        fd.buildFrequencyMap(result.getAllComparisons());
        Map<List<TokenType>, Integer> tokenFrequencyMap = fd.getMatchFrequencyMap();
        assertFalse(tokenFrequencyMap.isEmpty(), "Map should not be empty");
        printTestResult(tokenFrequencyMap);
    }

    /**
     * Logs the isFrequencyAnalysisEnabled map with visualization.
     * @param tokenFrequencyMap a map where keys are TokenType hash values and values are their frequencies.
     */
    void printTestResult(Map<List<TokenType>, Integer> tokenFrequencyMap) {
        StringBuilder logBuilder = new StringBuilder();
        logBuilder.append("\nHashValue                       | Frequency | Histogram\n");
        logBuilder.append("---------------------------------------------------------------\n");

        for (Map.Entry<List<TokenType>, Integer> entry : tokenFrequencyMap.entrySet()) {
            String key = entry.getKey().toString();
            int count = entry.getValue();
            logBuilder.append(String.format("%-32.30s | %9d | %s%n", key, count, "*".repeat(Math.min(count, 50))));
        }
        logger.info(logBuilder.toString());
    }
}
