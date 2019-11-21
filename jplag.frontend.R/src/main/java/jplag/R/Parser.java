package jplag.R;

import java.io.*;

import jplag.Structure;
import jplag.R.grammar.RLexer;
import jplag.R.grammar.RParser;
import jplag.R.grammar.RParser.ProgContext;


import jplag.Token;
import jplag.TokenAdder;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.jetbrains.annotations.NotNull;

/*
Esta clase se encarga de pasar el analizador lexico y sintactico creado en ANTLR4 y mandar los tokens (que decidimos en JplagRListener) al programa principal
para que aplique el algoritmo.
*/


public class Parser extends jplag.StreamParser implements RTokenConstants {
    @NotNull
    @Override
    public Token getEndOfFileToken(String file) {
        return new RToken(FILE_END, file, -1, -1, -1);
    }

    public static void main(String args[]) {
        args = new String[]{"/home/thomas/zzz.R"};
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


    @Override
    public boolean parseStream(@NotNull InputStream stream, @NotNull TokenAdder adder) throws IOException {
        ANTLRInputStream input = new ANTLRInputStream(stream);

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
            ptw.walk(new JplagRListener(new RTokenCreator(adder)), pt);
        }
        return true;
    }
}
