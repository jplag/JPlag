package jplag.chars;

import java.io.File;

import jplag.ProgramI;

/*
 * read in text files as characters
 */
public class Language implements jplag.Language {
	private ProgramI program;

	private jplag.chars.Parser parser = new Parser();

	public Language(ProgramI program) {
		this.program = program;
		this.parser.setProgram(this.program);
	}

	public String[] suffixes() {
		String[] res = { ".TXT", ".txt", ".ASC", ".asc", ".TEX", ".tex" };
		return res;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jplag.Language#errorsCount()
	 */
	public int errorsCount() {
		// TODO Auto-generated method stub
		return this.parser.errorsCount();
	}

	public String name() {
		return "Character Parser";
	}

	public String getShortName() {
		return "char";
	}

	public int min_token_match() {
		return 10;
	}

	public jplag.Structure parse(File dir, String[] files) {
		return this.parser.parse(dir, files);
	}

	public boolean errors() {
		return this.parser.getErrors();
	}

	public boolean supportsColumns() {
		return false;
	}

	public boolean isPreformated() {
		return false;
	}

	public boolean usesIndex() {
		return true;
	}

	public int noOfTokens() {
		return jplag.chars.CharToken.numberOfTokens();
	}

	public String type2string(int type) {
		return jplag.chars.CharToken.type2string(type);
	}
}
