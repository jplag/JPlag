package de.jplag;

/**
 * @author Emeric Kwemou
 * @date 22.01.2005
 */
public abstract class AbstractParser {
    protected ErrorConsumer errorConsumer;
    protected int errors = 0;
    private int errorsSum = 0;

    public boolean hasErrors() {
        return errors != 0;
    }

    public int errorsCount() {
        return errorsSum;
    }

    protected void parseEnd() {
        errorsSum += errors;
    }

    public ErrorConsumer getErrorConsumer() {
        return errorConsumer;
    }

    public void setProgram(ErrorConsumer errorConsumer) {
        this.errorConsumer = errorConsumer;
    }
}
