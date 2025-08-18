package de.jplag.highlightextraction;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import de.jplag.JPlagComparison;
import de.jplag.Match;

public class FrequencySimilarity {
    List<JPlagComparison> comparisons;
    SimilarityStrategy strategy;
    public FrequencySimilarity(List<JPlagComparison> comparisons, SimilarityStrategy strategy) {
        this.comparisons = comparisons;
        this.strategy = strategy;
    }

    public List<JPlagComparison> calculateFrequencySimilarity(List<JPlagComparison> comparisons, double weight) {
        final double frequencyWeight = weight;
        return this.comparisons = comparisons.stream()
                .sorted(Comparator.comparingDouble((JPlagComparison c) -> frequencySimilarity(c, frequencyWeight)).reversed()).toList();
    }

    public double frequencySimilarity(JPlagComparison comparison, double weight) {
        if (weight == 0.0) {
            return comparison.similarity(); // exakt das Original-Ergebnis verwenden
        }

        int divisor = comparison.firstSubmission().getSimilarityDivisor() + comparison.secondSubmission().getSimilarityDivisor();

        if (divisor == 0) {
            return 0;
        }
        int matchedFrequencyTokensOfFirst = getWeightedMatchLengthOfFirst(comparison, weight, true, strategy);
        int matchedFrequencyTokensOfSecond = getWeightedMatchLengthOfFirst(comparison, weight, false, strategy);

        return (matchedFrequencyTokensOfFirst + matchedFrequencyTokensOfSecond) / (double) divisor;

    }
    public int getWeightedMatchLengthOfFirst(JPlagComparison comparison, double weight, boolean first, SimilarityStrategy strategy) {
        double minWeight;
        double maxWeight;
        double maxFrequency = comparison.matches().stream()
                .mapToDouble(Match::getFrequencyWeight)
                .max()
                .orElse(1.0);
        if (maxFrequency == 0.0) maxFrequency = 1.0;

        if(Objects.equals(strategy, ProportionalWeigthedStrategy.class)) {
            minWeight = 0.01;
            maxWeight = 2.0;
        } else {
            minWeight = 1.0;
            maxWeight = 2.0;
        }

        double finalMaxFrequency = maxFrequency;
        double weightedSum = comparison.matches().stream()
                .mapToDouble(match -> {
                    double freq = match.getFrequencyWeight();
                    if (Double.isNaN(freq) || freq < 0.0) freq = 0.0;

                    double rarity = 1.0 - Math.min(freq / finalMaxFrequency, 1.0);
                    double rarityWeight = strategy.computeWeight(minWeight, maxWeight, rarity);
                    double myWeight = (1 - weight) * 1.0 + weight * rarityWeight;

                    if (first) {
                        return match.getLengthOfFirst() * myWeight;
                    } else {
                        return match.getLengthOfSecond() * myWeight;
                    }

                })
                .sum();

        return (int) Math.round(weightedSum);
    }
}