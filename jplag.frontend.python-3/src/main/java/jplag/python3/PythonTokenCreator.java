package jplag.python3;

import jplag.TokenAdder;

public class PythonTokenCreator {
    TokenAdder adder;

    public PythonTokenCreator(TokenAdder adder) {
        this.adder = adder;
    }

    public void add(int type, org.antlr.v4.runtime.Token tok) {
        this.adder.addToken(new Python3Token(type, this.adder.currentFile, tok.getLine(), tok.getCharPositionInLine() + 1,
                tok.getText().length()));
    }

    public void addEnd(int type, org.antlr.v4.runtime.Token tok) {
        this.adder.addToken(new Python3Token(type, this.adder.currentFile, tok.getLine(), this.adder.getLast().getColumn() + 1,0));
    }
}
