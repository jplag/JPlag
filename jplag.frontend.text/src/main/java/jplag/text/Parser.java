package jplag.text;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashSet;

import jplag.InputState;
import jplag.ParserToken;
import jplag.TokenList;

/**
 * @Changed by Emeric Kwemou 29.01.2005
 *  
 */
public class Parser extends jplag.Parser implements jplag.TokenConstants {

	protected TokenStructure tokenStructure = new TokenStructure();

	private TokenList struct;

	private String currentFile;

	private HashSet<String> filter = null;

	public void initializeFilter(String fileName) throws FileNotFoundException {
		File file = new File(fileName);

		filter = new HashSet<String>();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line;

			while ((line = reader.readLine()) != null) {
				filter.add(line.trim().toLowerCase());
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

	public jplag.TokenList parse(File dir, String files[]) {
		struct = new TokenList();
		errors = 0;
		for (int i = 0; i < files.length; i++) {
			getProgram().print("", "Parsing file " + files[i] + "\n");
			if (!parseFile(dir, files[i]))
				errors++;
			struct.addToken(new TextToken(FILE_END, files[i], this));
		}

		TokenList tmp = struct;
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
}
