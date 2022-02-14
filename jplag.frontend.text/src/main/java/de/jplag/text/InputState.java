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
    public int column = 0;
    public int tokenColumn = 0;

    public InputState(InputBuffer inputBuffer) {
        super(inputBuffer);
        column = 1;
        line = 1;
    }

    public InputState(InputStream inputStream) {
        super(inputStream);
        column = 1;
        line = 1;
    }

    public InputState(Reader inputReader) {
        super(inputReader);
        column = 1;
        line = 1;
    }

    @Override
    public int getLine() {
        return line;
    }
}
