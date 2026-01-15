package de.jplag;

import java.util.concurrent.ConcurrentMap;

/**
 * The default token equivalence model that can be used by most languages. It assumes tokens are only equivalent if they
 * have the same type and contain no additional data.
 */
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
