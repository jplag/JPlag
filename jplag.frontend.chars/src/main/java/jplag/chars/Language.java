package jplag.chars;

import java.io.File;

import jplag.ProgramI;
import jplag.Token;

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

	@Override
    public String[] suffixes() {
		String[] res = { ".TXT", ".txt", ".ASC", ".asc", ".TEX", ".tex" };
		return res;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jplag.Language#errorsCount()
	 */
	@Override
    public int errorsCount() {
		return this.parser.errorsCount();
	}

	@Override
    public String name() {
		return "Character Parser";
	}

	@Override
    public String getShortName() {
		return "char";
	}

	@Override
    public int min_token_match() {
		return 10;
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
    public boolean supportsColumns() {
		return false;
	}

	@Override
    public boolean isPreformatted() {
		return false;
	}

	@Override
    public boolean usesIndex() {
		return true;
	}

	@Override
    public int noOfTokens() {
		return jplag.chars.CharToken.numberOfTokens();
	}

	@Override
    public String type2string(int type) {
		return Token.type2string(type);
	}
}
