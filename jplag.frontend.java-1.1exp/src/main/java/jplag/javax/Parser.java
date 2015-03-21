package jplag.javax;

import java.io.*;

public class Parser extends jplag.Parser implements JavaTokenConstants {
	private String actFile;

	private jplag.Structure struct;

	public void main(String args[]) {
		System.out.println(parse(new File("."), args).toString());
	}

	public jplag.Structure parse(File dir, String files[]) {
		struct = new jplag.Structure();
		errors = 0;
		JavaParser parser = null;// no worry it will be reinitialized
		// in method parseFile(...)
		for (int i = 0; i < files.length; i++) {
			actFile = files[i];
			getProgram().print(null, "Parsing file " + files[i] + "\n");
			if (!JavaParser.parseFile(dir, files[i], parser, this))
				errors++;
			struct.addToken(new JavaToken(FILE_END, actFile, 0));
		}
		// System.err.println(struct.toString());
		if (errors == 0)
			program.print(null, "OK\n");
		else
			program.print(null, errors + " ERRORS\n");
		this.parseEnd();
		return struct;
	}

	public void add(int type, Token token) {
		struct.addToken(new JavaToken(type, actFile, token.beginLine));
		/*
		 * System.out.println(token.beginLine+"\t"+ (new
		 * JavaToken(0,null,0)).type2string(type)+"\t"+ token.image);
		 */
	}

}
