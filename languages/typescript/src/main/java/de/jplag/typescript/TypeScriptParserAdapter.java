package de.jplag.typescript;

import de.jplag.antlr.AbstractAntlrListener;
import de.jplag.antlr.AbstractAntlrParserAdapter;
import de.jplag.antlr.TokenCollector;
import de.jplag.typescript.grammar.TypeScriptLexer;
import de.jplag.typescript.grammar.TypeScriptParser;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.ParserRuleContext;

import java.io.File;

public class TypeScriptParserAdapter extends AbstractAntlrParserAdapter<TypeScriptParser> {

    public TypeScriptParserAdapter() {
    }

    @Override
    protected Lexer createLexer(CharStream input) {
        TypeScriptLexer lexer = new TypeScriptLexer(input);
        return lexer;
    }

    @Override
    protected TypeScriptParser createParser(CommonTokenStream tokenStream) {
        return new TypeScriptParser(tokenStream);
    }

    @Override
    protected ParserRuleContext getEntryContext(TypeScriptParser parser) {
        return parser.sourceElements();
    }

    @Override
    protected AbstractAntlrListener createListener(TokenCollector collector, File currentFile) {
        return new TypeScriptListener(collector, currentFile);
    }
}
