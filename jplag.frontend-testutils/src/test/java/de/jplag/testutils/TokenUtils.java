package de.jplag.testutils;

import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.stream.StreamSupport;

import de.jplag.Token;
import de.jplag.TokenList;

public final class TokenUtils {

    private TokenUtils() {
        // private constructor to prevent instantiation
    }

    /**
     * Returns the type of all tokens in a {@link TokenList} that belong to a file.
     * @param tokens is the {@link TokenList}.
     * @param name is the name of the target file.
     * @return the immutable list of token types.
     */
    public static List<Integer> tokenTypesByFile(TokenList tokenList, String name) {
        var tokens = tokensByFile(tokenList, name);
        return tokens.stream().map(Token::getType).collect(toList());
    }

    /**
     * Returns the tokens in a {@link TokenList} that belong to a file.
     * @param tokens is the {@link TokenList}.
     * @param name is the name of the target file.
     * @return the immutable list of tokens.
     */
    public static List<Token> tokensByFile(TokenList tokenList, String name) {
        var tokens = StreamSupport.stream(tokenList.allTokens().spliterator(), false);
        return tokens.filter(it -> it.getFile().startsWith(name)).collect(toList());
    }

}
