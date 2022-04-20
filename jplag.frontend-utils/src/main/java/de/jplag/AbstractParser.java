package de.jplag;

/**
 * Abstract parser class. Counts errors and manages an error consumer.
 * @author Emeric Kwemou
 */
public abstract class AbstractParser {
    protected ErrorConsumer errorConsumer;
    protected int errors = 0;

    public AbstractParser(ErrorConsumer errorConsumer) {
        this.errorConsumer = errorConsumer;
    }

    /**
     * @return true if the last parse call (which could be still ongoing) lead to one or more errors.
     */
    public boolean hasErrors() {
        return errors != 0;
    }

    /**
     * @return the error consumer that collects and prints errors.
     */
    public ErrorConsumer getErrorConsumer() {
        return errorConsumer;
    }

    /**
     * Setter for the error consumer that collects and prints errors.
     * @param errorConsumer is the consumer to set.
     */

}
