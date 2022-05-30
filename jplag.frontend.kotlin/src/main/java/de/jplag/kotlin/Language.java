package de.jplag.kotlin;

import java.io.File;

import de.jplag.ErrorConsumer;
import de.jplag.TokenList;

public class Language implements de.jplag.Language {

    public static final String NAME = "Kotlin Parser";
    public static final String SHORT_NAME = "Kotlin";
    public static final int DEFAULT_MIN_TOKEN_MATCH = 8;
    private final KotlinParserAdapter parserAdapter;

    public Language(ErrorConsumer consumer) {
        this.parserAdapter = new KotlinParserAdapter(consumer);
    }

    @Override
    public String[] suffixes() {
        return new String[] {".kt"};
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
        return false;
    }

    @Override
    public boolean supportsColumns() {
        return false;
    }

    @Override
    public boolean isPreformatted() {
        return false;
    }

    @Override
    public boolean usesIndex() {
        return false;
    }

    @Override
    public int numberOfTokens() {
        return KotlinTokenConstants.NUMBER_DIFF_TOKENS;
    }
}
