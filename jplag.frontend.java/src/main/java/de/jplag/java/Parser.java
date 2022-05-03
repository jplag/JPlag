package de.jplag.java;

import static java.util.stream.Collectors.toList;

import java.io.File;
import java.util.Arrays;

import de.jplag.AbstractParser;
import de.jplag.ErrorConsumer;
import de.jplag.TokenList;

public class Parser extends AbstractParser {
    private TokenList tokens;

    /**
     * Creates the parser.
     * @param errorConsumer is the consumer for any occurring errors.
     */
    public Parser(ErrorConsumer errorConsumer) {
        super(errorConsumer);
    }

    public TokenList parse(File directory, String[] files) {
        tokens = new TokenList();
        errors = 0;
        var pathedFiles = Arrays.stream(files).map(it -> new File(directory, it)).collect(toList());
        errors += new JavacAdapter().parseFiles(directory, pathedFiles, this);
        return tokens;
    }

    public void add(int type, String filename, long line, long column, long length) {
        tokens.addToken(new JavaToken(type, filename, (int) line, (int) column, (int) length));
    }

    public void increaseErrors() {
        errors++;
    }
}
