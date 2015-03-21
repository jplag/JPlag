package jplag;


public class ParserToken extends antlr.Token {
  /**
   * This variable holds the line number of the current token.
   */
  protected int _line = -1;

  /**
   * This variable holds the column of the current token in its line.
   */
  protected int _column = -1;

  /**
   * This variable holds the label of the current token.
   */
  protected String _text = null;

  /**
   * This variable holds the identifier of the current token.
   */

  protected int _id = -1;

  public ParserToken() { super(); }
  public ParserToken(int type) { super(type); }
  public ParserToken(int type, String text) {
    super(type, text); setText(text);
  }

  public void setLine(int line) { _line = line;}
  public void setColumn(int column) { _column = column; }
  public void setID(int id) { _id = id; }
  public void setText(String text) {
    _text = (text!=null ? text.intern() : null);
  }

  public int getColumn() { return _column; }
  public int getLine() { return _line; }
  public String getText() { return _text; }
  public int getID() { return _id; }

  public int getLength() { return _text.length(); }

  public String toString()
  {
    return "{\"" + getText() + "\", <" + getType() + ">, " +
      getLine() + " " + getColumn() + "}";
  }
}
