package de.jplag.highlightextraction.strategy;

import java.util.List;

import de.jplag.TokenType;

/**
 * Strategy that determines a count value of matches across all submissions. For each match, the complete token sequence
 * is added to the frequency map without modification. So the Strategy counts all occurrences of complete matches inside
 * all the complete matches of the comparisons.
 */
public final class CompleteMatchesStrategy extends FrequencyStrategy {
    /**
     * Adds the given token sequence to the map and updates its frequency.
     * @param matchTokenTypes List of tokens types representing the match.
     */
    @Override
    public void processMatchTokenTypes(List<TokenType> matchTokenTypes) {
        incrementSequence(matchTokenTypes);
    }

    /**
     * Calculates the frequency of a match according to the strategy.
     * @param matchTokens token type sequence of the match
     * @return a weight for the match
     */
    @Override
    public double calculateMatchCount(List<TokenType> matchTokens) {
        return getCount(matchTokens);
    }
}
