package de.jplag.java_cpg.token.semantic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jplag.TokenType;
import de.jplag.java_cpg.token.cpg.CpgTokenEquivalenceModel;

public class SemanticCpgTokenEquivalenceModel extends CpgTokenEquivalenceModel {
    private static final Logger logger = LoggerFactory.getLogger(SemanticCpgTokenEquivalenceModel.class);
    private final double semanticThreshold;

    public SemanticCpgTokenEquivalenceModel(int semanticThreshold) {
        this.semanticThreshold = semanticThreshold;
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
