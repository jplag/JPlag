package de.jplag.highlightextraction;

import java.util.List;
import java.util.Map;

import de.jplag.JPlagComparison;
import de.jplag.Match;
import de.jplag.Token;
import de.jplag.TokenType;

/**
 * Calculates isFrequencyAnalysisEnabled values and writes them into the matches.
 */
public class MatchWeightCalculator {
    private final FrequencyStrategy strategy;
    private final Map<List<TokenType>, Integer> frequencyMap;
    private final MatchFrequency matchFrequency;

    /**
     * Constructor defining the used isFrequencyAnalysisEnabled strategy and isFrequencyAnalysisEnabled map.
     * @param strategy chosen to determine the isFrequencyAnalysisEnabled of a match
     * @param frequencyMap build frequencyMap based on the strategy
     */
    public MatchWeightCalculator(FrequencyStrategy strategy, Map<List<TokenType>, Integer> frequencyMap) {
        this.strategy = strategy;
        this.frequencyMap = frequencyMap;
        this.matchFrequency = new MatchFrequency();
    }

    /**
     * Calculates isFrequencyAnalysisEnabled value for a match.
     * @param match the match to determine the isFrequencyAnalysisEnabled for
     * @param matchToken token sequence of the match
     */
    public void weightMatch(Match match, List<TokenType> matchToken) {
        double matchWeight = strategy.calculateMatchFrequency(match, frequencyMap, matchToken);
        matchFrequency.matchFrequencyMap().put(matchToken, matchWeight);
    }

    /**
     * Calculates isFrequencyAnalysisEnabled value for all matches.
     * @param matches the matches to determine the isFrequencyAnalysisEnabled for
     * @param firstSubmissionToken token sequence of the comparison
     * @return the isFrequencyAnalysisEnabled of the match
     */
    public MatchFrequency weightAllMatches(List<Match> matches, List<TokenType> firstSubmissionToken) {
        for (Match match : matches) {
            List<TokenType> matchTokens = FrequencyUtil.matchesToMatchTokenTypes(match, firstSubmissionToken);
            weightMatch(match, matchTokens);
        }
        return matchFrequency;
    }

    /**
     * Calculates isFrequencyAnalysisEnabled value for all matches.
     * @param comparisons list of comparisons to weight
     * @return the isFrequencyAnalysisEnabled of the match
     */
    public MatchFrequency weightAllComparisons(List<JPlagComparison> comparisons) {
        for (JPlagComparison comparison : comparisons) {
            List<Token> FirstSubmissionToken = comparison.firstSubmission().getTokenList();
            List<TokenType> FirstSubmissionTokenTypes = FirstSubmissionToken.stream().map(Token::getType).toList();
            weightAllMatches(comparison.matches(), FirstSubmissionTokenTypes);
        }
        return matchFrequency;
    }

}
