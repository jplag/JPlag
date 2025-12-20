package de.jplag.java_cpg.token.semantic;

import de.jplag.TokenType;
import de.jplag.java_cpg.token.cpg.CpgTokenEquivalenceModel;

public class SemanticCpgTokenEquivalenceModel extends CpgTokenEquivalenceModel {

    private final double semanticThreshold;

    public SemanticCpgTokenEquivalenceModel(int semanticThreshold) {
        this.semanticThreshold = (double) semanticThreshold;
    }

    @Override
    public boolean areSecondaryEquivalent(TokenType leftType, TokenType rightType) {
        int[] leftVector = ((SemanticCpgTokenType) leftType).semanticVector().getValue();
        int[] rightVector = ((SemanticCpgTokenType) rightType).semanticVector().getValue();
        double sum = 0;
        for (int i = 0; i < leftVector.length; i++) {
            sum += Math.pow(leftVector[i] - rightVector[i], 2);
        }
        return sum < semanticThreshold;
    }
}
