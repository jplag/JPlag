package de.jplag.typescript;

import java.io.File;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.ParserRuleContext;

import de.jplag.antlr.AbstractAntlrListener;
import de.jplag.antlr.AbstractAntlrParserAdapter;
import de.jplag.antlr.TokenCollector;
import de.jplag.typescript.grammar.TypeScriptLexer;
import de.jplag.typescript.grammar.TypeScriptParser;

public class TypeScriptParserAdapter extends AbstractAntlrParserAdapter<TypeScriptParser> {

    private final boolean useStrictDefault;

    /**
     * Creates a new Parser adapter for the Typescript Antlr Grammar
     * @param useStrictDefault True if the grammars should parse the files using the JavaScript strict syntax
     */
    public TypeScriptParserAdapter(boolean useStrictDefault) {
        this.useStrictDefault = useStrictDefault;
    }

    @Override
    protected Lexer createLexer(CharStream input) {
        TypeScriptLexer lexer = new TypeScriptLexer(input);
        lexer.setUseStrictDefault(useStrictDefault);
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
