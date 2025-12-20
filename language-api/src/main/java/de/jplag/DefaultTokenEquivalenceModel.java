package de.jplag;

import java.util.List;
import java.util.concurrent.ConcurrentMap;

public class DefaultTokenEquivalenceModel implements TokenEquivalenceModel {

    @Override
    public TokenType getPrimaryType(Token token) {
        return token.getType();
    }

    @Override
    public boolean arePrimaryEquivalent(int leftValue, int rightValue, ConcurrentMap<TokenType, Integer> tokenTypedValues) {
        return leftValue == rightValue;
    }
}

