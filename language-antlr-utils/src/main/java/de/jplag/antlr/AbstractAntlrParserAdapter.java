package de.jplag.antlr;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import de.jplag.AbstractParser;
import de.jplag.ParsingException;
import de.jplag.Token;
import de.jplag.util.FileUtils;

/**
 * Base class for Antlr parser adapters
 * @param <T> The type of the antlr parser
 */
public abstract class AbstractAntlrParserAdapter<T extends Parser> extends AbstractParser {

    private final boolean extractsSemantics;

    /**
     * New instance
     * @param extractsSemantics If true, the listener will extract semantics along with every token
     */
    public AbstractAntlrParserAdapter(boolean extractsSemantics) {
        super();
        this.extractsSemantics = extractsSemantics;
    }

    /**
     * New instance
     */
    public AbstractAntlrParserAdapter() {
        this(false);
    }

    /**
     * Parsers the set of files
     * @param files The files
     * @return The extracted tokens
     * @throws ParsingException If anything goes wrong
     */
    public List<Token> parse(Set<File> files) throws ParsingException {
        List<File> filesList = new ArrayList<>(files);
        if (files.isEmpty())
            return new ArrayList<>();
        File firstFile = filesList.remove(0);
        TokenCollector collector = new TokenCollector(extractsSemantics, firstFile);
        parseFile(firstFile, collector);
        for (File file : filesList) {
            collector.addFileEndToken(file);  // takes the NEXT file
            parseFile(file, collector);
        }
        collector.addFileEndToken(null);
        return collector.getTokens();
    }

    private void parseFile(File file, TokenCollector collector) throws ParsingException {
        try (Reader reader = FileUtils.openFileReader(file)) {
            Lexer lexer = this.createLexer(CharStreams.fromReader(reader));
            CommonTokenStream tokenStream = new CommonTokenStream(lexer);
            T parser = this.createParser(tokenStream);

            ParserRuleContext entryContext = this.getEntryContext(parser);
            ParseTreeWalker treeWalker = new ParseTreeWalker();

            AbstractAntlrListener listener = this.createListener(collector, file);
            for (ParseTree child : entryContext.children) {
                treeWalker.walk(listener, child);
            }
        } catch (IOException exception) {
            throw new ParsingException(file, exception.getMessage(), exception);
        }
    }

    /**
     * Creates the antlr lexer
     * @param input The input stream
     * @return The lexer
     */
    protected abstract Lexer createLexer(CharStream input);

    /**
     * Creates the antlr parser
     * @param tokenStream The token input
     * @return The parser
     */
    protected abstract T createParser(CommonTokenStream tokenStream);

    /**
     * Extracts the core context from the parser. Should return the root context for the entire source file
     * @param parser The parser
     * @return The root context
     */
    protected abstract ParserRuleContext getEntryContext(T parser);

    /**
     * Creates the listener
     * @param collector The token collector
     * @param currentFile The current file
     * @return The parser
     */
    protected abstract AbstractAntlrListener createListener(TokenCollector collector, File currentFile);
}
