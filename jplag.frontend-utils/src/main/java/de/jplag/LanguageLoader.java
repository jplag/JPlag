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

    private static List<Language> loaded = null;

    private LanguageLoader() {
        throw new IllegalAccessError();
    }

    /**
     * Load all languages that are currently in the classpath.<br>
     * Please remember to invoke {@link Language#createInitializedLanguage(ErrorConsumer)} to initialize the language. This
     * method does only return prototypes.
     * @return the languages
     */
    public static synchronized List<Language> loadLanguages() {
        if (loaded != null)
            return loaded;

        Set<String> loadedShortNames = new HashSet<>();
        List<Language> languages = new ArrayList<>();

        for (Language notInitializedLanguage : ServiceLoader.load(Language.class)) {
            String shortName = notInitializedLanguage.getShortName();
            if (!loadedShortNames.add(shortName)) {
                logger.error("Multiple implementations for a language '{}' are present in the classpath! Skipping ..", shortName);
                languages.removeIf(l -> Objects.equals(shortName, l.getShortName()));
                continue;
            }
            logger.info("Loading Language Frontend '{}'", notInitializedLanguage.getName());
            languages.add(notInitializedLanguage);
        }

        languages.sort(Comparator.comparing(Language::getShortName));

        loaded = Collections.unmodifiableList(languages);
        return loaded;
    }

    /**
     * Load a language that is currently in the classpath by its short name.<br>
     * Please remember to invoke {@link Language#createInitializedLanguage(ErrorConsumer)} to initialize the language. This
     * method does only return prototypes.
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

    /**
     * Resets the internal cache of all loaded languages
     */
    public static synchronized void reload() {
        loaded = null;
    }
}
