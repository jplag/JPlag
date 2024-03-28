package de.jplag;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract parser class. Counts errors and manages an error consumer.
 * @author Emeric Kwemou
 * @version $Id: $Id
 */
public abstract class AbstractParser {
    public final Logger logger;

    /**
     * <p>
     * Constructor for AbstractParser.
     * </p>
     */
    protected AbstractParser() {
        this.logger = LoggerFactory.getLogger(this.getClass());
    }
}
