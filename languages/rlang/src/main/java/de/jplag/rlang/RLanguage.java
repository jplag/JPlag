package de.jplag.rlang;

import org.kohsuke.MetaInfServices;

import de.jplag.antlr.AbstractAntlrLanguage;

/**
 * This represents the R language as a language supported by JPlag.
 */
@MetaInfServices(de.jplag.Language.class)
public class RLanguage extends AbstractAntlrLanguage {
    private static final String NAME = "R Parser";
    private static final String IDENTIFIER = "rlang";
    private static final int DEFAULT_MIN_TOKEN_MATCH = 8;
    private static final String[] FILE_EXTENSION = {".R", ".r"};

    public RLanguage() {
        super(new RParserAdapter());
    }

    @Override
    public String[] suffixes() {
        return FILE_EXTENSION;
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
