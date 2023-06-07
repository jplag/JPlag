package de.jplag.antlr;

import de.jplag.AbstractParser;
import de.jplag.ParsingException;
import de.jplag.Token;
import de.jplag.util.FileUtils;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.util.List;
import java.util.Set;

public abstract class AbstractAntlrParser<T extends Parser> extends AbstractParser {
    public List<Token> parse(Set<File> files) throws ParsingException {
        TokenCollector collector = new TokenCollector();

        for (File file : files) {
            parseFile(file, collector);
        }

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

            collector.addToken(Token.fileEnd(file));
        } catch (IOException exception) {
            throw new ParsingException(file, exception.getMessage(), exception);
        }
    }

    protected abstract Lexer createLexer(CharStream input);

    protected abstract T createParser(CommonTokenStream tokenStream);

    protected abstract ParserRuleContext getEntryContext(T parser);

    protected abstract AbstractAntlrListener createListener(TokenCollector collector, File currentFile);
}
