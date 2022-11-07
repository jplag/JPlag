package de.jplag.emf.dynamic;

import org.kohsuke.MetaInfServices;

import de.jplag.emf.dynamic.parser.DynamicEcoreParser;

/**
 * Language for EMF metamodels from the Eclipse Modeling Framework (EMF). This language is based on a dynamically
 * created token set instead of a hand-picked one.
 * @author Timur Saglam
 */
@MetaInfServices(de.jplag.Language.class)
public class Language extends de.jplag.emf.Language {
    private static final String NAME = "EMF metamodels (dynamically created token set)";
    private static final String IDENTIFIER = "emf-dynamic";

    private static final int DEFAULT_MIN_TOKEN_MATCH = 10;

    public Language() {
        super(new DynamicEcoreParser());
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
