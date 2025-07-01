package de.jplag.c;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import de.jplag.AbstractParser;
import de.jplag.ParsingException;
import de.jplag.Token;

public class Scanner extends AbstractParser {
    private File currentFile;

    private List<Token> tokens;

    public List<Token> scan(Set<File> files) throws ParsingException {
        tokens = new ArrayList<>();
        for (File file : files) {
            this.currentFile = file;
            logger.trace("Scanning file {}", currentFile);
            try {
                CPPScanner.scanFile(file, this);
            } catch (ParsingException e) {
                throw e;
            } catch (Exception e) {
                throw new ParsingException(file, "Unexpected error during parsing." + System.lineSeparator() + e.getMessage(), e);
            }
            tokens.add(Token.fileEnd(currentFile));
        }
        return tokens;
    }

    public void add(CTokenType type, de.jplag.c.Token token) {
        int length = token.endColumn - token.beginColumn + 1;
        tokens.add(new Token(type, currentFile, token.beginLine, token.beginColumn, length));
    }
}
