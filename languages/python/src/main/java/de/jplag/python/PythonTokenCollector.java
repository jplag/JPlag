package de.jplag.python;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import de.jplag.Token;
import de.jplag.treesitter.TreeSitterVisitor;

import io.github.treesitter.jtreesitter.Node;

/**
 * A token collector for Python using Tree-sitter that implements a map-based visitor pattern.
 */
public class PythonTokenCollector implements TreeSitterVisitor {
    private final List<Token> tokens;
    private final File file;
    private final String code;

    private final Map<String, Consumer<Node>> enterHandlers = new HashMap<>();
    private final Map<String, Consumer<Node>> exitHandlers = new HashMap<>();

    public PythonTokenCollector(File file, String code) {
        tokens = new ArrayList<>();
        this.file = file;
        this.code = code;
        initializeHandlers();
    }

    /**
     * Initialize the handler maps for different node types.
     */
    private void initializeHandlers() {
        enterHandlers.put("import_statement", node -> addToken(PythonTokenType.IMPORT, node));
        enterHandlers.put("import_from_statement", node -> addToken(PythonTokenType.IMPORT, node));
        enterHandlers.put("class_definition", node -> addToken(PythonTokenType.CLASS_BEGIN, node));
        enterHandlers.put("function_definition", node -> addToken(PythonTokenType.METHOD_BEGIN, node));
        enterHandlers.put("assignment", node -> addToken(PythonTokenType.ASSIGN, node));
        enterHandlers.put("augmented_assignment", node -> addToken(PythonTokenType.ASSIGN, node));
        enterHandlers.put("while_statement", node -> addToken(PythonTokenType.WHILE_BEGIN, node));
        enterHandlers.put("for_statement", node -> addToken(PythonTokenType.FOR_BEGIN, node));
        enterHandlers.put("try_statement", node -> addToken(PythonTokenType.TRY_BEGIN, node));
        enterHandlers.put("except_clause", node -> addToken(PythonTokenType.EXCEPT_BEGIN, node));
        enterHandlers.put("finally_clause", node -> addToken(PythonTokenType.FINALLY_BEGIN, node));
        enterHandlers.put("if_statement", node -> addToken(PythonTokenType.IF_BEGIN, node));
        enterHandlers.put("elif_clause", node -> addToken(PythonTokenType.IF_BEGIN, node));
        enterHandlers.put("else_clause", node -> addToken(PythonTokenType.IF_BEGIN, node));
        enterHandlers.put("call", node -> addToken(PythonTokenType.APPLY, node));
        enterHandlers.put("break_statement", node -> addToken(PythonTokenType.BREAK, node));
        enterHandlers.put("continue_statement", node -> addToken(PythonTokenType.CONTINUE, node));
        enterHandlers.put("return_statement", node -> addToken(PythonTokenType.RETURN, node));
        enterHandlers.put("raise_statement", node -> addToken(PythonTokenType.RAISE, node));
        enterHandlers.put("decorator", node -> addToken(PythonTokenType.DECORATOR_BEGIN, node));
        enterHandlers.put("lambda", node -> addToken(PythonTokenType.LAMBDA, node));
        enterHandlers.put("assert_statement", node -> addToken(PythonTokenType.ASSERT, node));
        enterHandlers.put("yield", node -> addToken(PythonTokenType.YIELD, node));
        enterHandlers.put("delete_statement", node -> addToken(PythonTokenType.DEL, node));
        enterHandlers.put("with_statement", node -> addToken(PythonTokenType.WITH_BEGIN, node));
        enterHandlers.put("pass_statement", node -> addToken(PythonTokenType.PASS, node));
        enterHandlers.put("global_statement", node -> addToken(PythonTokenType.GLOBAL, node));
        enterHandlers.put("nonlocal_statement", node -> addToken(PythonTokenType.NONLOCAL, node));
        enterHandlers.put("named_expression", node -> addToken(PythonTokenType.NAMED_EXPR, node));
        enterHandlers.put("match_statement", node -> addToken(PythonTokenType.MATCH_BEGIN, node));
        enterHandlers.put("case_clause", node -> addToken(PythonTokenType.CASE, node));
        enterHandlers.put("type_alias_statement", node -> addToken(PythonTokenType.TYPE_ALIAS, node));
        enterHandlers.put("async", node -> addToken(PythonTokenType.ASYNC, node));
        enterHandlers.put("await", node -> addToken(PythonTokenType.AWAIT, node));

        exitHandlers.put("class_definition", node -> addToken(PythonTokenType.CLASS_END, node));
        exitHandlers.put("function_definition", node -> addToken(PythonTokenType.METHOD_END, node));
        exitHandlers.put("while_statement", node -> addToken(PythonTokenType.WHILE_END, node));
        exitHandlers.put("for_statement", node -> addToken(PythonTokenType.FOR_END, node));
        exitHandlers.put("try_statement", node -> addToken(PythonTokenType.TRY_END, node));
        exitHandlers.put("except_clause", node -> addToken(PythonTokenType.EXCEPT_END, node));
        exitHandlers.put("finally_clause", node -> addToken(PythonTokenType.FINALLY_END, node));
        exitHandlers.put("if_statement", node -> addToken(PythonTokenType.IF_END, node));
        exitHandlers.put("elif_clause", node -> addToken(PythonTokenType.IF_END, node));
        exitHandlers.put("else_clause", node -> addToken(PythonTokenType.IF_END, node));
        exitHandlers.put("decorator", node -> addToken(PythonTokenType.DECORATOR_END, node));
        exitHandlers.put("with_statement", node -> addToken(PythonTokenType.WITH_END, node));
        exitHandlers.put("match_statement", node -> addToken(PythonTokenType.MATCH_END, node));
    }

    @Override
    public void enter(Node node) {
        String nodeType = node.getType();
        Consumer<Node> handler = enterHandlers.get(nodeType);
        if (handler != null) {
            handler.accept(node);
        }
    }

    @Override
    public void exit(Node node) {
        String nodeType = node.getType();
        Consumer<Node> handler = exitHandlers.get(nodeType);
        if (handler != null) {
            handler.accept(node);
        }
    }

    /**
     * Add a token of the specified type for the given node.
     * @param tokenType The type of token to create
     * @param node The Tree-sitter node
     */
    private void addToken(PythonTokenType tokenType, Node node) {
        addToken(tokenType, node, getTokenLength(tokenType, node));
    }

    /**
     * Add a token of the specified type for the given node with a specific length.
     * @param tokenType The type of token to create
     * @param node The Tree-sitter node
     * @param length The length of the token
     */
    private void addToken(PythonTokenType tokenType, Node node, int length) {
        int line;
        int column;

        // For end tokens, use the end position for line but start position for column (visual alignment)
        if (isEndToken(tokenType)) {
            line = getLineNumber(node.getEndByte());
        } else {
            line = getLineNumber(node.getStartByte());
        }
        column = getColumnNumber(node.getStartByte());

        tokens.add(new Token(tokenType, file, line, column, length, null));
    }

    /**
     * Get the hardcoded length for Python keywords and statements.
     */
    private int getTokenLength(PythonTokenType tokenType, Node node) {
        return switch (tokenType) {
            case IMPORT -> 6; // "import"
            case CLASS_BEGIN, CLASS_END -> 5; // "class"
            case METHOD_BEGIN, METHOD_END -> 3; // "def"
            case ASSIGN -> 1; // "="
            case WHILE_BEGIN, WHILE_END -> 5; // "while"
            case FOR_BEGIN, FOR_END -> 3; // "for"
            case TRY_BEGIN, TRY_END -> 3; // "try"
            case EXCEPT_BEGIN, EXCEPT_END -> 6; // "except"
            case FINALLY_BEGIN, FINALLY_END -> 7; // "finally"
            case BREAK -> 5; // "break"
            case CONTINUE -> 8; // "continue"
            case RETURN -> 6; // "return"
            case RAISE -> 5; // "raise"
            case DECORATOR_BEGIN, DECORATOR_END -> 1; // "@"
            case LAMBDA -> 6; // "lambda"
            case ASSERT -> 6; // "assert"
            case YIELD -> 5; // "yield"
            case DEL -> 3; // "del"
            case WITH_BEGIN, WITH_END -> 4; // "with"
            case ASYNC -> 5; // "async"
            case AWAIT -> 5; // "await"
            case PASS -> 4; // "pass"
            case GLOBAL -> 6; // "global"
            case NONLOCAL -> 8; // "nonlocal"
            case NAMED_EXPR -> 2; // ":="
            case MATCH_BEGIN, MATCH_END -> 5; // "match"
            case CASE -> 4; // "case"
            case EXCEPT_GROUP_BEGIN, EXCEPT_GROUP_END -> 7; // "except*"
            case TYPE_ALIAS -> 4; // "type"
            case IF_BEGIN, IF_END, APPLY -> node.getEndByte() - node.getStartByte(); // Variable length tokens that need node inspection
            default -> node.getEndByte() - node.getStartByte(); // Fallback to node span
        };
    }

    /**
     * Check if character at given position is a newline.
     */
    private boolean isNewline(int position) {
        return code.charAt(position) == '\n';
    }

    /**
     * Convert byte offset to line number (1-based).
     */
    private int getLineNumber(int byteOffset) {
        int line = 1;
        for (int i = 0; i < byteOffset && i < code.length(); i++) {
            if (isNewline(i)) {
                line++;
            }
        }
        return line;
    }

    /**
     * Convert byte offset to column number (1-based).
     */
    private int getColumnNumber(int byteOffset) {
        int lastNewlinePosition = -1;
        for (int i = 0; i < byteOffset && i < code.length(); i++) {
            if (isNewline(i)) {
                lastNewlinePosition = i;
            }
        }
        return byteOffset - lastNewlinePosition;
    }

    /**
     * Check if a token type represents an "end" token that should use the end position of the node.
     */
    private boolean isEndToken(PythonTokenType tokenType) {
        return switch (tokenType) {
            case CLASS_END, METHOD_END, WHILE_END, FOR_END, TRY_END, EXCEPT_END, FINALLY_END, IF_END, DECORATOR_END, WITH_END, MATCH_END, EXCEPT_GROUP_END -> true;
            default -> false;
        };
    }

    /**
     * Get the collected tokens.
     * @return List of extracted tokens
     */
    public List<Token> getTokens() {
        return new ArrayList<>(tokens);
    }
}
