lexer grammar TestLexer;

BRACKET_OPEN: '(';
BRACKET_CLOSE: ')';
PLUS: '+';
MINUS: '-';

NUMBER: ('0'..'9')+;
VAR_NAME: ('a'..'z')+;
VAR_SEPARATOR: ',';

LINEBREAK: ('\n' | '\r\n');

WHITESPACE: ' '+;