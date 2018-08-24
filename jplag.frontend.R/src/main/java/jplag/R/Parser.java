package jplag.R;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;

import jplag.Structure;
import jplag.R.grammar.RLexer;
import jplag.R.grammar.RParser;
import jplag.R.grammar.RParser.ProgContext;


import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

/*
Esta clase se encarga de pasar el analizador lexico y sintactico creado en ANTLR4 y mandar los tokens (que decidimos en JplagRListener) al programa principal
para que aplique el algoritmo.
*/


public class Parser extends jplag.Parser implements RTokenConstants {
	private Structure struct;
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
                        if (!first) {
                            System.out.println();
                        }
                        RToken tok = (RToken) struct.tokens[token];
                        System.out.print(RToken.type2string(tok.type) + " ("
                                + tok.getLine() + ","
                                + tok.getColumn() + ","
                                + tok.getLength() + ")\t");
                        first = false;
                        token++;
                    }
                    if (first) {
                        System.out.print("                \t");
                    }
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
            if (!parseFile(dir, files[i])) {
                errors++;
            }
            System.gc();//Emeric
            struct.addToken(new RToken(FILE_END, files[i], -1, -1, -1));
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
            RLexer lexer = new RLexer(input);

            // create a buffer of tokens pulled from the lexer
            CommonTokenStream tokens = new CommonTokenStream(lexer);

            // create a parser that feeds off the tokens buffer
            RParser parser = new RParser(tokens);
            ProgContext in = parser.prog();

            ParseTreeWalker ptw = new ParseTreeWalker();
            for (int i = 0; i < in.getChildCount(); i++) {
                ParseTree pt = in.getChild(i);
                ptw.walk(new JplagRListener(this), pt);
            }

        } catch (IOException e) {
            getProgram().addError("Parsing Error in '" + file + "':\n" + e.getMessage() + "\n");
            return false;
        }

        return true;
    }

    // Metodo para añadir el token a JPLAG
    public void add(int type, org.antlr.v4.runtime.Token tok) {
        struct.addToken(new RToken(type, (currentFile == null ? "null" : currentFile), tok.getLine(), tok.getCharPositionInLine() + 1,
                tok.getText().length()));
    }
    // Metodo para añadir el token en caso de que sea uno con terminacion.
    public void addEnd(int type, org.antlr.v4.runtime.Token tok) {
        struct.addToken(new RToken(type, (currentFile == null ? "null" : currentFile), tok.getLine(), struct.tokens[struct.size()-1].getColumn() + 1,0));
    }
}
