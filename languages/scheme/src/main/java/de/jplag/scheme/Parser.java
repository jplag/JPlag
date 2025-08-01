package de.jplag.scheme;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jplag.ParsingException;
import de.jplag.Token;
import de.jplag.TokenType;

public class Parser {
    static final Logger logger = LoggerFactory.getLogger(Parser.class);

    private File currentFile;
    private List<Token> tokens;

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
        tokens.add(new Token(type, currentFile, token.beginLine, token.beginColumn, token.endLine, token.endColumn, length));
    }

}
