package de.jplag.java_cpg.token.characteristic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jplag.TokenType;
import de.jplag.java_cpg.token.cpg.CpgTokenEquivalenceModel;

public class CharacteristicCpgTokenEquivalenceModel extends CpgTokenEquivalenceModel {
    private static final Logger logger = LoggerFactory.getLogger(CharacteristicCpgTokenEquivalenceModel.class);
    private final double characteristicVectorThreshold;

    public CharacteristicCpgTokenEquivalenceModel(int characteristicVectorThreshold) {
        this.characteristicVectorThreshold = characteristicVectorThreshold;
    }

    @Override
    public boolean areSecondaryEquivalent(TokenType leftType, TokenType rightType) {
        int[] leftVector = ((CharacteristicCpgTokenType) leftType).characteristicVector().getValue();
        int[] rightVector = ((CharacteristicCpgTokenType) rightType).characteristicVector().getValue();
        double sum = 0;
        for (int i = 0; i < leftVector.length; i++) {
            sum += Math.pow(leftVector[i] - rightVector[i], 2);
        }
        return sum < characteristicVectorThreshold;
    }
}
