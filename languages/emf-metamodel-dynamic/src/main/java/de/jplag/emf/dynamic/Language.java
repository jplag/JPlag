package de.jplag.emf.dynamic;

import de.jplag.emf.dynamic.parser.DynamicEcoreParser;
import de.jplag.emf.parser.EcoreParser;

/**
 * Language for EMF metamodels from the Eclipse Modeling Framework (EMF). This language is based on a dynamically
 * created token set instead of a hand-picked one.
 * @author Timur Saglam
 */
public class Language extends de.jplag.emf.Language { // currently not included in the CLI
    private static final String NAME = "EMF metamodels (dynamically created token set)";
    private static final String IDENTIFIER = "emf-dynamic";

    private static final int DEFAULT_MIN_TOKEN_MATCH = 10;

    /**
     * Creates an EMF language instance with a dynamic token parser.
     */
    public Language() {
        super(new DynamicEcoreParser());
    }

    /**
     * Creates an EMF language instance with a custom token parser.
     */
    public Language(EcoreParser parser) {
        super(parser);
    }

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
}
