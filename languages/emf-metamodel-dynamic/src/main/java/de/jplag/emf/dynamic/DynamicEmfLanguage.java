package de.jplag.emf.dynamic;

import java.io.File;
import java.util.List;
import java.util.Set;

import de.jplag.ParsingException;
import de.jplag.Token;
import de.jplag.emf.EmfLanguage;
import de.jplag.emf.dynamic.parser.DynamicEcoreParser;

/**
 * Language for EMF metamodels from the Eclipse Modeling Framework (EMF). This language is based on a dynamically
 * created token set instead of a hand-picked one.
 * @author Timur Saglam
 */
public class DynamicEmfLanguage extends EmfLanguage { // currently not included in the CLI
    private static final String NAME = "EMF metamodels (dynamically created token set)";
    private static final String IDENTIFIER = "emf-dynamic";

    private static final int DEFAULT_MIN_TOKEN_MATCH = 10;

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String getIdentifier() {
        return IDENTIFIER;
    }

    @Override
    public int minimumTokenMatch() {
        return DEFAULT_MIN_TOKEN_MATCH;
    }

    @Override
    public List<Token> parse(Set<File> files, boolean normalize) throws ParsingException {
        return new DynamicEcoreParser().parse(files, normalize);
    }
}
