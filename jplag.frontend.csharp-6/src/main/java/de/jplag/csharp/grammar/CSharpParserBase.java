package de.jplag.csharp.grammar;

import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.TokenStream;

import de.jplag.csharp.grammar.CSharpParser.Local_variable_declarationContext;
import de.jplag.csharp.grammar.CSharpParser.Local_variable_typeContext;

/**
 * This class was taken from <a href="https://github.com/antlr/grammars-v4/tree/master/csharp">antlr/grammars-v4</a>. It
 * was originally written by Ken Domino. Note that this class is licensed under Eclipse Public License - v 1.0.
 */
public abstract class CSharpParserBase extends Parser {
    protected CSharpParserBase(TokenStream input) {
        super(input);
    }

    protected boolean isLocalVariableDeclaration() {
        Local_variable_declarationContext localVariable = (Local_variable_declarationContext) this._ctx;
        if (localVariable == null) {
            return true;
        }
        Local_variable_typeContext localVariableType = localVariable.local_variable_type();
        if (localVariableType == null) {
            return true;
        }
        return !localVariableType.getText().equals("var");
    }
}
