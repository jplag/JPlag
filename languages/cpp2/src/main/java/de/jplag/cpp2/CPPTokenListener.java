package de.jplag.cpp2;

import de.jplag.cpp2.grammar.CPP14Parser;
import de.jplag.cpp2.grammar.CPP14ParserBaseListener;

public class CPPTokenListener extends CPP14ParserBaseListener {

    private final Parser parser;

    public CPPTokenListener(Parser parser) {
        this.parser = parser;
    }

    @Override
    public void enterClassOrDeclType(CPP14Parser.ClassOrDeclTypeContext ctx) {
        parser.addEnter(CPPTokenType.C_CLASS_BEGIN, ctx.getStart());
    }

    @Override
    public void exitClassOrDeclType(CPP14Parser.ClassOrDeclTypeContext ctx) {
        parser.addExit(CPPTokenType.C_CLASS_END, ctx.getStop());
    }
}
