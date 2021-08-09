package jplag.java;

import java.io.File;
import java.io.FileInputStream;

import jplag.InputState;
import jplag.ParserToken;
import jplag.TokenList;
import jplag.java.grammar.JLexer;
import jplag.java.grammar.JRecognizer;

public class Parser extends jplag.Parser implements JavaTokenConstants {
	private TokenList struct;
	private String currentFile;

	public jplag.TokenList parse(File dir, String files[]) {
		struct = new TokenList();
		errors = 0;
		for (int i = 0; i < files.length; i++) {
			getProgram().print(null, "Parsing file " + files[i] + "\n");
			if (!parseFile(dir, files[i]))
				errors++;
			System.gc();//Emeric
			struct.addToken(new JavaToken(FILE_END, files[i], -1, -1, -1));
		}
		this.parseEnd();
		return struct;
	}

	private boolean parseFile(File dir, String file) {
		InputState inputState = null;
		try {
			FileInputStream fis = new FileInputStream(new File(dir, file));
			currentFile = file;
			// Create a scanner that reads from the input stream passed to us
			inputState = new InputState(fis);
			JLexer lexer = new JLexer(inputState);
			lexer.setFilename(file);
			lexer.setTokenObjectClass("jplag.ParserToken");

			// Create a parser that reads from the scanner
			JRecognizer parser = new JRecognizer(lexer);
			parser.setFilename(file);
			parser.parser = this;

			// start parsing at the compilationUnit rule
			parser.compilationUnit();

			// close file
			fis.close();
		} catch (Exception e) {
			getProgram().addError(
					"  Parsing Error in '" + file + "':\n" + "  Parse error at line "
							+ (inputState != null ? "" + inputState.getLine() : "UNKNOWN") + ", column "
							+ (inputState != null ? "" + inputState.tokColumn : "UNKNOWN") + ": " + e.getMessage() + "\n");
			return false;
		}

		return true;
	}

	public void add(int type, antlr.Token tok) {
		ParserToken ptok = (ParserToken) tok;
		struct.addToken(new JavaToken(type, currentFile, ptok.getLine(), ptok.getColumn(), ptok.getLength()));
	}
}
