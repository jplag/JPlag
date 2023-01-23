package de.jplag.cli;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jplag.Experimental;
import de.jplag.Language;

/**
 * This class contains methods to load {@link Language Languages}.
 * @author Dominik Fuchss
 */
public final class LanguageLoader {
    private static final Logger logger = LoggerFactory.getLogger(LanguageLoader.class);

    private static Map<String, Language> cachedLanguageInstances = null;

    private LanguageLoader() {
        throw new IllegalAccessError();
    }

    /**
     * Get all languages that are currently in the classpath. The languages will be cached. Use {@link #clearCache()} to
     * obtain new instances.
     * @return the languages as unmodifiable map from identifier to language instance.
     */
    public static synchronized Map<String, Language> getAllAvailableLanguages() {
        if (cachedLanguageInstances != null)
            return cachedLanguageInstances;

        Map<String, Language> languages = new TreeMap<>();

        for (Language language : ServiceLoader.load(Language.class)) {
            if (language.getClass().isAnnotationPresent(Experimental.class) && !Boolean.getBoolean("jplag.experimental")) {
                logger.info("Ignoring experimental language {}", language.getIdentifier());
                continue;
            }
            String languageIdentifier = language.getIdentifier();
            if (languages.containsKey(languageIdentifier)) {
                logger.error("Multiple implementations for a language '{}' are present in the classpath! Skipping ..", languageIdentifier);
                languages.remove(languageIdentifier);
                continue;
            }
            logger.debug("Loading Language Module '{}'", language.getName());
            languages.put(languageIdentifier, language);
        }
        logger.info("Available languages: '{}'", languages.values().stream().map(Language::getName).toList());

        cachedLanguageInstances = Collections.unmodifiableMap(languages);
        return cachedLanguageInstances;
    }

    /**
     * Load a language that is currently in the classpath by its short name.
     * @param identifier the identifier of the language
     * @return the language or an empty optional if no language has been found.
     * @see Language#getIdentifier()
     */
    public static Optional<Language> getLanguage(String identifier) {
        var language = getAllAvailableLanguages().get(identifier);
        if (language == null)
            logger.warn("Attempt to load Language {} was not successful", identifier);
        return Optional.ofNullable(language);
    }

    /**
     * Get an unmodifiable set of all available languages with their identifiers.
     * @return identifiers of all available languages
     * @see Language#getIdentifier()
     */
    public static Set<String> getAllAvailableLanguageIdentifiers() {
        return new TreeSet<>(getAllAvailableLanguages().keySet());
    }

    /**
     * Resets the internal cache of all loaded languages
     */
    public static synchronized void clearCache() {
        cachedLanguageInstances = null;
    }
}
