package de.jplag.java_cpg.token.cpg;

import de.jplag.DefaultTokenEquivalenceModel;
import de.jplag.Token;
import de.jplag.TokenType;
import de.jplag.java_cpg.token.semantic.SemanticCpgTokenType;

import java.util.List;

public class CpgTokenEquivalenceModel extends DefaultTokenEquivalenceModel {
    @Override
    public boolean ensureTokenType(List<Token> tokens) {
        for (Token token : tokens) {
            if (token.getType() instanceof SemanticCpgTokenType) {
                return false;
            }
        }
        return true;
    }

    @Override
    public TokenType getPrimaryType(Token token) {
        return ((SemanticCpgTokenType) token.getType()).tokenType();
    }




}
