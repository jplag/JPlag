package de.jplag.antlr;

import java.util.function.Predicate;

import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.TerminalNode;

/**
 * The visitor for terminals.
 */
public class TerminalVisitor extends AbstractVisitor<TerminalNode> {

    TerminalVisitor(Predicate<TerminalNode> condition) {
        super(condition);
    }

    @Override
    Token extractEnterToken(TerminalNode token) {
        return token.getSymbol();
    }
}
