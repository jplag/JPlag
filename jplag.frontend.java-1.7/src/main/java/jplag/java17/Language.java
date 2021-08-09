package jplag.java17;

import java.io.File;

import jplag.ProgramI;

public class Language implements jplag.Language {
	private jplag.java17.Parser parser;

	public Language(ProgramI program) {
		this.parser = new jplag.java17.Parser();
		this.parser.setProgram(program);
	}

	@Override
    public String[] suffixes() {
		String[] res = { ".java", ".jav", ".JAVA", ".JAV" };
		return res;
	}

	@Override
    public int errorsCount() {
		return this.parser.errorsCount();
	}

	@Override
    public String name() {
		return "Java1.7 Parser";
	}

	@Override
    public String getShortName() {
		return "java17";
	}

	@Override
    public int min_token_match() {
		return 9;
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
		return jplag.java17.JavaToken.numberOfTokens();
	}

	@Override
    public String type2string(int type) {
		return jplag.java17.JavaToken.type2string(type);
	}
}
