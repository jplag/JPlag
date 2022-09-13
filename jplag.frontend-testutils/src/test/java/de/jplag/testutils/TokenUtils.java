package de.jplag.testutils;

import java.util.List;

import de.jplag.Token;

public final class TokenUtils {

    private TokenUtils() {
        // private constructor to prevent instantiation
    }

    /**
     * Returns the type of all tokens that belong to a certain file.
     * @param tokenList is the list of {@link Token Tokens}.
     * @param name is the name of the target file.
     * @return the immutable list of token types.
     */
    public static List<Integer> tokenTypesByFile(List<Token> tokens, String name) {
        return tokensByFile(tokens, name).stream().map(Token::getType).toList();
    }

    /**
     * Returns the tokens that belong to a certain file.
     * @param tokenList is the list of {@link Token Tokens}.
     * @param name is the name of the target file.
     * @return the immutable list of tokens.
     */
    public static List<Token> tokensByFile(List<Token> tokens, String name) {
        return tokens.stream().filter(it -> it.getFile().startsWith(name)).toList();
    }

}
