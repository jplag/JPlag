package de.jplag;

/**
 * Abstract parser class. Counts errors and manages an error consumer.
 * @author Emeric Kwemou
 */
public abstract class AbstractParser {
    protected ErrorConsumer errorConsumer;
    protected int errors = 0;

    /**
     * @return true if there currently are some errors.
     */
    public boolean hasErrors() {
        return errors != 0;
    }

    /**
     * @return number of total errors.
     */
    public int errorCount() {
        return errors;
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
    public void setErrorConsumer(ErrorConsumer errorConsumer) {
        this.errorConsumer = errorConsumer;
    }
}
