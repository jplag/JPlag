package de.jplag.highlightextraction;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.jplag.*;

/**
 * Calculates frequencies of match subsequences across all comparisons according to different strategies.
 */
public class FrequencyDetermination {
    private final Map<List<TokenType>, Integer> matchFrequencyMap;
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
     * Builds the frequency map by applying the strategy method on all matches found in the given list of comparisons.
     * @param comparisons contains information of matches between two submissions.
     * @throws IllegalArgumentException if match indices are out of range.
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
                    throw new IllegalArgumentException("startIndexOfMatch + lengthOfMatch <= submissionTokenTypes.size()");
                }
                List<TokenType> matchTokenTypes = submissionTokenTypes.subList(startIndexOfMatch, startIndexOfMatch + lengthOfMatch);
                frequencyStrategy.processMatchTokenTypes(matchTokenTypes, this::addSequenceKey, this::addSequence, strategyNumber);
            }
        }
    }

    /**
     * Adds the Sequence to the Frequency map.
     * @param sequence The token sequence whose frequency will be updated.
     */
    private void addSequenceKey(List<TokenType> sequence) {
        matchFrequencyMap.putIfAbsent(sequence, 0);
    }

    /**
     * @return Map containing (sub-)matches and their frequency according to the strategy.
     */
    public Map<List<TokenType>, Integer> getMatchFrequencyMap() {
        return matchFrequencyMap;
    }

    /**
     * Updates the frequency of the given sequence in the frequency map.
     * @param sequence The token sequence whose frequency will be updated.
     */
    private void addSequence(List<TokenType> sequence) {
        matchFrequencyMap.put(sequence, matchFrequencyMap.getOrDefault(sequence, 0) + 1);
    }
}