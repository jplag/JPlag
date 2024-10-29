package de.jplag.golang;

import org.kohsuke.MetaInfServices;

import de.jplag.antlr.AbstractAntlrLanguage;

@MetaInfServices(de.jplag.Language.class)
public class GoLanguage extends AbstractAntlrLanguage {
    private static final String NAME = "Go";
    private static final String IDENTIFIER = "go";
    private static final int DEFAULT_MIN_TOKEN_MATCH = 8;
    private static final String[] FILE_EXTENSIONS = {".go"};

    public GoLanguage() {
        super(new GoParserAdapter());
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
