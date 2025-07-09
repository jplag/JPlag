package de.jplag.highlight_extraction;

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
        int matchedFrequencyTokensOfFirst = getRareTokensWeightedMatchLengthOfFirst(comparison, weight);//todo hier anpassen
        int matchedFrequencyTokensOfSecond = getRareTokensWeightedMatchLengthOfSecond(comparison, weight);

         //System.out.println(comparison.matches().stream().mapToInt(Match::getLengthOfFirst).sum());

        return (matchedFrequencyTokensOfFirst + matchedFrequencyTokensOfSecond) / (double) divisor;

    }

public int getLinearWeightedMatchLengthOfFirst(JPlagComparison comparison, double weight) {
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
                double myWeight = (1 - weight) * 1.0 + weight * weighted;

                double length = match.getLengthOfFirst();
                double weightedLength = length * myWeight;

                if (Double.isNaN(weightedLength)) {
                    System.err.printf("NaN detected! freq=%.3f, maxFreq=%.3f, normalized=%.3f, myWeight=%.3f, length=%.3f%n",
                            freq, finalMaxFrequency, normalized, myWeight, length);
                    return 0.0;
                }

                return weightedLength;
            })
            .sum();
   // System.out.println("weightedSum = " + weightedSum);
    return (int) Math.round(weightedSum);
}

    public int getLinearWeightedMatchLengthOfSecond(JPlagComparison comparison, double weight) {
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
                    double myWeight = (1 - weight) * 1.0 + weight * weighted;

                    double length = match.getLengthOfSecond();
                    double weightedLength = length * myWeight;

                    if (Double.isNaN(weightedLength)) {
                        System.err.printf("NaN detected! freq=%.3f, maxFreq=%.3f, normalized=%.3f, myWeight=%.3f, length=%.3f%n",
                                freq, finalMaxFrequency, normalized, myWeight, length);
                        return 0.0;
                    }

                    return weightedLength;
                })
                .sum();
        //System.out.println("weightedSum = " + weightedSum);
        return (int) Math.round(weightedSum);
    }

    public int getRareTokensWeightedMatchLengthOfFirst(JPlagComparison comparison, double weight) {
        double maxFrequency = comparison.matches().stream()
                .mapToDouble(Match::getFrequencyWeight)
                .max()
                .orElse(1.0);
        if (maxFrequency == 0.0) maxFrequency = 1.0;

        double minWeight = 1.0;     // Minimumgewicht jetzt 1.0
        double maxWeight = 2.0;     // Seltene stärker gewichtet

        double finalMaxFrequency = maxFrequency;
        double weightedSum = comparison.matches().stream()
                .mapToDouble(match -> {
                    double freq = match.getFrequencyWeight();
                    if (Double.isNaN(freq) || freq < 0.0) freq = 0.0;

                    // Normieren
                    double rarity = 1.0 - Math.min(freq / finalMaxFrequency, 1.0);

                    // Gewichtung nur erhöhen
                    double rarityWeight = minWeight + (maxWeight - minWeight) * rarity;

                    // linearisierung
                    double myWeight = (1 - weight) * 1.0 + weight * rarityWeight;

                    return match.getLengthOfFirst() * myWeight;
                })
                .sum();

        return (int) Math.round(weightedSum);
    }

    public int getRareTokensWeightedMatchLengthOfSecond(JPlagComparison comparison, double weight) {
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
                    double rarityWeight = minWeight + (maxWeight - minWeight) * rarity;

                    // linearisierung
                    double myWeight = (1 - weight) * 1.0 + weight * rarityWeight;

                    return match.getLengthOfFirst() * myWeight;
                })
                .sum();

        return (int) Math.round(weightedSum);
    }
}