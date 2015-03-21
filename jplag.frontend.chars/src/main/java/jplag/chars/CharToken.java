package jplag.chars;

public class CharToken extends jplag.Token {
	private static final long serialVersionUID = 1L;

	private int index;

	private Parser parser;

	public CharToken(int type, String file, int index, Parser parser) {
		super(type, file, -1);
		this.index = index;
		this.parser = parser;
	}

	public CharToken(int type, String file, Parser parser) {
		this(type, file, -1, parser);
	}

	public int getLine() {
		return index; // so that the result-index is sorted correctly
	}

	public int getColumn() { return 0; }

	public int getLength() { return 0; }

	public int getIndex() { return index; }

	public static String type2string(int type, Parser parser) {
		return "" + parser.reverseMapping(type);
	}

	public static int numberOfTokens() { return 36;	}
}
