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

        JPlagOptions options = getDefaultOptions("merging");
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
    void testFrequencyAnalysisStrategiesTokenFrequencyAndHistogram() throws Exception {
        fd.frequencyAnalysisStrategies(result.getAllComparisons());
        System.out.println(fd);
        Map<String, List<String>> tokenFrequencyMap = fd.getTokenFrequencyMap();
        System.out.println("\nToken-Häufigkeitshistogramm:");
        for (Map.Entry<String, List<String>> entry : tokenFrequencyMap.entrySet()) {
            String key = entry.getKey();
            int count = entry.getValue().size();
            System.out.printf("Tokens: [%.30s...] | Häufigkeit: %2d | %s%n",
                    key, count, "*".repeat(Math.min(count, 50)));
        }
    }
}
