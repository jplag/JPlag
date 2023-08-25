package de.jplag.antlr;

import java.util.function.Predicate;

import org.antlr.v4.runtime.Token;

import de.jplag.semantics.VariableRegistry;

/**
 * The visitor for terminals.
 */
public class TerminalVisitor extends AbstractVisitor<Token> {

    TerminalVisitor(Predicate<org.antlr.v4.runtime.Token> condition, TokenCollector tokenCollector, VariableRegistry variableRegistry) {
        super(condition, tokenCollector, variableRegistry);
    }

    Token extractEnterToken(Token token) {
        return token;
    }
}
