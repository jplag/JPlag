package de.jplag.rlang;

import de.jplag.Token;
import de.jplag.TokenType;

/**
 * This class represents the occurrence of an R Token in the source code. Based on an R frontend for JPlag v2.15 by Olmo
 * Kramer, see their <a href="https://github.com/CodeGra-de/jplag/tree/master/jplag.frontend.R">JPlag fork</a>.
 */
public class RToken extends Token {

    public RToken(TokenType type, String file, int line, int column, int length) {
        super(type, file, line, column, length);
    }
}
