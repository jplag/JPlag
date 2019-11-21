package jplag.ipython;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import jplag.ProgramI;

public class IPythonLanguage implements jplag.Language {
	private IPythonParser parser;
	int minimum_token_match = Integer.MAX_VALUE;

	public String[] suffixes() {
		return new String[]{".ipynb", ".IPYNB"};
	}

	public String name() {
		return "IPython Language";
	}

	public String getShortName() {
		return "IPython";
	}

	public int min_token_match() {
		return 9;
	}

	public IPythonLanguage(ProgramI program) {
		this.parser = new IPythonParser();
		this.parser.setProgram(program);
	}

	public int errorsCount() {
		return this.parser.errorsCount();
	}

	public jplag.Structure parse(File dir, String[] files) {
		return this.parser.parse(dir, files);
	}

	public boolean errors() {
		return this.parser.getErrors();
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
		return parser.getEndOfFileToken("").numberOfTokens();
	}

	public String type2string(int type) {
		return IPythonToken.type2string(type);
	}


	public static void debugWithFile(IPythonParser parser, String ...filePaths) {
		for (String filePath : filePaths) {
			String[] args = new String[]{filePath};
			parser.setProgram(new jplag.StrippedProgram());
			jplag.Structure struct = parser.parse(null, args);

			try {
				BufferedReader reader = new BufferedReader(new FileReader(new File(args[0])));
				int lineNr = 0;
				int token = 0;
				String line;
				while ((line = reader.readLine()) != null) {
					boolean first = true;
					if (token < struct.size()) {
						while (struct.tokens[token] != null
								&& struct.tokens[token].getLine() == lineNr) {
							if (!first)
								System.out.println();
							IPythonToken tok = (IPythonToken) struct.tokens[token];
							System.out.println(IPythonToken.type2string(tok.type) + " ("
									+ tok.getLine() + ","
									+ tok.getColumn() + ","
									+ tok.getLength() + ")\t");
							first = false;
							token++;
						}
					}
					lineNr++;
				}
				reader.close();
			} catch (IOException e) {
				System.out.println(e);
			}
		}
	}
}
