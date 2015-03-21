/* Hey Emacs, this is -*- mode: java; page-delimiter: "^%%$"; -*- */

header
{

package jplag.text;

import jplag.InputState;
import jplag.ParserToken;

//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.io.FileInputStream;
import java.io.IOException;
}

// tell ANTLR that we want to generate Java source code
options
{
  language="Java";
}

// Import the necessary classes
{
  //import antlr.CharBuffer;
  //import antlr.collections.AST;
  //import antlr.ParserException;
}

class TextParser extends Parser;
options
{
  k = 2;			  // two token lookahead
  //  exportVocab=Text;	          // Call its vocabulary "Text"
  codeGenMakeSwitchThreshold = 2; // Some optimizations
  codeGenBitsetTestThreshold = 3;
  defaultErrorHandler = true;
  buildAST = false;
  ASTLabelType = "ParserToken";
}

{
public jplag.text.Parser parser;
  /**
   * The main routine is just for testing.
   */
  /*public static void main(String[] args) throws Exception
    {
      try
	{
	  InputStream in = new FileInputStream(args[0]);
	  WordParser parser = new WordParser(in);
	  AST ast = parser.makeAST();
	  ASTVisitor visitor = new ASTVisitor(System.out);
	  visitor.visit(ast);
	}
      catch(Exception e)
        {
	  System.err.println("exception: " + e);
	  throw e;   // so we can get stack trace
        }      
	}*/
}

file : ( w:WORD { parser.add(w); } | PUNCTUATION | SPECIALS )* EOF ;

//----------------------------------------------------------------------------
// The Text scanner
//----------------------------------------------------------------------------

class TextLexer extends Lexer;
options
{
  //  exportVocab=Text;        // call the vocabulary "Text"
  testLiterals = false;    // don't automatically test for literals
  k = 2;                   // two characters of lookahead
  charVocabulary = '\u0000'..'\u00FF';
}

{
    public void newline() {
	  super.newline();
	  ((InputState)inputState).column = 1;
    }

    public void consume() throws antlr.CharStreamException {
      if ( inputState.guessing == 0 ) {
	    InputState state = (InputState)inputState;
	    if (text.length()==0) {
	      // remember token start column
	      state.tokColumn = state.column;
	    }
	    state.column++;
      }
      super.consume();
    }
    
    protected Token makeToken(int t) {
      ParserToken tok = (ParserToken)super.makeToken(t);
      tok.setColumn(((InputState)inputState).tokColumn);
      return tok;
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
	  |	'\t' //{ ((InputState)inputState).column += 7; }
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
