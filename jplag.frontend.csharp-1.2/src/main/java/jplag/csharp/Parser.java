package jplag.csharp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;

import jplag.StrippedProgram;
import jplag.Structure;
import jplag.UnicodeReader;
import jplag.csharp.grammar.CSharpLexer;
import jplag.csharp.grammar.CSharpParser;

public class Parser extends jplag.Parser implements CSharpTokenConstants {
	private Structure struct;
	private String currentFile;

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

	public jplag.Structure parse(File dir, String files[]) {
		struct = new Structure();
		errors = 0;
		for (int i = 0; i < files.length; i++) {
			//			getProgram().print(null, "Parsing file " + files[i] + "\n");
			if (!parseFile(dir, files[i]))
				errors++;
			struct.addToken(new CSharpToken(FILE_END, files[i], -1, -1, -1));
		}
		this.parseEnd();
		return struct;
	}

	private boolean parseFile(File dir, String file) {
		try {
			FileInputStream fis = new FileInputStream(new File(dir, file));
			currentFile = file;
			// Create a scanner that reads from the input stream passed to us
			CSharpLexer lexer = new CSharpLexer(new UnicodeReader(fis, "UTF-8"));
			lexer.setFilename(file);
			lexer.setTabSize(1);

			// Create a parser that reads from the scanner
			CSharpParser parser = new CSharpParser(lexer);
			parser.setFilename(file);
			parser.parser = this;//Added by emeric 22.01.05
			// start parsing at the compilationUnit rule
			parser.compilation_unit();

			// close file
			fis.close();
		} catch (Exception e) {
			getProgram().addError("  Parsing Error in '" + file + "':\n  " + e.toString() + "\n");
			return false;
		}
		return true;
	}

	public void add(int type, antlr.Token tok) {
		if (tok == null) {
			System.out.println("tok == null  ERROR!");
			return;
		}
		struct.addToken(new CSharpToken(type, currentFile, tok.getLine(), tok.getColumn(), tok.getText().length()));
		//     System.out.println("type: " + CSharpToken.type2string(type) +
		// 		       " text: '"+tok.getText()+"'");
	}

	public void add(int type, CSharpParser p) {
		add(type, p.getLastConsumedToken());
	}
}
