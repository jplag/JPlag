package de.jplag.javax;

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
    public int errorCount() {
		return this.parser.errorsCount();
	}

	@Override
    public String[] suffixes() {
		String[] res = { ".java", ".jav", ".JAVA", ".JAV" };
		return res;
	}

	@Override
    public String getName() {
		return "experimental Java1.1";
	}

	@Override
    public String getShortName() {
		return "Java1.1(exp)";
	}

	@Override
    public int minimumTokenMatch() {
		return 15;
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
    public TokenList parse(File dir, String[] files) {
		return this.parser.parse(dir, files);
	}

	@Override
    public boolean hasErrors() {
		return this.parser.hasErrors();
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
