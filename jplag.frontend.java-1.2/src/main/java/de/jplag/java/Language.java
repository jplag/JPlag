package de.jplag.java;

import java.io.File;

import de.jplag.Program;
import de.jplag.TokenList;

public class Language implements de.jplag.Language {
	private Parser parser;

	public Language(Program program) {
		this.parser = new Parser();
		this.parser.setProgram(program);
	}

	@Override
    public String[] suffixes() {
		String[] res = { ".java", ".jav", ".JAVA", ".JAV" };
		return res;
	}

	@Override
    public int errorCount() {
		return this.parser.errorsCount();
	}

	@Override
    public String getName() {
		return "Java1.2 AbstractParser";
	}

	@Override
    public String getShortName() {
		return "java12";
	}

	@Override
    public int minimumTokenMatch() {
		return 9;
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
    public int numberOfTokens() {
		return JavaTokenConstants.NUM_DIFF_TOKENS;
	}

	@Override
    public String type2string(int type) {
		return JavaToken.type2string(type);
	}
}
