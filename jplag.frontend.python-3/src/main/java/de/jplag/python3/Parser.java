package de.jplag.python3;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.jplag.AbstractParser;
import de.jplag.TokenList;
import de.jplag.python3.grammar.Python3Lexer;
import de.jplag.python3.grammar.Python3Parser;
import de.jplag.python3.grammar.Python3Parser.File_inputContext;

public class Parser extends AbstractParser implements Python3TokenConstants {
    private static final Logger logger = LogManager.getLogger(Parser.class);

    private TokenList struct = new TokenList();
    private String currentFile;

    public TokenList parse(File dir, String files[]) {
        struct = new TokenList();
        errors = 0;
        for (int i = 0; i < files.length; i++) {
            logger.info("Parsing file " + files[i]);
            if (!parseFile(dir, files[i])) {
                errors++;
            }
            System.gc();// Emeric
            struct.addToken(new Python3Token(FILE_END, files[i], -1, -1, -1));
        }
        this.parseEnd();
        return struct;
    }

    private boolean parseFile(File dir, String file) {
        BufferedInputStream fis;

        CharStream input;
        try {
            fis = new BufferedInputStream(new FileInputStream(new File(dir, file)));
            currentFile = file;
            input = CharStreams.fromStream(fis);

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
            logger.error("Parsing Error in '" + file + "':\n" + e.getMessage(), e);
            return false;
        }

        return true;
    }

    public void add(int type, Token tok) {
        struct.addToken(new Python3Token(type, (currentFile == null ? "null" : currentFile), tok.getLine(), tok.getCharPositionInLine() + 1,
                tok.getText().length()));
    }

    public void addEnd(int type, Token tok) {
        struct.addToken(new Python3Token(type, (currentFile == null ? "null" : currentFile), tok.getLine(),
                struct.getToken(struct.size() - 1).getColumn() + 1, 0));
    }
}
