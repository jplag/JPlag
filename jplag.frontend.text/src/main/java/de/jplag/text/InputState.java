package de.jplag.text;

import java.io.InputStream;
import java.io.Reader;

import antlr.InputBuffer;
import antlr.LexerSharedInputState;

/**
 * This object contains the data associated with an input stream of characters. Multiple lexers share a single
 * LexerSharedInputState to lex the same input stream.
 */
public class InputState extends LexerSharedInputState {
    private int columnIndex = 1;
    private int tokenColumnIndex = 1;

    public InputState(InputBuffer inputBuffer) {
        super(inputBuffer);
    }

    public InputState(InputStream inputStream) {
        super(inputStream);
    }

    public InputState(Reader inputReader) {
        super(inputReader);
    }

    @Override
    public int getLine() {
        return line;
    }

    public int getColumnIndex() {
        return columnIndex;
    }

    public void setColumnIndex(int columnIndex) {
        this.columnIndex = columnIndex;
    }

    public int getTokenColumnIndex() {
        return tokenColumnIndex;
    }

    public void setTokenColumnIndex(int tokenColumnIndex) {
        this.tokenColumnIndex = tokenColumnIndex;
    }
}
