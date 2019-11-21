package jplag.generic;

import jplag.ProgramI;

class PhpToken extends GenericToken {
    public PhpToken(int type, String file, int line, int column, int length) {
        super(type, file, line, column, length);
    }

    @Override
    protected String getCommand() {
        return PhpParser.command;
    }
}

public class PhpLanguage extends GenericLanguage {
    public PhpLanguage(ProgramI program) {
        super(program);
    }

    @Override
    protected GenericParser makeParser() {
        return new PhpParser();
    }

    @Override
    public String[] suffixes() {
        return new String[]{".php", ".PHP"};
    }

    @Override
    public String name() {
        return "Php Parser";
    }

    @Override
    public String getShortName() {
        return "php";
    }

    @Override
    public int min_token_match() {
        return 9;
    }
}
