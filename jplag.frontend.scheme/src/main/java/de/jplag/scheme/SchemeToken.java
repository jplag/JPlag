package de.jplag.scheme;

import de.jplag.Token;
import de.jplag.TokenType;

public class SchemeToken extends Token {

    public SchemeToken(TokenType type, String file, int line, int column, int length) {
        super(type, file, line, column, length);
    }

    public SchemeToken(TokenType type, String file) {
        super(type, file, NO_VALUE, NO_VALUE, NO_VALUE);
    }
}
