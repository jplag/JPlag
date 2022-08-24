package de.jplag.rust;

import java.io.File;

import de.jplag.TokenList;

public class Language implements de.jplag.Language {

    protected static final String[] FILE_EXTENSIONS = {".rs"};
    public static final String NAME = "Rust frontend";
    public static final String IDENTIFIER = "rust";
    public static final int MINIMUM_TOKEN_MATCH = 8;

    private final RustParserAdapter parserAdapter;

    public Language() {
        this.parserAdapter = new RustParserAdapter();
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
        return MINIMUM_TOKEN_MATCH;
    }

    @Override
    public TokenList parse(File directory, String[] files) {
        return parserAdapter.parse(directory, files);
    }

    @Override
    public boolean hasErrors() {
        return parserAdapter.hasErrors();
    }

}
