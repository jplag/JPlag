package de.jplag.python3;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.ParserRuleContext;

import de.jplag.antlr.AbstractAntlrListener;
import de.jplag.antlr.AbstractAntlrParserAdapter;
import de.jplag.python3.grammar.PythonLexer;
import de.jplag.python3.grammar.PythonParser;

/**
 * ANTLR-based parser adapter for Python 3.
 */
public class PythonParserAdapter extends AbstractAntlrParserAdapter<PythonParser> {
    @Override
    protected Lexer createLexer(CharStream input) {
        return new PythonLexer(input);
    }

    @Override
    protected PythonParser createParser(CommonTokenStream tokenStream) {
        return new PythonParser(tokenStream);
    }

    @Override
    protected ParserRuleContext getEntryContext(PythonParser parser) {
        return parser.file_input();
    }

    @Override
    protected AbstractAntlrListener getListener() {
        return new PythonListener();
    }
}
