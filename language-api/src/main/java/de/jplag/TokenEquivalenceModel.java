package de.jplag;

import java.util.List;
import java.util.concurrent.ConcurrentMap;

public interface TokenEquivalenceModel {

    TokenType getPrimaryType(Token token);

    default boolean ensureTokenType(List<Token> tokens) {
        return true;
    }

    boolean arePrimaryEquivalent(int leftValue, int rightValue, ConcurrentMap<TokenType, Integer> tokenTypedValues);

    default boolean areSecondaryEquivalent(TokenType leftType, TokenType rightType) {
        return true;
    }

}
