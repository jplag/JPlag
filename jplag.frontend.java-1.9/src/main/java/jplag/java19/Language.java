package jplag.java19;

import java.io.File;

import jplag.ProgramI;
import jplag.Structure;

/**
 * Hello world!
 *
 */
public class Language implements jplag.Language {
	private Parser parser;

	public Language(ProgramI program) {
		this.parser = new Parser();
		this.parser.setProgram(program);

	}
	
    public String[] suffixes() {
    	String[] res = { ".java", ".JAVA" };
		return res;
	}

	public String name() {
		return "Javac 1.9+ based AST plugin";
	}

	public String getShortName() {
		return "java19";
	}

	public int min_token_match() {
		return 9;
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
		return JavaToken.numberOfTokens();
	}

	public String type2string(int type) {
		return JavaToken.type2string(type);
	}


	public Structure parse(File dir, String[] files) {
		return this.parser.parse(dir, files);
	}

	public boolean errors() {
		return this.parser.getErrors();
	}

	public int errorsCount() {
		return this.parser.errorsCount();
	}

}
