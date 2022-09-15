package de.jplag.cpp;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import de.jplag.AbstractParser;
import de.jplag.Token;

public class Scanner extends AbstractParser {
    private String currentFile;

    private List<Token> tokens;

    /**
     * Creates the parser.
     */
    public Scanner() {
        super();
    }

    public List<Token> scan(File directory, String[] files) {
        tokens = new ArrayList<>();
        errors = 0;
        for (String currentFile : files) {
            this.currentFile = currentFile;
            logger.trace("Scanning file {}", currentFile);
            if (!CPPScanner.scanFile(directory, currentFile, this)) {
                errors++;
            }
            tokens.add(Token.fileEnd(currentFile));
        }
        return tokens;
    }

    public void add(CPPTokenType type, de.jplag.cpp.Token token) {
        int length = token.endColumn - token.beginColumn + 1;
        tokens.add(new Token(type, currentFile, token.beginLine, token.beginColumn, length));
    }
}
