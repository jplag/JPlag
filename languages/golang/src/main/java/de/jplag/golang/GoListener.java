package de.jplag.golang;

import static de.jplag.golang.GoTokenAttribute.ARGUMENT;
import static de.jplag.golang.GoTokenAttribute.ARRAY_BODY_BEGIN;
import static de.jplag.golang.GoTokenAttribute.ARRAY_BODY_END;
import static de.jplag.golang.GoTokenAttribute.ARRAY_CONSTRUCTOR;
import static de.jplag.golang.GoTokenAttribute.ARRAY_ELEMENT;
import static de.jplag.golang.GoTokenAttribute.ASSIGNMENT;
import static de.jplag.golang.GoTokenAttribute.BREAK;
import static de.jplag.golang.GoTokenAttribute.CASE_BLOCK_BEGIN;
import static de.jplag.golang.GoTokenAttribute.CASE_BLOCK_END;
import static de.jplag.golang.GoTokenAttribute.CONTINUE;
import static de.jplag.golang.GoTokenAttribute.DEFER;
import static de.jplag.golang.GoTokenAttribute.ELSE_BLOCK_BEGIN;
import static de.jplag.golang.GoTokenAttribute.ELSE_BLOCK_END;
import static de.jplag.golang.GoTokenAttribute.FALLTHROUGH;
import static de.jplag.golang.GoTokenAttribute.FOR_BLOCK_BEGIN;
import static de.jplag.golang.GoTokenAttribute.FOR_BLOCK_END;
import static de.jplag.golang.GoTokenAttribute.FOR_STATEMENT;
import static de.jplag.golang.GoTokenAttribute.FUNCTION_BODY_BEGIN;
import static de.jplag.golang.GoTokenAttribute.FUNCTION_BODY_END;
import static de.jplag.golang.GoTokenAttribute.FUNCTION_DECLARATION;
import static de.jplag.golang.GoTokenAttribute.FUNCTION_LITERAL;
import static de.jplag.golang.GoTokenAttribute.FUNCTION_PARAMETER;
import static de.jplag.golang.GoTokenAttribute.GO;
import static de.jplag.golang.GoTokenAttribute.GOTO;
import static de.jplag.golang.GoTokenAttribute.IF_BLOCK_BEGIN;
import static de.jplag.golang.GoTokenAttribute.IF_BLOCK_END;
import static de.jplag.golang.GoTokenAttribute.IF_STATEMENT;
import static de.jplag.golang.GoTokenAttribute.IMPORT_CLAUSE;
import static de.jplag.golang.GoTokenAttribute.IMPORT_CLAUSE_BEGIN;
import static de.jplag.golang.GoTokenAttribute.IMPORT_CLAUSE_END;
import static de.jplag.golang.GoTokenAttribute.IMPORT_DECLARATION;
import static de.jplag.golang.GoTokenAttribute.INTERFACE_BLOCK_BEGIN;
import static de.jplag.golang.GoTokenAttribute.INTERFACE_BLOCK_END;
import static de.jplag.golang.GoTokenAttribute.INTERFACE_DECLARATION;
import static de.jplag.golang.GoTokenAttribute.INTERFACE_METHOD;
import static de.jplag.golang.GoTokenAttribute.INVOCATION;
import static de.jplag.golang.GoTokenAttribute.MAP_BODY_BEGIN;
import static de.jplag.golang.GoTokenAttribute.MAP_BODY_END;
import static de.jplag.golang.GoTokenAttribute.MAP_CONSTRUCTOR;
import static de.jplag.golang.GoTokenAttribute.MAP_ELEMENT;
import static de.jplag.golang.GoTokenAttribute.MEMBER_DECLARATION;
import static de.jplag.golang.GoTokenAttribute.NAMED_TYPE_BODY_BEGIN;
import static de.jplag.golang.GoTokenAttribute.NAMED_TYPE_BODY_END;
import static de.jplag.golang.GoTokenAttribute.NAMED_TYPE_CONSTRUCTOR;
import static de.jplag.golang.GoTokenAttribute.NAMED_TYPE_ELEMENT;
import static de.jplag.golang.GoTokenAttribute.PACKAGE;
import static de.jplag.golang.GoTokenAttribute.RECEIVER;
import static de.jplag.golang.GoTokenAttribute.RECEIVE_STATEMENT;
import static de.jplag.golang.GoTokenAttribute.RETURN;
import static de.jplag.golang.GoTokenAttribute.SELECT_BLOCK_BEGIN;
import static de.jplag.golang.GoTokenAttribute.SELECT_BLOCK_END;
import static de.jplag.golang.GoTokenAttribute.SELECT_STATEMENT;
import static de.jplag.golang.GoTokenAttribute.SEND_STATEMENT;
import static de.jplag.golang.GoTokenAttribute.SLICE_BODY_BEGIN;
import static de.jplag.golang.GoTokenAttribute.SLICE_BODY_END;
import static de.jplag.golang.GoTokenAttribute.SLICE_CONSTRUCTOR;
import static de.jplag.golang.GoTokenAttribute.SLICE_ELEMENT;
import static de.jplag.golang.GoTokenAttribute.STATEMENT_BLOCK_BEGIN;
import static de.jplag.golang.GoTokenAttribute.STATEMENT_BLOCK_END;
import static de.jplag.golang.GoTokenAttribute.STRUCT_BODY_BEGIN;
import static de.jplag.golang.GoTokenAttribute.STRUCT_BODY_END;
import static de.jplag.golang.GoTokenAttribute.STRUCT_DECLARATION;
import static de.jplag.golang.GoTokenAttribute.SWITCH_BLOCK_BEGIN;
import static de.jplag.golang.GoTokenAttribute.SWITCH_BLOCK_END;
import static de.jplag.golang.GoTokenAttribute.SWITCH_CASE;
import static de.jplag.golang.GoTokenAttribute.SWITCH_STATEMENT;
import static de.jplag.golang.GoTokenAttribute.TYPE_ASSERTION;
import static de.jplag.golang.GoTokenAttribute.TYPE_CONSTRAINT;
import static de.jplag.golang.GoTokenAttribute.VARIABLE_DECLARATION;

import java.util.function.Function;

import org.antlr.v4.runtime.ParserRuleContext;

import de.jplag.antlr.AbstractAntlrListener;
import de.jplag.antlr.ContextVisitor;
import de.jplag.golang.grammar.GoParser.ArgumentsContext;
import de.jplag.golang.grammar.GoParser.ArrayTypeContext;
import de.jplag.golang.grammar.GoParser.AssignmentContext;
import de.jplag.golang.grammar.GoParser.BlockContext;
import de.jplag.golang.grammar.GoParser.BreakStmtContext;
import de.jplag.golang.grammar.GoParser.CommCaseContext;
import de.jplag.golang.grammar.GoParser.CommClauseContext;
import de.jplag.golang.grammar.GoParser.CompositeLitContext;
import de.jplag.golang.grammar.GoParser.ConstSpecContext;
import de.jplag.golang.grammar.GoParser.ContinueStmtContext;
import de.jplag.golang.grammar.GoParser.DeferStmtContext;
import de.jplag.golang.grammar.GoParser.ExprCaseClauseContext;
import de.jplag.golang.grammar.GoParser.ExprSwitchStmtContext;
import de.jplag.golang.grammar.GoParser.ExpressionContext;
import de.jplag.golang.grammar.GoParser.FallthroughStmtContext;
import de.jplag.golang.grammar.GoParser.FieldDeclContext;
import de.jplag.golang.grammar.GoParser.ForStmtContext;
import de.jplag.golang.grammar.GoParser.FunctionDeclContext;
import de.jplag.golang.grammar.GoParser.FunctionLitContext;
import de.jplag.golang.grammar.GoParser.GoStmtContext;
import de.jplag.golang.grammar.GoParser.GotoStmtContext;
import de.jplag.golang.grammar.GoParser.IfStmtContext;
import de.jplag.golang.grammar.GoParser.ImportDeclContext;
import de.jplag.golang.grammar.GoParser.ImportSpecContext;
import de.jplag.golang.grammar.GoParser.InterfaceTypeContext;
import de.jplag.golang.grammar.GoParser.KeyedElementContext;
import de.jplag.golang.grammar.GoParser.LiteralTypeContext;
import de.jplag.golang.grammar.GoParser.MapTypeContext;
import de.jplag.golang.grammar.GoParser.MethodDeclContext;
import de.jplag.golang.grammar.GoParser.MethodSpecContext;
import de.jplag.golang.grammar.GoParser.PackageClauseContext;
import de.jplag.golang.grammar.GoParser.ParameterDeclContext;
import de.jplag.golang.grammar.GoParser.ReceiverContext;
import de.jplag.golang.grammar.GoParser.RecvStmtContext;
import de.jplag.golang.grammar.GoParser.ReturnStmtContext;
import de.jplag.golang.grammar.GoParser.SelectStmtContext;
import de.jplag.golang.grammar.GoParser.SendStmtContext;
import de.jplag.golang.grammar.GoParser.ShortVarDeclContext;
import de.jplag.golang.grammar.GoParser.SliceTypeContext;
import de.jplag.golang.grammar.GoParser.StatementContext;
import de.jplag.golang.grammar.GoParser.StatementListContext;
import de.jplag.golang.grammar.GoParser.StructTypeContext;
import de.jplag.golang.grammar.GoParser.SwitchStmtContext;
import de.jplag.golang.grammar.GoParser.TypeAssertionContext;
import de.jplag.golang.grammar.GoParser.TypeCaseClauseContext;
import de.jplag.golang.grammar.GoParser.TypeNameContext;
import de.jplag.golang.grammar.GoParser.TypeSwitchStmtContext;
import de.jplag.golang.grammar.GoParser.VarDeclContext;

/**
 * Provides token extraction rules for {@link GoLanguage} Based on an older implementation of the language module; see
 * JPlagGoListener.java in the history.
 */
public class GoListener extends AbstractAntlrListener {
    public GoListener() {
        metaDeclarations();

        interfaceDeclarations();
        structDeclarations();

        functionDeclarations();

        controlFlowRules();
        statements();

        objectCreation();
        controlFlowKeywords();
    }

    private void metaDeclarations() {
        visit(PackageClauseContext.class).mapRange(PACKAGE);

        visit(ImportDeclContext.class).map(IMPORT_DECLARATION);
        visit(ImportDeclContext.class).delegateTerminal(ImportDeclContext::L_PAREN).map(IMPORT_CLAUSE_BEGIN);
        visit(ImportDeclContext.class).delegateTerminalExit(ImportDeclContext::R_PAREN).map(IMPORT_CLAUSE_END);

        visit(ImportSpecContext.class).mapRange(IMPORT_CLAUSE);
    }

    private void interfaceDeclarations() {
        visit(InterfaceTypeContext.class).mapEnter(INTERFACE_DECLARATION);
        visit(InterfaceTypeContext.class).delegateTerminal(InterfaceTypeContext::L_CURLY).map(INTERFACE_BLOCK_BEGIN);
        visit(InterfaceTypeContext.class).delegateTerminalExit(InterfaceTypeContext::R_CURLY).map(INTERFACE_BLOCK_END);
    }

    private void structDeclarations() {
        visit(StructTypeContext.class).map(STRUCT_DECLARATION);
        visit(StructTypeContext.class).delegateTerminal(StructTypeContext::L_CURLY).map(STRUCT_BODY_BEGIN);
        visit(StructTypeContext.class).delegateTerminalExit(StructTypeContext::R_CURLY).map(STRUCT_BODY_END);

        visit(FieldDeclContext.class).mapRange(MEMBER_DECLARATION);
    }

    private void functionDeclarations() {
        visit(FunctionDeclContext.class).delegateTerminal(FunctionDeclContext::FUNC).map(FUNCTION_DECLARATION);
        visit(FunctionDeclContext.class).delegateTerminal(context -> context.block().L_CURLY()).map(FUNCTION_BODY_BEGIN);
        visit(FunctionDeclContext.class).delegateTerminalExit(context -> context.block().R_CURLY()).map(FUNCTION_BODY_END);

        visit(MethodDeclContext.class).delegateTerminal(MethodDeclContext::FUNC).map(FUNCTION_DECLARATION);
        visit(MethodDeclContext.class).delegateTerminal(context -> context.block().L_CURLY()).map(FUNCTION_BODY_BEGIN);
        visit(MethodDeclContext.class).delegateTerminalExit(context -> context.block().R_CURLY()).map(FUNCTION_BODY_END);

        visit(ParameterDeclContext.class, context -> !(context.parent.parent instanceof ReceiverContext)).mapRange(FUNCTION_PARAMETER);
        visit(ParameterDeclContext.class, context -> (context.parent.parent instanceof ReceiverContext)).mapRange(RECEIVER);
    }

    private void controlFlowRules() {
        visit(IfStmtContext.class).delegateTerminal(IfStmtContext::IF).map(IF_STATEMENT);
        visit(BlockContext.class, context -> context.parent instanceof IfStmtContext ifStmt && context.equals((ifStmt).block(0))).map(IF_BLOCK_BEGIN,
                IF_BLOCK_END);
        visit(BlockContext.class, context -> context.parent instanceof IfStmtContext ifStmt && context.equals((ifStmt).block(1)))
                .map(ELSE_BLOCK_BEGIN, ELSE_BLOCK_END);

        visit(ForStmtContext.class).map(FOR_STATEMENT);
        visit(ForStmtContext.class).delegateTerminal(context -> context.block().L_CURLY()).map(FOR_BLOCK_BEGIN);
        visit(ForStmtContext.class).delegateTerminalExit(context -> context.block().R_CURLY()).map(FOR_BLOCK_END);

        visit(SwitchStmtContext.class).map(SWITCH_STATEMENT);
        visit(ExprSwitchStmtContext.class).delegateTerminal(ExprSwitchStmtContext::L_CURLY).map(SWITCH_BLOCK_BEGIN);
        visit(TypeSwitchStmtContext.class).delegateTerminal(TypeSwitchStmtContext::L_CURLY).map(SWITCH_BLOCK_BEGIN);
        visit(ExprSwitchStmtContext.class).delegateTerminalExit(ExprSwitchStmtContext::R_CURLY).map(SWITCH_BLOCK_END);
        visit(TypeSwitchStmtContext.class).delegateTerminalExit(TypeSwitchStmtContext::R_CURLY).map(SWITCH_BLOCK_END);

        visit(ExprCaseClauseContext.class).map(SWITCH_CASE);
        visit(StatementListContext.class, context -> context.parent instanceof ExprCaseClauseContext).map(CASE_BLOCK_BEGIN, CASE_BLOCK_END);
        visit(TypeCaseClauseContext.class).map(SWITCH_CASE);
        visit(StatementListContext.class, context -> context.parent instanceof TypeCaseClauseContext).map(CASE_BLOCK_BEGIN, CASE_BLOCK_END);

        visit(SelectStmtContext.class).map(SELECT_STATEMENT);
        visit(SelectStmtContext.class).delegateTerminal(SelectStmtContext::L_CURLY).map(SELECT_BLOCK_BEGIN);
        visit(SelectStmtContext.class).delegateTerminalExit(SelectStmtContext::R_CURLY).map(SELECT_BLOCK_END);

        visit(CommCaseContext.class).map(SWITCH_CASE);
        visit(StatementListContext.class, context -> context.parent instanceof CommClauseContext).map(CASE_BLOCK_BEGIN, CASE_BLOCK_END);
    }

    private void statements() {
        visit(VarDeclContext.class).mapRange(VARIABLE_DECLARATION);
        visit(ConstSpecContext.class).map(VARIABLE_DECLARATION);

        visit(FunctionLitContext.class).map(FUNCTION_LITERAL);
        visit(FunctionLitContext.class).delegateContext(FunctionLitContext::block).map(FUNCTION_BODY_BEGIN, FUNCTION_BODY_END);

        visit(AssignmentContext.class).mapRange(ASSIGNMENT);

        visit(ShortVarDeclContext.class).map(VARIABLE_DECLARATION);
        visit(ShortVarDeclContext.class).map(ASSIGNMENT);

        visit(ArgumentsContext.class).mapRange(INVOCATION);
        visit(ExpressionContext.class, context -> hasAncestor(context, ArgumentsContext.class)).mapRange(ARGUMENT);

        visit(StatementContext.class).delegateContext(StatementContext::block).map(STATEMENT_BLOCK_BEGIN, STATEMENT_BLOCK_END);
    }

    private void objectCreation() {
        visitKeyedElement(LiteralTypeContext::arrayType).mapRange(ARRAY_ELEMENT);
        visitKeyedElement(LiteralTypeContext::structType).mapRange(MEMBER_DECLARATION);
        visitKeyedElement(LiteralTypeContext::mapType).mapRange(MAP_ELEMENT);
        visitKeyedElement(LiteralTypeContext::sliceType).mapRange(SLICE_ELEMENT);
        visitKeyedElement(LiteralTypeContext::typeName).mapRange(NAMED_TYPE_ELEMENT);

        visitCompositeLitChild(ArrayTypeContext.class).map(ARRAY_CONSTRUCTOR);
        visitCompositeLitDelegate(ArrayTypeContext.class).map(ARRAY_BODY_BEGIN, ARRAY_BODY_END);

        visitCompositeLitChild(SliceTypeContext.class).map(SLICE_CONSTRUCTOR);
        visitCompositeLitDelegate(SliceTypeContext.class).map(SLICE_BODY_BEGIN, SLICE_BODY_END);

        visitCompositeLitChild(MapTypeContext.class).map(MAP_CONSTRUCTOR);
        visitCompositeLitDelegate(MapTypeContext.class).map(MAP_BODY_BEGIN, MAP_BODY_END);

        visitCompositeLitChild(TypeNameContext.class).map(NAMED_TYPE_CONSTRUCTOR);
        visitCompositeLitDelegate(TypeNameContext.class).map(NAMED_TYPE_BODY_BEGIN, NAMED_TYPE_BODY_END);
        visit(TypeNameContext.class, context -> context.parent instanceof InterfaceTypeContext).mapRange(TYPE_CONSTRAINT);

        visit(TypeAssertionContext.class).mapRange(TYPE_ASSERTION);
        visit(MethodSpecContext.class).mapRange(INTERFACE_METHOD);
    }

    private void controlFlowKeywords() {
        visit(ReturnStmtContext.class).mapRange(RETURN);
        visit(BreakStmtContext.class).mapRange(BREAK);
        visit(ContinueStmtContext.class).mapRange(CONTINUE);
        visit(FallthroughStmtContext.class).mapRange(FALLTHROUGH);
        visit(GotoStmtContext.class).mapRange(GOTO);
        visit(GoStmtContext.class).mapRange(GO);
        visit(DeferStmtContext.class).mapRange(DEFER);
        visit(SendStmtContext.class).mapRange(SEND_STATEMENT);
        visit(RecvStmtContext.class).mapRange(RECEIVE_STATEMENT);
    }

    private <T extends ParserRuleContext> de.jplag.antlr.ContextVisitor<T> visitCompositeLitChild(Class<T> type) {
        return visit(type, context -> context.parent.parent instanceof CompositeLitContext);
    }

    private <T extends ParserRuleContext> ContextVisitor<CompositeLitContext> visitCompositeLitDelegate(Class<T> type) {
        return visit(CompositeLitContext.class,
                context -> context.literalType().children.stream().anyMatch(it -> type.isAssignableFrom(it.getClass())));
    }

    private ContextVisitor<KeyedElementContext> visitKeyedElement(Function<LiteralTypeContext, ?> typeGetter) {
        return visit(KeyedElementContext.class, context -> {
            CompositeLitContext parent = getAncestor(context, CompositeLitContext.class);
            if (parent == null) {
                return false;
            }

            LiteralTypeContext typeContext = parent.literalType();
            if (typeContext == null) {
                return false;
            }

            return typeGetter.apply(typeContext) != null;
        });
    }
}
