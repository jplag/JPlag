package de.jplag.highlightextraction.frequencysimilarity;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import de.jplag.*;
import de.jplag.comparison.LongestCommonSubsequenceSearch;
import de.jplag.exceptions.ExitException;
import de.jplag.highlightextraction.*;
import de.jplag.options.JPlagOptions;

public class FrequencySimilarityTest extends TestBase {
    public static FrequencyStrategy completeMatchesStrategy = new CompleteMatchesStrategy();
    private JPlagComparison comparison;
    private JPlagComparison comparison2;
    private FrequencySimilarity frequencySimilarity1;
    private FrequencySimilarity frequencySimilarity2;
    private FrequencySimilarity frequencySimilarity3;
    private FrequencySimilarity frequencySimilarity4;
    public static SimilarityStrategy proportionalWeigthedStrategy = new ProportionalWeigthedStrategy();
    public static SimilarityStrategy rareTokensWeightedStrategy = new RareTokensWeightedStrategy();
    public static SimilarityStrategy quadraticWeightedStrategy = new QuadraticWeightedStrategy();
    public static SimilarityStrategy sigmoidWeightingStrategy = new SigmoidWeightingStrategy();
    public Map<List<TokenType>, Integer> frequencyMap = new HashMap<>();
    Match testMatch;
    Match matchContained;
    Submission testSubmission;

    /**
     * Creates Test data by running JPlag Methods to get JPlag result for building test data.
     * @param options JPlag options used in this test
     * @return JPlag result for test input
     * @throws ExitException submission set builder can throw this exception
     */
    private JPlagResult getJPlagResult(JPlagOptions options) throws ExitException {
        SubmissionSetBuilder builder = new SubmissionSetBuilder(options);
        SubmissionSet submissionSet = builder.buildSubmissionSet();
        LongestCommonSubsequenceSearch subsequenceSearch = new LongestCommonSubsequenceSearch(options);
        return subsequenceSearch.compareSubmissions(submissionSet);
    }

    /**
     * Creates Test data to validate different match-frequency combinations.
     * @throws ExitException if getJPlagResult fails to create the comparison result.
     */
    @BeforeEach
    void prepareMatchResult() throws ExitException {
        JPlagOptions options = getDefaultOptions("PartialPlagiarism");
        JPlagResult result = getJPlagResult(options);
        JPlagComparison testComparison = result.getAllComparisons().getFirst();
        testMatch = testComparison.matches().getFirst();
        testSubmission = testComparison.firstSubmission();
        matchContained = new Match(testMatch.getStartOfFirst(), testMatch.getStartOfSecond(), testMatch.getLengthOfFirst() - 1,
                testMatch.getLengthOfSecond() - 1);
        this.comparison = result.getAllComparisons().getFirst();
        this.comparison2 = result.getAllComparisons().getLast();
        this.frequencySimilarity1 = new FrequencySimilarity(List.of(comparison), proportionalWeigthedStrategy);
        this.frequencySimilarity2 = new FrequencySimilarity(List.of(comparison), rareTokensWeightedStrategy);
        this.frequencySimilarity3 = new FrequencySimilarity(List.of(comparison), quadraticWeightedStrategy);
        this.frequencySimilarity4 = new FrequencySimilarity(List.of(comparison), sigmoidWeightingStrategy);
        List<TokenType> submissionToken = new ArrayList<>();
        for (int i = 0; i < testSubmission.getTokenList().size(); i++) {
            submissionToken.add(testSubmission.getTokenList().get(i).getType());
        }
        completeMatchesStrategy.processMatchTokenTypes(extractMatchTokenTypes(comparison.matches().getFirst()), this::addSequenceKey,
                this::addSequence, 20);
        completeMatchesStrategy.processMatchTokenTypes(extractMatchTokenTypes(comparison.matches().getLast()), this::addSequenceKey,
                this::addSequence, 20);
        MatchWeighting weighting = new MatchWeighting(completeMatchesStrategy, frequencyMap);
        weighting.weightAllMatches(List.of(comparison.matches().getFirst(), comparison.matches().getLast()), submissionToken);
    }

    private List<TokenType> extractMatchTokenTypes(Match match) {
        List<TokenType> tokens = new ArrayList<>();
        for (int i = match.getStartOfFirst(); i <= match.getStartOfFirst() + match.getLengthOfFirst(); i++) {
            tokens.add(testSubmission.getTokenList().get(i).getType());
        }
        return tokens;
    }

    /**
     * Tests frequency similarity strategies
     */
    @Test
    @DisplayName("frequencySimilarity berechnet realistische Werte")
    void testFrequencySimilarity_withRealisticMatchData() {

        double result1 = frequencySimilarity1.frequencySimilarity(comparison, 0.5);
        double result2 = frequencySimilarity2.frequencySimilarity(comparison, 0.5);
        double result3 = frequencySimilarity3.frequencySimilarity(comparison, 0.5);
        double result4 = frequencySimilarity4.frequencySimilarity(comparison, 0.5);

        double roundedResult1 = Math.round(result1 * 100.0) / 100.0;
        double roundedResult2 = Math.round(result2 * 100.0) / 100.0;
        double roundedResult3 = Math.round(result3 * 100.0) / 100.0;
        double roundedResult4 = Math.round(result4 * 100.0) / 100.0;

        assertEquals(1.0, roundedResult1, 0.01, "proportional frequencySimilarity should have following weight at 0.5: 1");
        assertEquals(1.0, roundedResult2, 0.01, "linear frequencySimilarity should have following weight at 0.5: 1");
        assertEquals(1.0, roundedResult3, 0.01, "quadratic frequencySimilarity should have following weight at 0.5: 1");
        assertEquals(1.0, roundedResult4, 0.01, "sigmoid frequencySimilarity should have following weight at 0.5: 1");
        List<JPlagComparison> comparisons = new ArrayList<>();
        comparisons.add(comparison);
        comparisons.add(comparison2);
        List<JPlagComparison> result5 = frequencySimilarity1.calculateFrequencySimilarity(comparisons, 0);
        List<JPlagComparison> result6 = frequencySimilarity2.calculateFrequencySimilarity(comparisons, 1);
        List<JPlagComparison> result7 = frequencySimilarity3.calculateFrequencySimilarity(comparisons, 0.5);
        List<JPlagComparison> result8 = frequencySimilarity4.calculateFrequencySimilarity(comparisons, 0.5);
        assertEquals(0.0, result5.get(1).similarity(), 0.01, "proportional frequencySimilarity should have following weight at 0.5: 1");
        assertEquals(0.0, result6.get(1).similarity(), 0.01, "linear frequencySimilarity should have following weight at 0.5: 1");
        assertEquals(0.0, result7.get(1).similarity(), 0.01, "quadratic frequencySimilarity should have following weight at 0.5: 1");
        assertEquals(0.0, result8.get(1).similarity(), 0.01, "sigmoid frequencySimilarity should have following weight at 0.5: 1");

    }

    /**
     * Adds the Sequence to the Frequency map.
     * @param sequence The token sequence whose frequency will be updated.
     */
    private void addSequenceKey(List<TokenType> sequence) {
        frequencyMap.putIfAbsent(sequence, 0);
    }

    /**
     * Updates the frequency of the given sequence in the frequency map.
     * @param sequence The token sequence whose frequency will be updated.
     */
    private void addSequence(List<TokenType> sequence) {
        frequencyMap.put(sequence, frequencyMap.getOrDefault(sequence, 0) + 1);
    }

}
