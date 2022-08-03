package de.jplag.golang;

import java.io.File;

import de.jplag.TokenList;

public class Language implements de.jplag.Language {

    private static final String NAME = "Go Parser";
    private static final String SHORT_NAME = "Go";
    private static final int DEFAULT_MIN_TOKEN_MATCH = 8;
    private static final String[] FILE_EXTENSIONS = {".go"};
    private final GoParserAdapter parserAdapter;

    public Language() {
        this.parserAdapter = new GoParserAdapter();
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
    public String getShortName() {
        return SHORT_NAME;
    }

    @Override
    public int minimumTokenMatch() {
        return DEFAULT_MIN_TOKEN_MATCH;
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
