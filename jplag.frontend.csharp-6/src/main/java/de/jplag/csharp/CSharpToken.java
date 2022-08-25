package de.jplag.csharp;

import de.jplag.Token;
import de.jplag.TokenType;

/**
 * C# token class.
 * @author Timur Saglam
 */
public class CSharpToken extends Token {

    /**
     * Creates a C# token.
     * @param type is the type of the token.
     * @param file is the name of the source code file.
     * @param line is the line index in the source code where the token resides. Cannot be smaller than 1.
     * @param column is the column index, meaning where the token starts in the line.
     * @param length is the length of the token in the source code.
     */
    public CSharpToken(TokenType type, String file, int line, int column, int length) {
        super(type, file, line, column, length);
    }
}
