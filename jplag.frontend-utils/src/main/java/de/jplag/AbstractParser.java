package de.jplag;

/**
 * @author Emeric Kwemou
 */
public abstract class AbstractParser {
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

}
