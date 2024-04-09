package de.jplag.golang;

import de.jplag.antlr.AbstractAntlrListener;
import de.jplag.golang.grammar.GoParser.*;

import java.util.Objects;

import static de.jplag.golang.GoTokenType.*;

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
        visit(FunctionDeclContext.class).map(FUNCTION_DECLARATION);
        visit(FunctionDeclContext.class).delegateTerminal(context -> context.block().L_CURLY()).map(FUNCTION_BODY_BEGIN);
        visit(FunctionDeclContext.class).delegateTerminalExit(context -> context.block().R_CURLY()).map(FUNCTION_BODY_END);

        visit(MethodDeclContext.class).map(FUNCTION_DECLARATION);
        visit(MethodDeclContext.class).delegateTerminal(context -> context.block().L_CURLY()).map(FUNCTION_BODY_BEGIN);
        visit(MethodDeclContext.class).delegateTerminalExit(context -> context.block().R_CURLY()).map(FUNCTION_BODY_END);

        visit(ParameterDeclContext.class, context -> !(context.parent.parent instanceof ReceiverContext)).mapRange(FUNCTION_PARAMETER);
        visit(ParameterDeclContext.class, context -> (context.parent.parent instanceof ReceiverContext)).mapRange(RECEIVER);
    }

    private void controlFlowRules() {
        visit(IfStmtContext.class).map(IF_STATEMENT);
        visit(IfStmtContext.class).delegateTerminal(context -> context.block(0).L_CURLY()).map(IF_BLOCK_BEGIN);
        visit(IfStmtContext.class).delegateTerminal(context -> context.block(0).R_CURLY()).map(IF_BLOCK_END);
        //TODO no else token?
        visit(IfStmtContext.class, context -> context.ELSE() != null).delegateTerminal(context -> context.block(1).L_CURLY()).map(ELSE_BLOCK_BEGIN);
        visit(IfStmtContext.class, context -> context.ELSE() != null).delegateTerminal(context -> context.block(1).L_CURLY()).map(ELSE_BLOCK_END);

        visit(ForStmtContext.class).map(FOR_STATEMENT);
        visit(ForStmtContext.class).delegateTerminal(context -> context.block().L_CURLY()).map(FOR_BLOCK_BEGIN);
        visit(ForStmtContext.class).delegateTerminal(context -> context.block().R_CURLY()).map(FOR_BLOCK_END);

        visit(SwitchStmtContext.class).map(SWITCH_STATEMENT);
        visit(ExprSwitchStmtContext.class).delegateTerminal(ExprSwitchStmtContext::L_CURLY).map(SWITCH_BLOCK_BEGIN);
        visit(TypeSwitchStmtContext.class).delegateTerminal(TypeSwitchStmtContext::L_CURLY).map(SWITCH_BLOCK_BEGIN);
        visit(ExprSwitchStmtContext.class).delegateTerminalExit(ExprSwitchStmtContext::R_CURLY).map(SWITCH_BLOCK_END);
        visit(TypeSwitchStmtContext.class).delegateTerminalExit(TypeSwitchStmtContext::R_CURLY).map(SWITCH_BLOCK_END);

        visit(ExprCaseClauseContext.class).map(SWITCH_CASE);
        visit(ExprCaseClauseContext.class).delegateContext(ExprCaseClauseContext::statementList).map(CASE_BLOCK_BEGIN, CASE_BLOCK_END);
        visit(TypeCaseClauseContext.class).map(SWITCH_CASE);
        visit(TypeCaseClauseContext.class).delegateContext(TypeCaseClauseContext::statementList).map(CASE_BLOCK_BEGIN, CASE_BLOCK_END);

        visit(SelectStmtContext.class).map(SELECT_STATEMENT);
        visit(SelectStmtContext.class).delegateTerminal(SelectStmtContext::L_CURLY).map(SELECT_BLOCK_BEGIN);
        visit(SelectStmtContext.class).delegateTerminal(SelectStmtContext::R_CURLY).map(SELECT_BLOCK_END);

        visit(CommCaseContext.class).map(SWITCH_CASE);
        visit(CommClauseContext.class).delegateContext(CommClauseContext::statementList).map(CASE_BLOCK_BEGIN, CASE_BLOCK_END);
    }

    private void statements() {
        visit(VarDeclContext.class).mapRange(VARIABLE_DECLARATION);
        visit(ConstSpecContext.class).map(VARIABLE_DECLARATION);

        visit(FunctionLitContext.class).map(FUNCTION_LITERAL);
        visit(FunctionLitContext.class).delegateContext(FunctionLitContext::block).map(FUNCTION_BODY_BEGIN, FUNCTION_BODY_END);

        visit(AssignmentContext.class).mapRange(ASSIGNMENT);

        visit(ShortVarDeclContext.class).map(VARIABLE_DECLARATION);
        visit(ShortVarDeclContext.class).map(ASSIGNMENT);

        visit(ExpressionContext.class, context -> hasAncestor(context, ArgumentsContext.class)).map(ARGUMENT);

        visit(StatementContext.class).map(STATEMENT_BLOCK_BEGIN, STATEMENT_BLOCK_END);
    }

    private void objectCreation() {
        visit(KeyedElementContext.class, context -> hasAncestor(context, ArrayTypeContext.class) &&
                hasAncestor(context, CompositeLitContext.class)).mapRange(ARRAY_ELEMENT);
        visit(KeyedElementContext.class, context -> hasAncestor(context, StructTypeContext.class)).mapRange(MEMBER_DECLARATION);
        visit(KeyedElementContext.class, context -> hasAncestor(context, MapTypeContext.class) &&
                hasAncestor(context, CompositeLitContext.class)).mapRange(MAP_ELEMENT);
        visit(KeyedElementContext.class, context -> hasAncestor(context, SliceTypeContext.class) &&
                hasAncestor(context, CompositeLitContext.class)).mapRange(SLICE_ELEMENT);
        visit(KeyedElementContext.class, context -> hasAncestor(context, TypeNameContext.class) &&
                hasAncestor(context, CompositeLitContext.class)).mapRange(NAMED_TYPE_ELEMENT);

        visit(ArrayTypeContext.class, context -> hasAncestor(context, CompositeLitContext.class)).map(ARRAY_CONSTRUCTOR);
        visit(ArrayTypeContext.class, context -> hasAncestor(context, CompositeLitContext.class)).delegateTerminal(ArrayTypeContext::L_BRACKET).map(ARRAY_BODY_BEGIN);
        visit(ArrayTypeContext.class, context -> hasAncestor(context, CompositeLitContext.class)).delegateTerminalExit(ArrayTypeContext::R_BRACKET).map(ARRAY_BODY_END);

        visit(SliceTypeContext.class, context -> hasAncestor(context, CompositeLitContext.class)).map(SLICE_CONSTRUCTOR);
        visit(SliceTypeContext.class, context -> hasAncestor(context, CompositeLitContext.class)).delegateTerminal(SliceTypeContext::L_BRACKET).map(SLICE_BODY_BEGIN);
        visit(SliceTypeContext.class, context -> hasAncestor(context, CompositeLitContext.class)).delegateTerminalExit(SliceTypeContext::R_BRACKET).map(SLICE_BODY_END);

        visit(MapTypeContext.class, context -> hasAncestor(context, CompositeLitContext.class)).map(MAP_CONSTRUCTOR);
        visit(MapTypeContext.class, context -> hasAncestor(context, CompositeLitContext.class)).delegateTerminal(MapTypeContext::L_BRACKET).map(MAP_BODY_BEGIN);
        visit(MapTypeContext.class, context -> hasAncestor(context, CompositeLitContext.class)).delegateTerminalExit(MapTypeContext::R_BRACKET).map(MAP_BODY_END);

        visit(TypeNameContext.class, context -> hasAncestor(context, CompositeLitContext.class)).map(NAMED_TYPE_CONSTRUCTOR);
        visit(TypeNameContext.class, context -> hasAncestor(context, CompositeLitContext.class)).delegateContext(context -> Objects.requireNonNull(getAncestor(context, CompositeLitContext.class)).literalValue()).map(NAMED_TYPE_BODY_BEGIN, NAMED_TYPE_BODY_END);
        visit(TypeNameContext.class, context -> hasAncestor(context, InterfaceTypeContext.class)).mapRange(TYPE_CONSTRAINT);

        visit(TypeAssertionContext.class).mapRange(TYPE_ASSERTION);
        visit(MethodSpecContext.class).mapRange(INTERFACE_METHOD);
    }

    private void controlFlowKeywords() {
        visit(ReturnStmtContext.class).mapRange(RETURN);
        visit(BreakStmtContext.class).mapRange(BREAK);
        visit(ContinueStmtContext.class).mapRange(CONTINUE);
        visit(FallthroughStmtContext.class).mapRange(FALLTHROUGH);
        visit(GotoStmtContext.class).mapRange(GOTO);
        visit(GotoStmtContext.class).mapRange(GO);
        visit(DeferStmtContext.class).mapRange(DEFER);
        visit(SendStmtContext.class).mapRange(SEND_STATEMENT);
        visit(RecvStmtContext.class).mapRange(RECEIVE_STATEMENT);
    }
}
