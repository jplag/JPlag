package de.jplag.bash;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import de.jplag.ParsingException;
import de.jplag.Token;
import de.jplag.bash.grammar.BashLexer;
import de.jplag.bash.grammar.BashParser;
import de.jplag.util.FileUtils;

/**
 * ANTLR-based parser adapter for Bash.
 */
public class BashParserAdapter {

    private File currentFile;
    private List<Token> tokens;

    /**
     * Parses a set of files into a single list of {@link Token}s.
     * @param files the set of files.
     * @return a list containing all tokens of all files.
     * @throws ParsingException if parsing fails.
     */
    public List<Token> parse(Set<File> files) throws ParsingException {
        tokens = new ArrayList<>();
        for (File file : files) {
            parseFile(file);
            tokens.add(Token.fileEnd(file));
        }
        return tokens;
    }

    private void parseFile(File file) throws ParsingException {
        try (BufferedReader reader = FileUtils.openFileReader(file, true)) {
            currentFile = file;

            BashLexer lexer = new BashLexer(CharStreams.fromReader(reader));
            CommonTokenStream tokenStream = new CommonTokenStream(lexer);

            BashParser parser = new BashParser(tokenStream);

            ParserRuleContext entryContext = parser.program();
            ParseTreeWalker treeWalker = new ParseTreeWalker();

            for (int i = 0; i < entryContext.getChildCount(); i++) {
                ParseTree parseTree = entryContext.getChild(i);
                treeWalker.walk(new JPlagBashListener(this), parseTree);
            }
        } catch (IOException exception) {
            throw new ParsingException(file, exception.getMessage(), exception);
        }
    }

    /**
     * Adds a new {@link Token} to the current token list.
     * @param type the type of the new {@link Token}
     * @param line the line of the Token in the current file
     * @param start the start column of the Token in the line
     * @param length the length of the Token
     */
    /* package-private */ void addToken(BashTokenType type, int line, int start, int length) {
        tokens.add(new Token(type, currentFile, line, start, length));
    }
}
