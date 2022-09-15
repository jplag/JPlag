package de.jplag.csharp;

import static de.jplag.csharp.CSharpTokenType.ACCESSORS_BEGIN;
import static de.jplag.csharp.CSharpTokenType.ACCESSORS_END;
import static de.jplag.csharp.CSharpTokenType.ACCESSOR_BEGIN;
import static de.jplag.csharp.CSharpTokenType.ACCESSOR_END;
import static de.jplag.csharp.CSharpTokenType.ARRAY_CREATION;
import static de.jplag.csharp.CSharpTokenType.ASSIGNMENT;
import static de.jplag.csharp.CSharpTokenType.ATTRIBUTE;
import static de.jplag.csharp.CSharpTokenType.BREAK;
import static de.jplag.csharp.CSharpTokenType.CASE;
import static de.jplag.csharp.CSharpTokenType.CATCH;
import static de.jplag.csharp.CSharpTokenType.CHECKED;
import static de.jplag.csharp.CSharpTokenType.CLASS;
import static de.jplag.csharp.CSharpTokenType.CLASS_BEGIN;
import static de.jplag.csharp.CSharpTokenType.CLASS_END;
import static de.jplag.csharp.CSharpTokenType.CONSTANT;
import static de.jplag.csharp.CSharpTokenType.CONSTRUCTOR;
import static de.jplag.csharp.CSharpTokenType.CONTINUE;
import static de.jplag.csharp.CSharpTokenType.DELEGATE;
import static de.jplag.csharp.CSharpTokenType.DESTRUCTOR;
import static de.jplag.csharp.CSharpTokenType.DO;
import static de.jplag.csharp.CSharpTokenType.ENUM;
import static de.jplag.csharp.CSharpTokenType.ENUMERAL;
import static de.jplag.csharp.CSharpTokenType.ENUM_BEGIN;
import static de.jplag.csharp.CSharpTokenType.ENUM_END;
import static de.jplag.csharp.CSharpTokenType.EVENT;
import static de.jplag.csharp.CSharpTokenType.FIELD;
import static de.jplag.csharp.CSharpTokenType.FINALLY;
import static de.jplag.csharp.CSharpTokenType.FIXED;
import static de.jplag.csharp.CSharpTokenType.FOR;
import static de.jplag.csharp.CSharpTokenType.FOREACH;
import static de.jplag.csharp.CSharpTokenType.GOTO;
import static de.jplag.csharp.CSharpTokenType.IF;
import static de.jplag.csharp.CSharpTokenType.IF_BEGIN;
import static de.jplag.csharp.CSharpTokenType.IF_END;
import static de.jplag.csharp.CSharpTokenType.INDEXER;
import static de.jplag.csharp.CSharpTokenType.INTERFACE;
import static de.jplag.csharp.CSharpTokenType.INTERFACE_BEGIN;
import static de.jplag.csharp.CSharpTokenType.INTERFACE_END;
import static de.jplag.csharp.CSharpTokenType.INVOCATION;
import static de.jplag.csharp.CSharpTokenType.LOCAL_VARIABLE;
import static de.jplag.csharp.CSharpTokenType.LOCK;
import static de.jplag.csharp.CSharpTokenType.METHOD;
import static de.jplag.csharp.CSharpTokenType.METHOD_BEGIN;
import static de.jplag.csharp.CSharpTokenType.METHOD_END;
import static de.jplag.csharp.CSharpTokenType.NAMESPACE_BEGIN;
import static de.jplag.csharp.CSharpTokenType.NAMESPACE_END;
import static de.jplag.csharp.CSharpTokenType.OBJECT_CREATION;
import static de.jplag.csharp.CSharpTokenType.OPERATOR;
import static de.jplag.csharp.CSharpTokenType.PROPERTY;
import static de.jplag.csharp.CSharpTokenType.RETURN;
import static de.jplag.csharp.CSharpTokenType.STRUCT;
import static de.jplag.csharp.CSharpTokenType.STRUCT_BEGIN;
import static de.jplag.csharp.CSharpTokenType.STRUCT_END;
import static de.jplag.csharp.CSharpTokenType.SWITCH_BEGIN;
import static de.jplag.csharp.CSharpTokenType.SWITCH_END;
import static de.jplag.csharp.CSharpTokenType.THROW;
import static de.jplag.csharp.CSharpTokenType.TRY;
import static de.jplag.csharp.CSharpTokenType.UNCHECKED;
import static de.jplag.csharp.CSharpTokenType.UNSAFE;
import static de.jplag.csharp.CSharpTokenType.USING;
import static de.jplag.csharp.CSharpTokenType.USING_DIRECTIVE;
import static de.jplag.csharp.CSharpTokenType.WHILE;

import org.antlr.v4.runtime.Token;

import de.jplag.csharp.grammar.CSharpParser.Accessor_bodyContext;
import de.jplag.csharp.grammar.CSharpParser.Accessor_declarationsContext;
import de.jplag.csharp.grammar.CSharpParser.Array_initializerContext;
import de.jplag.csharp.grammar.CSharpParser.Assignment_operatorContext;
import de.jplag.csharp.grammar.CSharpParser.AttributeContext;
import de.jplag.csharp.grammar.CSharpParser.BreakStatementContext;
import de.jplag.csharp.grammar.CSharpParser.Catch_clausesContext;
import de.jplag.csharp.grammar.CSharpParser.CheckedStatementContext;
import de.jplag.csharp.grammar.CSharpParser.Class_bodyContext;
import de.jplag.csharp.grammar.CSharpParser.Class_definitionContext;
import de.jplag.csharp.grammar.CSharpParser.Constant_declarationContext;
import de.jplag.csharp.grammar.CSharpParser.Constructor_declarationContext;
import de.jplag.csharp.grammar.CSharpParser.ContinueStatementContext;
import de.jplag.csharp.grammar.CSharpParser.Delegate_definitionContext;
import de.jplag.csharp.grammar.CSharpParser.Destructor_definitionContext;
import de.jplag.csharp.grammar.CSharpParser.DoStatementContext;
import de.jplag.csharp.grammar.CSharpParser.Enum_bodyContext;
import de.jplag.csharp.grammar.CSharpParser.Enum_definitionContext;
import de.jplag.csharp.grammar.CSharpParser.Enum_member_declarationContext;
import de.jplag.csharp.grammar.CSharpParser.Event_declarationContext;
import de.jplag.csharp.grammar.CSharpParser.Field_declarationContext;
import de.jplag.csharp.grammar.CSharpParser.Finally_clauseContext;
import de.jplag.csharp.grammar.CSharpParser.FixedStatementContext;
import de.jplag.csharp.grammar.CSharpParser.ForStatementContext;
import de.jplag.csharp.grammar.CSharpParser.ForeachStatementContext;
import de.jplag.csharp.grammar.CSharpParser.GotoStatementContext;
import de.jplag.csharp.grammar.CSharpParser.IfStatementContext;
import de.jplag.csharp.grammar.CSharpParser.If_bodyContext;
import de.jplag.csharp.grammar.CSharpParser.Indexer_declarationContext;
import de.jplag.csharp.grammar.CSharpParser.Interface_bodyContext;
import de.jplag.csharp.grammar.CSharpParser.Interface_definitionContext;
import de.jplag.csharp.grammar.CSharpParser.Local_variable_declarationContext;
import de.jplag.csharp.grammar.CSharpParser.LockStatementContext;
import de.jplag.csharp.grammar.CSharpParser.Method_bodyContext;
import de.jplag.csharp.grammar.CSharpParser.Method_declarationContext;
import de.jplag.csharp.grammar.CSharpParser.Method_invocationContext;
import de.jplag.csharp.grammar.CSharpParser.Namespace_bodyContext;
import de.jplag.csharp.grammar.CSharpParser.Object_creation_expressionContext;
import de.jplag.csharp.grammar.CSharpParser.Operator_declarationContext;
import de.jplag.csharp.grammar.CSharpParser.Property_declarationContext;
import de.jplag.csharp.grammar.CSharpParser.ReturnStatementContext;
import de.jplag.csharp.grammar.CSharpParser.Struct_bodyContext;
import de.jplag.csharp.grammar.CSharpParser.Struct_definitionContext;
import de.jplag.csharp.grammar.CSharpParser.SwitchStatementContext;
import de.jplag.csharp.grammar.CSharpParser.Switch_sectionContext;
import de.jplag.csharp.grammar.CSharpParser.ThrowStatementContext;
import de.jplag.csharp.grammar.CSharpParser.TryStatementContext;
import de.jplag.csharp.grammar.CSharpParser.UncheckedExpressionContext;
import de.jplag.csharp.grammar.CSharpParser.UnsafeStatementContext;
import de.jplag.csharp.grammar.CSharpParser.UsingStatementContext;
import de.jplag.csharp.grammar.CSharpParser.Using_directivesContext;
import de.jplag.csharp.grammar.CSharpParser.WhileStatementContext;
import de.jplag.csharp.grammar.CSharpParserBaseListener;

/**
 * Listener class for visiting the C# ANTLR parse tree. Transforms selected ANTLR token into JPlag tokens.
 * @author Timur Saglam
 */
public class CSharpListener extends CSharpParserBaseListener {

    private final CSharpParserAdapter parserAdapter;

    /**
     * Creates the listener.
     * @param parserAdapter is the JPlag parser adapter which receives the transformed tokens.
     */
    public CSharpListener(CSharpParserAdapter parserAdapter) {
        this.parserAdapter = parserAdapter;
    }

    /**
     * Transforms an ANTLR Token into a JPlag token and transfers it to the token adapter.
     * @param targetType is the type of the JPlag token to be created.
     * @param token is the ANTLR token.
     */
    private void transformToken(CSharpTokenType targetType, Token token) {
        parserAdapter.addToken(targetType, token.getLine(), token.getCharPositionInLine() + 1, token.getText().length());
    }

    @Override
    public void enterMethod_invocation(Method_invocationContext context) {
        transformToken(INVOCATION, context.getStart());
        super.enterMethod_invocation(context);
    }

    @Override
    public void enterObject_creation_expression(Object_creation_expressionContext context) {
        transformToken(OBJECT_CREATION, context.getStart());
        super.enterObject_creation_expression(context);
    }

    @Override
    public void enterArray_initializer(Array_initializerContext context) {
        transformToken(ARRAY_CREATION, context.getStart());
        super.enterArray_initializer(context);
    }

    @Override
    public void enterAssignment_operator(Assignment_operatorContext context) {
        transformToken(ASSIGNMENT, context.getStart());
        super.enterAssignment_operator(context);
    }

    @Override
    public void enterIfStatement(IfStatementContext context) {
        transformToken(IF, context.getStart());
        super.enterIfStatement(context);
    }

    @Override
    public void enterIf_body(If_bodyContext context) {
        transformToken(IF_BEGIN, context.getStart());
        super.enterIf_body(context);
    }

    @Override
    public void exitIf_body(If_bodyContext context) {
        transformToken(IF_END, context.getStop());
        super.exitIf_body(context);
    }

    @Override
    public void enterSwitch_section(Switch_sectionContext context) {
        transformToken(SWITCH_BEGIN, context.getStart());
        super.enterSwitch_section(context);
    }

    @Override
    public void exitSwitch_section(Switch_sectionContext context) {
        transformToken(SWITCH_END, context.getStop());
        super.exitSwitch_section(context);
    }

    @Override
    public void enterSwitchStatement(SwitchStatementContext context) {
        transformToken(CASE, context.getStart());
        super.enterSwitchStatement(context);
    }

    @Override
    public void enterDoStatement(DoStatementContext context) {
        transformToken(DO, context.getStart());
        super.enterDoStatement(context);
    }

    @Override
    public void enterWhileStatement(WhileStatementContext context) {
        transformToken(WHILE, context.getStart());
        super.enterWhileStatement(context);
    }

    @Override
    public void enterForStatement(ForStatementContext context) {
        transformToken(FOR, context.getStart());
        super.enterForStatement(context);
    }

    @Override
    public void enterForeachStatement(ForeachStatementContext context) {
        transformToken(FOREACH, context.getStart());
        super.enterForeachStatement(context);
    }

    @Override
    public void enterBreakStatement(BreakStatementContext context) {
        transformToken(BREAK, context.getStart());
        super.enterBreakStatement(context);
    }

    @Override
    public void enterContinueStatement(ContinueStatementContext context) {
        transformToken(CONTINUE, context.getStart());
        super.enterContinueStatement(context);
    }

    @Override
    public void enterGotoStatement(GotoStatementContext context) {
        transformToken(GOTO, context.getStart());
        super.enterGotoStatement(context);
    }

    @Override
    public void enterReturnStatement(ReturnStatementContext context) {
        transformToken(RETURN, context.getStart());
        super.enterReturnStatement(context);
    }

    @Override
    public void enterThrowStatement(ThrowStatementContext context) {
        transformToken(THROW, context.getStart());
        super.enterThrowStatement(context);
    }

    @Override
    public void enterCheckedStatement(CheckedStatementContext context) {
        transformToken(CHECKED, context.getStart());
        super.enterCheckedStatement(context);
    }

    @Override
    public void enterUncheckedExpression(UncheckedExpressionContext context) {
        transformToken(UNCHECKED, context.getStart());
        super.enterUncheckedExpression(context);
    }

    @Override
    public void enterLockStatement(LockStatementContext context) {
        transformToken(LOCK, context.getStart());
        super.enterLockStatement(context);
    }

    @Override
    public void enterUsingStatement(UsingStatementContext context) {
        transformToken(USING, context.getStart());
        super.enterUsingStatement(context);
    }

    @Override
    public void enterTryStatement(TryStatementContext context) {
        transformToken(TRY, context.getStart());
        super.enterTryStatement(context);
    }

    @Override
    public void enterCatch_clauses(Catch_clausesContext context) {
        transformToken(CATCH, context.getStart());
        super.enterCatch_clauses(context);
    }

    @Override
    public void enterFinally_clause(Finally_clauseContext context) {
        transformToken(FINALLY, context.getStart());
        super.enterFinally_clause(context);
    }

    @Override
    public void enterNamespace_body(Namespace_bodyContext context) {
        transformToken(NAMESPACE_BEGIN, context.getStart());
        super.enterNamespace_body(context);
    }

    @Override
    public void exitNamespace_body(Namespace_bodyContext context) {
        transformToken(NAMESPACE_END, context.getStop());
        super.exitNamespace_body(context);
    }

    @Override
    public void enterUsing_directives(Using_directivesContext context) {
        transformToken(USING_DIRECTIVE, context.getStart());
        super.enterUsing_directives(context);
    }

    @Override
    public void enterClass_definition(Class_definitionContext context) {
        transformToken(CLASS, context.getStart());
        super.enterClass_definition(context);
    }

    @Override
    public void enterClass_body(Class_bodyContext context) {
        transformToken(CLASS_BEGIN, context.getStart());
        super.enterClass_body(context);
    }

    @Override
    public void exitClass_body(Class_bodyContext context) {
        transformToken(CLASS_END, context.getStop());
        super.exitClass_body(context);
    }

    @Override
    public void enterMethod_declaration(Method_declarationContext context) {
        transformToken(METHOD, context.getStart());
        super.enterMethod_declaration(context);
    }

    @Override
    public void enterMethod_body(Method_bodyContext context) {
        transformToken(METHOD_BEGIN, context.getStart());
        super.enterMethod_body(context);
    }

    @Override
    public void exitMethod_body(Method_bodyContext context) {
        transformToken(METHOD_END, context.getStop());
        super.exitMethod_body(context);
    }

    @Override
    public void enterProperty_declaration(Property_declarationContext context) {
        transformToken(PROPERTY, context.getStart());
        super.enterProperty_declaration(context);
    }

    @Override
    public void enterEvent_declaration(Event_declarationContext context) {
        transformToken(EVENT, context.getStart());
        super.enterEvent_declaration(context);
    }

    @Override
    public void enterIndexer_declaration(Indexer_declarationContext context) {
        transformToken(INDEXER, context.getStart());
        super.enterIndexer_declaration(context);
    }

    @Override
    public void enterOperator_declaration(Operator_declarationContext context) {
        transformToken(OPERATOR, context.getStart());
        super.enterOperator_declaration(context);
    }

    @Override
    public void enterConstructor_declaration(Constructor_declarationContext context) {
        transformToken(CONSTRUCTOR, context.getStart());
        super.enterConstructor_declaration(context);
    }

    @Override
    public void enterDestructor_definition(Destructor_definitionContext context) {
        transformToken(DESTRUCTOR, context.getStart());
        super.enterDestructor_definition(context);
    }

    @Override
    public void enterStruct_definition(Struct_definitionContext context) {
        transformToken(STRUCT, context.getStart());
        super.enterStruct_definition(context);
    }

    @Override
    public void enterStruct_body(Struct_bodyContext context) {
        transformToken(STRUCT_BEGIN, context.getStart());
        super.enterStruct_body(context);
    }

    @Override
    public void exitStruct_body(Struct_bodyContext context) {
        transformToken(STRUCT_END, context.getStop());
        super.exitStruct_body(context);
    }

    @Override
    public void enterInterface_definition(Interface_definitionContext context) {
        transformToken(INTERFACE, context.getStart());
        super.enterInterface_definition(context);
    }

    @Override
    public void enterInterface_body(Interface_bodyContext context) {
        transformToken(INTERFACE_BEGIN, context.getStart());
        super.enterInterface_body(context);
    }

    @Override
    public void exitInterface_body(Interface_bodyContext context) {
        transformToken(INTERFACE_END, context.getStart());
        super.exitInterface_body(context);
    }

    @Override
    public void enterEnum_definition(Enum_definitionContext context) {
        transformToken(ENUM, context.getStart());
        super.enterEnum_definition(context);
    }

    @Override
    public void enterEnum_body(Enum_bodyContext context) {
        transformToken(ENUM_BEGIN, context.getStart());
        super.enterEnum_body(context);
    }

    @Override
    public void exitEnum_body(Enum_bodyContext context) {
        transformToken(ENUM_END, context.getStop());
        super.exitEnum_body(context);
    }

    @Override
    public void enterEnum_member_declaration(Enum_member_declarationContext context) {
        transformToken(ENUMERAL, context.getStart());
        super.enterEnum_member_declaration(context);
    }

    @Override
    public void enterAttribute(AttributeContext context) {
        transformToken(ATTRIBUTE, context.getStart());
        super.enterAttribute(context);
    }

    @Override
    public void enterDelegate_definition(Delegate_definitionContext context) {
        transformToken(DELEGATE, context.getStart());
        super.enterDelegate_definition(context);
    }

    @Override
    public void enterUnsafeStatement(UnsafeStatementContext context) {
        transformToken(UNSAFE, context.getStart());
        super.enterUnsafeStatement(context);
    }

    @Override
    public void enterFixedStatement(FixedStatementContext context) {
        transformToken(FIXED, context.getStart());
        super.enterFixedStatement(context);
    }

    @Override
    public void enterAccessor_declarations(Accessor_declarationsContext context) {
        transformToken(ACCESSORS_BEGIN, context.getStart());
        super.enterAccessor_declarations(context);
    }

    @Override
    public void exitAccessor_declarations(Accessor_declarationsContext context) {
        transformToken(ACCESSORS_END, context.getStart());
        super.enterAccessor_declarations(context);
    }

    @Override
    public void enterAccessor_body(Accessor_bodyContext context) {
        transformToken(ACCESSOR_BEGIN, context.getStart());
        super.enterAccessor_body(context);
    }

    @Override
    public void exitAccessor_body(Accessor_bodyContext context) {
        transformToken(ACCESSOR_END, context.getStart());
        super.exitAccessor_body(context);
    }

    @Override
    public void enterConstant_declaration(Constant_declarationContext context) {
        transformToken(CONSTANT, context.getStart());
        super.enterConstant_declaration(context);
    }

    @Override
    public void enterField_declaration(Field_declarationContext context) {
        transformToken(FIELD, context.getStart());
        super.enterField_declaration(context);
    }

    @Override
    public void enterLocal_variable_declaration(Local_variable_declarationContext context) {
        transformToken(LOCAL_VARIABLE, context.getStart());
        super.enterLocal_variable_declaration(context);
    }
}
