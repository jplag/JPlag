package de.jplag.antlr;

import java.util.function.Predicate;

import de.jplag.TokenType;

/**
 * Contains a type of token to extract and a condition when to do so.
 * @param <T> The antlr type being mapped
 */
class TypeBuilder<T> {
    private final Predicate<T> condition;
    private final TokenType tokenType;

    /**
     * New instance
     * @param tokenType The token type
     * @param condition The condition
     */
    public TypeBuilder(TokenType tokenType, Predicate<T> condition) {
        this.condition = condition;
        this.tokenType = tokenType;
    }

    /**
     * Checks if the token should be extracted for this node.
     * @param value The node to check
     * @return true, if the token should be extracted
     */
    public boolean matches(T value) {
        return this.condition.test(value);
    }

    /**
     * @return The type to extract
     */
    public TokenType getTokenType() {
        return tokenType;
    }
}
