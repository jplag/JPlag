package de.jplag.rust.grammar;

import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.TokenStream;

public abstract class RustParserBase extends Parser {
    protected RustParserBase(TokenStream input) {
        super(input);
    }

    public boolean next(char expect) {
        return _input.LA(1) == expect;
    }
}