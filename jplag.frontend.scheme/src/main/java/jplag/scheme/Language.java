package jplag.scheme;

import java.io.File;

import jplag.ProgramI;

public class Language implements jplag.Language {

	public Language(ProgramI program) {
		this.parser = new Parser();
		this.parser.setProgram(program);

	}

	@Override
    public int errorsCount() {
		return this.parser.errorsCount();
	}

	private jplag.scheme.Parser parser; // Not yet instantiated? See constructor!

	@Override
    public String[] suffixes() {
		String[] res = { ".scm", ".SCM", ".ss", ".SS" };
		return res;
	}

	@Override
    public String name() {
		return "SchemeR4RS Parser [basic markup]";
	}

	@Override
    public String getShortName() {
		return "scheme";
	}

	@Override
    public int min_token_match() {
		return 13;
	}

	@Override
    public boolean supportsColumns() {
		return false;
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
    public jplag.TokenList parse(File dir, String[] files) {
		return this.parser.parse(dir, files);
	}

	@Override
    public boolean errors() {
		return this.parser.getErrors();
	}

	@Override
    public int noOfTokens() {
		return jplag.scheme.SchemeToken.numberOfTokens();
	}

	@Override
    public String type2string(int type) {
		return jplag.scheme.SchemeToken.type2string(type);
	}
}
