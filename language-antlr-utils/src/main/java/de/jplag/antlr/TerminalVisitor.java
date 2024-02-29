package de.jplag.antlr;

import java.util.function.Predicate;

import org.antlr.v4.runtime.Token;

/**
 * The visitor for terminals.
 */
public class TerminalVisitor extends AbstractVisitor<Token> {

    TerminalVisitor(Predicate<org.antlr.v4.runtime.Token> condition) {
        super(condition);
    }

    @Override
    Token extractEnterToken(Token token) {
        return token;
    }
}
