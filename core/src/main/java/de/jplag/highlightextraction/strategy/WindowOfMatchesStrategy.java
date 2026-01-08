package de.jplag.highlightextraction.strategy;

import java.util.ArrayList;
import java.util.List;

import de.jplag.TokenType;
import de.jplag.highlightextraction.WindowIterator;

/**
 * Strategy that uses a fixed window size to create submatches of a match sequence in a comparison and calculates their
 * frequencies over all submissions. So the Strategy counts all occurrences of the contiguous windows inside all the
 * contiguous windows of the matches from the comparisons.
 */
public final class WindowOfMatchesStrategy extends FrequencyStrategy {
    /**
     * The window size for the considered window sequences.
     */
    private final int windowLength;

    /**
     * Creates a new {@link WindowOfMatchesStrategy} with the given window length.
     * @param windowLength is the window length.
     */
    public WindowOfMatchesStrategy(int windowLength) {
        this.windowLength = windowLength;
    }

    /**
     * Adds all submatches with window length of the matches to a map using the token sequence as the key.
     * @param matchTokenTypes Token list of the match.
     */
    @Override
    public void processMatchTokenTypes(List<TokenType> matchTokenTypes) {
        WindowIterator<TokenType> tokenTypeWindows = new WindowIterator<>(matchTokenTypes, windowLength);
        tokenTypeWindows.forEachRemaining(this::incrementSequence);
    }

    /**
     * Calculates the weight of a match considering window sized subsequences of the match.
     * @param matchTokenTypes tokenType sequence of the match
     * @return a weight for the match
     */
    @Override
    public double calculateMatchCount(List<TokenType> matchTokenTypes) {
        WindowIterator<TokenType> tokenTypeWindows = new WindowIterator<>(matchTokenTypes, windowLength);
        List<Integer> frequencies = new ArrayList<>();
        tokenTypeWindows.forEachRemaining(window -> frequencies.add(getCount(window)));
        return frequencies.stream().mapToInt(Integer::intValue).average().orElse(0.0);
    }

}
