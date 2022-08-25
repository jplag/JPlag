package de.jplag.cpp;

import java.io.File;

import de.jplag.AbstractParser;
import de.jplag.TokenList;

public class Scanner extends AbstractParser {
    private String currentFile;

    private TokenList tokens;

    /**
     * Creates the parser.
     */
    public Scanner() {
        super();
    }

    public TokenList scan(File directory, String[] files) {
        tokens = new TokenList();
        errors = 0;
        for (String currentFile : files) {
            this.currentFile = currentFile;
            logger.trace("Scanning file {}", currentFile);
            if (!CPPScanner.scanFile(directory, currentFile, this)) {
                errors++;
            }
            tokens.addToken(new CPPToken(CPPTokenConstants.FILE_END, currentFile));
        }
        return tokens;
    }

    public void add(int type, Token token) {
        int length = token.endColumn - token.beginColumn + 1;
        tokens.addToken(new CPPToken(type, currentFile, token.beginLine, token.beginColumn, length));
    }
}
