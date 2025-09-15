package de.jplag.highlightextraction;

import java.util.List;

import de.jplag.Match;
import de.jplag.TokenType;

public class FrequencyUtil {
    private FrequencyUtil() {
        // private constructor to prevent instantiation
    }

    public static List<TokenType> matchesToMatchTokenTypes(Match match, List<TokenType> submissionTokenTypes) {
        int startIndexOfMatch = match.startOfFirst();
        int lengthOfMatch = match.lengthOfFirst();
        if (startIndexOfMatch + lengthOfMatch > submissionTokenTypes.size()) {
            throw new IllegalArgumentException("startIndexOfMatch + lengthOfMatch <= submissionTokenTypes.size()");
        }
        return submissionTokenTypes.subList(startIndexOfMatch, startIndexOfMatch + lengthOfMatch);
    }

}
