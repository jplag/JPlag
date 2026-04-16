package de.jplag.bash;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.ParserRuleContext;

import de.jplag.antlr.AbstractAntlrListener;
import de.jplag.antlr.AbstractAntlrParserAdapter;
import de.jplag.bash.grammar.BashLexer;
import de.jplag.bash.grammar.BashParser;

/**
 * ANTLR-based parser adapter for Bash.
 */
public class BashParserAdapter extends AbstractAntlrParserAdapter<BashParser> {
    private static final BashListener listener = new BashListener();

    @Override
    protected Lexer createLexer(CharStream input) {
        return new BashLexer(input);
    }

    @Override
    protected BashParser createParser(CommonTokenStream tokenStream) {
        return new BashParser(tokenStream);
    }

    @Override
    protected ParserRuleContext getEntryContext(BashParser parser) {
        return parser.program();
    }

    @Override
    protected AbstractAntlrListener getListener() {
        return listener;
    }
}
