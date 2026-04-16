package de.jplag.bash;

import static de.jplag.bash.BashTokenType.AND_LOGICAL;
import static de.jplag.bash.BashTokenType.ARGUMENT;
import static de.jplag.bash.BashTokenType.ARITHMETIC_EXPRESSION;
import static de.jplag.bash.BashTokenType.BRACE_GROUP_END;
import static de.jplag.bash.BashTokenType.BRACE_GROUP_START;
import static de.jplag.bash.BashTokenType.BREAK_STATEMENT;
import static de.jplag.bash.BashTokenType.CASE_BODY_END;
import static de.jplag.bash.BashTokenType.CASE_BODY_START;
import static de.jplag.bash.BashTokenType.CASE_ITEM;
import static de.jplag.bash.BashTokenType.CASE_STATEMENT;
import static de.jplag.bash.BashTokenType.COMMAND;
import static de.jplag.bash.BashTokenType.COMMAND_SUBSTITUTION_END;
import static de.jplag.bash.BashTokenType.COMMAND_SUBSTITUTION_START;
import static de.jplag.bash.BashTokenType.CONTINUE_STATEMENT;
import static de.jplag.bash.BashTokenType.DECLARE_VARIABLE;
import static de.jplag.bash.BashTokenType.ELIF_BODY_END;
import static de.jplag.bash.BashTokenType.ELIF_BODY_START;
import static de.jplag.bash.BashTokenType.ELIF_STATEMENT;
import static de.jplag.bash.BashTokenType.ELSE_BODY_END;
import static de.jplag.bash.BashTokenType.ELSE_BODY_START;
import static de.jplag.bash.BashTokenType.ELSE_STATEMENT;
import static de.jplag.bash.BashTokenType.EXPORT_VARIABLE;
import static de.jplag.bash.BashTokenType.FOR_BODY_END;
import static de.jplag.bash.BashTokenType.FOR_BODY_START;
import static de.jplag.bash.BashTokenType.FOR_STATEMENT;
import static de.jplag.bash.BashTokenType.FUNCTION;
import static de.jplag.bash.BashTokenType.FUNCTION_BODY_END;
import static de.jplag.bash.BashTokenType.FUNCTION_BODY_START;
import static de.jplag.bash.BashTokenType.IF_BODY_END;
import static de.jplag.bash.BashTokenType.IF_BODY_START;
import static de.jplag.bash.BashTokenType.IF_STATEMENT;
import static de.jplag.bash.BashTokenType.LOCAL_VARIABLE;
import static de.jplag.bash.BashTokenType.OR_LOGICAL;
import static de.jplag.bash.BashTokenType.PIPELINE;
import static de.jplag.bash.BashTokenType.READONLY_VARIABLE;
import static de.jplag.bash.BashTokenType.REDIRECTION;
import static de.jplag.bash.BashTokenType.RETURN_STATEMENT;
import static de.jplag.bash.BashTokenType.SELECT_BODY_END;
import static de.jplag.bash.BashTokenType.SELECT_BODY_START;
import static de.jplag.bash.BashTokenType.SELECT_STATEMENT;
import static de.jplag.bash.BashTokenType.SUBSHELL_END;
import static de.jplag.bash.BashTokenType.SUBSHELL_START;
import static de.jplag.bash.BashTokenType.TEST_EXPRESSION;
import static de.jplag.bash.BashTokenType.UNTIL_BODY_END;
import static de.jplag.bash.BashTokenType.UNTIL_BODY_START;
import static de.jplag.bash.BashTokenType.UNTIL_STATEMENT;
import static de.jplag.bash.BashTokenType.VARIABLE_ASSIGNMENT;
import static de.jplag.bash.BashTokenType.WHILE_BODY_END;
import static de.jplag.bash.BashTokenType.WHILE_BODY_START;
import static de.jplag.bash.BashTokenType.WHILE_STATEMENT;

import java.util.Set;

import de.jplag.antlr.AbstractAntlrListener;
import de.jplag.bash.grammar.BashParser.ArgumentContext;
import de.jplag.bash.grammar.BashParser.ArithmeticExpressionContext;
import de.jplag.bash.grammar.BashParser.AssignmentContext;
import de.jplag.bash.grammar.BashParser.BraceGroupContext;
import de.jplag.bash.grammar.BashParser.CaseClauseContext;
import de.jplag.bash.grammar.BashParser.CaseItemContext;
import de.jplag.bash.grammar.BashParser.CommandNameContext;
import de.jplag.bash.grammar.BashParser.ElifClauseContext;
import de.jplag.bash.grammar.BashParser.ElseClauseContext;
import de.jplag.bash.grammar.BashParser.ForClauseContext;
import de.jplag.bash.grammar.BashParser.FunctionBodyContext;
import de.jplag.bash.grammar.BashParser.FunctionDefinitionContext;
import de.jplag.bash.grammar.BashParser.IfClauseContext;
import de.jplag.bash.grammar.BashParser.PipelineCommandContext;
import de.jplag.bash.grammar.BashParser.RedirectionContext;
import de.jplag.bash.grammar.BashParser.SelectClauseContext;
import de.jplag.bash.grammar.BashParser.SubshellContext;
import de.jplag.bash.grammar.BashParser.TerminatorContext;
import de.jplag.bash.grammar.BashParser.TestExpressionContext;
import de.jplag.bash.grammar.BashParser.UntilClauseContext;
import de.jplag.bash.grammar.BashParser.WhileClauseContext;
import de.jplag.bash.grammar.BashParser.WordContext;

/**
 * Listener for a Bash parse tree that extracts JPlag tokens.
 */
class BashListener extends AbstractAntlrListener {

    private static final Set<String> CONTROL_FLOW_COMMANDS = Set.of("return", "break", "continue");

    BashListener() {
        // Function definitions
        visit(FunctionDefinitionContext.class).map(FUNCTION);
        visit(FunctionBodyContext.class).map(FUNCTION_BODY_START, FUNCTION_BODY_END);

        // If/elif/else
        visit(IfClauseContext.class).map(IF_STATEMENT, IF_BODY_END);
        visit(IfClauseContext.class).delegateTerminal(IfClauseContext::THEN).map(IF_BODY_START);
        visit(ElifClauseContext.class).map(ELIF_STATEMENT, ELIF_BODY_END);
        visit(ElifClauseContext.class).delegateTerminal(ElifClauseContext::THEN).map(ELIF_BODY_START);
        visit(ElseClauseContext.class).map(ELSE_STATEMENT, ELSE_BODY_END);
        visit(ElseClauseContext.class).delegateTerminal(ElseClauseContext::ELSE).map(ELSE_BODY_START);

        // For/while/until/select loops
        visit(ForClauseContext.class).map(FOR_STATEMENT, FOR_BODY_END);
        visit(ForClauseContext.class).delegateTerminal(ForClauseContext::DO).map(FOR_BODY_START);
        visit(WhileClauseContext.class).map(WHILE_STATEMENT, WHILE_BODY_END);
        visit(WhileClauseContext.class).delegateTerminal(WhileClauseContext::DO).map(WHILE_BODY_START);
        visit(UntilClauseContext.class).map(UNTIL_STATEMENT, UNTIL_BODY_END);
        visit(UntilClauseContext.class).delegateTerminal(UntilClauseContext::DO).map(UNTIL_BODY_START);
        visit(SelectClauseContext.class).map(SELECT_STATEMENT, SELECT_BODY_END);
        visit(SelectClauseContext.class).delegateTerminal(SelectClauseContext::DO).map(SELECT_BODY_START);

        // Case
        visit(CaseClauseContext.class).map(CASE_STATEMENT, CASE_BODY_END);
        visit(CaseClauseContext.class).delegateTerminal(CaseClauseContext::IN).map(CASE_BODY_START);
        visit(CaseItemContext.class).map(CASE_ITEM);

        // Subshell and brace group
        visit(SubshellContext.class).map(SUBSHELL_START, SUBSHELL_END);
        visit(BraceGroupContext.class, ctx -> !(ctx.getParent() instanceof FunctionBodyContext))
                .map(BRACE_GROUP_START, BRACE_GROUP_END);

        // Assignments
        visit(AssignmentContext.class, ctx -> ctx.LOCAL() != null).map(LOCAL_VARIABLE);
        visit(AssignmentContext.class, ctx -> ctx.DECLARE() != null).map(DECLARE_VARIABLE);
        visit(AssignmentContext.class, ctx -> ctx.EXPORT() != null).map(EXPORT_VARIABLE);
        visit(AssignmentContext.class, ctx -> ctx.READONLY() != null).map(READONLY_VARIABLE);
        visit(AssignmentContext.class, ctx -> ctx.LOCAL() == null && ctx.DECLARE() == null
                && ctx.EXPORT() == null && ctx.READONLY() == null).map(VARIABLE_ASSIGNMENT);

        // Commands
        visit(CommandNameContext.class, ctx -> "return".equals(ctx.getText())).map(RETURN_STATEMENT);
        visit(CommandNameContext.class, ctx -> "break".equals(ctx.getText())).map(BREAK_STATEMENT);
        visit(CommandNameContext.class, ctx -> "continue".equals(ctx.getText())).map(CONTINUE_STATEMENT);
        visit(CommandNameContext.class, ctx -> !CONTROL_FLOW_COMMANDS.contains(ctx.getText())).map(COMMAND);

        // Arguments
        visit(ArgumentContext.class, ctx -> ctx.word() != null).map(ARGUMENT);

        // Redirections
        visit(RedirectionContext.class).map(REDIRECTION);

        // Arithmetic and test expressions
        visit(ArithmeticExpressionContext.class).map(ARITHMETIC_EXPRESSION);
        visit(TestExpressionContext.class).map(TEST_EXPRESSION);

        // Command substitution
        visit(WordContext.class, ctx -> ctx.DOLLAR_LPAREN() != null).map(COMMAND_SUBSTITUTION_START, COMMAND_SUBSTITUTION_END);
        visit(WordContext.class, ctx -> ctx.BACKTICK() != null && !ctx.BACKTICK().isEmpty())
                .map(COMMAND_SUBSTITUTION_START, COMMAND_SUBSTITUTION_END);

        // Pipeline
        visit(PipelineCommandContext.class, ctx -> ctx.PIPE() != null)
                .delegateTerminal(PipelineCommandContext::PIPE).map(PIPELINE);

        // Logical operators in terminators
        visit(TerminatorContext.class, ctx -> ctx.AND_IF() != null)
                .delegateTerminal(TerminatorContext::AND_IF).map(AND_LOGICAL);
        visit(TerminatorContext.class, ctx -> ctx.OR_IF() != null)
                .delegateTerminal(TerminatorContext::OR_IF).map(OR_LOGICAL);
    }
}
