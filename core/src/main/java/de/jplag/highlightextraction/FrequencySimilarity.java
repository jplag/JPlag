package de.jplag.highlightextraction;

import java.util.Comparator;
import java.util.List;

import de.jplag.JPlagComparison;
import de.jplag.Match;

public class FrequencySimilarity {
    List<JPlagComparison> comparisons;

    public FrequencySimilarity(List<JPlagComparison> comparisons) {
        this.comparisons = comparisons;
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
        int matchedFrequencyTokensOfFirst = getLinearWeightedMatchLengthOfFirst(comparison, weight, true);//todo hier anpassen
        int matchedFrequencyTokensOfSecond = getLinearWeightedMatchLengthOfFirst(comparison, weight, false);

        return (matchedFrequencyTokensOfFirst + matchedFrequencyTokensOfSecond) / (double) divisor;

    }

public int getLinearWeightedMatchLengthOfFirst(JPlagComparison comparison, double weight, boolean first) {
    double maxFrequency = comparison.matches().stream()
            .mapToDouble(Match::getFrequencyWeight)
            .max()
            .orElse(0.0);
    if (maxFrequency == 0.0) maxFrequency = 1.0;

    double minWeight = 0.01;
    double maxWeight = 2.0;

    double finalMaxFrequency = maxFrequency;
    double weightedSum = comparison.matches().stream()
            .mapToDouble(match -> {
                double freq = match.getFrequencyWeight();
                if (Double.isNaN(freq) || freq < 0.0) freq = 0.0;

                double normalized = 1.0 - Math.min(freq / finalMaxFrequency, 1.0);

                double weighted = minWeight + (maxWeight - minWeight) * normalized;
                //linear
                double myWeight = (1 - weight) * 1.0 + weight * weighted;
                double length = 0;
                if (first) {
                    length = match.getLengthOfFirst();
                } else {
                    length = match.getLengthOfSecond();
                }

                double weightedLength = length * myWeight;

                if (Double.isNaN(weightedLength)) {
                    System.err.printf("NaN detected! freq=%.3f, maxFreq=%.3f, normalized=%.3f, myWeight=%.3f, length=%.3f%n",
                            freq, finalMaxFrequency, normalized, myWeight, length);
                    return 0.0;
                }

                return weightedLength;
            })
            .sum();
    return (int) Math.round(weightedSum);
}

    public int getRareTokensWeightedMatchLengthOfFirst(JPlagComparison comparison, double weight, boolean first) {
        double maxFrequency = comparison.matches().stream()
                .mapToDouble(Match::getFrequencyWeight)
                .max()
                .orElse(1.0);
        if (maxFrequency == 0.0) maxFrequency = 1.0;

        double minWeight = 1.0;
        double maxWeight = 2.0;

        double finalMaxFrequency = maxFrequency;
        double weightedSum = comparison.matches().stream()
                .mapToDouble(match -> {
                    double freq = match.getFrequencyWeight();
                    if (Double.isNaN(freq) || freq < 0.0) freq = 0.0;

                    // Normieren
                    double rarity = 1.0 - Math.min(freq / finalMaxFrequency, 1.0);

                    // Gewichtung nur erhöhen
                    //1+x
                    double rarityWeight = minWeight + (maxWeight - minWeight) * rarity;
                    //1+x*x
                    double rarityWeight2 = minWeight + (maxWeight - minWeight) * (rarity * rarity);
                    // linearisierung: Lineare Interpolation zwischen (x0, f0) und (x1, f1):
                    //f(x) = f0 + (x - x0) * (f1 - f0) / (x1 - x0)
                    //sigmoid
                    double rarityWeight3 = (2 - 1) * (
                            (1.0 / (1.0 + Math.exp(-10 * (rarity - 0.5))))
                                    / (1.0 / (1.0 + Math.exp(-10 * (1 - 0.5)))))
                            + 1;
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