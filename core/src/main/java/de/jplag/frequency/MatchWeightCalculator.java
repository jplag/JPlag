package de.jplag.frequency;

import java.util.List;
import java.util.Map;

import de.jplag.JPlagComparison;
import de.jplag.Match;
import de.jplag.Token;
import de.jplag.TokenType;

/**
 * Calculates frequency values of the matches and writes them into a map.
 */
public class MatchWeightCalculator {
    private final FrequencyStrategy strategy;
    private final Map<List<TokenType>, Integer> frequencyMap;
    private final MatchFrequency matchFrequency;

    /**
     * Constructor defining the used frequency strategy and frequency map.
     * @param strategy chosen to determine the frequency of a match
     * @param frequencyMap build frequencyMap based on the strategy
     */
    public MatchWeightCalculator(FrequencyStrategy strategy, Map<List<TokenType>, Integer> frequencyMap) {
        this.strategy = strategy;
        this.frequencyMap = frequencyMap;
        this.matchFrequency = new MatchFrequency();
    }

    /**
     * Calculates frequency value for a match.
     * @param match the match to determine the frequency for
     * @param matchToken token sequence of the match
     */
    public void weightMatch(Match match, List<TokenType> matchToken) {
        double matchWeight = strategy.calculateMatchFrequency(match, frequencyMap, matchToken);
        matchFrequency.matchFrequencyMap().put(matchToken, matchWeight);
    }

    /**
     * Calculates frequency value for all matches.
     * @param matches the matches to determine the frequency for
     * @param firstSubmissionToken token sequence of the comparison
     * @return the frequency of the match
     */
    public MatchFrequency weightAllMatches(List<Match> matches, List<TokenType> firstSubmissionToken) {
        for (Match match : matches) {
            List<TokenType> matchTokens = FrequencyUtil.matchesToMatchTokenTypes(match, firstSubmissionToken);
            weightMatch(match, matchTokens);
        }
        return matchFrequency;
    }

    /**
     * Calculates frequency value for all matches.
     * @param comparisons list of comparisons to weight
     * @return the frequency of the match
     */
    public MatchFrequency weightAllComparisons(List<JPlagComparison> comparisons) {
        for (JPlagComparison comparison : comparisons) {
            List<Token> firstSubmissionToken = comparison.firstSubmission().getTokenList();
            List<TokenType> firstSubmissionTokenTypes = firstSubmissionToken.stream().map(Token::getType).toList();
            weightAllMatches(comparison.matches(), firstSubmissionTokenTypes);
        }
        return matchFrequency;
    }

}
