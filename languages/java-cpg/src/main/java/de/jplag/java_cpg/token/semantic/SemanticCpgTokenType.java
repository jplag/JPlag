package de.jplag.java_cpg.token.semantic;

import de.jplag.TokenType;
import de.jplag.java_cpg.token.cpg.CpgTokenType;

public record SemanticCpgTokenType(CpgTokenType cpgTokenType, SemanticVector semanticVector) implements TokenType {

    @Override
    public String getDescription() {
        return cpgTokenType.getDescription() + " " + semanticVector.toString();
    }

    @Override
    public Boolean isExcludedFromMatching() {
        return cpgTokenType.isExcludedFromMatching();
    }
}
