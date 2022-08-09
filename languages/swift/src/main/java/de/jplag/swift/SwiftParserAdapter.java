package de.jplag.swift;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import de.jplag.AbstractParser;
import de.jplag.Token;
import de.jplag.TokenConstants;
import de.jplag.TokenList;
import de.jplag.swift.grammar.Swift5Lexer;
import de.jplag.swift.grammar.Swift5Parser;

public class SwiftParserAdapter extends AbstractParser {

    public static final int NOT_SET = -1;
    private String currentFile;
    private TokenList tokens;

    /**
     * Creates the SwiftParserAdapter
     * @param consumer the ErrorConsumer that parser errors are passed on to.
     */
    public SwiftParserAdapter() {
        super();
    }

    /**
     * Parsers a list of files into a single list of {@link Token}s.
     * @param directory the directory of the files.
     * @param fileNames the file names of the files.
     * @return a list containing all tokens of all files.
     */
    public TokenList parse(File directory, String[] fileNames) {
        tokens = new TokenList();
        for (String file : fileNames) {
            if (!parseFile(directory, file)) {
                errors++;
            }
            tokens.addToken(new SwiftToken(TokenConstants.FILE_END, file, NOT_SET, NOT_SET, NOT_SET));
        }
        return tokens;
    }

    private boolean parseFile(File directory, String fileName) {
        File file = new File(directory, fileName);
        try (FileInputStream inputStream = new FileInputStream(file)) {
            currentFile = fileName;

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
            logger.error("Parsing Error in '%s': %s%s".formatted(fileName, File.separator, exception));
            return false;
        }
        return true;
    }

    /**
     * Adds a new {@link Token} to the current token list.
     * @param tokenType the type of the new {@link Token}
     * @param line the line of the Token in the current file
     * @param column the start column of the Token in the line
     * @param length the length of the Token
     */
    /* package-private */ void addToken(int tokenType, int line, int column, int length) {
        tokens.addToken(new SwiftToken(tokenType, currentFile, line, column, length));
    }
}
