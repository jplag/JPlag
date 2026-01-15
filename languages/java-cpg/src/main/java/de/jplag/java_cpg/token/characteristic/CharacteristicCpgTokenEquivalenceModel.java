package de.jplag.java_cpg.token.characteristic;

import de.jplag.TokenType;
import de.jplag.java_cpg.token.cpg.CpgTokenEquivalenceModel;

/**
 * A token equivalence model that determines whether to tokens are equivalent based on
 * {@link CharacteristicCpgTokenType}.
 */
public class CharacteristicCpgTokenEquivalenceModel extends CpgTokenEquivalenceModel {
    private final double characteristicVectorThreshold;

    /**
     * Creates a new characteristic CPG token equivalence model.
     * @param characteristicVectorThreshold The threshold for the characteristic vectors to be considered equivalent
     */
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
