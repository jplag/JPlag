package de.jplag.swift;

import static de.jplag.swift.SwiftTokenType.*;

import de.jplag.antlr.AbstractAntlrListener;
import de.jplag.swift.grammar.Swift5Parser.*;

public class SwiftListener extends AbstractAntlrListener {
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
     * @param context
     * @return
     */
    private boolean isComputedReadOnlyVariableGetterContext(Getter_setter_blockContext context) {
        return context.getChildCount() == 1 && context.getChild(0) instanceof Code_blockContext;
    }
}
