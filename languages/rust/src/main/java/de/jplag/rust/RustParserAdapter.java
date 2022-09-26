package de.jplag.rust;

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
import de.jplag.rust.grammar.RustLexer;
import de.jplag.rust.grammar.RustParser;

public class RustParserAdapter extends AbstractParser {

    private File currentFile;
    private List<Token> tokens;

    /**
     * Parsers a set of files into a single list of {@link Token}s.
     * @param files the set of files.
     * @return a list containing all tokens of all files.
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
        try (FileInputStream inputStream = new FileInputStream(file)) {
            currentFile = file;

            // create a lexer, a parser and a buffer between them.
            RustLexer lexer = new RustLexer(CharStreams.fromStream(inputStream));
            CommonTokenStream tokenStream = new CommonTokenStream(lexer);

            RustParser parser = new RustParser(tokenStream);

            // Create a tree walker and the entry context defined by the parser grammar
            ParserRuleContext entryContext = parser.crate();
            ParseTreeWalker treeWalker = new ParseTreeWalker();

            // Walk over the parse tree:
            for (int i = 0; i < entryContext.getChildCount(); i++) {
                ParseTree parseTree = entryContext.getChild(i);
                treeWalker.walk(new JPlagRustListener(this), parseTree);
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
    /* package-private */ void addToken(RustTokenType type, int line, int start, int length) {
        tokens.add(new Token(type, currentFile, line, start, length));

    }
}
