package de.jplag.kotlin;

import java.io.File;

import org.antlr.v4.runtime.*;

import de.jplag.antlr.AbstractAntlrListener;
import de.jplag.antlr.AbstractAntlrParser;
import de.jplag.antlr.TokenCollector;
import de.jplag.kotlin.grammar.KotlinLexer;

public class KotlinParser extends AbstractAntlrParser<de.jplag.kotlin.grammar.KotlinParser> {
    @Override
    protected Lexer createLexer(CharStream input) {
        return new KotlinLexer(input);
    }

    @Override
    protected de.jplag.kotlin.grammar.KotlinParser createParser(CommonTokenStream tokenStream) {
        return new de.jplag.kotlin.grammar.KotlinParser(tokenStream);
    }

    @Override
    protected ParserRuleContext getEntryContext(de.jplag.kotlin.grammar.KotlinParser parser) {
        return parser.kotlinFile();
    }

    @Override
    protected AbstractAntlrListener createListener(TokenCollector collector, File currentFile) {
        return new KotlinListener(collector, currentFile);
    }
}
