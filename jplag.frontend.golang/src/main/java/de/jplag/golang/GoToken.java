package de.jplag.golang;

import de.jplag.Token;
import de.jplag.TokenType;

public class GoToken extends Token {

    public GoToken(TokenType type, String file, int line, int column, int length) {
        super(type, file, line, column, length);
    }
}
