package jplag.python3;

import java.io.*;

import jplag.StreamParser;
import jplag.Structure;
import jplag.Token;
import jplag.TokenAdder;
import jplag.python3.grammar.Python3Lexer;
import jplag.python3.grammar.Python3Parser;
import jplag.python3.grammar.Python3Parser.File_inputContext;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.jetbrains.annotations.NotNull;

public class Parser extends StreamParser implements Python3TokenConstants {
    @Override
    @NotNull
    public Token getEndOfFileToken(String file) {
        return new Python3Token(FILE_END, file, -1, -1, -1);
    }

    @Override
    public boolean parseStream(@NotNull InputStream stream, @NotNull TokenAdder adder) throws IOException {
        ANTLRInputStream input = new ANTLRInputStream(stream);

        // create a lexer that feeds off of input CharStream
        Python3Lexer lexer = new Python3Lexer(input);

        // create a buffer of tokens pulled from the lexer
        CommonTokenStream tokens = new CommonTokenStream(lexer);

        // create a parser that feeds off the tokens buffer
        Python3Parser parser = new Python3Parser(tokens);
        File_inputContext in = parser.file_input();

        ParseTreeWalker ptw = new ParseTreeWalker();
        for (int i = 0; i < in.getChildCount(); i++) {
            ParseTree pt = in.getChild(i);
            ptw.walk(new JplagPython3Listener(new PythonTokenCreator(adder)), pt);
        }
        return true;
    }

    public static void main(String args[]) {
        String[] ffiles = {};
        Parser parser = new Parser();
        parser.setProgram(new jplag.StrippedProgram());
        jplag.Structure struct = parser.parse(null, ffiles);
        try {
            BufferedReader reader = new BufferedReader(new FileReader(new File(ffiles[0])));
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
                        Python3Token tok = (Python3Token)struct.tokens[token];
                        System.out.print(Python3Token.type2string(tok.type) + " ("
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
}
