package jplag.generic;

import jplag.ProgramI;

class JavascriptToken extends GenericToken {
    public JavascriptToken(int type, String file, int line, int column, int length) {
        super(type, file, line, column, length);
    }
}

class JavascriptParser extends GenericParser {
    @Override
    protected GenericToken makeToken(int type, String file, int line, int column, int length) {
        return new JavascriptToken(type, file, line, column, length);
    }

    @Override
    protected String getCommandLineProgram() {
        return "javascript_to_jplag_parser";
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