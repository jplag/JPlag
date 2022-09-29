package de.jplag.swift;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import de.jplag.AbstractParser;
import de.jplag.ParsingException;
import de.jplag.Token;
import de.jplag.swift.grammar.Swift5Lexer;
import de.jplag.swift.grammar.Swift5Parser;

public class SwiftParserAdapter extends AbstractParser {

    public static final int NOT_SET = -1;
    private File currentFile;
    private List<Token> tokens;

    /**
     * Creates the SwiftParserAdapter
     */
    public SwiftParserAdapter() {
        super();
    }

    /**
     * Parsers a set of files into a single list of {@link Token}s.
     * @param files the set of files.
     * @return a list containing all tokens of all files.
     */
    public List<Token> parse(Set<File> files) throws ParsingException {
        tokens = new ArrayList<>();
        for (File file : files) {
            parse(file);
            tokens.add(Token.fileEnd(file));
        }
        return tokens;
    }

    private void parse(File file) throws ParsingException {
        try (FileInputStream inputStream = new FileInputStream(file)) {
            currentFile = file;

            Swift5Lexer lexer = new Swift5Lexer(CharStreams.fromStream(inputStream));
            CommonTokenStream tokenStream = new CommonTokenStream(lexer);
            Swift5Parser parser = new Swift5Parser(tokenStream);

            ParserRuleContext entryContext = parser.top_level();
            ParseTreeWalker treeWalker = new ParseTreeWalker();

            JPlagSwiftListener listener = new JPlagSwiftListener(this);
            for (int i = 0; i < entryContext.getChildCount(); i++) {
                ParseTree parseTree = entryContext.getChild(i);
                treeWalker.walk(listener, parseTree);
            }
        } catch (IOException exception) {
            throw new ParsingException(file, exception.getMessage(), exception);
        }
    }

    /**
     * Adds a new {@link Token} to the current token list.
     * @param tokenType the type of the new {@link Token}
     * @param line the line of the Token in the current file
     * @param column the start column of the Token in the line
     * @param length the length of the Token
     */
    /* package-private */ void addToken(SwiftTokenType tokenType, int line, int column, int length) {
        tokens.add(new Token(tokenType, currentFile, line, column, length));
    }
}
