package de.jplag.csharp;

import static de.jplag.csharp.CSharpTokenType.*;

import org.antlr.v4.runtime.ParserRuleContext;

import de.jplag.antlr.AbstractAntlrListener;
import de.jplag.csharp.grammar.CSharpParser.*;

public class CSharpListener extends AbstractAntlrListener {
    public CSharpListener() {
        visit(UsingStatementContext.class).map(USING);
        visit(Using_directivesContext.class).map(USING_DIRECTIVE);
        visit(Method_invocationContext.class).map(INVOCATION);
        visit(Object_creation_expressionContext.class).map(OBJECT_CREATION);
        visit(Array_initializerContext.class).map(ARRAY_CREATION);
        visit(Assignment_operatorContext.class).map(ASSIGNMENT);
        visit(IfStatementContext.class).map(IF);
        visit(If_bodyContext.class).map(IF_BEGIN, IF_END);
        visit(Switch_sectionContext.class).map(SWITCH_BEGIN, SWITCH_END);
        visit(SwitchStatementContext.class).map(CASE);

        visit(DoStatementContext.class).map(DO);
        visit(WhileStatementContext.class).map(WHILE);
        visit(ForStatementContext.class).map(FOR);
        visit(ForeachStatementContext.class).map(FOREACH);

        visit(BreakStatementContext.class).map(BREAK);
        visit(ContinueStatementContext.class).map(CONTINUE);
        visit(GotoStatementContext.class).map(GOTO);

        visit(ReturnStatementContext.class).map(RETURN);
        visit(ThrowStatementContext.class).map(THROW);

        visit(CheckedStatementContext.class).map(CHECKED);
        visit(UncheckedExpressionContext.class).map(UNCHECKED);

        visit(LockStatementContext.class).map(LOCK);

        visit(TryStatementContext.class).map(TRY);
        visit(Catch_clausesContext.class).map(CATCH);
        visit(Finally_clauseContext.class).map(FINALLY);

        visit(Namespace_bodyContext.class).map(NAMESPACE_BEGIN, NAMESPACE_END);
        visit(Class_definitionContext.class).map(CLASS);
        visit(Class_bodyContext.class, this::isClassBody).map(CLASS_BEGIN, CLASS_END);
        visit(Method_declarationContext.class).map(METHOD);
        visit(Method_bodyContext.class).map(METHOD_BEGIN, METHOD_END);
        visit(Property_declarationContext.class).map(PROPERTY);
        visit(Event_declarationContext.class).map(EVENT);
        visit(Indexer_declarationContext.class).map(INDEXER);
        visit(Operator_declarationContext.class).map(OPERATOR);
        visit(Constructor_declarationContext.class).map(CONSTRUCTOR);
        visit(Destructor_definitionContext.class).map(DESTRUCTOR);
        visit(Struct_definitionContext.class).map(STRUCT);
        visit(Struct_bodyContext.class).map(STRUCT_BEGIN, STRUCT_END);
        visit(Interface_definitionContext.class).map(INTERFACE);
        visit(Class_bodyContext.class, this::isInterfaceBody).map(INTERFACE_BEGIN, INTERFACE_END);
        visit(Enum_definitionContext.class).map(ENUM);
        visit(Enum_bodyContext.class).map(ENUM_BEGIN, ENUM_END);
        visit(Enum_member_declarationContext.class).map(ENUMERAL);
        visit(AttributeContext.class).map(ATTRIBUTE);
        visit(Delegate_definitionContext.class).map(DELEGATE);
        visit(UnsafeStatementContext.class).map(UNSAFE);
        visit(FixedStatementContext.class).map(FIXED);
        visit(Accessor_declarationsContext.class).map(ACCESSORS_BEGIN, ACCESSORS_END);
        visit(Accessor_bodyContext.class).map(ACCESSOR_BEGIN, ACCESSOR_END);
        visit(Constant_declarationContext.class).map(CONSTANT);
        visit(Field_declarationContext.class).map(FIELD);
        visit(Local_variable_declarationContext.class).map(LOCAL_VARIABLE);
    }

    private boolean isClassBody(ParserRuleContext context) {
        return hasAncestor(context, Class_definitionContext.class, Interface_definitionContext.class);
    }

    private boolean isInterfaceBody(ParserRuleContext context) {
        return hasAncestor(context, Interface_definitionContext.class, Class_definitionContext.class);
    }
}
