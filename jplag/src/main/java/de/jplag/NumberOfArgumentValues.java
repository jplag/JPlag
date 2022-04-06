package de.jplag;

/**
 * Allowed number of values of a command-line argument in the CLI.
 */
public enum NumberOfArgumentValues {
    SINGLE_VALUE(""),
    ONE_OR_MORE_VALUES("+"),
    ZERO_OR_MORE_VALUES("*");

    private final String representation;

    NumberOfArgumentValues(String representation) {
        this.representation = representation;
    }

    @Override
    public String toString() {
        return representation;
    }
}