package de.jplag.c;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jplag.ParsingException;
import de.jplag.Token;

/**
 * JavaCC-based scanner for the C language.
 */
public class Scanner {
    /** Logger used by the scanner and the ANTLR generated scanner. */
    static final Logger logger = LoggerFactory.getLogger(Scanner.class);

    private File currentFile;
    private List<Token> tokens;

    /**
     * Scans a set of C files.
     * @param files is the set of files.
     * @return the token sequence.
     * @throws ParsingException if parsing fails.
     */
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

    /**
     * Adds a token to the managed sequence.
     * @param type is the JPlag token type.
     * @param token is the corresponding JavaCC token.
     */
    public void add(CTokenType type, de.jplag.c.Token token) {
        int length = token.endColumn - token.beginColumn + 1;
        tokens.add(new Token(type, currentFile, token.beginLine, token.beginColumn, token.endLine, token.endColumn, length));
    }
}
