package de.jplag.highlightExtraction;

import java.util.List;
import java.util.Map;

import de.jplag.TestBase;
import de.jplag.comparison.LongestCommonSubsquenceSearch;
import de.jplag.options.JPlagOptions;
import org.junit.jupiter.api.*;
import de.jplag.JPlagResult;
import de.jplag.SubmissionSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import de.jplag.SubmissionSetBuilder;
import de.jplag.exceptions.ExitException;


public class FrequencyDeterminationTest extends TestBase {

    private static JPlagOptions options;
    private static LongestCommonSubsquenceSearch comparisonStrategy;
    private static SubmissionSet submissionSet;
    private FrequencyDetermination fd = new FrequencyDetermination();
    private static JPlagResult result;

    @Test
    @BeforeEach
    void prepareMatchResult() throws ExitException {

        JPlagOptions options = getDefaultOptions("PartialPlagiarism"); //getDefaultOptions("merging");
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
        fd.frequencyAnalysisStrategies(result.getAllComparisons(), FrequencyStrategies.completeMatches);
        System.out.println(fd);
        Map<String, List<String>> tokenFrequencyMap = fd.getTokenFrequencyMap();
        printTestresult(tokenFrequencyMap);
        }

    @Test
    @DisplayName("Test token frequency with containedMatches")
    void testFrequencyAnalysisStrategiesContainedMatches() throws Exception {
        fd.frequencyAnalysisStrategies(result.getAllComparisons(), FrequencyStrategies.containedMatches);
        System.out.println(fd);
        Map<String, List<String>> tokenFrequencyMap = fd.getTokenFrequencyMap();
        printTestresult(tokenFrequencyMap);
    }

    @Test
    @DisplayName("Test token frequency with subMatches")
    void testFrequencyAnalysisStrategiesSubMatches() throws Exception {
        fd.frequencyAnalysisStrategies(result.getAllComparisons(), FrequencyStrategies.subMatches);
        System.out.println(fd);
        Map<String, List<String>> tokenFrequencyMap = fd.getTokenFrequencyMap();
        printTestresult(tokenFrequencyMap);
    }

    @Test
    @DisplayName("Test token frequency with windows of Matches")
    void testFrequencyAnalysisStrategiesWindowOfMatches() throws Exception {
        fd.frequencyAnalysisStrategies(result.getAllComparisons(), FrequencyStrategies.windowOfMatches);
        System.out.println(fd);
        Map<String, List<String>> tokenFrequencyMap = fd.getTokenFrequencyMap();
        printTestresult(tokenFrequencyMap);
    }



    void printTestresult(Map<String, List<String>> tokenFrequencyMap){
        System.out.println("\nToken-Häufigkeitshistogramm:");
        for (Map.Entry<String, List<String>> entry : tokenFrequencyMap.entrySet()) {
            String key = entry.getKey();
            int count = entry.getValue().size();
            System.out.printf("Tokens: [%.30s...] | Häufigkeit: %2d | %s%n",
                    key, count, "*".repeat(Math.min(count, 50)));
    }
}
}
