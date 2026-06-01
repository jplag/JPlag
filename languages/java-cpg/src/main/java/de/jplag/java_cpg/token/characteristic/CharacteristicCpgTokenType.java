package de.jplag.java_cpg.token.characteristic;

import de.jplag.TokenType;

/**
 * A token type that combines a base token type with a characteristic vector.
 * @param tokenType the base token type
 * @param characteristicVector the characteristic vector
 */
public record CharacteristicCpgTokenType(TokenType tokenType, CharacteristicVector characteristicVector) implements TokenType {

    @Override
    public String getDescription() {
        return tokenType.getDescription() + " " + characteristicVector.toString();
    }

    @Override
    public Boolean isExcludedFromMatching() {
        return tokenType.isExcludedFromMatching();
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof CharacteristicCpgTokenType)) {
            return false;
        }
        return ((CharacteristicCpgTokenType) object).characteristicVector.equals(this.characteristicVector)
                && ((CharacteristicCpgTokenType) object).tokenType.equals(this.tokenType);
    }
}
