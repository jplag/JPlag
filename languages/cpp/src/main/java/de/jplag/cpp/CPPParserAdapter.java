package de.jplag.cpp;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.ParserRuleContext;

import de.jplag.antlr.AbstractAntlrListener;
import de.jplag.antlr.AbstractAntlrParserAdapter;
import de.jplag.cpp.grammar.CPP14Lexer;
import de.jplag.cpp.grammar.CPP14Parser;

/**
 * The adapter for the ANTLR-based parser of this language module.
 */
public class CPPParserAdapter extends AbstractAntlrParserAdapter<CPP14Parser> {
    private static final CPPListener listener = new CPPListener();

    /**
     * Creates the parser adapter.
     */
    public CPPParserAdapter() {
        super(true);
    }

    @Override
    protected Lexer createLexer(CharStream input) {
        return new CPP14Lexer(input);
    }

    @Override
    protected CPP14Parser createParser(CommonTokenStream tokenStream) {
        return new CPP14Parser(tokenStream);
    }

    @Override
    protected ParserRuleContext getEntryContext(CPP14Parser parser) {
        return parser.translationUnit();
    }

    @Override
    protected AbstractAntlrListener getListener() {
        return listener;
    }
}
