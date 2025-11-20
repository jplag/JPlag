package de.jplag.comparison;

import java.util.List;

import de.jplag.Submission;
import de.jplag.Token;

/**
 * Functional interface for a method returning the token list of a submission.
 */
@FunctionalInterface
public interface TokenListSupplier {
    /**
     * Returns the token list of a single submission.
     * @param submission Submission to retrieve token list from
     * @return Token list of submission
     */
    List<Token> getTokenList(Submission submission);
}
