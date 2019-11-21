package jplag.generic;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import jplag.ProgramI;

abstract public class GenericLanguage implements jplag.Language {
	private GenericParser parser;

	abstract public String[] suffixes();
	abstract public String name();
	abstract public String getShortName();
	abstract public int min_token_match();
	protected abstract GenericParser makeParser();

	public GenericLanguage(ProgramI program) {
		this.parser = this.makeParser();
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
		return this.parser.getEndOfFileToken("").numberOfTokens();
	}

	public String type2string(int type) {
		return GenericToken.type2string(type);
	}

	public static void debugWithFile(GenericParser parser, String filePath) {
		String[] args = new String[]{filePath};
		parser.setProgram(new jplag.StrippedProgram());
		jplag.Structure struct = parser.parse(null, args);

		try {
			BufferedReader reader = new BufferedReader(new FileReader(new File(args[0])));
			int lineNr = 1;
			int token = 0;
			String line;
			while ((line = reader.readLine()) != null) {
				if (token < struct.size()) {
					boolean first = true;
					while (struct.tokens[token] != null
							&& struct.tokens[token].getLine() == lineNr) {
						if (!first)
							System.out.println();
						GenericToken tok = (GenericToken) struct.tokens[token];
						System.out.print(GenericToken.type2string(tok.type) + " ("
								+ tok.getLine() + ","
								+ tok.getColumn() + ","
								+ tok.getLength() + ")\t");
						first = false;
						token++;
					}
					if (first)
						System.out.print("                \t");
				}
				System.out.println(line);
				lineNr++;
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
