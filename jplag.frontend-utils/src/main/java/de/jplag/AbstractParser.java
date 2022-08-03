package de.jplag;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract parser class. Counts errors and manages an error consumer.
 * @author Emeric Kwemou
 */
public abstract class AbstractParser {
    protected int errors = 0;
    public final Logger logger;

    protected AbstractParser() {
        this.logger = LoggerFactory.getLogger(this.getClass());
    }

    /**
     * @return true if the last parse call (which could be still ongoing) lead to one or more errors.
     */
    public boolean hasErrors() {
        return errors != 0;
    }
}
