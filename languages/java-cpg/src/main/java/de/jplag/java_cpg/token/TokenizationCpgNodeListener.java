package de.jplag.java_cpg.token;

import static de.jplag.SharedTokenType.FILE_END;
import static de.jplag.java_cpg.token.cpg.CpgTokenType.ASSERT_STATEMENT;
import static de.jplag.java_cpg.token.cpg.CpgTokenType.ASSIGNMENT;
import static de.jplag.java_cpg.token.cpg.CpgTokenType.BLOCK_END;
import static de.jplag.java_cpg.token.cpg.CpgTokenType.BREAK;
import static de.jplag.java_cpg.token.cpg.CpgTokenType.CASE_STATEMENT;
import static de.jplag.java_cpg.token.cpg.CpgTokenType.CATCH_CLAUSE_BEGIN;
import static de.jplag.java_cpg.token.cpg.CpgTokenType.CATCH_CLAUSE_END;
import static de.jplag.java_cpg.token.cpg.CpgTokenType.CONSTRUCTOR_CALL;
import static de.jplag.java_cpg.token.cpg.CpgTokenType.CONTINUE;
import static de.jplag.java_cpg.token.cpg.CpgTokenType.DEFAULT_STATEMENT;
import static de.jplag.java_cpg.token.cpg.CpgTokenType.DO_WHILE_BLOCK_END;
import static de.jplag.java_cpg.token.cpg.CpgTokenType.DO_WHILE_BLOCK_START;
import static de.jplag.java_cpg.token.cpg.CpgTokenType.DO_WHILE_STATEMENT;
import static de.jplag.java_cpg.token.cpg.CpgTokenType.ELSE_BLOCK_BEGIN;
import static de.jplag.java_cpg.token.cpg.CpgTokenType.ELSE_BLOCK_END;
import static de.jplag.java_cpg.token.cpg.CpgTokenType.ENUM_DECL_BEGIN;
import static de.jplag.java_cpg.token.cpg.CpgTokenType.ENUM_DECL_END;
import static de.jplag.java_cpg.token.cpg.CpgTokenType.ENUM_ELEMENT;
import static de.jplag.java_cpg.token.cpg.CpgTokenType.FIELD_DECL;
import static de.jplag.java_cpg.token.cpg.CpgTokenType.FOR_BLOCK_BEGIN;
import static de.jplag.java_cpg.token.cpg.CpgTokenType.FOR_BLOCK_END;
import static de.jplag.java_cpg.token.cpg.CpgTokenType.FOR_STATEMENT;
import static de.jplag.java_cpg.token.cpg.CpgTokenType.GOTO_STATEMENT;
import static de.jplag.java_cpg.token.cpg.CpgTokenType.IF_BLOCK_BEGIN;
import static de.jplag.java_cpg.token.cpg.CpgTokenType.IF_BLOCK_END;
import static de.jplag.java_cpg.token.cpg.CpgTokenType.IF_STATEMENT;
import static de.jplag.java_cpg.token.cpg.CpgTokenType.INCLUDE;
import static de.jplag.java_cpg.token.cpg.CpgTokenType.LAMBDA_EXPRESSION;
import static de.jplag.java_cpg.token.cpg.CpgTokenType.METHOD_BODY_BEGIN;
import static de.jplag.java_cpg.token.cpg.CpgTokenType.METHOD_BODY_END;
import static de.jplag.java_cpg.token.cpg.CpgTokenType.METHOD_CALL;
import static de.jplag.java_cpg.token.cpg.CpgTokenType.METHOD_DECL_BEGIN;
import static de.jplag.java_cpg.token.cpg.CpgTokenType.METHOD_PARAM;
import static de.jplag.java_cpg.token.cpg.CpgTokenType.NEW_ARRAY;
import static de.jplag.java_cpg.token.cpg.CpgTokenType.RECORD_DECL_BEGIN;
import static de.jplag.java_cpg.token.cpg.CpgTokenType.RECORD_DECL_END;
import static de.jplag.java_cpg.token.cpg.CpgTokenType.RETURN;
import static de.jplag.java_cpg.token.cpg.CpgTokenType.SWITCH_BLOCK_END;
import static de.jplag.java_cpg.token.cpg.CpgTokenType.SWITCH_BLOCK_START;
import static de.jplag.java_cpg.token.cpg.CpgTokenType.SWITCH_STATEMENT;
import static de.jplag.java_cpg.token.cpg.CpgTokenType.SYNCHRONIZED_BLOCK_END;
import static de.jplag.java_cpg.token.cpg.CpgTokenType.SYNCHRONIZED_BLOCK_START;
import static de.jplag.java_cpg.token.cpg.CpgTokenType.SYNCHRONIZED_STATEMENT;
import static de.jplag.java_cpg.token.cpg.CpgTokenType.THROW;
import static de.jplag.java_cpg.token.cpg.CpgTokenType.TRY_BLOCK_END;
import static de.jplag.java_cpg.token.cpg.CpgTokenType.TRY_BLOCK_START;
import static de.jplag.java_cpg.token.cpg.CpgTokenType.TRY_STATEMENT;
import static de.jplag.java_cpg.token.cpg.CpgTokenType.VARIABLE_DECL;
import static de.jplag.java_cpg.token.cpg.CpgTokenType.WHILE_BLOCK_END;
import static de.jplag.java_cpg.token.cpg.CpgTokenType.WHILE_BLOCK_START;
import static de.jplag.java_cpg.token.cpg.CpgTokenType.WHILE_STATEMENT;

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
import de.jplag.Token;
import de.jplag.TokenType;
import de.jplag.java_cpg.NodeRegistry;
import de.jplag.java_cpg.token.characteristic.CharacteristicCpgTokenType;
import de.jplag.java_cpg.token.characteristic.CharacteristicVector;
import de.jplag.java_cpg.token.cpg.CpgToken;
import de.jplag.java_cpg.token.cpg.CpgTokenConsumer;
import de.jplag.java_cpg.token.cpg.CpgTokenType;

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
        tokenConsumer.addToken(new CharacteristicCpgTokenType(FILE_END, getSemanticVector(translationUnitDeclaration)),
                new File(translationUnitDeclaration.getName().toString()), -1, -1, -1, translationUnitDeclaration.getName());
    }

    @Override
    public void exit(EnumDeclaration enumDeclaration) {
        tokenConsumer.addToken(new CharacteristicCpgTokenType(ENUM_DECL_END, getSemanticVector(enumDeclaration)), enumDeclaration, true);
    }

    @Override
    public void exit(RecordDeclaration recordDeclaration) {
        if (recordDeclaration.getLocation() == null) {
            // skip auto generated records e.g. for used classes like Queue
            return;
        }
        tokenConsumer.addToken(new CharacteristicCpgTokenType(RECORD_DECL_END, getSemanticVector(recordDeclaration)), recordDeclaration, true);
    }

    @Override
    public void exit(CatchClause catchclause) {
        tokenConsumer.addToken(new CharacteristicCpgTokenType(CATCH_CLAUSE_END, getSemanticVector(catchclause)), catchclause, true);
    }

    @Override
    public void exit(DoStatement doStatement) {
        tokenConsumer.addToken(new CharacteristicCpgTokenType(DO_WHILE_BLOCK_END, getSemanticVector(doStatement)), doStatement, true);
    }

    @Override
    public void exit(Block block) {
        TokenType blockEndToken = openBlocks.pop();
        if (blockEndToken == BLOCK_END)
            return;

        tokenConsumer.addToken(new CharacteristicCpgTokenType(blockEndToken, getSemanticVector(block)), block, true);
    }

    private void expect(CpgTokenType opening, CpgTokenType closing) {
        expectedBlocks.addFirst(new BlockTokens(opening, closing));
    }

    @Override
    public void visit(ConstructorDeclaration constructorDeclaration) {
        // Constructor may be implicit standard constructor
        tokenConsumer.addToken(new CharacteristicCpgTokenType(METHOD_BODY_BEGIN, getSemanticVector(constructorDeclaration)), constructorDeclaration,
                false);
        expect(METHOD_BODY_BEGIN, METHOD_BODY_END);
    }

    @Override
    public void visit(EnumConstantDeclaration enumConstantDeclaration) {
        tokenConsumer.addToken(new CharacteristicCpgTokenType(ENUM_ELEMENT, getSemanticVector(enumConstantDeclaration)), enumConstantDeclaration,
                false);
    }

    @Override
    public void visit(EnumDeclaration enumDeclaration) {
        tokenConsumer.addToken(new CharacteristicCpgTokenType(ENUM_DECL_BEGIN, getSemanticVector(enumDeclaration)), enumDeclaration, false);
    }

    @Override
    public void visit(FieldDeclaration fieldDeclaration) {
        if (fieldDeclaration.getLocation() == null) {
            // skip auto generated fields e.g. static class calls from Integer.
            return;
        }
        tokenConsumer.addToken(new CharacteristicCpgTokenType(FIELD_DECL, getSemanticVector(fieldDeclaration)), fieldDeclaration, false);
    }

    @Override
    public void visit(IncludeDeclaration includeDeclaration) {
        tokenConsumer.addToken(new CharacteristicCpgTokenType(INCLUDE, getSemanticVector(includeDeclaration)), includeDeclaration, false);
    }

    @Override
    public void visit(MethodDeclaration methodDeclaration) {
        tokenConsumer.addToken(new CharacteristicCpgTokenType(METHOD_DECL_BEGIN, getSemanticVector(methodDeclaration)), methodDeclaration, false);
        expect(METHOD_BODY_BEGIN, METHOD_BODY_END);
    }

    @Override
    public void visit(NewArrayExpression newArrayExpression) {
        tokenConsumer.addToken(new CharacteristicCpgTokenType(NEW_ARRAY, getSemanticVector(newArrayExpression)), newArrayExpression, false);
    }

    @Override
    public void visit(ParameterDeclaration parameterDeclaration) {
        tokenConsumer.addToken(new CharacteristicCpgTokenType(METHOD_PARAM, getSemanticVector(parameterDeclaration)), parameterDeclaration, false);
    }

    @Override
    public void visit(RecordDeclaration recordDeclaration) {
        if (recordDeclaration.getLocation() == null) {
            // skip auto generated records e.g. for used classes like Queue
            return;
        }
        tokenConsumer.addToken(new CharacteristicCpgTokenType(RECORD_DECL_BEGIN, getSemanticVector(recordDeclaration)), recordDeclaration, false);
    }

    @Override
    public void visit(VariableDeclaration variableDeclaration) {
        tokenConsumer.addToken(new CharacteristicCpgTokenType(VARIABLE_DECL, getSemanticVector(variableDeclaration)), variableDeclaration, false);
    }

    @Override
    public void visit(AssertStatement assertStatement) {
        tokenConsumer.addToken(new CharacteristicCpgTokenType(ASSERT_STATEMENT, getSemanticVector(assertStatement)), assertStatement, false);
    }

    @Override
    public void visit(BreakStatement breakStatement) {
        tokenConsumer.addToken(new CharacteristicCpgTokenType(BREAK, getSemanticVector(breakStatement)), breakStatement, false);
    }

    @Override
    public void visit(CaseStatement caseStatement) {
        tokenConsumer.addToken(new CharacteristicCpgTokenType(CASE_STATEMENT, getSemanticVector(caseStatement)), caseStatement, false);
    }

    @Override
    public void visit(CatchClause catchclause) {
        tokenConsumer.addToken(new CharacteristicCpgTokenType(CATCH_CLAUSE_BEGIN, getSemanticVector(catchclause)), catchclause, false);
    }

    @Override
    public void visit(ContinueStatement continueStatement) {
        tokenConsumer.addToken(new CharacteristicCpgTokenType(CONTINUE, getSemanticVector(continueStatement)), continueStatement, false);
    }

    @Override
    public void visit(DefaultStatement defaultStatement) {
        tokenConsumer.addToken(new CharacteristicCpgTokenType(DEFAULT_STATEMENT, getSemanticVector(defaultStatement)), defaultStatement, false);
    }

    @Override
    public void visit(DoStatement doStatement) {
        tokenConsumer.addToken(new CharacteristicCpgTokenType(DO_WHILE_STATEMENT, getSemanticVector(doStatement)), doStatement, false);
        expect(DO_WHILE_BLOCK_START, DO_WHILE_BLOCK_END);
    }

    @Override
    public void visit(ForEachStatement forEachStatement) {
        tokenConsumer.addToken(new CharacteristicCpgTokenType(FOR_STATEMENT, getSemanticVector(forEachStatement)), forEachStatement, false);
        expect(FOR_STATEMENT, FOR_BLOCK_END);
    }

    @Override
    public void visit(ForStatement forStatement) {
        tokenConsumer.addToken(new CharacteristicCpgTokenType(FOR_STATEMENT, getSemanticVector(forStatement)), forStatement, false);
        expect(FOR_BLOCK_BEGIN, FOR_BLOCK_END);
    }

    @Override
    public void visit(GotoStatement gotoStatement) {
        tokenConsumer.addToken(new CharacteristicCpgTokenType(GOTO_STATEMENT, getSemanticVector(gotoStatement)), gotoStatement, false);
    }

    @Override
    public void visit(IfStatement ifStatement) {
        tokenConsumer.addToken(new CharacteristicCpgTokenType(IF_STATEMENT, getSemanticVector(ifStatement)), ifStatement, false);

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
        tokenConsumer.addToken(new CharacteristicCpgTokenType(LAMBDA_EXPRESSION, getSemanticVector(lambdaExpression)), lambdaExpression, false);
    }

    @Override
    public void visit(ReturnStatement returnStatement) {
        if (Objects.isNull(returnStatement.getLocation())) {
            // implicit return without return value
            return;
        }
        tokenConsumer.addToken(new CharacteristicCpgTokenType(RETURN, getSemanticVector(returnStatement)), returnStatement, false);
    }

    @Override
    public void visit(SwitchStatement switchStatement) {
        tokenConsumer.addToken(new CharacteristicCpgTokenType(SWITCH_STATEMENT, getSemanticVector(switchStatement)), switchStatement, false);
        expect(SWITCH_BLOCK_START, SWITCH_BLOCK_END);
    }

    @Override
    public void visit(SynchronizedStatement synchronizedStatement) {
        tokenConsumer.addToken(new CharacteristicCpgTokenType(SYNCHRONIZED_STATEMENT, getSemanticVector(synchronizedStatement)),
                synchronizedStatement, false);
        expect(SYNCHRONIZED_BLOCK_START, SYNCHRONIZED_BLOCK_END);
    }

    @Override
    public void visit(TryStatement tryStatement) {
        tokenConsumer.addToken(new CharacteristicCpgTokenType(TRY_STATEMENT, getSemanticVector(tryStatement)), tryStatement, false);
        expect(TRY_BLOCK_START, TRY_BLOCK_END);
    }

    @Override
    public void visit(UnaryOperator unaryoperator) {

        String operatorCode = unaryoperator.getOperatorCode();
        if (Objects.isNull(operatorCode))
            return;
        if (Objects.equals(operatorCode, "throw")) {
            tokenConsumer.addToken(new CharacteristicCpgTokenType(THROW, getSemanticVector(unaryoperator)), unaryoperator, false);
        } else if (Objects.equals(operatorCode, "++") || Objects.equals(operatorCode, "--")) {
            tokenConsumer.addToken(new CharacteristicCpgTokenType(ASSIGNMENT, getSemanticVector(unaryoperator)), unaryoperator, false);
        }
        super.visit(unaryoperator);
    }

    @Override
    public void visit(WhileStatement whileStatement) {
        tokenConsumer.addToken(new CharacteristicCpgTokenType(WHILE_STATEMENT, getSemanticVector(whileStatement)), whileStatement, false);
        expect(WHILE_BLOCK_START, WHILE_BLOCK_END);
    }

    @Override
    public void visit(AssignExpression assignExpression) {
        tokenConsumer.addToken(new CharacteristicCpgTokenType(ASSIGNMENT, getSemanticVector(assignExpression)), assignExpression, false);
    }

    @Override
    public void visit(Block block) {
        if (expectedBlocks.isEmpty()) {
            // Do not add BLOCK_START and BLOCK_END, otherwise that is a vulnerability
            openBlocks.addFirst(BLOCK_END);
        } else {
            BlockTokens blockTokens = expectedBlocks.pop();
            tokenConsumer.addToken(new CharacteristicCpgTokenType(blockTokens.opening, getSemanticVector(block)), block, false);
            openBlocks.addFirst(blockTokens.closing);
        }
    }

    @Override
    public void visit(CallExpression callExpression) {
        tokenConsumer.addToken(new CharacteristicCpgTokenType(METHOD_CALL, getSemanticVector(callExpression)), callExpression, false);
    }

    @Override
    public void visit(ConstructExpression constructorCallExpression) {
        tokenConsumer.addToken(new CharacteristicCpgTokenType(CONSTRUCTOR_CALL, getSemanticVector(constructorCallExpression)),
                constructorCallExpression, false);
    }

    @Override
    public void visit(MemberCallExpression memberCallExpression) {
        tokenConsumer.addToken(new CharacteristicCpgTokenType(METHOD_CALL, getSemanticVector(memberCallExpression)), memberCallExpression, false);
    }

    private record BlockTokens(CpgTokenType opening, CpgTokenType closing) {
    }

    private CharacteristicVector getSemanticVector(Node node) {
        CharacteristicVector vector = NodeRegistry.INSTANCE.getNodeData(node);
        if (vector == null) {
            throw new IllegalStateException("No Vector calculated for node " + node);
        }
        return vector;
    }

}
