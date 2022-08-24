package de.jplag;

import java.io.File;

/**
 * Common interface for all languages. Each language-front end must provide a concrete language implementation.
 */
public interface Language {

    /**
     * Suffixes for the files containing code of the language. An empty array means all suffixes are valid.
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
     * Determines whether a fixed-width font should be used to display that language.
     */
    default boolean isPreformatted() {
        return true;
    }

    /**
     * Indicates whether the input files (code) should be used as representation in the report, or different files that form
     * a view on the input files.
     */
    default boolean useViewFiles() {
        return false;
    }

    /**
     * If the language uses representation files, this method returns the suffix used for the representation files.
     */
    default String viewFileSuffix() {
        return "";
    }

}
