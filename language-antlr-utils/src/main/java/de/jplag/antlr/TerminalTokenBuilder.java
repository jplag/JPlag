package de.jplag.antlr;

import java.io.File;
import java.util.function.Predicate;

import org.antlr.v4.runtime.Token;

import de.jplag.TokenType;

/**
 * Builds tokens from terminal antlr nodes
 */
public class TerminalTokenBuilder extends TokenBuilder<Token> {
    /**
     * New instance
     * @param tokenType The token type
     * @param condition The condition
     * @param collector The token collector for the listener
     * @param file The file the listener is for
     */
    TerminalTokenBuilder(TokenType tokenType, Predicate<Token> condition, TokenCollector collector, File file) {
        super(tokenType, condition, collector, file);
    }

    @Override
    protected Token getAntlrToken(Token antlrContent) {
        return antlrContent;
    }
}
