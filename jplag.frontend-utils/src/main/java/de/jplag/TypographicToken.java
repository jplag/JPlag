package de.jplag;

/**
 * A token that tracks typographic information in the underlying source code, such as the column of the token in the
 * line and the length of the token in the line.
 * @author Timur Saglam
 */
public abstract class TypographicToken extends Token {
    private int column;
    private int length;

    /**
     * Creates a typographic token.
     * @param type is the token type.
     * @param file is the name of the source code file.
     * @param line is the line in the source code where the token resides.
     * @param column is the line index in the line, meaning where the token starts
     * @param length is the length of the token in the line.
     */
    public TypographicToken(int type, String file, int line, int column, int length) {
        super(type, file, line);
        this.column = column;
        this.length = length;
    }

    @Override
    public int getColumn() {
        return column;
    }

    @Override
    public int getLength() {
        return length;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    public void setLength(int length) {
        this.length = length;
    }

}
