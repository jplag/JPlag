package de.jplag.java15;

import java.io.File;

import de.jplag.ProgramI;

/**
 * Java 1.5 parser with method separators; if you know why these separators
 * exist, PLEASE tell us or document at
 * https://svn.ipd.kit.edu/trac/de/jplag/wiki/Server/Frontends/Java-1.5)
 */
public class LanguageWithDelimitedMethods implements de.jplag.Language {
	private Parser parser;

	public LanguageWithDelimitedMethods(ProgramI program) {
		this.parser = new Parser(true);
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
		return "Java1.5 Parser with delimited methods";
	}

	@Override
    public String getShortName() {
		return "java15dm";
	}

	@Override
    public int min_token_match() {
		return 8;
	}

	@Override
    public de.jplag.TokenList parse(File dir, String[] files) {
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
		return de.jplag.java15.JavaToken.type2string(type);
	}
}
