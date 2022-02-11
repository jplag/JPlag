package de.jplag;

public abstract class Token {
    private boolean basecode = false;
    private String file;
    private int hash = -1;// hash-value. set and used by main algorithm (GSTiling)
    private boolean marked;

    protected int column;
    protected int length;
    protected int line;
    protected int type;

    public Token(int type, String file, int line) {
        this(type, file, line, -1, -1);
    }

    public Token(int type, String file, int line, int column, int length) {
        this.type = type;
        this.file = file;
        setLine(line > 0 ? line : 1);
        setColumn(column);
        setLength(length);
    }

    public int getColumn() {
        return column;
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

    public int getLength() {
        return length;
    }

    public int getLine() {
        return line;
    }

    public boolean isBasecode() {
        return basecode;
    }

    public boolean isMarked() {
        return marked;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public void setLine(int line) {
        this.line = line;
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
