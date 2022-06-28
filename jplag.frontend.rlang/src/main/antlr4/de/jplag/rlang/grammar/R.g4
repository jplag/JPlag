/*
 [The "BSD licence"]
 Copyright (c) 2013 Terence Parr
 All rights reserved.
 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions
 are met:
 1. Redistributions of source code must retain the above copyright
    notice, this list of conditions and the following disclaimer.
 2. Redistributions in binary form must reproduce the above copyright
    notice, this list of conditions and the following disclaimer in the
    documentation and/or other materials provided with the distribution.
 3. The name of the author may not be used to endorse or promote products
    derived from this software without specific prior written permission.
 THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

/**
derived from http://svn.r-project.org/R/trunk/src/main/gram.y
http://cran.r-project.org/doc/manuals/R-lang.html#Parser
I'm no R genius but this seems to work.
Requires RFilter.g4 to strip away NL that are really whitespace,
not end-of-command. See TestR.java
Usage:
$ antlr4 R.g4 RFilter.g4
$ javac *.java
$ java TestR sample.R
... prints parse tree ...
*/

/*
Modified version of the original in https://github.com/antlr/grammars-v4/blob/master/r/R.g4 so that I can separate the most relevant tokens of R in 
the JplagRListenter.java file.
Author of the modification: Antonio Javier Rodriguez Perez
*/

grammar R;

prog:   (   expr (';'|NL)
        |   NL
        )*
        EOF
    ;

/*
expr_or_assign
    :   expr ('<-'|'='|'<<-') expr_or_assign
    |   expr
    ;
*/

expr:   expr index_statement  // '[[' follows R's yacc grammar
    |   expr access_package expr
    |   expr ('$'|'@') expr
    |   <assoc=right> expr '^' expr
    |   ('-'|'+') expr
    |   expr ':' expr
    |   expr USER_OP expr // anything wrappedin %: '%' .* '%'
    |   expr ('*'|'/') expr
    |   expr ('+'|'-') expr
    |   expr ('>'|'>='|'<'|'<='|'=='|'!=') expr
    |   '!' expr
    |   expr ('&'|'&&') expr
    |   expr ('|'|'||') expr
    |   '~' expr
    |   expr '~' expr
    |   expr assign_value expr
    |   function_definition                 // define function
    |   expr function_call                  // call function
    |   compound_statement
    |   if_statement
    |   for_statement
    |   while_statement
    |   repeat_statement
    |   help
    |   next_statement
    |   break_statement
    |   '(' expr ')'
    |   ID
    |   constant
    ;

index_statement : '[[' sublist ']' ']' | '[' sublist ']' ;

access_package: '::'|':::' ;

function_definition: 'function' '(' formlist? ')' expr ;

function_call : '(' sublist ')' ;

constant: constant_number | constant_string | constant_bool | 'NULL' | 'NA' | 'Inf' | 'NaN' ;

constant_number: HEX | INT | FLOAT | COMPLEX ;

constant_string: STRING ;

constant_bool: 'TRUE' | 'FALSE' ;

help: '?' expr ; // get help on expr, usually string or ID

if_statement :  'if' '(' expr ')' expr | 'if' '(' expr ')' expr 'else' expr ;

for_statement : 'for' '(' ID 'in' expr ')' expr ;

while_statement : 'while' '(' expr ')' expr ;

repeat_statement: 'repeat' expr ;

next_statement: 'next' ;

break_statement: 'break' ;

compound_statement: '{' exprlist '}' ;

exprlist
    :   expr ((';'|NL) expr?)*
    |
    ;

formlist : form (',' form)* ;

form:   ID
    |   assign_func_declaration
    ;

sublist : sub (',' sub)* ;

sub :   expr
    |   assign_value_list
    |
    ;

assign_value: '<-'|'<<-'|'='|'->'|'->>'|':=';

assign_func_declaration: ID '=' expr | '...' ;

assign_value_list: ID '=' | ID '=' expr | constant_string '=' | constant_string '=' expr | 'NULL' '=' | 'NULL' '=' expr | '...' ;



HEX :   '0' ('x'|'X') HEXDIGIT+ [Ll]? ;

INT :   DIGIT+ [Ll]? ;

fragment
HEXDIGIT : ('0'..'9'|'a'..'f'|'A'..'F') ;

FLOAT:  DIGIT+ '.' DIGIT* EXP? [Ll]?
    |   DIGIT+ EXP? [Ll]?
    |   '.' DIGIT+ EXP? [Ll]?
    ;

fragment
DIGIT:  '0'..'9' ;

fragment
EXP :   ('E' | 'e') ('+' | '-')? INT ;

COMPLEX
    :   INT 'i'
    |   FLOAT 'i'
    ;

STRING
    :   '"' ( ESC | ~[\\"] )*? '"'
    |   '\'' ( ESC | ~[\\'] )*? '\''
    |   '`' ( ESC | ~[\\'] )*? '`'
    ;
fragment
ESC :   '\\' [abtnfrv"'\\]
    |   UNICODE_ESCAPE
    |   HEX_ESCAPE
    |   OCTAL_ESCAPE
    ;

fragment
UNICODE_ESCAPE
    :   '\\' 'u' HEXDIGIT HEXDIGIT HEXDIGIT HEXDIGIT
    |   '\\' 'u' '{' HEXDIGIT HEXDIGIT HEXDIGIT HEXDIGIT '}'
    ;

fragment
OCTAL_ESCAPE
    :   '\\' [0-3] [0-7] [0-7]
    |   '\\' [0-7] [0-7]
    |   '\\' [0-7]
    ;

fragment
HEX_ESCAPE
    :   '\\' HEXDIGIT HEXDIGIT?
    ;

ID  :   '.' (LETTER|'_'|'.') (LETTER|DIGIT|'_'|'.')*
    |   LETTER (LETTER|DIGIT|'_'|'.')*
    ;
    
fragment LETTER  : [a-zA-Z] ;

USER_OP :   '%' .*? '%' ;

COMMENT :   '#' .*? '\r'? '\n' -> type(NL) ;

// Match both UNIX and Windows newlines
NL      :   '\r'? '\n' ;

WS      :   [ \t\u000C]+ -> skip ;