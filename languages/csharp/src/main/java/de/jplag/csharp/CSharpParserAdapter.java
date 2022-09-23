package de.jplag.csharp;

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
import de.jplag.TokenType;
import de.jplag.csharp.grammar.CSharpLexer;
import de.jplag.csharp.grammar.CSharpParser;

/**
 * Parser adapter for the ANTLR 4 CSharp Parser and Lexer. It receives file to parse and passes them to the ANTLR
 * pipeline. Then it walks the produced parse tree and creates JPlag token with the {@link CSharpListener}.
 * @author Timur Saglam
 */
public class CSharpParserAdapter extends AbstractParser {
    private List<Token> tokens;
    private File currentFile;

    /**
     * Creates the parser adapter.
     */
    public CSharpParserAdapter() {
        super();
    }

    /**
     * Parses all tokens from a set of files.
     * @param files is the set of files.
     * @return the list of parsed tokens.
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
            throw new ParsingException(file, exception.getMessage(), exception);
        }
    }

    /* package-private */ void addToken(TokenType type, int line, int column, int length) {
        tokens.add(new Token(type, currentFile, line, column, length));
    }
}
