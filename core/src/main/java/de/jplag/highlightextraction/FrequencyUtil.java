package de.jplag.highlightextraction;

import java.util.List;

import de.jplag.Match;
import de.jplag.TokenType;

/**
 * Contains methods that are used in multiple highlight extraction classes.
 */
public class FrequencyUtil {
    private FrequencyUtil() {
        // private constructor to prevent instantiation
    }

    /**
     * The Token Type representation of a match.
     * @param match that's representation is created
     * @param submissionTokenTypes the Token Type list of the complete submission
     * @return the Token Type list of the match
     */
    public static List<TokenType> matchesToMatchTokenTypes(Match match, List<TokenType> submissionTokenTypes) {
        int startIndexOfMatch = match.startOfFirst();
        int lengthOfMatch = match.lengthOfFirst();
        if (startIndexOfMatch + lengthOfMatch > submissionTokenTypes.size()) {
            throw new IllegalArgumentException("startIndexOfMatch + lengthOfMatch <= submissionTokenTypes.size()");
        }
        return submissionTokenTypes.subList(startIndexOfMatch, startIndexOfMatch + lengthOfMatch);
    }

}
