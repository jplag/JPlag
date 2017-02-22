package jplag.python3;

import java.io.File;

import jplag.ProgramI;

public class Language implements jplag.Language {

    private jplag.python3.Parser parser;

    public Language(ProgramI program) {
        this.parser = new jplag.python3.Parser();
        this.parser.setProgram(program);
    }

    public String[] suffixes() {
        String[] res = {".py"};
        return res;
    }

    public int errorsCount() {
        return this.parser.errorsCount();
    }

    public String name() {
        return "Python3 Parser";
    }

    public String getShortName() {
        return "python3";
    }

    public int min_token_match() {
        return 12;
    }

    public jplag.Structure parse(File dir, String[] files) {
        return this.parser.parse(dir, files);
    }

    public boolean errors() {
        return this.parser.getErrors();
    }

    public boolean supportsColumns() {
        return true;
    }

    public boolean isPreformated() {
        return true;
    }

    public boolean usesIndex() {
        return false;
    }

    public int noOfTokens() {
        return jplag.python3.Python3Token.numberOfTokens();
    }

    public String type2string(int type) {
        return jplag.python3.Python3Token.type2string(type);
    }
}
