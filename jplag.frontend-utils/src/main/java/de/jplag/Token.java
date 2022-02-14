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
    private int hash = -1;// hash-value. set and used by main algorithm (GSTiling)

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

    public String getFile() {
        return file;
    }

    public int getHash() {
        return hash;
    }

    // this is made to distinguish the character front end. Maybe other front ends can use it too?
    public int getIndex() {
        return -1;
    }

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }

    public int getLength() {
        return length;
    }

    public boolean isBasecode() {
        return basecode;
    }

    public boolean isMarked() {
        return marked;
    }

    public void setLine(int line) {
        this.line = line;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    public void setLength(int length) {
        this.length = length;
    }

    @Override
    public String toString() {
        return type2string();
    }

    protected abstract String type2string();

    /* Package Private */ boolean setBasecode(boolean basecode) {
        this.basecode = basecode;
        return basecode;
    }

    /* Package Private */ void setFile(String file) {
        this.file = file;
    }

    /* Package Private */ void setHash(int hash) {
        this.hash = hash;
    }

    /* Package Private */ boolean setMarked(boolean marked) {
        this.marked = marked;
        return marked;
    }
}
