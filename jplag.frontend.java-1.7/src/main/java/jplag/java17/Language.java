package jplag.java17;

import java.io.File;

import jplag.ProgramI;

public class Language implements jplag.Language {
	private jplag.java17.Parser parser;

	public Language(ProgramI program) {
		this.parser = new jplag.java17.Parser();
		this.parser.setProgram(program);
	}

	public String[] suffixes() {
		String[] res = { ".java", ".jav", ".JAVA", ".JAV" };
		return res;
	}

	public int errorsCount() {
		return this.parser.errorsCount();
	}

	public String name() {
		return "Java1.7 Parser";
	}

	public String getShortName() {
		return "java17";
	}

	public int min_token_match() {
		return 9;
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
		return jplag.java17.JavaToken.numberOfTokens();
	}

	public String type2string(int type) {
		return jplag.java17.JavaToken.type2string(type);
	}
}
