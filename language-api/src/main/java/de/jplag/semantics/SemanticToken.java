package de.jplag.semantics;

import java.io.File;

import de.jplag.SharedTokenType;
import de.jplag.Token;
import de.jplag.TokenType;

public class SemanticToken extends Token {

    private TokenSemantics semantics;

    /**
     * @return a record containing semantic information about the token.
     */
    public TokenSemantics semantics() {
        return semantics;
    }

    /**
     * Creates a token with column, length and semantic information.
     * @param type is the token type.
     * @param file is the name of the source code file.
     * @param line is the line index in the source code where the token resides. Index is 1-based.
     * @param column is the column index, meaning where the token starts in the line. Index is 1-based.
     * @param length is the length of the token in the source code.
     * @param semantics is a record containing semantic information about the token.
     */
    public SemanticToken(TokenType type, File file, int line, int column, int length, TokenSemantics semantics) {
        super(type, file, line, column, length);
        this.semantics = semantics;
    }

    public static SemanticToken fileEnd(File file) {
        TokenSemantics semantics = new TokenSemanticsBuilder().control().critical().build(); // todo
        return new SemanticToken(SharedTokenType.FILE_END, file, NO_VALUE, NO_VALUE, NO_VALUE, semantics);
    }
}
