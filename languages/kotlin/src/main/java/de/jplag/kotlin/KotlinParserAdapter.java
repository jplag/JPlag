package de.jplag.kotlin;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.ParserRuleContext;

import de.jplag.Language;
import de.jplag.LanguageLoader;
import de.jplag.antlr.AbstractAntlrListener;
import de.jplag.antlr.AbstractAntlrParserAdapter;
import de.jplag.antlr.treewalker.TreeWalkerRuleBuilder;
import de.jplag.kotlin.grammar.KotlinLexer;
import de.jplag.kotlin.grammar.KotlinParser;

public class KotlinParserAdapter extends AbstractAntlrParserAdapter<KotlinParser> {
    private static final KotlinListener listener = new KotlinListener();

    @Override
    protected Lexer createLexer(CharStream input) {
        return new KotlinLexer(input);
    }

    @Override
    protected KotlinParser createParser(CommonTokenStream tokenStream) {
        return new KotlinParser(tokenStream);
    }

    @Override
    protected ParserRuleContext getEntryContext(KotlinParser parser) {
        return parser.kotlinFile();
    }

    @Override
    protected AbstractAntlrListener getListener() {
        return listener;
    }

    @Override
    protected Language getLanguage() {
        return LanguageLoader.getLanguage(KotlinLanguage.class).get();
    }

    @Override
    protected TreeWalkerRuleBuilder initializeRuleBuilder() {
        return new KotlinTreeWalkerRules();
    }
}
