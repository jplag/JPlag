package de.jplag.highlightextraction;

import java.util.List;

import de.jplag.JPlagComparison;
import de.jplag.Match;
import de.jplag.Submission;
import de.jplag.Token;
import de.jplag.TokenType;

/**
 * Utility class for extracting sequences of token types (token sequences for brevity) out of submissions.
 */
public class TokenSequenceUtil {

    /**
     * Extracts the token types for the given submission.
     * @param submission is the submission.
     * @return the token types.
     */
    public static List<TokenType> tokenTypesFor(Submission submission) {
        return submission.getTokenList().stream().map(Token::getType).toList();
    }

    /**
     * Extracts the token types for the given match of the given comparison.
     * @param comparison is the comparison.
     * @param match is the match.
     * @return the token types.
     */
    public static List<TokenType> tokenTypesFor(JPlagComparison comparison, Match match) {
        return tokenTypesFor(comparison.firstSubmission()).subList(match.startOfFirst(), match.endOfFirst() + 1);
    }
}