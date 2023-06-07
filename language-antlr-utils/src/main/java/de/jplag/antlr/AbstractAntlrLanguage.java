package de.jplag.antlr;

import de.jplag.Language;
import de.jplag.ParsingException;
import de.jplag.Token;

import java.io.File;
import java.util.List;
import java.util.Set;

public abstract class AbstractAntlrLanguage implements Language {
    private final AbstractAntlrParser<?> parser;

    public AbstractAntlrLanguage(AbstractAntlrParser<?> parser) {
        this.parser = parser;
    }

    @Override
    public List<Token> parse(Set<File> files) throws ParsingException {
        return this.parser.parse(files);
    }
}
