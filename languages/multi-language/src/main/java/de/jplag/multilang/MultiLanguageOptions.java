package de.jplag.multilang;

import java.util.Arrays;
import java.util.List;

import de.jplag.Language;
import de.jplag.LanguageLoader;
import de.jplag.options.LanguageOption;
import de.jplag.options.LanguageOptions;
import de.jplag.options.OptionType;

/**
 * Options for multi-language support in JPlag. This class allows users to configure multiple supported languages either
 * explicitly through a comma-separated list or implicitly by selecting all available multi-language-supported ones.
 */
public class MultiLanguageOptions extends LanguageOptions {
    private static final String ERROR_LANGUAGE_NOT_FOUND = "The selected language %s could not be found";
    private static final String ERROR_NOT_ENOUGH_LANGUAGES = "To use multi language specify at least 1 language";
    private static final char LIST_SEPARATOR = ',';
    private static final String OPTION_DESCRIPTION_LANGUAGES = "The languages that should be used. This is a '" + LIST_SEPARATOR + "' separated list";

    private final LanguageOption<String> languageNames = createOption(OptionType.string(), "languages", OPTION_DESCRIPTION_LANGUAGES);
    private List<Language> languages = null;

    /**
     * Returns the list of languages selected for multi-language processing. If no languages were explicitly set, all
     * supported languages are returned.
     * @return list of {@link Language} instances
     * @throws IllegalArgumentException if no valid languages are found
     */
    public List<Language> getLanguages() {
        if (this.languages == null) {
            if (languageNames.getValue() == null) {
                this.languages = LanguageLoader.getAllAvailableLanguages().values().stream().filter(Language::supportsMultiLanguage).toList();
            } else {
                this.languages = Arrays.stream(languageNames.getValue().split(String.valueOf(LIST_SEPARATOR)))
                        .map(name -> LanguageLoader.getLanguage(name)
                                .orElseThrow(() -> new IllegalArgumentException(String.format(ERROR_LANGUAGE_NOT_FOUND, name))))
                        .filter(language -> !language.getClass().equals(MultiLanguage.class)).toList();
            }

            if (this.languages.isEmpty()) {
                throw new IllegalArgumentException(ERROR_NOT_ENOUGH_LANGUAGES);
            }
        }

        return this.languages;
    }

    /**
     * Returns the raw language names option, as a string.
     * @return {@link LanguageOption} representing the input string
     */
    public LanguageOption<String> getLanguageNames() {
        return this.languageNames;
    }
}
