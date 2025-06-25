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
        int divisor = comparison.firstSubmission().getSimilarityDivisor() + comparison.secondSubmission().getSimilarityDivisor();
//        System.out.printf("Divisor = %d (%d + %d) für %s <-> %s\n",
//                divisor,
//                comparison.firstSubmission().getSimilarityDivisor(),
//                comparison.secondSubmission().getSimilarityDivisor(),
//                comparison.firstSubmission().getName(),
//                comparison.secondSubmission().getName());
        if (divisor == 0) {
            System.out.println("⚠️ Divisor ist 0 für: " + comparison);
            return 0;
        }
        int matchedFrequencyTokensOfFirst = getLinearWeightedMatchLengthOfFirst(comparison, weight);
        int matchedFrequencyTokensOfSecond = getLinearWeightedMatchLengthOfSecond(comparison, weight);

//        System.out.printf("→ [%s] w=%.2f | matchedFirst=%d, matchedSecond=%d, divisor=%d\n",
//                comparison.toString(), weight, matchedFrequencyTokensOfFirst, matchedFrequencyTokensOfSecond, divisor);
        System.out.println(comparison.matches().stream().mapToInt(Match::getLengthOfFirst).sum());
//        System.out.printf("→ MatchCount: %d | matchedFirst: %d, matchedSecond: %d, divisor: %d → freqSim: %.5f\n",
//                comparison.matches().size(),
//                matchedFrequencyTokensOfFirst,
//                matchedFrequencyTokensOfSecond,
//                divisor,
//                (matchedFrequencyTokensOfFirst + matchedFrequencyTokensOfSecond) / (double) divisor);
//

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
//todo old
//    public int getLinearWeightedMatchLengthOfFirst(JPlagComparison comparison, double weight) {
//        double maxFrequency = comparison.matches().stream().mapToDouble(Match::getFrequencyWeight).max().orElse(1.0);
//        System.out.println("maxFrequency = " + maxFrequency);
//
////        return comparison.matches().stream().mapToDouble(match -> {
////            double freq = match.getFrequencyWeight();
////            double normalized = 1.0 - Math.min(freq / maxFrequency, 1.0);
////            double myWeight = 1 + weight * (normalized - 0.5);
////            myWeight = Math.max(0.01, myWeight);
////            return match.getLengthOfFirst() * myWeight;
////        }).mapToInt(d -> (int) Math.round(d)).sum();
//        /*
//        return (int) Math.round(
//                comparison.matches().stream()
//                        .mapToDouble(match -> {
//                            double freq = match.getFrequencyWeight();
//                            double normalized = 1.0 - Math.min(freq / maxFrequency, 1.0);
//                            double myWeight = 1 + weight * (normalized - 0.5);
//                            myWeight = Math.max(0.01, myWeight);
//                            return match.getLengthOfFirst() * myWeight;
//                        })
//                        .sum()); */
//
//        double weightedSum = comparison.matches().stream()
//                .mapToDouble(match -> {
//                    double freq = match.getFrequencyWeight();
//                    double normalized = 1.0 - Math.min(freq / maxFrequency, 1.0);
//                    double myWeight = 1 + weight * (normalized - 0.5);
//                    myWeight = Math.max(0.01, myWeight);
//
//                    int length = match.getLengthOfFirst();
//                    double weightedLength = length * myWeight;
//
//                    System.out.printf("freq=%.3f, normalized=%.3f, myWeight=%.3f, length=%d, weightedLength=%.3f%n",
//                            freq, normalized, myWeight, length, weightedLength);
//
//                    return weightedLength;
//                })
//                .sum();
//
//        System.out.println("weightedSum = " + weightedSum);
//        return (int) Math.round(weightedSum);
//    }
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
    System.out.println("weightedSum = " + weightedSum);
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
        System.out.println("weightedSum = " + weightedSum);
        return (int) Math.round(weightedSum);
    }
//todo davor
//    public int getLinearWeightedMatchLengthOfSecond(JPlagComparison comparison, double weight) {
//        double maxFrequency = comparison.matches().stream().mapToDouble(Match::getFrequencyWeight).max().orElse(1.0);
////        return comparison.matches().stream().mapToDouble(match -> {
////            double freq = match.getFrequencyWeight();
////            double normalized = 1.0 - Math.min(freq / maxFrequency, 1.0);
////            double myWeight = 1 + weight * (normalized - 0.5);
////            myWeight = Math.max(0.01, myWeight);
////            return match.getLengthOfSecond() * myWeight;
////        }).mapToInt(d -> (int) Math.round(d)).sum();
//        return (int) Math.round(
//                comparison.matches().stream()
//                        .mapToDouble(match -> {
//                            double freq = match.getFrequencyWeight();
//                            double normalized = 1.0 - Math.min(freq / maxFrequency, 1.0);
//                            double myWeight = 1 + weight * (normalized - 0.5);
//                            myWeight = Math.max(0.01, myWeight);
//                            return match.getLengthOfSecond() * myWeight;
//                        })
//                        .sum());
//    }

    public double getMinFrequency(JPlagComparison comparison) {
        return comparison.matches().stream().mapToDouble(Match::getFrequencyWeight).min().orElse(0.0);
    }

    public double getMaxFrequency(JPlagComparison comparison) {
        return comparison.matches().stream().mapToDouble(Match::getFrequencyWeight).max().orElse(1.0);
    }

}