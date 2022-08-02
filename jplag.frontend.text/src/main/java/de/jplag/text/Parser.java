package de.jplag.text;

import java.io.File;
import java.io.FileInputStream;
import java.util.Hashtable;

import antlr.Token;

import de.jplag.AbstractParser;
import de.jplag.TokenConstants;
import de.jplag.TokenList;

public class Parser extends AbstractParser {

    private final Hashtable<String, Integer> tokenTypes = new Hashtable<>();
    protected int serial = 1; // 0 is FILE_END token, SEPARATOR is not used as there are no methods.

    private TokenList tokens;

    private String currentFile;

    private boolean runOut = false;

    /**
     * Creates the parser.
     */
    public Parser() {
        super();
    }

    public TokenList parse(File directory, String[] files) {
        tokens = new TokenList();
        errors = 0;
        for (String file : files) {
            logger.trace("Parsing file {}", file);
            if (!parseFile(directory, file)) {
                errors++;
            }
            tokens.addToken(new TextToken(TokenConstants.FILE_END, file));
        }
        return tokens;
    }

    public void add(Token token) {
        if (token instanceof ParserToken parserToken) {
            String text = token.getText();
            int type = getTokenType(text);
            tokens.addToken(new TextToken(text, type, currentFile, parserToken));
        } else {
            throw new IllegalArgumentException("Illegal token implementation: " + token);
        }

    }

    private boolean parseFile(File directory, String file) {
        InputState inputState = null;
        try (FileInputStream inputStream = new FileInputStream(new File(directory, file))) {
            currentFile = file;
            // Create a scanner that reads from the input stream passed to us
            inputState = new InputState(inputStream);
            TextLexer lexer = new TextLexer(inputState);
            lexer.setFilename(file);
            lexer.setTokenObjectClass("de.jplag.text.ParserToken");

            // Create a parser that reads from the scanner
            TextParser parser = new TextParser(lexer);
            parser.setFilename(file);
            parser.setParser(this);

            // start parsing at the compilationUnit rule
            parser.file();
        } catch (Exception e) {
            logger.error("Parsing Error in " + file + " (line " + (inputState != null ? "" + inputState.getLine() : "") + "):" + e.getMessage(), e);
            return false;
        }
        return true;
    }

    private int getTokenType(String text) {
        text = text.toLowerCase();
        tokenTypes.computeIfAbsent(text, it -> {
            if (serial == Integer.MAX_VALUE) {
                throw new IllegalStateException("Out of serials!");
            }
            return ++serial;
        });
        return tokenTypes.get(text);

    }
}
