package de.jplag.semantics;

/**
 * The ways a variable can be accessed.
 */
public enum VariableAccessType {
    /**
     * Read-only variable.
     */
    READ(true, false),

    /**
     * Write-only variable.
     */
    WRITE(false, true),

    /**
     * Read and write variable.
     */
    READ_WRITE(true, true);

    private final boolean isRead;
    private final boolean isWrite;

    VariableAccessType(boolean isRead, boolean isWrite) {
        this.isRead = isRead;
        this.isWrite = isWrite;
    }

    /**
     * @return true if the variable is read from.
     */
    public boolean isRead() {
        return isRead;
    }

    /**
     * @return if the variable is written to.
     */
    public boolean isWrite() {
        return isWrite;
    }
}
