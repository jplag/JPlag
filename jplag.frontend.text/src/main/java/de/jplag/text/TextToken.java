package de.jplag.text;

import java.io.Serial;

import de.jplag.Token;

public class TextToken extends Token {
    @Serial
    private static final long serialVersionUID = 4301179216570538972L;

    private static int getSerial(String text, Parser parser) {
        text = text.toLowerCase();
        Integer serial = parser.table.get(text);
        if (serial == null) {
            serial = parser.serial;
            if (parser.serial == Integer.MAX_VALUE)
                parser.outOfSerials();
            else
                parser.serial++;
            parser.table.put(text, serial);
        }
        return serial;
    }

    // ///////////////////// END OF STATIC MEMBERS

    private int line, column, length;
    private String text;

    public TextToken(int type, String file, Parser parser) {
        super(type, file, -1, -1, -1);
    }

    public TextToken(String text, String file, int line, int column, int length, Parser parser) {
        super(-1, file, line, column, length);
        this.type = getSerial(text, parser);
        this.text = text.toLowerCase();
    }

    @Override
    public int getLine() {
        return line;
    }

    @Override
    public int getColumn() {
        return column;
    }

    @Override
    public int getLength() {
        return length;
    }

    @Override
    public void setLine(int line) {
        this.line = line;
    }

    @Override
    public void setColumn(int column) {
        this.column = column;
    }

    @Override
    public void setLength(int length) {
        this.length = length;
    }

    public String getText() {
        return this.text;
    }
}
