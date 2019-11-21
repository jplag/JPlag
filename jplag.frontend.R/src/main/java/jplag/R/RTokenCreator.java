package jplag.R;

import jplag.R.RToken;
import jplag.TokenAdder;

public class RTokenCreator {
    TokenAdder adder;

    public RTokenCreator(TokenAdder adder) {
        this.adder = adder;
    }

    public void add(int type, org.antlr.v4.runtime.Token tok) {
        this.adder.addToken(new RToken(type, this.adder.currentFile, tok.getLine(), tok.getCharPositionInLine() + 1,
                tok.getText().length()));
    }

    public void addEnd(int type, org.antlr.v4.runtime.Token tok) {
        this.adder.addToken(new RToken(type, this.adder.currentFile, tok.getLine(), this.adder.getLast().getColumn() + 1,0));
    }
}
