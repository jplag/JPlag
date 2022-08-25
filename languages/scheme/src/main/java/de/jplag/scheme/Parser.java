package de.jplag.scheme;

import java.io.File;

import de.jplag.AbstractParser;
import de.jplag.TokenList;

public class Parser extends AbstractParser {
    private String currentFile;

    private TokenList tokens;

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
            currentFile = file;
            logger.trace("Parsing file {}", file);
            if (!SchemeParser.parseFile(directory, file, null, this))
                errors++;
            tokens.addToken(new SchemeToken(SchemeTokenConstants.FILE_END, currentFile));
        }
        return tokens;
    }

    public void add(int type, Token token) {
        int length = token.endColumn - token.beginColumn + 1;
        tokens.addToken(new SchemeToken(type, currentFile, token.beginLine, token.endLine, length));
    }

}
