package de.jplag.java;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.tools.ToolProvider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jplag.CriticalParsingException;
import de.jplag.ParsingException;
import de.jplag.Token;

public class Parser {
    private static final Logger logger = LoggerFactory.getLogger(Parser.class);
    private static final String JDK_ERROR_MESSAGE = "Cannot parse as 'javac' is not available. Ensure a full JDK is installed.";
    private List<Token> tokens;

    public List<Token> parse(Set<File> files) throws ParsingException {
        ensureJavacIsAvailable();
        tokens = new ArrayList<>();
        new JavacAdapter().parseFiles(files, this);
        logger.debug("--- token semantics ---");
        for (Token token : tokens) {
            logger.debug("{} | {} | {}", token.getStartLine(), token.getType().getDescription(), token.getSemantics());
        }
        return tokens;
    }

    private void ensureJavacIsAvailable() throws CriticalParsingException {
        if (ToolProvider.getSystemJavaCompiler() == null) {
            throw new CriticalParsingException(JDK_ERROR_MESSAGE);
        }
    }

    public void add(Token token) {
        tokens.add(token);
    }
}
