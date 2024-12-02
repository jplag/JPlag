package de.jplag.multilang;

import java.util.Arrays;
import java.util.List;

import de.jplag.Language;
import de.jplag.LanguageLoader;
import de.jplag.options.LanguageOption;
import de.jplag.options.LanguageOptions;
import de.jplag.options.OptionType;

public class MultiLanguageOptions extends LanguageOptions {
    private static final String ERROR_LANGUAGE_NOT_FOUND = "The selected language %s could not be found";
    private static final String ERROR_NOT_ENOUGH_LANGUAGES = "To use multi language specify at least 1 language";

    public LanguageOption<String> languageNames = createOption(OptionType.string(), "languages",
            "The languages that should be used. This is a ',' separated list");
    private List<Language> languages = null;

    public List<Language> getLanguages() {
        if (this.languages == null) {
            if (languageNames.getValue() == null) {
                throw new IllegalArgumentException(ERROR_NOT_ENOUGH_LANGUAGES);
            }

            List<Language> languages = Arrays.stream(languageNames.getValue().split(","))
                    .map(name -> LanguageLoader.getLanguage(name)
                            .orElseThrow(() -> new IllegalArgumentException(String.format(ERROR_LANGUAGE_NOT_FOUND, name))))
                    .filter(language -> !language.getClass().equals(MultiLanguage.class)).toList();

            if (languages.isEmpty()) {
                throw new IllegalArgumentException(ERROR_NOT_ENOUGH_LANGUAGES);
            }

            this.languages = languages;
        }

        return this.languages;
    }
}
