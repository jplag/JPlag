package de.jplag;

import java.util.List;
import java.util.concurrent.ConcurrentMap;

/**
 * Defines an interface for when tokens are considered equivalent. This is used to determine matches between tokens by
 * using a two step approach: First, the primary types of the tokens are compared using
 * {@link #arePrimaryEquivalent(int, int)}. If they are considered equivalent, the secondary types are
 * compared using {@link #areSecondaryEquivalent(TokenType, TokenType)}.
 */
public interface TokenEquivalenceModel {

    /**
     * Gets the primary {@link TokenType} of a token.
     * @param token The token
     * @return The primary type
     */
    TokenType getPrimaryType(Token token);

    /**
     * Ensures that the tokens have the correct type assigned. By default, this method does nothing and returns true.
     * @param tokens The tokens
     * @return True, if the types are ensured
     */
    default boolean ensureTokenType(List<Token> tokens) {
        return true;
    }

    /**
     * Determines whether two tokens are primary equivalent based on their int representation. Uses an int representation of
     * the token types for performance reasons.
     * @param leftValue the left token value
     * @param rightValue the right token value
     * @return True, if the primary token values are equivalent
     */
    boolean arePrimaryEquivalent(int leftValue, int rightValue);

    /**
     * Determines whether two tokens are secondary equivalent based on their TokenType representation. By default, this
     * method returns true.
     * @param leftType the left token type
     * @param rightType the right token type
     * @return True, if the secondary token types are equivalent
     */
    default boolean areSecondaryEquivalent(TokenType leftType, TokenType rightType) {
        return true;
    }

}
