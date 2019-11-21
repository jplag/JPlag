package jplag.csharp;

import java.io.*;

import jplag.*;
import jplag.csharp.grammar.CSharpLexer;
import jplag.csharp.grammar.CSharpParser;
import org.jetbrains.annotations.NotNull;

public class Parser extends StreamParser implements CSharpTokenConstants {
	private Structure struct;
	private String currentFile;

	@NotNull
	@Override
	public Token getEndOfFileToken(String file) {
		return new CSharpToken(FILE_END, file, -1, -1, -1);
	}

	public static void main(String args[]) {
		if (args.length != 1) {
			System.out.println("Only one parameter allowed.");
			System.exit(-1);
		}
		Parser parser = new Parser();
		parser.setProgram(new StrippedProgram());
		jplag.Structure struct = parser.parse(new File(args[0]).getParentFile(), new String[] { new File(args[0]).getName() });
		try {
			BufferedReader reader = new BufferedReader(new FileReader(new File(args[0])));
			int lineNr = 1;
			int token = 0;
			String line;
			while ((line = reader.readLine()) != null) {
				if (token < struct.size()) {
					boolean first = true;
					while (struct.tokens[token] != null && struct.tokens[token].getLine() == lineNr) {
						if (!first)
							System.out.println();
						jplag.Token tok = struct.tokens[token];
						System.out.print(CSharpToken.type2string(tok.type) + " (" + tok.getLine() + "," + tok.getColumn() + ","
								+ tok.getLength() + ")\t");
						first = false;
						token++;
					}
					if (first)
						System.out.print(" \t");
				} else
					System.out.print(" \t");
				System.out.println(line);
				lineNr++;
			}
			reader.close();
		} catch (IOException e) {
			System.out.println(e);
		}
	}

	@Override
	public boolean parseStream(@NotNull InputStream stream, @NotNull final TokenAdder adder) throws IOException {
		try {
			CSharpLexer lexer = new CSharpLexer(new UnicodeReader(stream, "UTF-8"));
			lexer.setFilename(adder.currentFile);
			lexer.setTabSize(1);

			// Create a parser that reads from the scanner
			CSharpParser parser = new CSharpParser(lexer);
			parser.setFilename(adder.currentFile);
			parser.parser = new CSharpTokenAdder(adder);//Added by emeric 22.01.05
			// start parsing at the compilationUnit rule
			parser.compilation_unit();
		} catch (Exception e) {
			getProgram().addError("  Parsing Error in '" + adder.currentFile + "':\n  " + e.toString() + "\n");
			return false;
		}

		return true;
	}
}
