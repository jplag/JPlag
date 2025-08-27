package de.jplag;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jplag.semantics.CodeSemantics;

/**
 * This class represents a token in a source code. It can represent keywords, identifiers, syntactical structures etc.
 * What types of tokens there are depends on the specific language, meaning JPlag does not enforce a specific token set.
 * The language parsers decide what is a token and what is not.
 */
public class Token {
    /** Indicates that the requested field has no value. */
    public static final int NO_VALUE = -1;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final int startLine;
    private final int startColumn;
    /**
     * @deprecated The length does not include line breaks and should not be used to calculate the end position of a token
     */
    @Deprecated(since = "6.2.0", forRemoval = true)
    private final int length;
    private final int endLine;
    private final int endColumn;
    private final File file;
    private final TokenType type;
    private CodeSemantics semantics; // value null if no semantics

    /**
     * Creates a token with column and length information.
     * @param type is the token type.
     * @param file is the name of the source code file.
     * @param line is the line index in the source code where the token resides. Index is 1-based.
     * @param column is the column index, meaning where the token starts in the line. Index is 1-based.
     * @param length is the length of the token in the source code.
     * @deprecated Replaced by constructor that takes explicit end position
     */
    @Deprecated(since = "6.2.0", forRemoval = true)
    public Token(TokenType type, File file, int line, int column, int length) {
        if (line == 0) {
            logger.warn("Creating a token with line index 0 while index is 1-based");
        }
        if (column == 0) {
            logger.warn("Creating a token with column index 0 while index is 1-based");
        }
        this.type = type;
        this.file = file;
        this.startLine = line;
        this.startColumn = column;
        this.endLine = line;
        this.endColumn = column + length;
        this.length = length;
    }

    /**
     * Creates a token with a start and end position.
     * @param type is the token type.
     * @param file is the name of the source code file.
     * @param startLine is the line index in the source code where the token starts. Index is 1-based.
     * @param startColumn is the column index, meaning where the token starts in the line. Index is 1-based.
     * @param endLine is the line index in the source code where the token ends. Index is 1-based.
     * @param endColumn is the column index, meaning where the token ends in the line. Index is 1-based.
     * @param length is the length of the token in the source code.
     */
    public Token(TokenType type, File file, int startLine, int startColumn, int endLine, int endColumn, int length) {
        if (startLine == 0 || endLine == 0) {
            logger.warn("Creating a token with line index 0 while index is 1-based");
        }
        if (startColumn == 0 || endColumn == 0) {
            logger.warn("Creating a token with column index 0 while index is 1-based");
        }
        if (startLine > endLine || startLine == endLine && startColumn > endColumn) {
            logger.warn("Creating a token that ends earlier than it start. Start: {}:{}; End: {}:{}", startLine, startColumn, endLine, endColumn);
        }
        this.type = type;
        this.file = file;
        this.startLine = startLine;
        this.startColumn = startColumn;
        this.endLine = endLine;
        this.endColumn = endColumn;
        this.length = length;
    }

    /**
     * Creates a token from column and length information.
     * @param type is the token type.
     * @param file is the name of the source code file.
     * @param trace is the tracing information of the token, meaning line, column, and length.
     */
    public Token(TokenType type, File file, TokenTrace trace) {
        this(type, file, trace.line(), trace.column(), trace.line(), trace.column() + trace.length(), trace.length());
    }

    /**
     * Creates a token with a start and end position and semantic information.
     * @param type is the token type.
     * @param file is the name of the source code file.
     * @param startLine is the line index in the source code where the token starts. Index is 1-based.
     * @param startColumn is the column index, meaning where the token starts in the line. Index is 1-based.
     * @param endLine is the line index in the source code where the token ends. Index is 1-based.
     * @param endColumn is the column index, meaning where the token ends in the line. Index is 1-based.
     * @param length is the length of the token in the source code.
     * @param semantics is a record containing semantic information about the token.
     */
    public Token(TokenType type, File file, int startLine, int startColumn, int endLine, int endColumn, int length, CodeSemantics semantics) {
        this(type, file, startLine, startColumn, endLine, endColumn, length);
        this.semantics = semantics;
    }

    /**
     * Creates a token of type {@link SharedTokenType#FILE_END FILE_END} without information about line, column, and length.
     * @param file is the name of the source code file.
     * @return the file end token.
     */
    public static Token fileEnd(File file) {
        return new Token(SharedTokenType.FILE_END, file, NO_VALUE, NO_VALUE, NO_VALUE, NO_VALUE, NO_VALUE);
    }

    /**
     * Creates a token of type {@link SharedTokenType#FILE_END FILE_END} without information about line, column, and length,
     * but with semantic information.
     * @param file is the name of the source code file.
     * @return the file end token.
     */
    public static Token semanticFileEnd(File file) {
        CodeSemantics semantics = CodeSemantics.createControl();
        return new Token(SharedTokenType.FILE_END, file, NO_VALUE, NO_VALUE, NO_VALUE, NO_VALUE, NO_VALUE, semantics);
    }

    /**
     * Gives the line index denoting in which line the code sections represented by this token starts.
     * @return the line index.
     */
    public int getStartLine() {
        return startLine;
    }

    /**
     * Returns the character index which denotes where the code sections represented by this token starts in the line.
     * @return the character index in the line.
     */
    public int getStartColumn() {
        return startColumn;
    }

    /**
     * Gives the line index denoting in which line the code sections represented by this token ends.
     * @return the line index.
     */
    public int getEndLine() {
        return endLine;
    }

    /**
     * Returns the character index which denotes where the code sections represented by this token ends in the line.
     * @return the character index in the line.
     */
    public int getEndColumn() {
        return endColumn;
    }

    /**
     * Returns the character index which denotes where the code sections represented by this token starts in the line.
     * @return the character index in the line.
     * @deprecated see {@link Token#startColumn}
     */
    @Deprecated(since = "6.2.0", forRemoval = true)
    public int getColumn() {
        return getStartColumn();
    }

    /**
     * @return the name of the file where the source code that the token represents is located in.
     */
    public File getFile() {
        return file;
    }

    /**
     * Gives the length if the code sections represented by this token.
     * @return the length in characters.
     * @deprecated The length does not include line breaks and should not be used to calculate the end position of a token
     */
    @Deprecated(since = "6.2.0", forRemoval = true)
    public int getLength() {
        return length;
    }

    /**
     * Gives the line index denoting in which line the code sections represented by this token starts.
     * @return the line index.
     * @deprecated see {@link Token#startLine}
     */
    @Deprecated(since = "6.2.0", forRemoval = true)
    public int getLine() {
        return getStartLine();
    }

    /**
     * @return the type of the token.
     */
    public TokenType getType() {
        return type;
    }

    @Override
    public String toString() {
        return type.toString();
    }

    /**
     * @return the semantics of the token.
     */
    public CodeSemantics getSemantics() {
        return semantics;
    }
}
