package de.jplag.java;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import de.jplag.AbstractParser;
import de.jplag.ParsingException;
import de.jplag.Token;

public class Parser extends AbstractParser {
    private List<Token> tokens;

    /**
     * Creates the parser.
     */
    public Parser() {
        super();
    }

    public List<Token> parse(Set<File> files) throws ParsingException {
        tokens = new ArrayList<>();
        new JavacAdapter().parseFiles(files, this);
        logger.debug("--- token semantics ---");
        for (Token token : tokens) {
            logger.debug("{} | {} | {}", token.getLine(), token.getType().getDescription(), token.getSemantics());
        }
        return tokens;
    }

    public void add(Token token) {
        tokens.add(token);
    }
}
