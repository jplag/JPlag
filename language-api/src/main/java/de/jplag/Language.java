package de.jplag;

import java.io.File;
import java.util.List;
import java.util.Set;

import de.jplag.options.LanguageOptions;

/**
 * Common interface for all languages. Each language-front end must provide a concrete language implementation.
 */
public interface Language {

    /**
     * File extensions for the files containing code of the language. An empty array means all suffixes are valid.
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
     * Parses a set of files. Override this method, if you don't require normalization.
     * @param files are the files to parse.
     * @return the list of parsed JPlag tokens.
     * @throws ParsingException if an error during parsing the files occurred.
     * @deprecated Replaced by {@link #parse(Set, boolean)}
     */
    @Deprecated(forRemoval = true)
    default List<Token> parse(Set<File> files) throws ParsingException {
        return parse(files, false);
    }

    /**
     * Parses a set of files. Override this method, if you require normalization within the language module.
     * @param files are the files to parse.
     * @param normalize True, if the tokens should be normalized
     * @return the list of parsed JPlag tokens.
     * @throws ParsingException if an error during parsing the files occurred.
     */
    List<Token> parse(Set<File> files, boolean normalize) throws ParsingException;

    /**
     * Indicates whether the tokens returned by parse have semantic information added to them, i.e. whether the token
     * attribute semantics is null or not.
     */
    default boolean tokensHaveSemantics() {
        return false;
    }

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

    /**
     * Returns a new option object for the language.
     * @return The options
     */
    default LanguageOptions getOptions() {
        return LanguageOptions.EMPTY_OPTIONS;
    }

    /**
     * Specifies if the submission order is relevant for this language.
     * @return defaults to false.
     */
    default boolean expectsSubmissionOrder() {
        return false;
    }

    /**
     * Re-orders the provided submission according the requirements of the language.
     * @param submissions is the list of submissions.
     * @return the re-ordered list.
     */
    default List<File> customizeSubmissionOrder(List<File> submissions) {
        return submissions;
    }

    /**
     * @return True, if this language supports token sequence normalization. This does not include other normalization
     * mechanisms that might be part of the language modules.
     */
    default boolean supportsNormalization() {
        return false;
    }

    /**
     * Override this method, if you need normalization within the language module, but not in the core module.
     * @return True, If the core normalization should be used.
     */
    default boolean requiresCoreNormalization() {
        return true;
    }
}
