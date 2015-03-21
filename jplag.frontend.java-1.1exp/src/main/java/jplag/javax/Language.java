package jplag.javax;

import java.io.*;

import jplag.ProgramI;
import jplag.javax.Parser;

public class Language implements jplag.Language {
	private Parser parser;

	public Language(ProgramI program) {
		this.parser = new Parser();
		this.parser.setProgram(program);

	}

	public int errorsCount() {
		return this.parser.errorsCount();
	}

	public String[] suffixes() {
		String[] res = { ".java", ".jav", ".JAVA", ".JAV" };
		return res;
	}

	public String name() {
		return "experimental Java1.1";
	}

	public String getShortName() {
		return "Java1.1(exp)";
	}

	public int min_token_match() {
		return 15;
	}

	public boolean supportsColumns() {
		return false;
	}

	public boolean isPreformated() {
		return true;
	}

	public boolean usesIndex() {
		return false;
	}

	public jplag.Structure parse(File dir, String[] files) {
		return this.parser.parse(dir, files);
	}

	public boolean errors() {
		return this.parser.getErrors();
	}

	public int noOfTokens() {
		return jplag.javax.JavaToken.numberOfTokens();
	}

	public String type2string(int type) {
		return jplag.javax.JavaToken.type2string(type);
	}
}
