package de.jplag;

/**
 * This class represents a token in a source code. It can represents keywords, identifies, syntactical structures etc.
 * What types of tokens there are depends on the specific language, meaning JPlag does not enforce a specific token set.
 * The language parsers decide what is a token and what is not.
 */
public abstract class Token {
    private int line;
    private int column;
    private int length;
    private String file;

    private boolean marked;
    private boolean basecode = false;
    private int hash = -1; // hash-value. set and used by main algorithm (GSTiling)

    protected int type;

    /**
     * Creates a token without information about the column or the length of the token in the line.
     * @param type is the token type.
     * @param file is the name of the source code file.
     * @param line is the line index in the source code where the token resides. Cannot be smaller than 1.
     */
    public Token(int type, String file, int line) {
        this.type = type;
        this.file = file;
        setLine(line > 0 ? line : 1);
    }

    /**
     * Creates a token with column and length information.
     * @param type is the token type.
     * @param file is the name of the source code file.
     * @param line is the line index in the source code where the token resides. Cannot be smaller than 1.
     * @param column is the column index, meaning where the token starts in the line.
     * @param length is the length of the token in the source code.
     */
    public Token(int type, String file, int line, int column, int length) {
        this(type, file, line);
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
    public String getFile() {
        return file;
    }

    // this is made to distinguish the character front end. Maybe other front ends can use it too?
    public int getIndex() {
        return -1;
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
     * @return the type identifier of the token.
     */
    public int getType() {
        return type;
    }

    /**
     * Sets the character index which denotes where the code sections represented by this token starts in the line.
     * @param column is the index in characters to set.
     */
    public void setColumn(int column) {
        this.column = column;
    }

    /**
     * Sets the length if the code sections represented by this token.
     * @param length is the length in characters to set.
     */
    public void setLength(int length) {
        this.length = length;
    }

    /**
     * Sets the line index denoting in which line the code sections represented by this token starts.
     * @param line is the line index to set.
     */
    public void setLine(int line) {
        this.line = line;
    }

    @Override
    public String toString() {
        return type2string();
    }

    /**
     * @return a string representation depending on the type of the token.
     */
    protected abstract String type2string();

    /* package-private */ int getHash() {
        return hash;
    }

    /**
     * @return whether this token is part of a basecode.
     */
    /* package-private */ boolean isBasecode() {
        return basecode;
    }

    /**
     * @return whether this token is marked by the comparison algorithm.
     */
    /* package-private */ boolean isMarked() {
        return marked;
    }

    /* package-private */ boolean setBasecode(boolean basecode) {
        this.basecode = basecode;
        return basecode;
    }

    /* package-private */ void setFile(String file) {
        this.file = file;
    }

    /* package-private */ void setHash(int hash) {
        this.hash = hash;
    }

    /* package-private */ boolean setMarked(boolean marked) {
        this.marked = marked;
        return marked;
    }
}
