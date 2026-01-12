package de.jplag.java_cpg.token.characteristic;

import de.jplag.TokenType;

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
