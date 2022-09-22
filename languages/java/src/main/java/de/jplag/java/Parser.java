package de.jplag.java;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import de.jplag.AbstractParser;
import de.jplag.ParsingException;
import de.jplag.Token;
import de.jplag.TokenType;

public class Parser extends AbstractParser {
    private List<Token> tokens;

    /**
     * Creates the parser.
     */
    public Parser() {
        super();
    }

    public List<Token> parse(Set<File> files) throws ParsingException {
        tokens = new ArrayList<>();
        new JavacAdapter().parseFiles(files, this);
        return tokens;
    }

    public void add(TokenType type, File file, long line, long column, long length) {
        add(new Token(type, file, (int) line, (int) column, (int) length));
    }

    public void add(Token token) {
        tokens.add(token);
    }
}
