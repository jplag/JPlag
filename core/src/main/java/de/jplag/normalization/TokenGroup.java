package de.jplag.normalization;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import de.jplag.Token;
import de.jplag.semantics.TokenSemantics;

public class TokenGroup implements Comparable<TokenGroup> {

    List<Token> tokens;
    TokenSemantics semantics;

    public TokenGroup(List<Token> tokens) {
        this.tokens = Collections.unmodifiableList(tokens);
        this.semantics = TokenSemantics.join(tokens.stream().map(Token::getSemantics).toList());
    }

    public static List<TokenGroup> group(List<Token> tokens) {
        List<TokenGroup> tokenGroups = new LinkedList<>();
        List<Token> groupTokens = new LinkedList<>();
        int currentLine = tokens.get(0).getLine();
        for (Token t : tokens) {
            if (t.getLine() != currentLine) {
                currentLine = t.getLine();
                tokenGroups.add(new TokenGroup(new LinkedList<>(groupTokens)));
                groupTokens.clear();
            }
            groupTokens.add(t);
        }
        tokenGroups.add(new TokenGroup(new LinkedList<>(groupTokens)));
        return tokenGroups;
    }

    public static List<Token> ungroup(List<TokenGroup> tokenGroups) {
        return tokenGroups.stream().flatMap(tg -> tg.tokens.stream()).toList();
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
        return String.join(" ", tokens.stream().map(Token::toString).toList());
    }
}
