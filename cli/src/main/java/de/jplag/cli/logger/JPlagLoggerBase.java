package de.jplag.cli.logger;

import org.slf4j.Marker;
import org.slf4j.event.Level;
import org.slf4j.helpers.AbstractLogger;

/**
 * Handles the enabled log levels for SLF4J.
 */
public abstract class JPlagLoggerBase extends AbstractLogger {
    protected static final Level LOG_LEVEL_TRACE = Level.TRACE;
    protected static final Level LOG_LEVEL_DEBUG = Level.DEBUG;
    protected static final Level LOG_LEVEL_INFO = Level.INFO;
    protected static final Level LOG_LEVEL_WARN = Level.WARN;
    protected static final Level LOG_LEVEL_ERROR = Level.ERROR;

    private static final Level LOG_LEVEL_FOR_EXTERNAL_LIBRARIES = LOG_LEVEL_ERROR;

    public static Level currentLogLevel = LOG_LEVEL_INFO;

    /**
     * @param name The name of the logger
     */
    protected JPlagLoggerBase(String name) {
        this.name = name;
    }

    @Override
    public boolean isTraceEnabled() {
        return isLogLevelEnabled(LOG_LEVEL_TRACE);
    }

    @Override
    public boolean isTraceEnabled(Marker marker) {
        return isTraceEnabled();
    }

    @Override
    public boolean isDebugEnabled() {
        return isLogLevelEnabled(LOG_LEVEL_DEBUG);
    }

    @Override
    public boolean isDebugEnabled(Marker marker) {
        return isDebugEnabled();
    }

    @Override
    public boolean isInfoEnabled() {
        return isLogLevelEnabled(LOG_LEVEL_INFO);
    }

    @Override
    public boolean isInfoEnabled(Marker marker) {
        return isInfoEnabled();
    }

    @Override
    public boolean isWarnEnabled() {
        return isLogLevelEnabled(LOG_LEVEL_WARN);
    }

    @Override
    public boolean isWarnEnabled(Marker marker) {
        return isWarnEnabled();
    }

    @Override
    public boolean isErrorEnabled() {
        return isLogLevelEnabled(LOG_LEVEL_ERROR);
    }

    @Override
    public boolean isErrorEnabled(Marker marker) {
        return isErrorEnabled();
    }

    private boolean isLogLevelEnabled(Level logLevel) {
        return logLevel.toInt() >= (isJPlagLog() ? this.currentLogLevel.toInt() : LOG_LEVEL_FOR_EXTERNAL_LIBRARIES.toInt());
    }

    private boolean isJPlagLog() {
        return this.name.startsWith("de.jplag.");
    }

    @Override
    protected String getFullyQualifiedCallerName() {
        return null; // does not seem to be used by anything, but is required by SLF4J
    }
}
