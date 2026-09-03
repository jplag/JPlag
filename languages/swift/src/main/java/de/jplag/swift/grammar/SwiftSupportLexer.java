// CHECKSTYLE:OFF
package de.jplag.swift.grammar;

import java.util.Stack;

import org.antlr.v4.runtime.*;

public abstract class SwiftSupportLexer extends Lexer {
    protected SwiftSupportLexer(CharStream input) {
        super(input);
    }

    public Stack<Integer> parenthesis = new Stack<Integer>();

    @Override
    public void reset() {
        super.reset();
        parenthesis.clear();
    }
}
