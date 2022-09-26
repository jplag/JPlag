package de.jplag.testutils;

import java.io.File;
import java.util.List;

import de.jplag.Token;
import de.jplag.TokenType;

public final class TokenUtils {

    private TokenUtils() {
        // private constructor to prevent instantiation
    }

    /**
     * Returns the type of all tokens that belong to a certain file.
     * @param tokens is the list of {@link Token Tokens}.
     * @param file is the target file.
     * @return the immutable list of token types.
     */
    public static List<TokenType> tokenTypesByFile(List<Token> tokens, File file) {
        return tokensByFile(tokens, file).stream().map(Token::getType).toList();
    }

    /**
     * Returns the tokens that belong to a certain file.
     * @param tokens is the list of {@link Token Tokens}.
     * @param file is the target file.
     * @return the immutable list of tokens.
     */
    public static List<Token> tokensByFile(List<Token> tokens, File file) {
        return tokens.stream().filter(it -> it.getFile().equals(file)).toList();
    }

}
