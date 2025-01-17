package de.jplag.swift;

import org.kohsuke.MetaInfServices;

import de.jplag.antlr.AbstractAntlrLanguage;

/**
 * This represents the Swift language as a language supported by JPlag.
 */
@MetaInfServices(de.jplag.Language.class)
public class SwiftLanguage extends AbstractAntlrLanguage {

    private static final String IDENTIFIER = "swift";

    private static final String NAME = "Swift";
    private static final int DEFAULT_MIN_TOKEN_MATCH = 8;
    private static final String[] FILE_EXTENSIONS = {".swift"};

    public SwiftLanguage() {
        super(new SwiftParserAdapter());
    }

    @Override
    public String[] suffixes() {
        return FILE_EXTENSIONS;
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
