package de.jplag.python3;

import static de.jplag.Token.NO_VALUE;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import de.jplag.AbstractParser;
import de.jplag.SharedTokenType;
import de.jplag.TokenType;
import de.jplag.python3.grammar.Python3Lexer;
import de.jplag.python3.grammar.Python3Parser;
import de.jplag.python3.grammar.Python3Parser.File_inputContext;

public class Parser extends AbstractParser {

    private List<de.jplag.Token> tokens;
    private String currentFile;

    /**
     * Creates the parser.
     */
    public Parser() {
        super();
    }

    public List<de.jplag.Token> parse(File directory, String[] files) {
        tokens = new ArrayList<>();
        errors = 0;
        for (String file : files) {
            logger.trace("Parsing file {}", file);
            if (!parseFile(directory, file)) {
                errors++;
            }
            tokens.add(new Python3Token(SharedTokenType.FILE_END, file, NO_VALUE, NO_VALUE, NO_VALUE));
        }
        return tokens;
    }

    private boolean parseFile(File directory, String file) {
        BufferedInputStream inputStream;

        CharStream input;
        try {
            inputStream = new BufferedInputStream(new FileInputStream(new File(directory, file)));
            currentFile = file;
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

    public void add(TokenType type, Token token) {
        tokens.add(new Python3Token(type, (currentFile == null ? "null" : currentFile), token.getLine(), token.getCharPositionInLine() + 1,
                token.getText().length()));
    }

    public void addEnd(TokenType type, Token token) {
        tokens.add(new Python3Token(type, (currentFile == null ? "null" : currentFile), token.getLine(),
                tokens.get(tokens.size() - 1).getColumn() + 1, 0));
    }
}
