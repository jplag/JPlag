lexer grammar BashLexer;

@lexer::members {
    private java.util.Deque<String> pendingHeredocs = new java.util.ArrayDeque<>();

    private void queueHeredoc() {
        String text = getText();
        String delim = text.replaceFirst("^<<-?[ \\t]*", "");
        if ((delim.startsWith("'") && delim.endsWith("'")) ||
            (delim.startsWith("\"") && delim.endsWith("\""))) {
            delim = delim.substring(1, delim.length() - 1);
        }
        pendingHeredocs.add(delim);
    }

    private void consumePendingHeredocs() {
        while (!pendingHeredocs.isEmpty()) {
            String delim = pendingHeredocs.poll();
            StringBuilder line = new StringBuilder();
            int c;
            while ((c = _input.LA(1)) != -1) {
                _input.consume();
                if (c == '\n') {
                    getInterpreter().setLine(getInterpreter().getLine() + 1);
                    getInterpreter().setCharPositionInLine(0);
                    if (line.toString().trim().equals(delim)) {
                        break;
                    }
                    line = new StringBuilder();
                } else if (c != '\r') {
                    line.append((char) c);
                    getInterpreter().setCharPositionInLine(
                        getInterpreter().getCharPositionInLine() + 1);
                }
            }
        }
    }

    @Override
    public Token nextToken() {
        Token t = super.nextToken();
        if (t.getType() == NEWLINE && !pendingHeredocs.isEmpty()) {
            consumePendingHeredocs();
        }
        return t;
    }
}

// Keywords
IF          : 'if';
THEN        : 'then';
ELIF        : 'elif';
ELSE        : 'else';
FI          : 'fi';
FOR         : 'for';
WHILE       : 'while';
UNTIL       : 'until';
DO          : 'do';
DONE        : 'done';
CASE        : 'case';
ESAC        : 'esac';
IN          : 'in';
SELECT      : 'select';
FUNCTION    : 'function';
RETURN      : 'return';
BREAK       : 'break';
CONTINUE    : 'continue';
LOCAL       : 'local';
DECLARE     : 'declare';
READONLY    : 'readonly';
EXPORT      : 'export';
UNSET       : 'unset';
EVAL        : 'eval';
EXEC        : 'exec';
SOURCE      : 'source';
TRAP        : 'trap';

// Operators (multi-char first for longest-match priority)
AND_IF      : '&&';
OR_IF       : '||';
SEMI_DSEMI  : ';;&';
DSEMI       : ';;';
SEMI_AND    : ';&';
DLBRACKET   : '[[';
DRBRACKET   : ']]';
DLPAREN     : '((';
DRPAREN     : '))';
PIPE        : '|';
AMP         : '&';
SEMI        : ';';
LPAREN      : '(';
RPAREN      : ')';
LBRACE      : '{';
RBRACE      : '}';
BANG        : '!';
LBRACKET    : '[';
RBRACKET    : ']';

// Redirection (multi-char first)
TLESS       : '<<<';
HEREDOC_OP  : ('<<-' | '<<') [ \t]* ([a-zA-Z_][a-zA-Z0-9_]* | '\'' ~[']* '\'' | '"' ~["]* '"') { queueHeredoc(); };
DLESSDASH   : '<<-';
DLESS       : '<<';
DGREAT      : '>>';
LESSAND     : '<&';
GREATAND    : '>&';
LESSGREAT   : '<>';
CLOBBER     : '>|';
LESS        : '<';
GREAT       : '>';

// Assignment
PLUS_EQUALS : '+=';
EQUALS      : '=';

// Dollar expansions (multi-char first)
DOLLAR_DLPAREN : '$((';
DOLLAR_LPAREN  : '$(';
DOLLAR_LBRACE_HASH : '${#';
DOLLAR_LBRACE  : '${';
DOLLAR_SPECIAL : '$' [?@*!#$-];
// Matches $VARNAME immediately followed by non-whitespace word chars (e.g. $HOME/.local)
DOLLAR_NAME_WORD : '$' [a-zA-Z_] [a-zA-Z0-9_]* (~[ \t\r\n;|&<>(){}$`'"\\#=\u005B\u005D])+;
DOLLAR       : '$';

// Backtick command substitution
BACKTICK    : '`';

// Newline
NEWLINE     : '\r'? '\n';

// String literals - simplified to handle all content including $vars
SINGLE_QUOTED_STRING : '\'' (~['] | '\\' .)* '\'';
DOUBLE_QUOTED_STRING : '"' (~["\\] | '\\' .)* '"';
ANSI_C_STRING : '$\'' (~['] | '\\' .)* '\'';

// Shebang
SHEBANG     : '#!' ~[\r\n]* -> skip;

// Comments
COMMENT     : '#' ~[\r\n]* -> skip;

// Line continuation
LINE_CONTINUATION : '\\' '\r'? '\n' -> skip;

// Escaped character (must come after LINE_CONTINUATION)
ESCAPED_CHAR : '\\' .;

// Whitespace (not newlines)
WS          : [ \t]+ -> skip;

// Names and words
NAME        : [a-zA-Z_] [a-zA-Z0-9_]*;
NUMBER      : [0-9]+;

// Catch-all for other word characters (flags, paths, operators in word context, etc.)
// Excludes: whitespace, newlines, ; | & < > ( ) { } $ ` ' " \ # [ ] =
WORD        : (~[ \t\r\n;|&<>(){}$`'"\\#=\u005B\u005D])+;
