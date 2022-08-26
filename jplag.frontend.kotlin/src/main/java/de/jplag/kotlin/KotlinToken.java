package de.jplag.kotlin;

import de.jplag.SharedTokenType;
import de.jplag.Token;

public class KotlinToken extends Token {

    public KotlinToken(SharedTokenType type, String file, int line, int column, int length) {
        super(type, file, line, column, length);
    }

    public KotlinToken(KotlinTokenType type, String file, int line, int column, int length) {
        super(type, file, line, column, length);
    }
}
