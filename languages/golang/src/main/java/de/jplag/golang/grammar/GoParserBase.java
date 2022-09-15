package de.jplag.golang.grammar;

import org.antlr.v4.runtime.BufferedTokenStream;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.TokenStream;

/**
 * All parser methods that used in grammar (p, prev, notLineTerminator, etc.) should start with lower case char similar
 * to parser rules. Taken from <a href="https://github.com/antlr/grammars-v4/tree/master/golang">the ANTLR4 Project
 * grammars repository</a>. Licenced under BSD-3.
 */
public abstract class GoParserBase extends Parser {
    protected GoParserBase(TokenStream input) {
        super(input);
    }

    /**
     * Returns true if the current Token is a closing bracket (")" or "}")
     */
    protected boolean closingBracket() {
        BufferedTokenStream stream = (BufferedTokenStream) _input;
        int prevTokenType = stream.LA(1);

        return prevTokenType == GoParser.R_CURLY || prevTokenType == GoParser.R_PAREN;
    }
}
