package de.jplag.semantics;

public enum NextOperation {
    READ(true, false),
    WRITE(false, true),
    READ_WRITE(true, true);

    final boolean isRead;
    final boolean isWrite;

    NextOperation(boolean isRead, boolean isWrite) {
        this.isRead = isRead;
        this.isWrite = isWrite;
    }
}
