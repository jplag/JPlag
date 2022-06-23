package de.jplag;

import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class contains methods to load {@link Language Languages}.
 * @author Dominik Fuchss
 */
public final class LanguageLoader {
    private static final Logger logger = LoggerFactory.getLogger(LanguageLoader.class);

    private LanguageLoader() {
        throw new IllegalAccessError();
    }

    /**
     * Load all languages that are currently in the classpath.<br>
     * Please remember to invoke {@link Language#initializeLanguage(ErrorConsumer)} to initialize the language. This method
     * does only return prototypes.
     * @return the languages
     */
    public static List<Language> loadLanguages() {
        List<Language> languages = new ArrayList<>();

        for (Language notInitializedLanguage : ServiceLoader.load(Language.class)) {
            logger.info("Loading Language Frontend '{}'", notInitializedLanguage.getName());
            languages.add(notInitializedLanguage);
        }

        languages.sort(Comparator.comparing(Language::getShortName));

        return languages;
    }

    /**
     * Load a language that is currently in the classpath by its short name.<br>
     * Please remember to invoke {@link Language#initializeLanguage(ErrorConsumer)} to initialize the language. This method
     * does only return prototypes.
     * @param shortName the short name of the language
     * @return the language or an empty optional if no language has been found.
     */
    public static Optional<Language> loadLanguage(String shortName) {
        var result = loadLanguages().stream().filter(it -> Objects.equals(it.getShortName(), shortName)).findFirst();
        if (result.isEmpty())
            logger.warn("Attempt to load Language {} was not successful", shortName);
        return result;
    }

    /**
     * Get a list of all available languages with their short name.
     * @return the list of all languages
     */
    public static List<String> getAllLanguageNames() {
        return loadLanguages().stream().map(Language::getShortName).toList();
    }
}
