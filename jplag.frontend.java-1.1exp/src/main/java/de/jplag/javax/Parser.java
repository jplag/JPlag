package de.jplag.javax;

import java.io.File;

import de.jplag.AbstractParser;
import de.jplag.TokenList;

public class Parser extends AbstractParser implements JavaTokenConstants {
	private String actFile;

	private TokenList struct;

	public TokenList parse(File dir, String files[]) {
		struct = new TokenList();
		errors = 0;
		JavaParser parser = null;// no worry it will be reinitialized
		// in method parseFile(...)
		for (int i = 0; i < files.length; i++) {
			actFile = files[i];
			getErrorConsumer().print(null, "Parsing file " + files[i]);
			if (!JavaParser.parseFile(dir, files[i], parser, this))
				errors++;
			struct.addToken(new JavaToken(FILE_END, actFile, 0));
		}
		if (errors == 0)
			errorConsumer.print(null, "OK");
		else
			errorConsumer.print(null, errors + " ERRORS");
		this.parseEnd();
		return struct;
	}

	public void add(int type, Token token) {
		struct.addToken(new JavaToken(type, actFile, token.beginLine));
	}

}
