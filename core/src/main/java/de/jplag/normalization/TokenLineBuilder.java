package de.jplag.normalization;

import java.util.LinkedList;
import java.util.List;

import de.jplag.Token;

class TokenLineBuilder {

    private List<Token> tokens;
    private final int lineNumber;

    TokenLineBuilder(int lineNumber) {
        this.lineNumber = lineNumber;
        this.tokens = new LinkedList<>();
    }

    int lineNumber() {
        return lineNumber;
    }

    void addToken(Token tok) {
        tokens.add(tok);
    }

    TokenLine build() {
        return new TokenLine(tokens, lineNumber);
    }
}
