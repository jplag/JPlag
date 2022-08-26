package de.jplag.text;

import de.jplag.Token;
import de.jplag.TokenType;

public class TextToken extends Token {
    public TextToken(TokenType type, String file) {
        super(type, file, NO_VALUE, NO_VALUE, NO_VALUE);
    }

    public TextToken(String text, TokenType type, String file, int line, int column, int length) {
        super(type, file, line, column, length);
    }
}
