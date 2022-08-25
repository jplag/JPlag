// Eclipse Public License - v 1.0, http://www.eclipse.org/legal/epl-v10.html
// Copyright (c) 2013, Christian Wulf (chwchw@gmx.de)
// Copyright (c) 2016-2017, Ivan Kochurkin (kvanttt@gmail.com), Positive Technologies.

parser grammar CSharpPreprocessorParser;

options { tokenVocab=CSharpLexer; superClass=CSharpPreprocessorParserBase; }

preprocessor_directive returns [Boolean value]
	: DEFINE CONDITIONAL_SYMBOL directive_new_line_or_sharp { this.onPreprocessorDirectiveDefine(); }  #preprocessorDeclaration
	| UNDEF CONDITIONAL_SYMBOL directive_new_line_or_sharp { this.onPreprocessorDirectiveUndef(); } #preprocessorDeclaration
	| IF expr=preprocessor_expression directive_new_line_or_sharp { this.onPreprocessorDirectiveIf(); }	  #preprocessorConditional
	| ELIF expr=preprocessor_expression directive_new_line_or_sharp { this.onPreprocessorDirectiveElif(); } #preprocessorConditional
	| ELSE directive_new_line_or_sharp { this.onPreprocessorDirectiveElse(); }    #preprocessorConditional
	| ENDIF directive_new_line_or_sharp { this.onPreprocessorDirectiveEndif(); } #preprocessorConditional
	| LINE (DIGITS STRING? | DEFAULT | DIRECTIVE_HIDDEN) directive_new_line_or_sharp { this.onPreprocessorDirectiveLine(); } #preprocessorLine
	| ERROR TEXT directive_new_line_or_sharp { this.onPreprocessorDirectiveError(); }   #preprocessorDiagnostic
	| WARNING TEXT directive_new_line_or_sharp { this.onPreprocessorDirectiveWarning(); }   #preprocessorDiagnostic
	| REGION TEXT? directive_new_line_or_sharp { this.onPreprocessorDirectiveRegion(); }   #preprocessorRegion
	| ENDREGION TEXT? directive_new_line_or_sharp { this.onPreprocessorDirectiveEndregion(); }   #preprocessorRegion
	| PRAGMA TEXT directive_new_line_or_sharp { this.onPreprocessorDirectivePragma(); }   #preprocessorPragma
	| NULLABLE TEXT directive_new_line_or_sharp { this.onPreprocessorDirectiveNullable(); }   #preprocessorNullable
	;

directive_new_line_or_sharp
    : DIRECTIVE_NEW_LINE
    | EOF
    ;

preprocessor_expression returns [String value]
	: TRUE { this.onPreprocessorExpressionTrue(); }
	| FALSE { this.onPreprocessorExpressionFalse(); }
	| CONDITIONAL_SYMBOL { this.onPreprocessorExpressionConditionalSymbol(); }
	| OPEN_PARENS expr=preprocessor_expression CLOSE_PARENS { this.onPreprocessorExpressionConditionalOpenParens(); }
	| BANG expr=preprocessor_expression { this.onPreprocessorExpressionConditionalBang(); }
	| expr1=preprocessor_expression OP_EQ expr2=preprocessor_expression { this.onPreprocessorExpressionConditionalEq(); }
	| expr1=preprocessor_expression OP_NE expr2=preprocessor_expression { this.onPreprocessorExpressionConditionalNe(); }
	| expr1=preprocessor_expression OP_AND expr2=preprocessor_expression { this.onPreprocessorExpressionConditionalAnd(); }
	| expr1=preprocessor_expression OP_OR expr2=preprocessor_expression { this.onPreprocessorExpressionConditionalOr(); }
	;
