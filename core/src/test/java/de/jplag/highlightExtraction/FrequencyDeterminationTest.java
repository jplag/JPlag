package de.jplag.highlightExtraction;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import de.jplag.JPlagResult;
import de.jplag.SubmissionSet;
import de.jplag.SubmissionSetBuilder;
import de.jplag.TestBase;
import de.jplag.comparison.LongestCommonSubsquenceSearch;
import de.jplag.exceptions.ExitException;
import de.jplag.options.JPlagOptions;

public class FrequencyDeterminationTest extends TestBase {

    private static JPlagOptions options;
    private static LongestCommonSubsquenceSearch comparisonStrategy;
    private static SubmissionSet submissionSet;
    private FrequencyDetermination fd = new FrequencyDetermination();
    private static JPlagResult result;

    @Test
    @BeforeEach
    void prepareMatchResult() throws ExitException {

        JPlagOptions options = getDefaultOptions("PartialPlagiarism"); // getDefaultOptions("merging");
        System.out.println(options);
        SubmissionSetBuilder builder = new SubmissionSetBuilder(options);
        submissionSet = builder.buildSubmissionSet();
        comparisonStrategy = new LongestCommonSubsquenceSearch(options);
        result = comparisonStrategy.compareSubmissions(submissionSet);
        System.out.println("result: !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        System.out.println(result);
    }

    @Test
    @DisplayName("Test token frequency completeMatches")
    void testFrequencyAnalysisStrategiesCompleteMatches() throws Exception {
        fd.frequencyAnalysisStrategies(result.getAllComparisons(), FrequencyStrategies.COMPLETEMATCHES, 1);
        System.out.println(fd);
        Map<String, List<String>> tokenFrequencyMap = fd.getTokenFrequencyMap();
        printTestResult(tokenFrequencyMap);
    }

    @Test
    @DisplayName("Test token frequency with containedMatches")
    void testFrequencyAnalysisStrategiesContainedMatches() throws Exception {
        fd.frequencyAnalysisStrategies(result.getAllComparisons(), FrequencyStrategies.CONTAINEDMATCHES, 300);
        System.out.println(fd);
        Map<String, List<String>> tokenFrequencyMap = fd.getTokenFrequencyMap();
        printTestResult(tokenFrequencyMap);
    }

    @Test
    @DisplayName("Test token frequency with subMatches")
    void testFrequencyAnalysisStrategiesSubMatches() throws Exception {
        fd.frequencyAnalysisStrategies(result.getAllComparisons(), FrequencyStrategies.SUBMATCHES, 300);
        System.out.println(fd);
        Map<String, List<String>> tokenFrequencyMap = fd.getTokenFrequencyMap();
        printTestResult(tokenFrequencyMap);
    }

    @Test
    @DisplayName("Test token frequency with windows of Matches")
    void testFrequencyAnalysisStrategiesWindowOfMatches() throws Exception {
        fd.frequencyAnalysisStrategies(result.getAllComparisons(), FrequencyStrategies.WINDOWOFMATCHES, 300);
        System.out.println(fd);
        Map<String, List<String>> tokenFrequencyMap = fd.getTokenFrequencyMap();
        printTestResult(tokenFrequencyMap);
    }

    void printTestResult(Map<String, List<String>> tokenFrequencyMap) {
        System.out.println("\nToken-HäufigkeitsHistogramm:");
        for (Map.Entry<String, List<String>> myEntry : tokenFrequencyMap.entrySet()) {
            String key = myEntry.getKey();
            int count = myEntry.getValue().size();
            String id = myEntry.getValue().toString();
            System.out.printf("Tokens: [%.30s...] | Häufigkeit: %2d | %s%n | %s \n", key, count, "*".repeat(Math.min(count, 50)), id);
        }
    }
}
