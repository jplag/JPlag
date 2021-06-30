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

	@Override
    public int errorsCount() {
		return this.scanner.errorsCount();
	}

	@Override
    public String[] suffixes() {
		String[] res = { ".cpp", ".CPP", ".cxx", ".CXX", ".c++", ".C++", ".c", ".C", ".cc", ".CC", ".h", ".H",
				".hpp", ".HPP", ".hh", ".HH" };
		return res;
	}

	@Override
    public String name() {
		return "C/C++ Scanner [basic markup]";
	}

	@Override
    public String getShortName() {
		return "cpp";
	}

	@Override
    public int min_token_match() {
		return 12;
	}

	@Override
    public boolean supportsColumns() {
		return false;
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
    public jplag.TokenList parse(File dir, String[] files) {
		return this.scanner.scan(dir, files);
	}

	@Override
    public boolean errors() {
		return this.scanner.getErrors();
	}

	@Override
    public int noOfTokens() {
		return jplag.cpp.CPPToken.numberOfTokens();
	}

	@Override
    public String type2string(int type) {
		return jplag.cpp.CPPToken.type2string(type);
	}
}
