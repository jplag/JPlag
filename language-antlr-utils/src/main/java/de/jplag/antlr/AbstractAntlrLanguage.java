package de.jplag.antlr;

import java.io.File;
import java.util.List;
import java.util.Set;

import de.jplag.Language;
import de.jplag.ParsingException;
import de.jplag.Token;

/**
 * Base class for Antlr languages. Handle the parse function from {@link Language}
 * <p>
 * You can either pass the parser to the super constructor, or implement the initializeParser method. That allows you to
 * access class members, like language specific options.
 */
public abstract class AbstractAntlrLanguage implements Language {
    private AbstractAntlrParserAdapter<?> parser;

    /**
     * New instance
     * @param parser The parser for source files
     */
    protected AbstractAntlrLanguage(AbstractAntlrParserAdapter<?> parser) {
        this.parser = parser;
    }

    /**
     * New instance, without pre initialized parser. If you use this constructor, you need to override the initializeParser
     * method.
     */
    protected AbstractAntlrLanguage() {
        this.parser = null;
    }

    @Override
    public List<Token> parse(Set<File> files) throws ParsingException {
        if (this.parser == null) {
            this.parser = this.initializeParser();
        }

        return this.parser.parse(files);
    }

    /**
     * Lazily creates the parser. Has to be implemented, if no parser is passed in the constructor.
     * @return The newly initialized parser
     */
    protected AbstractAntlrParserAdapter<?> initializeParser() {
        throw new UnsupportedOperationException(
                String.format("The initializeParser method needs to be implemented for %s", this.getClass().getName()));
    }
}
