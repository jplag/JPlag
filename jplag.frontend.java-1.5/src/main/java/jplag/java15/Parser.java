package jplag.java15;

import java.io.File;

import jplag.java15.grammar.JavaParser;
import jplag.java15.grammar.Token;

public class Parser extends jplag.Parser implements JavaTokenConstants {
	private String actFile;
	private boolean useMethodSeparators;

	private jplag.TokenList struct;

	public Parser(boolean useMethodSeparators) {
		this.useMethodSeparators = useMethodSeparators;
	}

	public jplag.TokenList parse(File dir, String files[]) {
		struct = new jplag.TokenList();
		errors = 0;

		JavaParser parser = null; // This will be (re)initialised in parseFile()

		for (int i = 0; i < files.length; i++) {
			actFile = files[i];
			getProgram().print(null, "Parsing file " + files[i] + "\n");
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
