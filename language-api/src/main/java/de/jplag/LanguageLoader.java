package de.jplag;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class contains methods to load {@link Language Languages}.
 * @author Dominik Fuchss
 */
public final class LanguageLoader {
    private static boolean loadModules = false;
    private static String modulePath = "";
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
        if (cachedLanguageInstances != null) {
            return cachedLanguageInstances;
        }

        Map<String, Language> languages = new TreeMap<>();

        for (Language language : ServiceLoader.load(Language.class)) {
            String languageIdentifier = language.getIdentifier();
            if (languages.containsKey(languageIdentifier)) {
                logger.error("Multiple implementations for a language '{}' are present in the classpath! Skipping ..", languageIdentifier);
                languages.remove(languageIdentifier);
                continue;
            }
            logger.trace("Loading Language Module '{}'", language.getName());
            languages.put(languageIdentifier, language);
        }
        logger.debug("Available languages: '{}'", languages.values().stream().map(Language::getName).toList());

        if(loadModules) {
            logger.debug("Attempting to load modules from external jar files");
            Path path = getModulesPath();
            if(path != null) {
                try {
                    Files.list(path).filter(it -> it.getFileName().toString().endsWith(".jar")).forEach(moduleFile -> {
                        logger.debug("Loading module file: {}", moduleFile.getFileName());
                        try {
                            ClassLoader moduleClassLoader = new URLClassLoader(new URL[]{moduleFile.toUri().toURL()});
                            for (Language language : ServiceLoader.load(Language.class, moduleClassLoader)) {
                                if(!languages.containsKey(language.getIdentifier())) {
                                    logger.debug("Loading language {} from module {}", language.getName(), moduleFile.getFileName());
                                    languages.put(language.getIdentifier(), language);
                                }
                            }
                        } catch (MalformedURLException e) {
                            logger.error("Failed to load module: {}", moduleFile.getFileName());
                        }
                    });
                } catch (IOException _) {
                    logger.error("Failed to load modules");
                }
            }
        }

        cachedLanguageInstances = Collections.unmodifiableMap(languages);
        return cachedLanguageInstances;
    }

    private static Path getModulesPath() {
        Path path = null;

        if(modulePath.isBlank()) {
            try {
                Path jarLocation = Path.of(LanguageLoader.class.getProtectionDomain().getCodeSource().getLocation().toURI());
                path = jarLocation.toRealPath().getParent().resolve("modules");
            } catch (URISyntaxException | IOException e) {
                logger.error("Failed to find location of external modules");
            }
        } else {
            path = Path.of(modulePath);
        }

        if(!(Files.exists(path) && Files.isDirectory(path))) {
            logger.error("Failed to find location of external modules");
            return null;
        }

        return path;
    }

    /**
     * Load a language that is currently in the classpath by its short name.
     * @param identifier the identifier of the language
     * @return the language or an empty optional if no language has been found.
     * @see Language#getIdentifier()
     */
    public static Optional<Language> getLanguage(String identifier) {
        var language = getAllAvailableLanguages().get(identifier);
        if (language == null) {
            logger.warn("Attempt to load Language {} was not successful", identifier);
        }
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
     * Resets the internal cache of all loaded languages.
     */
    public static synchronized void clearCache() {
        cachedLanguageInstances = null;
    }

    /**
     * Sets a path to load language modules from. The path should point to a directory containing the relevant jar files
     * @param path The path
     */
    public static void loadModulesFromPath(String path) {
        loadModules = true;
        modulePath = path;
    }
}
