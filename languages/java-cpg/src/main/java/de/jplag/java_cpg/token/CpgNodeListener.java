package de.jplag.java_cpg.token;

import static de.jplag.SharedTokenType.FILE_END;
import static de.jplag.java_cpg.token.CpgTokenType.ASSERT_STATEMENT;
import static de.jplag.java_cpg.token.CpgTokenType.ASSIGNMENT;
import static de.jplag.java_cpg.token.CpgTokenType.BLOCK_END;
import static de.jplag.java_cpg.token.CpgTokenType.BREAK;
import static de.jplag.java_cpg.token.CpgTokenType.CASE_STATEMENT;
import static de.jplag.java_cpg.token.CpgTokenType.CATCH_CLAUSE_BEGIN;
import static de.jplag.java_cpg.token.CpgTokenType.CATCH_CLAUSE_END;
import static de.jplag.java_cpg.token.CpgTokenType.CONSTRUCTOR_CALL;
import static de.jplag.java_cpg.token.CpgTokenType.CONTINUE;
import static de.jplag.java_cpg.token.CpgTokenType.DEFAULT_STATEMENT;
import static de.jplag.java_cpg.token.CpgTokenType.DO_WHILE_BLOCK_END;
import static de.jplag.java_cpg.token.CpgTokenType.DO_WHILE_BLOCK_START;
import static de.jplag.java_cpg.token.CpgTokenType.DO_WHILE_STATEMENT;
import static de.jplag.java_cpg.token.CpgTokenType.ELSE_BLOCK_BEGIN;
import static de.jplag.java_cpg.token.CpgTokenType.ELSE_BLOCK_END;
import static de.jplag.java_cpg.token.CpgTokenType.ENUM_DECL_BEGIN;
import static de.jplag.java_cpg.token.CpgTokenType.ENUM_DECL_END;
import static de.jplag.java_cpg.token.CpgTokenType.ENUM_ELEMENT;
import static de.jplag.java_cpg.token.CpgTokenType.FIELD_DECL;
import static de.jplag.java_cpg.token.CpgTokenType.FOR_BLOCK_BEGIN;
import static de.jplag.java_cpg.token.CpgTokenType.FOR_BLOCK_END;
import static de.jplag.java_cpg.token.CpgTokenType.FOR_STATEMENT;
import static de.jplag.java_cpg.token.CpgTokenType.GOTO_STATEMENT;
import static de.jplag.java_cpg.token.CpgTokenType.IF_BLOCK_BEGIN;
import static de.jplag.java_cpg.token.CpgTokenType.IF_BLOCK_END;
import static de.jplag.java_cpg.token.CpgTokenType.IF_STATEMENT;
import static de.jplag.java_cpg.token.CpgTokenType.INCLUDE;
import static de.jplag.java_cpg.token.CpgTokenType.LAMBDA_EXPRESSION;
import static de.jplag.java_cpg.token.CpgTokenType.METHOD_BODY_BEGIN;
import static de.jplag.java_cpg.token.CpgTokenType.METHOD_BODY_END;
import static de.jplag.java_cpg.token.CpgTokenType.METHOD_CALL;
import static de.jplag.java_cpg.token.CpgTokenType.METHOD_DECL_BEGIN;
import static de.jplag.java_cpg.token.CpgTokenType.METHOD_PARAM;
import static de.jplag.java_cpg.token.CpgTokenType.NEW_ARRAY;
import static de.jplag.java_cpg.token.CpgTokenType.RECORD_DECL_BEGIN;
import static de.jplag.java_cpg.token.CpgTokenType.RECORD_DECL_END;
import static de.jplag.java_cpg.token.CpgTokenType.RETURN;
import static de.jplag.java_cpg.token.CpgTokenType.SWITCH_BLOCK_END;
import static de.jplag.java_cpg.token.CpgTokenType.SWITCH_BLOCK_START;
import static de.jplag.java_cpg.token.CpgTokenType.SWITCH_STATEMENT;
import static de.jplag.java_cpg.token.CpgTokenType.SYNCHRONIZED_BLOCK_END;
import static de.jplag.java_cpg.token.CpgTokenType.SYNCHRONIZED_BLOCK_START;
import static de.jplag.java_cpg.token.CpgTokenType.SYNCHRONIZED_STATEMENT;
import static de.jplag.java_cpg.token.CpgTokenType.THROW;
import static de.jplag.java_cpg.token.CpgTokenType.TRY_BLOCK_END;
import static de.jplag.java_cpg.token.CpgTokenType.TRY_BLOCK_START;
import static de.jplag.java_cpg.token.CpgTokenType.TRY_STATEMENT;
import static de.jplag.java_cpg.token.CpgTokenType.VARIABLE_DECL;
import static de.jplag.java_cpg.token.CpgTokenType.WHILE_BLOCK_END;
import static de.jplag.java_cpg.token.CpgTokenType.WHILE_BLOCK_START;
import static de.jplag.java_cpg.token.CpgTokenType.WHILE_STATEMENT;

import java.io.File;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Objects;

import de.fraunhofer.aisec.cpg.graph.Name;
import de.fraunhofer.aisec.cpg.graph.Node;
import de.fraunhofer.aisec.cpg.graph.declarations.ConstructorDeclaration;
import de.fraunhofer.aisec.cpg.graph.declarations.EnumConstantDeclaration;
import de.fraunhofer.aisec.cpg.graph.declarations.EnumDeclaration;
import de.fraunhofer.aisec.cpg.graph.declarations.FieldDeclaration;
import de.fraunhofer.aisec.cpg.graph.declarations.IncludeDeclaration;
import de.fraunhofer.aisec.cpg.graph.declarations.MethodDeclaration;
import de.fraunhofer.aisec.cpg.graph.declarations.ParameterDeclaration;
import de.fraunhofer.aisec.cpg.graph.declarations.RecordDeclaration;
import de.fraunhofer.aisec.cpg.graph.declarations.TranslationUnitDeclaration;
import de.fraunhofer.aisec.cpg.graph.declarations.VariableDeclaration;
import de.fraunhofer.aisec.cpg.graph.statements.AssertStatement;
import de.fraunhofer.aisec.cpg.graph.statements.BreakStatement;
import de.fraunhofer.aisec.cpg.graph.statements.CaseStatement;
import de.fraunhofer.aisec.cpg.graph.statements.CatchClause;
import de.fraunhofer.aisec.cpg.graph.statements.ContinueStatement;
import de.fraunhofer.aisec.cpg.graph.statements.DefaultStatement;
import de.fraunhofer.aisec.cpg.graph.statements.DoStatement;
import de.fraunhofer.aisec.cpg.graph.statements.ForEachStatement;
import de.fraunhofer.aisec.cpg.graph.statements.ForStatement;
import de.fraunhofer.aisec.cpg.graph.statements.GotoStatement;
import de.fraunhofer.aisec.cpg.graph.statements.IfStatement;
import de.fraunhofer.aisec.cpg.graph.statements.ReturnStatement;
import de.fraunhofer.aisec.cpg.graph.statements.Statement;
import de.fraunhofer.aisec.cpg.graph.statements.SwitchStatement;
import de.fraunhofer.aisec.cpg.graph.statements.SynchronizedStatement;
import de.fraunhofer.aisec.cpg.graph.statements.TryStatement;
import de.fraunhofer.aisec.cpg.graph.statements.WhileStatement;
import de.fraunhofer.aisec.cpg.graph.statements.expressions.AssignExpression;
import de.fraunhofer.aisec.cpg.graph.statements.expressions.Block;
import de.fraunhofer.aisec.cpg.graph.statements.expressions.CallExpression;
import de.fraunhofer.aisec.cpg.graph.statements.expressions.ConstructExpression;
import de.fraunhofer.aisec.cpg.graph.statements.expressions.LambdaExpression;
import de.fraunhofer.aisec.cpg.graph.statements.expressions.MemberCallExpression;
import de.fraunhofer.aisec.cpg.graph.statements.expressions.NewArrayExpression;
import de.fraunhofer.aisec.cpg.graph.statements.expressions.UnaryOperator;
import de.fraunhofer.aisec.cpg.sarif.Region;
import de.jplag.Token;
import de.jplag.TokenType;

/**
 * This class specifies for which {@link Node}s a {@link CpgToken} shall be created.
 */
public class CpgNodeListener extends ACpgNodeListener {

    private final CpgTokenConsumer tokenConsumer;
    private final LinkedList<TokenType> openBlocks;
    private final LinkedList<BlockTokens> expectedBlocks;

    /**
     * Creates a new {@link CpgNodeListener}.
     * @param consumer the {@link CpgTokenConsumer} that receives the tokens.
     */
    public CpgNodeListener(CpgTokenConsumer consumer) {
        this.tokenConsumer = consumer;
        this.expectedBlocks = new LinkedList<>();
        this.openBlocks = new LinkedList<>();
    }

    /**
     * This is used to iterate over {@link Token} list, comparing them one {@link Token} at a time.
     * @param nodes an iterator for {@link Node}s
     * @return an iterator for {@link Token}s
     */
    public static Iterator<Token> tokenIterator(Iterator<Node> nodes) {
        return new Iterator<>() {

            Token next;
            final CpgTokenConsumer consumer = new CpgTokenConsumer() {
                @Override
                public void addToken(TokenType type, File file, Region region, int length, Name name) {
                    next = new CpgToken(type, file, region, length, name);
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
        tokenConsumer.addToken(FILE_END, new File(translationUnitDeclaration.getName().toString()), new Region(-1, -1, -1, -1), -1,
                translationUnitDeclaration.getName());
    }

    @Override
    public void exit(EnumDeclaration enumDeclaration) {
        tokenConsumer.addToken(ENUM_DECL_END, enumDeclaration, true);
    }

    @Override
    public void exit(RecordDeclaration recordDeclaration) {
        tokenConsumer.addToken(RECORD_DECL_END, recordDeclaration, true);
    }

    @Override
    public void exit(CatchClause catchclause) {
        tokenConsumer.addToken(CATCH_CLAUSE_END, catchclause, true);
    }

    @Override
    public void exit(DoStatement doStatement) {
        tokenConsumer.addToken(DO_WHILE_BLOCK_END, doStatement, true);
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
        tokenConsumer.addToken(INCLUDE, includeDeclaration, false);
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
    public void visit(LambdaExpression lambdaExpression) {
        tokenConsumer.addToken(LAMBDA_EXPRESSION, lambdaExpression, false);
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

    private record BlockTokens(CpgTokenType opening, CpgTokenType closing) {
    }

}
