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
import static de.jplag.csharp.grammar.CSharpParser.KW_ELSE;
import static de.jplag.csharp.grammar.CSharpParser.TK_MINUS_MINUS;
import static de.jplag.csharp.grammar.CSharpParser.TK_PLUS_PLUS;

import de.jplag.antlr.ContextVisitor;
import de.jplag.csharp.grammar.CSharpParser;
import de.jplag.csharp.grammar.CSharpParser.AssignmentContext;
import de.jplag.csharp.grammar.CSharpParser.Break_statementContext;
import de.jplag.csharp.grammar.CSharpParser.Checked_expressionContext;
import de.jplag.csharp.grammar.CSharpParser.Checked_statementContext;
import de.jplag.csharp.grammar.CSharpParser.Class_declarationContext;
import de.jplag.csharp.grammar.CSharpParser.Constructor_bodyContext;
import de.jplag.csharp.grammar.CSharpParser.Continue_statementContext;
import de.jplag.csharp.grammar.CSharpParser.Delegate_declarationContext;
import de.jplag.csharp.grammar.CSharpParser.Do_statementContext;
import de.jplag.csharp.grammar.CSharpParser.Embedded_statementContext;
import de.jplag.csharp.grammar.CSharpParser.Enum_declarationContext;
import de.jplag.csharp.grammar.CSharpParser.Finalizer_declarationContext;
import de.jplag.csharp.grammar.CSharpParser.Fixed_statementContext;
import de.jplag.csharp.grammar.CSharpParser.For_statementContext;
import de.jplag.csharp.grammar.CSharpParser.Foreach_statementContext;
import de.jplag.csharp.grammar.CSharpParser.Goto_statementContext;
import de.jplag.csharp.grammar.CSharpParser.If_statementContext;
import de.jplag.csharp.grammar.CSharpParser.Interface_bodyContext;
import de.jplag.csharp.grammar.CSharpParser.Interface_declarationContext;
import de.jplag.csharp.grammar.CSharpParser.Lock_statementContext;
import de.jplag.csharp.grammar.CSharpParser.Operator_bodyContext;
import de.jplag.csharp.grammar.CSharpParser.Ref_method_bodyContext;
import de.jplag.csharp.grammar.CSharpParser.Return_statementContext;
import de.jplag.csharp.grammar.CSharpParser.Static_constructor_bodyContext;
import de.jplag.csharp.grammar.CSharpParser.Static_constructor_declarationContext;
import de.jplag.csharp.grammar.CSharpParser.Struct_declarationContext;
import de.jplag.csharp.grammar.CSharpParser.Switch_statementContext;
import de.jplag.csharp.grammar.CSharpParser.Throw_statementContext;
import de.jplag.csharp.grammar.CSharpParser.Try_statementContext;
import de.jplag.csharp.grammar.CSharpParser.Unchecked_expressionContext;
import de.jplag.csharp.grammar.CSharpParser.Unchecked_statementContext;
import de.jplag.csharp.grammar.CSharpParser.Unsafe_statementContext;
import de.jplag.csharp.grammar.CSharpParser.While_statementContext;

import de.jplag.antlr.AbstractAntlrListener;
import de.jplag.csharp.grammar.CSharpParser.Accessor_bodyContext;
import de.jplag.csharp.grammar.CSharpParser.Accessor_declarationsContext;
import de.jplag.csharp.grammar.CSharpParser.Array_initializerContext;
import de.jplag.csharp.grammar.CSharpParser.Assignment_operatorContext;
import de.jplag.csharp.grammar.CSharpParser.AttributeContext;
import de.jplag.csharp.grammar.CSharpParser.Class_bodyContext;
import de.jplag.csharp.grammar.CSharpParser.Constant_declarationContext;
import de.jplag.csharp.grammar.CSharpParser.Constructor_declarationContext;
import de.jplag.csharp.grammar.CSharpParser.Conversion_operator_declaratorContext;
import de.jplag.csharp.grammar.CSharpParser.Enum_bodyContext;
import de.jplag.csharp.grammar.CSharpParser.Enum_member_declarationContext;
import de.jplag.csharp.grammar.CSharpParser.Event_declarationContext;
import de.jplag.csharp.grammar.CSharpParser.Field_declarationContext;
import de.jplag.csharp.grammar.CSharpParser.Finally_clauseContext;
import de.jplag.csharp.grammar.CSharpParser.Indexer_declarationContext;
import de.jplag.csharp.grammar.CSharpParser.Local_constant_declarationContext;
import de.jplag.csharp.grammar.CSharpParser.Local_variable_declarationContext;
import de.jplag.csharp.grammar.CSharpParser.Method_bodyContext;
import de.jplag.csharp.grammar.CSharpParser.Method_declarationContext;
import de.jplag.csharp.grammar.CSharpParser.Namespace_bodyContext;
import de.jplag.csharp.grammar.CSharpParser.Namespace_declarationContext;
import de.jplag.csharp.grammar.CSharpParser.Namespace_or_type_nameContext;
import de.jplag.csharp.grammar.CSharpParser.Object_creation_expressionContext;
import de.jplag.csharp.grammar.CSharpParser.Operator_declarationContext;
import de.jplag.csharp.grammar.CSharpParser.Property_declarationContext;
import de.jplag.csharp.grammar.CSharpParser.Shift_expressionContext;
import de.jplag.csharp.grammar.CSharpParser.Specific_catch_clauseContext;
import de.jplag.csharp.grammar.CSharpParser.Struct_bodyContext;
import de.jplag.csharp.grammar.CSharpParser.Switch_sectionContext;
import de.jplag.csharp.grammar.CSharpParser.Type_argument_listContext;

/**
 * Extracts tokens for the {@link CSharpLanguage}.
 */
public class CSharpListener extends AbstractAntlrListener {
    /**
     * Creates the listener.
     */
    public CSharpListener() {
        visit(CSharpParser.Using_namespace_directiveContext.class).map(USING_DIRECTIVE);
        visit(CSharpParser.Using_alias_directiveContext.class, this::isAliasUsageAlsoImport).map(USING_DIRECTIVE);
        visit(CSharpParser.Using_static_directiveContext.class).map(USING_DIRECTIVE);

        visit(CSharpParser.Primary_expressionContext.class, context -> context.TK_LPAREN() != null).map(INVOCATION);
        visit(CSharpParser.Null_conditional_invocation_expressionContext.class).map(INVOCATION);
        visit(Object_creation_expressionContext.class).map(OBJECT_CREATION);
        visit(Array_initializerContext.class).map(ARRAY_CREATION);
        visit(AssignmentContext.class).map(ASSIGNMENT);
        visit(If_statementContext.class).map(IF);
        visit(KW_ELSE).map(IF);
        visit(Embedded_statementContext.class, context -> context.getParent() instanceof If_statementContext).map(IF_BEGIN, IF_END);
        visit(Switch_sectionContext.class).map(SWITCH_BEGIN, SWITCH_END);
        visit(Switch_statementContext.class).map(CASE);

        visit(Do_statementContext.class).map(LOOP_BEGIN, LOOP_END);
        visit(While_statementContext.class).map(LOOP_BEGIN, LOOP_END);
        visit(For_statementContext.class).map(LOOP_BEGIN, LOOP_END);
        visit(Foreach_statementContext.class).map(LOOP_BEGIN, LOOP_END);

        visit(Break_statementContext.class).map(BREAK);
        visit(Continue_statementContext.class).map(CONTINUE);
        visit(Goto_statementContext.class).map(GOTO);

        visit(Return_statementContext.class).map(RETURN);
        visit(Throw_statementContext.class).map(THROW);

        visit(Checked_statementContext.class).map(CHECKED);
        visit(Checked_expressionContext.class).map(CHECKED);
        visit(Unchecked_statementContext.class).map(UNCHECKED);
        visit(Unchecked_expressionContext.class).map(UNCHECKED);

        visit(Lock_statementContext.class).map(LOCK);

        visit(Try_statementContext.class).map(TRY);
        visit(Specific_catch_clauseContext.class).map(CATCH);
        visit(Finally_clauseContext.class).map(FINALLY);

        visit(Namespace_declarationContext.class).map(NAMESPACE);
        visit(Namespace_bodyContext.class).map(NAMESPACE_BEGIN, NAMESPACE_END);
        visit(Class_declarationContext.class).map(CLASS);
        visit(Class_bodyContext.class).map(CLASS_BEGIN, CLASS_END);
        visit(Method_declarationContext.class).delegateContext(method -> method.method_header()).map(METHOD);
        visit(Method_bodyContext.class).map(METHOD_BEGIN, METHOD_END);
        visit(Ref_method_bodyContext.class).map(METHOD_BEGIN, METHOD_END);
        visit(Property_declarationContext.class).map(PROPERTY);
        visit(Event_declarationContext.class).delegateContext(event -> event.type_()).map(EVENT);
        visit(Indexer_declarationContext.class).map(INDEXER);
        visit(Operator_declarationContext.class).delegateContext(operator -> operator.operator_declarator()).map(OPERATOR);
        visit(Operator_bodyContext.class).map(METHOD_BEGIN, METHOD_END);
        visit(Conversion_operator_declaratorContext.class).map(OPERATOR);
        visit(Constructor_declarationContext.class).map(CONSTRUCTOR);
        visit(Static_constructor_declarationContext.class).map(CONSTRUCTOR);
        visit(Constructor_bodyContext.class).map(METHOD_BEGIN, METHOD_END);
        visit(Static_constructor_bodyContext.class).map(METHOD_BEGIN, METHOD_END);
        visit(Finalizer_declarationContext.class).map(DESTRUCTOR);
        visit(Struct_declarationContext.class).map(STRUCT);
        visit(Struct_bodyContext.class).map(STRUCT_BEGIN, STRUCT_END);
        visit(Interface_declarationContext.class).map(INTERFACE);
        visit(Interface_bodyContext.class).map(INTERFACE_BEGIN, INTERFACE_END);
        visit(Enum_declarationContext.class).delegateContext(e -> e.identifier()).map(ENUM);
        visit(Enum_bodyContext.class).map(ENUM_BEGIN, ENUM_END);
        visit(Enum_member_declarationContext.class).map(ENUMERAL);
        visit(AttributeContext.class).map(ATTRIBUTE);
        visit(Delegate_declarationContext.class).map(DELEGATE);
        visit(Unsafe_statementContext.class).map(UNSAFE);
        visit(Fixed_statementContext.class).map(FIXED);
        visit(Accessor_declarationsContext.class).map(ACCESSORS_BEGIN, ACCESSORS_END);
        visit(Accessor_bodyContext.class).map(ACCESSOR_BEGIN, ACCESSOR_END);
        visit(Constant_declarationContext.class).map(CONSTANT);
        visit(Field_declarationContext.class).delegateContext(field -> field.type_()).map(FIELD);
        visit(Local_variable_declarationContext.class).map(LOCAL_VARIABLE);
        visit(Local_constant_declarationContext.class).map(LOCAL_VARIABLE);

        registerUnaryOperatorVisitors();
    }

    private void registerUnaryOperatorVisitors() {
        visit(TK_PLUS_PLUS).map(ASSIGNMENT);
        visit(TK_MINUS_MINUS).map(ASSIGNMENT);

        //visit(Shift_expressionContext.class, expr -> expr.shift_expression() == null).map(ASSIGNMENT);
    }

    private boolean isAliasUsageAlsoImport(CSharpParser.Using_alias_directiveContext context) {
        Namespace_or_type_nameContext namespace = getDescendant(context, Namespace_or_type_nameContext.class);

        return namespace != null && !namespace.TK_DOT().isEmpty() && getDescendant(namespace, Type_argument_listContext.class) == null;
    }
}
