package de.jplag.highlight_extraction;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import de.jplag.TokenType;

public final class SubSequenceUtil {
    private SubSequenceUtil() {
        // private constructor to prevent instantiation
    }

    /**
     * Calculates all possible Sublists with min minSubSequenceSize length of the Match matchTokenTypes
     * @param matchTokenTypes matchTokenTypes Of the Match
     * @param minSubSequenceSize minimum considered minSubSequenceSize of the Sublist
     * @return List of all as considered Sublists
     */
    public static List<List<TokenType>> getSubSequences(List<TokenType> matchTokenTypes, int minSubSequenceSize) {
        List<List<TokenType>> subSequences = new LinkedList<>();
        if (matchTokenTypes.size() >= minSubSequenceSize) {
            for (int subSequenceSize = minSubSequenceSize; subSequenceSize <= matchTokenTypes.size(); subSequenceSize++) {
                for (int windowStartIndex = 0; windowStartIndex <= matchTokenTypes.size() - subSequenceSize; windowStartIndex++) {
                    List<TokenType> subSequence = new ArrayList<>(matchTokenTypes.subList(windowStartIndex, windowStartIndex + subSequenceSize));
                    subSequences.add(subSequence);
                }
            }
        }
        return subSequences;
    }
}
