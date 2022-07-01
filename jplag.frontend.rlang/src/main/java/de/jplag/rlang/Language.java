package de.jplag.rlang;

import java.io.File;

import de.jplag.ErrorConsumer;
import de.jplag.TokenList;

/**
 * This represents the R language as a language supported by JPlag.
 */
public class Language implements de.jplag.Language {

    public static final String NAME = "R Parser";
    public static final String SHORT_NAME = "R";
    public static final int DEFAULT_MIN_TOKEN_MATCH = 8;
    private final RParserAdapter parserAdapter;

    public Language(ErrorConsumer consumer) {
        this.parserAdapter = new RParserAdapter(consumer);
    }

    @Override
    public String[] suffixes() {
        return new String[] {".R", ".r"};
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
        return RTokenConstants.NUM_DIFF_TOKENS;
    }
}
