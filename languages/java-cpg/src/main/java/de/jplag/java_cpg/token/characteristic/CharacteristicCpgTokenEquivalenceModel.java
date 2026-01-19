package de.jplag.java_cpg.token.characteristic;

import de.jplag.TokenType;
import de.jplag.java_cpg.token.cpg.CpgTokenEquivalenceModel;

/**
 * A token equivalence model that determines whether to tokens are equivalent based on
 * {@link CharacteristicCpgTokenType}.
 */
public class CharacteristicCpgTokenEquivalenceModel extends CpgTokenEquivalenceModel {
    private final double characteristicVectorThreshold;
    private final VectorComparisonMode vectorComparisonMode;

    /**
     * Creates a new characteristic CPG token equivalence model.
     * @param characteristicVectorThreshold The threshold for the characteristic vectors to be considered equivalent
     */
    public CharacteristicCpgTokenEquivalenceModel(int characteristicVectorThreshold, VectorComparisonMode vectorComparisonMode) {
        this.characteristicVectorThreshold = characteristicVectorThreshold;
        this.vectorComparisonMode = vectorComparisonMode;
    }

    @Override
    public boolean areSecondaryEquivalent(TokenType leftType, TokenType rightType) {
        int[] leftVector = ((CharacteristicCpgTokenType) leftType).characteristicVector().getValue();
        int[] rightVector = ((CharacteristicCpgTokenType) rightType).characteristicVector().getValue();
        if (vectorComparisonMode == VectorComparisonMode.EUCLIDEAN_DISTANCE) {
            return areEquivalentEuclidean(leftVector, rightVector);
        } else if (vectorComparisonMode == VectorComparisonMode.L1_DISTANCE) {
            return areEquivalentL1(leftVector, rightVector);
        } else {
            return areEquivalentCosine(leftVector, rightVector);
        }
    }

    private boolean areEquivalentEuclidean(int[] leftVector, int[] rightVector) {
        double sum = 0.0;
        for (int i = 0; i < leftVector.length; i++) {
            sum += Math.pow(leftVector[i] - rightVector[i], 2);
        }
        double distance = Math.sqrt(sum);
        return distance <= characteristicVectorThreshold;
    }

    private boolean areEquivalentL1(int[] leftVector, int[] rightVector) {
        double sum = 0.0;
        for (int i = 0; i < leftVector.length; i++) {
            sum += Math.abs(leftVector[i] - rightVector[i]);
        }
        return sum <= characteristicVectorThreshold;
    }

    private boolean areEquivalentCosine(int[] leftVector, int[] rightVector) {
        double dotProduct = 0.0;
        double leftMagnitude = 0.0;
        double rightMagnitude = 0.0;
        for (int i = 0; i < leftVector.length; i++) {
            dotProduct += leftVector[i] * rightVector[i];
            leftMagnitude += Math.pow(leftVector[i], 2);
            rightMagnitude += Math.pow(rightVector[i], 2);
        }
        leftMagnitude = Math.sqrt(leftMagnitude);
        rightMagnitude = Math.sqrt(rightMagnitude);
        double cosineSimilarity = dotProduct / (leftMagnitude * rightMagnitude);
        return cosineSimilarity * 100 >= characteristicVectorThreshold;
    }

}
