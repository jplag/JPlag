package jplag.scheme;

import java.io.File;

import jplag.ProgramI;

public class Language implements jplag.Language {

	public Language(ProgramI program) {
		this.parser = new Parser();
		this.parser.setProgram(program);

	}

	public int errorsCount() {
		// TODO Auto-generated method stub
		return this.parser.errorsCount();
	}

	private jplag.scheme.Parser parser;//noch nicht instanziert? siehe
									   // Konstruktor

	public String[] suffixes() {
		String[] res = { ".scm", ".SCM", ".ss", ".SS" };
		return res;
	}

	public String name() {
		return "SchemeR4RS Parser [basic markup]";
	}

	public String getShortName() {
		return "scheme";
	}

	public int min_token_match() {
		return 13;
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
		return jplag.scheme.SchemeToken.numberOfTokens();
	}

	public String type2string(int type) {
		return jplag.scheme.SchemeToken.type2string(type);
	}
}
