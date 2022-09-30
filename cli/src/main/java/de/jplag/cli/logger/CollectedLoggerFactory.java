package de.jplag.cli.logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;

/**
 * The factory for {@link CollectedLogger}.
 * @author Dominik Fuchss
 */
public final class CollectedLoggerFactory implements ILoggerFactory {

    private final ConcurrentMap<String, CollectedLogger> loggerMap;

    public CollectedLoggerFactory() {
        loggerMap = new ConcurrentHashMap<>();
    }

    /**
     * Return an appropriate {@link CollectedLogger} instance by name.
     */
    public Logger getLogger(String name) {
        CollectedLogger simpleLogger = loggerMap.get(name);
        if (simpleLogger != null) {
            return simpleLogger;
        } else {
            CollectedLogger newInstance = new CollectedLogger(name);
            Logger oldInstance = loggerMap.putIfAbsent(name, newInstance);
            return oldInstance == null ? newInstance : oldInstance;
        }
    }

    /**
     * Print all errors of existing instances of {@link CollectedLogger}.
     */
    public void finalizeInstances() {
        List<CollectedLogger> copy = new ArrayList<>(loggerMap.values());
        copy.forEach(CollectedLogger::printAllErrorsForLogger);
    }
}
