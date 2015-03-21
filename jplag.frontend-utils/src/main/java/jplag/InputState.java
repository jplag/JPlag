package jplag;

import java.io.InputStream;
import java.io.Reader;

import antlr.InputBuffer;
import antlr.LexerSharedInputState;

/** This object contains the data associated with an
 *  input stream of characters.  Multiple lexers
 *  share a single LexerSharedInputState to lex
 *  the same input stream.
 */
public class InputState extends LexerSharedInputState {
  public int column = 0;
  public int tokColumn = 0;
  // public int guessing = 0;
  public InputState(InputBuffer inbuf) {
    super(inbuf);
    column = 1;
    line = 1;
  }
  public InputState(InputStream in) {
    super(in);
    column = 1;
    line = 1;
  }
  public InputState(Reader in) {
    super(in);
    column = 1;
    line = 1;
  }
  
  public int getLine() { return line; }
}






