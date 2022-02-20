package de.jplag.chars;

import java.io.Serial;

import de.jplag.Token;

public class CharToken extends Token {

    @Serial
    private static final long serialVersionUID = -754166753275082477L;
    private final int index;

    public CharToken(int type, String file, int index, Parser parser) {
        super(type, file, -1);
        this.index = index;
    }

    public CharToken(int type, String file, Parser parser) {
        this(type, file, -1, parser);
    }

    @Override
    public int getLine() {
        return index; // so that the result-index is sorted correctly
    }

    @Override
    public int getColumn() {
        return 0;
    }

    @Override
    public int getLength() {
        return 0;
    }

    @Override
    public int getIndex() {
        return index;
    }
}
