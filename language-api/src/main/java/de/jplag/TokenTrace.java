package de.jplag;

/**
 * Tracing information to locate the corresponding code section of a token.
 * @param line is the line index in the source code where the token resides. Index is 1-based.
 * @param column is the column index, meaning where the token starts in the line. Index is 1-based.
 * @param length is the length of the token in the source code.
 */
public record TokenTrace(int line, int column, int length) {

    /**
     * Creates a empty trace with line, column, and length set to {@link Token#NO_VALUE NO_VALUE}.
     */
    public TokenTrace() {
        this(Token.NO_VALUE, Token.NO_VALUE, Token.NO_VALUE);
    }
}
