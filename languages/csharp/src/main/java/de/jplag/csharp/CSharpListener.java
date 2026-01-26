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
import static de.jplag.csharp.CSharpTokenType.ENUM;
import static de.jplag.csharp.CSharpTokenType.ENUMERAL;
import static de.jplag.csharp.CSharpTokenType.ENUM_BEGIN;
import static de.jplag.csharp.CSharpTokenType.ENUM_END;
import static de.jplag.csharp.CSharpTokenType.EVENT;
import static de.jplag.csharp.CSharpTokenType.FIELD;
import static de.jplag.csharp.CSharpTokenType.FINALLY;
import static de.jplag.csharp.CSharpTokenType.FIXED;
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
import static de.jplag.csharp.CSharpTokenType.LOOP_BEGIN;
import static de.jplag.csharp.CSharpTokenType.LOOP_END;
import static de.jplag.csharp.CSharpTokenType.METHOD;
import static de.jplag.csharp.CSharpTokenType.METHOD_BEGIN;
import static de.jplag.csharp.CSharpTokenType.METHOD_END;
import static de.jplag.csharp.CSharpTokenType.NAMESPACE;
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
import static de.jplag.csharp.CSharpTokenType.USING_DIRECTIVE;
import static de.jplag.csharp.grammar.CSharpParser.ELSE;
import static de.jplag.csharp.grammar.CSharpParser.OP_ADD_ASSIGNMENT;
import static de.jplag.csharp.grammar.CSharpParser.OP_AND_ASSIGNMENT;
import static de.jplag.csharp.grammar.CSharpParser.OP_COALESCING_ASSIGNMENT;
import static de.jplag.csharp.grammar.CSharpParser.OP_DEC;
import static de.jplag.csharp.grammar.CSharpParser.OP_DIV_ASSIGNMENT;
import static de.jplag.csharp.grammar.CSharpParser.OP_INC;
import static de.jplag.csharp.grammar.CSharpParser.OP_MOD_ASSIGNMENT;
import static de.jplag.csharp.grammar.CSharpParser.OP_MULT_ASSIGNMENT;
import static de.jplag.csharp.grammar.CSharpParser.OP_OR_ASSIGNMENT;
import static de.jplag.csharp.grammar.CSharpParser.OP_SUB_ASSIGNMENT;
import static de.jplag.csharp.grammar.CSharpParser.OP_XOR_ASSIGNMENT;

import org.antlr.v4.runtime.ParserRuleContext;

import de.jplag.antlr.AbstractAntlrListener;
import de.jplag.csharp.grammar.CSharpParser.Accessor_bodyContext;
import de.jplag.csharp.grammar.CSharpParser.Accessor_declarationsContext;
import de.jplag.csharp.grammar.CSharpParser.Array_initializerContext;
import de.jplag.csharp.grammar.CSharpParser.Assignment_operatorContext;
import de.jplag.csharp.grammar.CSharpParser.AttributeContext;
import de.jplag.csharp.grammar.CSharpParser.BodyContext;
import de.jplag.csharp.grammar.CSharpParser.BreakStatementContext;
import de.jplag.csharp.grammar.CSharpParser.CheckedExpressionContext;
import de.jplag.csharp.grammar.CSharpParser.CheckedStatementContext;
import de.jplag.csharp.grammar.CSharpParser.Class_bodyContext;
import de.jplag.csharp.grammar.CSharpParser.Class_definitionContext;
import de.jplag.csharp.grammar.CSharpParser.Common_member_declarationContext;
import de.jplag.csharp.grammar.CSharpParser.Constant_declarationContext;
import de.jplag.csharp.grammar.CSharpParser.Constructor_declarationContext;
import de.jplag.csharp.grammar.CSharpParser.ContinueStatementContext;
import de.jplag.csharp.grammar.CSharpParser.Conversion_operator_declaratorContext;
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
import de.jplag.csharp.grammar.CSharpParser.Interface_definitionContext;
import de.jplag.csharp.grammar.CSharpParser.Local_constant_declarationContext;
import de.jplag.csharp.grammar.CSharpParser.Local_variable_declarationContext;
import de.jplag.csharp.grammar.CSharpParser.LockStatementContext;
import de.jplag.csharp.grammar.CSharpParser.Method_bodyContext;
import de.jplag.csharp.grammar.CSharpParser.Method_declarationContext;
import de.jplag.csharp.grammar.CSharpParser.Method_invocationContext;
import de.jplag.csharp.grammar.CSharpParser.Namespace_bodyContext;
import de.jplag.csharp.grammar.CSharpParser.Namespace_declarationContext;
import de.jplag.csharp.grammar.CSharpParser.Namespace_or_type_nameContext;
import de.jplag.csharp.grammar.CSharpParser.Object_creation_expressionContext;
import de.jplag.csharp.grammar.CSharpParser.Operator_declarationContext;
import de.jplag.csharp.grammar.CSharpParser.Property_declarationContext;
import de.jplag.csharp.grammar.CSharpParser.ReturnStatementContext;
import de.jplag.csharp.grammar.CSharpParser.Shift_expressionContext;
import de.jplag.csharp.grammar.CSharpParser.Specific_catch_clauseContext;
import de.jplag.csharp.grammar.CSharpParser.Struct_bodyContext;
import de.jplag.csharp.grammar.CSharpParser.Struct_definitionContext;
import de.jplag.csharp.grammar.CSharpParser.SwitchStatementContext;
import de.jplag.csharp.grammar.CSharpParser.Switch_sectionContext;
import de.jplag.csharp.grammar.CSharpParser.ThrowStatementContext;
import de.jplag.csharp.grammar.CSharpParser.TryStatementContext;
import de.jplag.csharp.grammar.CSharpParser.Type_argument_listContext;
import de.jplag.csharp.grammar.CSharpParser.UncheckedExpressionContext;
import de.jplag.csharp.grammar.CSharpParser.UncheckedStatementContext;
import de.jplag.csharp.grammar.CSharpParser.UnsafeStatementContext;
import de.jplag.csharp.grammar.CSharpParser.UsingAliasDirectiveContext;
import de.jplag.csharp.grammar.CSharpParser.UsingNamespaceDirectiveContext;
import de.jplag.csharp.grammar.CSharpParser.UsingStaticDirectiveContext;
import de.jplag.csharp.grammar.CSharpParser.WhileStatementContext;

/**
 * Extracts tokens for the {@link CSharpLanguage}.
 */
public class CSharpListener extends AbstractAntlrListener {
    /**
     * Creates the listener.
     */
    public CSharpListener() {
        visit(UsingNamespaceDirectiveContext.class).map(USING_DIRECTIVE);
        visit(UsingAliasDirectiveContext.class, this::isAliasUsageAlsoImport).map(USING_DIRECTIVE);
        visit(UsingStaticDirectiveContext.class).map(USING_DIRECTIVE);

        visit(Method_invocationContext.class).map(INVOCATION);
        visit(Object_creation_expressionContext.class).map(OBJECT_CREATION);
        visit(Array_initializerContext.class).map(ARRAY_CREATION);
        visit(Assignment_operatorContext.class).map(ASSIGNMENT);
        visit(IfStatementContext.class).map(IF);
        visit(ELSE).map(IF);
        visit(If_bodyContext.class).map(IF_BEGIN, IF_END);
        visit(Switch_sectionContext.class).map(SWITCH_BEGIN, SWITCH_END);
        visit(SwitchStatementContext.class).map(CASE);

        visit(DoStatementContext.class).map(LOOP_BEGIN, LOOP_END);
        visit(WhileStatementContext.class).map(LOOP_BEGIN, LOOP_END);
        visit(ForStatementContext.class).map(LOOP_BEGIN, LOOP_END);
        visit(ForeachStatementContext.class).map(LOOP_BEGIN, LOOP_END);

        visit(BreakStatementContext.class).map(BREAK);
        visit(ContinueStatementContext.class).map(CONTINUE);
        visit(GotoStatementContext.class).map(GOTO);

        visit(ReturnStatementContext.class).map(RETURN);
        visit(ThrowStatementContext.class).map(THROW);

        visit(CheckedStatementContext.class).map(CHECKED);
        visit(CheckedExpressionContext.class).map(CHECKED);
        visit(UncheckedStatementContext.class).map(UNCHECKED);
        visit(UncheckedExpressionContext.class).map(UNCHECKED);

        visit(LockStatementContext.class).map(LOCK);

        visit(TryStatementContext.class).map(TRY);
        visit(Specific_catch_clauseContext.class).map(CATCH);
        visit(Finally_clauseContext.class).map(FINALLY);

        visit(Namespace_declarationContext.class).map(NAMESPACE);
        visit(Namespace_bodyContext.class).map(NAMESPACE_BEGIN, NAMESPACE_END);
        visit(Class_definitionContext.class).map(CLASS);
        visit(Class_bodyContext.class, this::isClassBody).map(CLASS_BEGIN, CLASS_END);
        visit(Method_declarationContext.class).map(METHOD);
        visit(Method_bodyContext.class).map(METHOD_BEGIN, METHOD_END);
        visit(Property_declarationContext.class).map(PROPERTY);
        visit(Event_declarationContext.class).map(EVENT);
        visit(Indexer_declarationContext.class).map(INDEXER);
        visit(Operator_declarationContext.class).map(OPERATOR);
        visit(BodyContext.class, this::isOperatorBody).map(METHOD_BEGIN, METHOD_END);
        visit(Conversion_operator_declaratorContext.class).map(OPERATOR);
        visit(BodyContext.class, this::isConversionOperatorBody).map(METHOD_BEGIN, METHOD_END);
        visit(Constructor_declarationContext.class).map(CONSTRUCTOR);
        visit(BodyContext.class, this::isConstructorBody).map(METHOD_BEGIN, METHOD_END);
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
        visit(Local_constant_declarationContext.class).map(LOCAL_VARIABLE);

        registerUnaryOperatorVisitors();
    }

    private void registerUnaryOperatorVisitors() {
        visit(OP_DEC).map(ASSIGNMENT);
        visit(OP_INC).map(ASSIGNMENT);

        visit(Shift_expressionContext.class, expr -> expr.additive_expression().size() == 2).map(ASSIGNMENT);

        visit(OP_ADD_ASSIGNMENT).map(ASSIGNMENT);
        visit(OP_SUB_ASSIGNMENT).map(ASSIGNMENT);
        visit(OP_AND_ASSIGNMENT).map(ASSIGNMENT);
        visit(OP_DIV_ASSIGNMENT).map(ASSIGNMENT);
        visit(OP_MOD_ASSIGNMENT).map(ASSIGNMENT);
        visit(OP_OR_ASSIGNMENT).map(ASSIGNMENT);
        visit(OP_XOR_ASSIGNMENT).map(ASSIGNMENT);
        visit(OP_MULT_ASSIGNMENT).map(ASSIGNMENT);
        visit(OP_COALESCING_ASSIGNMENT).map(ASSIGNMENT);
    }

    private boolean isClassBody(ParserRuleContext context) {
        return hasAncestor(context, Class_definitionContext.class, Interface_definitionContext.class);
    }

    private boolean isInterfaceBody(ParserRuleContext context) {
        return hasAncestor(context, Interface_definitionContext.class, Class_definitionContext.class);
    }

    private boolean isConstructorBody(BodyContext context) {
        return context.parent instanceof Constructor_declarationContext;
    }

    private boolean isOperatorBody(BodyContext context) {
        return context.parent instanceof Operator_declarationContext;
    }

    private boolean isConversionOperatorBody(BodyContext context) {
        if (!(context.parent instanceof Common_member_declarationContext parent)) {
            return false;
        }

        return parent.conversion_operator_declarator() != null;
    }

    private boolean isAliasUsageAlsoImport(UsingAliasDirectiveContext context) {
        Namespace_or_type_nameContext namespace = getDescendant(context, Namespace_or_type_nameContext.class);

        return namespace != null && !namespace.DOT().isEmpty() && getDescendant(namespace, Type_argument_listContext.class) == null;
    }
}
