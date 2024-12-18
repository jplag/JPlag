package de.jplag.antlr.testLanguage;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.ParserRuleContext;

import de.jplag.Language;
import de.jplag.antlr.AbstractAntlrListener;
import de.jplag.antlr.AbstractAntlrParserAdapter;
import de.jplag.antlr.TestLexer;
import de.jplag.antlr.TestParser;

public class TestParserAdapter extends AbstractAntlrParserAdapter<TestParser> {
    private static final TestListener listener = new TestListener();
    private Language language;

    public TestParserAdapter(Language language) {
        this.language = language;
    }

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
    protected AbstractAntlrListener getListener() {
        return listener;
    }

    @Override
    public Language getLanguage() {
        return language;
    }
}
