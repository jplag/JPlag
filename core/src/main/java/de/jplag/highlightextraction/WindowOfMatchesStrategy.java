package de.jplag.highlightextraction;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

import de.jplag.TokenType;

/**
 * Strategy that uses a fixed window size to create submatches of a match sequence in a comparison and calculates their
 * frequencies over all submissions. So the Strategy counts all occurrences of the contiguous windows inside all the
 * contiguous windows of the matches from the comparisons.
 */
public class WindowOfMatchesStrategy implements FrequencyStrategy {

    /**
     * Adds all submatches with window length of the matches to a map using the token sequence as the key.
     * @param matchTokenTypes Token list of the match.
     * @param addSequenceKey Consumer that adds the sequence to the list, without counting the frequency.
     * @param addSequence Consumer that adds the sequence to the list, and updates the frequency.
     * @param strategyNumber The length of the considered token window.
     */
    @Override
    public void processMatchTokenTypes(List<TokenType> matchTokenTypes, Consumer<List<TokenType>> addSequenceKey,
            Consumer<List<TokenType>> addSequence, int strategyNumber) {
        List<List<TokenType>> windowSequences = getWindowSequences(matchTokenTypes, strategyNumber);
        for (List<TokenType> windowSequence : windowSequences) {
            addSequence.accept(windowSequence);
        }
    }

    /**
     * Calculates all possible contiguous Sublists with length of windowSize.
     * @param matchTokenTypes tokens of the Match.
     * @param windowSize considered size of the sublists.
     * @return List of all as considered sublists.
     */
    public static List<List<TokenType>> getWindowSequences(List<TokenType> matchTokenTypes, int windowSize) {
        List<List<TokenType>> windowSequences = new LinkedList<>();

        for (int windowStartIndex = 0; windowStartIndex <= matchTokenTypes.size() - windowSize; windowStartIndex++) {
            List<TokenType> windowSequence = new ArrayList<>(matchTokenTypes.subList(windowStartIndex, windowStartIndex + windowSize));
            windowSequences.add(windowSequence);
        }
        return windowSequences;
    }
}
