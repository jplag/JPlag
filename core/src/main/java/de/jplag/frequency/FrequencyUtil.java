package de.jplag.frequency;

import java.util.List;

import de.jplag.JPlagComparison;
import de.jplag.Match;
import de.jplag.Token;
import de.jplag.TokenType;

/**
 * Utility class for extracting sequences of token types (token sequences for brevity) out of submissions.
 */
public class FrequencyUtil {

    private FrequencyUtil() {
        // Utility class, not instantiable.
    }

    /**
     * Extracts the token types for the given match of the given comparison.
     * @param comparison is the comparison.
     * @param match is the match.
     * @return the token types.
     */
    public static List<TokenType> tokenTypesFor(JPlagComparison comparison, Match match) {
        return comparison.firstSubmission().getTokenList().subList(match.startOfFirst(), match.endOfFirst() + 1)
                .stream().map(Token::getType).toList();
    }
}