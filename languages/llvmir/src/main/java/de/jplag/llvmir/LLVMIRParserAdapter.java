package de.jplag.llvmir;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.ParserRuleContext;

import de.jplag.AbstractParser;
import de.jplag.Language;
import de.jplag.LanguageLoader;
import de.jplag.antlr.AbstractAntlrListener;
import de.jplag.antlr.AbstractAntlrParserAdapter;
import de.jplag.llvmir.grammar.LLVMIRLexer;
import de.jplag.llvmir.grammar.LLVMIRParser;

/**
 * The adapter between {@link AbstractParser} and the ANTLR based parser of this language module.
 */
public class LLVMIRParserAdapter extends AbstractAntlrParserAdapter<LLVMIRParser> {
    private static final LLVMIRListener listener = new LLVMIRListener();

    @Override
    protected Lexer createLexer(CharStream input) {
        return new LLVMIRLexer(input);
    }

    @Override
    protected LLVMIRParser createParser(CommonTokenStream tokenStream) {
        return new LLVMIRParser(tokenStream);
    }

    @Override
    protected ParserRuleContext getEntryContext(LLVMIRParser parser) {
        return parser.compilationUnit();
    }

    @Override
    protected AbstractAntlrListener getListener() {
        return listener;
    }

    @Override
    protected Language getLanguage() {
        return LanguageLoader.getLanguage(LLVMIRLanguage.class).get();
    }
}
