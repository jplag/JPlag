package de.jplag.java;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import de.jplag.AbstractParser;
import de.jplag.CriticalParsingException;
import de.jplag.ParsingException;
import de.jplag.Token;

public class Parser extends AbstractParser {
    private static final String JDK_ERROR_MESSAGE = "Cannot parse as 'javac' is not available. Ensure a full JDK is installed.";
    private static final String JAVAC_CLASS_NAME = "com.sun.source.util.JavacTask";
    private List<Token> tokens;

    /**
     * Creates the parser.
     */
    public Parser() {
        super();
    }

    public List<Token> parse(Set<File> files) throws ParsingException {
        ensureJavacIsAvailable();
        tokens = new ArrayList<>();
        new JavacAdapter().parseFiles(files, this);
        logger.debug("--- token semantics ---");
        for (Token token : tokens) {
            logger.debug("{} | {} | {}", token.getLine(), token.getType().getDescription(), token.getSemantics());
        }
        return tokens;
    }

    private void ensureJavacIsAvailable() throws CriticalParsingException {
        try {
            Class.forName(JAVAC_CLASS_NAME);
        } catch (ClassNotFoundException exception) {
            throw new CriticalParsingException(JDK_ERROR_MESSAGE, exception);
        }
    }

    public void add(Token token) {
        tokens.add(token);
    }
}
