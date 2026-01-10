package de.jplag.java_cpg.token;

import static de.jplag.SharedTokenType.FILE_END;
import static de.jplag.java_cpg.token.cpg.CpgTokenType.*;

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
import de.jplag.java_cpg.NodeRegistry;
import de.jplag.java_cpg.token.cpg.CpgToken;
import de.jplag.java_cpg.token.cpg.CpgTokenConsumer;
import de.jplag.java_cpg.token.cpg.CpgTokenType;
import de.jplag.java_cpg.token.semantic.SemanticCpgTokenType;
import de.jplag.java_cpg.token.semantic.SemanticVector;

/**
 * This class specifies for which {@link Node}s a {@link CpgToken} shall be created.
 */
public class TokenizationCpgNodeListener extends ACpgNodeListener {

    private final CpgTokenConsumer tokenConsumer;
    private final LinkedList<TokenType> openBlocks;
    private final LinkedList<BlockTokens> expectedBlocks;

    /**
     * Creates a new {@link TokenizationCpgNodeListener}.
     * @param consumer the {@link CpgTokenConsumer} that receives the tokens.
     */
    public TokenizationCpgNodeListener(CpgTokenConsumer consumer) {
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
                public void addToken(TokenType type, File file, int startLine, int startColumn, int length, Name name) {
                    next = new CpgToken(type, file, startLine, startColumn, length, name);
                }
            };

            final TokenizationCpgNodeListener listener = new TokenizationCpgNodeListener(consumer);

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
        tokenConsumer.addToken(new SemanticCpgTokenType(FILE_END, getSemanticVector(translationUnitDeclaration)),
                new File(translationUnitDeclaration.getName().toString()), -1, -1, -1, translationUnitDeclaration.getName());
    }

    @Override
    public void exit(EnumDeclaration enumDeclaration) {
        tokenConsumer.addToken(new SemanticCpgTokenType(ENUM_DECL_END, getSemanticVector(enumDeclaration)), enumDeclaration, true);
    }

    @Override
    public void exit(RecordDeclaration recordDeclaration) {
        tokenConsumer.addToken(new SemanticCpgTokenType(RECORD_DECL_END, getSemanticVector(recordDeclaration)), recordDeclaration, true);
    }

    @Override
    public void exit(CatchClause catchclause) {
        tokenConsumer.addToken(new SemanticCpgTokenType(CATCH_CLAUSE_END, getSemanticVector(catchclause)), catchclause, true);
    }

    @Override
    public void exit(DoStatement doStatement) {
        tokenConsumer.addToken(new SemanticCpgTokenType(DO_WHILE_BLOCK_END, getSemanticVector(doStatement)), doStatement, true);
    }

    @Override
    public void exit(Block block) {
        TokenType blockEndToken = openBlocks.pop();
        if (blockEndToken == BLOCK_END)
            return;

        tokenConsumer.addToken(new SemanticCpgTokenType(BLOCK_END, getSemanticVector(block)), block, true);
    }

    private void expect(CpgTokenType opening, CpgTokenType closing) {
        expectedBlocks.addFirst(new BlockTokens(opening, closing));
    }

    @Override
    public void visit(ConstructorDeclaration constructorDeclaration) {
        // Constructor may be implicit standard constructor
        tokenConsumer.addToken(new SemanticCpgTokenType(METHOD_BODY_BEGIN, getSemanticVector(constructorDeclaration)), constructorDeclaration, false);
        expect(METHOD_BODY_BEGIN, METHOD_BODY_END);
    }

    @Override
    public void visit(EnumConstantDeclaration enumConstantDeclaration) {
        tokenConsumer.addToken(new SemanticCpgTokenType(ENUM_ELEMENT, getSemanticVector(enumConstantDeclaration)), enumConstantDeclaration, false);
    }

    @Override
    public void visit(EnumDeclaration enumDeclaration) {
        tokenConsumer.addToken(new SemanticCpgTokenType(ENUM_DECL_BEGIN, getSemanticVector(enumDeclaration)), enumDeclaration, false);
    }

    @Override
    public void visit(FieldDeclaration fieldDeclaration) {
        tokenConsumer.addToken(new SemanticCpgTokenType(FIELD_DECL, getSemanticVector(fieldDeclaration)), fieldDeclaration, false);
    }

    @Override
    public void visit(IncludeDeclaration includeDeclaration) {
        tokenConsumer.addToken(new SemanticCpgTokenType(INCLUDE, getSemanticVector(includeDeclaration)), includeDeclaration, false);
    }

    @Override
    public void visit(MethodDeclaration methodDeclaration) {
        tokenConsumer.addToken(new SemanticCpgTokenType(METHOD_DECL_BEGIN, getSemanticVector(methodDeclaration)), methodDeclaration, false);
        expect(METHOD_BODY_BEGIN, METHOD_BODY_END);
    }

    @Override
    public void visit(NewArrayExpression newArrayExpression) {
        tokenConsumer.addToken(new SemanticCpgTokenType(NEW_ARRAY, getSemanticVector(newArrayExpression)), newArrayExpression, false);
    }

    @Override
    public void visit(ParameterDeclaration parameterDeclaration) {
        tokenConsumer.addToken(new SemanticCpgTokenType(METHOD_PARAM, getSemanticVector(parameterDeclaration)), parameterDeclaration, false);
    }

    @Override
    public void visit(RecordDeclaration recordDeclaration) {
        tokenConsumer.addToken(new SemanticCpgTokenType(RECORD_DECL_BEGIN, getSemanticVector(recordDeclaration)), recordDeclaration, false);
    }

    @Override
    public void visit(VariableDeclaration variableDeclaration) {
        tokenConsumer.addToken(new SemanticCpgTokenType(VARIABLE_DECL, getSemanticVector(variableDeclaration)), variableDeclaration, false);
    }

    @Override
    public void visit(AssertStatement assertStatement) {
        tokenConsumer.addToken(new SemanticCpgTokenType(ASSERT_STATEMENT, getSemanticVector(assertStatement)), assertStatement, false);
    }

    @Override
    public void visit(BreakStatement breakStatement) {
        tokenConsumer.addToken(new SemanticCpgTokenType(BREAK, getSemanticVector(breakStatement)), breakStatement, false);
    }

    @Override
    public void visit(CaseStatement caseStatement) {
        tokenConsumer.addToken(new SemanticCpgTokenType(CASE_STATEMENT, getSemanticVector(caseStatement)), caseStatement, false);
    }

    @Override
    public void visit(CatchClause catchclause) {
        tokenConsumer.addToken(new SemanticCpgTokenType(CATCH_CLAUSE_BEGIN, getSemanticVector(catchclause)), catchclause, false);
    }

    @Override
    public void visit(ContinueStatement continueStatement) {
        tokenConsumer.addToken(new SemanticCpgTokenType(CONTINUE, getSemanticVector(continueStatement)), continueStatement, false);
    }

    @Override
    public void visit(DefaultStatement defaultStatement) {
        tokenConsumer.addToken(new SemanticCpgTokenType(DEFAULT_STATEMENT, getSemanticVector(defaultStatement)), defaultStatement, false);
    }

    @Override
    public void visit(DoStatement doStatement) {
        tokenConsumer.addToken(new SemanticCpgTokenType(DO_WHILE_STATEMENT, getSemanticVector(doStatement)), doStatement, false);
        expect(DO_WHILE_BLOCK_START, DO_WHILE_BLOCK_END);
    }

    @Override
    public void visit(ForEachStatement forEachStatement) {
        tokenConsumer.addToken(new SemanticCpgTokenType(FOR_STATEMENT, getSemanticVector(forEachStatement)), forEachStatement, false);
        expect(FOR_STATEMENT, FOR_BLOCK_END);
    }

    @Override
    public void visit(ForStatement forStatement) {
        tokenConsumer.addToken(new SemanticCpgTokenType(FOR_STATEMENT, getSemanticVector(forStatement)), forStatement, false);
        expect(FOR_BLOCK_BEGIN, FOR_BLOCK_END);
    }

    @Override
    public void visit(GotoStatement gotoStatement) {
        tokenConsumer.addToken(new SemanticCpgTokenType(GOTO_STATEMENT, getSemanticVector(gotoStatement)), gotoStatement, false);
    }

    @Override
    public void visit(IfStatement ifStatement) {
        tokenConsumer.addToken(new SemanticCpgTokenType(IF_STATEMENT, getSemanticVector(ifStatement)), ifStatement, false);

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
        tokenConsumer.addToken(new SemanticCpgTokenType(LAMBDA_EXPRESSION, getSemanticVector(lambdaExpression)), lambdaExpression, false);
    }

    @Override
    public void visit(ReturnStatement returnStatement) {
        if (Objects.isNull(returnStatement.getLocation())) {
            // implicit return without return value
            return;
        }
        tokenConsumer.addToken(new SemanticCpgTokenType(RETURN, getSemanticVector(returnStatement)), returnStatement, false);
    }

    @Override
    public void visit(SwitchStatement switchStatement) {
        tokenConsumer.addToken(new SemanticCpgTokenType(SWITCH_STATEMENT, getSemanticVector(switchStatement)), switchStatement, false);
        expect(SWITCH_BLOCK_START, SWITCH_BLOCK_END);
    }

    @Override
    public void visit(SynchronizedStatement synchronizedStatement) {
        tokenConsumer.addToken(new SemanticCpgTokenType(SYNCHRONIZED_STATEMENT, getSemanticVector(synchronizedStatement)), synchronizedStatement,
                false);
        expect(SYNCHRONIZED_BLOCK_START, SYNCHRONIZED_BLOCK_END);
    }

    @Override
    public void visit(TryStatement tryStatement) {
        tokenConsumer.addToken(new SemanticCpgTokenType(TRY_STATEMENT, getSemanticVector(tryStatement)), tryStatement, false);
        expect(TRY_BLOCK_START, TRY_BLOCK_END);
    }

    @Override
    public void visit(UnaryOperator unaryoperator) {

        String operatorCode = unaryoperator.getOperatorCode();
        if (Objects.isNull(operatorCode))
            return;
        if (Objects.equals(operatorCode, "throw")) {
            tokenConsumer.addToken(new SemanticCpgTokenType(THROW, getSemanticVector(unaryoperator)), unaryoperator, false);
        } else if (Objects.equals(operatorCode, "++") || Objects.equals(operatorCode, "--")) {
            tokenConsumer.addToken(new SemanticCpgTokenType(ASSIGNMENT, getSemanticVector(unaryoperator)), unaryoperator, false);
        }
        super.visit(unaryoperator);
    }

    @Override
    public void visit(WhileStatement whileStatement) {
        tokenConsumer.addToken(new SemanticCpgTokenType(WHILE_STATEMENT, getSemanticVector(whileStatement)), whileStatement, false);
        expect(WHILE_BLOCK_START, WHILE_BLOCK_END);
    }

    @Override
    public void visit(AssignExpression assignExpression) {
        tokenConsumer.addToken(new SemanticCpgTokenType(ASSIGNMENT, getSemanticVector(assignExpression)), assignExpression, false);
    }

    @Override
    public void visit(Block block) {
        if (expectedBlocks.isEmpty()) {
            // Do not add BLOCK_START and BLOCK_END, otherwise that is a vulnerability
            openBlocks.addFirst(BLOCK_END);
        } else {
            BlockTokens blockTokens = expectedBlocks.pop();
            tokenConsumer.addToken(new SemanticCpgTokenType(blockTokens.opening, getSemanticVector(block)), block, false);  // TODO semantic vector?
            openBlocks.addFirst(blockTokens.closing);
        }
    }

    @Override
    public void visit(CallExpression callExpression) {
        tokenConsumer.addToken(new SemanticCpgTokenType(METHOD_CALL, getSemanticVector(callExpression)), callExpression, false);
    }

    @Override
    public void visit(ConstructExpression constructorCallExpression) {
        tokenConsumer.addToken(new SemanticCpgTokenType(CONSTRUCTOR_CALL, getSemanticVector(constructorCallExpression)), constructorCallExpression,
                false);
    }

    @Override
    public void visit(MemberCallExpression memberCallExpression) {
        tokenConsumer.addToken(new SemanticCpgTokenType(METHOD_CALL, getSemanticVector(memberCallExpression)), memberCallExpression, false);
    }

    private record BlockTokens(CpgTokenType opening, CpgTokenType closing) {
    }

    private SemanticVector getSemanticVector(Node node) {
        SemanticVector vector = NodeRegistry.INSTANCE.getNodeData(node);
        if (vector == null) {
            throw new IllegalStateException("No Vector calculated for node " + node);
        }
        return vector;
    }

}
