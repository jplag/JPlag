package de.jplag.highlightExtraction;

import de.jplag.*;
import de.jplag.comparison.LongestCommonSubsquenceSearch;
import de.jplag.exceptions.ExitException;
import de.jplag.options.JPlagOptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class FrequencyDeterminationStrategyTest extends TestBase {
    private static JPlagResult result;
    private static FrequencyDeterminationTest t = new FrequencyDeterminationTest();
    private static Match baseMatchA;
    private static Submission baseAFirst;
    private static Submission baseASecond;
    private static List<Match> baseAIgnoredMatches;
    private static Match baseMatchB;
    private static Match baseMatchC;
    private static Match baseMatchD;
    private static Match baseMatchE;

    List<Match> testMatches = new LinkedList<Match>();



    @Test
    //@BeforeEach
    void prepareMatchResult() throws ExitException {

        JPlagOptions options = getDefaultOptions("PartialPlagiarism"); // getDefaultOptions("merging");
        System.out.println(options);
        SubmissionSetBuilder builder = new SubmissionSetBuilder(options);
        SubmissionSet submissionSet = builder.buildSubmissionSet();
        LongestCommonSubsquenceSearch strategy = new LongestCommonSubsquenceSearch(options);
        result = strategy.compareSubmissions(submissionSet);
        System.out.println("result: !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        System.out.println(result);

        for (JPlagComparison comparison : result.getAllComparisons()) {
            System.out.println(comparison);
            if (comparison.matches() != null) {
                baseMatchA = comparison.matches().getFirst();
                baseAFirst = comparison.firstSubmission();
                baseASecond = comparison.secondSubmission();
                baseAIgnoredMatches = comparison.ignoredMatches();
                break;
            }
        }
        testMatches.add(baseMatchA);
        List<Match> testBaseA = new ArrayList<Match>();
        testBaseA.add(baseMatchA);
        System.out.println("testMatches: !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        System.out.println(testMatches);
        JPlagComparison comparisonContainingAB = new JPlagComparison(baseAFirst, baseASecond, testBaseA, baseAIgnoredMatches);
        System.out.println(comparisonContainingAB);
        System.out.println(comparisonContainingAB.matches());
        System.out.println(comparisonContainingAB.maximalSimilarity());
    }

    @Test
    @DisplayName("Test token frequency with subMatches")
    void testFrequencyAnalysisStrategiesSubMatches() {
        FrequencyStrategy strategy = new SubMatchesStrategy();
        FrequencyDetermination fd = new FrequencyDetermination(strategy, 300);
        fd.runAnalysis(result.getAllComparisons());
        System.out.println(fd);
        Map<String, List<String>> tokenFrequencyMap = fd.getTokenFrequencyMap();
        t.printTestResult(tokenFrequencyMap);
    }


    @Test
    @DisplayName("Test token frequency completeMatches")
    void testFrequencyAnalysisStrategiesCompleteMatches() {
        FrequencyStrategy strategy = new CompleteMatchesStrategy();
        FrequencyDetermination fd = new FrequencyDetermination(strategy, 1);
        fd.runAnalysis(result.getAllComparisons());
        System.out.println(fd);
        Map<String, List<String>> tokenFrequencyMap = fd.getTokenFrequencyMap();
        t.printTestResult(tokenFrequencyMap);
    }


}
