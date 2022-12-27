package de.jplag.cpp2;

import de.jplag.cpp2.grammar.CPP14Parser;
import de.jplag.cpp2.grammar.CPP14ParserBaseListener;
import org.antlr.v4.runtime.ParserRuleContext;

public class CPPTokenListener extends CPP14ParserBaseListener {

    private final Parser parser;

    public CPPTokenListener(Parser parser) {
        this.parser = parser;
    }

    @Override
    public void enterCompoundStatement(CPP14Parser.CompoundStatementContext ctx) {
        parser.addEnter(CPPTokenType.C_BLOCK_BEGIN, ctx.getStart());
    }

    @Override
    public void exitCompoundStatement(CPP14Parser.CompoundStatementContext ctx) {
        parser.addExit(CPPTokenType.C_BLOCK_END, ctx.getStop());
    }

    @Override
    public void enterClassSpecifier(CPP14Parser.ClassSpecifierContext ctx) {
        CPP14Parser.ClassKeyContext classKey = ctx.classHead().classKey();
        if (classKey.Class() != null) {
            parser.addEnter(CPPTokenType.C_CLASS_BEGIN, ctx.getStart());
        } else if (classKey.Struct() != null) {
            parser.addEnter(CPPTokenType.C_STRUCT_BEGIN, ctx.getStart());
        }
    }

    @Override
    public void exitClassSpecifier(CPP14Parser.ClassSpecifierContext ctx) {
        CPP14Parser.ClassKeyContext classKey = ctx.classHead().classKey();
        if (classKey.Class() != null) {
            parser.addExit(CPPTokenType.C_CLASS_END, ctx.getStop());
        } else if (classKey.Struct() != null) {
            parser.addExit(CPPTokenType.C_STRUCT_END, ctx.getStop());
        }
    }

    @Override
    public void enterEnumSpecifier(CPP14Parser.EnumSpecifierContext ctx) {
        parser.addEnter(CPPTokenType.C_ENUM_BEGIN, ctx.getStart());
    }
    @Override
    public void exitEnumSpecifier(CPP14Parser.EnumSpecifierContext ctx) {
        parser.addExit(CPPTokenType.C_ENUM_END, ctx.getStop());
    }

    @Override
    public void enterFunctionDefinition(CPP14Parser.FunctionDefinitionContext ctx) {
        parser.addEnter(CPPTokenType.C_FUNCTION_BEGIN, ctx.getStart());
    }
    @Override
    public void exitFunctionDefinition(CPP14Parser.FunctionDefinitionContext ctx) {
        parser.addExit(CPPTokenType.C_FUNCTION_END, ctx.getStop());
    }

    @Override
    public void enterIterationStatement(CPP14Parser.IterationStatementContext ctx) {
        if (ctx.Do() != null) {
            parser.addEnter(CPPTokenType.C_DO_BEGIN, ctx.getStart());
        } else if (ctx.For() != null) {
            parser.addEnter(CPPTokenType.C_FOR_BEGIN, ctx.getStart());
        } else if (ctx.While() != null) {
            parser.addEnter(CPPTokenType.C_WHILE_BEGIN, ctx.getStart());
        }
    }

    @Override
    public void exitIterationStatement(CPP14Parser.IterationStatementContext ctx) {
        if (ctx.Do() != null) {
            parser.addEnter(CPPTokenType.C_DO_END, ctx.getStop());
        } else if (ctx.For() != null) {
            parser.addEnter(CPPTokenType.C_FOR_END, ctx.getStop());
        } else if (ctx.While() != null) {
            parser.addEnter(CPPTokenType.C_WHILE_END, ctx.getStop());
        }
    }

    @Override
    public void enterSelectionStatement(CPP14Parser.SelectionStatementContext ctx) {
        if (ctx.Switch() != null) {
            parser.addEnter(CPPTokenType.C_SWITCH_BEGIN, ctx.getStart());
        } else if (ctx.If() != null) {
            parser.addEnter(CPPTokenType.C_IF_BEGIN, ctx.getStart());
            // TODO this might be bad in terms of ordering
            if (ctx.Else() != null) {
                parser.addEnter(CPPTokenType.C_ELSE, ctx.Else().getSymbol());
            }
        }
    }

    @Override
    public void exitSelectionStatement(CPP14Parser.SelectionStatementContext ctx) {
        if (ctx.Switch() != null) {
            parser.addEnter(CPPTokenType.C_SWITCH_END, ctx.getStop());
        } else if (ctx.If() != null) {
            parser.addEnter(CPPTokenType.C_IF_END, ctx.getStop());
        }
    }

    @Override
    public void enterLabeledStatement(CPP14Parser.LabeledStatementContext ctx) {
        if (ctx.Case() != null) {
            parser.addEnter(CPPTokenType.C_CASE, ctx.getStart());
        } else if (ctx.Default() != null) {
            parser.addEnter(CPPTokenType.C_DEFAULT, ctx.getStart());
        }
    }

    @Override
    public void enterTryBlock(CPP14Parser.TryBlockContext ctx) {
        parser.addEnter(CPPTokenType.C_TRY, ctx.getStart());
    }

    @Override
    public void enterHandler(CPP14Parser.HandlerContext ctx) {
        parser.addEnter(CPPTokenType.C_CATCH_BEGIN, ctx.getStart());
    }

    @Override
    public void exitHandler(CPP14Parser.HandlerContext ctx) {
        parser.addEnter(CPPTokenType.C_CATCH_END, ctx.getStop());
    }

    @Override
    public void enterJumpStatement(CPP14Parser.JumpStatementContext ctx) {
        if (ctx.Break() != null) {
            parser.addEnter(CPPTokenType.C_BREAK, ctx.getStart());
        } else if (ctx.Continue() != null) {
            parser.addEnter(CPPTokenType.C_CONTINUE, ctx.getStart());
        } else if (ctx.Goto() != null) {
            parser.addEnter(CPPTokenType.C_GOTO, ctx.getStart());
        } else if (ctx.Return() != null) {
            parser.addEnter(CPPTokenType.C_RETURN, ctx.getStart());
        }
    }

    @Override
    public void enterThrowExpression(CPP14Parser.ThrowExpressionContext ctx) {
        parser.addEnter(CPPTokenType.C_THROW, ctx.getStart());
    }

    @Override
    public void enterNewExpression(CPP14Parser.NewExpressionContext ctx) {
        // TODO NEWARRAY, ARRAYINIT
        if (ctx.newInitializer() == null) {
            parser.addEnter(CPPTokenType.C_NEWARRAY, ctx.getStart());
        } else {
            parser.addEnter(CPPTokenType.C_NEWCLASS, ctx.getStart());
        }
    }

    @Override
    public void enterTemplateDeclaration(CPP14Parser.TemplateDeclarationContext ctx) {
        parser.addEnter(CPPTokenType.C_GENERIC, ctx.getStart());
    }

    @Override
    public void enterAssignmentOperator(CPP14Parser.AssignmentOperatorContext ctx) {
        // does not cover ++, --, this is done via UnaryExpressionContext and PostfixExpressionContext
        parser.addEnter(CPPTokenType.C_ASSIGN, ctx.getStart());
    }

    @Override
    public void enterUnaryExpression(CPP14Parser.UnaryExpressionContext ctx) {
        if (ctx.PlusPlus() != null || ctx.MinusMinus() != null) {
            parser.addEnter(CPPTokenType.C_ASSIGN, ctx.getStart());
        }
    }

    @Override
    public void enterStaticAssertDeclaration(CPP14Parser.StaticAssertDeclarationContext ctx) {
        parser.addEnter(CPPTokenType.C_STATIC_ASSERT, ctx.getStart());
    }

    @Override
    public void enterInitDeclarator(CPP14Parser.InitDeclaratorContext ctx) {
        // TODO covers too much
        // parser.addEnter(CPPTokenType.C_VARDEF, ctx.getStart());
    }

    @Override
    public void enterMemberDeclarator(CPP14Parser.MemberDeclaratorContext ctx) {
        // TODO start pos is variable name
        parser.addEnter(CPPTokenType.C_VARDEF, ctx.getStart());
    }

    @Override
    public void enterEnumeratorDefinition(CPP14Parser.EnumeratorDefinitionContext ctx) {
        parser.addEnter(CPPTokenType.C_VARDEF, ctx.getStart());
    }

    @Override
    public void enterConditionalExpression(CPP14Parser.ConditionalExpressionContext ctx) {
        if (ctx.Question() != null) {
            parser.addEnter(CPPTokenType.C_QUESTIONMARK, ctx.getStart());
        }
    }

    @Override
    public void enterPostfixExpression(CPP14Parser.PostfixExpressionContext ctx) {
        // TODO this only covers foo->bar() and foo.bar()
        if (ctx.LeftParen() != null) {
            parser.addEnter(CPPTokenType.C_APPLY, ctx.getStart());
        } else if (ctx.PlusPlus() != null || ctx.MinusMinus() != null) {
            parser.addEnter(CPPTokenType.C_ASSIGN, ctx.getStart());
        }
    }

    @Override
    public void enterEveryRule(ParserRuleContext ctx) {
        super.enterEveryRule(ctx);
    }
}
