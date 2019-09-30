package jplag.chars;

public class CharToken extends jplag.Token {
    private static final long serialVersionUID = 1L;

    private int index;
    private int line;

    private Parser parser;

    public CharToken(int type, String file, int index, Parser parser, int line) {
        super(type, file, line);
        this.index = index;
        this.parser = parser;
    }

    public CharToken(int type, String file, Parser parser, int line) {
        this(type, file, line, parser, line);
    }

    public int getLine() {
        return line;
    }

    protected void setLine(int line) {
        this.line = line;
    }

    public int getColumn() { return 0; }

    public int getLength() { return 0; }

    public int getIndex() { return index; }

    public static String type2string(int type, Parser parser) {
        return "" + parser.reverseMapping(type);
    }

    public static int numberOfTokens() { return 36; }
}
