package de.jplag.java_cpg.token.semantic;

import de.jplag.TokenType;

public record SemanticCpgTokenType(TokenType tokenType, SemanticVector semanticVector) implements TokenType {

    @Override
    public String getDescription() {
        return tokenType.getDescription() + " " + semanticVector.toString();
    }

    @Override
    public Boolean isExcludedFromMatching() {
        return tokenType.isExcludedFromMatching();
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof SemanticCpgTokenType)) {
            return false;
        }
        return ((SemanticCpgTokenType) object).semanticVector.equals(this.semanticVector)
                && ((SemanticCpgTokenType) object).tokenType.equals(this.tokenType);
    }
}
