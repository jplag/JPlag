package de.jplag.rust;

import java.io.File;

import de.jplag.ErrorConsumer;
import de.jplag.TokenList;

public class Language implements de.jplag.Language {

    public static final String[] FILE_EXTENSIONS = {".rs"};
    public static final String NAME = "Rust frontend";
    public static final String SHORT_NAME = "Rust";
    public static final int MINIMUM_TOKEN_MATCH = 8;

    private final RustParserAdapter parserAdapter;

    public Language(ErrorConsumer consumer) {
        this.parserAdapter = new RustParserAdapter(consumer);
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

    @Override
    public boolean supportsColumns() {
        return true;
    }

    @Override
    public boolean isPreformatted() {
        return true;
    }

    @Override
    public boolean usesIndex() {
        return false;
    }

    @Override
    public int numberOfTokens() {
        return RustTokenConstants.NUMBER_DIFF_TOKENS;
    }
}
