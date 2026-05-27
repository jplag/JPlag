package de.jplag.swift;

import static de.jplag.swift.SwiftTokenType.ASSIGNMENT;
import static de.jplag.swift.SwiftTokenType.BREAK;
import static de.jplag.swift.SwiftTokenType.CATCH_BODY_BEGIN;
import static de.jplag.swift.SwiftTokenType.CATCH_BODY_END;
import static de.jplag.swift.SwiftTokenType.CLASS_BODY_BEGIN;
import static de.jplag.swift.SwiftTokenType.CLASS_BODY_END;
import static de.jplag.swift.SwiftTokenType.CLASS_DECLARATION;
import static de.jplag.swift.SwiftTokenType.CLOSURE_BODY_BEGIN;
import static de.jplag.swift.SwiftTokenType.CLOSURE_BODY_END;
import static de.jplag.swift.SwiftTokenType.CONTINUE;
import static de.jplag.swift.SwiftTokenType.DEFER_BODY_BEGIN;
import static de.jplag.swift.SwiftTokenType.DEFER_BODY_END;
import static de.jplag.swift.SwiftTokenType.DO_TRY_BODY_BEGIN;
import static de.jplag.swift.SwiftTokenType.DO_TRY_BODY_END;
import static de.jplag.swift.SwiftTokenType.ENUM_BODY_BEGIN;
import static de.jplag.swift.SwiftTokenType.ENUM_BODY_END;
import static de.jplag.swift.SwiftTokenType.ENUM_DECLARATION;
import static de.jplag.swift.SwiftTokenType.ENUM_LITERAL;
import static de.jplag.swift.SwiftTokenType.FALLTHROUGH;
import static de.jplag.swift.SwiftTokenType.FOR_BODY_BEGIN;
import static de.jplag.swift.SwiftTokenType.FOR_BODY_END;
import static de.jplag.swift.SwiftTokenType.FUNCTION;
import static de.jplag.swift.SwiftTokenType.FUNCTION_BODY_BEGIN;
import static de.jplag.swift.SwiftTokenType.FUNCTION_BODY_END;
import static de.jplag.swift.SwiftTokenType.FUNCTION_CALL;
import static de.jplag.swift.SwiftTokenType.FUNCTION_PARAMETER;
import static de.jplag.swift.SwiftTokenType.IF_BODY_BEGIN;
import static de.jplag.swift.SwiftTokenType.IF_BODY_END;
import static de.jplag.swift.SwiftTokenType.IMPORT;
import static de.jplag.swift.SwiftTokenType.PROPERTY_ACCESSOR_BEGIN;
import static de.jplag.swift.SwiftTokenType.PROPERTY_ACCESSOR_END;
import static de.jplag.swift.SwiftTokenType.PROPERTY_DECLARATION;
import static de.jplag.swift.SwiftTokenType.PROTOCOL_BODY_BEGIN;
import static de.jplag.swift.SwiftTokenType.PROTOCOL_BODY_END;
import static de.jplag.swift.SwiftTokenType.PROTOCOL_DECLARATION;
import static de.jplag.swift.SwiftTokenType.REPEAT_WHILE_BODY_BEGIN;
import static de.jplag.swift.SwiftTokenType.REPEAT_WHILE_BODY_END;
import static de.jplag.swift.SwiftTokenType.RETURN;
import static de.jplag.swift.SwiftTokenType.STRUCT_BODY_BEGIN;
import static de.jplag.swift.SwiftTokenType.STRUCT_BODY_END;
import static de.jplag.swift.SwiftTokenType.STRUCT_DECLARATION;
import static de.jplag.swift.SwiftTokenType.SWITCH_BODY_BEGIN;
import static de.jplag.swift.SwiftTokenType.SWITCH_BODY_END;
import static de.jplag.swift.SwiftTokenType.SWITCH_CASE;
import static de.jplag.swift.SwiftTokenType.THROW;
import static de.jplag.swift.SwiftTokenType.WHILE_BODY_BEGIN;
import static de.jplag.swift.SwiftTokenType.WHILE_BODY_END;

import de.jplag.antlr.AbstractAntlrListener;
import de.jplag.swift.grammar.Swift5Parser.Binary_operatorContext;
import de.jplag.swift.grammar.Swift5Parser.Break_statementContext;
import de.jplag.swift.grammar.Swift5Parser.Catch_clauseContext;
import de.jplag.swift.grammar.Swift5Parser.Class_bodyContext;
import de.jplag.swift.grammar.Swift5Parser.Class_declarationContext;
import de.jplag.swift.grammar.Swift5Parser.Closure_expressionContext;
import de.jplag.swift.grammar.Swift5Parser.Code_blockContext;
import de.jplag.swift.grammar.Swift5Parser.Constant_declarationContext;
import de.jplag.swift.grammar.Swift5Parser.Continue_statementContext;
import de.jplag.swift.grammar.Swift5Parser.Defer_statementContext;
import de.jplag.swift.grammar.Swift5Parser.DidSet_clauseContext;
import de.jplag.swift.grammar.Swift5Parser.Do_blockContext;
import de.jplag.swift.grammar.Swift5Parser.Else_clauseContext;
import de.jplag.swift.grammar.Swift5Parser.Enum_nameContext;
import de.jplag.swift.grammar.Swift5Parser.Fallthrough_statementContext;
import de.jplag.swift.grammar.Swift5Parser.For_in_statementContext;
import de.jplag.swift.grammar.Swift5Parser.Function_bodyContext;
import de.jplag.swift.grammar.Swift5Parser.Function_call_suffixContext;
import de.jplag.swift.grammar.Swift5Parser.Function_nameContext;
import de.jplag.swift.grammar.Swift5Parser.Function_resultContext;
import de.jplag.swift.grammar.Swift5Parser.Getter_clauseContext;
import de.jplag.swift.grammar.Swift5Parser.Getter_setter_blockContext;
import de.jplag.swift.grammar.Swift5Parser.Guard_statementContext;
import de.jplag.swift.grammar.Swift5Parser.If_statementContext;
import de.jplag.swift.grammar.Swift5Parser.Import_declarationContext;
import de.jplag.swift.grammar.Swift5Parser.InitializerContext;
import de.jplag.swift.grammar.Swift5Parser.Initializer_bodyContext;
import de.jplag.swift.grammar.Swift5Parser.Initializer_declarationContext;
import de.jplag.swift.grammar.Swift5Parser.ParameterContext;
import de.jplag.swift.grammar.Swift5Parser.Protocol_bodyContext;
import de.jplag.swift.grammar.Swift5Parser.Protocol_declarationContext;
import de.jplag.swift.grammar.Swift5Parser.Protocol_initializer_declarationContext;
import de.jplag.swift.grammar.Swift5Parser.Protocol_property_declarationContext;
import de.jplag.swift.grammar.Swift5Parser.Raw_value_assignmentContext;
import de.jplag.swift.grammar.Swift5Parser.Raw_value_style_enumContext;
import de.jplag.swift.grammar.Swift5Parser.Raw_value_style_enum_caseContext;
import de.jplag.swift.grammar.Swift5Parser.Raw_value_style_enum_membersContext;
import de.jplag.swift.grammar.Swift5Parser.Repeat_while_statementContext;
import de.jplag.swift.grammar.Swift5Parser.Return_statementContext;
import de.jplag.swift.grammar.Swift5Parser.Setter_clauseContext;
import de.jplag.swift.grammar.Swift5Parser.Struct_bodyContext;
import de.jplag.swift.grammar.Swift5Parser.Struct_declarationContext;
import de.jplag.swift.grammar.Swift5Parser.Switch_caseContext;
import de.jplag.swift.grammar.Swift5Parser.Switch_statementContext;
import de.jplag.swift.grammar.Swift5Parser.Throw_statementContext;
import de.jplag.swift.grammar.Swift5Parser.Union_style_enumContext;
import de.jplag.swift.grammar.Swift5Parser.Union_style_enum_caseContext;
import de.jplag.swift.grammar.Swift5Parser.Union_style_enum_membersContext;
import de.jplag.swift.grammar.Swift5Parser.Variable_declarationContext;
import de.jplag.swift.grammar.Swift5Parser.While_statementContext;
import de.jplag.swift.grammar.Swift5Parser.WillSet_clauseContext;

/**
 * Tree visitor for Swift programs.
 */
public class SwiftListener extends AbstractAntlrListener {

    /**
     * Creates the listener.
     */
    public SwiftListener() {
        this.registerDataStructureRules();
        this.registerFunctionRules();
        this.registerControlStructureRules();

        this.visit(Import_declarationContext.class).mapRange(IMPORT);

        this.visit(Variable_declarationContext.class).map(PROPERTY_DECLARATION);
        this.visit(Protocol_property_declarationContext.class).mapRange(PROPERTY_DECLARATION);
        this.visit(Constant_declarationContext.class).mapRange(PROPERTY_DECLARATION);

        this.visit(Fallthrough_statementContext.class).mapRange(FALLTHROUGH);
        this.visit(InitializerContext.class).mapRange(ASSIGNMENT);
        this.visit(Raw_value_assignmentContext.class).mapRange(ASSIGNMENT);

        this.visit(Binary_operatorContext.class, context -> context.getText().contains("=") && !context.getText().contains("=="))
                .mapRange(ASSIGNMENT);
        this.visit(Closure_expressionContext.class).map(CLOSURE_BODY_BEGIN, CLOSURE_BODY_END);
    }

    private void registerDataStructureRules() {
        this.visit(Class_declarationContext.class).map(CLASS_DECLARATION);
        this.visit(Class_bodyContext.class).map(CLASS_BODY_BEGIN, CLASS_BODY_END);
        this.visit(Closure_expressionContext.class).map(CLASS_BODY_BEGIN, CLASS_BODY_END);

        this.visit(Struct_declarationContext.class).map(STRUCT_DECLARATION);
        this.visit(Struct_bodyContext.class).map(STRUCT_BODY_BEGIN, STRUCT_BODY_END);

        this.visit(Enum_nameContext.class).map(ENUM_DECLARATION);
        this.visit(Raw_value_style_enum_membersContext.class).map(ENUM_BODY_BEGIN);
        this.visit(Union_style_enum_membersContext.class).map(ENUM_BODY_BEGIN);
        this.visit(Raw_value_style_enumContext.class).mapExit(ENUM_BODY_END);
        this.visit(Union_style_enumContext.class).mapExit(ENUM_BODY_END);

        this.visit(Raw_value_style_enum_caseContext.class).mapRange(ENUM_LITERAL);
        this.visit(Union_style_enum_caseContext.class).mapRange(ENUM_LITERAL);

        this.visit(Protocol_declarationContext.class).mapRange(PROTOCOL_DECLARATION);
        this.visit(Protocol_bodyContext.class).map(PROTOCOL_BODY_BEGIN, PROTOCOL_BODY_END);
    }

    private void registerFunctionRules() {
        this.visit(Initializer_declarationContext.class).mapRange(FUNCTION);
        this.visit(Protocol_initializer_declarationContext.class).mapRange(FUNCTION);
        this.visit(Function_nameContext.class).mapRange(FUNCTION);
        this.visit(Initializer_bodyContext.class).map(FUNCTION_BODY_BEGIN, FUNCTION_BODY_END);
        this.visit(ParameterContext.class).mapRange(FUNCTION_PARAMETER);
        this.visit(Function_resultContext.class).mapRange(FUNCTION_PARAMETER);
        this.visit(Function_bodyContext.class).map(FUNCTION_BODY_BEGIN, FUNCTION_BODY_END);
        this.visit(Function_call_suffixContext.class).mapRange(FUNCTION_CALL);

        this.visit(Getter_clauseContext.class).map(PROPERTY_ACCESSOR_BEGIN, PROPERTY_ACCESSOR_END);
        this.visit(Setter_clauseContext.class).map(PROPERTY_ACCESSOR_BEGIN, PROPERTY_ACCESSOR_END);
        this.visit(WillSet_clauseContext.class).map(PROPERTY_ACCESSOR_BEGIN, PROPERTY_ACCESSOR_END);
        this.visit(Getter_setter_blockContext.class, this::isComputedReadOnlyVariableGetterContext).map(PROPERTY_ACCESSOR_BEGIN,
                PROPERTY_ACCESSOR_END);
        this.visit(DidSet_clauseContext.class).map(PROPERTY_ACCESSOR_BEGIN, PROPERTY_ACCESSOR_END);
    }

    private void registerControlStructureRules() {
        this.visit(For_in_statementContext.class).map(FOR_BODY_BEGIN, FOR_BODY_END);
        this.visit(If_statementContext.class).map(IF_BODY_BEGIN, IF_BODY_END);
        this.visit(Else_clauseContext.class).map(IF_BODY_END);
        this.visit(Else_clauseContext.class, context -> !(context.getChild(0) instanceof If_statementContext)).map(IF_BODY_BEGIN);
        this.visit(Guard_statementContext.class).map(IF_BODY_BEGIN, IF_BODY_END);
        this.visit(Switch_statementContext.class).map(SWITCH_BODY_BEGIN, SWITCH_BODY_END);
        this.visit(Switch_caseContext.class).map(SWITCH_CASE);
        this.visit(While_statementContext.class).map(WHILE_BODY_BEGIN, WHILE_BODY_END);
        this.visit(Repeat_while_statementContext.class).map(REPEAT_WHILE_BODY_BEGIN, REPEAT_WHILE_BODY_END);
        this.visit(Defer_statementContext.class).map(DEFER_BODY_BEGIN, DEFER_BODY_END);

        this.visit(Do_blockContext.class).map(DO_TRY_BODY_BEGIN, DO_TRY_BODY_END);
        this.visit(Catch_clauseContext.class).map(CATCH_BODY_BEGIN, CATCH_BODY_END);
        this.visit(Throw_statementContext.class).mapRange(THROW);
        this.visit(Return_statementContext.class).mapRange(RETURN);
        this.visit(Continue_statementContext.class).mapRange(CONTINUE);
        this.visit(Break_statementContext.class).mapRange(BREAK);
    }

    /**
     * Indicates whether the given context encodes a computed read-only variable getter. An example of this is
     * <code>var example: Int { return 1 }</code>.
     * @param context is the getter/setter context.
     * @return true if it is a computed read-only variable getter.
     */
    private boolean isComputedReadOnlyVariableGetterContext(Getter_setter_blockContext context) {
        return context.getChildCount() == 1 && context.getChild(0) instanceof Code_blockContext;
    }
}
