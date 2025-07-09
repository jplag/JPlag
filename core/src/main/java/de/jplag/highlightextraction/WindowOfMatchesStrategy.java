package de.jplag.highlightextraction;

import static de.jplag.highlightextraction.SubSequenceUtil.addSequence;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import de.jplag.TokenType;

/**
 * Strategy that uses a fixed window size to create submatches of a match sequence in a comparison and calculates the
 * frequencies over all submissions.
 */
public class WindowOfMatchesStrategy implements FrequencyStrategy {

    /**
     * Adds all submatches with window length of the matches to a map using the token sequence as the key.
     * @param matchTokenTypes Token list of the match.
     * @param frequencyMap Map that contains token subsequences and how often they occur across comparisons.
     * @param strategyNumber The length of the considered token window.
     */
    @Override
    public void processMatchTokenTypes(List<TokenType> matchTokenTypes, Map<Integer, Integer> frequencyMap, int strategyNumber) {
        Consumer<List<TokenType>> sequenceConsumer = seq -> addSequence(frequencyMap, seq);
        List<List<TokenType>> windowSequences = getWindowSequences(matchTokenTypes, strategyNumber);
        for (List<TokenType> windowSequence : windowSequences) {
            sequenceConsumer.accept(windowSequence);
        }
    }

    /**
     * Calculates all possible Sublists with length of windowSize.
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
