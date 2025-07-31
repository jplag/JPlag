package de.jplag.reporting.reportobject.model;

/**
 * Represents a position in source code with line, column, and token list index.
 * @param line the 1-based line number in the source code
 * @param column the 0-based column number in the source code line
 * @param tokenListIndex the 0-based index of the token in the token list
 */
public record CodePosition(
        // 1-based
        int line,
        // 0-based
        int column,
        // 0-based
        int tokenListIndex) {
}
