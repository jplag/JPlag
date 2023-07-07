package de.jplag.antlr;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.jplag.Token;

/**
 * Collects the tokens during parsing.
 */
public class TokenCollector {
    private final List<Token> collected;

    /**
     * New instance
     */
    public TokenCollector() {
        this.collected = new ArrayList<>();
    }

    /**
     * Adds a token to the collector
     * @param token The token to add
     */
    public void addToken(Token token) {
        this.collected.add(token);
    }

    /**
     * @return All collected tokens
     */
    public List<Token> getTokens() {
        return Collections.unmodifiableList(this.collected);
    }
}
