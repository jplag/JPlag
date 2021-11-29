package de.jplag.java15;

import java.io.File;

import de.jplag.AbstractParser;
import de.jplag.TokenList;
import de.jplag.java15.grammar.JavaParser;
import de.jplag.java15.grammar.Token;

public class Parser extends AbstractParser implements JavaTokenConstants {
	private String actFile;
	private boolean useMethodSeparators;

	private TokenList struct;

	public Parser(boolean useMethodSeparators) {
		this.useMethodSeparators = useMethodSeparators;
	}

	public TokenList parse(File dir, String files[]) {
		struct = new TokenList();
		errors = 0;

		JavaParser parser = null; // This will be (re)initialised in parseFile()

		for (int i = 0; i < files.length; i++) {
			actFile = files[i];
			getProgram().print(null, "Parsing file " + files[i]);
			if (!JavaParser.parseFile(dir, files[i], parser, this))
				errors++;

			struct.addToken(new JavaToken(FILE_END, actFile, -1, -1, -1));
		}

		this.parseEnd();
		return struct;
	}

	public void add(int type, Token token) {
		if (type == SEPARATOR_TOKEN && !useMethodSeparators)
			return;

		JavaToken tok = new JavaToken(type, actFile, token.beginLine, token.beginColumn, token.image.length());
		struct.addToken(tok);

		/*
		 * getProgram().print(null,token.beginLine+"\t"+
		 * JavaToken.type2string(type)+"\t"+ token.image);
		 */
	}
}
