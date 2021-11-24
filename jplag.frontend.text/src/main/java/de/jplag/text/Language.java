
package de.jplag.text;

import java.io.File;

import de.jplag.ErrorReporting;
import de.jplag.TokenList;

/**
 * @Changed by Emeric Kwemou 29.01.2005
 *  
 */
public class Language implements de.jplag.Language {

	private ErrorReporting program;

	private Parser parser = new Parser();

	public Language(ErrorReporting program) {
		this.program = program;
		this.parser.setProgram(this.program);
	}

	@Override
    public int errorCount() {
		return this.parser.errorsCount();
	}

	@Override
    public String[] suffixes() {
		String[] res = { ".TXT", ".txt", ".ASC", ".asc", ".TEX", ".tex" };
		return res;
	}

	@Override
    public String getName() {
		return "Text AbstractParser";
	}

	@Override
    public String getShortName() {
		return "text";
	}

	@Override
    public int minimumTokenMatch() {
		return 5;
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
		return false;
	}

	@Override
    public boolean usesIndex() {
		return false;
	}

	@Override
    public int numberOfTokens() {
        return parser.tokenStructure.serial;
//		return de.jplag.text.TextToken.numberOfTokens();   // always returns 1 ....
	}

	@Override
    public String type2string(int type) {
		return TextToken.type2string(type);
	}
}
