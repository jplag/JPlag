package de.jplag.frequency.frequencysimilarity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import de.jplag.JPlagComparison;
import de.jplag.JPlagResult;
import de.jplag.Match;
import de.jplag.Submission;
import de.jplag.SubmissionSet;
import de.jplag.SubmissionSetBuilder;
import de.jplag.TestBase;
import de.jplag.TokenType;
import de.jplag.comparison.LongestCommonSubsequenceSearch;
import de.jplag.exceptions.ExitException;
import de.jplag.frequency.FrequencyUtil;
import de.jplag.frequency.MatchFrequencyWeighting;
import de.jplag.frequency.MatchWeightingFunction;
import de.jplag.frequency.weighting.LinearWeighting;
import de.jplag.frequency.weighting.ProportionalWeighting;
import de.jplag.frequency.weighting.QuadraticWeighting;
import de.jplag.frequency.weighting.SigmoidWeighting;
import de.jplag.options.JPlagOptions;

/**
 * Tests the {@link MatchFrequencyWeighting} class with different weighting functions and factor combinations.
 */
class FrequencyWeightingTest extends TestBase {
    private static final double ORIGINAL_SIMILARITY = 0.31157;
    private Submission testSubmission;
    private Match match;
    private Match matchShort;
    private final List<Match> testMatches = new LinkedList<>();
    private List<Match> ignoredMatches = new LinkedList<>();
    private JPlagComparison comparison;
    private Map<List<TokenType>, Double> matchFrequency;

    /**
     * Creates test comparison and a frequency map from the PartialPlagiarism sample data.
     * @throws ExitException if building the submission set or performing comparisons fails
     */
    @BeforeEach
    void setUp() throws ExitException {
        JPlagOptions options = getDefaultOptions("PartialPlagiarism");
        JPlagResult result = getJPlagResult(options);
        JPlagComparison testComparison = result.getAllComparisons().getFirst();

        testSubmission = testComparison.firstSubmission();
        match = testComparison.matches().getFirst();
        matchShort = new Match(match.startOfFirst(), match.startOfSecond(), 10, 10);
        ignoredMatches = testComparison.ignoredMatches();

        Submission first = new Submission("W", testSubmission.getRoot(), testSubmission.isNew(), testSubmission.getFiles(), options.language());
        Submission second = new Submission("X", testSubmission.getRoot(), testSubmission.isNew(), testSubmission.getFiles(), options.language());
        first.setTokenList(testSubmission.getTokenList());
        second.setTokenList(testSubmission.getTokenList());

        testMatches.clear();
        testMatches.add(match);
        comparison = new JPlagComparison(first, second, testMatches, ignoredMatches);

        matchFrequency = new HashMap<>();
        matchFrequency.put(FrequencyUtil.tokenTypesFor(comparison, match), 5.0);
        matchFrequency.put(FrequencyUtil.tokenTypesFor(comparison, matchShort), 1.0);
    }

    /**
     * Creates test comparison data by running JPlag on the PartialPlagiarism sample.
     * @param options the JPlag options for the comparison
     * @return the JPlag result
     * @throws ExitException if creating the submission set fails
     */
    private JPlagResult getJPlagResult(JPlagOptions options) throws ExitException {
        SubmissionSetBuilder builder = new SubmissionSetBuilder(options);
        SubmissionSet submissionSet = builder.buildSubmissionSet();
        return new LongestCommonSubsequenceSearch(options).compareSubmissions(submissionSet);
    }

    private MatchFrequencyWeighting createWeighting(MatchWeightingFunction function) {
        return new MatchFrequencyWeighting(function, matchFrequency);
    }

    static Stream<Arguments> weightingFunctions() {
        return Stream.of(Arguments.of(new ProportionalWeighting()), Arguments.of(new LinearWeighting()), Arguments.of(new QuadraticWeighting()),
                Arguments.of(new SigmoidWeighting()));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("weightingFunctions")
    @DisplayName("A weighting factor of zero returns the original similarity unchanged")
    void factorZeroPreservesSimilarity(MatchWeightingFunction weightingFunction) {
        MatchFrequencyWeighting weighting = createWeighting(weightingFunction);
        assertEquals(ORIGINAL_SIMILARITY, weighting.frequencySimilarity(comparison, 0), 0.0001);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("weightingFunctions")
    @DisplayName("weightedComparisonSimilarity returns a comparison with the frequency flag set")
    void producesWeightedComparison(MatchWeightingFunction weightingFunction) {
        MatchFrequencyWeighting weighting = createWeighting(weightingFunction);
        JPlagComparison weightedComparison = weighting.weightedComparisonSimilarity(comparison, 1);
        assertTrue(weightedComparison.frequencyWeightedSimilarity() >= 0);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("weightingFunctions")
    @DisplayName("getWeightedMatchLength with factor=1 computes correctly for each weighting function")
    void weightedMatchLengthFactorOne(MatchWeightingFunction weightingFunction) {
        MatchFrequencyWeighting weighting = createWeighting(weightingFunction);
        double result = weighting.getWeightedMatchLength(comparison, 1, true, weightingFunction);

        if (weightingFunction instanceof ProportionalWeighting) {
            assertEquals(0, result, 0.0001);
        } else {
            assertEquals(315, result, 0.0001);
        }
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("weightingFunctions")
    @DisplayName("frequencySimilarity with factor=1 computes correctly for each weighting function")
    void frequencySimilarityFactorOne(MatchWeightingFunction weightingFunction) {
        MatchFrequencyWeighting weighting = createWeighting(weightingFunction);
        double result = weighting.frequencySimilarity(comparison, 1);

        if (weightingFunction instanceof ProportionalWeighting) {
            assertEquals(0, result, 0.0001);
        } else {
            assertEquals(ORIGINAL_SIMILARITY, result, 0.0001);
        }
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("weightingFunctions")
    @DisplayName("frequencySimilarity with factor=0.5 computes correctly for each weighting function")
    void frequencySimilarityFactorHalf(MatchWeightingFunction weightingFunction) {
        MatchFrequencyWeighting weighting = createWeighting(weightingFunction);
        double result = weighting.frequencySimilarity(comparison, 0.5);

        if (weightingFunction instanceof ProportionalWeighting) {
            assertEquals(ORIGINAL_SIMILARITY / 2, result, 0.0001);
        } else {
            assertEquals(ORIGINAL_SIMILARITY, result, 0.0001);
        }
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("weightingFunctions")
    @DisplayName("Rare matches contribute more to the weighted length than common matches")
    void rareMatchesWeightMoreThanCommonOnes(MatchWeightingFunction weightingFunction) {
        var rareMap = new HashMap<List<TokenType>, Double>();
        rareMap.put(FrequencyUtil.tokenTypesFor(comparison, match), 1.0);
        rareMap.put(FrequencyUtil.tokenTypesFor(comparison, matchShort), 5.0);

        var commonMap = new HashMap<List<TokenType>, Double>();
        commonMap.put(FrequencyUtil.tokenTypesFor(comparison, match), 5.0);
        commonMap.put(FrequencyUtil.tokenTypesFor(comparison, matchShort), 1.0);

        double rareWeighted = new MatchFrequencyWeighting(weightingFunction, rareMap).getWeightedMatchLength(comparison, 1, true, weightingFunction);
        double commonWeighted = new MatchFrequencyWeighting(weightingFunction, commonMap).getWeightedMatchLength(comparison, 1, true,
                weightingFunction);

        assertTrue(rareWeighted > commonWeighted,
                () -> weightingFunction.getClass().getSimpleName() + ": rare=" + rareWeighted + " should exceed common=" + commonWeighted);
    }
}
