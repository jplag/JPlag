package de.jplag.swift;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.ParserRuleContext;

import de.jplag.antlr.AbstractAntlrListener;
import de.jplag.antlr.AbstractAntlrParserAdapter;
import de.jplag.swift.grammar.Swift5Lexer;
import de.jplag.swift.grammar.Swift5Parser;

/**
 * ANTLR-based parser adapter for Swift.
 */
public class SwiftParserAdapter extends AbstractAntlrParserAdapter<Swift5Parser> {
    @Override
    protected Lexer createLexer(CharStream input) {
        return new Swift5Lexer(input);
    }

    @Override
    protected Swift5Parser createParser(CommonTokenStream tokenStream) {
        return new Swift5Parser(tokenStream);
    }

    @Override
    protected ParserRuleContext getEntryContext(Swift5Parser parser) {
        return parser.top_level();
    }

    @Override
    protected AbstractAntlrListener getListener() {
        return new SwiftListener();
    }
}
