package de.jplag.rust;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import de.jplag.AbstractParser;
import de.jplag.TokenConstants;
import de.jplag.Token;
import de.jplag.rust.grammar.RustLexer;
import de.jplag.rust.grammar.RustParser;

public class RustParserAdapter extends AbstractParser {

    private static final int NOT_SET = -1;
    private String currentFile;
    private List<Token> tokens;

    /**
     * Parsers a list of files into a single list of {@link Token}s.
     * @param directory the directory of the files.
     * @param fileNames the file names of the files.
     * @return a list containing all tokens of all files.
     */
    public List<Token> parse(File directory, String[] fileNames) {
        tokens = new ArrayList<>();
        errors = 0;
        for (String fileName : fileNames) {
            if (!parseFile(directory, fileName)) {
                errors++;
            }
            tokens.add(new RustToken(TokenConstants.FILE_END, fileName, NOT_SET, NOT_SET, NOT_SET));
        }
        return tokens;
    }

    private boolean parseFile(File directory, String fileName) {
        File file = new File(directory, fileName);
        try (FileInputStream inputStream = new FileInputStream(file)) {
            currentFile = fileName;

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
            logger.error("Parsing Error in '" + fileName + "':" + File.separator, exception);
            return false;
        }
        return true;
    }

    /**
     * Adds a new {@link Token} to the current token list.
     * @param type the type of the new {@link Token}
     * @param line the line of the Token in the current file
     * @param start the start column of the Token in the line
     * @param length the length of the Token
     */
    /* package-private */ void addToken(int type, int line, int start, int length) {
        tokens.add(new RustToken(type, currentFile, line, start, length));

    }
}
