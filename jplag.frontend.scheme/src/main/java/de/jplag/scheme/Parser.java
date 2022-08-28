package de.jplag.scheme;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import de.jplag.AbstractParser;
import de.jplag.TokenConstants;

public class Parser extends AbstractParser {
    private String currentFile;

    private List<de.jplag.Token> tokens;

    /**
     * Creates the parser.
     */
    public Parser() {
        super();
    }

    public List<de.jplag.Token> parse(File directory, String[] files) {
        tokens = new ArrayList<>();
        errors = 0;
        for (String file : files) {
            currentFile = file;
            logger.trace("Parsing file {}", file);
            if (!SchemeParser.parseFile(directory, file, null, this))
                errors++;
            tokens.add(new SchemeToken(TokenConstants.FILE_END, currentFile));
        }
        return tokens;
    }

    public void add(int type, Token token) {
        int length = token.endColumn - token.beginColumn + 1;
        tokens.add(new SchemeToken(type, currentFile, token.beginLine, token.endLine, length));
    }

}
