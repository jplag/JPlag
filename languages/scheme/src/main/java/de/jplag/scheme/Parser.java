package de.jplag.scheme;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import de.jplag.AbstractParser;
import de.jplag.ParsingException;
import de.jplag.Token;
import de.jplag.TokenType;

public class Parser extends AbstractParser {
    private File currentFile;

    private List<Token> tokens;

    /**
     * Creates the parser.
     */
    public Parser() {
        super();
    }

    public List<Token> parse(Set<File> files) throws ParsingException {
        tokens = new ArrayList<>();
        for (File file : files) {
            currentFile = file;
            logger.trace("Parsing file {}", file.getName());
            SchemeParser.parseFile(file, null, this);
            tokens.add(Token.fileEnd(currentFile));
        }
        return tokens;
    }

    public void add(TokenType type, de.jplag.scheme.Token token) {
        int length = token.endColumn - token.beginColumn + 1;
        tokens.add(new Token(type, currentFile, token.beginLine, token.endLine, length));
    }

}
