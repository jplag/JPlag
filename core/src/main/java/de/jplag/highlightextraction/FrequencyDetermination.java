package de.jplag.highlightextraction;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.jplag.*;

/**
 * Calculates frequencies of match subsequences across all comparisons according to different strategies.
 */
public class FrequencyDetermination {
    private final Map<Integer, Integer> matchFrequencyMap;
    private final FrequencyStrategy frequencyStrategy;
    private final int strategyNumber;

    /**
     * Constructor.
     * @param frequencyStrategy The chosen strategy for frequency calculation.
     * @param strategyNumber Parameter used by certain strategies to determine submatch length.
     */
    public FrequencyDetermination(FrequencyStrategy frequencyStrategy, int strategyNumber) {
        this.matchFrequencyMap = new HashMap<>();
        this.frequencyStrategy = frequencyStrategy;
        this.strategyNumber = strategyNumber;
    }

    /**
     * Applies the "create" and "check" methods of the frequency strategy.
     * @param comparisons contains information of matches between two submissions.
     */
    public void buildFrequencyMap(List<JPlagComparison> comparisons) {
        for (JPlagComparison comparison : comparisons) {
            Submission leftSubmission = comparison.firstSubmission();
            List<Token> submissionTokens = leftSubmission.getTokenList();
            List<TokenType> submissionTokenTypes = submissionTokens.stream().map(Token::getType).toList();

            for (Match match : comparison.matches()) {
                int startIndexOfMatch = match.startOfFirst();
                int lengthOfMatch = match.lengthOfFirst();
                if (startIndexOfMatch + lengthOfMatch > submissionTokenTypes.size()) {
                    throw new RuntimeException("startIndexOfMatch + lengthOfMatch <= submissionTokenTypes.size()");
                }
                List<TokenType> matchTokenTypes = submissionTokenTypes.subList(startIndexOfMatch, startIndexOfMatch + lengthOfMatch);
                frequencyStrategy.processMatchTokenTypes(matchTokenTypes, matchFrequencyMap, strategyNumber);
            }
        }
    }

    /**
     * @return Map containing (sub-)matches and their frequency according to the strategy.
     */
    public Map<Integer, Integer> getMatchFrequencyMap() {
        return matchFrequencyMap;
    }
}