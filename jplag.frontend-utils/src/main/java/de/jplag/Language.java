package de.jplag;

import java.io.File;

/**
 * Common interface for all languages. Each language-front end must provide a concrete language implementation.
 */
public interface Language {

    /**
     * Suffixes for the files containing code of the language.
     */
    String[] suffixes();

    /**
     * Descriptive name of the language.
     */
    String getName();

    /**
     * Short name of the language used for CLI options.
     */
    String getShortName();

    /**
     * Minimum number of tokens required for a match.
     */
    int minimumTokenMatch();

    /**
     * Parses a set files in a directory.
     * @param directory is the directory where the files are located.
     * @param files are the names of the files to parse.
     * @return the list of parsed JPlag tokens.
     */
    TokenList parse(File directory, String[] files);

    /**
     * Whether errors were found during the last {@link #parse}.
     */
    boolean hasErrors();

    /**
     * Determines whether the parser provide column information.
     */
    boolean supportsColumns();

    /**
     * Determines whether JPlag should use a fixed-width font in its reports.
     */
    boolean isPreformatted();

    /**
     * Determines whether tokens from the scanner are indexed.
     */
    boolean usesIndex();

    /**
     * Number of defined tokens in the scanner of the language.
     */
    int numberOfTokens();
}
