package de.jplag.csharp;

import java.io.File;

import de.jplag.ProgramI;

public class Language implements de.jplag.Language {
	private Parser parser;

	public Language(ProgramI program) {
		this.parser = new Parser();
		this.parser.setProgram(program);

	}

	@Override
    public String[] suffixes() {
		String[] res = { ".cs", ".CS" };
		return res;
	}

	@Override
    public int errorsCount() {
		return this.parser.errorsCount();
	}

	@Override
    public String name() {
		return "C# 1.2 AbstractParser";
	}

	@Override
    public String getShortName() {
		return "c#-1.2";
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
		return parser.hasErrors();
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
		return CSharpTokenConstants.NUM_DIFF_TOKENS;
	}

	@Override
    public String type2string(int type) {
		return de.jplag.csharp.CSharpToken.type2string(type);
	}
}
