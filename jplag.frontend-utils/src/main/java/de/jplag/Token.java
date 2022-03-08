package de.jplag;

abstract public class Token {
    public int type;
    public String file;

    protected int line, column, length;

    protected boolean marked; // TODO TS: Why protected/no accessors?
    protected boolean basecode = false;
    protected int hash = -1;// hash-value. set and used by main algorithm (GSTiling)

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

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }

    public int getLength() {
        return length;
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

    // this is made to distinguish the character front end. Maybe other front ends can use it too?
    public int getIndex() {
        return -1;
    }

    protected abstract String type2string();

    @Override
    public String toString() {
        return type2string();
    }
}
