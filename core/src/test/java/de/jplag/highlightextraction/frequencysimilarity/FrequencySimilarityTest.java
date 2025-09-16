package de.jplag.highlightextraction.frequencysimilarity;

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
    private FrequencySimilarity similarityProportional;
    private FrequencySimilarity similarityLinear;
    private FrequencySimilarity similarityQuadratic;
    private FrequencySimilarity similaritySigmoid;
    public static SimilarityStrategy proportionalWeightedStrategy = new ProportionalWeightedStrategy();
    public static SimilarityStrategy rareTokensWeightedStrategy = new LinearWeightedStrategy();
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
        matchContained = new Match(testMatch.startOfFirst(), testMatch.startOfSecond(), testMatch.lengthOfFirst() - 1,
                testMatch.lengthOfSecond() - 1);
        this.comparison = result.getAllComparisons().getFirst();
        this.comparison2 = result.getAllComparisons().getLast();
        List<TokenType> submissionToken = new ArrayList<>();
        JPlagComparison.setFrequency(true);
        for (int i = 0; i < testSubmission.getTokenList().size(); i++) {
            submissionToken.add(testSubmission.getTokenList().get(i).getType());
        }
        completeMatchesStrategy.processMatchTokenTypes(extractMatchTokenTypes(comparison.matches().getFirst()), this::addSequenceKey,
                this::addSequence, 20);
        completeMatchesStrategy.processMatchTokenTypes(extractMatchTokenTypes(comparison.matches().getLast()), this::addSequenceKey,
                this::addSequence, 20);
        MatchWeighting weighting = new MatchWeighting(completeMatchesStrategy, frequencyMap);
        MatchFrequency matchFrequency = weighting.weightAllMatches(List.of(comparison.matches().getFirst(), comparison.matches().getLast()),
                submissionToken);
        this.similarityProportional = new FrequencySimilarity(List.of(comparison), proportionalWeightedStrategy, matchFrequency);
        this.similarityLinear = new FrequencySimilarity(List.of(comparison), rareTokensWeightedStrategy, matchFrequency);
        this.similarityQuadratic = new FrequencySimilarity(List.of(comparison), quadraticWeightedStrategy, matchFrequency);
        this.similaritySigmoid = new FrequencySimilarity(List.of(comparison), sigmoidWeightingStrategy, matchFrequency);
    }

    private List<TokenType> extractMatchTokenTypes(Match match) {
        List<TokenType> tokens = new ArrayList<>();
        for (int i = match.startOfFirst(); i <= match.startOfFirst() + match.lengthOfFirst(); i++) {
            tokens.add(testSubmission.getTokenList().get(i).getType());
        }
        return tokens;
    }

    /**
     * Tests frequency similarity strategies
     */
    @Test
    @DisplayName("frequencySimilarity calculates realistic values")
    void testFrequencySimilarity_withRealisticMatchData() {

        double resultProportional = similarityProportional.frequencySimilarity(comparison, 0.5);
        double resultLinear = similarityLinear.frequencySimilarity(comparison, 0.5);
        double resultQuadratic = similarityQuadratic.frequencySimilarity(comparison, 0.5);
        double resultSigmoid = similaritySigmoid.frequencySimilarity(comparison, 0.5);

        double roundedResultProportional = Math.round(resultProportional * 100.0) / 100.0;
        double roundedResultLinear = Math.round(resultLinear * 100.0) / 100.0;
        double roundedResultQuadratic = Math.round(resultQuadratic * 100.0) / 100.0;
        double roundedResultSigmoid = Math.round(resultSigmoid * 100.0) / 100.0;

        // assertEquals(1.0, roundedResultProportional, 0.01, "proportional frequencySimilarity should have following weight at
        // 0.5: 1");
        // assertEquals(1.0, roundedResultLinear, 0.01, "linear frequencySimilarity should have following weight at 0.5: 1");
        // assertEquals(1.0, roundedResultQuadratic, 0.01, "quadratic frequencySimilarity should have following weight at 0.5:
        // 1");
        // assertEquals(1.0, roundedResultSigmoid, 0.01, "sigmoid frequencySimilarity should have following weight at 0.5: 1");
        // List<JPlagComparison> comparisons = new ArrayList<>();
        // comparisons.add(comparison);
        // comparisons.add(comparison2);
        // List<JPlagComparison> result5 = similarityProportional.calculateFrequencySimilarity(comparisons, 0);
        // List<JPlagComparison> result6 = similarityLinear.calculateFrequencySimilarity(comparisons, 1);
        // List<JPlagComparison> result7 = similarityQuadratic.calculateFrequencySimilarity(comparisons, 0.5);
        // List<JPlagComparison> result8 = similaritySigmoid.calculateFrequencySimilarity(comparisons, 0.5);
        // assertEquals(0.0, result5.get(1).similarity(), 0.01, "proportional frequencySimilarity should have following weight
        // at 0.5: 1");
        // assertEquals(0.0, result6.get(1).similarity(), 0.01, "linear frequencySimilarity should have following weight at 0.5:
        // 1");
        // assertEquals(0.0, result7.get(1).similarity(), 0.01, "quadratic frequencySimilarity should have following weight at
        // 0.5: 1");
        // assertEquals(0.0, result8.get(1).similarity(), 0.01, "sigmoid frequencySimilarity should have following weight at
        // 0.5: 1");

    }

    /**
     * Tests frequency similarity strategies
     */
    @Test
    @DisplayName("frequencySimilarity calculates realistic values")
    void testFrequencySimilarity_Proportional() {

        double resultProportional = similarityProportional.frequencySimilarity(comparison, 0.5);
        double resultLinear = similarityLinear.frequencySimilarity(comparison, 0.5);
        double resultQuadratic = similarityQuadratic.frequencySimilarity(comparison, 0.5);
        double resultSigmoid = similaritySigmoid.frequencySimilarity(comparison, 0.5);

        double roundedResultProportional = Math.round(resultProportional * 100.0) / 100.0;
        double roundedResultLinear = Math.round(resultLinear * 100.0) / 100.0;
        double roundedResultQuadratic = Math.round(resultQuadratic * 100.0) / 100.0;
        double roundedResultSigmoid = Math.round(resultSigmoid * 100.0) / 100.0;

        // assertEquals(1.0, roundedResultProportional, 0.01, "proportional frequencySimilarity should have following weight at
        // 0.5: 1");
        // assertEquals(1.0, roundedResultLinear, 0.01, "linear frequencySimilarity should have following weight at 0.5: 1");
        // assertEquals(1.0, roundedResultQuadratic, 0.01, "quadratic frequencySimilarity should have following weight at 0.5:
        // 1");
        // assertEquals(1.0, roundedResultSigmoid, 0.01, "sigmoid frequencySimilarity should have following weight at 0.5: 1");
        // List<JPlagComparison> comparisons = new ArrayList<>();
        // comparisons.add(comparison);
        // comparisons.add(comparison2);
        // List<JPlagComparison> result5 = similarityProportional.calculateFrequencySimilarity(comparisons, 0);
        // List<JPlagComparison> result6 = similarityLinear.calculateFrequencySimilarity(comparisons, 1);
        // List<JPlagComparison> result7 = similarityQuadratic.calculateFrequencySimilarity(comparisons, 0.5);
        // List<JPlagComparison> result8 = similaritySigmoid.calculateFrequencySimilarity(comparisons, 0.5);
        // assertEquals(0.0, result5.get(1).similarity(), 0.01, "proportional frequencySimilarity should have following weight
        // at 0.5: 1");
        // assertEquals(0.0, result6.get(1).similarity(), 0.01, "linear frequencySimilarity should have following weight at 0.5:
        // 1");
        // assertEquals(0.0, result7.get(1).similarity(), 0.01, "quadratic frequencySimilarity should have following weight at
        // 0.5: 1");
        // assertEquals(0.0, result8.get(1).similarity(), 0.01, "sigmoid frequencySimilarity should have following weight at
        // 0.5: 1");

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
