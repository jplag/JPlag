package de.jplag.antlr;

import de.jplag.Token;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TokenCollector {
    private final List<Token> collected;

    public TokenCollector() {
        this.collected = new ArrayList<>();
    }

    public void addToken(Token token) {
        this.collected.add(token);
    }

    public List<Token> getTokens() {
        return Collections.unmodifiableList(this.collected);
    }
}
