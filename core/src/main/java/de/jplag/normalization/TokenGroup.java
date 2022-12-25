package de.jplag.normalization;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import de.jplag.Token;
import de.jplag.semantics.TokenSemantics;

class TokenGroup implements Comparable<TokenGroup> {

    private List<Token> tokens;
    private int beginLine;
    private TokenSemantics semantics;
    private boolean keep;

    TokenGroup(List<Token> tokens, int beginLine) {
        this.tokens = Collections.unmodifiableList(tokens);
        this.beginLine = beginLine;
        this.semantics = TokenSemantics.join(tokens.stream().map(Token::getSemantics).toList());
        keep = semantics.critical() || semantics.control();
    }

    public List<Token> tokens() {
        return tokens;
    }

    public TokenSemantics semantics() {
        return semantics;
    }

    public boolean keep() {
        return keep;
    }

    public void markKeep() {
        keep = true;
    }

    private int tokenOrdinal(Token token) {
        return ((Enum<?>) token.getType()).ordinal(); // reflects the order the enums were declared in
    }

    @Override
    public int compareTo(TokenGroup other) {
        int sizeComp = Integer.compare(this.tokens.size(), other.tokens.size());
        if (sizeComp != 0)
            return -sizeComp; // bigger size should come first
        Iterator<Token> tokens = this.tokens.iterator();
        Iterator<Token> otherTokens = other.tokens.iterator();
        for (int i = 0; i < this.tokens.size(); i++) {
            int tokenComp = Integer.compare(tokenOrdinal(tokens.next()), tokenOrdinal(otherTokens.next()));
            if (tokenComp != 0)
                return tokenComp;
        }
        return 0;
    }

    @Override
    public String toString() {
        return beginLine + ": " + String.join(" ", tokens.stream().map(Token::toString).toList());
    }
}
