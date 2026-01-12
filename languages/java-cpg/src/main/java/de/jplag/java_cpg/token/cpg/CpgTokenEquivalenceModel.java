package de.jplag.java_cpg.token.cpg;

import java.util.List;

import de.jplag.DefaultTokenEquivalenceModel;
import de.jplag.Token;
import de.jplag.TokenType;
import de.jplag.java_cpg.token.characteristic.CharacteristicCpgTokenType;

public class CpgTokenEquivalenceModel extends DefaultTokenEquivalenceModel {
    @Override
    public boolean ensureTokenType(List<Token> tokens) {
        for (Token token : tokens) {
            if (!(token.getType() instanceof CharacteristicCpgTokenType)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public TokenType getPrimaryType(Token token) {
        return ((CharacteristicCpgTokenType) token.getType()).tokenType();
    }

}
