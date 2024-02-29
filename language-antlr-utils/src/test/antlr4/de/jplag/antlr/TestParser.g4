parser grammar TestParser;

options { tokenVocab = TestLexer; }

expressionFile
    : varDefs? calcExpression EOF
    | varDefs? subExpression EOF
    ;

varDefs
    : (varDef VAR_SEPARATOR)* varDef LINEBREAK
    ;

varDef
    : VAR_NAME
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
    | varRef
    ;

varRef
    : VAR_NAME
    ;

operator
    : PLUS
    | MINUS
    ;