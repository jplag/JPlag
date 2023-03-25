package de.jplag.semantics;

/**
 * The ways a variable can be accessed.
 */
public enum VariableAccessType {
    /**
     * The variable is read from.
     */
    READ(true, false),
    /**
     * The variable is written to.
     */
    WRITE(false, true),
    /**
     * The variable is read from and written to.
     */
    READ_WRITE(true, true);

    final boolean isRead;
    final boolean isWrite;

    VariableAccessType(boolean isRead, boolean isWrite) {
        this.isRead = isRead;
        this.isWrite = isWrite;
    }
}
