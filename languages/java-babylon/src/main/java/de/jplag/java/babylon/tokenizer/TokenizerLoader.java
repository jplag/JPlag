package de.jplag.java.babylon.tokenizer;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class for loading installed {@link BabylonTokenizer.Provider} implementations. Based on
 * {@link de.jplag.java.babylon.transformer.TransformationStepLoader}.
 */
public final class TokenizerLoader {
    private static final Logger logger = LoggerFactory.getLogger(TokenizerLoader.class);

    private static volatile Map<String, BabylonTokenizer.Provider> cachedStepInstances = null;
    private static volatile ServiceLoader<BabylonTokenizer.Provider> tokenizerLoader = null;

    private TokenizerLoader() {
        throw new IllegalAccessError();
    }

    /**
     * Get all tokenizers that are currently in the classpath. The result will be cached. Use {@link #clearCache()} to
     * obtain new instances.
     * @return the tokenizers as an unmodifiable map from identifiers to {@link BabylonTokenizer.Provider} instances
     */
    public static Map<String, BabylonTokenizer.Provider> getAllAvailableTokenizers() {
        if (cachedStepInstances == null) {
            synchronized (TokenizerLoader.class) {
                if (cachedStepInstances == null) {
                    Map<String, BabylonTokenizer.Provider> tokenizers = new TreeMap<>();
                    Set<String> skipped = new HashSet<>();

                    if (tokenizerLoader == null)
                        tokenizerLoader = ServiceLoader.load(BabylonTokenizer.Provider.class);

                    for (BabylonTokenizer.Provider tokenizer : tokenizerLoader) {
                        String identifier = tokenizer.getIdentifier();
                        if (tokenizers.remove(identifier) != null && skipped.add(identifier)) {
                            logger.error("Multiple implementations for a tokenizer '{}' are present in the classpath! Skipping ..", identifier);
                            continue;
                        }
                        logger.trace("Loading tokenizer '{}'", identifier);
                        tokenizers.put(identifier, tokenizer);
                    }
                    logger.debug("Available tokenizers: '{}'", tokenizers.keySet().stream().toList());

                    cachedStepInstances = Collections.unmodifiableMap(tokenizers);
                }
            }
        }

        return cachedStepInstances;
    }

    /**
     * Load a tokenizer that is currently in the classpath by its identifier.
     * @param identifier the identifier of the tokenizer
     * @return the tokenizer or an empty optional if no corresponding tokenizer was found
     * @see BabylonTokenizer.Provider#getIdentifier()
     */
    public static Optional<BabylonTokenizer.Provider> getTokenizer(String identifier) {
        BabylonTokenizer.Provider tokenizer = getAllAvailableTokenizers().get(identifier);
        if (tokenizer == null) {
            logger.warn("Attempt to load tokenizer {} was not successful", identifier);
        }
        return Optional.ofNullable(tokenizer);
    }

    /**
     * Get an unmodifiable set of all available tokenizer identifiers.
     * @return identifiers of all available tokenizers
     * @see BabylonTokenizer.Provider#getIdentifier()
     */
    public static Set<String> getAllAvailableTokenizerIdentifiers() {
        return new TreeSet<>(getAllAvailableTokenizers().keySet());
    }

    /**
     * Clears the internal cache of {@link BabylonTokenizer.Provider} instances, allowing new tokenizers to be found.
     */
    public static void clearCache() {
        synchronized (TokenizerLoader.class) {
            cachedStepInstances = null;
            if (tokenizerLoader != null)
                tokenizerLoader.reload();
        }
    }
}
