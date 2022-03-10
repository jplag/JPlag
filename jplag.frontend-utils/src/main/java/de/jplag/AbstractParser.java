package de.jplag;

/**
 * @author Emeric Kwemou
 */
public abstract class AbstractParser {
    protected ErrorConsumer errorConsumer;
    protected int errors = 0;
    private int numberOfErrors = 0;

    public boolean hasErrors() {
        return errors != 0;
    }

    public int errorsCount() {
        return numberOfErrors;
    }

    protected void parseEnd() {
        numberOfErrors += errors;
    }

    public ErrorConsumer getErrorConsumer() {
        return errorConsumer;
    }

    public void setProgram(ErrorConsumer errorConsumer) {
        this.errorConsumer = errorConsumer;
    }
}
