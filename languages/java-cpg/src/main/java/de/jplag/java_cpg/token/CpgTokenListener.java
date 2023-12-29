package de.jplag.java_cpg.token;

import de.fraunhofer.aisec.cpg.graph.Node;
import de.fraunhofer.aisec.cpg.graph.declarations.*;
import de.fraunhofer.aisec.cpg.graph.statements.*;
import de.fraunhofer.aisec.cpg.graph.statements.expressions.*;
import de.jplag.TokenType;

import java.io.File;
import java.util.LinkedList;

import static de.jplag.SharedTokenType.FILE_END;
import static de.jplag.java_cpg.token.CpgTokenType.*;

/**
 * This class specifies for which {@link Node}s a {@link CpgToken} shall be created.
 */
public class CpgTokenListener extends ACpgTokenListener {

    private final CpgTokenConsumer tokenConsumer;
    private final LinkedList<TokenType> openBlocks;

    public CpgTokenListener(CpgTokenConsumer consumer) {
        this.tokenConsumer = consumer;
        this.openBlocks = new LinkedList<TokenType>();
    }

    @Override
    public void exit(TranslationUnitDeclaration translationUnitDeclaration) {
        tokenConsumer.addToken(FILE_END, new File(translationUnitDeclaration.getName().toString()), -1, -1, -1);
    }

    @Override
    public void visit(ConstructorDeclaration constructorDeclaration) {
        // Constructor may be implicit standard constructor
        openBlocks.addFirst(METHOD_BODY_END);
        tokenConsumer.addToken(METHOD_DECL_BEGIN, constructorDeclaration, false);
    }

    @Override
    public void exit(ConstructorDeclaration constructorDeclaration) {
    }

    @Override
    public void visit(Declaration declaration) {

    }

    @Override
    public void exit(Declaration declaration) {

    }

    @Override
    public void visit(DeclarationSequence declarationsequence) {

    }

    @Override
    public void exit(DeclarationSequence declarationsequence) {

    }

    @Override
    public void visit(EnumConstantDeclaration enumconstantDeclaration) {

    }

    @Override
    public void exit(EnumConstantDeclaration enumconstantDeclaration) {

    }

    @Override
    public void visit(EnumDeclaration enumDeclaration) {

    }

    @Override
    public void exit(EnumDeclaration enumDeclaration) {

    }

    @Override
    public void visit(FieldDeclaration fieldDeclaration) {
        tokenConsumer.addToken(FIELD_DECL, fieldDeclaration, false);
    }

    @Override
    public void exit(FieldDeclaration fieldDeclaration) {

    }

    @Override
    public void visit(FunctionDeclaration functionDeclaration) {

    }

    @Override
    public void exit(FunctionDeclaration functionDeclaration) {

    }

    @Override
    public void visit(FunctionTemplateDeclaration functiontemplateDeclaration) {

    }

    @Override
    public void exit(FunctionTemplateDeclaration functiontemplateDeclaration) {

    }

    @Override
    public void visit(IncludeDeclaration includeDeclaration) {

    }

    @Override
    public void exit(IncludeDeclaration includeDeclaration) {

    }

    @Override
    public void visit(MethodDeclaration methodDeclaration) {
        openBlocks.addFirst(DO_WHILE_BLOCK_END);
        tokenConsumer.addToken(METHOD_DECL_BEGIN, methodDeclaration, false);
    }

    @Override
    public void visit(ParameterDeclaration parameterDeclaration) {
        tokenConsumer.addToken(METHOD_PARAM, parameterDeclaration, false);
    }

    @Override
    public void visit(RecordDeclaration recordDeclaration) {
        tokenConsumer.addToken(RECORD_DECL_BEGIN, recordDeclaration, false);
    }

    @Override
    public void exit(RecordDeclaration recordDeclaration) {
        tokenConsumer.addToken(RECORD_DECL_END, recordDeclaration, true);
    }


    @Override
    public void visit(TupleDeclaration tupleDeclaration) {

    }

    @Override
    public void exit(TupleDeclaration tupleDeclaration) {

    }

    @Override
    public void visit(VariableDeclaration variableDeclaration) {
        tokenConsumer.addToken(VARIABLE_DECL, variableDeclaration, false);
    }

    @Override
    public void visit(ASMDeclarationStatement asmDeclarationStatement) {

    }

    @Override
    public void exit(ASMDeclarationStatement asmDeclarationStatement) {

    }

    @Override
    public void visit(AssertStatement assertStatement) {
        tokenConsumer.addToken(ASSERT_STATEMENT, assertStatement, false);
    }

    @Override
    public void visit(BreakStatement breakStatement) {
        tokenConsumer.addToken(BREAK, breakStatement, false);
    }

    @Override
    public void exit(BreakStatement breakStatement) {
    }

    @Override
    public void visit(CaseStatement caseStatement) {
        tokenConsumer.addToken(CASE_STATEMENT, caseStatement, false);
    }

    @Override
    public void exit(CaseStatement caseStatement) {
    }

    @Override
    public void visit(CatchClause catchclause) {
        tokenConsumer.addToken(CATCH_CLAUSE_BEGIN, catchclause, false);
    }

    @Override
    public void exit(CatchClause catchclause) {
        tokenConsumer.addToken(CATCH_CLAUSE_END, catchclause, true);
    }

    @Override
    public void visit(ContinueStatement continueStatement) {
        tokenConsumer.addToken(CONTINUE, continueStatement, false);
    }

    @Override
    public void visit(DeclarationStatement declarationStatement) {

    }

    @Override
    public void exit(DeclarationStatement declarationStatement) {

    }

    @Override
    public void visit(DefaultStatement defaultStatement) {

    }

    @Override
    public void exit(DefaultStatement defaultStatement) {

    }

    @Override
    public void visit(DoStatement doStatement) {
        openBlocks.addFirst(DO_WHILE_BLOCK_END);
        tokenConsumer.addToken(DO_WHILE_STATEMENT, doStatement, false);
    }

    @Override
    public void exit(DoStatement doStatement) {
        tokenConsumer.addToken(DO_WHILE_BLOCK_END, doStatement, true);
    }

    @Override
    public void visit(EmptyStatement emptyStatement) {

    }

    @Override
    public void exit(EmptyStatement emptyStatement) {

    }

    @Override
    public void visit(ForEachStatement foreachStatement) {
        openBlocks.addFirst(FOR_STATEMENT_END);
        tokenConsumer.addToken(FOR_STATEMENT_BEGIN, foreachStatement, false);
    }

    @Override
    public void visit(ForStatement forStatement) {
        openBlocks.addFirst(FOR_STATEMENT_END);
        tokenConsumer.addToken(FOR_STATEMENT_BEGIN, forStatement, false);
    }

    @Override
    public void visit(GotoStatement gotoStatement) {
        tokenConsumer.addToken(GOTO_STATEMENT, gotoStatement, false);
    }

    @Override
    public void visit(IfStatement ifStatement) {
        openBlocks.addFirst(IF_BLOCK_END);
        tokenConsumer.addToken(IF_STATEMENT, ifStatement, false);
    }


    @Override
    public void visit(ReturnStatement returnStatement) {
        tokenConsumer.addToken(RETURN, returnStatement, false);
    }

    @Override
    public void visit(SwitchStatement switchStatement) {
        openBlocks.addFirst(SWITCH_STATEMENT_END);
        tokenConsumer.addToken(SWITCH_STATEMENT, switchStatement, false);
    }

    @Override
    public void visit(SynchronizedStatement synchronizedStatement) {

    }

    @Override
    public void exit(SynchronizedStatement synchronizedStatement) {

    }

    @Override
    public void visit(TryStatement tryStatement) {
        openBlocks.addFirst(TRY_STATEMENT_END);
        tokenConsumer.addToken(TRY_STATEMENT_BEGIN, tryStatement, false);
    }

    @Override
    public void visit(WhileStatement whileStatement) {
        openBlocks.addFirst(WHILE_BLOCK_END);
        tokenConsumer.addToken(WHILE_BLOCK_START, whileStatement, false);
    }

    @Override
    public void visit(AssignExpression assignExpression) {
        tokenConsumer.addToken(ASSIGNMENT, assignExpression, false);
    }

    @Override
    public void visit(Block block) {

    }

    @Override
    public void exit(Block block) {
        tokenConsumer.addToken(openBlocks.pop(), block, true);
    }

    @Override
    public void visit(CallExpression callExpression) {
        tokenConsumer.addToken(METHOD_CALL, callExpression, false);
    }

    @Override
    public void visit(ConstructorCallExpression constructorCallExpression) {
        tokenConsumer.addToken(CONSTRUCTOR_CALL, constructorCallExpression, false);
    }

    @Override
    public void visit(MemberCallExpression membercallExpression) {
        tokenConsumer.addToken(METHOD_CALL, membercallExpression, false);
    }


    public void visit(Node node) {
        super.visit(node);
    }

}
