package de.jplag.cpp;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import de.jplag.AbstractParser;
import de.jplag.SharedTokenType;

public class Scanner extends AbstractParser {
    private String currentFile;

    private List<de.jplag.Token> tokens;

    /**
     * Creates the parser.
     */
    public Scanner() {
        super();
    }

    public List<de.jplag.Token> scan(File directory, String[] files) {
        tokens = new ArrayList<>();
        errors = 0;
        for (String currentFile : files) {
            this.currentFile = currentFile;
            logger.trace("Scanning file {}", currentFile);
            if (!CPPScanner.scanFile(directory, currentFile, this)) {
                errors++;
            }
            tokens.add(new CPPToken(SharedTokenType.FILE_END, currentFile));
        }
        return tokens;
    }

    public void add(CPPTokenType type, Token token) {
        int length = token.endColumn - token.beginColumn + 1;
        tokens.add(new CPPToken(type, currentFile, token.beginLine, token.beginColumn, length));
    }
}
