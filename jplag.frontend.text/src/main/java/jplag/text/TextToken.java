package jplag.text;

public class TextToken extends jplag.Token {
	private static final long serialVersionUID = 4301179216570538972L;

	public static int getSerial(String text, Parser parser) {
        text = text.toLowerCase();
        Integer obj = (Integer) parser.tokenStructure.table.get(text);
        if(obj == null) {
            obj = new Integer(parser.tokenStructure.serial);
            if(parser.tokenStructure.serial == Integer.MAX_VALUE)
                parser.outOfSerials();
            else
                parser.tokenStructure.serial++;
            parser.tokenStructure.table.put(text, obj);
            if(parser.tokenStructure.reverseMapping != null)
                parser.tokenStructure.reverseMapping = null;
        }
        return obj.intValue();
    }

    // throw away this method soon:

    public static String type2string(int i, TokenStructure tokenStructure) {
        if(tokenStructure.reverseMapping == null)
            tokenStructure.createReverseMapping();
        return tokenStructure.reverseMapping[i];
    }

    // ///////////////////// END OF STATIC MEMBERS

    private int line, column, length;
    private String text;

    public TextToken(int type, String file, Parser parser) {
        super(type, file, -1, -1, -1);
    }

    public TextToken(String text, String file, int line, int column,
            int length, Parser parser) {
        super(-1, file, line, column, length);
        this.type = getSerial(text, parser);
        this.text = text.toLowerCase();
    }

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }

    public int getLength() {
        return length;
    }

    public void setLine(int line) {
        this.line = line;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public String getText() {
        return this.text;
    }

    public static int numberOfTokens(TokenStructure tokenStructure) {
        return tokenStructure.table.size();
    }
}
