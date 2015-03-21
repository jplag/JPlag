package jplag.csharp;

import java.io.File;

import jplag.ProgramI;

public class Language implements jplag.Language {
	private Parser parser;

	public Language(ProgramI program) {
		this.parser = new Parser();
		this.parser.setProgram(program);

	}

	public String[] suffixes() {
		String[] res = { ".cs", ".CS" };
		return res;
	}

	public int errorsCount() {
		// TODO Auto-generated method stub
		return this.parser.errorsCount();
	}

	public String name() {
		return "C# 1.2 Parser";
	}

	public String getShortName() {
		return "c#-1.2";
	}

	public int min_token_match() {
		return 8;
	}

	public jplag.Structure parse(File dir, String[] files) {
		return this.parser.parse(dir, files);
	}

	public boolean errors() {
		return parser.getErrors();
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
		return jplag.csharp.CSharpToken.numberOfTokens();
	}

	public String type2string(int type) {
		return jplag.csharp.CSharpToken.type2string(type);
	}
}
