package de.jplag.highlightextraction;

import java.util.List;

import de.jplag.JPlagComparison;
import de.jplag.Match;
import de.jplag.Token;
import de.jplag.TokenType;

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
    MatchWeightingFunction strategy;
    private final MatchFrequency matchFrequency;

    /**
     * Constructor defines comparisons and strategy for the similarity calculation.
     * @param comparisons considered comparisons to calculate the similarity score for
     * @param strategy chosen weighting function
     * @param matchFrequency the matchFrequency containing the map that maps a match to its frequency
     */
    public MatchFrequencyWeighting(List<JPlagComparison> comparisons, MatchWeightingFunction strategy, MatchFrequency matchFrequency) {
        this.comparisons = comparisons;
        this.strategy = strategy;
        this.matchFrequency = matchFrequency;
    }

    /**
     * Calculates the similarity score for a comparison.
     * @param comparison considered comparison to calculate the similarity score for
     * @param weightingFactor weighting factor, is factor for the (max) influence of the frequency
     * @param isFrequencyAnalysisEnabled if the frequency shall be considered
     * @return similarity of the comparison
     */
    public JPlagComparison weightedComparisonSimilarity(JPlagComparison comparison, double weightingFactor, boolean isFrequencyAnalysisEnabled) {
        double frequencyWeightedSimilarity = frequencySimilarity(comparison, weightingFactor);
        return new JPlagComparison(comparison, frequencyWeightedSimilarity, isFrequencyAnalysisEnabled);
    }

    private double getFrequencyFromMap(JPlagComparison comparison, Match match) {
        List<TokenType> submissionTokenTypes = comparison.firstSubmission().getTokenList().stream().map(Token::getType).toList();
        List<TokenType> matchTokens = FrequencyUtil.matchesToMatchTokenTypes(match, submissionTokenTypes);
        return matchFrequency.matchFrequencyMap().getOrDefault(matchTokens, 0.0);
    }

    /**
     * Calculates the similarity score for a comparison. The score gets influenced form the frequency of the match according
     * to the chosen Frequency Strategy, Similarity Strategy and weighting factor.
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
     * Changes the considered match length according to the frequency Weight, depending on the frequency similarity
     * weightingFunction.
     * @param comparison considered comparison to calculate the similarity score for
     * @param frequencyWeight weighting factor, is factor for the (max) influence of the frequency
     * @param firstSubmission considered submission to calculate the frequency, the frequency for both submissions will be
     * calculated
     * @param weightingFunction chosen weighting function
     * @return the similarity score
     */
    public int getWeightedMatchLength(JPlagComparison comparison, double frequencyWeight, boolean firstSubmission,
            MatchWeightingFunction weightingFunction) {
        double minimumWeight;
        double maximumWeight;
        double maximumFoundFrequency = 0.0;
        maximumFoundFrequency = getMaximumFoundFrequency(maximumFoundFrequency);

        if (weightingFunction == MatchFrequencyWeightingFunction.PROPORTIONAL) {
            minimumWeight = 0.01;
        } else {
            minimumWeight = 1.0;
        }
        maximumWeight = 2.0;

        double finalMaximumFoundFrequency = maximumFoundFrequency;
        double consideredLengthOfMatchesWithFrequencyInfluence = comparison.matches().stream().mapToDouble(match -> {

            double frequencyOfMatch = getFrequencyFromMap(comparison, match);

            if (frequencyOfMatch == 0) {
                if (firstSubmission) {
                    return match.lengthOfFirst();
                } else {
                    return match.lengthOfSecond();
                }
            }

            double influenceOnMatchLength = getInfluenceOnMatchLength(frequencyWeight, weightingFunction, frequencyOfMatch,
                    finalMaximumFoundFrequency, minimumWeight, maximumWeight);

            if (firstSubmission) {
                return match.lengthOfFirst() * influenceOnMatchLength;
            } else {
                return match.lengthOfSecond() * influenceOnMatchLength;
            }
        }).sum();

        return (int) Math.round(consideredLengthOfMatchesWithFrequencyInfluence);
    }

    /**
     * Calculates the frequency of the match that is found most frequent across all matches in all comparisons.
     * @param maximumFoundFrequency the frequency of the match that is found most frequent across all matches in all
     * comparisons.
     * @return this frequency
     */
    private double getMaximumFoundFrequency(double maximumFoundFrequency) {
        if (matchFrequency.matchFrequencyMap().isEmpty()) {
            maximumFoundFrequency = 1.0;
        } else {
            for (double frequency : matchFrequency.matchFrequencyMap().values()) {
                if (frequency > maximumFoundFrequency) {
                    maximumFoundFrequency = frequency;
                }
            }
        }

        if (maximumFoundFrequency == 0.0)
            maximumFoundFrequency = 1.0;
        return maximumFoundFrequency;
    }

    /**
     * Calculates how much the considered length of the match will be changed to influence the similarity score according to
     * the matches' frequency.
     * @param frequencyWeight weighting factor, is factor for the (max) influence of the frequency
     * @param weightingFunction chosen weighting function
     * @param matchFrequency calculated frequency of the match
     * @param finalMaximumFoundFrequency highes frequency that occurs across all matches in all comparisons
     * @param minimumWeight minimum possible Weighting influence
     * @param maximumWeight maximum possible Weighting influence
     * @return the influence the frequency has on the considered match length
     */
    private static double getInfluenceOnMatchLength(double frequencyWeight, MatchWeightingFunction weightingFunction, double matchFrequency,
            double finalMaximumFoundFrequency, double minimumWeight, double maximumWeight) {
        if (Double.isNaN(matchFrequency) || matchFrequency < 0.0)
            matchFrequency = 0.0;
        double relativeFrequencyOfMatch = 1.0 - Math.min(matchFrequency / finalMaximumFoundFrequency, 1.0);
        double weightOfMatch = weightingFunction.computeWeight(minimumWeight, maximumWeight, relativeFrequencyOfMatch);
        return (1 - frequencyWeight) + frequencyWeight * weightOfMatch;
    }
}