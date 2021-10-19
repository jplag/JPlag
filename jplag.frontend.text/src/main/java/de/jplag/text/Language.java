
package de.jplag.text;

import java.io.File;

import de.jplag.ProgramI;

/**
 * @Changed by Emeric Kwemou 29.01.2005
 *  
 */
public class Language implements de.jplag.Language {

	private ProgramI program;

	private de.jplag.text.Parser parser = new de.jplag.text.Parser();

	public Language(ProgramI program) {
		this.program = program;
		this.parser.setProgram(this.program);
	}

	@Override
    public int errorsCount() {
		return this.parser.errorsCount();
	}

	@Override
    public String[] suffixes() {
		String[] res = { ".TXT", ".txt", ".ASC", ".asc", ".TEX", ".tex" };
		return res;
	}

	@Override
    public String name() {
		return "Text AbstractParser";
	}

	@Override
    public String getShortName() {
		return "text";
	}

	@Override
    public int min_token_match() {
		return 5;
	}

	@Override
    public de.jplag.TokenList parse(File dir, String[] files) {
		return this.parser.parse(dir, files);
	}

	@Override
    public boolean errors() {
		return this.parser.getErrors();
	}

	@Override
    public boolean supportsColumns() {
		return true;
	}

	@Override
    public boolean isPreformatted() {
		return false;
	}

	@Override
    public boolean usesIndex() {
		return false;
	}

	@Override
    public int noOfTokens() {
        return parser.tokenStructure.serial;
//		return de.jplag.text.TextToken.numberOfTokens();   // always returns 1 ....
	}

	@Override
    public String type2string(int type) {
		return TextToken.type2string(type);
	}
}
