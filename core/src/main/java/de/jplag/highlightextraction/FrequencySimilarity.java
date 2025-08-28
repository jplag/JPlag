package de.jplag.highlightextraction;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import de.jplag.JPlagComparison;
import de.jplag.Match;

/**
 * Calculates the frequency dependent similarity for the comparisons according to the frequency similarity weighting
 * strategy.
 */
public class FrequencySimilarity {
    List<JPlagComparison> comparisons;
    SimilarityStrategy strategy;

    /**
     * Constructor defines comparisons and strategy for the similarity calculation
     * @param strategy chosen weighting function
     */
    public FrequencySimilarity(List<JPlagComparison> comparisons, SimilarityStrategy strategy) {
        this.comparisons = comparisons;
        this.strategy = strategy;
    }

    /**
     * @param comparison considered comparison to calculate the similarity score for
     * @param weightingFactor weighting factor, is factor for the (max) influence of the frequency
     * @return similarity of the comparison
     */
    public double weightedComparisonSimilarity(JPlagComparison comparison, double weightingFactor) {
        return frequencySimilarity(comparison, weightingFactor);
    }

    /**
     * Sorts the comparisons, according to the frequency.
      * @param weight weighting factor, is factor for the (max) influence of the frequency
     * @return the comparisons sorted with similarity score
     */

    public List<JPlagComparison> calculateFrequencySimilarity(List<JPlagComparison> comparisons, double weight) {
        final double frequencyWeight = weight;
        return this.comparisons = comparisons.stream()
                .sorted(Comparator.comparingDouble((JPlagComparison c) -> frequencySimilarity(c, frequencyWeight)).reversed()).toList();
    }

    /**
     * Calculates the similarity score for a comparison. The score gets influenced form the frequency of the match according
     * to the cooden Frequency Strategy, Similarity Strategy and weighting factor.
     * @param comparison considered comparison to calculate the similarity score for
      * @param weight weighting factor, is factor for the (max) influence of the frequency
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
     * Changes the considered match length according to the frequency weight, depending on the frequency similarity
     * strategy.
     * @param comparison considered comparison to calculate the similarity score for
     * @param weight weighting factor, is factor for the (max) influence of the frequency
     * @param first considered submission to calculate the frequency for, both will be calculated
     * @param strategy chosen weighting function
     * @return the similarity score
     */
    public int getWeightedMatchLength(JPlagComparison comparison, double weight, boolean first, SimilarityStrategy strategy) {
        double minWeight;
        double maxWeight;
        double maxFrequency = comparison.matches().stream().mapToDouble(Match::getFrequencyWeight).max().orElse(1.0);
        if (maxFrequency == 0.0)
            maxFrequency = 1.0;

        if (Objects.equals(strategy, ProportionalWeigthedStrategy.class)) {
            minWeight = 0.01;
            maxWeight = 2.0;
        } else {
            minWeight = 1.0;
            maxWeight = 2.0;
        }

        double finalMaxFrequency = maxFrequency;
        double weightedSum = comparison.matches().stream().mapToDouble(match -> {
            double freq = match.getFrequencyWeight();
            if (freq == 0) {
                if (first) {
                    return match.getLengthOfFirst();
                } else {
                    return match.getLengthOfSecond();
                }
            }
            if (Double.isNaN(freq) || freq < 0.0)
                freq = 0.0;
            double rarity = 1.0 - Math.min(freq / finalMaxFrequency, 1.0);
            double rarityWeight = strategy.computeWeight(minWeight, maxWeight, rarity);
            double myWeight = (1 - weight) + weight * rarityWeight;

            if (first) {
                return match.getLengthOfFirst() * myWeight;
            } else {
                return match.getLengthOfSecond() * myWeight;
            }

        }).sum();

        return (int) Math.round(weightedSum);
    }
}