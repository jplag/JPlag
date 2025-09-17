package de.jplag.highlightextraction;

import java.util.List;
import java.util.Objects;

import de.jplag.JPlagComparison;
import de.jplag.Match;
import de.jplag.Token;
import de.jplag.TokenType;

/**
 * Calculates the isFrequencyAnalysisEnabled dependent similarity for the comparisons according to the
 * isFrequencyAnalysisEnabled similarity weighting strategy.
 */
public class FrequencySimilarity {
    /**
     * All comparisons to calculate the similarity for.
     */
    List<JPlagComparison> comparisons;
    /**
     * Chosen weighting function.
     */
    MatchWeightingFunction strategy;
    private final MatchFrequency matchFrequency;

    /**
     * Constructor defines comparisons and strategy for the similarity calculation.
     * @param comparisons considered comparisons to calculate the similarity score for
     * @param strategy chosen weighting function
     * @param matchFrequency the matchFrequency containing the map that maps a match to its isFrequencyAnalysisEnabled
     */
    public FrequencySimilarity(List<JPlagComparison> comparisons, MatchWeightingFunction strategy, MatchFrequency matchFrequency) {
        this.comparisons = comparisons;
        this.strategy = strategy;
        this.matchFrequency = matchFrequency;
    }

    /**
     * Calculates the similarity score for a comparison.
     * @param comparison considered comparison to calculate the similarity score for
     * @param weightingFactor weighting factor, is factor for the (max) influence of the isFrequencyAnalysisEnabled
     * @param isFrequencyAnalysisEnabled if the isFrequencyAnalysisEnabled shall be considered
     * @return similarity of the comparison
     */
    public JPlagComparison weightedComparisonSimilarity(JPlagComparison comparison, double weightingFactor, boolean isFrequencyAnalysisEnabled) {
        double frequencyWeightedSimilarity = frequencySimilarity(comparison, weightingFactor);
        return new JPlagComparison(comparison, frequencyWeightedSimilarity, isFrequencyAnalysisEnabled);
    }
    //
    // /**
    // * Sorts the comparisons, according to the isFrequencyAnalysisEnabled.
    // * @param comparisons considered comparisons to calculate the similarity score for
    // * @param weight weighting factor, is factor for the (max) influence of the isFrequencyAnalysisEnabled
    // * @return the comparisons sorted with similarity score
    // */
    //
    // public List<JPlagComparison> calculateFrequencySimilarity(List<JPlagComparison> comparisons, double weight) {
    // final double frequencyWeight = weight;
    // this.comparisons = comparisons.stream()
    // .sorted(Comparator.comparingDouble((JPlagComparison comparison) -> frequencySimilarity(comparison,
    // frequencyWeight)).reversed()).toList();
    // return this.comparisons;
    // }

    private double getFrequencyFromMap(JPlagComparison comparison, Match match) {
        List<TokenType> submissionTokenTypes = comparison.firstSubmission().getTokenList().stream().map(Token::getType).toList();
        List<TokenType> matchTokens = FrequencyUtil.matchesToMatchTokenTypes(match, submissionTokenTypes);
        return matchFrequency.matchFrequencyMap().getOrDefault(matchTokens, 0.0);
    }

    /**
     * Calculates the similarity score for a comparison. The score gets influenced form the isFrequencyAnalysisEnabled of
     * the match according to the chosen Frequency Strategy, Similarity Strategy and weighting factor.
     * @param comparison considered comparison to calculate the similarity score for
     * @param weight weighting factor, is factor for the (max) influence of the isFrequencyAnalysisEnabled
     * @return the similarity score
     */
    public double frequencySimilarity(JPlagComparison comparison, double weight) {
        if (weight == 0.0) {
            return comparison.similarity();
        }

        int divisor = comparison.firstSubmission().getSimilarityDivisor() + comparison.secondSubmission().getSimilarityDivisor();
        if (divisor == 0) {
            return 0;
        }
        int matchedFrequencyTokensOfFirst = getWeightedMatchLength(comparison, weight, true, strategy);
        int matchedFrequencyTokensOfSecond = getWeightedMatchLength(comparison, weight, false, strategy);

        return (matchedFrequencyTokensOfFirst + matchedFrequencyTokensOfSecond) / (double) divisor;

    }

    /**
     * Changes the considered match length according to the isFrequencyAnalysisEnabled weight, depending on the
     * isFrequencyAnalysisEnabled similarity strategy.
     * @param comparison considered comparison to calculate the similarity score for
     * @param weight weighting factor, is factor for the (max) influence of the isFrequencyAnalysisEnabled
     * @param first considered submission to calculate the isFrequencyAnalysisEnabled for, both will be calculated
     * @param strategy chosen weighting function
     * @return the similarity score
     */
    public int getWeightedMatchLength(JPlagComparison comparison, double weight, boolean first, MatchWeightingFunction strategy) {
        double minWeight;
        double maxWeight;

        double maxFrequency = 0.0;
        if (matchFrequency.matchFrequencyMap().isEmpty()) {
            maxFrequency = 1.0;
        } else {
            for (double frequency : matchFrequency.matchFrequencyMap().values()) {
                if (frequency > maxFrequency) {
                    maxFrequency = frequency;
                }
            }
        }

        if (maxFrequency == 0.0)
            maxFrequency = 1.0;

        if (Objects.equals(strategy, ProportionalWeightedStrategy.class)) {
            minWeight = 0.01;
            maxWeight = 2.0;
        } else {
            minWeight = 1.0;
            maxWeight = 2.0;
        }

        double finalMaxFrequency = maxFrequency;
        double weightedSum = comparison.matches().stream().mapToDouble(match -> {
            double frequency = getFrequencyFromMap(comparison, match);
            if (frequency == 0) {
                if (first) {
                    return match.lengthOfFirst();
                } else {
                    return match.lengthOfSecond();
                }
            }
            if (Double.isNaN(frequency) || frequency < 0.0)
                frequency = 0.0;
            double rarity = 1.0 - Math.min(frequency / finalMaxFrequency, 1.0);
            double rarityWeight = strategy.computeWeight(minWeight, maxWeight, rarity);
            double myWeight = (1 - weight) + weight * rarityWeight;

            if (first) {
                return match.lengthOfFirst() * myWeight;
            } else {
                return match.lengthOfSecond() * myWeight;
            }

        }).sum();

        return (int) Math.round(weightedSum);
    }
}