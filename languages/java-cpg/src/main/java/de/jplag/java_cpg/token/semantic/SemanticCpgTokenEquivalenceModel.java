package de.jplag.java_cpg.token.semantic;

import de.jplag.Token;
import de.jplag.TokenType;
import de.jplag.java_cpg.token.cpg.CpgTokenEquivalenceModel;

import java.util.List;

public class SemanticCpgTokenEquivalenceModel extends CpgTokenEquivalenceModel {

    private final double semanticThreshold;

    public SemanticCpgTokenEquivalenceModel(int semanticThreshold) {
        this.semanticThreshold = (double) semanticThreshold / 100;
    }

    @Override
    public boolean ensureSecondaryTokenType(List<Token> tokens) {
        for (Token token : tokens) {
            if (token.getType() instanceof SemanticCpgTokenType) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean areSecondaryEquivalent(TokenType leftType, TokenType rightType) {
        return leftType.equals(rightType);
    }
}
