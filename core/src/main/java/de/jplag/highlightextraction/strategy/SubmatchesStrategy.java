package de.jplag.highlightextraction.strategy;

import java.util.Iterator;
import java.util.List;
import java.util.stream.StreamSupport;

import de.jplag.TokenType;
import de.jplag.highlightextraction.SublistIterator;

/**
 * Strategy that counts all occurrences of complete matches and contiguous submatches from the comparisons, if the
 * matches and submatches are longer than minLength.
 */
public final class SubmatchesStrategy extends FrequencyStrategy {
    /**
     * Minimum considered subsequence length.
     */
    private final int minLength;

    /**
     * Creates a new {@link SubmatchesStrategy} with the given minimum submatch length.
     * @param minLength is the minimum submatch length.
     */
    public SubmatchesStrategy(int minLength) {
        this.minLength = minLength;
    }

    /**
     * Creates submatches to build the keys and adds their frequencies to the frequencyMap.
     * @param matchTokenTypes List of matchTokenTypes representing the match.
     */
    @Override
    public void processMatchTokenTypes(List<TokenType> matchTokenTypes) {
        Iterator<List<TokenType>> subSequences = new SublistIterator<>(matchTokenTypes, minLength);
        subSequences.forEachRemaining(this::incrementSequence);
    }

    /**
     * Calculates the weight of a match considering subsequences of the match.
     * @param matchToken tokenType sequence of the match
     * @return a weight for the match
     */
    @Override
    public double calculateMatchCount(List<TokenType> matchToken) {
        Iterable<List<TokenType>> subSequences = () -> new SublistIterator<>(matchToken, minLength);

        return StreamSupport.stream(subSequences.spliterator(), false).mapToInt(this::getCount).average().orElse(0.0);

    }
}
