package de.jplag.haskell;

import de.jplag.antlr.AbstractAntlrListener;
import de.jplag.antlr.AbstractAntlrParserAdapter;
import de.jplag.haskell.grammar.HaskellLexer;
import de.jplag.haskell.grammar.HaskellParser;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.ParserRuleContext;

public class HaskellParserAdapter extends AbstractAntlrParserAdapter<HaskellParser> {
    private static final HaskellListener listener = new HaskellListener();

    @Override
    protected Lexer createLexer(CharStream input) {
        return new HaskellLexer(input);
    }

    @Override
    protected HaskellParser createParser(CommonTokenStream tokenStream) {
        return new HaskellParser(tokenStream);
    }

    @Override
    protected ParserRuleContext getEntryContext(HaskellParser parser) {
        return parser.module();
    }

    @Override
    protected AbstractAntlrListener getListener() {
        return listener;
    }
}
