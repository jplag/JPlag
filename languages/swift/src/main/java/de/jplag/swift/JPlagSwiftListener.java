package de.jplag.swift;

import static de.jplag.swift.SwiftTokenConstants.*;

import org.antlr.v4.runtime.Token;
import de.jplag.swift.grammar.Swift5Parser;
import de.jplag.swift.grammar.Swift5ParserBaseListener;

public class JPlagSwiftListener extends Swift5ParserBaseListener {
    private final SwiftParserAdapter parserAdapter;

    public JPlagSwiftListener(SwiftParserAdapter parserAdapter) {
        this.parserAdapter = parserAdapter;
    }

    /**
     * Passes a token of the given tokenType to the parserAdapter, representing the grammar's token given by token.
     * @param tokenType the custom token type that occurred.
     * @param token the corresponding grammar's token
     */
    private void transformToken(int tokenType, Token token) {
        parserAdapter.addToken(tokenType, token.getLine(), token.getCharPositionInLine() + 1, token.getText().length());
    }

    /**
     * Passes a token of the given tokenType to the parserAdapter, representing the current grammatical context given by
     * start and end.
     * @param tokenType the custom token type that occurred.
     * @param start the first Token of the context
     * @param end the last Token of the context
     */
    private void transformToken(int tokenType, Token start, Token end) {
        parserAdapter.addToken(tokenType, start.getLine(), start.getCharPositionInLine() + 1, end.getStopIndex() - start.getStartIndex() + 1);
    }
}
