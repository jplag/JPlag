package de.jplag;

import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import de.jplag.commentextraction.CommentExtractorSettings;
import de.jplag.options.LanguageOptions;

/**
 * Common interface for all languages. Each language-front end must provide a concrete language implementation.
 */
public interface Language {

    /**
     * @return File extensions for the files containing code of the language. All capitalization variants of extensions are
     * matched. An empty array means all extensions are valid.
     * @deprecated see {@link Language#fileExtensions()}
     */
    @Deprecated(since = "6.2.0", forRemoval = true)
    default String[] suffixes() {
        return fileExtensions().toArray(String[]::new);
    }

    /**
     * @return Permitted file extensions for the program files of this language. All capitalization variants of extensions
     * are matched. An empty array means all extensions are valid.
     */
    List<String> fileExtensions();

    /**
     * @return Descriptive name of the language.
     */
    String getName();

    /**
     * @return Identifier of the language used for CLI options and dynamic loading. You should use some name within
     * {@code [a-z_-]+}
     */
    String getIdentifier();

    /**
     * @return Minimum number of tokens required for a match.
     */
    int minimumTokenMatch();

    /**
     * Parses a set of files. Override this method if you do not require normalization.
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
     * Parses a set of files. Override this method if you require normalization within the language module.
     * @param files are the files to parse.
     * @param normalize True, if the tokens should be normalized
     * @return the list of parsed JPlag tokens.
     * @throws ParsingException if an error during parsing the files occurred.
     */
    List<Token> parse(Set<File> files, boolean normalize) throws ParsingException;

    /**
     * @return Indicates whether the tokens returned by parse have semantic information added to them, i.e., whether the
     * token attribute semantics is null or not.
     */
    default boolean tokensHaveSemantics() {
        return false;
    }

    /**
     * @return Determines whether a fixed-width font should be used to display that language.
     */
    default boolean isPreformatted() {
        return true;
    }

    /**
     * @return Indicates whether the input code files should be used as representation in the report, or different files
     * that form a view on the input files.
     */
    default boolean useViewFiles() {
        return false;
    }

    /**
     * @return If the language uses representation files, this method returns the suffix used for the representation files.
     * @deprecated see {@link Language#viewFileExtension()}
     */
    @Deprecated(since = "6.2.0", forRemoval = true)
    default String viewFileSuffix() {
        return viewFileExtension();
    }

    /**
     * @return If the language uses representation files, this method returns the file extension used for the representation
     * files.
     */
    default String viewFileExtension() {
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
     * Reorders the provided submission according the requirements of the language.
     * @param submissions is the list of submissions.
     * @return the reordered list.
     */
    default List<File> customizeSubmissionOrder(List<File> submissions) {
        return submissions;
    }

    /**
     * @return True if this language supports token sequence normalization. This does not include other normalization
     * mechanisms that might be part of the language modules.
     */
    default boolean supportsNormalization() {
        return false;
    }

    /**
     * Override this method if you need normalization within the language module, but not in the core module.
     * @return True if the core normalization should be used.
     */
    default boolean requiresCoreNormalization() {
        return true;
    }

    /**
     * @return True, if the language module can be used by the multi-language module
     */
    default boolean supportsMultiLanguage() {
        return true;
    }

    /**
     * @return True if the language module should be prioritized in the multi-language case, if multiple language modules
     * support the same file extension.
     */
    default boolean hasPriority() {
        return false;
    }

    /**
     * Returns the settings for the comment extractor for this language.
     * @return Settings for the comment extractor.
     */
    default Optional<CommentExtractorSettings> getCommentExtractorSettings() {
        return Optional.empty();
    }
}
