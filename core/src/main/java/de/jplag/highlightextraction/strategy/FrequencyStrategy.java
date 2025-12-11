package de.jplag.highlightextraction.strategy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import de.jplag.JPlagComparison;
import de.jplag.Match;
import de.jplag.TokenType;
import de.jplag.highlightextraction.TokenSequenceUtil;

/**
 * Interface for different frequency calculation strategies. Implementations define how submatches are considered in the
 * frequency calculation of matches.
 */
public abstract class FrequencyStrategy {

    private final Map<List<TokenType>, Integer> matchCounts;

    protected FrequencyStrategy() {
        this.matchCounts = new HashMap<>();
    }

    /**
     * Count the frequency of all matches in the given list of comparisons.
     * @param comparisons are the comparisons.
     */
    public void processMatches(List<JPlagComparison> comparisons) {
        for (JPlagComparison comparison : comparisons) {
            for (Match match : comparison.matches()) {
                processMatch(comparison, match);
            }
        }
    }

    /**
     * Count the frequency of the match in the given comparison.
     * @param comparison is the comparison.
     * @param match is the match.
     */
    public void processMatch(JPlagComparison comparison, Match match) {
        List<TokenType> tokenTypes = TokenSequenceUtil.tokenTypesFor(comparison, match);
        processMatchTokenTypes(tokenTypes);
    }

    /**
     * Updates the frequency map with token subsequences and their Counts according to the implemented strategy.
     * @param matchTokenTypes List of match token types representing the match.
     */
    protected abstract void processMatchTokenTypes(List<TokenType> matchTokenTypes);

    /**
     * Returns weight factor, as frequency value for the matches.
     * @param matchTokens tokenType sequence of the match
     * @return a weight for the match
     */
    public abstract double calculateMatchCount(List<TokenType> matchTokens);

    protected void registerSequence(List<TokenType> sequence) {
        matchCounts.putIfAbsent(sequence, 0);
    }

    /**
     * Updates the frequency of the given sequence in the frequency map.
     * @param sequence The token sequence whose frequency will be updated.
     */
    protected void incrementSequence(List<TokenType> sequence) {
        matchCounts.compute(sequence, (_, count) -> Objects.isNull(count) ? 1 : count + 1);
    }

    protected int getCount(List<TokenType> sequence) {
        return matchCounts.getOrDefault(sequence, 0);
    }

    /**
     * Gets the count of all considered token sequences.
     * @return the count map.
     */
    public Map<List<TokenType>, Integer> getResult() {
        return matchCounts;
    }
}
