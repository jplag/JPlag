package de.jplag;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.List;

/**
 * List of tokens. Allows random access to individual tokens. Contains a hash map for token hashes.
 */
public class TokenList {
    private final List<Token> tokens;
    TokenHashMap tokenHashes = null;
    int hashLength = -1;

    /**
     * Creates an empty token list.
     */
    public TokenList() {
        tokens = new ArrayList<>();
    }

    /**
     * @return the number of tokens in the list.
     */
    public final int size() {
        return tokens.size();
    }

    /**
     * Adds an token to the list.
     * @param token is the token to add.
     */
    public final void addToken(Token token) {
        if (tokens.size() > 0) {
            Token lastToken = tokens.get(tokens.size() - 1);
            if (lastToken.getFile().equals(token.getFile())) {
                token.setFile(lastToken.getFile()); // To save memory ...
            }
            if (token.getLine() < lastToken.getLine() && (token.getFile().equals(lastToken.getFile()))) {
                token.setLine(lastToken.getLine()); // just to make sure
            }
        }
        tokens.add(token);
    }

    /**
     * Returns a view on all tokens.
     * @return all tokens.
     */
    public Iterable<Token> allTokens() {
        return new ArrayList<>(tokens);
    }

    /**
     * Grants access to a specific token.
     * @param index is the token index.
     * @return the desired token.
     * @throws IllegalArgumentException if the index is out of bounds.
     */
    public Token getToken(int index) {
        if (index < 0 || index >= tokens.size()) {
            throw new IllegalArgumentException("Cannot access token with index " + index + ", there are only " + tokens.size() + " tokens!");
        }
        return tokens.get(index);
    }

    @Override
    public final String toString() {
        try {
            List<String> tokenStrings = tokens.stream().map(Token::toString).collect(toList());
            return String.join(System.lineSeparator(), tokenStrings);
        } catch (OutOfMemoryError exception) {
            return "Token list to large for output: " + tokens.size() + " Tokens";
        }
    }
}
