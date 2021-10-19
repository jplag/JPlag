package de.jplag.chars;

import de.jplag.Token;

public class CharToken extends Token {
	private static final long serialVersionUID = 1L;

	private int index;

	public CharToken(int type, String file, int index, Parser parser) {
		super(type, file, -1);
		this.index = index;
	}

	public CharToken(int type, String file, Parser parser) {
		this(type, file, -1, parser);
	}

	@Override
    public int getLine() {
		return index; // so that the result-index is sorted correctly
	}

	@Override
    public int getColumn() { return 0; }

	@Override
    public int getLength() { return 0; }

	@Override
    public int getIndex() { return index; }
}
