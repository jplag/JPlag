package de.jplag.csharp;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import de.jplag.AbstractParser;
import de.jplag.Token;
import de.jplag.csharp.grammar.CSharpLexer;
import de.jplag.csharp.grammar.CSharpParser;

/**
 * Parser adapter for the ANTLR 4 CSharp Parser and Lexer. It receives file to parse and passes them to the ANTLR
 * pipeline. Then it walks the produced parse tree and creates JPlag token with the {@link CSharpListener}.
 * @author Timur Saglam
 */
public class CSharpParserAdapter extends AbstractParser {
    private List<Token> tokens;
    private String currentFile;

    /**
     * Creates the parser adapter.
     */
    public CSharpParserAdapter() {
        super();
    }

    /**
     * Parses all tokens form a list of files.
     * @param directory is the base directory.
     * @param fileNames is the list of file names.
     * @return the list of parsed tokens.
     */
    public List<Token> parse(File directory, List<String> fileNames) {
        tokens = new ArrayList<>();
        errors = 0;
        for (String fileName : fileNames) {
            if (!parseFile(directory, fileName)) {
                errors++;
            }
            tokens.add(new CSharpToken(CSharpTokenConstants.FILE_END, fileName, -1, -1, -1));
        }
        return tokens;
    }

    private boolean parseFile(File directory, String fileName) {
        File file = new File(directory, fileName);
        try (FileInputStream inputStream = new FileInputStream(file)) {
            currentFile = fileName;

            // create a lexer, a parser and a buffer between them.
            CSharpLexer lexer = new CSharpLexer(CharStreams.fromStream(inputStream));
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            CSharpParser parser = new CSharpParser(tokens);

            // Create a tree walker and the entry context defined by the parser grammar
            ParserRuleContext entryContext = parser.compilation_unit();
            ParseTreeWalker treeWalker = new ParseTreeWalker();

            // Walk over the parse tree:
            for (int i = 0; i < entryContext.getChildCount(); i++) {
                ParseTree parseTree = entryContext.getChild(i);
                treeWalker.walk(new CSharpListener(this), parseTree);
            }
        } catch (IOException exception) {
            logger.error("Parsing Error in '" + fileName + "':" + File.separator + exception, exception);
            return false;
        }
        return true;
    }

    /* package-private */ void addToken(int type, int line, int column, int length) {
        tokens.add(new CSharpToken(type, currentFile, line, column, length));
    }
}
