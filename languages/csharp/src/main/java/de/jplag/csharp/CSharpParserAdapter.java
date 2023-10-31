package de.jplag.csharp;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.ParserRuleContext;

import de.jplag.antlr.AbstractAntlrListener;
import de.jplag.antlr.AbstractAntlrParserAdapter;
import de.jplag.csharp.grammar.CSharpLexer;
import de.jplag.csharp.grammar.CSharpParser;

/**
 * Parser adapter for the ANTLR 4 CSharp Parser and Lexer. It receives file to parse and passes them to the ANTLR
 * pipeline. Then it walks the produced parse tree and creates JPlag token with the {@link CSharpListener}.
 */
public class CSharpParserAdapter extends AbstractAntlrParserAdapter<CSharpParser> {
    @Override
    protected Lexer createLexer(CharStream input) {
        return new CSharpLexer(input);
    }

    @Override
    protected CSharpParser createParser(CommonTokenStream tokenStream) {
        return new CSharpParser(tokenStream);
    }

    @Override
    protected ParserRuleContext getEntryContext(CSharpParser parser) {
        return parser.compilation_unit();
    }

    @Override
    protected AbstractAntlrListener getListener() {
        return new CSharpListener();
    }
}
