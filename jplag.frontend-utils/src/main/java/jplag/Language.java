package jplag;

import java.io.*;

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
    public String name();

    /**
     * Short name of the language used for CLI options.
     */
    public String getShortName();

    /**
     * Minimum number of tokens required for a match.
     */
    public int min_token_match();

    /**
     * Parses a set files in a directory.
     */
    public Structure parse(File dir, String[] files);

    /**
     * Whether errors were found during the last {@link #parse}.
     */
    public boolean errors();

    /**
     * Number of errors found during the last {@link #parse}.
     */
    public int errorsCount();

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
    public int noOfTokens();

    /**
     * Convert a token type to a text representation.
     */
    public String type2string(int type);
}
