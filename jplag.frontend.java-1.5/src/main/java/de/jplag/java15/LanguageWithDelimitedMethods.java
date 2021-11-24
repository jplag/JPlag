package de.jplag.java15;

import java.io.File;

import de.jplag.Language;
import de.jplag.ErrorReporting;
import de.jplag.TokenList;

/**
 * Java 1.5 parser with method separators; if you know why these separators
 * exist, PLEASE tell us or document at
 * https://svn.ipd.kit.edu/trac/de/jplag/wiki/Server/Frontends/Java-1.5)
 */
public class LanguageWithDelimitedMethods implements Language {
	private Parser parser;

	public LanguageWithDelimitedMethods(ErrorReporting program) {
		this.parser = new Parser(true);
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
		return "Java1.5 Parser with delimited methods";
	}

	@Override
    public String getShortName() {
		return "java15dm";
	}

	@Override
    public int minimumTokenMatch() {
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
    public int numberOfTokens() {
		return JavaTokenConstants.NUM_DIFF_TOKENS;
	}

	@Override
    public String type2string(int type) {
		return JavaToken.type2string(type);
	}
}
