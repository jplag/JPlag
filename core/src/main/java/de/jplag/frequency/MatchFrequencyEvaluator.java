package de.jplag.frequency;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.jplag.JPlagComparison;
import de.jplag.Match;
import de.jplag.Token;
import de.jplag.TokenType;

/**
 * Calculates absolute frequencies for token sequences of matches.
 */
public class MatchFrequencyEvaluator {
    private final FrequencyStrategy strategy;
    private final Map<List<TokenType>, Integer> frequencyMap;
    private final Map<List<TokenType>, Double> matchFrequency;

    /**
     * Constructor defining the used frequency strategy and frequency map.
     * @param strategy chosen to determine the frequency of a match
     * @param frequencyMap build frequencyMap based on the strategy
     */
    public MatchFrequencyEvaluator(FrequencyStrategy strategy, Map<List<TokenType>, Integer> frequencyMap) {
        this.strategy = strategy;
        this.frequencyMap = frequencyMap;
        this.matchFrequency = new HashMap<>();
    }

    /**
     * Calculates absolute frequency value for all matches. Depending on the frequency strategy, this might either be just
     * the occurrences of the match token sequence or also average occurrences of sub sequences.
     * @param matches the matches to determine the frequency for
     * @param tokenSequence token sequence of the comparison
     * @return the frequency of the match
     */
    public Map<List<TokenType>, Double> computeMatchFrequencies(List<Match> matches, List<TokenType> tokenSequence) {
        for (Match match : matches) {
            List<TokenType> matchTokens = FrequencyUtil.matchesToMatchTokenTypes(match, tokenSequence);
            double absoluteFrequency = strategy.calculateMatchFrequency(match, frequencyMap, matchTokens);
            matchFrequency.put(matchTokens, absoluteFrequency);
        }
        return matchFrequency;
    }

    /**
     * Calculates absolute frequency values for all matches of all comparisons.
     * @param comparisons list of comparisons to consider.
     * @return the frequency values.
     */
    public Map<List<TokenType>, Double> computeMatchFrequencies(List<JPlagComparison> comparisons) {
        for (JPlagComparison comparison : comparisons) {
            List<Token> tokenSequence = comparison.firstSubmission().getTokenList(); // TODO this might break with match merging
            List<TokenType> firstSubmissionTokenTypes = tokenSequence.stream().map(Token::getType).toList();
            computeMatchFrequencies(comparison.matches(), firstSubmissionTokenTypes);
        }
        return matchFrequency;
    }

}
