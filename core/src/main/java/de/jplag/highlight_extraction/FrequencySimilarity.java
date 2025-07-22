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
        int matchedFrequencyTokensOfFirst = getRareLogSquareTokensWeightedMatchLengthOfFirst(comparison, weight);//todo hier anpassen
        int matchedFrequencyTokensOfSecond = getRareLogSquareTokensWeightedMatchLengthOfSecond(comparison, weight);

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
    /*
    maxWeight       =   2
    minWeight       =   1
    fiMaxFrcy       =   0.9      0.9       0.9
    weight          =   0,5      0,5       0,5
    freq            =   0.2      0.8       0.15
    rarity          =   0.7778  0.111      0.8333
    rarityWeight    =   1.7778  1.1111
    myWeight        =   1.3889  1.0556     1.4167
     */
    public int getRareTokensWeightedMatchLengthOfFirst(JPlagComparison comparison, double weight) {
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

                    // linearisierung: Lineare Interpolation zwischen (x0, f0) und (x1, f1):
                    //f(x) = f0 + (x - x0) * (f1 - f0) / (x1 - x0)
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
    public int getRareLogTokensWeightedMatchLengthOfFirst(JPlagComparison comparison, double weight) {
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
                    if (Double.isNaN(freq) || freq < 0.0) freq = 0.0; //solle nicht eintreten
/*
maxWeight       =   2
minWeight       =   1
finalMaxFrequency=  0.9
weight          =   0,5
freq            =   0.2      0.8       0.15
rarity          =   0.7778  0.111      0.8333
rarityWeight    =   1.7778  1.1111     1.8333
myWeight        =   1.3889  1.0556     1.4167
 */
                    // Normieren -> rellative häufigkeit zum maximalwert
                    double rarity = 1.0 - Math.min(freq / finalMaxFrequency, 1.0);

                    // Gewichtung nur erhöhen
                    double logMin = Math.log(minWeight);
                    double logMax = Math.log(maxWeight);
                    double rarityWeight = Math.exp(logMin + (logMax - logMin) * rarity);


                    // linearisierung: Lineare Interpolation zwischen (x0, f0) und (x1, f1):
                    //f(x) = f0 + (x - x0) * (f1 - f0) / (x1 - x0) = (1−α)⋅a+⋅b 𝑎
                    //a: Startwert (hier: 1.0)
                    //b: Zielwert (hier: rarityWeight)
                    //𝛼: (x - x0)/(x1 - x0) Gewichtungsfaktor (hier: weight, zwischen 0 und 1)
                    //Logarithmische Interpolation zwischen (x0, f0) und (x1, f1):
                    //f(x) = f0 * exp( ((x - x0) * (ln(f1) - ln(f0))) / (x1 - x0) )
                    // lineare interpolation zwischen der gewichtungsfunktion zwischen 0 und 1
                    double myWeight = (1.0 - weight) + weight * rarityWeight;

                    return match.getLengthOfFirst()   * myWeight;
                })
                .sum();

        return (int) Math.round(weightedSum);
    }

    public int getRareLogTokensWeightedMatchLengthOfSecond(JPlagComparison comparison, double weight) {
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
                    if (Double.isNaN(freq) || freq < 0.0) freq = 0.0; //solle nicht eintreten
                    double rarity = 1.0 - Math.min(freq / finalMaxFrequency, 1.0);
                    double logMin = Math.log(minWeight);
                    double logMax = Math.log(maxWeight);
                    double rarityWeight = Math.exp(logMin + (logMax - logMin) * rarity);

                    double myWeight = (1 - weight) * 1.0 + weight * rarityWeight;

                    return match.getLengthOfSecond()   * myWeight;
                })
                .sum();

        return (int) Math.round(weightedSum);
    }

    //git branch
    
    public int getRareLogSquareTokensWeightedMatchLengthOfFirst(JPlagComparison comparison, double weight) {
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
                    if (Double.isNaN(freq) || freq < 0.0) freq = 0.0; //solle nicht eintreten
                    // Normieren -> rellative häufigkeit zum maximalwert
                    double rarity = 1.0 - Math.min(freq / finalMaxFrequency, 1.0);

                    // Gewichtung nur erhöhen
                    double logMin = Math.log(minWeight);
                    double logMax = Math.log(maxWeight);

                    double logMinSq = logMin * logMin;
                    double logMaxSq = logMax * logMax;
                    double interpolatedLogSq = logMinSq + (logMaxSq - logMinSq) * rarity;

                    double rarityWeight = Math.exp(Math.sqrt(interpolatedLogSq));
                    double myWeight = (1 - weight) * 1.0 + weight * rarityWeight;

                    return match.getLengthOfFirst()   * myWeight;
                })
                .sum();

        return (int) Math.round(weightedSum);
    }
    public int getRareLogSquareTokensWeightedMatchLengthOfSecond(JPlagComparison comparison, double weight) {
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
                    if (Double.isNaN(freq) || freq < 0.0) freq = 0.0; //solle nicht eintreten

                    // Normieren -> rellative häufigkeit zum maximalwert
                    double rarity = 1.0 - Math.min(freq / finalMaxFrequency, 1.0);

                    double logMin = Math.log(minWeight);
                    double logMax = Math.log(maxWeight);

                    double logMinSq = logMin * logMin;
                    double logMaxSq = logMax * logMax;
                    double interpolatedLogSq = logMinSq + (logMaxSq - logMinSq) * rarity;
                    double rarityWeight = Math.exp(Math.sqrt(interpolatedLogSq));

                    double myWeight = (1 - weight) * 1.0 + weight * rarityWeight;

                    return match.getLengthOfSecond()   * myWeight;
                })
                .sum();

        return (int) Math.round(weightedSum);
    }
}