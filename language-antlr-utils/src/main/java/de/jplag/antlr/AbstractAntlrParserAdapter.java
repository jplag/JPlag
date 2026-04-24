package de.jplag.antlr;

import java.io.File;
import java.io.Reader;
import java.util.List;
import java.util.Set;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CodePointCharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import de.jplag.ParsingException;
import de.jplag.Token;
import de.jplag.util.FileUtils;

/**
 * Base class for Antlr parser adapters.
 * @param <T> The type of the antlr parser
 */
public abstract class AbstractAntlrParserAdapter<T extends Parser> {

    private final boolean extractsSemantics;

    /**
     * New instance.
     * @param extractsSemantics If true, the listener will extract semantics along with every token
     */
    protected AbstractAntlrParserAdapter(boolean extractsSemantics) {
        this.extractsSemantics = extractsSemantics;
    }

    /**
     * New instance.
     */
    protected AbstractAntlrParserAdapter() {
        this(false);
    }

    /**
     * Parsers the set of files.
     * @param files The files
     * @return The extracted tokens
     * @throws ParsingException If anything goes wrong
     */
    public List<Token> parse(Set<File> files) throws ParsingException {
        TokenCollector collector = new TokenCollector(extractsSemantics);
        for (File file : files) {
            parseFile(file, collector);
        }
        return collector.getTokens();
    }

    private void parseFile(File file, TokenCollector collector) throws ParsingException {
        collector.enterFile(file);
        try (Reader reader = FileUtils.openFileReader(file, true)) {
            CodePointCharStream stream = CharStreams.fromReader(reader, file.getAbsolutePath());  // Specify source to retain file in ANTLR errors.
            Lexer lexer = this.createLexer(stream);
            CommonTokenStream tokenStream = new CommonTokenStream(lexer);
            T parser = this.createParser(tokenStream);
            parser.removeErrorListeners();
            parser.addErrorListener(new AntlrLoggerErrorListener());
            ParserRuleContext entryContext = this.getEntryContext(parser);
            ParseTreeWalker treeWalker = new ParseTreeWalker();
            InternalListener listener = new InternalListener(this.getListener(), collector);
            for (ParseTree child : entryContext.children) {
                treeWalker.walk(listener, child);
            }
        } catch (Exception exception) { // catching generic exception to capture any exceptions thrown by ANTLR.
            throw new ParsingException(file, exception.getMessage(), exception);
        }
        collector.addFileEndToken();
    }

    /**
     * Creates the antlr lexer.
     * @param input The input stream
     * @return The lexer
     */
    protected abstract Lexer createLexer(CharStream input);

    /**
     * Creates the antlr parser.
     * @param tokenStream The token input
     * @return The parser
     */
    protected abstract T createParser(CommonTokenStream tokenStream);

    /**
     * Extracts the core context from the parser. Should return the root context for the entire source file.
     * @param parser The parser
     * @return The root context
     */
    protected abstract ParserRuleContext getEntryContext(T parser);

    /**
     * @return The listener. Should be created once statically since it never changes.
     */
    protected abstract AbstractAntlrListener getListener();
}
