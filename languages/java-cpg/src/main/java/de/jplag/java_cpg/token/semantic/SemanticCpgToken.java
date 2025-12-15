package de.jplag.java_cpg.token.semantic;

import de.fraunhofer.aisec.cpg.graph.Name;
import de.jplag.TokenType;
import de.jplag.java_cpg.token.cpg.CpgToken;

import java.io.File;

public class SemanticCpgToken extends CpgToken {
    private final SemanticVector semanticVector;
    public SemanticCpgToken(TokenType tokenType, File file, int startLine, int startColumn, int length, Name name, SemanticVector semanticVector) {
        super(tokenType, file, startLine, startColumn, length, name);
        this.semanticVector = semanticVector;
    }

    @Override
    public String toString() {
        return "%s %s".formatted(super.toString(), semanticVector.toString());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof SemanticCpgToken otherToken)) {
            return false;
        }
        return super.equals(otherToken) && this.semanticVector.equals(otherToken.semanticVector);
    }

    @Override
    public int hashCode() {
        return super.hashCode() + semanticVector.hashCode();
    }


}
