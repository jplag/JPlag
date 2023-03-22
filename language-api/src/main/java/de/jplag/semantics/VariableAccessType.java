package de.jplag.semantics;

public enum VariableAccessType {
    READ(true, false),
    WRITE(false, true),
    READ_WRITE(true, true);

    final boolean isRead;
    final boolean isWrite;

    VariableAccessType(boolean isRead, boolean isWrite) {
        this.isRead = isRead;
        this.isWrite = isWrite;
    }
}
