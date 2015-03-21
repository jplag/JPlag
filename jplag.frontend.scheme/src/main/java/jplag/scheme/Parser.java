package jplag.scheme;

import java.io.File;

public class Parser extends jplag.Parser implements SchemeTokenConstants {
	private String actFile;

	private jplag.Structure struct;

	public static void main(String args[]) {
		Parser p = new Parser();
		System.out.println(p.parse(new File("."), args).toString());
	}

	public jplag.Structure parse(File dir, String files[]) {
		struct = new jplag.Structure();
		errors = 0;
		SchemeParser parser = null;// no worry it will be reinitialized
		// in method parseFile(...)
		for (int i = 0; i < files.length; i++) {
			actFile = files[i];
		    getProgram().print(null, "Parsing file " + files[i] + "\n");
			if (!SchemeParser.parseFile(dir, files[i], parser, this))
				errors++;
			struct.addToken(new SchemeToken(FILE_END, actFile, 1));
		}
		this.parseEnd();
		return struct;
	}

	public void add(int type, Token token) {
		struct.addToken(new SchemeToken(type, actFile, token.beginLine));
		/*
		 * System.out.println(token.beginLine+"\t"+ (new
		 * SchemeToken(0,null,0)).type2string(type)+"\t"+ token.image);
		 */
	}

}
