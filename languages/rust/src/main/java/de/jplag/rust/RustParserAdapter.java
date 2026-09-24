package de.jplag.rust;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.ParserRuleContext;

import de.jplag.antlr.AbstractAntlrListener;
import de.jplag.antlr.AbstractAntlrParserAdapter;
import de.jplag.rust.grammar.RustLexer;
import de.jplag.rust.grammar.RustParser;

/**
 * ANTLR-based parser adapter for rust.
 */
public class RustParserAdapter extends AbstractAntlrParserAdapter<RustParser> {
    @Override
    protected Lexer createLexer(CharStream input) {
        return new RustLexer(input);
    }

    @Override
    protected RustParser createParser(CommonTokenStream tokenStream) {
        return new RustParser(tokenStream);
    }

    @Override
    protected ParserRuleContext getEntryContext(RustParser parser) {
        return parser.crate();
    }

    @Override
    protected AbstractAntlrListener getListener() {
        return new RustListener();
    }
}
