lexer grammar BashLexer;

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
DOLLAR_LBRACE  : '${';
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

// Whitespace (not newlines)
WS          : [ \t]+ -> skip;

// Names and words
NAME        : [a-zA-Z_] [a-zA-Z0-9_]*;
NUMBER      : [0-9]+;

// Catch-all for other word characters (flags, paths, operators in word context, etc.)
// Excludes: whitespace, newlines, ; | & < > ( ) { } $ ` ' " \ # [ ] =
WORD        : (~[ \t\r\n;|&<>(){}$`'"\\#=\u005B\u005D])+;
