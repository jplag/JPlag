package de.jplag.scalaantlr;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.ParserRuleContext;

import de.jplag.antlr.AbstractAntlrListener;
import de.jplag.antlr.AbstractAntlrParserAdapter;
import de.jplag.scalaantlr.grammar.Scala3Lexer;
import de.jplag.scalaantlr.grammar.Scala3Parser;

/**
 * Adapter for the scala parser
 */
public class ScalaParserAdapter extends AbstractAntlrParserAdapter<Scala3Parser> {
    @Override
    protected Lexer createLexer(CharStream input) {
        return new Scala3Lexer(input);
    }

    @Override
    protected Scala3Parser createParser(CommonTokenStream tokenStream) {
        return new Scala3Parser(tokenStream);
    }

    @Override
    protected ParserRuleContext getEntryContext(Scala3Parser parser) {
        return parser.compilationUnit();
    }

    @Override
    protected AbstractAntlrListener getListener() {
        return new ScalaListener();
    }
}
