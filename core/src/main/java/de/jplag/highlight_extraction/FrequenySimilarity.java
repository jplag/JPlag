package de.jplag.highlight_extraction;

import java.util.Comparator;
import java.util.List;

import de.jplag.JPlagComparison;
import de.jplag.Match;

public class FrequenySimilarity {
    List<JPlagComparison> comparisons;

    public FrequenySimilarity(List<JPlagComparison> comparisons) {
        this.comparisons = comparisons;
    }

    public List<JPlagComparison> calculateFrequencySimilarity(List<JPlagComparison> comparisons, double weight) {
        final double frequencyWeight = weight;
        return this.comparisons = comparisons.stream()
                .sorted(Comparator.comparingDouble((JPlagComparison c) -> frequencySimilarity(c, frequencyWeight)).reversed()).toList();
    }

    public double frequencySimilarity(JPlagComparison comparison, double weight) {
        int divisor = comparison.firstSubmission().getSimilarityDivisor() + comparison.secondSubmission().getSimilarityDivisor();
        if (divisor == 0)
            return 0;

        int matchedFrequencyTokensOfFirst = getLinearWeightedMatchLengthOfFirst(comparison, weight);
        int matchedFrequencyTokensOfSecond = getLinearWeightedMatchLengthOfSecond(comparison, weight);

        return (matchedFrequencyTokensOfFirst + matchedFrequencyTokensOfSecond) / (double) divisor;

    }
    // TODO getSimilarityDivisor() public ggf. wieder wegmachen

    // public double similarityWithoutFrequency(JPlagComparison comparison) {
    // int divisor = comparison.firstSubmission().getSimilarityDivisor() +
    // comparison.secondSubmission().getSimilarityDivisor();
    // if (divisor == 0) {
    // return 0;
    // }
    //
    // int matchedTokensOfFirst = comparison.matches().stream().mapToInt(Match::getLengthOfFirst).sum();
    // int matchedTokensOfSecond = comparison.matches().stream().mapToInt(Match::getLengthOfSecond).sum();
    //
    // return (matchedTokensOfFirst + matchedTokensOfSecond) / (double) divisor;
    //
    // }

    public int getLinearWeightedMatchLengthOfFirst(JPlagComparison comparison, double weight) {
        double maxFrequency = comparison.matches().stream().mapToDouble(Match::getFreuencyWeight).max().orElse(1.0);
        return comparison.matches().stream().mapToDouble(match -> {
            double freq = match.getFreuencyWeight();
            double normalized = 1.0 - Math.min(freq / maxFrequency, 1.0);
            double adjustment = 1 + weight * (normalized - 0.5);
            adjustment = Math.max(0.01, adjustment);
            return match.getLengthOfFirst() * adjustment;
        }).mapToInt(d -> (int) Math.round(d)).sum();
    }

    public int getLinearWeightedMatchLengthOfSecond(JPlagComparison comparison, double weight) {
        double maxFrequency = comparison.matches().stream().mapToDouble(Match::getFreuencyWeight).max().orElse(1.0);
        return comparison.matches().stream().mapToDouble(match -> {
            double freq = match.getFreuencyWeight();
            double normalized = 1.0 - Math.min(freq / maxFrequency, 1.0);
            double adjustment = 1 + weight * (normalized - 0.5);
            adjustment = Math.max(0.01, adjustment);
            return match.getLengthOfSecond() * adjustment;
        }).mapToInt(d -> (int) Math.round(d)).sum();
    }

    public double getMinFrequency(JPlagComparison comparison) {
        return comparison.matches().stream()
                .mapToDouble(Match::getFreuencyWeight)
                .min()
                .orElse(0.0);
    }

    public double getMaxFrequency(JPlagComparison comparison) {
        return comparison.matches().stream()
                .mapToDouble(Match::getFreuencyWeight)
                .max()
                .orElse(1.0);
    }



}