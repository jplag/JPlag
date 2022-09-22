package de.jplag;

import java.io.File;
import java.util.List;
import java.util.Set;

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
     * Identifier of the language used for CLI options and dynamic loading. You should use some name within {@code [a-z_-]+}
     */
    String getIdentifier();

    /**
     * Minimum number of tokens required for a match.
     */
    int minimumTokenMatch();

    /**
     * Parses a set of files.
     * @param files are the files to parse.
     * @return the list of parsed JPlag tokens.
     * @throws ParsingException if an error during parsing the files occurred.
     */
    List<Token> parse(Set<File> files) throws ParsingException;

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
