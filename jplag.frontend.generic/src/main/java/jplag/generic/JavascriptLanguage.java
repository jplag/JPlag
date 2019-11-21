package jplag.generic;

import jplag.ProgramI;

class JavascriptToken extends GenericToken {
    public JavascriptToken(int type, String file, int line, int column, int length) {
        super(type, file, line, column, length);
    }

    @Override
    protected String getCommand() {
        return JavascriptParser.command;
    }
}

public class JavascriptLanguage extends GenericLanguage {
    public JavascriptLanguage(ProgramI program) {
        super(program);
    }

    @Override
    protected GenericParser makeParser() {
        return new JavascriptParser();
    }

    @Override
    public String[] suffixes() {
        return new String[]{".js", ".JS"};
    }

    @Override
    public String name() {
        return "Javascript Parser";
    }

    @Override
    public String getShortName() {
        return "Javascript";
    }

    @Override
    public int min_token_match() {
        return 8;
    }
}
