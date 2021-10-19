package de.jplag.chars;

import java.io.File;

import de.jplag.ProgramI;
import de.jplag.Token;
import de.jplag.TokenList;

/*
 * read in text files as characters
 */
public class Language implements de.jplag.Language {
	private ProgramI program;

	private de.jplag.chars.Parser parser = new Parser();

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
	 * @see de.jplag.Language#errorsCount()
	 */
	@Override
    public int errorsCount() {
		return this.parser.errorsCount();
	}

	@Override
    public String name() {
		return "Character AbstractParser";
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
    public TokenList parse(File dir, String[] files) {
		return this.parser.parse(dir, files);
	}

	@Override
    public boolean hasErrors() {
		return this.parser.hasErrors();
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
		return 36;
	}

	@Override
    public String type2string(int type) {
		return Token.type2string(type);
	}
}
