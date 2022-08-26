package de.jplag.python3;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jplag.Token;
import de.jplag.TokenType;

public class Python3Token extends Token {
    public Python3Token(TokenType type, String file, int line, int column, int length) {
        super(type, file, line, column, length);
    }
}
