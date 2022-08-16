package de.jplag.java;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.jplag.AbstractParser;
import de.jplag.Token;

public class Parser extends AbstractParser {
    private List<Token> tokens;

    /**
     * Creates the parser.
     */
    public Parser() {
        super();
    }

    public List<Token> parse(File directory, String[] files) {
        tokens = new ArrayList<>();
        errors = 0;
        var pathedFiles = Arrays.stream(files).map(it -> new File(directory, it)).toList();
        errors += new JavacAdapter().parseFiles(directory, pathedFiles, this);
        return tokens;
    }

    public void add(int type, String filename, long line, long column, long length) {
        tokens.add(new JavaToken(type, filename, (int) line, (int) column, (int) length));
    }

    public void increaseErrors() {
        errors++;
    }
}
