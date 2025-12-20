package de.jplag;


import java.util.List;

public interface TokenEquivalenceModel {

    int[] generatePrimaryRepresentation(List<Token> tokens);

    boolean ensureSecondaryTokenType(List<Token> tokens);

    boolean arePrimaryEquivalent(int leftValue, int rightValue);

    default boolean areSecondaryEquivalent(TokenType leftType, TokenType rightType) {
        return true;
    }

}
