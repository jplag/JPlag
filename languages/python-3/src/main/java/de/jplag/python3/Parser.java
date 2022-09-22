package de.jplag.python3;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import de.jplag.AbstractParser;
import de.jplag.Token;
import de.jplag.TokenType;
import de.jplag.python3.grammar.Python3Lexer;
import de.jplag.python3.grammar.Python3Parser;
import de.jplag.python3.grammar.Python3Parser.File_inputContext;

public class Parser extends AbstractParser {

    private List<Token> tokens;
    private String currentFile;

    /**
     * Creates the parser.
     */
    public Parser() {
        super();
    }

    public List<Token> parse(Set<File> files) {
        tokens = new ArrayList<>();
        errors = 0;
        for (File file : files) {
            logger.trace("Parsing file {}", file.getName());
            if (!parseFile(file)) {
                errors++;
            }
            tokens.add(Token.fileEnd(file.getName()));
        }
        return tokens;
    }

    private boolean parseFile(File file) {
        BufferedInputStream inputStream;

        CharStream input;
        try {
            inputStream = new BufferedInputStream(new FileInputStream(file));
            currentFile = file.getName();
            input = CharStreams.fromStream(inputStream);

            // create a lexer that feeds off of input CharStream
            Python3Lexer lexer = new Python3Lexer(input);

            // create a buffer of tokens pulled from the lexer
            CommonTokenStream tokens = new CommonTokenStream(lexer);

            // create a parser that feeds off the tokens buffer
            Python3Parser parser = new Python3Parser(tokens);
            File_inputContext in = parser.file_input();

            ParseTreeWalker ptw = new ParseTreeWalker();
            for (int i = 0; i < in.getChildCount(); i++) {
                ParseTree pt = in.getChild(i);
                ptw.walk(new JplagPython3Listener(this), pt);
            }

        } catch (IOException e) {
            logger.error("Parsing Error in '" + file + "': " + e.getMessage(), e);
            return false;
        }

        return true;
    }

    public void add(TokenType type, org.antlr.v4.runtime.Token token) {
        tokens.add(new Token(type, (currentFile == null ? "null" : currentFile), token.getLine(), token.getCharPositionInLine() + 1,
                token.getText().length()));
    }

    public void addEnd(TokenType type, org.antlr.v4.runtime.Token token) {
        tokens.add(new Token(type, (currentFile == null ? "null" : currentFile), token.getLine(), tokens.get(tokens.size() - 1).getColumn() + 1, 0));
    }
}
