package de.jplag.text;

import antlr.Token;

/**
 * Token of the ANTLR grammar, needs to be converted into a JPlag token by the parser adapter.
 */
public class AntlrParserToken extends Token {
    /**
     * This variable holds the line number of the current token.
     */
    private int line = -1;

    /**
     * This variable holds the column of the current token in its line.
     */
    private int column = -1;

    /**
     * This variable holds the label of the current token.
     */
    private String text = null;

    /**
     * This variable holds the identifier of the current token.
     */
    private int id = -1;

    public AntlrParserToken() {
        super();
    }

    @Override
    public void setLine(int line) {
        this.line = line;
    }

    @Override
    public void setColumn(int column) {
        this.column = column;
    }

    public void setID(int id) {
        this.id = id;
    }

    @Override
    public void setText(String text) {
        this.text = (text != null ? text.intern() : null);
    }

    @Override
    public int getColumn() {
        return column;
    }

    @Override
    public int getLine() {
        return line;
    }

    @Override
    public String getText() {
        return text;
    }

    public int getID() {
        return id;
    }

    public int getLength() {
        return text.length();
    }

    @Override
    public String toString() {
        return "{\"" + getText() + "\", <" + getType() + ">, " + getLine() + " " + getColumn() + "}";
    }
}
