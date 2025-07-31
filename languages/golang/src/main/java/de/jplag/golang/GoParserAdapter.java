package de.jplag.golang;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.ParserRuleContext;

import de.jplag.antlr.AbstractAntlrListener;
import de.jplag.antlr.AbstractAntlrParserAdapter;
import de.jplag.golang.grammar.GoLexer;
import de.jplag.golang.grammar.GoParser;

/**
 * ANTLR-based parser adapter for the Go language.
 */
public class GoParserAdapter extends AbstractAntlrParserAdapter<GoParser> {
    @Override
    protected Lexer createLexer(CharStream input) {
        return new GoLexer(input);
    }

    @Override
    protected GoParser createParser(CommonTokenStream tokenStream) {
        return new GoParser(tokenStream);
    }

    @Override
    protected ParserRuleContext getEntryContext(GoParser parser) {
        return parser.sourceFile();
    }

    @Override
    protected AbstractAntlrListener getListener() {
        return new GoListener();
    }
}
