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

import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.TerminalNode;

import de.jplag.bash.grammar.BashLexer;
import de.jplag.bash.grammar.BashParser;
import de.jplag.bash.grammar.BashParserBaseListener;

/**
 * Listener for a Bash parse tree that extracts JPlag tokens.
 */
public class JPlagBashListener extends BashParserBaseListener implements ParseTreeListener {

    private final BashParserAdapter parserAdapter;

    public JPlagBashListener(BashParserAdapter parserAdapter) {
        this.parserAdapter = parserAdapter;
    }

    private void transformToken(BashTokenType targetType, Token token) {
        parserAdapter.addToken(targetType, token.getLine(), token.getCharPositionInLine() + 1, token.getText().length());
    }

    private void transformToken(BashTokenType targetType, Token start, Token end) {
        parserAdapter.addToken(targetType, start.getLine(), start.getCharPositionInLine() + 1, end.getStopIndex() - start.getStartIndex() + 1);
    }

    // --- Function definitions ---

    @Override
    public void enterFunctionDefinition(BashParser.FunctionDefinitionContext context) {
        transformToken(FUNCTION, context.getStart());
        super.enterFunctionDefinition(context);
    }

    @Override
    public void enterFunctionBody(BashParser.FunctionBodyContext context) {
        transformToken(FUNCTION_BODY_START, context.getStart());
        super.enterFunctionBody(context);
    }

    @Override
    public void exitFunctionBody(BashParser.FunctionBodyContext context) {
        transformToken(FUNCTION_BODY_END, context.getStop());
        super.exitFunctionBody(context);
    }

    // --- If/elif/else ---

    @Override
    public void enterIfClause(BashParser.IfClauseContext context) {
        transformToken(IF_STATEMENT, context.getStart());
        super.enterIfClause(context);
    }

    @Override
    public void exitIfClause(BashParser.IfClauseContext context) {
        transformToken(IF_BODY_END, context.getStop());
        super.exitIfClause(context);
    }

    @Override
    public void enterElifClause(BashParser.ElifClauseContext context) {
        transformToken(ELIF_STATEMENT, context.getStart());
        super.enterElifClause(context);
    }

    @Override
    public void exitElifClause(BashParser.ElifClauseContext context) {
        transformToken(ELIF_BODY_END, context.getStop());
        super.exitElifClause(context);
    }

    @Override
    public void enterElseClause(BashParser.ElseClauseContext context) {
        transformToken(ELSE_STATEMENT, context.getStart());
        super.enterElseClause(context);
    }

    @Override
    public void exitElseClause(BashParser.ElseClauseContext context) {
        transformToken(ELSE_BODY_END, context.getStop());
        super.exitElseClause(context);
    }

    // --- For ---

    @Override
    public void enterForClause(BashParser.ForClauseContext context) {
        transformToken(FOR_STATEMENT, context.getStart());
        super.enterForClause(context);
    }

    @Override
    public void exitForClause(BashParser.ForClauseContext context) {
        transformToken(FOR_BODY_END, context.getStop());
        super.exitForClause(context);
    }

    // --- While ---

    @Override
    public void enterWhileClause(BashParser.WhileClauseContext context) {
        transformToken(WHILE_STATEMENT, context.getStart());
        super.enterWhileClause(context);
    }

    @Override
    public void exitWhileClause(BashParser.WhileClauseContext context) {
        transformToken(WHILE_BODY_END, context.getStop());
        super.exitWhileClause(context);
    }

    // --- Until ---

    @Override
    public void enterUntilClause(BashParser.UntilClauseContext context) {
        transformToken(UNTIL_STATEMENT, context.getStart());
        super.enterUntilClause(context);
    }

    @Override
    public void exitUntilClause(BashParser.UntilClauseContext context) {
        transformToken(UNTIL_BODY_END, context.getStop());
        super.exitUntilClause(context);
    }

    // --- Case ---

    @Override
    public void enterCaseClause(BashParser.CaseClauseContext context) {
        transformToken(CASE_STATEMENT, context.getStart());
        super.enterCaseClause(context);
    }

    @Override
    public void exitCaseClause(BashParser.CaseClauseContext context) {
        transformToken(CASE_BODY_END, context.getStop());
        super.exitCaseClause(context);
    }

    @Override
    public void enterCaseItem(BashParser.CaseItemContext context) {
        transformToken(CASE_ITEM, context.getStart());
        super.enterCaseItem(context);
    }

    // --- Select ---

    @Override
    public void enterSelectClause(BashParser.SelectClauseContext context) {
        transformToken(SELECT_STATEMENT, context.getStart());
        super.enterSelectClause(context);
    }

    @Override
    public void exitSelectClause(BashParser.SelectClauseContext context) {
        transformToken(SELECT_BODY_END, context.getStop());
        super.exitSelectClause(context);
    }

    // --- Subshell ---

    @Override
    public void enterSubshell(BashParser.SubshellContext context) {
        transformToken(SUBSHELL_START, context.getStart());
        super.enterSubshell(context);
    }

    @Override
    public void exitSubshell(BashParser.SubshellContext context) {
        transformToken(SUBSHELL_END, context.getStop());
        super.exitSubshell(context);
    }

    // --- Brace group ---

    @Override
    public void enterBraceGroup(BashParser.BraceGroupContext context) {
        // Only emit brace group tokens when not used as function body
        if (!(context.getParent() instanceof BashParser.FunctionBodyContext)) {
            transformToken(BRACE_GROUP_START, context.getStart());
        }
        super.enterBraceGroup(context);
    }

    @Override
    public void exitBraceGroup(BashParser.BraceGroupContext context) {
        if (!(context.getParent() instanceof BashParser.FunctionBodyContext)) {
            transformToken(BRACE_GROUP_END, context.getStop());
        }
        super.exitBraceGroup(context);
    }

    // --- Assignments ---

    @Override
    public void enterAssignment(BashParser.AssignmentContext context) {
        if (context.LOCAL() != null) {
            transformToken(LOCAL_VARIABLE, context.getStart());
        } else if (context.DECLARE() != null) {
            transformToken(DECLARE_VARIABLE, context.getStart());
        } else if (context.EXPORT() != null) {
            transformToken(EXPORT_VARIABLE, context.getStart());
        } else if (context.READONLY() != null) {
            transformToken(READONLY_VARIABLE, context.getStart());
        } else {
            transformToken(VARIABLE_ASSIGNMENT, context.getStart());
        }
        super.enterAssignment(context);
    }

    // --- Simple commands ---

    @Override
    public void enterSimpleCommand(BashParser.SimpleCommandContext context) {
        // Find the first non-prefix word to mark as command
        var elements = context.simpleCommandElement();
        if (elements != null && !elements.isEmpty()) {
            var firstElement = elements.get(0);
            if (firstElement.word() != null) {
                Token token = firstElement.word().getStart();
                String text = token.getText();
                switch (text) {
                    case "return" -> transformToken(RETURN_STATEMENT, token);
                    case "break" -> transformToken(BREAK_STATEMENT, token);
                    case "continue" -> transformToken(CONTINUE_STATEMENT, token);
                    default -> transformToken(COMMAND, token);
                }
                // Mark remaining elements as arguments
                for (int i = 1; i < elements.size(); i++) {
                    var element = elements.get(i);
                    if (element.word() != null) {
                        transformToken(ARGUMENT, element.word().getStart());
                    }
                }
            }
        }
        super.enterSimpleCommand(context);
    }

    // --- Redirections ---

    @Override
    public void enterRedirection(BashParser.RedirectionContext context) {
        transformToken(REDIRECTION, context.getStart());
        super.enterRedirection(context);
    }

    // --- Arithmetic expressions ---

    @Override
    public void enterArithmeticExpression(BashParser.ArithmeticExpressionContext context) {
        transformToken(ARITHMETIC_EXPRESSION, context.getStart());
        super.enterArithmeticExpression(context);
    }

    // --- Test expressions ---

    @Override
    public void enterTestExpression(BashParser.TestExpressionContext context) {
        transformToken(TEST_EXPRESSION, context.getStart());
        super.enterTestExpression(context);
    }

    // --- Pipelines and logical operators ---

    @Override
    public void visitTerminal(TerminalNode node) {
        Token token = node.getSymbol();
        var parent = node.getParent();
        switch (token.getType()) {
            case BashLexer.PIPE -> {
                // Only emit PIPELINE for actual pipeline operators, not patterns or test expressions
                if (parent instanceof BashParser.PipelineCommandContext) {
                    transformToken(PIPELINE, token);
                }
            }
            case BashLexer.AND_IF -> {
                // Only emit for terminators connecting commands
                if (parent instanceof BashParser.TerminatorContext) {
                    transformToken(AND_LOGICAL, token);
                }
            }
            case BashLexer.OR_IF -> {
                if (parent instanceof BashParser.TerminatorContext) {
                    transformToken(OR_LOGICAL, token);
                }
            }
            case BashLexer.DO -> {
                if (parent instanceof BashParser.ForClauseContext) {
                    transformToken(FOR_BODY_START, token);
                } else if (parent instanceof BashParser.WhileClauseContext) {
                    transformToken(WHILE_BODY_START, token);
                } else if (parent instanceof BashParser.UntilClauseContext) {
                    transformToken(UNTIL_BODY_START, token);
                } else if (parent instanceof BashParser.SelectClauseContext) {
                    transformToken(SELECT_BODY_START, token);
                }
            }
            case BashLexer.THEN -> {
                if (parent instanceof BashParser.IfClauseContext) {
                    transformToken(IF_BODY_START, token);
                } else if (parent instanceof BashParser.ElifClauseContext) {
                    transformToken(ELIF_BODY_START, token);
                }
            }
            case BashLexer.ELSE -> {
                if (parent instanceof BashParser.ElseClauseContext) {
                    transformToken(ELSE_BODY_START, token);
                }
            }
            case BashLexer.IN -> {
                if (parent instanceof BashParser.CaseClauseContext) {
                    transformToken(CASE_BODY_START, token);
                }
            }
            default -> {
                // do nothing
            }
        }
    }

    // --- Command substitution ---

    @Override
    public void enterWord(BashParser.WordContext context) {
        if (context.DOLLAR_LPAREN() != null) {
            transformToken(COMMAND_SUBSTITUTION_START, context.getStart());
        } else if (context.BACKTICK() != null && !context.BACKTICK().isEmpty()) {
            transformToken(COMMAND_SUBSTITUTION_START, context.getStart());
        }
        super.enterWord(context);
    }

    @Override
    public void exitWord(BashParser.WordContext context) {
        if (context.DOLLAR_LPAREN() != null) {
            transformToken(COMMAND_SUBSTITUTION_END, context.getStop());
        } else if (context.BACKTICK() != null && !context.BACKTICK().isEmpty()) {
            transformToken(COMMAND_SUBSTITUTION_END, context.getStop());
        }
        super.exitWord(context);
    }
}
