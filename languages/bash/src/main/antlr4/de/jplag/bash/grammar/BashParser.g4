parser grammar BashParser;

options {
    tokenVocab = BashLexer;
}

program
    : (terminatedStatement | NEWLINE)* EOF
    ;

terminatedStatement
    : statement terminator
    | statement
    ;

statement
    : pipelineCommand
    | compoundCommand redirectionList?
    | functionDefinition
    | BANG statement
    ;

pipelineCommand
    : simpleCommand (PIPE pipelineCommand)?
    | compoundCommand redirectionList? (PIPE pipelineCommand)?
    ;

simpleCommand
    : cmdPrefix simpleCommandElement*
    | simpleCommandElement+
    ;

simpleCommandElement
    : word
    | redirection
    ;

cmdPrefix
    : (assignment | redirection)+
    ;

compoundCommand
    : ifClause
    | forClause
    | whileClause
    | untilClause
    | caseClause
    | selectClause
    | subshell
    | braceGroup
    | arithmeticExpression
    | testExpression
    ;

ifClause
    : IF compoundList THEN compoundList elifClause* elseClause? FI
    ;

elifClause
    : ELIF compoundList THEN compoundList
    ;

elseClause
    : ELSE compoundList
    ;

forClause
    : FOR NAME IN wordList terminator DO compoundList DONE
    | FOR NAME terminator? DO compoundList DONE
    | FOR DLPAREN arithmeticExpr? SEMI arithmeticExpr? SEMI arithmeticExpr? DRPAREN terminator? DO compoundList DONE
    ;

whileClause
    : WHILE compoundList DO compoundList DONE
    ;

untilClause
    : UNTIL compoundList DO compoundList DONE
    ;

caseClause
    : CASE word IN NEWLINE* caseItem* ESAC
    ;

caseItem
    : LPAREN? pattern RPAREN compoundList? (DSEMI | SEMI_AND | SEMI_DSEMI) NEWLINE*
    | LPAREN? pattern RPAREN compoundList? NEWLINE*
    ;

pattern
    : patternWord (PIPE patternWord)*
    ;

patternWord
    : word
    | BANG
    ;

selectClause
    : SELECT NAME IN wordList terminator DO compoundList DONE
    ;

subshell
    : LPAREN compoundList RPAREN
    ;

braceGroup
    : LBRACE compoundList RBRACE
    ;

arithmeticExpression
    : DOLLAR_DLPAREN arithmeticExpr DRPAREN
    | DLPAREN arithmeticExpr DRPAREN
    ;

testExpression
    : DLBRACKET testExprContent DRBRACKET
    | LBRACKET testExprContent RBRACKET
    ;

testExprContent
    : (word | EQUALS | BANG | LESS | GREAT | PIPE | AND_IF | OR_IF)+
    ;

arithmeticExpr
    : (word | EQUALS | PLUS_EQUALS | LESS | GREAT | LPAREN | RPAREN | BANG | PIPE)+
    ;

functionDefinition
    : FUNCTION NAME LPAREN RPAREN terminator? functionBody
    | FUNCTION NAME terminator? functionBody
    | NAME LPAREN RPAREN terminator? functionBody
    ;

functionBody
    : compoundCommand redirectionList?
    ;

compoundList
    : (terminatedStatement | NEWLINE)+
    ;

assignment
    : NAME EQUALS word?
    | NAME PLUS_EQUALS word?
    | LOCAL NAME (EQUALS word?)?
    | DECLARE declareOption* NAME (EQUALS word?)?
    | READONLY NAME (EQUALS word?)?
    | EXPORT NAME (EQUALS word?)?
    ;

declareOption
    : word
    ;

wordList
    : word+
    ;

word
    : NAME
    | NUMBER
    | WORD
    | SINGLE_QUOTED_STRING
    | DOUBLE_QUOTED_STRING
    | ANSI_C_STRING
    | DOLLAR NAME
    | DOLLAR NUMBER
    | DOLLAR_LBRACE wordContent* RBRACE
    | DOLLAR_LPAREN compoundList? RPAREN
    | DOLLAR_DLPAREN arithmeticExpr DRPAREN
    | BACKTICK compoundList? BACKTICK
    | RETURN
    | BREAK
    | CONTINUE
    | EVAL
    | EXEC
    | SOURCE
    | TRAP
    | UNSET
    | IN
    ;

wordContent
    : word
    | EQUALS
    | LESS
    | GREAT
    ;

redirection
    : NUMBER? LESS word
    | NUMBER? GREAT word
    | NUMBER? DGREAT word
    | NUMBER? LESSAND word
    | NUMBER? GREATAND word
    | NUMBER? LESSGREAT word
    | NUMBER? CLOBBER word
    | DLESS word
    | DLESSDASH word
    | TLESS word
    ;

redirectionList
    : redirection+
    ;

terminator
    : NEWLINE+
    | SEMI
    | AMP
    | AND_IF
    | OR_IF
    ;
