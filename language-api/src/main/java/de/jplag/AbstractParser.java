package de.jplag;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract parser class. Counts errors and manages an error consumer.
 * @author Emeric Kwemou
 */
public abstract class AbstractParser {
    public final Logger logger;

    protected AbstractParser() {
        this.logger = LoggerFactory.getLogger(this.getClass());
    }
}
