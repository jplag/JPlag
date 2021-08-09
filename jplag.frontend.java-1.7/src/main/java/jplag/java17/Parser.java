package jplag.java17;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import jplag.TokenList;
import jplag.java17.grammar.Java7Lexer;
import jplag.java17.grammar.Java7Parser;
import jplag.java17.grammar.Java7Parser.CompilationUnitContext;

public class Parser extends jplag.Parser implements JavaTokenConstants {
	private TokenList struct = new TokenList();
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

	public boolean parseFile(File dir, String file) {
		BufferedInputStream fis;

		CharStream input;
		try {
			fis = new BufferedInputStream(new FileInputStream(new File(dir, file)));
			currentFile = file;
			input = CharStreams.fromStream(fis);			        

			// create a lexer that feeds off of input CharStream
			Java7Lexer lexer = new Java7Lexer(input);

			// create a buffer of tokens pulled from the lexer
			CommonTokenStream tokens = new CommonTokenStream(lexer);

			// create a parser that feeds off the tokens buffer
			Java7Parser parser = new Java7Parser(tokens);
			CompilationUnitContext cuc = parser.compilationUnit();

			ParseTreeWalker ptw = new ParseTreeWalker();
			for (int i = 0; i < cuc.getChildCount(); i++) {
				ParseTree pt = cuc.getChild(i);
				ptw.walk(new JplagJava7Listener(this), pt);
			}

		} catch (IOException e) {
			getProgram().addError("Parsing Error in '" + file + "':\n" + e.getMessage() + "\n");
			return false;
		}

		return true;
	}

	//	public void addTerminal(int type, int line, int start, String text) {
	//		struct.addToken(new JavaToken(type, (currentFile == null ? "null" : currentFile), line, start, text.length()));
	//	}
	
	public void add(int type, org.antlr.v4.runtime.Token tok) {
		struct.addToken(new JavaToken(type, (currentFile == null ? "null" : currentFile), tok.getLine(), tok.getCharPositionInLine() + 1, 
				tok.getText().length()));
	}
}
