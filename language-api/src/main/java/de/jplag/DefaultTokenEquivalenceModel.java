package de.jplag;

import java.util.List;

public class DefaultTokenEquivalenceModel implements TokenEquivalenceModel {

    @Override
    public int[] generatePrimaryRepresentation(List<Token> tokens) {
        return new int[0];
    }

    @Override
    public boolean ensureSecondaryTokenType(List<Token> tokens) {
        return true;
    }

    @Override
    public boolean arePrimaryEquivalent(int leftValue, int rightValue) {
        return leftValue == rightValue;
    }
}
