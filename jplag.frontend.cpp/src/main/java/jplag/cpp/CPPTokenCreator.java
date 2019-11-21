package jplag.cpp;

import jplag.TokenAdder;

public class CPPTokenCreator {
    TokenAdder adder;

    public CPPTokenCreator(TokenAdder adder) {
        this.adder = adder;
    }

    public void add(int type, Token token) {
        this.adder.addToken(new CPPToken(type, this.adder.currentFile, token.beginLine));
    }
}
