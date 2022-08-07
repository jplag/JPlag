header {
package de.jplag.text;
}

// tell ANTLR that we want to generate Java source code
options {
  language="Java";
}

class TextParser extends Parser;
options {
  k = 2;			  // two token lookahead
  //  exportVocab=Text;	          // Call its vocabulary "Text"
  codeGenMakeSwitchThreshold = 2; // Some optimizations
  codeGenBitsetTestThreshold = 3;
  defaultErrorHandler = true;
  buildAST = false;
  ASTLabelType = "AntlrParserToken";
}

{
    private de.jplag.text.ParserAdapter parser;

    public void setParserAdapter(de.jplag.text.ParserAdapter adapter) {
        this.parser = adapter;
    }
}

file : ( w:WORD { parser.add(w); } | PUNCTUATION | SPECIALS )* EOF ;

//----------------------------------------------------------------------------
// The Text scanner
//----------------------------------------------------------------------------

{
import de.jplag.text.InputState;
import de.jplag.text.AntlrParserToken;
}

class TextLexer extends Lexer;
options {
    //  exportVocab=Text;    // call the vocabulary "Text"
    testLiterals = false;    // don't automatically test for literals
    k = 2;                   // two characters of lookahead
    charVocabulary = '\u0000'..'\u00FF';
}

{
    public void newline() {
        super.newline();
        ((InputState) inputState).setColumnIndex(1);
    }

    public void consume() throws antlr.CharStreamException {
        if (inputState.guessing == 0) {
            InputState state = (InputState) inputState;
            if (text.length() == 0) {
                // remember token start column
                state.setTokenColumnIndex(state.getColumnIndex());
            }
            state.setColumnIndex(state.getColumnIndex() + 1);
        }
        super.consume();
    }

    protected Token makeToken(int t) {
        AntlrParserToken token = (AntlrParserToken) super.makeToken(t);
        token.setColumn(((InputState) inputState).getTokenColumnIndex());
        return token;
    }
}

WORD
options { paraphrase = "an identifier"; } :
  (( '0'..'9') | ('A'..'Z') | ('a'..'z') |
   ('\300' .. '\326') | ('\330' .. '\366') | ('\370' .. '\377'))+ ;

PUNCTUATION : (	'!' | '"' | '\'' | '(' | ')' | ',' | '-' | '.' |
		':' | ';' | '?'  | '[' | ']' | '`' | '{' | '}' |
		'\253' | '\264' | '\273' | '\277' | '\0') ;

SPECIALS : ('#' | '$' | '%' | '&' | '+' | '<' | '=' | '*' |
	    '/' | '>' | '@' | '\\' | '^' | '_' | '|' | '~' |
	    ('\241' .. '\252') | ('\254' .. '\263') | ('\265' .. '\272') |
	    ('\274' .. '\276') | '\327' | '\367' | ('\200' .. '\237') ) ;

// Whitespace -- ignored
SPACE : ( ' '
	  |	'\t' //{ ((InputState)inputState).setColumnIndex((InputState)inputState).getColumnIndex() + 7); }
	  |	'\f'
	  |     '\240'
	  |     ('\001' .. '\010')
	  |     ('\016' .. '\037')
	  |     '\013'
	  |     '\177'
	) { _ttype = Token.SKIP; } ;

NEWLINE	: // handle newlines
  ( "\r\n" | // Evil DOS
    '\r'   | // Macintosh
    '\n'   ) // Unix (the right way)
  { newline(); _ttype = Token.SKIP; } ;
