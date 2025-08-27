package de.jplag.python3.grammar;

import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.TokenStream;

/**
 * ANTLR-based parser base for Python 3.
 */
public abstract class Python3ParserBase extends Parser {
    protected Python3ParserBase(TokenStream input) {
        super(input);
    }

    /**
     * Determines whether the current context prohibits the use of '+' or '-' operators.
     * @return always true.
     */
    public boolean CannotBePlusMinus() {
        return true;
    }

    /**
     * Determines whether the current context prohibits the use of '.', '(' or '=' symbols.
     * @return always true.
     */
    public boolean CannotBeDotLpEq() {
        return true;
    }
}