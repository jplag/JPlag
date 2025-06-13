package de.jplag.highlight_extraction.frequencyDetermination;

import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import de.jplag.JPlagResult;
import de.jplag.SubmissionSet;
import de.jplag.SubmissionSetBuilder;
import de.jplag.TestBase;
import de.jplag.comparison.LongestCommonSubsequenceSearch;
import de.jplag.exceptions.ExitException;
import de.jplag.highlight_extraction.*;
import de.jplag.options.JPlagOptions;

public class StrategyIntegrationTest extends TestBase {

    private static JPlagResult result;

    @BeforeEach
    void prepareMatchResult() throws ExitException {

        JPlagOptions options = getDefaultOptions("PartialPlagiarism");
        System.out.println(options);
        SubmissionSetBuilder builder = new SubmissionSetBuilder(options);
        SubmissionSet submissionSet = builder.buildSubmissionSet();
        LongestCommonSubsequenceSearch strategy = new LongestCommonSubsequenceSearch(options);
        result = strategy.compareSubmissions(submissionSet);
        System.out.println("result: !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        System.out.println(result);
    }

    @Test
    @DisplayName("Test token frequency completeMatches")
    void testFrequencyAnalysisStrategiesCompleteMatches() {
        FrequencyStrategy strategy = new CompleteMatchesStrategy();
        FrequencyDetermination fd = new FrequencyDetermination(strategy, 1);
        fd.runAnalysis(result.getAllComparisons());
        System.out.println(fd);
        Map<String, List<String>> tokenFrequencyMap = fd.getTokenFrequencyMap();
        assertFalse(tokenFrequencyMap.isEmpty(), "Map should not be empty");
        printTestResult(tokenFrequencyMap);
    }

    @Test
    @DisplayName("Test token frequency with containedMatches")
    void testFrequencyAnalysisStrategiesContainedMatches() {
        FrequencyStrategy strategy = new ContainedStrategy();
        FrequencyDetermination fd = new FrequencyDetermination(strategy, 300);
        fd.runAnalysis(result.getAllComparisons());
        System.out.println(fd);
        Map<String, List<String>> tokenFrequencyMap = fd.getTokenFrequencyMap();
        assertFalse(tokenFrequencyMap.isEmpty(), "Map should not be empty");
        printTestResult(tokenFrequencyMap);
    }

    @Test
    @DisplayName("Test token frequency with subMatches")
    void testFrequencyAnalysisStrategiesSubMatches() {
        FrequencyStrategy strategy = new SubMatchesStrategy();
        FrequencyDetermination fd = new FrequencyDetermination(strategy, 300);
        fd.runAnalysis(result.getAllComparisons());
        System.out.println(fd);
        Map<String, List<String>> tokenFrequencyMap = fd.getTokenFrequencyMap();
        assertFalse(tokenFrequencyMap.isEmpty(), "Map should not be empty");
        printTestResult(tokenFrequencyMap);
    }

    @Test
    @DisplayName("Test token frequency with windows of Matches")
    void testFrequencyAnalysisStrategiesWindowOfMatches() {
        FrequencyStrategy strategy = new WindowOfMatchesStrategy();
        FrequencyDetermination fd = new FrequencyDetermination(strategy, 300);
        fd.runAnalysis(result.getAllComparisons());
        System.out.println(fd);
        Map<String, List<String>> tokenFrequencyMap = fd.getTokenFrequencyMap();
        assertFalse(tokenFrequencyMap.isEmpty(), "Map should not be empty");
        printTestResult(tokenFrequencyMap);
    }

    void printTestResult(Map<String, List<String>> tokenFrequencyMap) {
        System.out.println("\nToken-Frequency:");
        for (Map.Entry<String, List<String>> myEntry : tokenFrequencyMap.entrySet()) {
            String key = myEntry.getKey();
            int count = myEntry.getValue().size();
            String id = myEntry.getValue().toString();
            System.out.printf("Tokens: [%.30s...] | Frequency: %2d | %s\n | %s \n", key, count, "*".repeat(Math.min(count, 50)), id);
        }
    }

}
