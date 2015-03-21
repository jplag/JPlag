package jplag.text;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;

import jplag.InputState;
import jplag.ParserToken;
import jplag.Structure;

/**
 * @Changed by Emeric Kwemou 29.01.2005
 *  
 */
public class Parser extends jplag.Parser implements jplag.TokenConstants {

	protected TokenStructure tokenStructure = new TokenStructure();

	private Structure struct;

	private String currentFile;

	private HashSet<String> filter = null;

	public void initializeFilter(String fileName) throws FileNotFoundException {
		File file = new File(fileName);

		filter = new HashSet<String>();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line;

			while ((line = reader.readLine()) != null) {
				line.trim();
				filter.add(line.toLowerCase());
			}

			System.out.println("Filter: " + filter.size() + " words read.");
			reader.close();
		} catch (Exception e) {
			System.out.println("Error reading filter file!");
			System.out.println(e);
			if (e instanceof FileNotFoundException)
				throw (FileNotFoundException) e;
		}
	}

	public jplag.Structure parse(File dir, String files[]) {
		struct = new Structure();
		errors = 0;
		for (int i = 0; i < files.length; i++) {
			getProgram().print("", "Parsing file " + files[i] + "\n");
			if (!parseFile(dir, files[i]))
				errors++;
			struct.addToken(new TextToken(FILE_END, files[i], this));
		}

		Structure tmp = struct;
		struct = null;
		this.parseEnd();
		return tmp;
	}

	public boolean parseFile(File dir, String file) {
		InputState inputState = null;
		try {
			FileInputStream fis = new FileInputStream(new File(dir, file));
			currentFile = file;
			// Create a scanner that reads from the input stream passed to us
			inputState = new InputState(fis);
			TextLexer lexer = new TextLexer(inputState);
			lexer.setFilename(file);
			lexer.setTokenObjectClass("jplag.ParserToken");

			// Create a parser that reads from the scanner
			TextParser parser = new TextParser(lexer);
			parser.setFilename(file);
			parser.parser = this;// Added by Emeric 26.01.05 BAD

			// start parsing at the compilationUnit rule
			parser.file();

			// close file
			fis.close();
		} catch (Exception e) {
			getProgram().addError("  Parsing Error in '" + file +
					"' (line " + (inputState != null ? "" + inputState.getLine()
					: "") + "):\n  " + e.getMessage());
			return false;
		}
		return true;
	}

	public void add(antlr.Token tok) {
		ParserToken ptok = (ParserToken) tok;
		if (filter != null && filter.contains(tok.getText().toLowerCase()))
			return;
		struct.addToken(new TextToken(tok.getText(), currentFile, ptok
				.getLine(), ptok.getColumn(), ptok.getLength(), this));
	}

	private boolean runOut = false;

	public void outOfSerials() {
		if (runOut)
			return;
		runOut = true;
		errors++;
		program.print("ERROR: Out of serials!", null);
        System.out.println("jplag.text.Parser: ERROR: Out of serials!");
	}

	public static void main(String args[]) {
		if (args.length != 1) {
			System.out.println("Only one parameter allowed.");
			System.exit(-1);
		}
		Parser parser = new Parser();
		jplag.Structure struct = parser.parse(new File("."), args);
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
						System.out.print(TextToken.type2string(tok.type) + " (" + tok.getLine() + "," + tok.getColumn() + ","
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

}
