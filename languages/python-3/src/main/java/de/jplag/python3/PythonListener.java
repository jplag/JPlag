package de.jplag.python3;

import static de.jplag.python3.Python3TokenAttribute.APPLY;
import static de.jplag.python3.Python3TokenAttribute.ARRAY;
import static de.jplag.python3.Python3TokenAttribute.ASSERT;
import static de.jplag.python3.Python3TokenAttribute.ASSIGN;
import static de.jplag.python3.Python3TokenAttribute.ASYNC;
import static de.jplag.python3.Python3TokenAttribute.AWAIT;
import static de.jplag.python3.Python3TokenAttribute.BREAK;
import static de.jplag.python3.Python3TokenAttribute.CLASS_BEGIN;
import static de.jplag.python3.Python3TokenAttribute.CLASS_END;
import static de.jplag.python3.Python3TokenAttribute.CONTINUE;
import static de.jplag.python3.Python3TokenAttribute.DEC_BEGIN;
import static de.jplag.python3.Python3TokenAttribute.DEC_END;
import static de.jplag.python3.Python3TokenAttribute.DEL;
import static de.jplag.python3.Python3TokenAttribute.EXCEPT_BEGIN;
import static de.jplag.python3.Python3TokenAttribute.EXCEPT_END;
import static de.jplag.python3.Python3TokenAttribute.FINALLY;
import static de.jplag.python3.Python3TokenAttribute.FOR_BEGIN;
import static de.jplag.python3.Python3TokenAttribute.FOR_END;
import static de.jplag.python3.Python3TokenAttribute.IF_BEGIN;
import static de.jplag.python3.Python3TokenAttribute.IF_END;
import static de.jplag.python3.Python3TokenAttribute.IMPORT;
import static de.jplag.python3.Python3TokenAttribute.LAMBDA;
import static de.jplag.python3.Python3TokenAttribute.METHOD_BEGIN;
import static de.jplag.python3.Python3TokenAttribute.METHOD_END;
import static de.jplag.python3.Python3TokenAttribute.RAISE;
import static de.jplag.python3.Python3TokenAttribute.RETURN;
import static de.jplag.python3.Python3TokenAttribute.TRY_BEGIN;
import static de.jplag.python3.Python3TokenAttribute.WHILE_BEGIN;
import static de.jplag.python3.Python3TokenAttribute.WHILE_END;
import static de.jplag.python3.Python3TokenAttribute.WITH_BEGIN;
import static de.jplag.python3.Python3TokenAttribute.WITH_END;
import static de.jplag.python3.Python3TokenAttribute.YIELD;

import de.jplag.antlr.AbstractAntlrListener;
import de.jplag.python3.grammar.Python3Parser;
import de.jplag.python3.grammar.Python3Parser.Assert_stmtContext;
import de.jplag.python3.grammar.Python3Parser.AugassignContext;
import de.jplag.python3.grammar.Python3Parser.Break_stmtContext;
import de.jplag.python3.grammar.Python3Parser.ClassdefContext;
import de.jplag.python3.grammar.Python3Parser.Continue_stmtContext;
import de.jplag.python3.grammar.Python3Parser.DecoratedContext;
import de.jplag.python3.grammar.Python3Parser.Del_stmtContext;
import de.jplag.python3.grammar.Python3Parser.DictorsetmakerContext;
import de.jplag.python3.grammar.Python3Parser.Except_clauseContext;
import de.jplag.python3.grammar.Python3Parser.For_stmtContext;
import de.jplag.python3.grammar.Python3Parser.FuncdefContext;
import de.jplag.python3.grammar.Python3Parser.If_stmtContext;
import de.jplag.python3.grammar.Python3Parser.Import_stmtContext;
import de.jplag.python3.grammar.Python3Parser.LambdefContext;
import de.jplag.python3.grammar.Python3Parser.Raise_stmtContext;
import de.jplag.python3.grammar.Python3Parser.Return_stmtContext;
import de.jplag.python3.grammar.Python3Parser.Testlist_compContext;
import de.jplag.python3.grammar.Python3Parser.TrailerContext;
import de.jplag.python3.grammar.Python3Parser.Try_stmtContext;
import de.jplag.python3.grammar.Python3Parser.While_stmtContext;
import de.jplag.python3.grammar.Python3Parser.With_stmtContext;
import de.jplag.python3.grammar.Python3Parser.Yield_argContext;
import de.jplag.python3.grammar.Python3Parser.Yield_stmtContext;

public class PythonListener extends AbstractAntlrListener {
    public PythonListener() {
        statements();
        controlStructures();
        contexts();
        values();
    }

    private void statements() {
        visit(Assert_stmtContext.class).map(ASSERT);
        visit(Raise_stmtContext.class).map(RAISE);
        visit(Return_stmtContext.class).map(RETURN);
        visit(Yield_argContext.class).map(YIELD);
        visit(Yield_stmtContext.class).map(YIELD);
        visit(Import_stmtContext.class).map(IMPORT);
        visit(Break_stmtContext.class).map(BREAK);
        visit(Continue_stmtContext.class).map(CONTINUE);
        visit(Del_stmtContext.class).map(DEL);
        visit(Python3Parser.FINALLY).map(FINALLY);

        visit(Python3Parser.ASYNC).map(ASYNC);
        visit(Python3Parser.AWAIT).map(AWAIT);

        visit(Except_clauseContext.class).map(EXCEPT_BEGIN, EXCEPT_END);
    }

    private void controlStructures() {
        visit(While_stmtContext.class).map(WHILE_BEGIN, WHILE_END);
        visit(Try_stmtContext.class).map(TRY_BEGIN);
        visit(If_stmtContext.class).map(IF_BEGIN, IF_END);
        visit(With_stmtContext.class).map(WITH_BEGIN, WITH_END);
        visit(For_stmtContext.class).map(FOR_BEGIN, FOR_END);
    }

    private void contexts() {
        visit(DecoratedContext.class).map(DEC_BEGIN, DEC_END);
        visit(LambdefContext.class).map(LAMBDA);
        visit(ClassdefContext.class).map(CLASS_BEGIN, CLASS_END);
        visit(FuncdefContext.class).map(METHOD_BEGIN, METHOD_END);
    }

    private void values() {
        visit(DictorsetmakerContext.class).map(ARRAY);
        visit(Testlist_compContext.class, context -> context.getText().contains(",")).map(ARRAY);
        visit(AugassignContext.class).map(ASSIGN);
        visit(Python3Parser.ASSIGN).map(ASSIGN);

        visit(TrailerContext.class, ctx -> ctx.getText().charAt(0) == '(').map(APPLY);
        visit(TrailerContext.class, ctx -> ctx.getText().charAt(0) != '(').map(ARRAY);
    }
}
