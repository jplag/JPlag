package jplag.csharp;

import jplag.TokenAdder;
import jplag.csharp.grammar.CSharpParser;
import org.jetbrains.annotations.NotNull;

public class CSharpTokenAdder {
    @NotNull private TokenAdder adder;

    public CSharpTokenAdder(@NotNull TokenAdder adder) {
        this.adder = adder;
    }

    public void add(int type, antlr.Token tok) {
        if (tok == null) {
            System.out.println("tok == null  ERROR!");
            return;
        }
        adder.addToken(new CSharpToken(type, adder.currentFile, tok.getLine(), tok.getColumn(), tok.getText().length()));
    }

    public void add(int type, CSharpParser p) {
        this.add(type, p.getLastConsumedToken());
    }
}
