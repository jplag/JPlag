package de.jplag.csharp;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import de.jplag.AbstractParser;
import de.jplag.ErrorConsumer;
import de.jplag.TokenList;
import de.jplag.csharp.grammar.CSharpLexer;
import de.jplag.csharp.grammar.CSharpParser;

/**
 * Parser adapter for the ANTLR 4 CSharp Parser and Lexer. It receives file to parse and passes them to the ANTLR
 * pipeline. Then it walks the produced parse tree and creates JPlag token with the {@link CSharpListener}.
 * @author Timur Saglam
 */
public class CSharpParserAdapter extends AbstractParser {
    private TokenList tokens;
    private String currentFile;

    /**
     * Creates the parser adapter.
     * @param errorConsumer is the consumer for any occurring errors.
     */
    public CSharpParserAdapter(ErrorConsumer errorConsumer) {
        super(errorConsumer);
    }

    /**
     * Parses all tokens form a list of files.
     * @param directory is the base directory.
     * @param fileNames is the list of file names.
     * @return the list of parsed tokens.
     */
    public TokenList parse(File directory, List<String> fileNames) {
        tokens = new TokenList();
        errors = 0;
        for (String fileName : fileNames) {
            if (!parseFile(directory, fileName)) {
                errors++;
            }
            tokens.addToken(new CSharpToken(CSharpTokenConstants.FILE_END, fileName, -1, -1, -1));
        }
        return tokens;
    }

    private boolean parseFile(File directory, String fileName) {
        File file = new File(directory, fileName);
        try (FileInputStream inputStream = new FileInputStream(file);) {
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
            getErrorConsumer().addError("Parsing Error in '" + fileName + "':" + File.separator + exception.toString());
            return false;
        }
        return true;
    }

    /* package-private */ void addToken(int type, int line, int column, int length) {
        tokens.addToken(new CSharpToken(type, currentFile, line, column, length));
    }
}
