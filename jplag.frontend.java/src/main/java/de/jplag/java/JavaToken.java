package de.jplag.java;

import de.jplag.Token;
import de.jplag.TokenType;

public class JavaToken extends Token {

    public JavaToken(TokenType type, String file, int line, int column, int length) {
        super(type, file, line, column, length);
    }

    @Override
    protected String type2string() {
        return "";
    }
}