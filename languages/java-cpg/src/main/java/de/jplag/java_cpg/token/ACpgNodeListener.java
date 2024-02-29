package de.jplag.java_cpg.token;

import de.fraunhofer.aisec.cpg.graph.Node;
import de.fraunhofer.aisec.cpg.graph.declarations.*;
import de.fraunhofer.aisec.cpg.graph.statements.*;
import de.fraunhofer.aisec.cpg.graph.statements.expressions.*;

/**
 * This class provides empty dummy implementations for {@link CpgNodeListener}s.
 */
public abstract class ACpgNodeListener extends IVisitorExitor<Node> {

    void visit(ConstructorDeclaration constructorDeclaration) {
        
    }

    void exit(ConstructorDeclaration constructorDeclaration) {
        
    }

    void visit(Declaration declaration) {
        
    }

    void exit(Declaration declaration) {
        
    }

    void visit(DeclarationSequence declarationsequence) {
        
    }

    void exit(DeclarationSequence declarationsequence) {
        
    }

    void visit(EnumConstantDeclaration enumconstantDeclaration) {
        
    }

    void exit(EnumConstantDeclaration enumconstantDeclaration) {
        
    }

    void visit(EnumDeclaration enumDeclaration) {
        
    }

    void exit(EnumDeclaration enumDeclaration) {
        
    }

    void visit(FieldDeclaration fieldDeclaration) {
        
    }

    void exit(FieldDeclaration fieldDeclaration) {
        
    }

    void visit(FunctionDeclaration functionDeclaration) {
        
    }

    void exit(FunctionDeclaration functionDeclaration) {
        
    }

    void visit(FunctionTemplateDeclaration functiontemplateDeclaration) {
        
    }

    void exit(FunctionTemplateDeclaration functiontemplateDeclaration) {
        
    }

    void visit(IncludeDeclaration includeDeclaration) {
        
    }

    void exit(IncludeDeclaration includeDeclaration) {
        
    }

    void visit(MethodDeclaration methodDeclaration) {
        
    }

    void exit(MethodDeclaration methodDeclaration) {}

    void visit(NamespaceDeclaration namespaceDeclaration) {}

    void exit(NamespaceDeclaration namespaceDeclaration) {}

    void visit(ParameterDeclaration parameterDeclaration) {}

    void exit(ParameterDeclaration parameterDeclaration) {}

    void visit(ProblemDeclaration problemDeclaration) {}

    void exit(ProblemDeclaration problemDeclaration) {}

    void visit(RecordDeclaration recordDeclaration) {}

    void exit(RecordDeclaration recordDeclaration) {}

    void visit(RecordTemplateDeclaration recordtemplateDeclaration) {}

    void exit(RecordTemplateDeclaration recordtemplateDeclaration) {}

    void visit(TemplateDeclaration templateDeclaration) {}

    void exit(TemplateDeclaration templateDeclaration) {}

    void visit(TranslationUnitDeclaration translationunitDeclaration) {}

    void exit(TranslationUnitDeclaration translationunitDeclaration) {}

    void visit(TupleDeclaration tupleDeclaration) {}

    void exit(TupleDeclaration tupleDeclaration) {}

    void visit(TypedefDeclaration typedefDeclaration) {}

    void exit(TypedefDeclaration typedefDeclaration) {}

    void visit(TypeParameterDeclaration typeparameterDeclaration) {}

    void exit(TypeParameterDeclaration typeparameterDeclaration) {}

    void visit(UsingDeclaration usingDeclaration) {}

    void exit(UsingDeclaration usingDeclaration) {}

    void visit(ValueDeclaration valueDeclaration) {}

    void exit(ValueDeclaration valueDeclaration) {}

    void visit(VariableDeclaration variableDeclaration) {}

    void exit(VariableDeclaration variableDeclaration) {}

    void visit(ASMDeclarationStatement asmDeclarationStatement) {}

    void exit(ASMDeclarationStatement asmDeclarationStatement) {}

    void visit(AssertStatement assertStatement) {}

    void exit(AssertStatement assertStatement) {}

    void visit(BreakStatement breakStatement) {}

    void exit(BreakStatement breakStatement) {}

    void visit(CaseStatement caseStatement) {}

    void exit(CaseStatement caseStatement) {}

    void visit(CatchClause catchclause) {}

    void exit(CatchClause catchclause) {}

    void visit(ContinueStatement continueStatement) {}

    void exit(ContinueStatement continueStatement) {}

    void visit(DeclarationStatement declarationStatement) {}

    void exit(DeclarationStatement declarationStatement) {}

    void visit(DefaultStatement defaultStatement) {}

    void exit(DefaultStatement defaultStatement) {}

    void visit(DoStatement doStatement) {}

    void exit(DoStatement doStatement) {}

    void visit(EmptyStatement emptyStatement) {}

    void exit(EmptyStatement emptyStatement) {}

    void visit(ForEachStatement foreachStatement) {}

    void exit(ForEachStatement foreachStatement) {}

    void visit(ForStatement forStatement) {}

    void exit(ForStatement forStatement) {}

    void visit(GotoStatement gotoStatement) {}

    void exit(GotoStatement gotoStatement) {}

    void visit(IfStatement ifStatement) {}

    void exit(IfStatement ifStatement) {}

    void visit(LabelStatement labelStatement) {}

    void exit(LabelStatement labelStatement) {}

    void visit(ReturnStatement returnStatement) {}

    void exit(ReturnStatement returnStatement) {}

    void visit(Statement Statement) {}

    void exit(Statement Statement) {}

    void visit(SwitchStatement switchStatement) {}

    void exit(SwitchStatement switchStatement) {}

    void visit(SynchronizedStatement synchronizedStatement) {}

    void exit(SynchronizedStatement synchronizedStatement) {}

    void visit(TryStatement tryStatement) {}

    void exit(TryStatement tryStatement) {}

    void visit(WhileStatement whileStatement) {}

    void exit(WhileStatement whileStatement) {}

    void visit(AssignExpression assignExpression) {}

    void exit(AssignExpression assignExpression) {}

    void visit(BinaryOperator binaryoperator) {}

    void exit(BinaryOperator binaryoperator) {}

    void visit(Block block) {}

    void exit(Block block) {}

    void visit(CallExpression callExpression) {}

    void exit(CallExpression callExpression) {}

    void visit(CastExpression castExpression) {}

    void exit(CastExpression castExpression) {}

    void visit(ConditionalExpression conditionalExpression) {}

    void exit(ConditionalExpression conditionalExpression) {}

    void visit(ConstructExpression constructExpression) {}

    void exit(ConstructExpression constructExpression) {}

    void visit(ConstructorCallExpression constructorCallExpression) {}

    void exit(ConstructorCallExpression constructorCallExpression) {}

    void visit(DeleteExpression deleteExpression) {}

    void exit(DeleteExpression deleteExpression) {}

    void visit(DesignatedInitializerExpression designatedinitializerExpression) {}

    void exit(DesignatedInitializerExpression designatedinitializerExpression) {}

    void visit(Expression expression) {}

    void exit(Expression expression) {}

    void visit(ExpressionList expressionlist) {}

    void exit(ExpressionList expressionlist) {}

    void visit(InitializerListExpression initializerlistExpression) {}

    void exit(InitializerListExpression initializerlistExpression) {}

    void visit(KeyValueExpression keyvalueExpression) {}

    void exit(KeyValueExpression keyvalueExpression) {}

    void visit(LambdaExpression lambdaExpression) {}

    void exit(LambdaExpression lambdaExpression) {}

    void visit(Literal<?> literal) {}

    void exit(Literal<?> literal) {}

    void visit(MemberCallExpression membercallExpression) {}

    void exit(MemberCallExpression membercallExpression) {}

    void visit(MemberExpression memberExpression) {}

    void exit(MemberExpression memberExpression) {}

    void visit(NewArrayExpression newarrayExpression) {}

    void exit(NewArrayExpression newarrayExpression) {}

    void visit(NewExpression newExpression) {}

    void exit(NewExpression newExpression) {}

    void visit(ProblemExpression problemExpression) {}

    void exit(ProblemExpression problemExpression) {}

    void visit(RangeExpression rangeExpression) {}

    void exit(RangeExpression rangeExpression) {}

    void visit(Reference reference) {}

    void exit(Reference reference) {}

    void visit(ShortCircuitOperator shortcircuitoperator) {}

    void exit(ShortCircuitOperator shortcircuitoperator) {}

    void visit(SubscriptExpression subscriptExpression) {}

    void exit(SubscriptExpression subscriptExpression) {}

    void visit(TypeExpression typeExpression) {}

    void exit(TypeExpression typeExpression) {}

    void visit(TypeIdExpression typeidExpression) {}

    void exit(TypeIdExpression typeidExpression) {}

    void visit(UnaryOperator unaryoperator) {}

    void exit(UnaryOperator unaryoperator) {}
}
