package de.jplag;

import java.util.ArrayList;
import java.util.List;

/**
 * List of tokens. Allows random access to individual tokens. Contains a hash map for token hashes.
 */
public class TokenList {
    private final List<Token> tokens;
    TokenHashMap tokenHashes = null;
    int hash_length = -1;

    public TokenList() {
        tokens = new ArrayList<>();
    }

    public final int size() {
        return tokens.size();
    }

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
        StringBuffer buffer = new StringBuffer();

        try {
            for (int i = 0; i < tokens.size(); i++) {
                buffer.append(i);
                buffer.append("\t");
                buffer.append(tokens.get(i).toString());
                if (i < tokens.size() - 1) {
                    buffer.append("\n");
                }
            }
        } catch (OutOfMemoryError e) {
            return "Tokenlist to large for output: " + tokens.size() + " Tokens";
        }
        return buffer.toString();
    }
}
