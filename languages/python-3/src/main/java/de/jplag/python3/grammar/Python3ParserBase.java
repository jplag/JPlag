package de.jplag.python3.grammar;

import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.TokenStream;

public abstract class Python3ParserBase extends Parser {
    protected Python3ParserBase(TokenStream input) {
        super(input);
    }

    public boolean cannotBePlusMinus() {
        return true;
    }

    public boolean cannotBeDotLpEq() {
        return true;
    }
}
