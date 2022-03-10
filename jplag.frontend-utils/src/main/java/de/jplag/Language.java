package de.jplag;

import java.io.File;

/**
 * Common interface for all languages. Each language-front end must provide a concrete language implementation.
 */
public interface Language {

    /**
     * Suffixes for the files containing code of the language.
     */
    public String[] suffixes();

    /**
     * Descriptive name of the language.
     */
    public String getName();

    /**
     * Short name of the language used for CLI options.
     */
    public String getShortName();

    /**
     * Minimum number of tokens required for a match.
     */
    public int minimumTokenMatch();

    /**
     * Parses a set files in a directory.
     */
    public TokenList parse(File dir, String[] files);

    /**
     * Whether errors were found during the last {@link #parse}.
     */
    public boolean hasErrors();

    /**
     * Number of errors found during the last {@link #parse}.
     */
    public int errorCount();

    /**
     * Determines whether the parser provide column information.
     */
    public boolean supportsColumns();

    /**
     * Determines whether JPlag should use a fixed-width font in its reports.
     */
    public boolean isPreformatted();

    /**
     * Determines whether tokens from the scanner are indexed.
     */
    public boolean usesIndex();

    /**
     * Number of defined tokens in the scanner of the language.
     */
    public int numberOfTokens();
}
