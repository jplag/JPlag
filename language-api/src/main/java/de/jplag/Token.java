package de.jplag;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class represents a token in a source code. It can represent keywords, identifiers, syntactical structures etc.
 * What types of tokens there are depends on the specific language, meaning JPlag does not enforce a specific token set.
 * The language parsers decide what is a token and what is not.
 */
public class Token {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    /** Indicates that the requested field has no value. */
    public static final int NO_VALUE = -1;

    private int line;
    private int column;
    private int length;
    private File file;
    private TokenType type;

    /**
     * Creates a token of type {@link SharedTokenType#FILE_END FILE_END} without information about line, column, and length.
     * @param file is the name of the source code file.
     */
    public static Token fileEnd(File file) {
        return new Token(SharedTokenType.FILE_END, file, NO_VALUE, NO_VALUE, NO_VALUE);
    }

    /**
     * Creates a token with column and length information.
     * @param type is the token type.
     * @param file is the name of the source code file.
     * @param line is the line index in the source code where the token resides. Index is 1-based.
     * @param column is the column index, meaning where the token starts in the line. Index is 1-based.
     * @param length is the length of the token in the source code.
     */
    public Token(TokenType type, File file, int line, int column, int length) {
        if (line == 0) {
            logger.warn("Creating a token with line index 0 while index is 1-based");
        }
        if (column == 0) {
            logger.warn("Creating a token with column index 0 while index is 1-based");
        }
        this.type = type;
        this.file = file;
        this.line = line;
        this.column = column;
        this.length = length;
    }

    /**
     * Returns the character index which denotes where the code sections represented by this token starts in the line.
     * @return the character index in the line.
     */
    public int getColumn() {
        return column;
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
     */
    public int getLength() {
        return length;
    }

    /**
     * Gives the line index denoting in which line the code sections represented by this token starts.
     * @return the line index.
     */
    public int getLine() {
        return line;
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
}
