package de.jplag;

import java.io.File;
import java.util.List;

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

    private final int line;
    private final int column;
    private final int length;
    private final File file;
    private final List<TokenAttribute> type;
    private CodeSemantics semantics; // value null if no semantics
    private Language language;

    /**
     * Creates a token with column and length information.
     * @param type is the token type.
     * @param file is the name of the source code file.
     * @param line is the line index in the source code where the token resides. Index is 1-based.
     * @param column is the column index, meaning where the token starts in the line. Index is 1-based.
     * @param length is the length of the token in the source code.
     * @param language The language that created the token
     */
    public Token(TokenAttribute type, File file, int line, int column, int length, Language language) {
        this(List.of(type), file, line, column, length, language);
    }

    /**
     * Creates a token with column and length information.
     * @param type is the token type.
     * @param file is the name of the source code file.
     * @param line is the line index in the source code where the token resides. Index is 1-based.
     * @param column is the column index, meaning where the token starts in the line. Index is 1-based.
     * @param length is the length of the token in the source code.
     * @param language The language that created the token
     */
    public Token(List<TokenAttribute> type, File file, int line, int column, int length, Language language) {
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
        this.language = language;
    }

    /**
     * Creates a token with column and length information.
     * @param type is the token type.
     * @param file is the name of the source code file.
     * @param trace is the tracing information of the token, meaning line, column, and length.
     * @param language The language that created the token
     */
    public Token(TokenAttribute type, File file, TokenTrace trace, Language language) {
        this(type, file, trace.line(), trace.column(), trace.length(), language);
    }

    /**
     * Creates a token with column, length and semantic information.
     * @param type is the token type.
     * @param file is the name of the source code file.
     * @param line is the line index in the source code where the token resides. Index is 1-based.
     * @param column is the column index, meaning where the token starts in the line. Index is 1-based.
     * @param length is the length of the token in the source code.
     * @param semantics is a record containing semantic information about the token.
     * @param language The language that created the token
     */
    public Token(TokenAttribute type, File file, int line, int column, int length, CodeSemantics semantics, Language language) {
        this(type, file, line, column, length, language);
        this.semantics = semantics;
    }

    public Token(List<TokenAttribute> type, File file, int line, int column, int length, CodeSemantics semantics, Language language) {
        this(type, file, line, column, length, language);
        this.semantics = semantics;
    }

    /**
     * Creates a token of type {@link SharedTokenAttribute#FILE_END FILE_END} without information about line, column, and
     * length.
     * @param file is the name of the source code file.
     */
    public static Token fileEnd(File file) {
        return new Token(SharedTokenAttribute.FILE_END, file, NO_VALUE, NO_VALUE, NO_VALUE, null); // TODO null?
    }

    /**
     * Creates a token of type {@link SharedTokenAttribute#FILE_END FILE_END} without information about line, column, and
     * length, but with semantic information.
     * @param file is the name of the source code file.
     */
    public static Token semanticFileEnd(File file) {
        CodeSemantics semantics = CodeSemantics.createControl();
        return new Token(SharedTokenAttribute.FILE_END, file, NO_VALUE, NO_VALUE, NO_VALUE, semantics, null); // TODO null?
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
        return new TokenType(this.type);
    }

    public TokenAttribute getTypeCompat() {
        return this.type.getFirst();
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

    public Language getLanguage() {
        return language;
    }
}
