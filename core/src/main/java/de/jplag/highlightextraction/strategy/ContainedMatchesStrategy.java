package de.jplag.highlightextraction.strategy;

import java.util.Iterator;
import java.util.List;
import java.util.stream.IntStream;

import de.jplag.TokenType;
import de.jplag.highlightextraction.SublistIterator;

/**
 * Strategy that counts all occurrences of complete matches inside all complete matches and contiguous submatches from
 * the comparisons, if the submatches are longer than minLength.
 */
public final class ContainedMatchesStrategy extends FrequencyStrategy {
    /**
     * Minimum considered subsequence length.
     */
    private final int minLength;

    /**
     * Constructs a new {@link ContainedMatchesStrategy} using the given minimum length of contained matches.
     * @param minLength is the minimum length of contained matches.
     */
    public ContainedMatchesStrategy(int minLength) {
        this.minLength = minLength;
    }

    /**
     * Adds all submatches of the given match to the map if their length is at least minLength long, using the token
     * sequence as key. The full match itself is also added if it is at least minLength.
     * @param matchTokenTypes List of tokens representing the match.
     */
    @Override
    public void processMatchTokenTypes(List<TokenType> matchTokenTypes) {
        Iterator<List<TokenType>> subsequences = new SublistIterator<>(matchTokenTypes, minLength);
        subsequences.forEachRemaining(this::registerSequence);

        if (matchTokenTypes.size() >= minLength) {
            incrementSequence(matchTokenTypes);
        }
    }

    /**
     * Calculates the weight of a match considering subsequences of the match.
     * @param matchToken tokenType sequence of the match
     * @return a weight for the match
     */
    @Override
    public double calculateMatchCount(List<TokenType> matchToken) {
        Iterator<List<TokenType>> subSequences = new SublistIterator<>(matchToken, minLength);
        IntStream.Builder builder = IntStream.builder();

        subSequences.forEachRemaining(subsequence -> {
            int frequency = getCount(subsequence);
            if (frequency > 0) {
                builder.accept(frequency);
            }
        });

        return builder.build().average().orElse(0.0);
    }
}
