package de.jplag.text;

import java.io.InputStream;
import java.io.Reader;

import antlr.InputBuffer;
import antlr.LexerSharedInputState;

import de.jplag.annotations.FinalAttributesIgnore;
import de.jplag.annotations.ShadowParentIgnore;

/**
 * This object contains the data associated with an input stream of characters. Multiple lexers share a single
 * LexerSharedInputState to lex the same input stream.
 */
@ShadowParentIgnore(details = "Change the visibility breaks this frontend. If we have more tests, we should consider refactorings")
@FinalAttributesIgnore(details = "Change the final state may break this frontend. If we have more tests, we should consider refactorings")
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
