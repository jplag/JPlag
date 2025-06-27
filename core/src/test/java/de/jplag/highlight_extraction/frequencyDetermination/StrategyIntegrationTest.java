package de.jplag.highlight_extraction.frequencyDetermination;

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
import de.jplag.highlight_extraction.*;
import de.jplag.options.JPlagOptions;

class StrategyIntegrationTest extends TestBase {

    private static JPlagResult result;
    private static final Logger logger = LoggerFactory.getLogger(StrategyIntegrationTest.class);

    @BeforeEach
    void prepareMatchResult() throws ExitException {

        JPlagOptions options = getDefaultOptions("PartialPlagiarism");
        SubmissionSetBuilder builder = new SubmissionSetBuilder(options);
        SubmissionSet submissionSet = builder.buildSubmissionSet();
        LongestCommonSubsequenceSearch strategy = new LongestCommonSubsequenceSearch(options);
        result = strategy.compareSubmissions(submissionSet);
    }

    @Test
    @DisplayName("Test token frequency completeMatches")
    void testFrequencyAnalysisStrategiesCompleteMatches() {
        FrequencyStrategy strategy = new CompleteMatchesStrategy();
        FrequencyDetermination fd = new FrequencyDetermination(strategy, 1);
        fd.buildFrequencyMap(result.getAllComparisons());
        Map<List<TokenType>, Integer> tokenFrequencyMap = fd.getMatchFrequencyMap();
        assertFalse(tokenFrequencyMap.isEmpty(), "Map should not be empty");
        printTestResult(tokenFrequencyMap);
    }

    @Test
    @DisplayName("Test token frequency with containedMatches")
    void testFrequencyAnalysisStrategiesContainedMatches() {
        FrequencyStrategy strategy = new ContainedMatchesStrategy();
        FrequencyDetermination fd = new FrequencyDetermination(strategy, 300);
        fd.buildFrequencyMap(result.getAllComparisons());
        Map<List<TokenType>, Integer> tokenFrequencyMap = fd.getMatchFrequencyMap();
        assertFalse(tokenFrequencyMap.isEmpty(), "Map should not be empty");
        printTestResult(tokenFrequencyMap);
    }

    @Test
    @DisplayName("Test token frequency with subMatches")
    void testFrequencyAnalysisStrategiesSubMatches() {
        FrequencyStrategy strategy = new SubMatchesStrategy();
        FrequencyDetermination fd = new FrequencyDetermination(strategy, 300);
        fd.buildFrequencyMap(result.getAllComparisons());
        Map<List<TokenType>, Integer> tokenFrequencyMap = fd.getMatchFrequencyMap();
        assertFalse(tokenFrequencyMap.isEmpty(), "Map should not be empty");
        printTestResult(tokenFrequencyMap);
    }

    @Test
    @DisplayName("Test token frequency with windows of Matches")
    void testFrequencyAnalysisStrategiesWindowOfMatches() {
        FrequencyStrategy strategy = new WindowOfMatchesStrategy();
        FrequencyDetermination fd = new FrequencyDetermination(strategy, 300);
        fd.buildFrequencyMap(result.getAllComparisons());
        Map<List<TokenType>, Integer> tokenFrequencyMap = fd.getMatchFrequencyMap();
        assertFalse(tokenFrequencyMap.isEmpty(), "Map should not be empty");
        printTestResult(tokenFrequencyMap);
    }

    void printTestResult(Map<List<TokenType>, Integer> tokenFrequencyMap) {
        StringBuilder logBuilder = new StringBuilder();
        logBuilder.append("\nToken-String                       | Len | Frequency | Histogram\n");
        logBuilder.append("---------------------------------------------------------------\n");

        for (Map.Entry<List<TokenType>, Integer> entry : tokenFrequencyMap.entrySet()) {
            String key = entry.getKey().toString();
            int count = entry.getValue();
            int length = key.trim().isEmpty() ? 0 : key.trim().split("\\s+").length;
            String id = entry.getValue().toString();

            logBuilder.append(String.format("%-32.30s | %3d | %9d | %s\n    ↳ %s\n", key, length, count, "*".repeat(Math.min(count, 50)), id));
        }
        logger.info(logBuilder.toString());
    }
}
