package de.jplag.cpp;

import de.jplag.Token;
import de.jplag.TokenType;

import static de.jplag.Token.NO_VALUE;

public class CPPToken extends Token {

    public CPPToken(TokenType type, String file, int line, int column, int length) {
        super(type, file, line, column, length);
    }

    public CPPToken(TokenType type, String file) {
        super(type, file, NO_VALUE, NO_VALUE, NO_VALUE);
    }
}
