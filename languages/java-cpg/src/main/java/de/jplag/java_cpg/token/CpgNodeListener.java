package de.jplag.java_cpg.token;

import static de.jplag.SharedTokenType.FILE_END;
import static de.jplag.java_cpg.token.CpgTokenType.*;

import java.io.File;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Objects;

import de.fraunhofer.aisec.cpg.graph.Name;
import de.fraunhofer.aisec.cpg.graph.Node;
import de.fraunhofer.aisec.cpg.graph.declarations.*;
import de.fraunhofer.aisec.cpg.graph.statements.*;
import de.fraunhofer.aisec.cpg.graph.statements.expressions.*;
import de.jplag.Token;
import de.jplag.TokenType;

/**
 * This class specifies for which {@link Node}s a {@link CpgToken} shall be created.
 */
public class CpgNodeListener extends ACpgNodeListener {

    private final CpgTokenConsumer tokenConsumer;
    private final LinkedList<TokenType> openBlocks;
    private final LinkedList<BlockTokens> expectedBlocks;

    public CpgNodeListener(CpgTokenConsumer consumer) {
        this.tokenConsumer = consumer;
        this.expectedBlocks = new LinkedList<>();
        this.openBlocks = new LinkedList<>();
    }

    public static Iterator<Token> tokenIterator(Iterator<Node> nodes) {
        return new Iterator<>() {

            Token next;
            final CpgTokenConsumer consumer = new CpgTokenConsumer() {
                @Override
                public void addToken(TokenType type, File file, int startLine, int startColumn, int length, Name name) {
                    next = new CpgToken(type, file, startLine, startColumn, length, name);
                }
            };

            final CpgNodeListener listener = new CpgNodeListener(consumer);

            @Override
            public boolean hasNext() {
                while (Objects.isNull(next) && nodes.hasNext()) {
                    listener.visit(nodes.next());
                }
                return !Objects.isNull(next);
            }

            @Override
            public Token next() {
                if (hasNext()) {
                    Token saveNext = next;
                    next = null;
                    return saveNext;
                }
                return null;
            }
        };
    }

    @Override
    public void exit(TranslationUnitDeclaration translationUnitDeclaration) {
        tokenConsumer.addToken(FILE_END, new File(translationUnitDeclaration.getName().toString()), -1, -1, -1, translationUnitDeclaration.getName());
    }

    @Override
    public void exit(ConstructorDeclaration constructorDeclaration) {
    }

    @Override
    public void exit(Declaration declaration) {

    }

    @Override
    public void exit(DeclarationSequence declarationsequence) {

    }

    @Override
    public void exit(EnumConstantDeclaration enumconstantDeclaration) {

    }

    @Override
    public void exit(EnumDeclaration enumDeclaration) {
        tokenConsumer.addToken(ENUM_DECL_END, enumDeclaration, true);
    }

    @Override
    public void exit(FieldDeclaration fieldDeclaration) {

    }

    @Override
    public void exit(FunctionDeclaration functionDeclaration) {

    }

    @Override
    public void exit(FunctionTemplateDeclaration functiontemplateDeclaration) {

    }

    @Override
    public void exit(IncludeDeclaration includeDeclaration) {

    }

    @Override
    public void exit(RecordDeclaration recordDeclaration) {
        tokenConsumer.addToken(RECORD_DECL_END, recordDeclaration, true);
    }

    @Override
    public void exit(TupleDeclaration tupleDeclaration) {

    }

    @Override
    public void exit(BreakStatement breakStatement) {
    }

    @Override
    public void exit(CaseStatement caseStatement) {
    }

    @Override
    public void exit(CatchClause catchclause) {
        tokenConsumer.addToken(CATCH_CLAUSE_END, catchclause, true);
    }

    @Override
    public void exit(DeclarationStatement declarationStatement) {

    }

    @Override
    public void exit(DefaultStatement defaultStatement) {

    }

    @Override
    public void exit(DoStatement doStatement) {
        tokenConsumer.addToken(DO_WHILE_BLOCK_END, doStatement, true);
    }

    @Override
    public void exit(SynchronizedStatement synchronizedStatement) {

    }

    @Override
    public void exit(Block block) {
        TokenType blockEndToken = openBlocks.pop();
        if (blockEndToken == BLOCK_END)
            return;

        tokenConsumer.addToken(blockEndToken, block, true);
    }

    private void expect(CpgTokenType opening, CpgTokenType closing) {
        expectedBlocks.addFirst(new BlockTokens(opening, closing));
    }

    @Override
    public void visit(ConstructorDeclaration constructorDeclaration) {
        // Constructor may be implicit standard constructor
        tokenConsumer.addToken(METHOD_DECL_BEGIN, constructorDeclaration, false);
        expect(METHOD_BODY_BEGIN, METHOD_BODY_END);
    }

    @Override
    public void visit(Declaration declaration) {

    }

    @Override
    public void visit(DeclarationSequence declarationsequence) {

    }

    @Override
    public void visit(EnumConstantDeclaration enumConstantDeclaration) {
        tokenConsumer.addToken(ENUM_ELEMENT, enumConstantDeclaration, false);
    }

    @Override
    public void visit(EnumDeclaration enumDeclaration) {
        tokenConsumer.addToken(ENUM_DECL_BEGIN, enumDeclaration, false);
    }

    @Override
    public void visit(FieldDeclaration fieldDeclaration) {
        tokenConsumer.addToken(FIELD_DECL, fieldDeclaration, false);
    }

    @Override
    public void visit(IncludeDeclaration includeDeclaration) {

    }

    @Override
    public void visit(MethodDeclaration methodDeclaration) {
        tokenConsumer.addToken(METHOD_DECL_BEGIN, methodDeclaration, false);
        expect(METHOD_BODY_BEGIN, METHOD_BODY_END);
    }

    @Override
    public void visit(NewArrayExpression newArrayExpression) {
        tokenConsumer.addToken(NEW_ARRAY, newArrayExpression, false);
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
    public void visit(TupleDeclaration tupleDeclaration) {

    }

    @Override
    public void visit(VariableDeclaration variableDeclaration) {
        tokenConsumer.addToken(VARIABLE_DECL, variableDeclaration, false);
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
    public void visit(CaseStatement caseStatement) {
        tokenConsumer.addToken(CASE_STATEMENT, caseStatement, false);
    }

    @Override
    public void visit(CatchClause catchclause) {
        tokenConsumer.addToken(CATCH_CLAUSE_BEGIN, catchclause, false);
    }

    @Override
    public void visit(ContinueStatement continueStatement) {
        tokenConsumer.addToken(CONTINUE, continueStatement, false);
    }

    @Override
    public void visit(DeclarationStatement declarationStatement) {

    }

    @Override
    public void visit(DefaultStatement defaultStatement) {
        tokenConsumer.addToken(DEFAULT_STATEMENT, defaultStatement, false);
    }

    @Override
    public void visit(DoStatement doStatement) {
        tokenConsumer.addToken(DO_WHILE_STATEMENT, doStatement, false);
        expect(DO_WHILE_BLOCK_START, DO_WHILE_BLOCK_END);
    }

    @Override
    public void visit(ForEachStatement forEachStatement) {
        tokenConsumer.addToken(FOR_STATEMENT, forEachStatement, false);
        expect(FOR_STATEMENT, FOR_BLOCK_END);
    }

    @Override
    public void visit(ForStatement forStatement) {
        tokenConsumer.addToken(FOR_STATEMENT, forStatement, false);
        expect(FOR_BLOCK_BEGIN, FOR_BLOCK_END);
    }

    @Override
    public void visit(GotoStatement gotoStatement) {
        tokenConsumer.addToken(GOTO_STATEMENT, gotoStatement, false);
    }

    @Override
    public void visit(IfStatement ifStatement) {
        tokenConsumer.addToken(IF_STATEMENT, ifStatement, false);

        Statement elseStatement = ifStatement.getElseStatement();
        if (!Objects.isNull(elseStatement) && elseStatement instanceof Block) {
            expect(ELSE_BLOCK_BEGIN, ELSE_BLOCK_END);
        }

        if (ifStatement.getThenStatement() instanceof Block) {
            expect(IF_BLOCK_BEGIN, IF_BLOCK_END);
        }

    }

    @Override
    public void visit(ReturnStatement returnStatement) {
        if (Objects.isNull(returnStatement.getLocation())) {
            // implicit return without return value
            return;
        }
        tokenConsumer.addToken(RETURN, returnStatement, false);
    }

    @Override
    public void visit(SwitchStatement switchStatement) {
        tokenConsumer.addToken(SWITCH_STATEMENT, switchStatement, false);
        expect(SWITCH_BLOCK_START, SWITCH_BLOCK_END);
    }

    @Override
    public void visit(SynchronizedStatement synchronizedStatement) {
        tokenConsumer.addToken(SYNCHRONIZED_STATEMENT, synchronizedStatement, false);
        expect(SYNCHRONIZED_BLOCK_START, SYNCHRONIZED_BLOCK_END);
    }

    @Override
    public void visit(TryStatement tryStatement) {
        tokenConsumer.addToken(TRY_STATEMENT, tryStatement, false);
        expect(TRY_BLOCK_START, TRY_BLOCK_END);
    }

    @Override
    void visit(UnaryOperator unaryoperator) {

        String operatorCode = unaryoperator.getOperatorCode();
        if (Objects.isNull(operatorCode))
            return;
        if (Objects.equals(operatorCode, "throw")) {
            tokenConsumer.addToken(THROW, unaryoperator, false);
        }

        super.visit(unaryoperator);
    }

    @Override
    public void visit(WhileStatement whileStatement) {
        tokenConsumer.addToken(WHILE_STATEMENT, whileStatement, false);
        expect(WHILE_BLOCK_START, WHILE_BLOCK_END);
    }

    @Override
    public void visit(AssignExpression assignExpression) {
        tokenConsumer.addToken(ASSIGNMENT, assignExpression, false);
    }

    @Override
    public void visit(Block block) {
        if (expectedBlocks.isEmpty()) {
            // Do not add BLOCK_START and BLOCK_END, otherwise that is a vulnerability
            openBlocks.addFirst(BLOCK_END);
        } else {
            BlockTokens blockTokens = expectedBlocks.pop();
            tokenConsumer.addToken(blockTokens.opening, block, false);
            openBlocks.addFirst(blockTokens.closing);
        }
    }

    @Override
    public void visit(CallExpression callExpression) {
        tokenConsumer.addToken(METHOD_CALL, callExpression, false);
    }

    @Override
    public void visit(ConstructExpression constructorCallExpression) {
        tokenConsumer.addToken(CONSTRUCTOR_CALL, constructorCallExpression, false);
    }

    @Override
    public void visit(MemberCallExpression memberCallExpression) {
        tokenConsumer.addToken(METHOD_CALL, memberCallExpression, false);
    }

    public void visit(Node node) {
        super.visit(node);
    }

    private record BlockTokens(CpgTokenType opening, CpgTokenType closing) {
    }

}
