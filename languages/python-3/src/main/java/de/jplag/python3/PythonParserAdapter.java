package de.jplag.python3;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.ParserRuleContext;

import de.jplag.antlr.AbstractAntlrListener;
import de.jplag.antlr.AbstractAntlrParserAdapter;
import de.jplag.python3.grammar.Python3Lexer;
import de.jplag.python3.grammar.Python3Parser;

/**
 * ANTLR-based parser adapter for Python 3.
 */
public class PythonParserAdapter extends AbstractAntlrParserAdapter<Python3Parser> {
    @Override
    protected Lexer createLexer(CharStream input) {
        return new Python3Lexer(input);
    }

    @Override
    protected Python3Parser createParser(CommonTokenStream tokenStream) {
        return new Python3Parser(tokenStream);
    }

    @Override
    protected ParserRuleContext getEntryContext(Python3Parser parser) {
        return parser.file_input();
    }

    @Override
    protected AbstractAntlrListener getListener() {
        return new PythonListener();
    }
}
