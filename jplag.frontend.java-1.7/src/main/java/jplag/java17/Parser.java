package jplag.java17;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;

import jplag.Structure;
import jplag.java17.grammar.Java7Lexer;
import jplag.java17.grammar.Java7Parser;
import jplag.java17.grammar.Java7Parser.CompilationUnitContext;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

public class Parser extends jplag.Parser implements JavaTokenConstants {
	private Structure struct = new Structure();
	private String currentFile;

	public static void main(String args[]) {
		if (args.length < 1) {
			System.out.println("Only one or more files as parameter allowed.");
			System.exit(-1);
		}
		Parser parser = new Parser();
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
						JavaToken tok=(JavaToken) struct.tokens[token];
						System.out.print(JavaToken.type2string(tok.type) + " ("
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
			System.out.println(e);
		}
	}
	
	public jplag.Structure parse(File dir, String files[]) {
		struct = new Structure();
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

		ANTLRInputStream input;
		try {
			fis = new BufferedInputStream(new FileInputStream(new File(dir, file)));
			currentFile = file;
			input = new ANTLRInputStream(fis);

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
