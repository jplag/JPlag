package de.jplag.java_cpg.token;

import de.jplag.Token;
import de.jplag.TokenType;

import java.io.File;

/**
 * This class represents a Token in the context of the CPG module of JPlag.
 */
public class CpgToken extends Token {
    public CpgToken(TokenType tokenType, File file, int startLine, int startColumn, int length) {
        super(tokenType, file, startLine, startColumn, length);
    }
}
