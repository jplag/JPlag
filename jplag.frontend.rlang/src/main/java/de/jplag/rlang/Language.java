package de.jplag.rlang;

import java.io.File;
import java.util.List;

import org.kohsuke.MetaInfServices;

import de.jplag.Token;

/**
 * This represents the R language as a language supported by JPlag.
 */
@MetaInfServices(de.jplag.Language.class)
public class Language implements de.jplag.Language {

    private static final String NAME = "R Parser";
    public static final String IDENTIFIER = "rlang";
    private static final int DEFAULT_MIN_TOKEN_MATCH = 8;
    private static final String[] FILE_EXTENSION = {".R", ".r"};
    private final RParserAdapter parserAdapter;

    public Language() {
        this.parserAdapter = new RParserAdapter();
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

    @Override
    public List<Token> parse(File directory, String[] files) {
        return parserAdapter.parse(directory, files);
    }

    @Override
    public boolean hasErrors() {
        return parserAdapter.hasErrors();
    }
}
