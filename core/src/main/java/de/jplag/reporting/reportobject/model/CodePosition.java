package de.jplag.reporting.reportobject.model;

public record CodePosition(
        // 1-based
        int line,
        // 0-based
        int column,
        // 0-based
        int tokenListIndex) {
}
