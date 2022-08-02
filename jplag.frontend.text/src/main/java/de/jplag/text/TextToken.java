package de.jplag.text;

import de.jplag.Token;

public class TextToken extends Token {

    private String text;

    public TextToken(int type, String file) {
        super(type, file, -1, -1, -1);
    }

    public TextToken(String text, int type, String file, ParserToken parserToken) {
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
