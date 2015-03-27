package jplag.cpp;

import java.io.File;

import jplag.Parser;
import jplag.StrippedProgram;

public class Scanner extends Parser implements CPPTokenConstants {
	private String actFile;

	private jplag.Structure struct;

	public static void main(String args[]) {
		System.out.print("File: ");
		for (int i = 0; i < args.length; i++) {
			System.out.print(args[i] + " ");
		}
		System.out.println();

		Scanner scanner = new Scanner();
		scanner.setProgram(new StrippedProgram());

		scanner.scan(new File("."), args);
	}

	public jplag.Structure scan(File dir, String files[]) {
		struct = new jplag.Structure();
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
