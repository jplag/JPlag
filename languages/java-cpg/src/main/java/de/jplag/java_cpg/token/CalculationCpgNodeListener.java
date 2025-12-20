package de.jplag.java_cpg.token;

import java.util.ArrayDeque;
import java.util.Deque;

import org.jetbrains.annotations.NotNull;

import de.fraunhofer.aisec.cpg.graph.Node;
import de.fraunhofer.aisec.cpg.graph.declarations.*;
import de.fraunhofer.aisec.cpg.graph.statements.*;
import de.fraunhofer.aisec.cpg.graph.statements.expressions.*;
import de.jplag.java_cpg.NodeRegistry;
import de.jplag.java_cpg.token.semantic.SemanticDimensionsMapper;
import de.jplag.java_cpg.token.semantic.SemanticVector;

public class CalculationCpgNodeListener extends ACpgNodeListener {
    private final Deque<Node> nodeStack = new ArrayDeque<>();

    @Override
    public void visit(@NotNull Node node) {
        nodeStack.push(node);
        NodeRegistry.INSTANCE.registerNodeData(node, new SemanticVector());
        if (NodeRegistry.INSTANCE.getNodeData(node) == null) {
            NodeRegistry.INSTANCE.registerNodeData(node, new SemanticVector());
        }
    }

    private void exitAnyNode(Node node) {
        if (!nodeStack.pop().equals(node)) {
            throw new IllegalStateException("Node stack is inconsistent!");
        }
        SemanticVector parentSemanticVector = NodeRegistry.INSTANCE.getNodeData(nodeStack.getFirst());
        SemanticVector currentSemanticVector = NodeRegistry.INSTANCE.getNodeData(node);

        parentSemanticVector.addVector(currentSemanticVector);
        if (SemanticDimensionsMapper.mapNodeType(node) != null) {
            parentSemanticVector.incrementDimension(SemanticDimensionsMapper.mapNodeType(node));
        }
        NodeRegistry.INSTANCE.registerNodeData(nodeStack.getFirst(), parentSemanticVector);
    }

    @Override
    void exit(AssertStatement assertStatement) {
        exitAnyNode(assertStatement);

    }

    @Override
    void exit(AssignExpression assignExpression) {
        exitAnyNode(assignExpression);
    }

    @Override
    void exit(BinaryOperator binaryoperator) {
        exitAnyNode(binaryoperator);
    }

    @Override
    void exit(Block block) {
        exitAnyNode(block);
    }

    @Override
    void exit(BreakStatement breakStatement) {
        exitAnyNode(breakStatement);
    }

    @Override
    void exit(CallExpression callExpression) {
        exitAnyNode(callExpression);
    }

    @Override
    void exit(CaseStatement caseStatement) {
        exitAnyNode(caseStatement);
    }

    @Override
    void exit(CastExpression castExpression) {
        exitAnyNode(castExpression);
    }

    @Override
    void exit(CatchClause catchclause) {
        exitAnyNode(catchclause);
    }

    @Override
    void exit(ConditionalExpression conditionalExpression) {
        exitAnyNode(conditionalExpression);
    }

    @Override
    void exit(ConstructExpression constructExpression) {
        exitAnyNode(constructExpression);
    }

    @Override
    void exit(ConstructorDeclaration constructorDeclaration) {
        exitAnyNode(constructorDeclaration);
    }

    @Override
    void exit(ContinueStatement continueStatement) {
        exitAnyNode(continueStatement);
    }

    @Override
    void exit(Declaration declaration) {
        exitAnyNode(declaration);
    }

    @Override
    void exit(DeclarationSequence declarationsequence) {
        exitAnyNode(declarationsequence);
    }

    @Override
    void exit(DeclarationStatement declarationStatement) {
        exitAnyNode(declarationStatement);
    }

    @Override
    void exit(DefaultStatement defaultStatement) {
        exitAnyNode(defaultStatement);
    }

    @Override
    void exit(DeleteExpression deleteExpression) {
        exitAnyNode(deleteExpression);
    }

    @Override
    void exit(DoStatement doStatement) {
        exitAnyNode(doStatement);
    }

    @Override
    void exit(EmptyStatement emptyStatement) {
        exitAnyNode(emptyStatement);
    }

    @Override
    void exit(EnumConstantDeclaration enumConstantDeclaration) {
        exitAnyNode(enumConstantDeclaration);
    }

    @Override
    void exit(EnumDeclaration enumDeclaration) {
        exitAnyNode(enumDeclaration);
    }

    @Override
    void exit(Expression expression) {
        exitAnyNode(expression);
    }

    @Override
    void exit(ExpressionList expressionlist) {
        exitAnyNode(expressionlist);
    }

    @Override
    void exit(FieldDeclaration fieldDeclaration) {
        exitAnyNode(fieldDeclaration);
    }

    @Override
    void exit(ForEachStatement forEachStatement) {
        exitAnyNode(forEachStatement);
    }

    @Override
    void exit(ForStatement forStatement) {
        exitAnyNode(forStatement);
    }

    @Override
    void exit(FunctionDeclaration functionDeclaration) {
        exitAnyNode(functionDeclaration);
    }

    @Override
    void exit(FunctionTemplateDeclaration functionTemplateDeclaration) {
        exitAnyNode(functionTemplateDeclaration);
    }

    @Override
    void exit(GotoStatement gotoStatement) {
        exitAnyNode(gotoStatement);
    }

    @Override
    void exit(IfStatement ifStatement) {
        exitAnyNode(ifStatement);
    }

    @Override
    void exit(IncludeDeclaration includeDeclaration) {
        exitAnyNode(includeDeclaration);
    }

    @Override
    void exit(InitializerListExpression initializerListExpression) {
        exitAnyNode(initializerListExpression);
    }

    @Override
    void exit(KeyValueExpression keyValueExpression) {
        exitAnyNode(keyValueExpression);
    }

    @Override
    void exit(LabelStatement labelStatement) {
        exitAnyNode(labelStatement);
    }

    @Override
    void exit(LambdaExpression lambdaExpression) {
        exitAnyNode(lambdaExpression);
    }

    @Override
    void exit(Literal<?> literal) {
        exitAnyNode(literal);
        // TODO Add data dependencies
    }

    @Override
    void exit(MemberCallExpression memberCallExpression) {
        exitAnyNode(memberCallExpression);
    }

    @Override
    void exit(MemberExpression memberExpression) {
        exitAnyNode(memberExpression);
    }

    @Override
    void exit(MethodDeclaration methodDeclaration) {
        exitAnyNode(methodDeclaration);
    }

    @Override
    void exit(NamespaceDeclaration namespaceDeclaration) {
        exitAnyNode(namespaceDeclaration);
    }

    @Override
    void exit(NewArrayExpression newArrayExpression) {
        exitAnyNode(newArrayExpression);
    }

    @Override
    void exit(NewExpression newExpression) {
        exitAnyNode(newExpression);
    }

    @Override
    void exit(ParameterDeclaration parameterDeclaration) {
        exitAnyNode(parameterDeclaration);
    }

    @Override
    void exit(ProblemDeclaration problemDeclaration) {
        exitAnyNode(problemDeclaration);
    }

    @Override
    void exit(ProblemExpression problemExpression) {
        exitAnyNode(problemExpression);
    }

    @Override
    void exit(RangeExpression rangeExpression) {
        exitAnyNode(rangeExpression);
    }

    @Override
    void exit(RecordDeclaration recordDeclaration) {
        exitAnyNode(recordDeclaration);
    }

    @Override
    void exit(RecordTemplateDeclaration recordTemplateDeclaration) {
        exitAnyNode(recordTemplateDeclaration);
    }

    @Override
    void exit(Reference reference) {
        exitAnyNode(reference);
    }

    @Override
    void exit(ReturnStatement returnStatement) {
        exitAnyNode(returnStatement);
    }

    @Override
    void exit(ShortCircuitOperator shortCircuitOperator) {
        exitAnyNode(shortCircuitOperator);
    }

    @Override
    void exit(Statement statement) {
        exitAnyNode(statement);
    }

    @Override
    void exit(SubscriptExpression subscriptExpression) {
        exitAnyNode(subscriptExpression);
    }

    @Override
    void exit(SwitchStatement switchStatement) {
        exitAnyNode(switchStatement);
    }

    @Override
    void exit(SynchronizedStatement synchronizedStatement) {
        exitAnyNode(synchronizedStatement);
    }

    @Override
    void exit(TemplateDeclaration templateDeclaration) {
        exitAnyNode(templateDeclaration);
    }

    @Override
    void exit(TranslationUnitDeclaration translationUnitDeclaration) {
        exitAnyNode(translationUnitDeclaration);
    }

    @Override
    void exit(TryStatement tryStatement) {
        exitAnyNode(tryStatement);
    }

    @Override
    void exit(TupleDeclaration tupleDeclaration) {
        exitAnyNode(tupleDeclaration);
    }

    @Override
    void exit(TypedefDeclaration typedefDeclaration) {
        exitAnyNode(typedefDeclaration);
    }

    @Override
    void exit(TypeExpression typeExpression) {
        exitAnyNode(typeExpression);
    }

    @Override
    void exit(TypeIdExpression typeIdExpression) {
        exitAnyNode(typeIdExpression);
    }

    @Override
    void exit(TypeParameterDeclaration typeParameterDeclaration) {
        exitAnyNode(typeParameterDeclaration);
    }

    @Override
    void exit(UnaryOperator unaryoperator) {
        exitAnyNode(unaryoperator);
    }

    @Override
    void exit(UsingDeclaration usingDeclaration) {
        exitAnyNode(usingDeclaration);
    }

    @Override
    void exit(ValueDeclaration valueDeclaration) {
        exitAnyNode(valueDeclaration);
    }

    @Override
    void exit(VariableDeclaration variableDeclaration) {
        exitAnyNode(variableDeclaration);
    }

    @Override
    void exit(WhileStatement whileStatement) {
        exitAnyNode(whileStatement);
    }

}
