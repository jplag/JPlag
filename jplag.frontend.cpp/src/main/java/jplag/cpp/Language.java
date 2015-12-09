package jplag.cpp;

import java.io.File;

import jplag.ProgramI;

/*
 * Leider werden C/C++ nicht geparst, sondern nur gescannt...
 */
public class Language implements jplag.Language {
	private Scanner scanner;

	public Language(ProgramI program) {
		this.scanner = new Scanner();
		this.scanner.setProgram(program);

	}

	public int errorsCount() {
		// TODO Auto-generated method stub
		return this.scanner.errorsCount();
	}

	public String[] suffixes() {
		String[] res = { ".cpp", ".CPP", ".cxx", ".CXX", ".c++", ".C++", ".c", ".C", ".cc", ".CC", ".h", ".H",
				".hpp", ".HPP", ".hh", ".HH" };
		return res;
	}

	public String name() {
		return "C/C++ Scanner [basic markup]";
	}

	public String getShortName() {
		return "cpp";
	}

	public int min_token_match() {
		return 12;
	}

	public boolean supportsColumns() {
		return false;
	}

	public boolean isPreformated() {
		return true;
	}

	public boolean usesIndex() {
		return false;
	}

	public jplag.Structure parse(File dir, String[] files) {
		return this.scanner.scan(dir, files);
	}

	public boolean errors() {
		return this.scanner.getErrors();
	}

	public int noOfTokens() {
		return jplag.cpp.CPPToken.numberOfTokens();
	}

	public String type2string(int type) {
		return jplag.cpp.CPPToken.type2string(type);
	}
}
