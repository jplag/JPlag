package jplag.json;

import java.io.File;

import jplag.ProgramI;

public class Language implements jplag.Language {

    private jplag.json.Parser parser;

    public Language(ProgramI program) {
        this.parser = new jplag.json.Parser();
        this.parser.setProgram(program);
    }

    public String[] suffixes() {
        String[] res = {".json", ".JSON"};
        return res;
    }

    public int errorsCount() {
        return this.parser.errorsCount();
    }

    public String name() {
        return "Json Parser";
    }

    public String getShortName() {
        return "json";
    }

    public int min_token_match() {
        return 2;
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
        return JsonToken.tokenStructure.table.size();
    }

    public String type2string(int type) {
        return JsonToken.type2string(type);
    }
}
