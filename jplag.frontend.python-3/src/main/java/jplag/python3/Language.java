package jplag.python3;

import java.io.File;

import jplag.ProgramI;

public class Language implements jplag.Language {

    private jplag.python3.Parser parser;

    public Language(ProgramI program) {
        this.parser = new jplag.python3.Parser();
        this.parser.setProgram(program);
    }

    @Override
    public String[] suffixes() {
        String[] res = {".py"};
        return res;
    }

    @Override
    public int errorsCount() {
        return this.parser.errorsCount();
    }

    @Override
    public String name() {
        return "Python3 Parser";
    }

    @Override
    public String getShortName() {
        return "python3";
    }

    @Override
    public int min_token_match() {
        return 12;
    }

    @Override
    public jplag.TokenList parse(File dir, String[] files) {
        return this.parser.parse(dir, files);
    }

    @Override
    public boolean errors() {
        return this.parser.getErrors();
    }

    @Override
    public boolean supportsColumns() {
        return true;
    }

    @Override
    public boolean isPreformatted() {
        return true;
    }

    @Override
    public boolean usesIndex() {
        return false;
    }

    @Override
    public int noOfTokens() {
        return jplag.python3.Python3Token.numberOfTokens();
    }

    @Override
    public String type2string(int type) {
        return jplag.python3.Python3Token.type2string(type);
    }
}
