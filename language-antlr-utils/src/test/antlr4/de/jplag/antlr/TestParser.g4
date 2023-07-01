parser grammar TestParser;

options { tokenVocab = TestLexer; }

expressionFile
    : calcExpression EOF
    | subExpression EOF
    ;

subExpression
    : BRACKET_OPEN calcExpression BRACKET_CLOSE
    ;

calcExpression
    : subExpression operator subExpression
    | calcExpression operator calcExpression
    | calcExpression operator subExpression
    | subExpression operator calcExpression
    | WHITESPACE calcExpression
    | calcExpression WHITESPACE
    | NUMBER
    ;

operator
    : PLUS
    | MINUS
    ;