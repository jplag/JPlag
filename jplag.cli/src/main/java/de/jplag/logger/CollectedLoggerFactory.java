package de.jplag.logger;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;

/**
 * The factory for {@link CollectedLogger}.
 * @author Dominik Fuchss
 */
public final class CollectedLoggerFactory implements ILoggerFactory {

    private final ConcurrentMap<String, Logger> loggerMap;

    public CollectedLoggerFactory() {
        loggerMap = new ConcurrentHashMap<>();
    }

    /**
     * Return an appropriate {@link CollectedLogger} instance by name.
     */
    public Logger getLogger(String name) {
        Logger simpleLogger = loggerMap.get(name);
        if (simpleLogger != null) {
            return simpleLogger;
        } else {
            Logger newInstance = new CollectedLogger(name);
            Logger oldInstance = loggerMap.putIfAbsent(name, newInstance);
            return oldInstance == null ? newInstance : oldInstance;
        }
    }
}
