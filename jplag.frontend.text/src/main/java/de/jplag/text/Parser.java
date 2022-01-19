package de.jplag.text;

import java.io.File;
import java.io.FileInputStream;
import java.util.Hashtable;

import antlr.Token;
import de.jplag.AbstractParser;
import de.jplag.TokenConstants;
import de.jplag.TokenList;

public class Parser extends AbstractParser implements TokenConstants {

    protected Hashtable<String, Integer> table = new Hashtable<>();
    protected int serial = 1; // 0 is FILE_END token

    private TokenList struct;

    private String currentFile;

    public TokenList parse(File dir, String files[]) {
        struct = new TokenList();
        errors = 0;
        for (String file : files) {
            getErrorConsumer().print("", "Parsing file " + file);
            if (!parseFile(dir, file))
                errors++;
            struct.addToken(new TextToken(FILE_END, file, this));
        }

        TokenList tmp = struct;
        struct = null;
        this.parseEnd();
        return tmp;
    }

    private boolean parseFile(File dir, String file) {
        InputState inputState = null;
        try {
            FileInputStream inputStream = new FileInputStream(new File(dir, file));
            currentFile = file;
            // Create a scanner that reads from the input stream passed to us
            inputState = new InputState(inputStream);
            TextLexer lexer = new TextLexer(inputState);
            lexer.setFilename(file);
            lexer.setTokenObjectClass("de.jplag.text.ParserToken");

            // Create a parser that reads from the scanner
            TextParser parser = new TextParser(lexer);
            parser.setFilename(file);
            parser.parser = this;// Added by Emeric 26.01.05 BAD

            // start parsing at the compilationUnit rule
            parser.file();

            // close file
            inputStream.close();
        } catch (Exception e) {
            getErrorConsumer().addError(
                    "  Parsing Error in '" + file + "' (line " + (inputState != null ? "" + inputState.getLine() : "") + "):\n  " + e.getMessage());
            return false;
        }
        return true;
    }

    public void add(Token tok) {
        ParserToken ptok = (ParserToken) tok;
        struct.addToken(new TextToken(tok.getText(), currentFile, ptok.getLine(), ptok.getColumn(), ptok.getLength(), this));
    }

    private boolean runOut = false;

    public void outOfSerials() {
        if (runOut)
            return;
        runOut = true;
        errors++;
        errorConsumer.print("ERROR: Out of serials!", null);
        System.out.println("de.jplag.text.Parser: ERROR: Out of serials!");
    }
}
