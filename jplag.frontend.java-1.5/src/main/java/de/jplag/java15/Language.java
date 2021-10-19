package de.jplag.java15;

import java.io.File;

import de.jplag.ProgramI;
import de.jplag.TokenList;

public class Language implements de.jplag.Language {
	private Parser parser;

	public Language(ProgramI program) {
		this.parser = new Parser(false);
		this.parser.setProgram(program);
	}

	@Override
    public int errorsCount() {
		return this.parser.errorsCount();
	}

	@Override
    public String[] suffixes() {
		String[] res = { ".java", ".jav", ".JAVA", ".JAV" };
		return res;
	}

	@Override
    public String name() {
		return "Java1.5 AbstractParser";
	}

	@Override
    public String getShortName() {
		return "java15";
	}

	@Override
    public int min_token_match() {
		return 8;
	}

	@Override
    public TokenList parse(File dir, String[] files) {
		return this.parser.parse(dir, files);
	}

	@Override
    public boolean hasErrors() {
		return this.parser.hasErrors();
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
		return JavaTokenConstants.NUM_DIFF_TOKENS;
	}

	@Override
    public String type2string(int type) {
		return JavaToken.type2string(type);
	}
}
