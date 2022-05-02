package de.jplag.csharp.grammar;

import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.TokenStream;

/**
 * This class was taken from <a href="https://github.com/antlr/grammars-v4/tree/master/csharp">antlr/grammars-v4</a>. It
 * was originally written by Ken Domino. Note that this class is licensed under Eclipse Public License - v 1.0.
 */
public abstract class CSharpParserBase extends Parser {
    protected CSharpParserBase(TokenStream input) {
        super(input);
    }

    protected boolean IsLocalVariableDeclaration() {
        CSharpParser.Local_variable_declarationContext local_var_decl = (CSharpParser.Local_variable_declarationContext) this._ctx;
        if (local_var_decl == null)
            return true;
        CSharpParser.Local_variable_typeContext local_variable_type = local_var_decl.local_variable_type();
        if (local_variable_type == null)
            return true;
        if (local_variable_type.getText().equals("var"))
            return false;
        return true;
    }
}
