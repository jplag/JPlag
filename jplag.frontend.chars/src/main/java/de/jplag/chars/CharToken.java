package de.jplag.chars;

import de.jplag.Token;

public class CharToken extends Token {

    private int index;
    private Character character;

    public CharToken(int type, String file, int index, Character character, Parser parser) {
        super(type, file, -1);
        this.index = index;
        this.character = character;
    }

    public CharToken(int type, String file, Parser parser) {
        this(type, file, -1, null, parser);
    }

    @Override
    public int getLine() {
        return index; // so that the result-index is sorted correctly
    }

    @Override
    public int getIndex() {
        return index;
    }

    @Override
    public void setLine(int line) {
        // Do nothing!
    }

    @Override
    public void setColumn(int column) {
        // Do nothing!
    }

    @Override
    public void setLength(int length) {
        // Do nothing!
    }

    @Override
    protected String type2string() {
        if (character == null) {
            return "FILE_END";
        }
        return character.toString();
    }
}
