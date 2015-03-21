package jplag.java17;

import jplag.java17.grammar.*;
import jplag.java17.grammar.Java7Parser.AnnotationContext;
import jplag.java17.grammar.Java7Parser.AnnotationMethodContext;
import jplag.java17.grammar.Java7Parser.AnnotationNameContext;
import jplag.java17.grammar.Java7Parser.AnnotationTypeDeclarationContext;
import jplag.java17.grammar.Java7Parser.AnnotationTypeElementContext;
import jplag.java17.grammar.Java7Parser.ArgumentsContext;
import jplag.java17.grammar.Java7Parser.ArrayCreatorRestContext;
import jplag.java17.grammar.Java7Parser.ArrayInitializerContext;
import jplag.java17.grammar.Java7Parser.AssertStmtContext;
import jplag.java17.grammar.Java7Parser.BlockContext;
import jplag.java17.grammar.Java7Parser.BlockStatementContext;
import jplag.java17.grammar.Java7Parser.BlockStmtContext;
import jplag.java17.grammar.Java7Parser.BooleanLiteralContext;
import jplag.java17.grammar.Java7Parser.BoundContext;
import jplag.java17.grammar.Java7Parser.BreakContext;
import jplag.java17.grammar.Java7Parser.CatchClauseContext;
import jplag.java17.grammar.Java7Parser.CbdBlockContext;
import jplag.java17.grammar.Java7Parser.CbdMemberContext;
import jplag.java17.grammar.Java7Parser.CbdSemicolonContext;
import jplag.java17.grammar.Java7Parser.ClassBodyContext;
import jplag.java17.grammar.Java7Parser.ClassCreatorRestContext;
import jplag.java17.grammar.Java7Parser.ClassDeclarationContext;
import jplag.java17.grammar.Java7Parser.ClassOrInterfaceDeclarationContext;
import jplag.java17.grammar.Java7Parser.ClassOrInterfaceTypeContext;
import jplag.java17.grammar.Java7Parser.CompilationUnitContext;
import jplag.java17.grammar.Java7Parser.ConstantDeclaratorContext;
import jplag.java17.grammar.Java7Parser.ConstantDeclaratorRestContext;
import jplag.java17.grammar.Java7Parser.ConstantDeclaratorsRestContext;
import jplag.java17.grammar.Java7Parser.ConstantExpressionContext;
import jplag.java17.grammar.Java7Parser.ConstructorDeclarationContext;
import jplag.java17.grammar.Java7Parser.ContinueStmtContext;
import jplag.java17.grammar.Java7Parser.CreatedNameContext;
import jplag.java17.grammar.Java7Parser.CreatorContext;
import jplag.java17.grammar.Java7Parser.DefaultValueContext;
import jplag.java17.grammar.Java7Parser.DoWhileStmtContext;
import jplag.java17.grammar.Java7Parser.ElementValueArrayInitializerContext;
import jplag.java17.grammar.Java7Parser.ElementValueContext;
import jplag.java17.grammar.Java7Parser.ElementValuePairContext;
import jplag.java17.grammar.Java7Parser.ElementValuePairsContext;
import jplag.java17.grammar.Java7Parser.EnhancedForControlContext;
import jplag.java17.grammar.Java7Parser.EnumConstantContext;
import jplag.java17.grammar.Java7Parser.EnumConstantNameContext;
import jplag.java17.grammar.Java7Parser.EnumConstantsContext;
import jplag.java17.grammar.Java7Parser.EnumDeclarationContext;
import jplag.java17.grammar.Java7Parser.ExplicitConstructorInvocationContext;
import jplag.java17.grammar.Java7Parser.ExplicitGenericInvocationContext;
import jplag.java17.grammar.Java7Parser.ExprArrayExpressionContext;
import jplag.java17.grammar.Java7Parser.ExprAssignmentContext;
import jplag.java17.grammar.Java7Parser.ExprBinaryAndContext;
import jplag.java17.grammar.Java7Parser.ExprBinaryBoolAndContext;
import jplag.java17.grammar.Java7Parser.ExprBinaryBoolOrContext;
import jplag.java17.grammar.Java7Parser.ExprBinaryEqualsContext;
import jplag.java17.grammar.Java7Parser.ExprBinaryNotContext;
import jplag.java17.grammar.Java7Parser.ExprBinaryOperatorAddContext;
import jplag.java17.grammar.Java7Parser.ExprBinaryOperatorCompContext;
import jplag.java17.grammar.Java7Parser.ExprBinaryOperatorGTContext;
import jplag.java17.grammar.Java7Parser.ExprBinaryOperatorInstanceofContext;
import jplag.java17.grammar.Java7Parser.ExprBinaryOperatorMultContext;
import jplag.java17.grammar.Java7Parser.ExprBinaryOrContext;
import jplag.java17.grammar.Java7Parser.ExprCastExpressionContext;
import jplag.java17.grammar.Java7Parser.ExprConditionalExpressionContext;
import jplag.java17.grammar.Java7Parser.ExprExplicitGenericInvocationContext;
import jplag.java17.grammar.Java7Parser.ExprExpressionIncDecContext;
import jplag.java17.grammar.Java7Parser.ExprIdentifierContext;
import jplag.java17.grammar.Java7Parser.ExprIncDecExpressionContext;
import jplag.java17.grammar.Java7Parser.ExprMethodExpressionListContext;
import jplag.java17.grammar.Java7Parser.ExprNewCreatorContext;
import jplag.java17.grammar.Java7Parser.ExprNewIdentidierContext;
import jplag.java17.grammar.Java7Parser.ExprNotExpressionContext;
import jplag.java17.grammar.Java7Parser.ExprPrimaryContext;
import jplag.java17.grammar.Java7Parser.ExprSuperContext;
import jplag.java17.grammar.Java7Parser.ExprSuperIdentifierContext;
import jplag.java17.grammar.Java7Parser.ExprThisContext;
import jplag.java17.grammar.Java7Parser.ExpressionListContext;
import jplag.java17.grammar.Java7Parser.FieldDeclarationContext;
import jplag.java17.grammar.Java7Parser.ForControlContext;
import jplag.java17.grammar.Java7Parser.ForInitContext;
import jplag.java17.grammar.Java7Parser.ForStmtContext;
import jplag.java17.grammar.Java7Parser.ForUpdateContext;
import jplag.java17.grammar.Java7Parser.FormalParameterDeclarationsContext;
import jplag.java17.grammar.Java7Parser.FormalParameterVariablesContext;
import jplag.java17.grammar.Java7Parser.FormalParametersContext;
import jplag.java17.grammar.Java7Parser.IdentifiedStmtContext;
import jplag.java17.grammar.Java7Parser.IfStmtContext;
import jplag.java17.grammar.Java7Parser.ImportDeclarationContext;
import jplag.java17.grammar.Java7Parser.InnerCreatorContext;
import jplag.java17.grammar.Java7Parser.IntegerLiteralContext;
import jplag.java17.grammar.Java7Parser.InterfaceBodyContext;
import jplag.java17.grammar.Java7Parser.InterfaceDeclarationContext;
import jplag.java17.grammar.Java7Parser.InterfaceGenericMethodDeclContext;
import jplag.java17.grammar.Java7Parser.InterfaceMemberDeclContext;
import jplag.java17.grammar.Java7Parser.InterfaceMethodDeclaratorRestContext;
import jplag.java17.grammar.Java7Parser.InterfaceMethodOrFieldDeclContext;
import jplag.java17.grammar.Java7Parser.InterfaceMethodOrFieldRestContext;
import jplag.java17.grammar.Java7Parser.LiteralContext;
import jplag.java17.grammar.Java7Parser.LocalVariableDeclarationContext;
import jplag.java17.grammar.Java7Parser.MemberDeclContext;
import jplag.java17.grammar.Java7Parser.MethodBodyContext;
import jplag.java17.grammar.Java7Parser.MethodDeclarationContext;
import jplag.java17.grammar.Java7Parser.ModifierContext;
import jplag.java17.grammar.Java7Parser.NonWildcardTypeArgumentsContext;
import jplag.java17.grammar.Java7Parser.NormalClassDeclarationContext;
import jplag.java17.grammar.Java7Parser.NormalInterfaceDeclarationContext;
import jplag.java17.grammar.Java7Parser.PackageDeclarationContext;
import jplag.java17.grammar.Java7Parser.PackageOrTypeNameContext;
import jplag.java17.grammar.Java7Parser.ParExpressionContext;
import jplag.java17.grammar.Java7Parser.PrimaryContext;
import jplag.java17.grammar.Java7Parser.PrimitiveTypeContext;
import jplag.java17.grammar.Java7Parser.QualifiedIdentifierContext;
import jplag.java17.grammar.Java7Parser.QualifiedIdentifierListContext;
import jplag.java17.grammar.Java7Parser.ResourceContext;
import jplag.java17.grammar.Java7Parser.ResourcesContext;
import jplag.java17.grammar.Java7Parser.ReturnStmtContext;
import jplag.java17.grammar.Java7Parser.SemicStmtContext;
import jplag.java17.grammar.Java7Parser.StatementExpressStmtContext;
import jplag.java17.grammar.Java7Parser.StatementExpressionContext;
import jplag.java17.grammar.Java7Parser.SwitchBlockContext;
import jplag.java17.grammar.Java7Parser.SwitchBlockStatementGroupContext;
import jplag.java17.grammar.Java7Parser.SwitchLabelContext;
import jplag.java17.grammar.Java7Parser.SwitchStmtContext;
import jplag.java17.grammar.Java7Parser.SynchronizedStmtContext;
import jplag.java17.grammar.Java7Parser.ThrowStmtContext;
import jplag.java17.grammar.Java7Parser.TryStatementContext;
import jplag.java17.grammar.Java7Parser.TryStmtContext;
import jplag.java17.grammar.Java7Parser.TypeArgumentContext;
import jplag.java17.grammar.Java7Parser.TypeArgumentsContext;
import jplag.java17.grammar.Java7Parser.TypeDeclarationContext;
import jplag.java17.grammar.Java7Parser.TypeListContext;
import jplag.java17.grammar.Java7Parser.TypeNameContext;
import jplag.java17.grammar.Java7Parser.TypeParameterContext;
import jplag.java17.grammar.Java7Parser.TypeParametersContext;
import jplag.java17.grammar.Java7Parser.TypeRefContext;
import jplag.java17.grammar.Java7Parser.VariableDeclaratorContext;
import jplag.java17.grammar.Java7Parser.VariableDeclaratorIdContext;
import jplag.java17.grammar.Java7Parser.VariableDeclaratorsContext;
import jplag.java17.grammar.Java7Parser.VariableInitializerContext;
import jplag.java17.grammar.Java7Parser.VariableModifierContext;
import jplag.java17.grammar.Java7Parser.VoidInterfaceMethodDeclaratorRestContext;
import jplag.java17.grammar.Java7Parser.WhileStmtContext;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;

public class JplagJava7Listener implements Java7Listener, JavaTokenConstants {

	private jplag.java17.Parser jplagParser;

	public JplagJava7Listener(jplag.java17.Parser jplag) {
		jplagParser = jplag;
	}

	@Override
	public void enterEveryRule(ParserRuleContext arg0) {

	}

	@Override
	public void exitEveryRule(ParserRuleContext arg0) {

	}

	@Override
	public void visitErrorNode(ErrorNode arg0) {

	}

	@Override
	public void visitTerminal(TerminalNode arg0) {
		if (arg0.getText().equals("else")) {
			jplagParser.add(J_ELSE, arg0.getSymbol());
		}

	}

	@Override
	public void enterInnerCreator(InnerCreatorContext ctx) {

	}

	@Override
	public void exitInnerCreator(InnerCreatorContext ctx) {

	}

	@Override
	public void enterAnnotationTypeDeclaration(AnnotationTypeDeclarationContext ctx) {
		jplagParser.add(J_ANNO_T_BEGIN, ctx.getStart());
	}

	@Override
	public void exitAnnotationTypeDeclaration(AnnotationTypeDeclarationContext ctx) {
		jplagParser.add(J_ANNO_T_END, ctx.getStop());
	}

	@Override
	public void enterVariableDeclarator(VariableDeclaratorContext ctx) {
		jplagParser.add(J_VARDEF, ctx.getStart());
	}

	@Override
	public void exitVariableDeclarator(VariableDeclaratorContext ctx) {

	}

	@Override
	public void enterResources(ResourcesContext ctx) {

	}

	@Override
	public void exitResources(ResourcesContext ctx) {

	}

	@Override
	public void enterExpressionList(ExpressionListContext ctx) {

	}

	@Override
	public void exitExpressionList(ExpressionListContext ctx) {

	}

	@Override
	public void enterQualifiedIdentifier(QualifiedIdentifierContext ctx) {

	}

	@Override
	public void exitQualifiedIdentifier(QualifiedIdentifierContext ctx) {

	}

	@Override
	public void enterTypeDeclaration(TypeDeclarationContext ctx) {

	}

	@Override
	public void exitTypeDeclaration(TypeDeclarationContext ctx) {

	}

	@Override
	public void enterForUpdate(ForUpdateContext ctx) {

	}

	@Override
	public void exitForUpdate(ForUpdateContext ctx) {

	}

	@Override
	public void enterFormalParameterVariables(FormalParameterVariablesContext ctx) {

	}

	@Override
	public void exitFormalParameterVariables(FormalParameterVariablesContext ctx) {

	}

	@Override
	public void enterElementValueArrayInitializer(ElementValueArrayInitializerContext ctx) {

	}

	@Override
	public void exitElementValueArrayInitializer(ElementValueArrayInitializerContext ctx) {

	}

	@Override
	public void enterAnnotation(AnnotationContext ctx) {
		jplagParser.add(J_ANNO, ctx.getStart());
	}

	@Override
	public void exitAnnotation(AnnotationContext ctx) {

	}

	@Override
	public void enterMemberDecl(MemberDeclContext ctx) {

	}

	@Override
	public void exitMemberDecl(MemberDeclContext ctx) {

	}

	@Override
	public void enterEnumConstant(EnumConstantContext ctx) {

	}

	@Override
	public void exitEnumConstant(EnumConstantContext ctx) {

	}

	@Override
	public void enterAnnotationName(AnnotationNameContext ctx) {

	}

	@Override
	public void exitAnnotationName(AnnotationNameContext ctx) {

	}

	@Override
	public void enterEnhancedForControl(EnhancedForControlContext ctx) {

	}

	@Override
	public void exitEnhancedForControl(EnhancedForControlContext ctx) {

	}

	@Override
	public void enterPrimary(PrimaryContext ctx) {

	}

	@Override
	public void exitPrimary(PrimaryContext ctx) {

	}

	@Override
	public void enterNormalClassDeclaration(NormalClassDeclarationContext ctx) {
		jplagParser.add(J_CLASS_BEGIN, ctx.getStart());
	}

	@Override
	public void exitNormalClassDeclaration(NormalClassDeclarationContext ctx) {
		jplagParser.add(J_CLASS_END, ctx.getStop());

	}

	@Override
	public void enterClassBody(ClassBodyContext ctx) {

	}

	@Override
	public void exitClassBody(ClassBodyContext ctx) {

	}

	@Override
	public void enterDefaultValue(DefaultValueContext ctx) {

	}

	@Override
	public void exitDefaultValue(DefaultValueContext ctx) {

	}

	@Override
	public void enterImportDeclaration(ImportDeclarationContext ctx) {
		jplagParser.add(J_IMPORT, ctx.getStart());
	}

	@Override
	public void exitImportDeclaration(ImportDeclarationContext ctx) {

	}

	@Override
	public void enterVariableModifier(VariableModifierContext ctx) {

	}

	@Override
	public void exitVariableModifier(VariableModifierContext ctx) {

	}

	@Override
	public void enterEnumConstantName(EnumConstantNameContext ctx) {

	}

	@Override
	public void exitEnumConstantName(EnumConstantNameContext ctx) {

	}

	@Override
	public void enterCreatedName(CreatedNameContext ctx) {
	}

	@Override
	public void exitCreatedName(CreatedNameContext ctx) {

	}

	@Override
	public void enterInterfaceDeclaration(InterfaceDeclarationContext ctx) {
	}

	@Override
	public void exitInterfaceDeclaration(InterfaceDeclarationContext ctx) {
	}

	@Override
	public void enterQualifiedIdentifierList(QualifiedIdentifierListContext ctx) {

	}

	@Override
	public void exitQualifiedIdentifierList(QualifiedIdentifierListContext ctx) {

	}

	@Override
	public void enterPackageDeclaration(PackageDeclarationContext ctx) {
		if (!ctx.annotation().isEmpty()) {
			// if we have an annoation, we have an package-info.java and suppress the package token
		} else {
			jplagParser.add(J_PACKAGE, ctx.getStart());
		}
	}

	@Override
	public void exitPackageDeclaration(PackageDeclarationContext ctx) {
	}

	@Override
	public void enterTypeRef(TypeRefContext ctx) {

	}

	@Override
	public void exitTypeRef(TypeRefContext ctx) {

	}

	@Override
	public void enterConstantDeclarator(ConstantDeclaratorContext ctx) {
		jplagParser.add(J_VARDEF, ctx.getStart());
	}

	@Override
	public void exitConstantDeclarator(ConstantDeclaratorContext ctx) {

	}

	@Override
	public void enterConstantDeclaratorRest(ConstantDeclaratorRestContext ctx) {

	}

	@Override
	public void exitConstantDeclaratorRest(ConstantDeclaratorRestContext ctx) {

	}

	@Override
	public void enterElementValuePairs(ElementValuePairsContext ctx) {

	}

	@Override
	public void exitElementValuePairs(ElementValuePairsContext ctx) {

	}

	@Override
	public void enterVariableDeclarators(VariableDeclaratorsContext ctx) {

	}

	@Override
	public void exitVariableDeclarators(VariableDeclaratorsContext ctx) {

	}

	@Override
	public void enterTypeArguments(TypeArgumentsContext ctx) {

	}

	@Override
	public void exitTypeArguments(TypeArgumentsContext ctx) {

	}

	@Override
	public void enterClassCreatorRest(ClassCreatorRestContext ctx) {
		if (ctx.classBody() != null) {
			jplagParser.add(J_IN_CLASS_BEGIN, ctx.getStart());
		}
	}

	@Override
	public void exitClassCreatorRest(ClassCreatorRestContext ctx) {
		if (ctx.classBody() != null) {
			jplagParser.add(J_IN_CLASS_END, ctx.getStop());
		}
	}

	@Override
	public void enterSwitchBlock(SwitchBlockContext ctx) {

	}

	@Override
	public void exitSwitchBlock(SwitchBlockContext ctx) {

	}

	@Override
	public void enterAnnotationMethod(AnnotationMethodContext ctx) {

	}

	@Override
	public void exitAnnotationMethod(AnnotationMethodContext ctx) {

	}

	@Override
	public void enterModifier(ModifierContext ctx) {

	}

	@Override
	public void exitModifier(ModifierContext ctx) {

	}

	@Override
	public void enterCatchClause(CatchClauseContext ctx) {
		jplagParser.add(J_CATCH_BEGIN, ctx.getStart());
	}

	@Override
	public void exitCatchClause(CatchClauseContext ctx) {
		jplagParser.add(J_CATCH_END, ctx.getStop());
	}


	@Override
	public void enterEnumConstants(EnumConstantsContext ctx) {
		jplagParser.add(J_ENUM_CLASS_BEGIN, ctx.getStop());
	}

	@Override
	public void exitEnumConstants(EnumConstantsContext ctx) {

	}

	@Override
	public void enterInterfaceBody(InterfaceBodyContext ctx) {

	}

	@Override
	public void exitInterfaceBody(InterfaceBodyContext ctx) {

	}

	@Override
	public void enterConstantExpression(ConstantExpressionContext ctx) {

	}

	@Override
	public void exitConstantExpression(ConstantExpressionContext ctx) {

	}

	@Override
	public void enterPackageOrTypeName(PackageOrTypeNameContext ctx) {
	}

	@Override
	public void exitPackageOrTypeName(PackageOrTypeNameContext ctx) {
	}

	@Override
	public void enterForControl(ForControlContext ctx) {
	}

	@Override
	public void exitForControl(ForControlContext ctx) {
	}

	@Override
	public void enterEnumDeclaration(EnumDeclarationContext ctx) {
		jplagParser.add(J_ENUM_BEGIN, ctx.getStart());
	}

	@Override
	public void exitEnumDeclaration(EnumDeclarationContext ctx) {
		jplagParser.add(J_ENUM_END, ctx.getStop());
	}

	@Override
	public void enterLocalVariableDeclaration(LocalVariableDeclarationContext ctx) {
	}

	@Override
	public void exitLocalVariableDeclaration(LocalVariableDeclarationContext ctx) {
	}

	@Override
	public void enterTypeList(TypeListContext ctx) {
	}

	@Override
	public void exitTypeList(TypeListContext ctx) {

	}

	@Override
	public void enterTypeParameter(TypeParameterContext ctx) {

	}

	@Override
	public void exitTypeParameter(TypeParameterContext ctx) {

	}

	@Override
	public void enterVariableDeclaratorId(VariableDeclaratorIdContext ctx) {

	}

	@Override
	public void exitVariableDeclaratorId(VariableDeclaratorIdContext ctx) {

	}

	@Override
	public void enterExplicitConstructorInvocation(ExplicitConstructorInvocationContext ctx) {
		jplagParser.add(J_APPLY, ctx.start);
	}

	@Override
	public void exitExplicitConstructorInvocation(ExplicitConstructorInvocationContext ctx) {

	}

	@Override
	public void enterInterfaceMethodDeclaratorRest(InterfaceMethodDeclaratorRestContext ctx) {

	}

	@Override
	public void exitInterfaceMethodDeclaratorRest(InterfaceMethodDeclaratorRestContext ctx) {

	}

	@Override
	public void enterElementValue(ElementValueContext ctx) {

	}

	@Override
	public void exitElementValue(ElementValueContext ctx) {

	}

	@Override
	public void enterCompilationUnit(CompilationUnitContext ctx) {

	}

	@Override
	public void exitCompilationUnit(CompilationUnitContext ctx) {

	}

	@Override
	public void enterStatementExpression(StatementExpressionContext ctx) {

	}

	@Override
	public void exitStatementExpression(StatementExpressionContext ctx) {

	}

	@Override
	public void enterClassOrInterfaceType(ClassOrInterfaceTypeContext ctx) {

	}

	@Override
	public void exitClassOrInterfaceType(ClassOrInterfaceTypeContext ctx) {

	}

	@Override
	public void enterFormalParameterDeclarations(FormalParameterDeclarationsContext ctx) {

	}

	@Override
	public void exitFormalParameterDeclarations(FormalParameterDeclarationsContext ctx) {

	}

	@Override
	public void enterBlock(BlockContext ctx) {

	}

	@Override
	public void exitBlock(BlockContext ctx) {

	}

	@Override
	public void enterVariableInitializer(VariableInitializerContext ctx) {
		if (ctx.parent instanceof ArrayInitializerContext) {
			// dont print assignment, as this is part of an array initialization
		} else {
			jplagParser.add(J_ASSIGN, ctx.getStart());
		}
	}

	@Override
	public void exitVariableInitializer(VariableInitializerContext ctx) {

	}

	@Override
	public void enterBlockStatement(BlockStatementContext ctx) {

	}

	@Override
	public void exitBlockStatement(BlockStatementContext ctx) {

	}

	@Override
	public void enterIntegerLiteral(IntegerLiteralContext ctx) {

	}

	@Override
	public void exitIntegerLiteral(IntegerLiteralContext ctx) {

	}

	@Override
	public void enterInterfaceMemberDecl(InterfaceMemberDeclContext ctx) {

	}

	@Override
	public void exitInterfaceMemberDecl(InterfaceMemberDeclContext ctx) {

	}

	@Override
	public void enterCreator(CreatorContext ctx) {
		if (ctx.classCreatorRest() != null) {
			if (// @formatter:off
				// "normal" generic
				ctx.createdName().typeArguments().size() > 0 
				// allow diamond operator 
				|| ctx.createdName().children.size() > 1 && (ctx.createdName().getChild(1).getText().equals("<") && ctx.createdName().getChild(2).getText().equals(">"))
				|| ctx.createdName().children.size() > 3 && (ctx.createdName().getChild(3).getText().equals("<") && ctx.createdName().getChild(4).getText().equals(">"))) {
			    // @formatter: on
				jplagParser.add(J_GENERIC, ctx.start);
			}
			jplagParser.add(J_NEWCLASS, ctx.start);
		} else if (ctx.arrayCreatorRest() != null) {
			jplagParser.add(J_NEWARRAY, ctx.start);

		}

	}

	@Override
	public void exitCreator(CreatorContext ctx) {

	}

	@Override
	public void enterConstantDeclaratorsRest(ConstantDeclaratorsRestContext ctx) {

	}

	@Override
	public void exitConstantDeclaratorsRest(ConstantDeclaratorsRestContext ctx) {

	}

	@Override
	public void enterInterfaceGenericMethodDecl(InterfaceGenericMethodDeclContext ctx) {

	}

	@Override
	public void exitInterfaceGenericMethodDecl(InterfaceGenericMethodDeclContext ctx) {

	}

	@Override
	public void enterInterfaceMethodOrFieldDecl(InterfaceMethodOrFieldDeclContext ctx) {

	}

	@Override
	public void exitInterfaceMethodOrFieldDecl(InterfaceMethodOrFieldDeclContext ctx) {

	}

	@Override
	public void enterTryStatement(TryStatementContext ctx) {
		jplagParser.add(J_TRY_BEGIN, ctx.getStart());
	}

	@Override
	public void exitTryStatement(TryStatementContext ctx) {
		if (hasFinally(ctx)) {
			jplagParser.add(J_FINALLY, ctx.start);
		}
	}

	private boolean hasFinally(TryStatementContext ctx) {
		for (ParseTree pt : ctx.children) {
			if (pt instanceof TerminalNode) {
				if (((TerminalNode) pt).getText().equals("finally")) {
					return true;
				}
			}
		}
		
		return false;
	}

	@Override
	public void enterFieldDeclaration(FieldDeclarationContext ctx) {

	}

	@Override
	public void exitFieldDeclaration(FieldDeclarationContext ctx) {

	}

	@Override
	public void enterNormalInterfaceDeclaration(NormalInterfaceDeclarationContext ctx) {
		jplagParser.add(J_INTERFACE_BEGIN, ctx.start);
	}

	@Override
	public void exitNormalInterfaceDeclaration(NormalInterfaceDeclarationContext ctx) {
		jplagParser.add(J_INTERFACE_END, ctx.start);
	}

	@Override
	public void enterExplicitGenericInvocation(ExplicitGenericInvocationContext ctx) {
		jplagParser.add(J_APPLY, ctx.start);
	}

	@Override
	public void exitExplicitGenericInvocation(ExplicitGenericInvocationContext ctx) {

	}

	@Override
	public void enterMethodDeclaration(MethodDeclarationContext ctx) {
		if (ctx.start.getText().equals("void")) {
			jplagParser.add(J_VOID, ctx.start);
		}
		jplagParser.add(J_METHOD_BEGIN, ctx.start);
	}

	@Override
	public void exitMethodDeclaration(MethodDeclarationContext ctx) {
		jplagParser.add(J_METHOD_END, ctx.getStop());
	}

	@Override
	public void enterParExpression(ParExpressionContext ctx) {

	}

	@Override
	public void exitParExpression(ParExpressionContext ctx) {

	}

	@Override
	public void enterSwitchLabel(SwitchLabelContext ctx) {

	}

	@Override
	public void exitSwitchLabel(SwitchLabelContext ctx) {
		jplagParser.add(J_CASE, ctx.getStop());
	}

	@Override
	public void enterConstructorDeclaration(ConstructorDeclarationContext ctx) {
		jplagParser.add(J_CONSTR_BEGIN, ctx.getStart());
	}

	@Override
	public void exitConstructorDeclaration(ConstructorDeclarationContext ctx) {
		jplagParser.add(J_CONSTR_END, ctx.getStop());
	}

	@Override
	public void enterTypeParameters(TypeParametersContext ctx) {

	}

	@Override
	public void exitTypeParameters(TypeParametersContext ctx) {

	}

	@Override
	public void enterAnnotationTypeElement(AnnotationTypeElementContext ctx) {

	}

	@Override
	public void exitAnnotationTypeElement(AnnotationTypeElementContext ctx) {

	}

	@Override
	public void enterResource(ResourceContext ctx) {
		jplagParser.add(J_TRY_WITH_RESOURCE, ctx.getStart());
	}

	@Override
	public void exitResource(ResourceContext ctx) {

	}

	@Override
	public void enterClassDeclaration(ClassDeclarationContext ctx) {
	}

	@Override
	public void exitClassDeclaration(ClassDeclarationContext ctx) {
	}

	@Override
	public void enterElementValuePair(ElementValuePairContext ctx) {

	}

	@Override
	public void exitElementValuePair(ElementValuePairContext ctx) {

	}

	@Override
	public void enterBooleanLiteral(BooleanLiteralContext ctx) {

	}

	@Override
	public void exitBooleanLiteral(BooleanLiteralContext ctx) {

	}

	@Override
	public void enterVoidInterfaceMethodDeclaratorRest(VoidInterfaceMethodDeclaratorRestContext ctx) {

	}

	@Override
	public void exitVoidInterfaceMethodDeclaratorRest(VoidInterfaceMethodDeclaratorRestContext ctx) {

	}

	@Override
	public void enterInterfaceMethodOrFieldRest(InterfaceMethodOrFieldRestContext ctx) {

	}

	@Override
	public void exitInterfaceMethodOrFieldRest(InterfaceMethodOrFieldRestContext ctx) {

	}

	@Override
	public void enterTypeName(TypeNameContext ctx) {

	}

	@Override
	public void exitTypeName(TypeNameContext ctx) {

	}

	@Override
	public void enterArguments(ArgumentsContext ctx) {
	}

	@Override
	public void exitArguments(ArgumentsContext ctx) {

	}

	@Override
	public void enterMethodBody(MethodBodyContext ctx) {

	}

	@Override
	public void exitMethodBody(MethodBodyContext ctx) {

	}

	@Override
	public void enterArrayInitializer(ArrayInitializerContext ctx) {
		jplagParser.add(J_ARRAY_INIT_BEGIN, ctx.getStart());
	}

	@Override
	public void exitArrayInitializer(ArrayInitializerContext ctx) {
		jplagParser.add(J_ARRAY_INIT_END, ctx.getStop());
	}

	@Override
	public void enterFormalParameters(FormalParametersContext ctx) {

	}

	@Override
	public void exitFormalParameters(FormalParametersContext ctx) {

	}

	@Override
	public void enterPrimitiveType(PrimitiveTypeContext ctx) {

	}

	@Override
	public void exitPrimitiveType(PrimitiveTypeContext ctx) {

	}

	@Override
	public void enterNonWildcardTypeArguments(NonWildcardTypeArgumentsContext ctx) {

	}

	@Override
	public void exitNonWildcardTypeArguments(NonWildcardTypeArgumentsContext ctx) {

	}

	@Override
	public void enterTypeArgument(TypeArgumentContext ctx) {
		if ( ctx.parent.parent instanceof CreatedNameContext) {	
			// the generic token has already been emitted by the class emitter
		} else {
			jplagParser.add(J_GENERIC, ctx.getStart());
		}
	}

	@Override
	public void exitTypeArgument(TypeArgumentContext ctx) {

	}

	@Override
	public void enterClassOrInterfaceDeclaration(ClassOrInterfaceDeclarationContext ctx) {

	}

	@Override
	public void exitClassOrInterfaceDeclaration(ClassOrInterfaceDeclarationContext ctx) {

	}

	@Override
	public void enterForInit(ForInitContext ctx) {

	}

	@Override
	public void exitForInit(ForInitContext ctx) {

	}

	@Override
	public void enterArrayCreatorRest(ArrayCreatorRestContext ctx) {

	}

	@Override
	public void exitArrayCreatorRest(ArrayCreatorRestContext ctx) {

	}

	@Override
	public void enterBound(BoundContext ctx) {

	}

	@Override
	public void exitBound(BoundContext ctx) {

	}

	@Override
	public void enterSwitchBlockStatementGroup(SwitchBlockStatementGroupContext ctx) {

	}

	@Override
	public void exitSwitchBlockStatementGroup(SwitchBlockStatementGroupContext ctx) {

	}

	@Override
	public void enterLiteral(LiteralContext ctx) {

	}

	@Override
	public void exitLiteral(LiteralContext ctx) {

	}

	@Override
	public void enterDoWhileStmt(DoWhileStmtContext ctx) {
		jplagParser.add(J_DO_BEGIN, ctx.getStart());
	}

	@Override
	public void exitDoWhileStmt(DoWhileStmtContext ctx) {
		jplagParser.add(J_DO_END, ctx.getStop());
	}

	@Override
	public void enterBreak(BreakContext ctx) {
		jplagParser.add(J_BREAK, ctx.getStart());
	}

	@Override
	public void exitBreak(BreakContext ctx) {

	}

	@Override
	public void enterIfStmt(IfStmtContext ctx) {
		jplagParser.add(J_IF_BEGIN, ctx.getStart());
	}

	@Override
	public void exitIfStmt(IfStmtContext ctx) {
		jplagParser.add(J_IF_END, ctx.getStop());
	}

	@Override
	public void enterThrowStmt(ThrowStmtContext ctx) {
		jplagParser.add(J_THROW, ctx.getStart());
	}

	@Override
	public void exitThrowStmt(ThrowStmtContext ctx) {
	}

	@Override
	public void enterSynchronizedStmt(SynchronizedStmtContext ctx) {
		jplagParser.add(J_SYNC_BEGIN, ctx.getStart());
	}

	@Override
	public void exitSynchronizedStmt(SynchronizedStmtContext ctx) {
		jplagParser.add(J_SYNC_END, ctx.getStop());
	}

	@Override
	public void enterStatementExpressStmt(StatementExpressStmtContext ctx) {
	}

	@Override
	public void exitStatementExpressStmt(StatementExpressStmtContext ctx) {

	}

	@Override
	public void enterBlockStmt(BlockStmtContext ctx) {

	}

	@Override
	public void exitBlockStmt(BlockStmtContext ctx) {

	}

	@Override
	public void enterReturnStmt(ReturnStmtContext ctx) {
		jplagParser.add(J_RETURN, ctx.getStart());
	}

	@Override
	public void exitReturnStmt(ReturnStmtContext ctx) {

	}

	@Override
	public void enterSemicStmt(SemicStmtContext ctx) {

	}

	@Override
	public void exitSemicStmt(SemicStmtContext ctx) {

	}

	@Override
	public void enterIdentifiedStmt(IdentifiedStmtContext ctx) {

	}

	@Override
	public void exitIdentifiedStmt(IdentifiedStmtContext ctx) {

	}

	@Override
	public void enterTryStmt(TryStmtContext ctx) {
		// nothing happens here see enterTryStatement
	}

	@Override
	public void exitTryStmt(TryStmtContext ctx) {
		// nothing happens here see exitTryStatement
	}

	@Override
	public void enterContinueStmt(ContinueStmtContext ctx) {
		jplagParser.add(J_CONTINUE, ctx.getStart());
	}

	@Override
	public void exitContinueStmt(ContinueStmtContext ctx) {
	}

	@Override
	public void enterAssertStmt(AssertStmtContext ctx) {
		jplagParser.add(J_ASSERT, ctx.getStart());
	}

	@Override
	public void exitAssertStmt(AssertStmtContext ctx) {
	}

	@Override
	public void enterSwitchStmt(SwitchStmtContext ctx) {
		jplagParser.add(J_SWITCH_BEGIN, ctx.getStart());
	}

	@Override
	public void exitSwitchStmt(SwitchStmtContext ctx) {
		jplagParser.add(J_SWITCH_END, ctx.getStop());
	}

	@Override
	public void enterForStmt(ForStmtContext ctx) {
		jplagParser.add(J_FOR_BEGIN, ctx.getStart());
	}

	@Override
	public void exitForStmt(ForStmtContext ctx) {
		jplagParser.add(J_FOR_END, ctx.getStop());
	}

	@Override
	public void enterWhileStmt(WhileStmtContext ctx) {
		jplagParser.add(J_WHILE_BEGIN, ctx.getStart());
	}

	@Override
	public void exitWhileStmt(WhileStmtContext ctx) {
		jplagParser.add(J_WHILE_END, ctx.getStop());
	}

	@Override
	public void enterExprBinaryOperatorInstanceof(ExprBinaryOperatorInstanceofContext ctx) {

	}

	@Override
	public void exitExprBinaryOperatorInstanceof(ExprBinaryOperatorInstanceofContext ctx) {

	}

	@Override
	public void enterExprBinaryOperatorGT(ExprBinaryOperatorGTContext ctx) {

	}

	@Override
	public void exitExprBinaryOperatorGT(ExprBinaryOperatorGTContext ctx) {

	}

	@Override
	public void enterExprNotExpression(ExprNotExpressionContext ctx) {

	}

	@Override
	public void exitExprNotExpression(ExprNotExpressionContext ctx) {

	}

	@Override
	public void enterExprNewCreator(ExprNewCreatorContext ctx) {

	}

	@Override
	public void exitExprNewCreator(ExprNewCreatorContext ctx) {

	}

	@Override
	public void enterExprBinaryOperatorAdd(ExprBinaryOperatorAddContext ctx) {

	}

	@Override
	public void exitExprBinaryOperatorAdd(ExprBinaryOperatorAddContext ctx) {

	}

	@Override
	public void enterExprBinaryOperatorMult(ExprBinaryOperatorMultContext ctx) {

	}

	@Override
	public void exitExprBinaryOperatorMult(ExprBinaryOperatorMultContext ctx) {

	}

	@Override
	public void enterExprPrimary(ExprPrimaryContext ctx) {

	}

	@Override
	public void exitExprPrimary(ExprPrimaryContext ctx) {

	}

	@Override
	public void enterExprExplicitGenericInvocation(ExprExplicitGenericInvocationContext ctx) {

	}

	@Override
	public void exitExprExplicitGenericInvocation(ExprExplicitGenericInvocationContext ctx) {

	}

	@Override
	public void enterExprSuperIdentifier(ExprSuperIdentifierContext ctx) {

	}

	@Override
	public void exitExprSuperIdentifier(ExprSuperIdentifierContext ctx) {

	}

	@Override
	public void enterExprSuper(ExprSuperContext ctx) {

	}

	@Override
	public void exitExprSuper(ExprSuperContext ctx) {

	}

	@Override
	public void enterExprThis(ExprThisContext ctx) {

	}

	@Override
	public void exitExprThis(ExprThisContext ctx) {

	}

	@Override
	public void enterExprBinaryBoolAnd(ExprBinaryBoolAndContext ctx) {

	}

	@Override
	public void exitExprBinaryBoolAnd(ExprBinaryBoolAndContext ctx) {

	}

	@Override
	public void enterExprBinaryAnd(ExprBinaryAndContext ctx) {

	}

	@Override
	public void exitExprBinaryAnd(ExprBinaryAndContext ctx) {

	}

	@Override
	public void enterExprIncDecExpression(ExprIncDecExpressionContext ctx) {

	}

	@Override
	public void exitExprIncDecExpression(ExprIncDecExpressionContext ctx) {

	}

	@Override
	public void enterExprBinaryBoolOr(ExprBinaryBoolOrContext ctx) {

	}

	@Override
	public void exitExprBinaryBoolOr(ExprBinaryBoolOrContext ctx) {

	}

	@Override
	public void enterExprBinaryEquals(ExprBinaryEqualsContext ctx) {

	}

	@Override
	public void exitExprBinaryEquals(ExprBinaryEqualsContext ctx) {

	}

	@Override
	public void enterExprAssignment(ExprAssignmentContext ctx) {
		jplagParser.add(J_ASSIGN, ctx.getStart());
	}

	@Override
	public void exitExprAssignment(ExprAssignmentContext ctx) {

	}

	@Override
	public void enterExprBinaryNot(ExprBinaryNotContext ctx) {

	}

	@Override
	public void exitExprBinaryNot(ExprBinaryNotContext ctx) {

	}

	@Override
	public void enterExprNewIdentidier(ExprNewIdentidierContext ctx) {

	}

	@Override
	public void exitExprNewIdentidier(ExprNewIdentidierContext ctx) {

	}

	@Override
	public void enterExprMethodExpressionList(ExprMethodExpressionListContext ctx) {
		jplagParser.add(J_APPLY, ctx.getStart());
	}

	@Override
	public void exitExprMethodExpressionList(ExprMethodExpressionListContext ctx) {

	}

	@Override
	public void enterExprCastExpression(ExprCastExpressionContext ctx) {

	}

	@Override
	public void exitExprCastExpression(ExprCastExpressionContext ctx) {

	}

	@Override
	public void enterExprBinaryOperatorComp(ExprBinaryOperatorCompContext ctx) {

	}

	@Override
	public void exitExprBinaryOperatorComp(ExprBinaryOperatorCompContext ctx) {

	}

	@Override
	public void enterExprBinaryOr(ExprBinaryOrContext ctx) {

	}

	@Override
	public void exitExprBinaryOr(ExprBinaryOrContext ctx) {

	}

	@Override
	public void enterExprExpressionIncDec(ExprExpressionIncDecContext ctx) {

	}

	@Override
	public void exitExprExpressionIncDec(ExprExpressionIncDecContext ctx) {

	}

	@Override
	public void enterExprArrayExpression(ExprArrayExpressionContext ctx) {

	}

	@Override
	public void exitExprArrayExpression(ExprArrayExpressionContext ctx) {

	}

	@Override
	public void enterExprIdentifier(ExprIdentifierContext ctx) {

	}

	@Override
	public void exitExprIdentifier(ExprIdentifierContext ctx) {

	}

	@Override
	public void enterCbdMember(CbdMemberContext ctx) {

	}

	@Override
	public void exitCbdMember(CbdMemberContext ctx) {

	}

	@Override
	public void enterCbdSemicolon(CbdSemicolonContext ctx) {

	}

	@Override
	public void exitCbdSemicolon(CbdSemicolonContext ctx) {

	}

	@Override
	public void enterCbdBlock(CbdBlockContext ctx) {
		jplagParser.add(J_INIT_BEGIN, ctx.getStart());
	}

	@Override
	public void exitCbdBlock(CbdBlockContext ctx) {
		jplagParser.add(J_INIT_END, ctx.getStop());
	}

	@Override
	public void enterExprConditionalExpression(ExprConditionalExpressionContext ctx) {
		jplagParser.add(J_COND, ctx.getStart());
	}

	@Override
	public void exitExprConditionalExpression(ExprConditionalExpressionContext ctx) {
	}
}
