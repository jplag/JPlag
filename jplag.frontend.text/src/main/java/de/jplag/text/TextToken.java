package de.jplag.text;

import de.jplag.Token;

public class TextToken extends Token {

    private static final String NO_TEXT = "<unknown>";
    private final String text;

    public TextToken(int type, String file) {
        super(type, file, -1, -1, -1);
        this.text = NO_TEXT;
    }

    public TextToken(String text, int type, String file, AntlrParserToken parserToken) {
        super(type, file, parserToken.getLine(), parserToken.getColumn(), parserToken.getLength());
        this.text = text.toLowerCase();
    }

    public String getText() {
        return this.text;
    }

    @Override
    protected String type2string() {
        return getText();
    }
}
