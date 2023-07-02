package de.jplag.antlr.testLanguage;

import java.io.File;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.ParserRuleContext;

import de.jplag.antlr.*;

public class TestParserAdapter extends AbstractAntlrParserAdapter<TestParser> {
    @Override
    protected Lexer createLexer(CharStream input) {
        return new TestLexer(input);
    }

    @Override
    protected TestParser createParser(CommonTokenStream tokenStream) {
        return new TestParser(tokenStream);
    }

    @Override
    protected ParserRuleContext getEntryContext(TestParser parser) {
        return parser.expressionFile();
    }

    @Override
    protected AbstractAntlrListener createListener(TokenCollector collector, File currentFile) {
        return new TestListener(collector, currentFile);
    }
}
