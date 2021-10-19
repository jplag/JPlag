package de.jplag.cpp;

import java.io.File;

import de.jplag.AbstractParser;
import de.jplag.TokenList;

public class Scanner extends AbstractParser implements CPPTokenConstants {
	private String actFile;

	private TokenList struct;

	public TokenList scan(File dir, String files[]) {
		struct = new TokenList();
		errors = 0;
		CPPScanner scanner = null;// will be initialized in Method scanFile
		for (int i = 0; i < files.length; i++) {
			actFile = files[i];
		    getProgram().print(null, "Scanning file " + files[i] + "\n");
			if (!CPPScanner.scanFile(dir, files[i], scanner, this))
				errors++;
			struct.addToken(new CPPToken(FILE_END, actFile, 1));
		}
		this.parseEnd();
		return struct;
	}

	public void add(int type, Token token) {
		struct.addToken(new CPPToken(type, actFile, token.beginLine));
	}
}
