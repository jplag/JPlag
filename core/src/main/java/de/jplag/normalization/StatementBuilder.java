package de.jplag.normalization;

import java.util.ArrayList;
import java.util.List;

import de.jplag.Token;

class StatementBuilder {

    private List<Token> tokens;
    private final int lineNumber;

    StatementBuilder(int lineNumber) {
        this.lineNumber = lineNumber;
        this.tokens = new ArrayList<>();
    }

    int lineNumber() {
        return lineNumber;
    }

    void addToken(Token token) {
        tokens.add(token);
    }

    Statement build() {
        return new Statement(tokens, lineNumber);
    }
}
