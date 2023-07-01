package de.jplag.antlr;

import java.io.File;
import java.util.List;
import java.util.Set;

import de.jplag.Language;
import de.jplag.ParsingException;
import de.jplag.Token;

/**
 * Base class for Antlr languages. Handle the parse function from {@link Language}
 */
public abstract class AbstractAntlrLanguage implements Language {
    private final AbstractAntlrParserAdapter<?> parser;

    /**
     * New instance
     * @param parser The parser for source files
     */
    protected AbstractAntlrLanguage(AbstractAntlrParserAdapter<?> parser) {
        this.parser = parser;
    }

    @Override
    public List<Token> parse(Set<File> files) throws ParsingException {
        return this.parser.parse(files);
    }
}
