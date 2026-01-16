package de.jplag.highlightextraction;

import java.util.List;
import java.util.Map;
import java.util.OptionalDouble;

import de.jplag.JPlagComparison;
import de.jplag.Match;
import de.jplag.TokenType;
import de.jplag.highlightextraction.strategy.FrequencyStrategy;

/**
 * Calculates the frequency dependent similarity for the comparisons according to the frequency similarity weighting
 * strategy.
 */
public class MatchFrequencyWeighting {
    /**
     * All comparisons to calculate the similarity for.
     */
    List<JPlagComparison> comparisons;
    /**
     * Chosen weighting function.
     */
    private final WeightingFunction strategy;
    private final Map<List<TokenType>, Double> matchFrequency;
    private static final double DEFAULT_MAXIMUM_FREQUENCY = 1.0;
    private static final double DEFAULT_MINIMUM_FREQUENCY = 0.0;

    /**
     * Constructor defines comparisons and strategy for the similarity calculation.
     * @param comparisons considered comparisons to calculate the similarity score for
     * @param strategy chosen weighting function
     * @param matchFrequency the matchFrequency containing the map that maps a match to its frequency
     */
    public MatchFrequencyWeighting(List<JPlagComparison> comparisons, WeightingFunction strategy, Map<List<TokenType>, Double> matchFrequency) {
        this.comparisons = comparisons;
        this.strategy = strategy;
        this.matchFrequency = matchFrequency;
    }

    /**
     * Calculates the similarity score for a comparison.
     * @param comparison considered comparison to calculate the similarity score for
     * @param weightingFactor controls the influence of the weighting
     * @return similarity of the comparison
     */
    public JPlagComparison weightedComparisonSimilarity(JPlagComparison comparison, double weightingFactor) {
        double frequencyWeightedSimilarity = frequencySimilarity(comparison, weightingFactor);
        return new JPlagComparison(comparison, frequencyWeightedSimilarity, true);
    }

    private double getMatchCount(JPlagComparison comparison, Match match) {
        List<TokenType> matchTokenTypes = TokenSequenceUtil.tokenTypesFor(comparison, match);
        return matchFrequency.getOrDefault(matchTokenTypes, DEFAULT_MINIMUM_FREQUENCY);
    }

    /**
     * Determines the similarity score for a comparison using the {@link FrequencyStrategy}, {@link WeightingFunction} and
     * weighting factor.
     * @param comparison considered comparison to calculate the similarity score for
     * @param weightingFactor controls the influence of the weighting
     * @return the similarity score
     */
    public double frequencySimilarity(JPlagComparison comparison, double weightingFactor) {
        if (weightingFactor == 0.0) {
            return comparison.similarity();
        }

        int divisor = comparison.firstSubmission().getSimilarityDivisor() + comparison.secondSubmission().getSimilarityDivisor();
        if (divisor == 0) {
            return 0;
        }
        double weightedMatchLengthLeft = getWeightedMatchLength(comparison, weightingFactor, true, strategy);
        double weightedMatchLengthRight = getWeightedMatchLength(comparison, weightingFactor, false, strategy);

        return (weightedMatchLengthLeft + weightedMatchLengthRight) / (double) divisor;

    }

    /**
     * Changes the considered match length according to the frequency Weight, depending on the frequency similarity
     * weightingFunction.
     * @param comparison considered comparison to calculate the similarity score for
     * @param weightingFactor controls the influence of the weighting
     * @param firstSubmission considered submission to calculate the frequency, the frequency for both submissions will be
     * calculated
     * @param weightingFunction chosen weighting function
     * @return the similarity score
     */
    public double getWeightedMatchLength(JPlagComparison comparison, double weightingFactor, boolean firstSubmission,
            WeightingFunction weightingFunction) {

        double finalMaximumFoundFrequency = getMaxFrequency();
        double weightedTotalMatchLength = 0;
        for (Match match : comparison.matches()) {
            double matchCount = getMatchCount(comparison, match);
            double matchLength = firstSubmission ? match.lengthOfFirst() : match.lengthOfSecond();

            if (matchCount == 0) {
                weightedTotalMatchLength += matchLength;
            }

            double weightFactor = getWeightFactor(matchCount, weightingFactor, weightingFunction, finalMaximumFoundFrequency);

            weightedTotalMatchLength += matchLength * weightFactor;
        }

        return weightedTotalMatchLength;
    }

    /**
     * Gets the max number of occurrences of any match across the submission set.
     * @return the max occurrence.
     */
    private double getMaxFrequency() {
        OptionalDouble maxFrequency = matchFrequency.values().stream().mapToDouble(Double::doubleValue).max();
        return maxFrequency.orElse(DEFAULT_MAXIMUM_FREQUENCY);
    }

    /**
     * Calculates how much the considered length of the match will be changed to influence the similarity score according to
     * the matches' frequency.
     * @param matchFrequency calculated frequency of the match
     * @param weightingFactor controls the influence of the weighting
     * @param weightingFunction determines the mapping of rarity to weights
     * @param maxFrequency highest frequency that occurred across all matches in all comparisons
     * @return the weight resulting from the frequency analysis for the given frequency
     */
    private static double getWeightFactor(double matchFrequency, double weightingFactor, WeightingFunction weightingFunction, double maxFrequency) {
        if (Double.isNaN(matchFrequency) || matchFrequency < DEFAULT_MINIMUM_FREQUENCY) {
            matchFrequency = DEFAULT_MINIMUM_FREQUENCY;
        }
        double relativeFrequencyOfMatch = DEFAULT_MAXIMUM_FREQUENCY - Math.min(matchFrequency / maxFrequency, DEFAULT_MAXIMUM_FREQUENCY);
        double weightOfMatch = weightingFunction.computeWeight(relativeFrequencyOfMatch);
        return (DEFAULT_MAXIMUM_FREQUENCY - weightingFactor) + weightingFactor * weightOfMatch;
    }
}